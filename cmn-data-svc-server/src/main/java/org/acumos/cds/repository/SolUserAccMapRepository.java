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

import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SolUserAccMapRepository
		extends PagingAndSortingRepository<MLPSolUserAccMap, MLPSolUserAccMap.SolUserAccessMapPK> {

	/**
	 * Gets all users that have access to the specified solution.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return Iterable of MLPUser
	 */
	@Query(value = "SELECT u FROM MLPUser u, MLPSolUserAccMap m " //
			+ " WHERE u.userId =  m.userId " //
			+ " AND m.solutionId = :solutionId")
	Iterable<MLPUser> getUsersForSolution(@Param("solutionId") String solutionId);

	/**
	 * Gets a page of solutions to which the specified user has access.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Pageable
	 * @return Page of MLPSolution
	 */
	@Query(value = "SELECT s FROM MLPSolution s, MLPSolUserAccMap m " //
			+ " WHERE s.solutionId =  m.solutionId " //
			+ " AND m.userId = :userId")
	Page<MLPSolution> getSolutionsForUser(@Param("userId") String userId, Pageable pageRequest);

	/**
	 * Deletes all entries for the specified solution ID.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteBySolutionId(@Param("solutionId") String solutionId);

	/**
	 * Gets the count of catalogs with the specified access-code type and containing
	 * the specified solution.
	 * 
	 * @param accessType
	 *                       Catalog access-type code; e.g., "PB"
	 * @param solutionId
	 *                       Solution ID
	 * @return Count of catalogs matching the criteria
	 */
	@Query(value = " SELECT COUNT(c.catalogId) FROM MLPCatalog c, MLPCatSolMap m " //
			+ " WHERE c.catalogId = m.catalogId " //
			+ " AND c.accessTypeCode = :accessType " //
			+ " AND m.solutionId = :solutionId ")
	long countCatalogsByAccessAndSolution(@Param("accessType") String accessType,
			@Param("solutionId") String solutionId);

}
