/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRightToUse_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("rtuSearchService")
@Transactional(readOnly = true)
public class RightToUseSearchServiceImpl extends AbstractSearchServiceImpl implements RightToUseSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createRtuPredicate(Root<MLPRightToUse> from, String solutionId, Boolean site, boolean isOr) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();
		if (solutionId != null && !solutionId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPRightToUse_.solutionId)), solutionId.toLowerCase()));
		if (site != null) {
			Predicate activePredicate = site ? cb.isTrue(from.<Boolean>get(MLPRightToUse_.site))
					: cb.isFalse(from.<Boolean>get(MLPRightToUse_.site));
			predicates.add(activePredicate);
		}
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
	public Page<MLPRightToUse> findRtus(String solutionId, Boolean site, boolean isOr, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		countQueryDef.distinct(true);
		Root<MLPRightToUse> countFrom = countQueryDef.from(MLPRightToUse.class);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createRtuPredicate(countFrom, solutionId, site, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findRtus: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery<MLPRightToUse> rootQueryDef = cb.createQuery(MLPRightToUse.class);
		// Coalesce any duplicates due to joins
		rootQueryDef.distinct(true);
		Root<MLPRightToUse> fromRoot = rootQueryDef.from(MLPRightToUse.class);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createRtuPredicate(countFrom, solutionId, site, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPRightToUse> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPRightToUse> queryResult = itemQuery.getResultList();
		logger.debug("findRtus: result size {}", queryResult.size());

		return new PageImpl<>(queryResult, pageable, count);
	}
}
