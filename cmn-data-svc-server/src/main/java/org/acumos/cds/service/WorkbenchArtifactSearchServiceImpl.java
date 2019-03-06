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

import org.acumos.cds.domain.MLPAbstractWorkbenchArtifact;
import org.acumos.cds.domain.MLPAbstractWorkbenchArtifact_;
import org.acumos.cds.domain.MLPNotebook_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Factors code out of search-service implementations
 */
@Service("workbenchArtifactSearchService")
@Transactional(readOnly = true)
public class WorkbenchArtifactSearchServiceImpl extends AbstractSearchServiceImpl
		implements WorkbenchArtifactSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createArtifactPredicate(Root<? extends MLPAbstractWorkbenchArtifact> from, String name,
			Boolean active, String userId, String version, String serviceStatus, String repositoryUrl,
			String serviceUrl, boolean isOr) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();
		if (name != null && !name.isEmpty())
			predicates
					.add(cb.equal(cb.lower(from.<String>get(MLPAbstractWorkbenchArtifact_.name)), name.toLowerCase()));
		if (active != null) {
			Predicate activePredicate = active ? cb.isTrue(from.<Boolean>get(MLPAbstractWorkbenchArtifact_.ACTIVE))
					: cb.isFalse(from.<Boolean>get(MLPAbstractWorkbenchArtifact_.ACTIVE));
			predicates.add(activePredicate);
		}
		if (userId != null && !userId.isEmpty())
			predicates.add(
					cb.equal(cb.lower(from.<String>get(MLPAbstractWorkbenchArtifact_.userId)), userId.toLowerCase()));
		if (version != null && !version.isEmpty())
			predicates.add(
					cb.equal(cb.lower(from.<String>get(MLPAbstractWorkbenchArtifact_.version)), version.toLowerCase()));
		if (serviceStatus != null && !serviceStatus.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPAbstractWorkbenchArtifact_.serviceStatusCode)),
					serviceStatus.toLowerCase()));
		if (repositoryUrl != null && !repositoryUrl.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPAbstractWorkbenchArtifact_.repositoryUrl)),
					repositoryUrl.toLowerCase()));
		if (serviceUrl != null && !serviceUrl.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPNotebook_.SERVICE_URL)), serviceUrl.toLowerCase()));
		if (predicates.isEmpty())
			throw new IllegalArgumentException("Missing query values, must have at least one non-null");
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		return isOr ? cb.or(predArray) : cb.and(predArray);
	}

	/*
	 * Use JPA in Spring-Boot version 2.1
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page<MLPAbstractWorkbenchArtifact> findWorkbenchArtifacts(
			Class<? extends MLPAbstractWorkbenchArtifact> clazz, String name, Boolean active, String userId,
			String version, String serviceStatus, String repositoryUrl, String serviceUrl, boolean isOr,
			Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		countQueryDef.distinct(true);
		Root<? extends MLPAbstractWorkbenchArtifact> countFrom = countQueryDef.from(clazz);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createArtifactPredicate(countFrom, name, active, userId, version, serviceStatus,
				repositoryUrl, serviceUrl, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findArtifacts: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery rootQueryDef = cb.createQuery(clazz);
		// Coalesce any duplicates due to joins
		rootQueryDef.distinct(true);
		Root fromRoot = rootQueryDef.from(clazz);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createArtifactPredicate(countFrom, name, active, userId, version, serviceStatus,
				repositoryUrl, serviceUrl, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPAbstractWorkbenchArtifact> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPAbstractWorkbenchArtifact> queryResult = itemQuery.getResultList();
		logger.debug("findArtifacts: result size {}", queryResult.size());

		return new PageImpl<>(queryResult, pageable, count);
	}

}
