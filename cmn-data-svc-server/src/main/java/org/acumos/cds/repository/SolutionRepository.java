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

import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Defines methods to process queries on specific fields and yield paginated
 * results. A value must be specified for every field, which is the key
 * difference from the methods in the solution search service class.
 */
public interface SolutionRepository extends JpaRepository<MLPSolution, String>, JpaSpecificationExecutor<MLPSolution> {

	/**
	 * Increments the solution view count by 1, with special handling for the first
	 * time.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	@Modifying
	@Transactional // throws exception without this
	@Query(value = "UPDATE MLPSolution s SET s.viewCount = "//
			+ " CASE WHEN s.viewCount is null THEN 1 ELSE (s.viewCount + 1) END" //
			+ " WHERE s.solutionId = :solutionId")
	void incrementViewCount(@Param("solutionId") String solutionId);

	/**
	 * Finds solutions using a LIKE query on the text column NAME.
	 * 
	 * @param searchTerm
	 *                        fragment to find in text columns
	 * @param pageRequest
	 *                        Page and sort criteria
	 * @return Page of MLPSolution
	 */
	@Query("SELECT s FROM MLPSolution s " //
			+ " WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<MLPSolution> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageRequest);

	/**
	 * Gets all solutions that use the specified tag.
	 * 
	 * @param tag
	 *                        Tag string
	 * @param pageRequest
	 *                        Page and sort criteria
	 * @return Page of MLPSolution
	 */
	@Query(value = "SELECT s FROM MLPSolution s, MLPSolTagMap m " //
			+ " WHERE s.solutionId =  m.solutionId " //
			+ "   AND m.tag = :tag")
	Page<MLPSolution> findByTag(@Param("tag") String tag, Pageable pageRequest);

	/**
	 * Searches for active solutions accessible to the specified peer by traversing
	 * the peer group and solution group membership mapping tables. Those solutions
	 * are expected to have only private revisions, but that's not checked here.
	 * 
	 * @param peerId
	 *                        Peer ID
	 * @param pageRequest
	 *                        Page and sort criteria
	 * @return Page of MLPSolution, which may be empty
	 */
	@Query(value = "SELECT DISTINCT s FROM MLPSolution s, MLPPeerGrpMemMap pg, MLPPeerSolAccMap psg, MLPSolGrpMemMap sg "
			+ " WHERE s.active = true "//
			+ "   AND pg.peerId = :peerId "//
			+ "   AND pg.groupId = psg.peerGroupId " //
			+ "   AND psg.solutionGroupId = sg.groupId ")
	Page<MLPSolution> findRestrictedSolutions(@Param("peerId") String peerId, Pageable pageRequest);

}
