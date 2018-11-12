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

import org.acumos.cds.domain.MLPUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<MLPUser, String> {

	/**
	 * Finds users for which loging name OR email address match.
	 * 
	 * @param name
	 *                 Login name or email address; both fields are checked.
	 * @return MLPUser
	 */
	@Query("SELECT u FROM MLPUser u " //
			+ " WHERE u.loginName = :name OR u.email = :name")
	MLPUser findByLoginOrEmail(@Param("name") String name);

	/**
	 * Finds users using a LIKE query on the text columns first name, middle name,
	 * last name, login name.
	 * 
	 * @param searchTerm
	 *                        fragment to find in the name fields
	 * @param pageRequest
	 *                        page and sort information
	 * @return Iterable of MLPUser
	 */
	@Query("SELECT u FROM MLPUser u " //
			+ " WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
			+ " OR LOWER(u.middleName)   LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
			+ " OR LOWER(u.lastName)     LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
			+ " OR LOWER(u.loginName)    LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<MLPUser> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageRequest);

}
