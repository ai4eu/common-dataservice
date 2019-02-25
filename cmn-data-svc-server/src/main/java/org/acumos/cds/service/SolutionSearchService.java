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

import java.time.Instant;
import java.util.Map;

import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * These methods implement the contracts defined in the REST client interface,
 * so the javadoc here is kept deliberately short to avoid duplication.
 */
public interface SolutionSearchService {

	/**
	 * Searches for solutions, single value per field
	 * 
	 * @param name
	 *                            Name
	 * @param active
	 *                            Active status
	 * @param userId
	 *                            User ID
	 * @param sourceId
	 *                            Source ID
	 * @param modelTypeCode
	 *                            Model type code
	 * @param toolkitTypeCode
	 *                            Toolkit type code
	 * @param origin
	 *                            Origin
	 * @param isOr
	 *                            true for or, false for and
	 * @param pageable
	 *                            Page and sort info
	 * @return Page of solutions
	 * @see org.acumos.cds.client.ICommonDataServiceRestClient#searchSolutions(Map,
	 *      boolean, org.acumos.cds.transport.RestPageRequest)
	 */
	Page<MLPSolution> searchSolutions(String name, Boolean active, String userId, String sourceId, String modelTypeCode,
			String toolkitTypeCode, String origin, boolean isOr, Pageable pageable);

	/**
	 * Searches for solutions, multiple values per field
	 * 
	 * @param nameKeywords
	 *                                Name keywords
	 * @param descriptionKeywords
	 *                                Description keywords
	 * @param active
	 *                                Active status
	 * @param userIds
	 *                                User IDs
	 * @param modelTypeCodes
	 *                                Model type codes
	 * @param accessTypeCodes
	 *                                Access type codes
	 * @param tags
	 *                                Tags
	 * @param authorKeywords
	 *                                authors
	 * @param publisherKeywords
	 *                                publishers
	 * @param pageable
	 *                                Page and sort info
	 * @return Page of solutions
	 * @see org.acumos.cds.client.ICommonDataServiceRestClient#findPortalSolutions(String[],
	 *      String[], boolean, String[], String[], String[], String[], String[],
	 *      String[], org.acumos.cds.transport.RestPageRequest)
	 */
	Page<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords, boolean active,
			String[] userIds, String[] modelTypeCodes, String[] accessTypeCodes, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, Pageable pageable);

	/**
	 * Searches for solutions, multiple values per field, enhanced for keywords and
	 * tags
	 * 
	 * @param keywords
	 *                            Keywords
	 * @param active
	 *                            Active status
	 * @param userIds
	 *                            User IDs
	 * @param modelTypeCodes
	 *                            Model type codes
	 * @param accessTypeCodes
	 *                            Access type codes
	 * @param allTags
	 *                            Tags that ALL must match
	 * @param anyTags
	 *                            Tags that ANY must match
	 * @param catalogIds
	 *                            Catalog IDs
	 * @param pageable
	 *                            Page and sort info
	 * @return Page of solutions
	 * @see org.acumos.cds.client.ICommonDataServiceRestClient#findPortalSolutionsByKwAndTags(String[],
	 *      boolean, String[], String[], String[], String[], String[], String[],
	 *      org.acumos.cds.transport.RestPageRequest)
	 */
	Page<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCodes, String[] accessTypeCodes, String[] allTags, String[] anyTags, String[] catalogIds,
			Pageable pageable);

	/**
	 * Searches for solutions editable by one user.
	 * 
	 * @param nameKeywords
	 *                                Name keywords
	 * @param descriptionKeywords
	 *                                Description keywords
	 * @param active
	 *                                Active status
	 * @param userId
	 *                                User ID
	 * @param modelTypeCodes
	 *                                Model type codes
	 * @param accessTypeCodes
	 *                                Access type codes
	 * @param tags
	 *                                Tags that ANY must match
	 * @param pageable
	 *                                Page and sort info
	 * @return Page of solutions
	 * @see org.acumos.cds.client.ICommonDataServiceRestClient#findUserSolutions(String[],
	 *      String[], boolean, String, String[], String[], String[],
	 *      org.acumos.cds.transport.RestPageRequest)
	 */
	public Page<MLPSolution> findUserSolutions(String[] nameKeywords, String[] descriptionKeywords, boolean active,
			String userId, String[] modelTypeCodes, String[] accessTypeCodes, String[] tags, Pageable pageable);

	/**
	 * Searches for solutions modified after a specified point in time. Primarily
	 * for federation.
	 * 
	 * @param active
	 *                            Active status
	 * @param accessTypeCodes
	 *                            Access type codes
	 * @param modifiedTs
	 *                            Point in time
	 * @param pageable
	 *                            Page and sort info
	 * @return Page of matches
	 * @see org.acumos.cds.client.ICommonDataServiceRestClient#findSolutionsByDate(boolean,
	 *      String[], Instant, org.acumos.cds.transport.RestPageRequest)
	 */
	Page<MLPSolution> findSolutionsByModifiedDate(boolean active, String[] accessTypeCodes, Instant modifiedTs,
			Pageable pageable);

}
