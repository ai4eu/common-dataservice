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

import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUser_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userSearchService")
@Transactional(readOnly = true)
public class UserSearchServiceImpl extends AbstractSearchServiceImpl implements UserSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createUserPredicate(Root<MLPUser> from, String firstName, String middleName, String lastName,
			String orgName, String email, String loginName, Boolean active, boolean isOr) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (firstName != null && !firstName.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPUser_.firstName)), firstName.toLowerCase()));
		if (middleName != null && !middleName.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPUser_.middleName)), middleName.toLowerCase()));
		if (lastName != null && !lastName.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPUser_.lastName)), lastName.toLowerCase()));
		if (orgName != null && !orgName.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPUser_.orgName)), orgName.toLowerCase()));
		if (email != null && !email.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPUser_.email)), email.toLowerCase()));
		if (loginName != null && !loginName.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPUser_.loginName)), loginName.toLowerCase()));
		if (active != null) {
			Predicate activePredicate = active ? cb.isTrue(from.<Boolean>get(MLPUser_.active))
					: cb.isFalse(from.<Boolean>get(MLPUser_.active));
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
	public Page<MLPUser> findUsers(String firstName, String middleName, String lastName, String orgName, String email,
			String loginName, Boolean active, boolean isOr, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		countQueryDef.distinct(true);
		Root<MLPUser> countFrom = countQueryDef.from(MLPUser.class);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createUserPredicate(countFrom, firstName, middleName, lastName, orgName, email, loginName,
				active, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findRoles: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery<MLPUser> rootQueryDef = cb.createQuery(MLPUser.class);
		// Coalesce any duplicates due to joins
		rootQueryDef.distinct(true);
		Root<MLPUser> fromRoot = rootQueryDef.from(MLPUser.class);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createUserPredicate(countFrom, firstName, middleName, lastName, orgName, email, loginName,
				active, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPUser> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPUser> queryResult = itemQuery.getResultList();
		logger.debug("findRoles: result size {}", queryResult.size());

		return new PageImpl<>(queryResult, pageable, count);
	}

}
