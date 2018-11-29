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

import org.acumos.cds.domain.MLPSolGrpMemMap;
import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SolGrpMemMapRepository
		extends PagingAndSortingRepository<MLPSolGrpMemMap, MLPSolGrpMemMap.SolGrpMemMapPK> {

	/**
	 * Gets a page of solutions in the specified group by joining on the
	 * solution-group mapping table.
	 * 
	 * @param groupId
	 *                     Peer group ID
	 * @param pageable
	 *                     Page and sort criteria
	 * @return Page of MLPPeer
	 */
	@Query(value = "select s from MLPSolution s, MLPSolGrpMemMap m " //
			+ " where s.solutionId =  m.solutionId " //
			+ " and m.groupId = :groupId")
	Page<MLPSolution> findSolutionsByGroupId(@Param("groupId") long groupId, Pageable pageable);

}
