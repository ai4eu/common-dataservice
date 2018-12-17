/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Defines hibernate-assisted methods to search solutions on user-specified
 * fields and yield paginated results. Any field value can be omitted, which is
 * the key difference from the methods in the solution repository class.
 * 
 * <P>
 * These two aspects must be observed to get pagination working as expected:
 * <OL>
 * <LI>For all to-many mappings, force use of separate select instead of left
 * outer join. This is far less efficient due to repeated trips to the database,
 * and becomes impossible if you must check properties on mapped (i.e., not the
 * root) entities.</LI>
 * <LI>Specify an unambiguous ordering. This at least is cheap, just add
 * order-by the ID field.</LI>
 * </OL>
 * I'm not the only one who has fought Hibernate to get paginated search
 * results:
 * 
 * <PRE>
 * https://stackoverflow.com/questions/300491/how-to-get-distinct-results-in-hibernate-with-joins-and-row-based-limiting-pagi
 * https://stackoverflow.com/questions/9418268/hibernate-distinct-results-with-pagination
 * https://stackoverflow.com/questions/11038234/pagination-with-hibernate-criteria-and-distinct-root-entity
 * https://stackoverflow.com/questions/42910271/duplicate-records-with-hibernate-joins-and-pagination
 * </PRE>
 * 
 * Many of the queries here check properties of the solution AND associated
 * entities especially revisions. The queries require an inner join and yield a
 * large cross product that Hibernate will coalesce. Because of the joins it's
 * unsafe to apply limit (pagination) parameters at the database. Therefore the
 * approach taken here is to fetch the full result from the database then
 * reduces the result size in the method, which is inefficient.
 *
 */
@Service("solutionSearchService")
@Transactional
public class SolutionSearchServiceImpl extends AbstractSearchServiceImpl implements SolutionSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private SessionFactory sessionFactory;

	private final String revAlias = "revs";
	private final String artAlias = "arts";
	private final String ownerAlias = "ownr";
	private final String accAlias = "acc";
	private final String descsAlias = "descs";
	private final String docsAlias = "docs";
	private final String solutionId = "solutionId";
	// Aliases used in subquery for required tags
	private final String solAlias = "sol";
	private final String subqAlias = "subsol";
	private final String tagsFieldAlias = "t";
	private final String tagValueField = tagsFieldAlias + ".tag";

	/*
	 * This criteria only checks properties of the solution entity, not of any
	 * associated entities, so inner joins and their cross products are avoidable.
	 * Therefore it's safe to use limit criteria in the database, which saves the
	 * effort of computing a big result and discarding all but the desired page.
	 * Unfortunately the solution entity has very few properties that are worth
	 * searching, so this is largely worthless.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Page<MLPSolution> findSolutions(Map<String, ? extends Object> queryParameters, boolean isOr,
			Pageable pageable) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolution.class);
		super.buildCriteria(criteria, queryParameters, isOr);

		// Adjust fetch mode to block Hibernate from using left outer join;
		// instead it runs a select to get tags for each solution in the result.
		criteria.setFetchMode("tags", FetchMode.SELECT);

		// Count the total rows
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Reset the count criteria
		criteria.setProjection(null);
		// This should not do any harm; had problems elsewhere without
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		// Add pagination and sorting to limit size
		super.applyPageableCriteria(criteria, pageable);
		// Ensure a total order using the ID field.
		criteria.addOrder(Order.asc("solutionId"));

		// Get a page of results and send it back with the total available
		List<MLPSolution> items = criteria.list();
		logger.info("findSolutions: result size={}", items.size());
		return new PageImpl<>(items, pageable, count);
	}

	/**
	 * Runs a query on the SolutionFOM entity, returns a page after converting the
	 * resulting FOM solution objects to plain solution objects.
	 * 
	 * @param criteria
	 *                     Criteria to evaluate
	 * @param pageable
	 *                     Page and sort criteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Page<MLPSolution> runSolutionFomQuery(Criteria criteria, Pageable pageable) {

		// Include user's sort request
		if (pageable.getSort() != null)
			super.applySortCriteria(criteria, pageable);
		// Add order on a unique field. Without this the pagination
		// can yield odd results; e.g., request 10 items but only get 8.
		criteria.addOrder(Order.asc(solutionId));
		// Hibernate should coalesce the results, yielding only solutions
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// Getting the complete result could be brutally expensive.
		List foms = criteria.list();
		if (foms.isEmpty() || foms.size() < pageable.getOffset())
			return new PageImpl<>(new ArrayList<>(), pageable, 0);

		// Get a page of FOM solutions and convert each to plain solution
		List<MLPSolution> items = new ArrayList<>();
		int lastItemInPage = pageable.getOffset() + pageable.getPageSize();
		int limit = lastItemInPage < foms.size() ? lastItemInPage : foms.size();
		for (int i = pageable.getOffset(); i < limit; ++i) {
			Object o = foms.get(i);
			if (o instanceof MLPSolutionFOM)
				items.add(((MLPSolutionFOM) o).toMLPSolution());
			else
				throw new AssertionFailure("Unexpected type: " + o.getClass().getName());
		}
		return new PageImpl<>(items, pageable, foms.size());
	}

	/**
	 * Finds solutions in the marketplace.
	 * 
	 * Also see comment above about paginated queries.
	 *
	 * This implementation is made yet more awkward due to the requirement to
	 * perform LIKE queries on certain fields.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descKeywords, boolean active,
			String[] userIds, String[] modelTypeCode, String[] accessTypeCode, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, Pageable pageable) {

		// build the query using FOM to access child attributes
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class, solAlias);
		// Attributes on the solution
		criteria.add(Restrictions.eq("active", active));
		if (nameKeywords != null && nameKeywords.length > 0)
			criteria.add(buildLikeListCriterion("name", nameKeywords, true));
		if (modelTypeCode != null && modelTypeCode.length > 0)
			criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
		if ((accessTypeCode != null && accessTypeCode.length > 0) //
				|| (descKeywords != null && descKeywords.length > 0)
				|| (authorKeywords != null && authorKeywords.length > 0)
				|| (publisherKeywords != null && publisherKeywords.length > 0)) {
			// revisions are optional, but a solution without them is useless
			criteria.createAlias("revisions", revAlias);
			if (accessTypeCode != null && accessTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
			if (authorKeywords != null && authorKeywords.length > 0)
				criteria.add(buildLikeListCriterion(revAlias + ".authors", authorKeywords, true));
			if (publisherKeywords != null && publisherKeywords.length > 0)
				criteria.add(buildLikeListCriterion(revAlias + ".publisher", publisherKeywords, true));
			if (descKeywords != null && descKeywords.length > 0) {
				criteria.createAlias(revAlias + ".descriptions", descsAlias,
						org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			}
		}
		if (userIds != null && userIds.length > 0) {
			criteria.createAlias("owner", ownerAlias);
			criteria.add(Restrictions.in(ownerAlias + ".userId", userIds));
		}
		if (tags != null && tags.length > 0) {
			// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
			DetachedCriteria subquery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
					.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
					.createAlias("tags", tagsFieldAlias) //
					.add(Restrictions.in(tagValueField, tags)) //
					.setProjection(Projections.count(tagValueField));
			criteria.add(Subqueries.eq((long) tags.length, subquery));
		}
		Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
		logger.info("findPortalSolutions: result size={}", result.getNumberOfElements());
		return result;
	}

	/**
	 * Finds models for a single user, in support of Portal's My Models page.
	 * 
	 * Also see comment above about paginated queries.
	 *
	 * Baffling problem occurred here:
	 * 
	 * Caused by: java.lang.IllegalArgumentException: Can not set java.lang.String
	 * field org.acumos.cds.domain.MLPUser.userId to java.lang.String
	 * 
	 * Which was due to a missing alias for the owner/userId field; i.e., my error,
	 * not some obscure Hibernate issue.
	 */
	@Override
	public Page<MLPSolution> findUserSolutions(String[] nameKeywords, String[] descKeywords, boolean active,
			String userId, String[] modelTypeCode, String[] accessTypeCode, String[] tags, Pageable pageable) {

		// build the query using FOM to access child attributes
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class, solAlias);
		// Find user's own models AND others via access map which requires outer join
		criteria.createAlias("owner", ownerAlias);
		Criterion owner = Restrictions.eq(ownerAlias + ".userId", userId);
		criteria.createAlias("accessUsers", accAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		Criterion access = Restrictions.eq(accAlias + ".userId", userId);
		criteria.add(Restrictions.or(owner, access));

		// Attributes on the solution
		criteria.add(Restrictions.eq("active", active));
		if (nameKeywords != null && nameKeywords.length > 0)
			criteria.add(buildLikeListCriterion("name", nameKeywords, false));
		if (modelTypeCode != null && modelTypeCode.length > 0)
			criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
		if ((accessTypeCode != null && accessTypeCode.length > 0) //
				|| (descKeywords != null && descKeywords.length > 0)) {
			// revisions are optional, but a solution without them is useless
			criteria.createAlias("revisions", revAlias);
			if (accessTypeCode != null && accessTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
			if (descKeywords != null && descKeywords.length > 0) {
				criteria.createAlias(revAlias + ".descriptions", descsAlias,
						org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			}
		}
		if (tags != null && tags.length > 0) {
			// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
			DetachedCriteria subquery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
					.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
					.createAlias("tags", tagsFieldAlias) //
					.add(Restrictions.in(tagValueField, tags)) //
					.setProjection(Projections.count(tagValueField));
			criteria.add(Subqueries.eq((long) tags.length, subquery));
		}
		Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
		logger.info("findUserSolutions: result size={}", result.getNumberOfElements());
		return result;
	}

	/**
	 * This supports federation, which needs to search for solutions modified after
	 * a point in time.
	 * 
	 * Also see comment above about paginated queries.
	 */
	@Override
	public Page<MLPSolution> findSolutionsByModifiedDate(boolean active, String[] accessTypeCode, Date date,
			Pageable pageable) {

		// build the query using FOM to access child attributes
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class, solAlias);
		// A solution should ALWAYS have revisions.
		criteria.createAlias("revisions", revAlias);
		// A revision should ALWAYS have artifacts
		criteria.createAlias(revAlias + ".artifacts", artAlias);
		// A revision MAY have descriptions
		criteria.createAlias(revAlias + ".descriptions", descsAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		// A revision MAY have documents
		criteria.createAlias(revAlias + ".documents", docsAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		// Attributes on the solution
		criteria.add(Restrictions.eq("active", active));
		if (accessTypeCode != null && accessTypeCode.length > 0)
			criteria.add(Restrictions.in(revAlias + ".accessTypeCode", accessTypeCode));
		// Construct a disjunction to find any updated item.
		// Unfortunately this requires hard-coded field names
		Criterion solModified = Restrictions.ge("modified", date);
		Criterion revModified = Restrictions.ge(revAlias + ".modified", date);
		Criterion descModified = Restrictions.ge(descsAlias + ".modified", date);
		Criterion docModified = Restrictions.ge(docsAlias + ".modified", date);
		Criterion artModified = Restrictions.ge(artAlias + ".modified", date);
		Disjunction itemModifiedAfter = Restrictions.disjunction();
		itemModifiedAfter.add(solModified);
		itemModifiedAfter.add(revModified);
		itemModifiedAfter.add(descModified);
		itemModifiedAfter.add(docModified);
		itemModifiedAfter.add(artModified);
		criteria.add(itemModifiedAfter);

		Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
		logger.info("findSolutionsByModifiedDate: result size={}", result.getNumberOfElements());
		return result;
	}

	/*
	 * Low-rent version of full-text search.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutionsByKw(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCode, String[] accessTypeCode, String[] tags, Pageable pageable) {

		// build the query using FOM to access child attributes
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class, solAlias);
		criteria.add(Restrictions.eq("active", active));
		// A solution should ALWAYS have revisions.
		criteria.createAlias("revisions", revAlias);
		// Descriptions are optional, so must use outer join
		if (keywords != null && keywords.length > 0) {
			criteria.createAlias(revAlias + ".descriptions", descsAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			Disjunction keywordDisjunction = Restrictions.disjunction();
			keywordDisjunction.add(buildLikeListCriterion("name", keywords, false));
			keywordDisjunction.add(buildLikeListCriterion(revAlias + ".authors", keywords, false));
			keywordDisjunction.add(buildLikeListCriterion(revAlias + ".publisher", keywords, false));
			// Also match on IDs, but exact only
			keywordDisjunction.add(buildEqualsListCriterion("solutionId", keywords));
			keywordDisjunction.add(buildEqualsListCriterion(revAlias + ".revisionId", keywords));
			criteria.add(keywordDisjunction);
		}
		if (modelTypeCode != null && modelTypeCode.length > 0)
			criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
		if (accessTypeCode != null && accessTypeCode.length > 0)
			criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
		if (userIds != null && userIds.length > 0) {
			criteria.createAlias("owner", ownerAlias);
			criteria.add(Restrictions.in(ownerAlias + ".userId", userIds));
		}
		if (tags != null && tags.length > 0) {
			// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
			DetachedCriteria subquery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
					.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
					.createAlias("tags", tagsFieldAlias) //
					.add(Restrictions.in(tagValueField, tags)) //
					.setProjection(Projections.count(tagValueField));
			criteria.add(Subqueries.eq((long) tags.length, subquery));
		}
		Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
		logger.info("findPortalSolutionsByKw: result size={}", result.getNumberOfElements());
		return result;
	}

	/*
	 * Provides flexible treatment of tags.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCode, String[] accessTypeCode, String[] allTags, String[] anyTags, Pageable pageable) {
		// build the query using FOM to access child attributes
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class, solAlias);
		criteria.add(Restrictions.eq("active", active));
		// A solution should ALWAYS have revisions.
		criteria.createAlias("revisions", revAlias);
		// Descriptions are optional, so must use outer join
		if (keywords != null && keywords.length > 0) {
			criteria.createAlias(revAlias + ".descriptions", descsAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			Disjunction keywordDisjunction = Restrictions.disjunction();
			keywordDisjunction.add(buildLikeListCriterion("name", keywords, false));
			keywordDisjunction.add(buildLikeListCriterion(revAlias + ".authors", keywords, false));
			keywordDisjunction.add(buildLikeListCriterion(revAlias + ".publisher", keywords, false));
			// Also match on IDs, but exact only
			keywordDisjunction.add(buildEqualsListCriterion("solutionId", keywords));
			keywordDisjunction.add(buildEqualsListCriterion(revAlias + ".revisionId", keywords));
			criteria.add(keywordDisjunction);
		}
		if (modelTypeCode != null && modelTypeCode.length > 0)
			criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
		if (accessTypeCode != null && accessTypeCode.length > 0)
			criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
		if (userIds != null && userIds.length > 0) {
			criteria.createAlias("owner", ownerAlias);
			criteria.add(Restrictions.in(ownerAlias + ".userId", userIds));
		}
		if (allTags != null && allTags.length > 0) {
			// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
			DetachedCriteria allTagsQuery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
					.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
					.createAlias("tags", tagsFieldAlias) //
					.add(Restrictions.in(tagValueField, allTags)) //
					.setProjection(Projections.count(tagValueField));
			criteria.add(Subqueries.eq((long) allTags.length, allTagsQuery));
		}
		if (anyTags != null && anyTags.length > 0) {
			final String subq2Alias = "subsol2";
			final String tag2Alias = "anytag";
			final String tag2ValueField = tag2Alias + ".tag";
			DetachedCriteria anyTagsQuery = DetachedCriteria.forClass(MLPSolutionFOM.class, subq2Alias)
					.add(Restrictions.eqProperty(subq2Alias + ".id", solAlias + ".id")) //
					.createAlias("tags", tag2Alias) //
					.add(Restrictions.in(tag2ValueField, anyTags)).setProjection(Projections.count(tag2ValueField));
			criteria.add(Subqueries.lt(0L, anyTagsQuery));
		}
		Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
		logger.info("findPortalSolutionsByKwAndTags: result size={}", result.getNumberOfElements());
		return result;
	}

}
