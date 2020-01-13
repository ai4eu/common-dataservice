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

package org.acumos.cds.client;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.springframework.web.client.RestClientResponseException;

/**
 * <P>
 * Defines the interface of the Controller REST client.
 * </P>
 * <P>
 * The server answers 400 (bad request) on any problem in the request, such as
 * missing required data or attempting to update or delete an item that does not
 * exists. Any non-success code results in an exception. Users are STRONGLY
 * advised to catch the unchecked runtime exception RestClientResponseException
 * and get the detailed error message reported by the server via method
 * {@link org.springframework.web.client.RestClientResponseException#getResponseBodyAsString()}.
 * </P>
 */
public interface ICommonDataServiceRestClient {

	/**
	 * Sets the request ID to use in a header on every request to the server. If no
	 * request ID is set, or if this method is called with null, the implementation
	 * must generate a new ID for each request.
	 * 
	 * @param requestId
	 *                      A request identifier
	 */
	void setRequestId(String requestId);

	/**
	 * Checks the health of the server by querying the database.
	 * 
	 * @return Object with health string
	 */
	SuccessTransport getHealth();

	/**
	 * Gets the server version string.
	 * 
	 * @return Object with version string
	 */
	SuccessTransport getVersion();

	/**
	 * Gets the list of value set names that can be used to fetch code-name pairs.
	 * 
	 * @return List of names
	 */
	List<String> getValueSetNames();

	/**
	 * Gets the list of code-name pair entries for the specified value set.
	 * 
	 * @param valueSetName
	 *                         Value set name
	 * @return List of code-name pairs
	 * @throws RestClientResponseException
	 *                                         If the value-set name is not known
	 */
	List<MLPCodeNamePair> getCodeNamePairs(CodeNameType valueSetName);

	/**
	 * Gets count of solutions.
	 * 
	 * @return Count of solutions.
	 */
	long getSolutionCount();

	/**
	 * Gets a page of solutions.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> getSolutions(RestPageRequest pageRequest);

	/**
	 * Searches for solutions with names or descriptions that contain the search
	 * term using the like operator.
	 * 
	 * @param searchTerm
	 *                        Limits match to solutions with name fields containing
	 *                        the specified string; uses a case-insensitive LIKE
	 *                        after surrounding the term with wildcard '%'
	 *                        characters
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution objects, which may be empty
	 */
	RestPageResponse<MLPSolution> findSolutionsBySearchTerm(String searchTerm, RestPageRequest pageRequest);

	/**
	 * Searches for solutions with attributes matching the non-null values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            active, userId, sourceId, modelTypeCode,
	 *                            toolkitTypeCode, origin.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size, sort information; defaults
	 *                            to page 0 of size 20 if null.
	 * @return Page of solution objects, which may be empty
	 */
	RestPageResponse<MLPSolution> searchSolutions(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets a page of solutions that are tagged with the specified tag.
	 * 
	 * @param tag
	 *                        Tag to find by exact match
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> findSolutionsByTag(String tag, RestPageRequest pageRequest);

	/**
	 * Gets a page of solutions matching all query parameters. Most parameters can
	 * be a set of values, and for those a match is found if ANY ONE of the values
	 * matches. In other words, this is a conjunction of disjunctions. This may be
	 * slow because it requires table scans.
	 * 
	 * @deprecated Use
	 *             {@link #findPublishedSolutionsByKwAndTags(String[], boolean, String[], String[], String[], String[], String[], RestPageRequest)}
	 * 
	 * @param nameKeywords
	 *                                Limits match to solutions with names
	 *                                containing ANY of the keywords; uses
	 *                                case-insensitive LIKE after surrounding each
	 *                                keyword with wildcard '%' characters; ignored
	 *                                if null or empty
	 * @param descriptionKeywords
	 *                                Limits match to solutions containing revisions
	 *                                with descriptions that have ANY of the
	 *                                keywords; uses case-insensitive LIKE after
	 *                                surrounding each keyword with wildcard '%'
	 *                                characters; ignored if null or empty
	 * @param active
	 *                                Solution active status; true for active, false
	 *                                for inactive
	 * @param userIds
	 *                                Limits match to solutions with associated user
	 *                                (creator) that matches ANY of the specified
	 *                                values; ignored if null or empty
	 * @param modelTypeCodes
	 *                                Limits match to solutions with ANY of the
	 *                                specified values including null (which is
	 *                                different from the special-case 4-character
	 *                                sequence "null"); ignored if null or empty
	 * @param tags
	 *                                Limits match to solutions with ANY of the
	 *                                specified tags; ignored if null or empty
	 * @param authorKeywords
	 *                                Limits match to solutions with a revision
	 *                                containing an author field with ANY of the
	 *                                specified keywords; uses case-insensitive LIKE
	 *                                after surrounding each keyword with wildcard
	 *                                '%' characters; ignored if null or empty
	 * @param publisherKeywords
	 *                                Same as author, but on the publisher field.
	 * @param pageRequest
	 *                                Page index, page size and sort information;
	 *                                defaults to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	@Deprecated
	RestPageResponse<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String[] userIds, String[] modelTypeCodes, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, RestPageRequest pageRequest);

	/**
	 * Gets a page of published solutions that match every condition, with the
	 * caveat that any one of the keywords can match, and multiple free-text fields
	 * are searched. Other facets such as userId, model type code, etc. must match.
	 * 
	 * @param keywords
	 *                           Keywords to find in the name, revision description,
	 *                           author, publisher and other field; ignored if null
	 *                           or empty
	 * @param active
	 *                           Solution active status; true for active, false for
	 *                           inactive
	 * @param userIds
	 *                           User IDs who created the solution; ignored if null
	 *                           or empty
	 * @param modelTypeCodes
	 *                           Model type codes; use four-letter sequence "null"
	 *                           to match a null value; ignored if null or empty
	 * @param allTags
	 *                           Solutions must have ALL tags in the supplied set;
	 *                           ignored if null or empty
	 * @param anyTags
	 *                           Solutions must have ANY tag in the supplied set
	 *                           (one or more); ignored if null or empty.
	 * @param catalogIds
	 *                           Solutions must be mapped to one of the specified
	 *                           catalogs. Matches all catalogs if null or empty;
	 *                           i.e., limits matches to solutions published to a
	 *                           catalog.
	 * @param pageRequest
	 *                           Page index, page size and sort information;
	 *                           defaults to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> findPublishedSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCodes, String[] allTags, String[] anyTags, String[] catalogIds,
			RestPageRequest pageRequest);

	/**
	 * Gets a page of published solutions that were modified after the specified
	 * point in time and match the additional parameter values. Checks the modified
	 * field on the solution, the revisions for the solution, the artifacts in the
	 * revisions, the descriptions for the revisions and the documents for the
	 * revisions. A solution must have revision(s) and artifact(s) to match.
	 * 
	 * Caveat: finds solutions with any description or document modification in any
	 * catalog, not just the catalog IDs specified.
	 * 
	 * @param catalogIds
	 *                        Solutions must be mapped to one of the specified
	 *                        catalogs. Matches all catalogs if null or empty; i.e.,
	 *                        limits matches to solutions published to a catalog.
	 * @param instant
	 *                        Point in time. Entities with modification dates prior
	 *                        to (i.e., smaller than) this point in time are
	 *                        ignored.
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> findPublishedSolutionsByDate(String[] catalogIds, Instant instant,
			RestPageRequest pageRequest);

	/**
	 * Gets a page of solutions editable by the specified user and matching all
	 * query parameters. A user's editable solutions include the specified user's
	 * own solutions (created by that user) AND solutions created by a different
	 * user and shared with the specified user. Most parameters can be a set of
	 * values, and a match is found for that parameter if ANY ONE of the values
	 * matches. In other words, this is a conjunction of disjunctions. This
	 * special-purpose method supports the My Models dynamic search page.
	 * 
	 * @param active
	 *                                Solution active status; true for active, false
	 *                                for inactive; required.
	 * @param published
	 *                                Whether solution appears in any catalog; true
	 *                                for yes (published), false for no (not
	 *                                published); required.
	 * @param userId
	 *                                Limits match to solutions with this user ID OR
	 *                                shared with this user ID; required.
	 * @param nameKeywords
	 *                                Limits match to solutions with names
	 *                                containing ANY of the keywords; uses
	 *                                case-insensitive LIKE after surrounding each
	 *                                keyword with wildcard '%' characters; ignored
	 *                                if null or empty
	 * @param descriptionKeywords
	 *                                Limits match to solutions containing revisions
	 *                                with descriptions that have ANY of the
	 *                                keywords; uses case-insensitive LIKE after
	 *                                surrounding each keyword with wildcard '%'
	 *                                characters; ignored if null or empty
	 * @param modelTypeCodes
	 *                                Limits match to solutions with codes that
	 *                                match ANY of the specified values including
	 *                                null (which is different from the special-case
	 *                                4-character sequence "null"); ignored if null
	 *                                or empty
	 * @param tags
	 *                                Limits match to solutions with ANY of the
	 *                                specified tags; ignored if null or empty
	 * @param pageRequest
	 *                                Page index, page size and sort information;
	 *                                defaults to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> findUserSolutions(boolean active, boolean published, String userId,
			String[] nameKeywords, String[] descriptionKeywords, String[] modelTypeCodes, String[] tags,
			RestPageRequest pageRequest);

	/**
	 * Gets the solution with the specified ID.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @return Solution object
	 */
	MLPSolution getSolution(String solutionId);

	/**
	 * Creates a new solution and generates an ID if needed.
	 * 
	 * @param solution
	 *                     Solution data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known. Any tags in the entry will be created
	 *                     if needed.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolution createSolution(MLPSolution solution) throws RestClientResponseException;

	/**
	 * Updates an existing solution with the supplied data. Any tags in the entry
	 * will be created if needed.
	 * 
	 * @param solution
	 *                     Solution data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolution(MLPSolution solution) throws RestClientResponseException;

	/**
	 * Increments the view count of the specified solution. This convenience method
	 * requires only one database access, instead of two to fetch the solution
	 * entity and save it again.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void incrementSolutionViewCount(String solutionId) throws RestClientResponseException;

	/**
	 * Deletes the solution with the specified ID. Cascades the delete to
	 * solution-revision records and related entities such as composite solutions,
	 * solution downloads, publish request and so on. The solution-revision in turn
	 * cascades the delete to artifacts and related records. Answers bad request if
	 * the ID is not known.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolution(String solutionId) throws RestClientResponseException;

	/**
	 * Gets all revisions for the specified solution ID.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @return List of solution revision objects, which may be empty
	 */
	List<MLPSolutionRevision> getSolutionRevisions(String solutionId);

	/**
	 * Gets all solution revisions for the specified solution IDs.
	 * 
	 * @param solutionIds
	 *                        solution IDs. Caveat: the number of possible entries
	 *                        in this list is constrained by client/server
	 *                        limitations on URL length.
	 * @return List of Solution revision objects for any of the specified solutions,
	 *         which may be empty
	 */
	List<MLPSolutionRevision> getSolutionRevisions(String[] solutionIds);

	/**
	 * Gets the solution revision with the specified ID.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param revisionId
	 *                       revision ID
	 * @return Solution revision object
	 */
	MLPSolutionRevision getSolutionRevision(String solutionId, String revisionId);

	/**
	 * Gets the solution revisions for the specified artifact ID.
	 * 
	 * @param artifactId
	 *                       artifact ID
	 * @return List of Solution revision objects for the specified artifact, which
	 *         may be empty
	 */
	List<MLPSolutionRevision> getSolutionRevisionsForArtifact(String artifactId);

	/**
	 * Creates a new revision and generates an ID if needed.
	 * 
	 * @param revision
	 *                     Solution revision data. If the ID field is null a new
	 *                     value is generated; otherwise the ID value is used if
	 *                     valid and not already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionRevision createSolutionRevision(MLPSolutionRevision revision) throws RestClientResponseException;

	/**
	 * Updates an existing revision with the supplied data.
	 * 
	 * @param revision
	 *                     Solution revision data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolutionRevision(MLPSolutionRevision revision) throws RestClientResponseException;

	/**
	 * Deletes the revision with the specified ID. Cascades the delete to related
	 * records including artifacts. Answers bad request if the ID is not known.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param revisionId
	 *                       revision ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionRevision(String solutionId, String revisionId) throws RestClientResponseException;

	/**
	 * Gets the artifacts for the specified solution revision.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param revisionId
	 *                       revision ID
	 * @return List of artifacts, which may be empty
	 */
	List<MLPArtifact> getSolutionRevisionArtifacts(String solutionId, String revisionId);

	/**
	 * Adds the specified artifact to the specified solution revision.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @param artifactId
	 *                       Artifact Id
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId)
			throws RestClientResponseException;

	/**
	 * Removes the specified artifact from the specified solution revision.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @param artifactId
	 *                       Artifact Id
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId)
			throws RestClientResponseException;

	/**
	 * Gets a page of solution tags.
	 *
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution tag objects, which may be empty
	 */
	RestPageResponse<MLPTag> getTags(RestPageRequest pageRequest);

	/**
	 * Creates a new solution tag.
	 * 
	 * @param tag
	 *                tag object
	 * @return Complete object which wraps the tag
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPTag createTag(MLPTag tag) throws RestClientResponseException;

	/**
	 * Deletes the specified solution tag. A tag can be deleted if is not associated
	 * with any other entities; if associations remain the delete will fail.
	 * 
	 * @param tag
	 *                tag object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteTag(MLPTag tag) throws RestClientResponseException;

	/**
	 * Gets all tags assigned to the specified solution ID.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @return List of tags, which may be empty
	 */
	List<MLPTag> getSolutionTags(String solutionId);

	/**
	 * Adds the specified tag to the specified solution. Creates the tag if needed.
	 * 
	 * @param tag
	 *                       tag string
	 * @param solutionId
	 *                       solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addSolutionTag(String solutionId, String tag) throws RestClientResponseException;

	/**
	 * Removes the specified tag from the specified solution.
	 * 
	 * @param tag
	 *                       tag string
	 * @param solutionId
	 *                       solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropSolutionTag(String solutionId, String tag) throws RestClientResponseException;

	/**
	 * Gets the count of artifacts.
	 * 
	 * @return Count of artifacts.
	 */
	long getArtifactCount();

	/**
	 * Gets a page of artifacts.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of artifacts, which may be empty
	 */
	RestPageResponse<MLPArtifact> getArtifacts(RestPageRequest pageRequest);

	/**
	 * Searches for artifacts with names or descriptions that contain the search
	 * term using the like operator; empty if no matches are found.
	 * 
	 * @param searchTerm
	 *                        String to find
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of artifacts, which may be empty
	 */
	RestPageResponse<MLPArtifact> findArtifactsBySearchTerm(String searchTerm, RestPageRequest pageRequest);

	/**
	 * Searches for artifacts with attributes matching the non-null values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names:
	 *                            artifactTypeCode, name, uri, version, userId.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of artifacts, which may be empty
	 */
	RestPageResponse<MLPArtifact> searchArtifacts(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the artifact with the specified ID.
	 * 
	 * @param artifactId
	 *                       artifact ID
	 * @return Artifact object
	 */
	MLPArtifact getArtifact(String artifactId);

	/**
	 * Creates a new artifact and generates an ID if needed.
	 * 
	 * @param artifact
	 *                     Artifact data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPArtifact createArtifact(MLPArtifact artifact) throws RestClientResponseException;

	/**
	 * Updates an existing artifact with the supplied data.
	 * 
	 * @param artifact
	 *                     Artifact data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateArtifact(MLPArtifact artifact) throws RestClientResponseException;

	/**
	 * Deletes an artifact with the specified ID. Cascades the delete; e.g., removes
	 * the association with any solution revisions and other records. Answers bad
	 * request if the ID is not known.
	 * 
	 * @param artifactId
	 *                       artifact ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteArtifact(String artifactId) throws RestClientResponseException;

	/**
	 * Gets count of users.
	 * 
	 * @return Count of users.
	 */
	long getUserCount();

	/**
	 * Gets a page of users.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of users, which may be empty
	 */
	RestPageResponse<MLPUser> getUsers(RestPageRequest pageRequest);

	/**
	 * Returns a page of users with names that contain the search term matched using
	 * a like operator on the first, middle, last and login-name fields.
	 * 
	 * @param searchTerm
	 *                        String to find
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of users, which may be empty
	 */
	RestPageResponse<MLPUser> findUsersBySearchTerm(String searchTerm, RestPageRequest pageRequest);

	/**
	 * Searches users for exact matches.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names:
	 *                            firstName, middleName, lastName, orgName, email,
	 *                            loginName, active.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of users, which may be empty
	 */
	RestPageResponse<MLPUser> searchUsers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Checks the specified credentials for full access. Does NOT check the
	 * expiration date of the password, the client must do that as needed.
	 * 
	 * Side effects: updates last-login field on success, count on failure. Imposes
	 * a temporary block after repeated failures as configured at server.
	 * 
	 * @param name
	 *                 login name or email address; both attributes are checked
	 * @param pass
	 *                 clear-text password
	 * @return User object if a match for an active user is found.
	 * @throws RestClientResponseException
	 *                                         If the user is not found, is not
	 *                                         active or the password does not
	 *                                         match. The exception message reveals
	 *                                         details such as existence of the
	 *                                         user, and should NOT be passed on to
	 *                                         end users. Error message is in the
	 *                                         response body.
	 */
	MLPUser loginUser(String name, String pass) throws RestClientResponseException;

	/**
	 * Checks the specified credentials for API access.
	 * 
	 * Side effects: updates last-login field on success, count on failure. Imposes
	 * a temporary block after repeated failures as configured at server.
	 * 
	 * @param name
	 *                     login name or email address; both attributes are checked
	 * @param apiToken
	 *                     clear-text API token
	 * @return User object if a match for an active user is found.
	 * @throws RestClientResponseException
	 *                                         If the user is not found, is not
	 *                                         active or the token does not match.
	 *                                         The exception message reveals details
	 *                                         such as existence of the user, and
	 *                                         should NOT be passed on to end users.
	 *                                         Error message is in the response
	 *                                         body.
	 */
	MLPUser loginApiUser(String name, String apiToken) throws RestClientResponseException;

	/**
	 * Checks the specified credentials for verification. This does NOT check the
	 * expiration date of the token, the client must do that as needed.
	 * 
	 * Side effects: updates last-login field on success, count on failure. Imposes
	 * a temporary block after repeated failures as configured at server.
	 * 
	 * @param name
	 *                        login name or email address; both attributes are
	 *                        checked
	 * @param verifyToken
	 *                        clear-text verification token
	 * @return User object if a match for an active user is found.
	 * @throws RestClientResponseException
	 *                                         If the user is not found, is not
	 *                                         active or the token does not match.
	 *                                         The exception message reveals details
	 *                                         such as existence of the user, and
	 *                                         should NOT be passed on to end users.
	 *                                         Error message is in the response
	 *                                         body.
	 */
	MLPUser verifyUser(String name, String verifyToken) throws RestClientResponseException;

	/**
	 * Gets the user with the specified ID.
	 * 
	 * @param userId
	 *                   user ID
	 * @return User object
	 */
	MLPUser getUser(String userId);

	/**
	 * Creates a new user and generates an ID if needed.
	 * 
	 * @param user
	 *                 User data. If the ID field is null a new value is generated;
	 *                 otherwise the ID value is used if valid and not already
	 *                 known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPUser createUser(MLPUser user) throws RestClientResponseException;

	/**
	 * Updates an existing user with the supplied data.
	 * 
	 * @param user
	 *                 User data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateUser(MLPUser user) throws RestClientResponseException;

	/**
	 * Deletes the user with the specified ID. Cascades the delete to
	 * login-provider, notification and role associations. If associations remain
	 * with artifacts such as solutions the delete will fail.
	 * 
	 * @param userId
	 *                   user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteUser(String userId) throws RestClientResponseException;

	/**
	 * Gets all roles assigned to the specified user ID.
	 * 
	 * @param userId
	 *                   user ID
	 * @return List of roles, which may be empty
	 */
	List<MLPRole> getUserRoles(String userId);

	/**
	 * Adds the specified role to the specified user's roles.
	 * 
	 * @param userId
	 *                   user ID
	 * @param roleId
	 *                   role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addUserRole(String userId, String roleId) throws RestClientResponseException;

	/**
	 * Assigns the specified roles to the specified user after dropping any existing
	 * role assignments.
	 * 
	 * @param userId
	 *                    user ID
	 * @param roleIds
	 *                    List of role IDs
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateUserRoles(String userId, List<String> roleIds) throws RestClientResponseException;

	/**
	 * Removes the specified role from the specified user's roles.
	 * 
	 * @param userId
	 *                   user ID
	 * @param roleId
	 *                   role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropUserRole(String userId, String roleId) throws RestClientResponseException;

	/**
	 * Adds the specified role to every specified user's roles.
	 * 
	 * @param userIds
	 *                    List of user IDs
	 * @param roleId
	 *                    role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addUsersInRole(List<String> userIds, String roleId) throws RestClientResponseException;

	/**
	 * Removes the specified role from every specified user's roles.
	 * 
	 * @param userIds
	 *                    List of user IDs
	 * @param roleId
	 *                    role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropUsersInRole(List<String> userIds, String roleId) throws RestClientResponseException;

	/**
	 * Gets count of users with the specified role.
	 * 
	 * @param roleId
	 *                   role ID
	 * @return Count of users in that role
	 */
	long getRoleUsersCount(String roleId);

	/**
	 * Gets a page of users assigned to the specified role.
	 * 
	 * @param roleId
	 *                        role ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of users, which may be empty
	 */
	RestPageResponse<MLPUser> getRoleUsers(String roleId, RestPageRequest pageRequest);

	/**
	 * Gets the login provider for the specified user, provider code and provider
	 * login.
	 * 
	 * @param userId
	 *                          user ID
	 * @param providerCode
	 *                          Provider code
	 * @param providerLogin
	 *                          User login at the provider
	 * @return user login provider
	 */
	MLPUserLoginProvider getUserLoginProvider(String userId, String providerCode, String providerLogin);

	/**
	 * Gets all login providers for the specified user.
	 * 
	 * @param userId
	 *                   user ID
	 * @return List of user login providers, which may be empty
	 */
	List<MLPUserLoginProvider> getUserLoginProviders(String userId);

	/**
	 * Creates a new user login provider.
	 * 
	 * @param provider
	 *                     data to populate new entry
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPUserLoginProvider createUserLoginProvider(MLPUserLoginProvider provider) throws RestClientResponseException;

	/**
	 * Updates an existing user login provider with the supplied data.
	 * 
	 * @param provider
	 *                     data to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateUserLoginProvider(MLPUserLoginProvider provider) throws RestClientResponseException;

	/**
	 * Deletes the specified user login provider.
	 * 
	 * @param provider
	 *                     data to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteUserLoginProvider(MLPUserLoginProvider provider) throws RestClientResponseException;

	/**
	 * Gets count of roles.
	 * 
	 * @return Count of roles.
	 */
	long getRoleCount();

	/**
	 * Searches for roles with attributes matching the non-null values specified as
	 * query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            active.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of roles, which may be empty
	 */
	RestPageResponse<MLPRole> searchRoles(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets a page of roles.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of roles, which may be empty
	 */
	RestPageResponse<MLPRole> getRoles(RestPageRequest pageRequest);

	/**
	 * Gets the role with the specified ID.
	 * 
	 * @param roleId
	 *                   role ID
	 * @return instance with the specified ID; null if none exists.
	 */
	MLPRole getRole(String roleId);

	/**
	 * Creates a new role and generates an ID if needed.
	 * 
	 * @param role
	 *                 Role data. If the ID field is null a new value is generated;
	 *                 otherwise the ID value is used if valid and not already
	 *                 known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRole createRole(MLPRole role) throws RestClientResponseException;

	/**
	 * Updates an existing role with the supplied data.
	 * 
	 * @param role
	 *                 instance to save
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRole(MLPRole role) throws RestClientResponseException;

	/**
	 * Deletes the role with the specified ID. A role can be deleted if is not
	 * associated with any users. Cascades the delete to associated role functions.
	 * 
	 * @param roleId
	 *                   Role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRole(String roleId) throws RestClientResponseException;

	/**
	 * Gets the functions for the specified role.
	 * 
	 * @param roleId
	 *                   role ID
	 * @return List of role functions, which may be empty
	 */
	List<MLPRoleFunction> getRoleFunctions(String roleId);

	/**
	 * Gets the role function with the specified ID.
	 * 
	 * @param roleId
	 *                           role ID
	 * @param roleFunctionId
	 *                           role function ID
	 * @return instance with the specified ID; null if none exists.
	 */
	MLPRoleFunction getRoleFunction(String roleId, String roleFunctionId);

	/**
	 * Creates a new role function and generates an ID.
	 * 
	 * @param roleFunction
	 *                         instance to save
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRoleFunction createRoleFunction(MLPRoleFunction roleFunction) throws RestClientResponseException;

	/**
	 * Updates an existing role function with the supplied data.
	 * 
	 * @param roleFunction
	 *                         instance to save
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRoleFunction(MLPRoleFunction roleFunction) throws RestClientResponseException;

	/**
	 * Deletes the role function with the specified ID.
	 * 
	 * @param roleId
	 *                           role ID
	 * @param roleFunctionId
	 *                           role function ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRoleFunction(String roleId, String roleFunctionId) throws RestClientResponseException;

	/**
	 * Gets a page of peers.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of peers, which may be empty
	 */
	RestPageResponse<MLPPeer> getPeers(RestPageRequest pageRequest);

	/**
	 * Searches for peers with attributes matching the non-null values specified as
	 * query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            subjectName, apiUrl, webUrl, isSelf, isLocal,
	 *                            contact1, statusCode.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of peers, which may be empty
	 */
	RestPageResponse<MLPPeer> searchPeers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the peer with the specified ID.
	 * 
	 * @param peerId
	 *                   Instance ID
	 * @return User object
	 */
	MLPPeer getPeer(String peerId);

	/**
	 * Creates a new peer and generates an ID if needed.
	 * 
	 * @param peer
	 *                 Peer data. If the ID field is null a new value is generated;
	 *                 otherwise the ID value is used if valid and not already
	 *                 known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPPeer createPeer(MLPPeer peer) throws RestClientResponseException;

	/**
	 * Updates an existing peer with the supplied data.
	 * 
	 * @param user
	 *                 Peer data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updatePeer(MLPPeer user) throws RestClientResponseException;

	/**
	 * Deletes the peer with the specified ID. Cascades the delete to peer
	 * subscriptions. If other associations remain the delete will fail.
	 * 
	 * @param peerId
	 *                   Instance ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deletePeer(String peerId) throws RestClientResponseException;

	/**
	 * Gets the count of subscriptions for the specified peer.
	 * 
	 * @param peerId
	 *                   Instance ID
	 * @return number of subscriptions for the peer
	 */
	long getPeerSubscriptionCount(String peerId);

	/**
	 * Gets all subscriptions for the specified peer.
	 * 
	 * @param peerId
	 *                   Peer ID
	 * @return List of peer subscriptions, which may be empty
	 */
	List<MLPPeerSubscription> getPeerSubscriptions(String peerId);

	/**
	 * Gets the peer subscription with the specified ID.
	 * 
	 * @param subscriptionId
	 *                           Subscription ID
	 * @return Peer subscription object
	 */
	MLPPeerSubscription getPeerSubscription(Long subscriptionId);

	/**
	 * Creates a peer subscription
	 * 
	 * @param peerSub
	 *                    subscription to create
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub) throws RestClientResponseException;

	/**
	 * Updates a peer subscription
	 * 
	 * @param peerSub
	 *                    subscription to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updatePeerSubscription(MLPPeerSubscription peerSub) throws RestClientResponseException;

	/**
	 * Deletes a peer subscription.
	 *
	 * @param subscriptionId
	 *                           Peer subscription ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deletePeerSubscription(Long subscriptionId) throws RestClientResponseException;

	/**
	 * Gets a page of download details for the specified solution's artifacts.
	 * 
	 * @param solutionId
	 *                        Instance ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution downloads, which may be empty
	 */
	RestPageResponse<MLPSolutionDownload> getSolutionDownloads(String solutionId, RestPageRequest pageRequest);

	/**
	 * Creates a new solution-artifact download object with a generated ID.
	 * 
	 * @param download
	 *                     Instance to save
	 * @return Complete object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionDownload createSolutionDownload(MLPSolutionDownload download) throws RestClientResponseException;

	/**
	 * Deletes the solution-artifact download object with the specified ID.
	 * 
	 * @param download
	 *                     Instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionDownload(MLPSolutionDownload download) throws RestClientResponseException;

	/**
	 * Gets a page of solutions that the specified user has marked as favorite.
	 * <P>
	 * (This does NOT return MLPSolutionFavorite objects!)
	 * 
	 * @param userId
	 *                        Instance ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty.
	 */
	RestPageResponse<MLPSolution> getFavoriteSolutions(String userId, RestPageRequest pageRequest);

	/**
	 * Marks the specified solution as a favorite of the specified user by creating
	 * a solution-favorite record.
	 * 
	 * @param sf
	 *               solution favorite model
	 * @return Complete object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionFavorite createSolutionFavorite(MLPSolutionFavorite sf) throws RestClientResponseException;

	/**
	 * Un-marks the specified solution as a favorite of the specified user by
	 * deleting a solution-favorite record.
	 * 
	 * @param sf
	 *               solution favorite model
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionFavorite(MLPSolutionFavorite sf) throws RestClientResponseException;

	/**
	 * Gets a page of user ratings for the specified solution.
	 * 
	 * @param solutionId
	 *                        Instance ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution ratings, which may be empty
	 */
	RestPageResponse<MLPSolutionRating> getSolutionRatings(String solutionId, RestPageRequest pageRequest);

	/**
	 * Gets a rating for the specified solution and user.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param userId
	 *                       User ID
	 * @return Solution rating
	 */
	MLPSolutionRating getSolutionRating(String solutionId, String userId);

	/**
	 * Creates a new rating for the specified solution and user.
	 * 
	 * @param rating
	 *                   Instance to save
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionRating createSolutionRating(MLPSolutionRating rating) throws RestClientResponseException;

	/**
	 * Updates a solution rating.
	 * 
	 * @param rating
	 *                   Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolutionRating(MLPSolutionRating rating) throws RestClientResponseException;

	/**
	 * Deletes the solution rating for the specified IDs.
	 * 
	 * @param rating
	 *                   Instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionRating(MLPSolutionRating rating) throws RestClientResponseException;

	/**
	 * Gets the count of notifications.
	 * 
	 * @return Count of notifications.
	 */
	long getNotificationCount();

	/**
	 * Gets a page of notifications.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of notifications, which may be empty
	 */
	RestPageResponse<MLPNotification> getNotifications(RestPageRequest pageRequest);

	/**
	 * Creates a new notification and generates an ID if needed.
	 * 
	 * @param notification
	 *                         Notification data. If the ID field is null a new
	 *                         value is generated; otherwise the ID value is used if
	 *                         valid and not already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPNotification createNotification(MLPNotification notification) throws RestClientResponseException;

	/**
	 * Updates an existing notification with the supplied data.
	 * 
	 * @param notification
	 *                         Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateNotification(MLPNotification notification) throws RestClientResponseException;

	/**
	 * Deletes the notification with the specified ID. A notification can be deleted
	 * if is not associated with any user recipients; if associations remain the
	 * delete will fail.
	 * 
	 * @param notificationId
	 *                           ID of instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteNotification(String notificationId) throws RestClientResponseException;

	/**
	 * Gets the count of unread active notifications for the specified user.
	 * "Active" means the current date/time falls within the notification's begin
	 * and end timestamps.
	 * 
	 * @param userId
	 *                   User ID
	 * @return Count of unread notifications.
	 */
	long getUserUnreadNotificationCount(String userId);

	/**
	 * Gets a page of active notifications for the specified user, both viewed and
	 * unviewed. "Active" means the current date/time falls within the
	 * notification's begin and end timestamps.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of user notifications, which may be empty
	 */
	RestPageResponse<MLPUserNotification> getUserNotifications(String userId, RestPageRequest pageRequest);

	/**
	 * Adds the specified user as a recipient of the specified notification.
	 * 
	 * @param notificationId
	 *                           notification ID
	 * @param userId
	 *                           user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addUserToNotification(String notificationId, String userId) throws RestClientResponseException;

	/**
	 * Drops the specified user as a recipient of the specified notification.
	 * 
	 * @param notificationId
	 *                           notification ID
	 * @param userId
	 *                           user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropUserFromNotification(String notificationId, String userId) throws RestClientResponseException;

	/**
	 * Records that the user viewed the notification by storing the current date and
	 * time.
	 * 
	 * @param notificationId
	 *                           notification ID
	 * @param userId
	 *                           user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void setUserViewedNotification(String notificationId, String userId) throws RestClientResponseException;

	/**
	 * Changes the user's password to the new value if the user exists, is active,
	 * and the old password matches.
	 * 
	 * @param user
	 *                          User object
	 * @param changeRequest
	 *                          Old and new passwords.
	 * @throws RestClientResponseException
	 *                                         If the old password does not match or
	 *                                         the user is not active. Error message
	 *                                         is in the response body.
	 */
	void updatePassword(MLPUser user, MLPPasswordChangeRequest changeRequest) throws RestClientResponseException;

	/**
	 * Gets a page of solution deployments for the specified user.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution deployments, which may be empty
	 */
	RestPageResponse<MLPSolutionDeployment> getUserDeployments(String userId, RestPageRequest pageRequest);

	/**
	 * Gets a page of deployments for the specified solution revision.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution deployments, which may be empty
	 */
	RestPageResponse<MLPSolutionDeployment> getSolutionDeployments(String solutionId, String revisionId,
			RestPageRequest pageRequest);

	/**
	 * Gets a page of deployments for the specified solution revision and user.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution deployments, which may be empty
	 */
	RestPageResponse<MLPSolutionDeployment> getUserSolutionDeployments(String solutionId, String revisionId,
			String userId, RestPageRequest pageRequest);

	/**
	 * Creates a new solution deployment record.
	 * 
	 * @param deployment
	 *                       Instance to save
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionDeployment createSolutionDeployment(MLPSolutionDeployment deployment) throws RestClientResponseException;

	/**
	 * Updates an existing solution deployment record.
	 * 
	 * @param deployment
	 *                       Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolutionDeployment(MLPSolutionDeployment deployment) throws RestClientResponseException;

	/**
	 * Deletes the specified solution deployment record.
	 * 
	 * @param deployment
	 *                       Instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionDeployment(MLPSolutionDeployment deployment) throws RestClientResponseException;

	/**
	 * Gets a page of site configuration objects.
	 * 
	 * @param pageRequest
	 *                        Page request
	 * @return Page of site configurations, which may be empty
	 */
	RestPageResponse<MLPSiteConfig> getSiteConfigs(RestPageRequest pageRequest);

	/**
	 * Gets the site configuration object for the specified key.
	 * 
	 * @param configKey
	 *                      Config key
	 * @return Site configuration
	 */
	MLPSiteConfig getSiteConfig(String configKey);

	/**
	 * Creates a new site configuration object.
	 * 
	 * @param config
	 *                   Instance to save
	 * @return Complete object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSiteConfig createSiteConfig(MLPSiteConfig config) throws RestClientResponseException;

	/**
	 * Updates an existing site configuration object with the supplied data.
	 * 
	 * @param config
	 *                   Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSiteConfig(MLPSiteConfig config) throws RestClientResponseException;

	/**
	 * Deletes a site configuration object.
	 * 
	 * @param configKey
	 *                      key of instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSiteConfig(String configKey) throws RestClientResponseException;

	/**
	 * Gets a page of site content objects.
	 * 
	 * @param pageRequest
	 *                        Page request
	 * @return Page of site contents, which may be empty
	 */
	RestPageResponse<MLPSiteContent> getSiteContents(RestPageRequest pageRequest);

	/**
	 * Gets the site content object for the specified key.
	 * 
	 * @param contentKey
	 *                       Content key
	 * @return Site content
	 */
	MLPSiteContent getSiteContent(String contentKey);

	/**
	 * Creates a new site content object.
	 * 
	 * @param content
	 *                    Instance to save
	 * @return Complete object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSiteContent createSiteContent(MLPSiteContent content) throws RestClientResponseException;

	/**
	 * Updates a site content entry.
	 * 
	 * @param content
	 *                    Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSiteContent(MLPSiteContent content) throws RestClientResponseException;

	/**
	 * Updates the site content object with the specified key.
	 * 
	 * @param contentKey
	 *                       key of instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSiteContent(String contentKey) throws RestClientResponseException;

	/**
	 * Gets count of threads.
	 * 
	 * @return Count of threads.
	 */
	long getThreadCount();

	/**
	 * Gets a page of threads.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of threads, which may be empty
	 */
	RestPageResponse<MLPThread> getThreads(RestPageRequest pageRequest);

	/**
	 * Gets the count of threads for the specified solution and revision IDs.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @return Count of threads
	 */
	long getSolutionRevisionThreadCount(String solutionId, String revisionId);

	/**
	 * Gets a page of threads for the specified solution and revision IDs.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of threads, which may be empty
	 */
	RestPageResponse<MLPThread> getSolutionRevisionThreads(String solutionId, String revisionId,
			RestPageRequest pageRequest);

	/**
	 * Gets the thread with the specified ID.
	 * 
	 * @param threadId
	 *                     thread ID
	 * @return Thread object
	 */
	MLPThread getThread(String threadId);

	/**
	 * Creates a new thread and generates an ID if needed.
	 * 
	 * @param thread
	 *                   Thread data. If the ID field is null a new value is
	 *                   generated; otherwise the ID value is used if valid and not
	 *                   already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPThread createThread(MLPThread thread) throws RestClientResponseException;

	/**
	 * Updates an existing thread with the supplied data.
	 * 
	 * @param thread
	 *                   Thread data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateThread(MLPThread thread) throws RestClientResponseException;

	/**
	 * Deletes the thread with the specified ID. Cascades the delete to comment
	 * associations.
	 * 
	 * @param threadId
	 *                     thread ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteThread(String threadId) throws RestClientResponseException;

	/**
	 * Gets the count of comments in the specified thread.
	 * 
	 * @param threadId
	 *                     Thread ID
	 * @return Count of comments
	 */
	long getThreadCommentCount(String threadId);

	/**
	 * Gets one page of a thread of comments.
	 * 
	 * @param threadId
	 *                        Thread ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return One page of comments in the thread, sorted as specified.
	 */
	RestPageResponse<MLPComment> getThreadComments(String threadId, RestPageRequest pageRequest);

	/**
	 * Gets the count of comments in all threads for the specified solution and
	 * revision IDs.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @return Number of comments
	 */
	long getSolutionRevisionCommentCount(String solutionId, String revisionId);

	/**
	 * Gets one page of comments for the specified solution and revision IDs, which
	 * may include multiple threads.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return A page of comments, which may be empty
	 */
	RestPageResponse<MLPComment> getSolutionRevisionComments(String solutionId, String revisionId,
			RestPageRequest pageRequest);

	/**
	 * Gets the comment with the specified ID.
	 * 
	 * @param threadId
	 *                      Thread ID
	 * @param commentId
	 *                      comment ID
	 * @return Comment object
	 */
	MLPComment getComment(String threadId, String commentId);

	/**
	 * Creates a new comment and generates an ID if needed.
	 * 
	 * @param comment
	 *                    Comment data. If the ID field is null a new value is
	 *                    generated; otherwise the ID value is used if valid and not
	 *                    already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPComment createComment(MLPComment comment) throws RestClientResponseException;

	/**
	 * Updates an existing comment with the supplied data.
	 * 
	 * @param comment
	 *                    Comment data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateComment(MLPComment comment) throws RestClientResponseException;

	/**
	 * Deletes the comment with the specified ID.
	 * 
	 * @param threadId
	 *                      Thread ID
	 * @param commentId
	 *                      comment ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteComment(String threadId, String commentId) throws RestClientResponseException;

	/**
	 * Gets the task step result with the specified ID.
	 * 
	 * @param taskStepResultId
	 *                             Task step result ID
	 * @return MLPTaskStepResult
	 */
	MLPTaskStepResult getTaskStepResult(long taskStepResultId);

	/**
	 * Gets all step results associated with the specified task ID.
	 * 
	 * @param taskId
	 *                   Task ID
	 * @return List of task step results, which may be empty
	 * 
	 */
	List<MLPTaskStepResult> getTaskStepResults(long taskId);

	/**
	 * Searches for step results with attributes matching the values specified as
	 * query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: taskId,
	 *                            trackingId, taskCode, solutionId, revisionId,
	 *                            artifactId, userId, statusCode, name.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of task step results, which may be empty
	 */
	RestPageResponse<MLPTaskStepResult> searchTaskStepResults(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Creates a new task step result with a generated ID.
	 * 
	 * @param stepResult
	 *                       Step Result data. The ID field should be null; the
	 *                       taskId field must be valid.
	 * @return Complete object, with generated information including ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPTaskStepResult createTaskStepResult(MLPTaskStepResult stepResult) throws RestClientResponseException;

	/**
	 * Updates an existing task step result with the supplied data.
	 * 
	 * @param stepResult
	 *                       Step Result data. The stepResultId and taskId fields
	 *                       must be valid.
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateTaskStepResult(MLPTaskStepResult stepResult) throws RestClientResponseException;

	/**
	 * Deletes the task step result with the specified ID.
	 * 
	 * @param stepResultId
	 *                         stepResult ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteTaskStepResult(long stepResultId) throws RestClientResponseException;

	/**
	 * Creates a user notification preference.
	 * 
	 * @param usrNotifPref
	 *                         user notification preference data
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPUserNotifPref createUserNotificationPreference(MLPUserNotifPref usrNotifPref) throws RestClientResponseException;

	/**
	 * Updates a user notification preference.
	 * 
	 * @param usrNotifPref
	 *                         user notification preference data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateUserNotificationPreference(MLPUserNotifPref usrNotifPref) throws RestClientResponseException;

	/**
	 * Deletes a user notification preference.
	 * 
	 * @param userNotifPrefId
	 *                            user notification preference ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteUserNotificationPreference(Long userNotifPrefId) throws RestClientResponseException;

	/**
	 * Gets a list of user notification preferences for the specified user.
	 * 
	 * @param userId
	 *                   User ID
	 * @return List of user notification preferences, which may be empty
	 */
	List<MLPUserNotifPref> getUserNotificationPreferences(String userId);

	/**
	 * Gets the user notification preference with the specified ID.
	 * 
	 * @param usrNotifPrefId
	 *                           user notification preference ID
	 * @return User Notification Preference object
	 */
	MLPUserNotifPref getUserNotificationPreference(Long usrNotifPrefId);

	/**
	 * Gets the child solution IDs in the specified composite (parent) solution.
	 * 
	 * @param parentId
	 *                     parent solution ID
	 * @return List of child solution IDs, which may be empty
	 */
	List<String> getCompositeSolutionMembers(String parentId);

	/**
	 * Adds the specified member (child) to the specified composite solution
	 * (parent).
	 * 
	 * @param parentId
	 *                     parent solution ID
	 * @param childId
	 *                     child solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addCompositeSolutionMember(String parentId, String childId) throws RestClientResponseException;

	/**
	 * Removes the specified member (child) from the specified composite solution
	 * (parent).
	 * 
	 * @param parentId
	 *                     parent solution ID
	 * @param childId
	 *                     child solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropCompositeSolutionMember(String parentId, String childId) throws RestClientResponseException;

	/**
	 * Gets the description for the specified revision and catalog IDs.
	 * 
	 * @param revisionId
	 *                       revision ID
	 * @param catalogId
	 *                       catalog ID
	 * @return MLPRevisionDescription
	 */
	MLPRevCatDescription getRevCatDescription(String revisionId, String catalogId);

	/**
	 * Creates a description for a revision and catalog.
	 * 
	 * @param revCatDesc
	 *                       Revision description to create
	 * @return Description object for the specified revision and catalog
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRevCatDescription createRevCatDescription(MLPRevCatDescription revCatDesc) throws RestClientResponseException;

	/**
	 * Updates an existing description for a revision and catalog.
	 * 
	 * @param revCatDesc
	 *                       Revision description to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRevCatDescription(MLPRevCatDescription revCatDesc) throws RestClientResponseException;

	/**
	 * Deletes a description for a revision and catalog.
	 * 
	 * @param revisionId
	 *                       revision ID
	 * @param catalogId
	 *                       catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRevCatDescription(String revisionId, String catalogId) throws RestClientResponseException;

	/**
	 * Gets the document object with the specified ID.
	 * 
	 * @param documentId
	 *                       Document ID
	 * @return Document object
	 */
	MLPDocument getDocument(String documentId);

	/**
	 * Creates a new document object and generates an ID if needed.
	 * 
	 * @param document
	 *                     Document data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPDocument createDocument(MLPDocument document) throws RestClientResponseException;

	/**
	 * Updates an existing document object with the supplied data.
	 * 
	 * 
	 * @param document
	 *                     Document data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateDocument(MLPDocument document) throws RestClientResponseException;

	/**
	 * Deletes a document object. A document can be deleted if is not associated
	 * with any solution revisions; if associations remain the delete will fail.
	 * 
	 * @param documentId
	 *                       Document ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteDocument(String documentId) throws RestClientResponseException;

	/**
	 * Gets the documents for a solution revision in the specified catalog.
	 * 
	 * @param revisionId
	 *                       revision ID
	 * @param catalogId
	 *                       Catalog ID
	 * @return List of documents, which may be empty
	 */
	List<MLPDocument> getRevisionCatalogDocuments(String revisionId, String catalogId);

	/**
	 * Adds a user document to a solution revision for the specified catalog.
	 * 
	 * @param revisionId
	 *                       Revision ID
	 * @param catalogId
	 *                       Catalog ID
	 * @param documentId
	 *                       Document Id
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addRevisionCatalogDocument(String revisionId, String catalogId, String documentId)
			throws RestClientResponseException;

	/**
	 * Removes a user document from a solution revision for the specified catalog.
	 * 
	 * @param revisionId
	 *                       Revision ID
	 * @param catalogId
	 *                       Catalog ID
	 * @param documentId
	 *                       Document Id
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropRevisionCatalogDocument(String revisionId, String catalogId, String documentId)
			throws RestClientResponseException;

	/**
	 * Gets the publish request with the specified ID.
	 * 
	 * @param requestId
	 *                      Publish request ID
	 * @return MLPPublishRequest, null if not found
	 */
	MLPPublishRequest getPublishRequest(long requestId);

	/**
	 * Gets a page of publish requests.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of publish requests, which may be empty
	 */
	RestPageResponse<MLPPublishRequest> getPublishRequests(RestPageRequest pageRequest);

	/**
	 * Searches for publish requests with attributes matching the values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names:
	 *                            solutionId, revisionId, requestUserId,
	 *                            reviewUserId, statusCode.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of publish requests, which may be empty
	 */
	RestPageResponse<MLPPublishRequest> searchPublishRequests(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Convenience method that checks for a pending publish request.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * 
	 * @return True if one is found, else false.
	 */
	boolean isPublishRequestPending(String solutionId, String revisionId);

	/**
	 * Creates a new publish request with a generated ID.
	 * 
	 * @param publishRequest
	 *                           result Publish Request data.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPPublishRequest createPublishRequest(MLPPublishRequest publishRequest) throws RestClientResponseException;

	/**
	 * Updates an existing publish request with the supplied data.
	 * 
	 * @param publishRequest
	 *                           Publish Request data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updatePublishRequest(MLPPublishRequest publishRequest) throws RestClientResponseException;

	/**
	 * Deletes the publish request with the specified ID.
	 * 
	 * @param publishRequestId
	 *                             publishRequest ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deletePublishRequest(long publishRequestId) throws RestClientResponseException;

	/**
	 * Adds the specified tag to the specified user. Creates the tag if needed.
	 * 
	 * @param tag
	 *                   tag string
	 * @param userId
	 *                   User ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 * 
	 * @deprecated User tags provided a simplistic way for users to indicate their
	 *             interests, but will be removed in a future release.
	 */
	@Deprecated
	void addUserTag(String userId, String tag) throws RestClientResponseException;

	/**
	 * Removes the specified tag from the specified user.
	 * 
	 * @param tag
	 *                   tag string
	 * @param userId
	 *                   User ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 * @deprecated User tags provided a simplistic way for users to indicate their
	 *             interests, but will be removed in a future release.
	 */
	@Deprecated
	void dropUserTag(String userId, String tag) throws RestClientResponseException;

	/**
	 * Gets the image for the specified solution ID.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return Solution image; null if the solution ID is not known or no picture is
	 *         available.
	 */
	byte[] getSolutionPicture(String solutionId);

	/**
	 * Saves or updates a solution image.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param picture
	 *                       Image to save or update; send null to delete an
	 *                       existing image.
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void saveSolutionPicture(String solutionId, byte[] picture) throws RestClientResponseException;

	/**
	 * Gets a page of catalogs, optionally sorted.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of catalogs, which may be empty
	 */
	RestPageResponse<MLPCatalog> getCatalogs(RestPageRequest pageRequest);

	/**
	 * Gets the distinct set of publisher names from all catalogs.
	 * 
	 * @return List of names, which may be empty
	 */
	List<String> getCatalogPublishers();

	/**
	 * Searches for catalogs with attributes matching the non-null values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names:
	 *                            accessTypeCode, selfPublish, description, name,
	 *                            origin, publisher, url.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of catalogs, which may be empty
	 */
	RestPageResponse<MLPCatalog> searchCatalogs(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the catalog with the specified ID.
	 * 
	 * @param catalogId
	 *                      Catalog ID
	 * @return MLPCatalog; null if not found
	 */
	MLPCatalog getCatalog(String catalogId);

	/**
	 * Creates a new catalog and generates an ID if needed.
	 * 
	 * @param catalog
	 *                    Catalog data. If the ID field is null a new value is
	 *                    generated; otherwise the ID value is used if valid and not
	 *                    already known.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPCatalog createCatalog(MLPCatalog catalog) throws RestClientResponseException;

	/**
	 * Updates an existing catalog with the supplied data.
	 * 
	 * @param catalog
	 *                    Catalog data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateCatalog(MLPCatalog catalog) throws RestClientResponseException;

	/**
	 * Deletes a catalog. A catalog can be deleted if is not associated with any
	 * solutions; if associations remain the delete will fail.
	 * 
	 * @param catalogId
	 *                      ID of instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteCatalog(String catalogId) throws RestClientResponseException;

	/**
	 * Gets the count of solutions in the specified catalog.
	 * 
	 * @param catalogId
	 *                      Catalog ID
	 * @return Number of solutions for the specified catalog ID
	 */
	long getCatalogSolutionCount(String catalogId);

	/**
	 * Gets a page of solutions in the specified catalog(s).
	 * 
	 * @param catalogIds
	 *                        Catalog IDs, minimum 1.
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> getSolutionsInCatalogs(String[] catalogIds, RestPageRequest pageRequest);

	/**
	 * Gets the catalogs where the specified solution is published.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return List of catalogs, which may be empty
	 */
	List<MLPCatalog> getSolutionCatalogs(String solutionId);

	/**
	 * Publishes the specified solution to the specified catalog.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param catalogId
	 *                       Catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addSolutionToCatalog(String solutionId, String catalogId) throws RestClientResponseException;

	/**
	 * Removes the specified solution from the specified catalog.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param catalogId
	 *                       Catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropSolutionFromCatalog(String solutionId, String catalogId) throws RestClientResponseException;

	/**
	 * Gets a task.
	 * 
	 * @param taskId
	 *                   Task ID
	 * @return MLPTask
	 */
	MLPTask getTask(long taskId);

	/**
	 * Gets a page of tasks.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of tasks, which may be empty
	 */
	RestPageResponse<MLPTask> getTasks(RestPageRequest pageRequest);

	/**
	 * Searches for tasks with attributes matching the non-null values specified as
	 * query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: taskId,
	 *                            trackingId, userId, statusCode, name.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of tasks, which may be empty
	 */
	RestPageResponse<MLPTask> searchTasks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Creates a new task with a generated ID.
	 * 
	 * @param task
	 *                 Task data. The ID field should be null.
	 * @return Complete object, with generated information including ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPTask createTask(MLPTask task) throws RestClientResponseException;

	/**
	 * Updates an existing task with the supplied data.
	 * 
	 * @param task
	 *                 Task data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateTask(MLPTask task) throws RestClientResponseException;

	/**
	 * Deletes the task with the specified ID. Cascades the delete to associated
	 * task step result items.
	 * 
	 * @param taskId
	 *                   task ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteTask(long taskId) throws RestClientResponseException;

	/**
	 * Gets the list of catalog IDs that are favorites of the specified user.
	 * 
	 * @param userId
	 *                   User ID
	 * @return List of catalog IDs, which may be empty
	 */
	List<String> getUserFavoriteCatalogIds(String userId);

	/**
	 * Marks the specified catalog as a favorite of the specified user.
	 * 
	 * @param userId
	 *                      user ID
	 * @param catalogId
	 *                      catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addUserFavoriteCatalog(String userId, String catalogId) throws RestClientResponseException;

	/**
	 * Removes the specified catalog as a favorite of the specified user.
	 * 
	 * @param userId
	 *                      user ID
	 * @param catalogId
	 *                      catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropUserFavoriteCatalog(String userId, String catalogId) throws RestClientResponseException;

	/**
	 * Gets a page of workbench projects.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of workbench projects, which may be empty
	 */
	RestPageResponse<MLPProject> getProjects(RestPageRequest pageRequest);

	/**
	 * Searches for projects with attributes matching the non-null values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            active, userId, version, serviceStatus,
	 *                            repositoryUrl.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of workbench projects, which may be empty
	 */
	RestPageResponse<MLPProject> searchProjects(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the workbench project record with the specified ID.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return Workbench project object
	 */
	MLPProject getProject(String projectId);

	/**
	 * Creates a new project and generates an ID if needed.
	 * 
	 * @param project
	 *                    Project data. If the ID field is null a new value is
	 *                    generated; otherwise the ID value is used if valid and not
	 *                    already known.
	 * @return Complete object, with generated information such as ID
	 */
	MLPProject createProject(MLPProject project);

	/**
	 * Updates an existing project with the supplied data.
	 * 
	 * @param project
	 *                    Project data
	 */
	void updateProject(MLPProject project);

	/**
	 * Deletes the project with the specified ID. Cascades the delete; e.g., removes
	 * the association with any notebooks, pipelines, users, etc. Answers bad
	 * request if the ID is not known.
	 * 
	 * @param projectId
	 *                      project ID
	 */
	void deleteProject(String projectId);

	/**
	 * Maps the specified workbench notebook to the specified project.
	 * 
	 * @param projectId
	 *                       Project ID
	 * @param notebookId
	 *                       Notebook ID
	 */
	void addProjectNotebook(String projectId, String notebookId);

	/**
	 * Unmaps the specified workbench notebook from the specified project.
	 * 
	 * @param projectId
	 *                       Project ID
	 * @param notebookId
	 *                       Notebook ID
	 */
	void dropProjectNotebook(String projectId, String notebookId);

	/**
	 * Maps the specified workbench pipeline to the specified project.
	 * 
	 * @param projectId
	 *                       Project ID
	 * @param pipelineId
	 *                       Pipeline ID
	 */
	void addProjectPipeline(String projectId, String pipelineId);

	/**
	 * Unmaps the specified workbench pipeline from the specified project.
	 * 
	 * @param projectId
	 *                       Project ID
	 * @param pipelineId
	 *                       Pipeline ID
	 */
	void dropProjectPipeline(String projectId, String pipelineId);

	/**
	 * Gets the workbench notebooks mapped to the specified project ID.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return List of notebooks, which may be empty
	 */
	List<MLPNotebook> getProjectNotebooks(String projectId);

	/**
	 * Gets the workbench projects to which the specified notebook is mapped.
	 * 
	 * @param notebookId
	 *                       Notebook ID
	 * @return List of projects, which may be empty
	 */
	List<MLPProject> getNotebookProjects(String notebookId);

	/**
	 * Gets the workbench pipelines mapped to the specified project ID.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return List of pipelines, which may be empty
	 */
	List<MLPPipeline> getProjectPipelines(String projectId);

	/**
	 * Gets the workbench projects to which the specified pipeline is mapped.
	 * 
	 * @param pipelineId
	 *                       Pipeline ID
	 * @return List of projects, which may be empty
	 */
	List<MLPProject> getPipelineProjects(String pipelineId);

	/**
	 * Gets a page of workbench notebooks.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of workbench notebooks, which may be empty
	 */
	RestPageResponse<MLPNotebook> getNotebooks(RestPageRequest pageRequest);

	/**
	 * Searches for notebooks with attributes matching the non-null values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            active, userId, version, serviceStatus,
	 *                            repositoryUrl, serviceUrl.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of workbench notebooks, which may be empty
	 */
	RestPageResponse<MLPNotebook> searchNotebooks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the workbench notebook record with the specified ID.
	 * 
	 * @param notebookId
	 *                       Notebook ID
	 * @return Workbench notebook object
	 */
	MLPNotebook getNotebook(String notebookId);

	/**
	 * Creates a new notebook and generates an ID if needed.
	 * 
	 * @param notebook
	 *                     Notebook data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known.
	 * @return Complete object, with generated information such as ID
	 */
	MLPNotebook createNotebook(MLPNotebook notebook);

	/**
	 * Updates an existing notebook with the supplied data.
	 * 
	 * @param notebook
	 *                     Notebook data
	 */
	void updateNotebook(MLPNotebook notebook);

	/**
	 * Deletes the notebook with the specified ID. Cascades the delete; e.g.,
	 * removes the association with any projects, users, etc. Answers bad request if
	 * the ID is not known.
	 * 
	 * @param notebookId
	 *                       notebook ID
	 */
	void deleteNotebook(String notebookId);

	/**
	 * Gets a page of workbench pipelines.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of workbench pipelines, which may be empty
	 */
	RestPageResponse<MLPPipeline> getPipelines(RestPageRequest pageRequest);

	/**
	 * Searches for pipelines with attributes matching the non-null values specified
	 * as query parameters.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            active, userId, version, serviceStatus,
	 *                            repositoryUrl, serviceUrl.
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of workbench pipelines, which may be empty
	 */
	RestPageResponse<MLPPipeline> searchPipelines(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the workbench pipeline record with the specified ID.
	 * 
	 * @param pipelineId
	 *                       Pipeline ID
	 * @return Workbench pipeline object
	 */
	MLPPipeline getPipeline(String pipelineId);

	/**
	 * Creates a new pipeline and generates an ID if needed.
	 * 
	 * @param pipeline
	 *                     Pipeline data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known.
	 * @return Complete object, with generated information such as ID
	 */
	MLPPipeline createPipeline(MLPPipeline pipeline);

	/**
	 * Updates an existing pipeline with the supplied data.
	 * 
	 * @param pipeline
	 *                     Pipeline data
	 */
	void updatePipeline(MLPPipeline pipeline);

	/**
	 * Deletes the pipeline with the specified ID. Cascades the delete; e.g.,
	 * removes the association with any projects, users, etc. Answers bad request if
	 * the ID is not known.
	 * 
	 * @param pipelineId
	 *                       pipeline ID
	 */
	void deletePipeline(String pipelineId);

	/**
	 * Gets the list of catalog IDs readable by the specified peer. The catalogs
	 * must have restricted access-type codes.
	 * 
	 * @param peerId
	 *                   Peer ID
	 * @return List of catalog IDs, which may be empty
	 */
	List<String> getPeerAccessCatalogIds(String peerId);

	/**
	 * Gets the peers that were granted access to the specified catalog. The catalog
	 * must have a restricted access-type code.
	 * 
	 * @param catalogId
	 *                      Catalog ID
	 * @return List of peers, which may be empty
	 * @throws RestClientResponseException
	 *                                         Error message is in the response
	 *                                         body; is thrown if the catalog is not
	 *                                         restricted
	 */
	List<MLPPeer> getCatalogAccessPeers(String catalogId) throws RestClientResponseException;

	/**
	 * Add read access to the specified catalog for the specified peer. The catalog
	 * must have a restricted access-type code.
	 * 
	 * @param peerId
	 *                      peer ID
	 * @param catalogId
	 *                      catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addPeerAccessCatalog(String peerId, String catalogId) throws RestClientResponseException;

	/**
	 * Removes read access to the specified catalog for the specified peer. The
	 * catalog must have a restricted access-type code.
	 * 
	 * @param peerId
	 *                      peer ID
	 * @param catalogId
	 *                      catalog ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropPeerAccessCatalog(String peerId, String catalogId) throws RestClientResponseException;

	/**
	 * Checks if the specified peer can read the specified catalog. Any of the
	 * following conditions grant read permission:
	 * <OL>
	 * <LI>Catalog has access-type code public</LI>
	 * <LI>The peer has been granted access to the catalog</LI>
	 * </OL>
	 * 
	 * @param peerId
	 *                      peer ID
	 * @param catalogId
	 *                      catalog ID
	 * @return true if the peer can access the catalog, otherwise false
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	boolean isPeerAccessToCatalog(String peerId, String catalogId) throws RestClientResponseException;

	/**
	 * Checks if the specified peer can read the specified solution. Any of the
	 * following conditions grant read permission:
	 * <OL>
	 * <LI>Solution is in a public catalog</LI>
	 * <LI>Solution is in a catalog to which the peer has been granted access</LI>
	 * </OL>
	 * 
	 * @param peerId
	 *                       peer ID
	 * @param solutionId
	 *                       solution ID
	 * @return true if the peer can read the solution, otherwise false
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	boolean isPeerAccessToSolution(String peerId, String solutionId) throws RestClientResponseException;

	/**
	 * Gets the users who were granted write access to the specified solution by
	 * sharing; this does not include the creator of the solution.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return List of users, which may be empty
	 */
	List<MLPUser> getSolutionAccessUsers(String solutionId);

	/**
	 * Gets a page of solutions for which the user has write permission but is not
	 * the owner; i.e., extra access has been granted by the solution owner.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest);

	/**
	 * Checks if the specified user can read the specified solution. Any of the
	 * following conditions grant read permission:
	 * <OL>
	 * <LI>Solution is in a catalog</LI>
	 * <LI>User is the creator</LI>
	 * <LI>User has been granted access</LI>
	 * </OL>
	 * 
	 * @param userId
	 *                       User ID
	 * @param solutionId
	 *                       Solution ID
	 * @return true if the user can read the solution, otherwise false
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	boolean isUserAccessToSolution(String userId, String solutionId) throws RestClientResponseException;

	/**
	 * Grants write permission to the specified solution for the specified user.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param userId
	 *                       user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addSolutionUserAccess(String solutionId, String userId) throws RestClientResponseException;

	/**
	 * Removes write permission from the specified solution for the specified user.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param userId
	 *                       user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropSolutionUserAccess(String solutionId, String userId) throws RestClientResponseException;

	/**
	 * Gets a page of license profile templates.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of license profile templates, which may be empty
	 */
	RestPageResponse<MLPLicenseProfileTemplate> getLicenseProfileTemplates(RestPageRequest pageRequest);

	/**
	 * Gets the license profile template with the specified ID.
	 * 
	 * @param templateId
	 *                       License profile template ID
	 * @return license profile template object
	 */
	MLPLicenseProfileTemplate getLicenseProfileTemplate(long templateId);

	/**
	 * Creates a new license profile template. Generates a new ID.
	 * 
	 * @param template
	 *                     License profile template
	 * @return Complete object
	 */
	MLPLicenseProfileTemplate createLicenseProfileTemplate(MLPLicenseProfileTemplate template);

	/**
	 * Updates an existing license profile template with the supplied data. Answers
	 * bad request if the ID is not known.
	 * 
	 * @param template
	 *                     License profile template
	 */
	void updateLicenseProfileTemplate(MLPLicenseProfileTemplate template);

	/**
	 * Deletes the license profile template with the specified ID. Answers bad
	 * request if the ID is not known.
	 * 
	 * @param templateId
	 *                       license profile template ID
	 */
	void deleteLicenseProfileTemplate(long templateId);

	/**
	 * Gets all roles assigned to the specified catalog ID.
	 * 
	 * @param catalogId
	 *                      catalog ID
	 * @return List of roles, which may be empty
	 */
	List<MLPRole> getCatalogRoles(String catalogId);

	/**
	 * Adds the specified role to the specified catalog's roles.
	 * 
	 * @param catalogId
	 *                      catalog ID
	 * @param roleId
	 *                      role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addCatalogRole(String catalogId, String roleId) throws RestClientResponseException;

	/**
	 * Assigns the specified roles to the specified catalog after dropping any
	 * existing role assignments.
	 * 
	 * @param catalogId
	 *                      catalog ID
	 * @param roleIds
	 *                      List of role IDs
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateCatalogRoles(String catalogId, List<String> roleIds) throws RestClientResponseException;

	/**
	 * Removes the specified role from the specified catalog's roles.
	 * 
	 * @param catalogId
	 *                      catalog ID
	 * @param roleId
	 *                      role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropCatalogRole(String catalogId, String roleId) throws RestClientResponseException;

	/**
	 * Adds the specified role to every specified catalog's roles.
	 * 
	 * @param catalogIds
	 *                       List of catalog IDs
	 * @param roleId
	 *                       role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addCatalogsInRole(List<String> catalogIds, String roleId) throws RestClientResponseException;

	/**
	 * Removes the specified role from every specified catalog's roles.
	 * 
	 * @param catalogIds
	 *                       List of catalog IDs
	 * @param roleId
	 *                       role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropCatalogsInRole(List<String> catalogIds, String roleId) throws RestClientResponseException;

	/**
	 * Gets count of catalogs with the specified role.
	 * 
	 * @param roleId
	 *                   role ID
	 * @return Count of catalogs mapped to that role
	 */
	long getRoleCatalogsCount(String roleId);

	/**
	 * Gets a page of catalogs assigned to the specified role.
	 * 
	 * @param roleId
	 *                        role ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of catalogs, which may be empty
	 */
	RestPageResponse<MLPCatalog> getRoleCatalogs(String roleId, RestPageRequest pageRequest);

	/**
	 * Gets a page of catalogs accessible to the specified user, which includes
	 * public catalogs and restricted catalogs via catalog-role and user-role
	 * mappings.
	 * 
	 * @param userId
	 *                        user ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of catalogs, which may be empty
	 */
	RestPageResponse<MLPCatalog> getUserAccessCatalogs(String userId, RestPageRequest pageRequest);

}
