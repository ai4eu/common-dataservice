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

import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CatSolMapRepository extends PagingAndSortingRepository<MLPCatSolMap, MLPCatSolMap.CatSolMapPK> {

	/**
	 * Gets the count of solutions within a catalog.
	 * 
	 * @param catalogId
	 *                      Catalog ID
	 * @return Count of solutions
	 */
	@Query("SELECT COUNT(solutionId) FROM MLPCatSolMap WHERE catalogId = :catalogId")
	long countCatalogSolutions(@Param("catalogId") String catalogId);

	/**
	 * Gets a page of solutions in the specified catalog by joining on the
	 * catalog-solution mapping table.
	 * 
	 * @param catalogIds
	 *                       Catalog IDs
	 * @param pageable
	 *                       Page and sort criteria
	 * @return Page of MLPSolution
	 */
	@Query(value = "select s from MLPSolution s, MLPCatSolMap m " //
			+ " where s.solutionId =  m.solutionId " //
			+ " and m.catalogId in (:catalogIds)")
	Page<MLPSolution> findSolutionsByCatalogIds(@Param("catalogIds") String[] catalogIds, Pageable pageable);

	/**
	 * Deletes all entries for the specified solution ID.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteBySolutionId(@Param("solutionId") String solutionId);

}
