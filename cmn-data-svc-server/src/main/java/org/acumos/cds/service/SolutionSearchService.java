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

import java.util.Date;
import java.util.Map;

import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines methods to query solution information.
 */
public interface SolutionSearchService {

	/**
	 * Searches for instances matching all or one of the query parameters, depending
	 * on the isOr parameter.
	 * 
	 * @param queryParameters
	 *                            field-name, field-value pairs. Value may be scalar
	 *                            or array.
	 * @param isOr
	 *                            If true, the query is a disjunction ("or");
	 *                            otherwise the query is a conjunction ("and").
	 * @param pageable
	 *                            Page and sort criteria
	 * @return Page of instances, which may be empty.
	 */
	Page<MLPSolution> findSolutions(Map<String, ? extends Object> queryParameters, boolean isOr, Pageable pageable);

	/**
	 * Gets a page of solutions matching all query parameters.
	 * 
	 * @param nameKeywords
	 *                                Searches the name field for the keywords using
	 *                                case-insensitive LIKE after surrounding with
	 *                                wildcard '%' characters; ignored if null or
	 *                                empty
	 * @param descriptionKeywords
	 *                                Searches the revision descriptions for the
	 *                                keywords using case-insensitive LIKE after
	 *                                surrounding with wildcard '%' characters;
	 *                                ignored if null or empty
	 * @param active
	 *                                Active status: true or false; required.
	 * @param userIds
	 *                                Limits match to solutions with one of the
	 *                                specified values; ignored if null or empty
	 * @param modelTypeCodes
	 *                                Limits match to solutions with one of the
	 *                                specified values including null (not the
	 *                                4-character sequence "null"); ignored if null
	 *                                or empty
	 * @param accessTypeCodes
	 *                                Limits match to solutions containing revisions
	 *                                with one of the specified values including
	 *                                null (not the 4-character sequence "null");
	 *                                ignored if null or empty
	 * @param tags
	 *                                Limits match to solutions with one of the
	 *                                specified tags; ignored if null or empty
	 * @param authorKeywords
	 *                                Searches the author field for the keywords
	 *                                using case-insensitive LIKE after surrounding
	 *                                each with wildcard '%' characters; ignored if
	 *                                null or empty
	 * @param publisherKeywords
	 *                                Searches the publisher field for the keywords
	 *                                using case-insensitive LIKE after surrounding
	 *                                each with wildcard '%' characters; ignored if
	 *                                null or empty
	 * @param pageable
	 *                                Page and sort info
	 * @return Page of matches
	 */
	Page<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords, boolean active,
			String[] userIds, String[] modelTypeCodes, String[] accessTypeCodes, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, Pageable pageable);

	/**
	 * Gets a page of solutions matching all query parameters, with the caveat that
	 * any one keyword can match and multiple text fields are searched. If keywords
	 * are supplied this will be slow because it requires table scans.
	 * 
	 * @param keywords
	 *                            Keywords to find in the name, revision
	 *                            description, author, publisher and other fields;
	 *                            ignored if null or empty.
	 * @param active
	 *                            Active status: true or false; required.
	 * @param userIds
	 *                            Limits match to solutions with one of the
	 *                            specified values; ignored if null or empty
	 * @param modelTypeCodes
	 *                            Limits match to solutions with one of the
	 *                            specified values including null (not the
	 *                            4-character sequence "null"); ignored if null or
	 *                            empty
	 * @param accessTypeCodes
	 *                            Limits match to solutions containing revisions
	 *                            with one of the specified values including null
	 *                            (not the 4-character sequence "null"); ignored if
	 *                            null or empty
	 * @param tags
	 *                            Limits match to solutions with one of the
	 *                            specified tags; ignored if null or empty
	 * @param pageable
	 *                            Page and sort info
	 * @return Page of matches
	 */
	Page<MLPSolution> findPortalSolutionsByKw(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCodes, String[] accessTypeCodes, String[] tags, Pageable pageable);

	/**
	 * Gets a page of solutions matching all query parameters, with the caveat that
	 * any one keyword can match and multiple text fields are searched. If keywords
	 * are supplied this will be slow because it requires table scans.
	 * 
	 * @param keywords
	 *                            Keywords to find in the name, revision
	 *                            description, author, publisher and other fields;
	 *                            ignored if null or empty.
	 * @param active
	 *                            Active status: true or false; required.
	 * @param userIds
	 *                            Limits match to solutions with one of the
	 *                            specified values; ignored if null or empty
	 * @param modelTypeCodes
	 *                            Limits match to solutions with one of the
	 *                            specified values including null (not the
	 *                            4-character sequence "null"); ignored if null or
	 *                            empty
	 * @param accessTypeCodes
	 *                            Limits match to solutions containing revisions
	 *                            with one of the specified values including null
	 *                            (not the 4-character sequence "null"); ignored if
	 *                            null or empty
	 * @param allTags
	 *                            Solutions must have ALL tags in the supplied set;
	 *                            ignored if null or empty
	 * @param anyTags
	 *                            Solutions must have ANY tag in the supplied set
	 *                            (one or more); ignored if null or empty.
	 * @param pageable
	 *                            Page and sort info
	 * @return Page of matches
	 */
	Page<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCodes, String[] accessTypeCodes, String[] allTags, String[] anyTags, Pageable pageable);

	/**
	 * Gets a page of user-accessible solutions. This includes the user's own
	 * private solutions as well as solutions shared with the user via the
	 * solution-access-map table.
	 * 
	 * @param nameKeywords
	 *                                  Searches the name field for the keywords
	 *                                  using case-insensitive LIKE after
	 *                                  surrounding with wildcard '%' characters;
	 *                                  ignored if null or empty
	 * @param descriptionKeywords
	 *                                  Searches the revision descriptions for the
	 *                                  keywords using case-insensitive LIKE after
	 *                                  surrounding with wildcard '%' characters;
	 *                                  ignored if null or empty
	 * @param active
	 *                                  Active status: true or false; required.
	 * @param userId
	 *                                  ID of the user who owns or has access to the
	 *                                  solutions; required.
	 * @param modelTypeCodes
	 *                                  Limits match to solutions with one of the
	 *                                  specified values including null (not the
	 *                                  4-character sequence "null"); ignored if
	 *                                  null or empty
	 * @param accessTypeCodes
	 *                                  Limits match to solutions containing
	 *                                  revisions with one of the specified values
	 *                                  including null (not the 4-character sequence
	 *                                  "null"); ignored if null or empty
	 * @param tags
	 *                                  Limits match to solutions with one of the
	 *                                  specified tags; ignored if null or empty
	 * @param pageable
	 *                                  Page and sort info
	 * @return Page of matches
	 */
	public Page<MLPSolution> findUserSolutions(String[] nameKeywords, String[] descriptionKeywords, boolean active,
			String userId, String[] modelTypeCodes, String[] accessTypeCodes, String[] tags, Pageable pageable);

	/**
	 * Gets a page of solutions with recent modifications. The following entities
	 * are searched for a modified field value that is greater than or equal to the
	 * specified value: solution, revision, revision description, revision document
	 * and artifact. Only finds solutions that have 1+ revision(s) and in turn 1+
	 * artifact(s); i.e., a freshly created solution with no revisions will not be
	 * found.
	 * 
	 * @param active
	 *                            Active status: true or false; required
	 * @param accessTypeCodes
	 *                            Limits match to solutions with one of the
	 *                            specified values including null (not the
	 *                            4-character sequence "null"); ignored if null or
	 *                            empty
	 * @param modifiedDate
	 *                            The threshold value used in the search. Entities
	 *                            with modification dates prior to (smaller than)
	 *                            this point in time are ignored.
	 * @param pageable
	 *                            Page and sort info
	 * @return Page of matches
	 */
	Page<MLPSolution> findSolutionsByModifiedDate(boolean active, String[] accessTypeCodes, Date modifiedDate,
			Pageable pageable);

}
