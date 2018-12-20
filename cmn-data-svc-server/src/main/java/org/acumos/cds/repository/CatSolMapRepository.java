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

package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CatSolMapRepository extends PagingAndSortingRepository<MLPCatSolMap, MLPCatSolMap.CatSolMapPK> {

	/**
	 * Gets a page of solutions in the specified catalog by joining on the
	 * catalog-solution mapping table.
	 * 
	 * @param catalogId
	 *                      Catalog ID
	 * @param pageable
	 *                      Page and sort criteria
	 * @return Page of MLPSolution
	 */
	@Query(value = "select s from MLPSolution s, MLPCatSolMap m " //
			+ " where s.solutionId =  m.solutionId " //
			+ " and m.catalogId = :catalogId")
	Page<MLPSolution> findSolutionsByCatalogId(@Param("catalogId") String catalogId, Pageable pageable);

}
