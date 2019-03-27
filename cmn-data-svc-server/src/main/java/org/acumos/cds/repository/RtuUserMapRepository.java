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

import org.acumos.cds.domain.MLPRtuUserMap;
import org.acumos.cds.domain.MLPUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RtuUserMapRepository extends CrudRepository<MLPRtuUserMap, MLPRtuUserMap.RtuUserMapPK> {

	/**
	 * Finds all user records that are mapped to the specified RTU ID. Size is
	 * expected to be modest, so this does not use page.
	 * 
	 * @param rtuId
	 *                  Right-to-use ID
	 * @return Iterable of MLPUser objects
	 */
	@Query(value = "select u from MLPUser u, MLPRtuUserMap m" //
			+ " WHERE u.userId = m.userId " //
			+ " AND m.rtuId = :rtuId ")
	Iterable<MLPUser> findUsersByRtuId(@Param("rtuId") Long rtuId);

}
