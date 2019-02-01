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

package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPRightToUse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface RightToUseRepository extends PagingAndSortingRepository<MLPRightToUse, Long> {

	/**
	 * Finds all RTU records for the specified solution that are also mapped to the
	 * specified user ID
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param userId
	 *                       User ID
	 * @return Iterable of role objects
	 */
	@Query(value = "select r from MLPRightToUse r, MLPRtuUserMap m" //
			+ " WHERE r.solutionId =  :solutionId " //
			+ " AND r.rtuId =  m.rtuId " //
			+ " AND m.userId = :userId")
	Iterable<MLPRightToUse> findBySolutionIdUserId(@Param("solutionId") String solutionId,
			@Param("userId") String userId);

}
