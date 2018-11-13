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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.acumos.cds.domain.MLPDomainModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

/**
 * Factors code out of search-service implementations
 */
public abstract class AbstractSearchServiceImpl {


	@Autowired
	protected EntityManager entityManager;

	/**
	 * Builds a list of sort orders suitable for supplying to the orderBy clause of
	 * a query.
	 * 
	 * @param cb
	 *                 Criteria Builder
	 * @param from
	 *                 Root item
	 * @param sort
	 *                 Spring sorting criteria
	 * @return List of javax.persistence.criteria.Order
	 */
	protected List<javax.persistence.criteria.Order> buildOrderList(CriteriaBuilder cb,
			Root<? extends MLPDomainModel> from, Sort sort) {
		List<javax.persistence.criteria.Order> jpaOrderList = new ArrayList<>();
		Iterator<org.springframework.data.domain.Sort.Order> sprOrderIter = sort.iterator();
		while (sprOrderIter.hasNext()) {
			org.springframework.data.domain.Sort.Order sprOrder = sprOrderIter.next();
			if (sprOrder.isAscending())
				jpaOrderList.add(cb.asc(from.get(sprOrder.getProperty())));
			else
				jpaOrderList.add(cb.desc(from.get(sprOrder.getProperty())));
		}
		return jpaOrderList;
	}
}
