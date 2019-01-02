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
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepResult_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("stepResultSearchService")
@Transactional(readOnly = true)
public class StepResultSearchServiceImpl extends AbstractSearchServiceImpl implements StepResultSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createStepResultPredicate(Root<MLPStepResult> from, String trackingId, String stepCode,
			String solutionId, String revisionId, String artifactId, String userId, String name, String statusCode,
			boolean isOr) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();
		if (trackingId != null && !trackingId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.trackingId)), trackingId.toLowerCase()));
		if (stepCode != null && !stepCode.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.stepCode)), stepCode.toLowerCase()));
		if (solutionId != null && !solutionId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.solutionId)), solutionId.toLowerCase()));
		if (revisionId != null && !revisionId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.revisionId)), revisionId.toLowerCase()));
		if (artifactId != null && !artifactId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.artifactId)), artifactId.toLowerCase()));
		if (userId != null && !userId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.userId)), userId.toLowerCase()));
		if (name != null && !name.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.name)), name.toLowerCase()));
		if (statusCode != null && !statusCode.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPStepResult_.statusCode)), statusCode.toLowerCase()));
		if (predicates.isEmpty())
			throw new IllegalArgumentException("Missing query values, must have at least one non-null");
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		return isOr ? cb.or(predArray) : cb.and(predArray);
	}

	/*
	 * Use JPA in Spring-Boot version 2.1
	 */
	@Override
	public Page<MLPStepResult> findStepResults(String trackingId, String stepCode, String solutionId, String revisionId,
			String artifactId, String userId, String name, String statusCode, boolean isOr, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		countQueryDef.distinct(true);
		Root<MLPStepResult> countFrom = countQueryDef.from(MLPStepResult.class);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createStepResultPredicate(countFrom, trackingId, stepCode, solutionId, revisionId,
				artifactId, userId, name, statusCode, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findStepResults: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery<MLPStepResult> rootQueryDef = cb.createQuery(MLPStepResult.class);
		// Coalesce any duplicates due to joins
		rootQueryDef.distinct(true);
		Root<MLPStepResult> fromRoot = rootQueryDef.from(MLPStepResult.class);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createStepResultPredicate(countFrom, trackingId, stepCode, solutionId, revisionId,
				artifactId, userId, name, statusCode, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPStepResult> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPStepResult> queryResult = itemQuery.getResultList();
		logger.debug("findStepResults: result size {}", queryResult.size());

		return new PageImpl<>(queryResult, pageable, count);
	}

}
