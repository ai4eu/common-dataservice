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

import org.acumos.cds.domain.MLPUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSearchService {

	/**
	 * Searches for instances matching all or one of the query parameters, depending
	 * on the isOr parameter; case is ignored in all String matches.
	 * 
	 * @param firstName
	 *                       First name; ignored if null
	 * @param middleName
	 *                       Middle name; ignored if null
	 * @param lastName
	 *                       Last name; ignored if null
	 * @param orgName
	 *                       Organization name; ignored if null
	 * @param email
	 *                       email; ignored if null
	 * @param loginName
	 *                       Login name; ignored if null
	 * @param active
	 *                       Active status; ignored if null
	 * @param isOr
	 *                       If true, the query is a disjunction ("or"); otherwise
	 *                       the query is a conjunction ("and").
	 * @param pageable
	 *                       Page and sort criteria
	 * @return Page of instances, which may be empty.
	 */
	Page<MLPUser> findUsers(String firstName, String middleName, String lastName, String orgName, String email,
			String loginName, Boolean active, boolean isOr, Pageable pageable);

}
