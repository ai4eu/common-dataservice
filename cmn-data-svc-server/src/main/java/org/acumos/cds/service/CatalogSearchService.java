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

package org.acumos.cds.service;

import org.acumos.cds.domain.MLPCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogSearchService {

	/**
	 * Searches for instances matching all or one of the query parameters, depending
	 * on the isOr parameter; case is ignored in all String matches.
	 * 
	 * @param accessTypeCode
	 *                           Access type code; ignored if null
	 * @param description
	 *                           Description; ignored if null
	 * @param name
	 *                           Name; ignored if null
	 * @param origin
	 *                           Origin string; ignored if null
	 * @param publisher
	 *                           Publisher ; ignored if null
	 * @param url
	 *                           URL; ignored if null
	 * @param isOr
	 *                           If true, the query is a disjunction ("or");
	 *                           otherwise the query is a conjunction ("and").
	 * @param pageable
	 *                           Page and sort criteria
	 * @return Page of instances, which may be empty.
	 */
	Page<MLPCatalog> findCatalogs(String accessTypeCode, String description, String name, String origin,
			String publisher, String url, boolean isOr, Pageable pageable);

}
