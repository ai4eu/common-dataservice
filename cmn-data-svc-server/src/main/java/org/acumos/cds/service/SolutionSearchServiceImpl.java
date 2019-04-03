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
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.acumos.cds.domain.MLPCatalog_;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRevCatDescription_;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.domain.MLPSolutionFOM_;
import org.acumos.cds.domain.MLPSolutionRevisionFOM;
import org.acumos.cds.domain.MLPSolutionRevisionFOM_;
import org.acumos.cds.domain.MLPSolution_;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPTag_;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUser_;
import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Defines methods to search solutions on user-specified fields and yield
 * paginated results. Most methods treat every field as optional, which is the
 * key difference from the methods in the solution repository class, where the
 * parameters are fixed and required.
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
 * Using the JPA (instead of the deprecated Hibernate Criteria API) makes the
 * code incredibly verbose.
 */
@Service("solutionSearchService")
@Transactional(readOnly = true)
public class SolutionSearchServiceImpl extends AbstractSearchServiceImpl implements SolutionSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String revAlias = "revs";
	private final String userAlias = "usr";
	private final String descsAlias = "descs";
	private final String solutionId = "solutionId";
	// Aliases used in subquery for required tags
	private final String solAlias = "sol";
	private final String subqAlias = "subsol";
	private final String tagsFieldAlias = "t";
	private final String tagValueField = tagsFieldAlias + ".tag";

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Gets the session factory from the entity manager factory. Factored out for
	 * convenience of subclasses.
	 * 
	 * @return SessionFactory
	 */
	private SessionFactory getSessionFactory() {
		return entityManagerFactory.unwrap(SessionFactory.class);
	}

	/**
	 * Builds a disjunction ("OR") criterion to check if field value occurs in the
	 * list, with special handling ("isNull") for null.
	 * 
	 * @param fieldName
	 *                      POJO field name
	 * @param values
	 *                      Set of values; null is permitted
	 * @return Criterion
	 */
	private Criterion buildEqualsListCriterion(String fieldName, Object[] values) {
		Junction junction = Restrictions.disjunction();
		for (Object v : values) {
			if (v == null)
				junction.add(Restrictions.isNull(fieldName));
			else
				junction.add(Restrictions.eq(fieldName, v));
		}
		return junction;
	}

	/**
	 * Builds a criterion to check approximate match of values in the list; null is
	 * not permitted.
	 * 
	 * @param fieldName
	 *                      POJO field name
	 * @param values
	 *                      String values; null is forbidden
	 * @param isOr
	 *                      If true, treat the query as a disjunction; else as a
	 *                      conjunction.
	 * @return Criterion
	 */
	private Criterion buildLikeListCriterion(String fieldName, String[] values, boolean isOr) {
		Junction junction = isOr ? Restrictions.disjunction() : Restrictions.conjunction();
		for (String v : values) {
			if (v == null)
				throw new IllegalArgumentException("Null not permitted in value list");
			else
				junction.add(Restrictions.like(fieldName, '%' + v + '%'));
		}
		return junction;
	}

	/**
	 * Adds sort criteria to the criteria.
	 * 
	 * @param criteria
	 *                     Criteria
	 * @param pageable
	 *                     Pageable
	 */
	private void applySortCriteria(Criteria criteria, Pageable pageable) {
		Iterator<Sort.Order> orderIter = pageable.getSort().iterator();
		while (orderIter.hasNext()) {
			Sort.Order sortOrder = orderIter.next();
			Order order;
			if (sortOrder.isAscending())
				order = Order.asc(sortOrder.getProperty());
			else
				order = Order.desc(sortOrder.getProperty());
			criteria.addOrder(order);
		}
	}

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createFindSolutionsPredicate(Root<MLPSolution> from, String name, Boolean active, String userId,
			String sourceId, String modelTypeCode, String toolkitTypeCode, String origin, boolean isOr) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();
		if (name != null && !name.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPSolution_.NAME)), name.toLowerCase()));
		if (active != null) {
			Predicate activePredicate = active ? cb.isTrue(from.<Boolean>get(MLPSolution_.ACTIVE))
					: cb.isFalse(from.<Boolean>get(MLPSolution_.ACTIVE));
			predicates.add(activePredicate);
		}
		if (userId != null && !userId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPSolution_.USER_ID)), userId.toLowerCase()));
		if (sourceId != null && !sourceId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPSolution_.SOURCE_ID)), sourceId.toLowerCase()));
		if (modelTypeCode != null && !modelTypeCode.isEmpty())
			predicates.add(
					cb.equal(cb.lower(from.<String>get(MLPSolution_.MODEL_TYPE_CODE)), modelTypeCode.toLowerCase()));
		if (toolkitTypeCode != null && !toolkitTypeCode.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPSolution_.TOOLKIT_TYPE_CODE)),
					toolkitTypeCode.toLowerCase()));
		if (origin != null && !origin.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPSolution_.ORIGIN)), origin.toLowerCase()));
		if (predicates.isEmpty())
			throw new IllegalArgumentException("Missing query values, must have at least one non-null");
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		return isOr ? cb.or(predArray) : cb.and(predArray);
	}

	/*
	 * This criteria only checks properties of the solution entity, not of any
	 * associated entities, so inner joins and their cross products are avoidable.
	 * Therefore it's safe to use limit criteria in the database, which saves the
	 * effort of computing a big result and discarding all but the desired page.
	 * Unfortunately the solution entity has very few properties that are worth
	 * searching, so this is largely worthless.
	 * 
	 * Calls the create-predicate method twice, once to form the count query and
	 * once to form the fetch query. Might not be necessary but the JPA remains
	 * black magic.
	 */
	@Override
	public Page<MLPSolution> searchSolutions(String name, Boolean active, String userId, String sourceId,
			String modelTypeCode, String toolkitTypeCode, String origin, boolean isOr, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		Root<MLPSolution> countFrom = countQueryDef.from(MLPSolution.class);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createFindSolutionsPredicate(countFrom, name, active, userId, sourceId, modelTypeCode,
				toolkitTypeCode, origin, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findSolutions: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery<MLPSolution> rootQueryDef = cb.createQuery(MLPSolution.class);
		Root<MLPSolution> fromRoot = rootQueryDef.from(MLPSolution.class);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createFindSolutionsPredicate(countFrom, name, active, userId, sourceId, modelTypeCode,
				toolkitTypeCode, origin, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPSolution> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPSolution> queryResult = itemQuery.getResultList();
		// Deal with lazy initialization
		for (MLPSolution s : queryResult)
			Hibernate.initialize(s.getTags());
		logger.debug("findSolutions: result size {}", queryResult.size());
		return new PageImpl<>(queryResult, pageable, count);
	}

	/**
	 * Converts list of MLPSolutionFOM to page of MLPSolution
	 * 
	 * @param foms
	 *                     List of MLPSolutionFOM
	 * @param pageable
	 *                     Page request
	 * @return Page of MLPSolution
	 */
	private PageImpl<MLPSolution> buildSolutionPage(List<MLPSolutionFOM> foms, Pageable pageable) {
		// Get a page of FOM solutions and convert each to plain solution
		List<MLPSolution> items = new ArrayList<>();
		long lastItemInPage = pageable.getOffset() + pageable.getPageSize();
		long limit = lastItemInPage < foms.size() ? lastItemInPage : foms.size();
		for (long i = pageable.getOffset(); i < limit; ++i) {
			Object o = foms.get((int) i);
			if (o instanceof MLPSolutionFOM) {
				MLPSolution s = ((MLPSolutionFOM) o).toMLPSolution();
				// Deal with lazy initialization
				Hibernate.initialize(s.getTags());
				items.add(s);
			} else {
				throw new AssertionFailure("Unexpected type: " + o.getClass().getName());
			}
		}
		return new PageImpl<>(items, pageable, foms.size());
	}

	/*
	 * Early attempt at providing a method for users to find solutions in the
	 * marketplace.
	 *
	 * This implementation is awkward due to the requirement to perform LIKE queries
	 * on certain fields.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descKeywords, boolean active,
			String[] userIds, String[] modelTypeCodes, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, Pageable pageable) {

		// build the query using FOM class to access child attributes
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MLPSolutionFOM> rootQueryDef = cb.createQuery(MLPSolutionFOM.class);
		Root<MLPSolutionFOM> solutionFom = rootQueryDef.from(MLPSolutionFOM.class);
		rootQueryDef.select(solutionFom);
		rootQueryDef.distinct(true);
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, solutionFom, pageable.getSort()));

		List<Predicate> predicates = new ArrayList<>();
		// Active is a required parameter
		predicates.add(active ? cb.isTrue(solutionFom.<Boolean>get(MLPSolution_.active))
				: cb.isFalse(solutionFom.<Boolean>get(MLPSolution_.active)));
		// Remaining parameters can be null or empty
		if (nameKeywords != null && nameKeywords.length > 0) {
			Predicate nameDisjunction = cb.disjunction();
			for (String n : nameKeywords)
				nameDisjunction.getExpressions()
						.add(cb.like(solutionFom.<String>get(MLPSolution_.name), '%' + n + '%'));
			predicates.add(nameDisjunction);
		}
		if (modelTypeCodes != null && modelTypeCodes.length > 0) {
			Predicate mtcInPredicate = solutionFom.<String>get(MLPSolution_.modelTypeCode)
					.in((Object[]) modelTypeCodes);
			predicates.add(mtcInPredicate);
		}

		if ((authorKeywords != null && authorKeywords.length > 0) || (descKeywords != null && descKeywords.length > 0)
				|| (publisherKeywords != null && publisherKeywords.length > 0)) {

			// revisions are optional, even tho a solution without them is useless
			Join<MLPSolutionFOM, MLPSolutionRevisionFOM> revisionFom = solutionFom.join(MLPSolutionFOM_.revisions);

			if (authorKeywords != null && authorKeywords.length > 0) {
				Predicate or = cb.disjunction();
				for (String s : authorKeywords)
					or.getExpressions()
							.add(cb.like(revisionFom.<String>get(MLPSolutionRevisionFOM_.authors), '%' + s + '%'));
				predicates.add(or);
			}
			if (publisherKeywords != null && publisherKeywords.length > 0) {
				Predicate or = cb.disjunction();
				for (String s : publisherKeywords)
					or.getExpressions()
							.add(cb.like(revisionFom.<String>get(MLPSolutionRevisionFOM_.publisher), '%' + s + '%'));
				predicates.add(or);
			}
			if (descKeywords != null && descKeywords.length > 0) {
				// Descriptions are optional so use outer join
				Join<MLPSolutionRevisionFOM, MLPRevCatDescription> revDesc = revisionFom
						.join(MLPSolutionRevisionFOM_.descriptions, JoinType.LEFT);
				Predicate or = cb.disjunction();
				for (String s : descKeywords)
					or.getExpressions()
							.add(cb.like(revDesc.<String>get(MLPRevCatDescription_.description), '%' + s + '%'));
				predicates.add(or);
			}
		}

		// Silly to join on the user table just to check the ID, but given the
		// annotation on MLPSolutionFOM I don't know another way.
		if (userIds != null && userIds.length > 0) {
			// User is mandatory so use inner join
			Join<MLPSolutionFOM, MLPUser> user = solutionFom.join(MLPSolutionFOM_.user);
			Predicate p = user.<String>get(MLPUser_.userId).in((Object[]) userIds);
			predicates.add(p);
		}

		// This checks for ANY TAG, not all tags.
		if (tags != null && tags.length > 0) {
			// Tags are optional so use outer join
			Join<MLPSolutionFOM, MLPTag> tag = solutionFom.join(MLPSolutionFOM_.tags, JoinType.LEFT);
			Predicate p = tag.<String>get(MLPTag_.tag).in((Object[]) tags);
			predicates.add(p);
		}

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		rootQueryDef.where(cb.and(predArray));

		TypedQuery<MLPSolutionFOM> typedQuery = entityManager.createQuery(rootQueryDef);
		List<MLPSolutionFOM> foms = typedQuery.getResultList();
		if (foms.isEmpty() || foms.size() < pageable.getOffset())
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		logger.info("findPortalSolutions: result size {}", foms.size());
		return buildSolutionPage(foms, pageable);
	}

	@Override
	public Page<MLPSolution> findUserSolutions(boolean active, boolean published, String userId, String[] nameKeywords,
			String[] descKeywords, String[] modelTypeCodes, String[] tags, Pageable pageable) {

		// build the query using FOM class to access child attributes
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MLPSolutionFOM> rootQueryDef = cb.createQuery(MLPSolutionFOM.class);
		Root<MLPSolutionFOM> solutionFom = rootQueryDef.from(MLPSolutionFOM.class);
		rootQueryDef.select(solutionFom);
		rootQueryDef.distinct(true);
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, solutionFom, pageable.getSort()));

		List<Predicate> predicates = new ArrayList<>();
		// Active is a required parameter
		predicates.add(active ? cb.isTrue(solutionFom.<Boolean>get(MLPSolution_.active))
				: cb.isFalse(solutionFom.<Boolean>get(MLPSolution_.active)));

		// Published is a required parameter but catalogs are optional.
		// This does not use an explicit join.
		Predicate publishedPred = published ? cb.isNotEmpty(solutionFom.get(MLPSolutionFOM_.catalogs))
				: cb.isEmpty(solutionFom.get(MLPSolutionFOM_.catalogs));
		predicates.add(publishedPred);

		// Check for user as OWNER or user as SHARE. It's silly to
		// join on the user table just to check the ID, but given the
		// annotation on MLPSolutionFOM I don't know another way.
		// Every solution has a creating user, so use inner join
		Join<MLPSolutionFOM, MLPUser> userAsCreator = solutionFom.join(MLPSolutionFOM_.user);
		Predicate creatorPred = cb.equal(userAsCreator.<String>get(MLPUser_.userId), userId);
		// Share is optional, so use outer join
		Join<MLPSolutionFOM, MLPUser> userAsShare = solutionFom.join(MLPSolutionFOM_.accessUsers, JoinType.LEFT);
		Predicate sharePred = cb.equal(userAsShare.<String>get(MLPUser_.userId), userId);
		predicates.add(cb.or(creatorPred, sharePred));

		// Remaining parameters can be null or empty
		if (nameKeywords != null && nameKeywords.length > 0) {
			Predicate nameDisjunction = cb.disjunction();
			for (String n : nameKeywords)
				nameDisjunction.getExpressions()
						.add(cb.like(solutionFom.<String>get(MLPSolution_.name), '%' + n + '%'));
			predicates.add(nameDisjunction);
		}
		if (modelTypeCodes != null && modelTypeCodes.length > 0) {
			Predicate mtcInPredicate = solutionFom.<String>get(MLPSolution_.modelTypeCode)
					.in((Object[]) modelTypeCodes);
			predicates.add(mtcInPredicate);
		}
		if (descKeywords != null && descKeywords.length > 0) {
			// revisions are not really optional, a solution without them is useless
			Join<MLPSolutionFOM, MLPSolutionRevisionFOM> revisionFom = solutionFom.join(MLPSolutionFOM_.revisions);
			// but descriptions are optional so use left join
			Join<MLPSolutionRevisionFOM, MLPRevCatDescription> revDesc = revisionFom
					.join(MLPSolutionRevisionFOM_.descriptions, JoinType.LEFT);
			Predicate or = cb.disjunction();
			for (String s : descKeywords)
				or.getExpressions().add(cb.like(revDesc.<String>get(MLPRevCatDescription_.description), '%' + s + '%'));
			predicates.add(or);
		}

		// This checks for ANY TAG, not all tags.
		if (tags != null && tags.length > 0) {
			// Tags are optional so use left join
			Join<MLPSolutionFOM, MLPTag> tag = solutionFom.join(MLPSolutionFOM_.tags, JoinType.LEFT);
			Predicate p = tag.<String>get(MLPTag_.tag).in((Object[]) tags);
			predicates.add(p);
		}

		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		rootQueryDef.where(cb.and(predArray));

		TypedQuery<MLPSolutionFOM> typedQuery = entityManager.createQuery(rootQueryDef);
		List<MLPSolutionFOM> foms = typedQuery.getResultList();
		if (foms.isEmpty() || foms.size() < pageable.getOffset())
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		logger.info("findUserSolutions: result size {}", foms.size());
		return buildSolutionPage(foms, pageable);
	}

	/**
	 * Runs a Hibernate Criteria-type query on the SolutionFOM entity. Returns a
	 * page after converting the resulting FOM solution objects to plain solution
	 * objects.
	 * 
	 * @param criteria
	 *                     Criteria to evaluate
	 * @param pageable
	 *                     Page and sort criteria
	 * @return Page of MLPSolution
	 */
	@SuppressWarnings("rawtypes")
	private Page<MLPSolution> runSolutionFomQuery(Criteria criteria, Pageable pageable) {

		// Include user's sort request
		if (pageable.getSort() != null)
			applySortCriteria(criteria, pageable);
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
		long lastItemInPage = pageable.getOffset() + pageable.getPageSize();
		long limit = lastItemInPage < foms.size() ? lastItemInPage : foms.size();
		for (long i = pageable.getOffset(); i < limit; ++i) {
			Object o = foms.get((int) i);
			if (o instanceof MLPSolutionFOM) {
				MLPSolution s = ((MLPSolutionFOM) o).toMLPSolution();
				// Deal with lazy initialization
				Hibernate.initialize(s.getTags());
				items.add(s);
			} else {
				throw new AssertionFailure("Unexpected type: " + o.getClass().getName());
			}
		}

		return new PageImpl<>(items, pageable, foms.size());
	}

	/*
	 * Low-rent version of full-text search. Provides flexible treatment of tags.
	 * 
	 * TODO: Rewrite to use JPA methods.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCode, String[] allTags, String[] anyTags, String[] catalogIds, Pageable pageable) {

		try (Session session = getSessionFactory().openSession()) {
			// build the query using FOM to access child attributes
			@SuppressWarnings("deprecation")
			Criteria criteria = session.createCriteria(MLPSolutionFOM.class, solAlias);
			criteria.add(Restrictions.eq("active", active));
			// A solution should ALWAYS have revisions.
			criteria.createAlias(MLPSolutionFOM_.REVISIONS, revAlias);
			// Descriptions are optional, so must use outer join
			if (keywords != null && keywords.length > 0) {
				criteria.createAlias(revAlias + "." + MLPSolutionRevisionFOM_.DESCRIPTIONS, descsAlias,
						org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
				Disjunction keywordDisjunction = Restrictions.disjunction();
				keywordDisjunction.add(buildLikeListCriterion(MLPSolutionFOM_.NAME, keywords, false));
				keywordDisjunction
						.add(buildLikeListCriterion(revAlias + "." + MLPSolutionRevisionFOM_.AUTHORS, keywords, false));
				keywordDisjunction.add(
						buildLikeListCriterion(revAlias + "." + MLPSolutionRevisionFOM_.PUBLISHER, keywords, false));
				// Also match on IDs, but exact only
				keywordDisjunction.add(buildEqualsListCriterion(MLPSolutionFOM_.SOLUTION_ID, keywords));
				keywordDisjunction
						.add(buildEqualsListCriterion(revAlias + "." + MLPSolutionRevisionFOM_.REVISION_ID, keywords));
				criteria.add(keywordDisjunction);
			}
			if (modelTypeCode != null && modelTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion(MLPSolutionFOM_.MODEL_TYPE_CODE, modelTypeCode));
			if (userIds != null && userIds.length > 0) {
				criteria.createAlias("user", userAlias);
				criteria.add(Restrictions.in(userAlias + "." + MLPUser_.USER_ID, (Object[]) userIds));
			}
			if (allTags != null && allTags.length > 0) {
				// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
				DetachedCriteria allTagsQuery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
						.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
						.createAlias("tags", tagsFieldAlias) //
						.add(Restrictions.in(tagValueField, (Object[]) allTags)) //
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
						.add(Restrictions.in(tag2ValueField, (Object[]) anyTags))
						.setProjection(Projections.count(tag2ValueField));
				criteria.add(Subqueries.lt(0L, anyTagsQuery));
			}
			if (catalogIds != null && catalogIds.length > 0) {
				final String catAlias = "ctlg";
				// Use inner join here
				criteria.createAlias(MLPSolutionFOM_.CATALOGS, catAlias);
				criteria.add(Restrictions.in(catAlias + "." + MLPCatalog_.CATALOG_ID, (Object[]) catalogIds));
			}
			Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
			logger.debug("findPortalSolutionsByKwAndTags: result size={}", result.getNumberOfElements());
			return result;
		}
	}

}