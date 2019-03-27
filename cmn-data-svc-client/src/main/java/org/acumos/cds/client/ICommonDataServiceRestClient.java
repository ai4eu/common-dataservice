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

import java.util.List;
import java.util.Map;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPRtuReference;
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
	 * Checks the health of the server.
	 * 
	 * @return Object with health string
	 */
	SuccessTransport getHealth();

	/**
	 * Gets the version of the server.
	 * 
	 * @return Object with version string
	 */
	SuccessTransport getVersion();

	/**
	 * Gets the list of code-name value-set names.
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
	 * Gets a page of solutions with a name field that contains the specified
	 * string. This may be slow because it requires table scans.
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
	 * Gets a page of solutions with exact matches on the specified fields, either
	 * as a conjunction ("and", all must match) or a disjunction ("or", any must
	 * match).
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
	 * @return Page of solution objects
	 */
	RestPageResponse<MLPSolution> searchSolutions(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets a page of solutions that are tagged with the specified string.
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
	 * slow because it requires table scans. Special-purpose method to support the
	 * dynamic search page on the portal marketplace.
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
	 * @param accessTypeCodes
	 *                                Limits match to solutions containing revisions
	 *                                with ANY of the specified values including
	 *                                null (which is different from the special-case
	 *                                4-character sequence "null"); ignored if null
	 *                                or empty
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
	 * 
	 * @param pageRequest
	 *                                Page index, page size and sort information;
	 *                                defaults to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String[] userIds, String[] accessTypeCodes, String[] modelTypeCodes, String[] tags,
			String[] authorKeywords, String[] publisherKeywords, RestPageRequest pageRequest);

	/**
	 * Gets a page of solutions that match every condition, with the caveat that any
	 * one of the keywords can match, and multiple free-text fields are searched.
	 * Other facets such as userId, model type code, etc. must match. This will be
	 * slow because it requires table scans.
	 * 
	 * @param keywords
	 *                            Keywords to find in the name, revision
	 *                            description, author, publisher and other field;
	 *                            ignored if null or empty
	 * @param active
	 *                            Solution active status; true for active, false for
	 *                            inactive
	 * @param userIds
	 *                            User IDs who created the solution; ignored if null
	 *                            or empty
	 * @param accessTypeCodes
	 *                            Access type codes; use four-letter sequence "null"
	 *                            to match a null value; ignored if null or empty
	 * @param modelTypeCodes
	 *                            Model type codes; use four-letter sequence "null"
	 *                            to match a null value; ignored if null or empty
	 * @param allTags
	 *                            Solutions must have ALL tags in the supplied set;
	 *                            ignored if null or empty
	 * @param anyTags
	 *                            Solutions must have ANY tag in the supplied set
	 *                            (one or more); ignored if null or empty.
	 * @param catalogIds
	 *                            Solutions must be mapped to one of the specified
	 *                            catalogs; ignored if null or empty
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of solutions, which may be empty
	 */
	RestPageResponse<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] accessTypeCodes, String[] modelTypeCodes, String[] allTags, String[] anyTags, String[] catalogIds,
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
	 * Creates a solution.
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
	 * Updates a solution. Any tags in the entry will be created if needed.
	 * 
	 * @param solution
	 *                     Solution data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolution(MLPSolution solution) throws RestClientResponseException;

	/**
	 * A convenience method that increments the view count of a solution by 1.
	 * 
	 * This requires only one database access, instead of two to fetch the solution
	 * entity and save it again.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void incrementSolutionViewCount(String solutionId) throws RestClientResponseException;

	/**
	 * Deletes a solution. Cascades the delete to solution-revision records and
	 * related entities such as composite solutions, solution downloads, publish
	 * request and so on. The solution-revision in turn cascades the delete to
	 * artifacts and related records. Answers bad request if the ID is not known.
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolution(String solutionId) throws RestClientResponseException;

	/**
	 * Gets the solution revisions for the specified solution ID.
	 * 
	 * @param solutionId
	 *                       solution ID.
	 * @return List of Solution revision objects for the specified solution.
	 */
	List<MLPSolutionRevision> getSolutionRevisions(String solutionId);

	/**
	 * Gets the solution revisions for the specified solution IDs.
	 * 
	 * @param solutionIds
	 *                        solution IDs. Caveat: the number of possible entries
	 *                        in this list is constrained by client/server
	 *                        limitations on URL length.
	 * @return List of Solution revision objects for any of the specified solutions.
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
	 * @return List of Solution revision objects for the specified artifact.
	 */
	List<MLPSolutionRevision> getSolutionRevisionsForArtifact(String artifactId);

	/**
	 * Creates a solution revision.
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
	 * Updates a solution revision.
	 * 
	 * @param revision
	 *                     Solution revision data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolutionRevision(MLPSolutionRevision revision) throws RestClientResponseException;

	/**
	 * Deletes a solution revision. Cascades the delete to related records including
	 * artifacts. Answers bad request if the ID is not known.
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
	 * Gets the artifacts for a solution revision
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param revisionId
	 *                       revision ID
	 * @return List of MLPArtifact
	 */
	List<MLPArtifact> getSolutionRevisionArtifacts(String solutionId, String revisionId);

	/**
	 * Adds an artifact to a solution revision
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
	 * Removes an artifact from a solution revision
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
	 * @return Page of solution tag objects
	 */
	RestPageResponse<MLPTag> getTags(RestPageRequest pageRequest);

	/**
	 * Creates a solution tag.
	 * 
	 * @param tag
	 *                tag object
	 * @return Complete object which wraps the tag
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPTag createTag(MLPTag tag) throws RestClientResponseException;

	/**
	 * Deletes a solution tag. A tag can be deleted if is not associated with any
	 * other entities; if associations remain the delete will fail.
	 * 
	 * @param tag
	 *                tag object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteTag(MLPTag tag) throws RestClientResponseException;

	/**
	 * Gets the solution tags for the specified solution ID.
	 * 
	 * @param solutionId
	 *                       solution ID.
	 * @return List of Solution tag objects for the specified solution.
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
	 * @return Page of artifact objects.
	 */
	RestPageResponse<MLPArtifact> getArtifacts(RestPageRequest pageRequest);

	/**
	 * Returns artifacts with a name or description that contains the search term.
	 * 
	 * @param searchTerm
	 *                        String to find
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of artifact objects.
	 */
	RestPageResponse<MLPArtifact> findArtifactsBySearchTerm(String searchTerm, RestPageRequest pageRequest);

	/**
	 * Searches artifacts for exact matches.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names:
	 *                            artifactTypeCode, name, uri, version, userId
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of artifact objects.
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
	 * Creates a artifact.
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
	 * Updates an artifact.
	 * 
	 * @param artifact
	 *                     Artifact data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateArtifact(MLPArtifact artifact) throws RestClientResponseException;

	/**
	 * Deletes an artifact. Cascades the delete; e.g., removes the association with
	 * any solution revisions and other records. Answers bad request if the ID is
	 * not known.
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
	 * @return Page of objects.
	 */
	RestPageResponse<MLPUser> getUsers(RestPageRequest pageRequest);

	/**
	 * Returns users with a first, middle, last or login name that contains the
	 * search term.
	 * 
	 * @param searchTerm
	 *                        String to find
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of user objects.
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
	 * @return Page of user objects
	 */
	RestPageResponse<MLPUser> searchUsers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Checks credentials for the specified active user. Does NOT check the
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
	 * Checks API token for the specified active user.
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
	 * Checks verification credentials for the specified active user. This does NOT
	 * check the expiration date of the token, the client must do that as needed.
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
	 * Creates a user.
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
	 * Updates a user.
	 * 
	 * @param user
	 *                 User data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateUser(MLPUser user) throws RestClientResponseException;

	/**
	 * Deletes a user. Cascades the delete to login-provider, notification and role
	 * associations. If associations remain with artifacts such as solutions the
	 * delete will fail.
	 * 
	 * @param userId
	 *                   user ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteUser(String userId) throws RestClientResponseException;

	/**
	 * Gets the roles for the specified user ID.
	 * 
	 * @param userId
	 *                   user ID.
	 * @return List of Role objects for the specified user.
	 */
	List<MLPRole> getUserRoles(String userId);

	/**
	 * Adds the specified role to the specified user.
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
	 * Updates the user to have exactly the specified roles only; i.e., remove any
	 * roles not in the list.
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
	 * Removes the specified role from the specified user.
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
	 * Assigns the specified role to each user in the specified list.
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
	 * Removes the specified role from each user in the specified list.
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
	 * Gets the specified user login provider.
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
	 * Gets the user's login providers.
	 * 
	 * @param userId
	 *                   user ID
	 * @return List of user login providers
	 */
	List<MLPUserLoginProvider> getUserLoginProviders(String userId);

	/**
	 * Creates a user login provider.
	 * 
	 * @param provider
	 *                     data to populate new entry
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPUserLoginProvider createUserLoginProvider(MLPUserLoginProvider provider) throws RestClientResponseException;

	/**
	 * Updates a user login provider
	 * 
	 * @param provider
	 *                     data to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateUserLoginProvider(MLPUserLoginProvider provider) throws RestClientResponseException;

	/**
	 * Deletes a user login provider.
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
	 * Searches roles for exact matches.
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
	 * @return Page of role objects
	 */
	RestPageResponse<MLPRole> searchRoles(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets the roles.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of MLPRoles
	 */
	RestPageResponse<MLPRole> getRoles(RestPageRequest pageRequest);

	/**
	 * Gets the object with the specified ID.
	 * 
	 * @param roleId
	 *                   role ID
	 * @return instance with the specified ID; null if none exists.
	 */
	MLPRole getRole(String roleId);

	/**
	 * Writes the specified role.
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
	 * Updates the specified role.
	 * 
	 * @param role
	 *                 instance to save
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRole(MLPRole role) throws RestClientResponseException;

	/**
	 * Deletes a role. A role can be deleted if is not associated with any users.
	 * Cascades the delete to associated role functions.
	 * 
	 * @param roleId
	 *                   Role ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRole(String roleId) throws RestClientResponseException;

	/**
	 * Gets the role functions for the specified role
	 * 
	 * @param roleId
	 *                   role ID
	 * @return List of RoleFunctions;
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
	 * Creates the specified role function.
	 * 
	 * @param roleFunction
	 *                         instance to save
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRoleFunction createRoleFunction(MLPRoleFunction roleFunction) throws RestClientResponseException;

	/**
	 * Creates the specified role function.
	 * 
	 * @param roleFunction
	 *                         instance to save
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRoleFunction(MLPRoleFunction roleFunction) throws RestClientResponseException;

	/**
	 * Deletes a role function.
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
	 * @return Page of peer objects.
	 */
	RestPageResponse<MLPPeer> getPeers(RestPageRequest pageRequest);

	/**
	 * Searches peers for exact matches.
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
	 * @return Page of peer objects
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
	 * Creates a peer.
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
	 * Updates a peer.
	 * 
	 * @param user
	 *                 Peer data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updatePeer(MLPPeer user) throws RestClientResponseException;

	/**
	 * Deletes a peer. Cascades the delete to peer subscriptions. If other
	 * associations remain the delete will fail.
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
	 * @return List of peer objects
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
	 * Gets the artifact download details for the specified solution.
	 * 
	 * @param solutionId
	 *                        Instance ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution downloads
	 */
	RestPageResponse<MLPSolutionDownload> getSolutionDownloads(String solutionId, RestPageRequest pageRequest);

	/**
	 * Creates a solution-artifact download record.
	 * 
	 * @param download
	 *                     Instance to save
	 * @return Complete object.
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionDownload createSolutionDownload(MLPSolutionDownload download) throws RestClientResponseException;

	/**
	 * Deletes a solution-artifact download record.
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
	 * @return Page of solutions that are favorites of the user; might be empty.
	 */
	RestPageResponse<MLPSolution> getFavoriteSolutions(String userId, RestPageRequest pageRequest);

	/**
	 * Creates a solution favorite record; i.e., marks a solution as a favorite of a
	 * specified user
	 * 
	 * @param fs
	 *               favorite solution model
	 * @return Complete object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionFavorite createSolutionFavorite(MLPSolutionFavorite fs) throws RestClientResponseException;

	/**
	 * Deletes a solution favorite record; i.e., unmarks a solution as a favorite of
	 * a specified user
	 * 
	 * @param fs
	 *               favorite solution model
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionFavorite(MLPSolutionFavorite fs) throws RestClientResponseException;

	/**
	 * Gets the user ratings for the specified solution.
	 * 
	 * @param solutionId
	 *                        Instance ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution ratings
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
	 * Creates a solution rating.
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
	 * Deletes a solution rating.
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
	 * @return Page of objects.
	 */
	RestPageResponse<MLPNotification> getNotifications(RestPageRequest pageRequest);

	/**
	 * Creates a notification.
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
	 * Updates a notification.
	 * 
	 * @param notification
	 *                         Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateNotification(MLPNotification notification) throws RestClientResponseException;

	/**
	 * Deletes a notification. A notification can be deleted if is not associated
	 * with any user recipients; if associations remain the delete will fail.
	 * 
	 * @param notificationId
	 *                           ID of instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteNotification(String notificationId) throws RestClientResponseException;

	/**
	 * Gets the count of user notifications not yet viewed.
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
	 * @return Page of objects.
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
	 * Sets the indicator that the user has viewed the notification.
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
	 * Gets the users with access to the specified solution.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return List of users
	 */
	List<MLPUser> getSolutionAccessUsers(String solutionId);

	/**
	 * Gets a page of solutions accessible to the specified user.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solutions
	 */
	RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest);

	/**
	 * Grants access to the specified solution for the specified user.
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
	 * Removes access to the specified solution for the specified user.
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
	 * Updates the password for the specified active user.
	 * 
	 * @param user
	 *                          User object
	 * @param changeRequest
	 *                          Old and new passwords. Old password may be null, new
	 *                          password must not be present.
	 * @throws RestClientResponseException
	 *                                         If the old password does not match or
	 *                                         the user is not active. Error message
	 *                                         is in the response body.
	 */
	void updatePassword(MLPUser user, MLPPasswordChangeRequest changeRequest) throws RestClientResponseException;

	/**
	 * Gets a page of deployments for the specified user.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of solution deployments
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
	 * @return Page of solution deployments
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
	 * @return Page of solution deployments
	 */
	RestPageResponse<MLPSolutionDeployment> getUserSolutionDeployments(String solutionId, String revisionId,
			String userId, RestPageRequest pageRequest);

	/**
	 * Creates a solution deployment record.
	 * 
	 * @param deployment
	 *                       Instance to save
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSolutionDeployment createSolutionDeployment(MLPSolutionDeployment deployment) throws RestClientResponseException;

	/**
	 * Updates a solution deployment record.
	 * 
	 * @param deployment
	 *                       Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSolutionDeployment(MLPSolutionDeployment deployment) throws RestClientResponseException;

	/**
	 * Deletes a solution deployment record.
	 * 
	 * @param deployment
	 *                       Instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSolutionDeployment(MLPSolutionDeployment deployment) throws RestClientResponseException;

	/**
	 * Gets a page of site configuration entries.
	 * 
	 * @param pageRequest
	 *                        Page request
	 * @return Page of site configurations, possibly empty
	 */
	RestPageResponse<MLPSiteConfig> getSiteConfigs(RestPageRequest pageRequest);

	/**
	 * Gets one site configuration entry.
	 * 
	 * @param configKey
	 *                      Config key
	 * @return Site configuration
	 */
	MLPSiteConfig getSiteConfig(String configKey);

	/**
	 * Creates a site configuration entry.
	 * 
	 * @param config
	 *                   Instance to save
	 * @return Complete object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPSiteConfig createSiteConfig(MLPSiteConfig config) throws RestClientResponseException;

	/**
	 * Updates a site configuration entry.
	 * 
	 * @param config
	 *                   Instance to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateSiteConfig(MLPSiteConfig config) throws RestClientResponseException;

	/**
	 * Deletes a site configuration entry.
	 * 
	 * @param configKey
	 *                      key of instance to delete
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteSiteConfig(String configKey) throws RestClientResponseException;

	/**
	 * Gets a page of site content entries.
	 * 
	 * @param pageRequest
	 *                        Page request
	 * @return Page of site contents, possibly empty
	 */
	RestPageResponse<MLPSiteContent> getSiteContents(RestPageRequest pageRequest);

	/**
	 * Gets one site content entry.
	 * 
	 * @param contentKey
	 *                       Content key
	 * @return Site content
	 */
	MLPSiteContent getSiteContent(String contentKey);

	/**
	 * Creates a site content entry.
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
	 * Deletes a site content entry.
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
	 * @return Page of threads.
	 */
	RestPageResponse<MLPThread> getThreads(RestPageRequest pageRequest);

	/**
	 * Gets the count of threads for the specified solution and revision.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @return Count of threads
	 */
	long getSolutionRevisionThreadCount(String solutionId, String revisionId);

	/**
	 * Gets a page of threads for the specified solution and revision.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of threads.
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
	 * Creates a thread for comment.
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
	 * Updates a thread.
	 * 
	 * @param thread
	 *                   Thread data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateThread(MLPThread thread) throws RestClientResponseException;

	/**
	 * Deletes a thread. Cascades the delete to comment associations.
	 * 
	 * @param threadId
	 *                     thread ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteThread(String threadId) throws RestClientResponseException;

	/**
	 * Gets count of comments in a thread.
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
	 * Gets comment count for the specified solution and revision IDs, which may
	 * include multiple threads.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @return Number of comments for the specified IDs
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
	 * @return One page of comments for the specified IDs, sorted as specified.
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
	 * Creates a comment.
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
	 * Updates a comment.
	 * 
	 * @param comment
	 *                    Comment data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateComment(MLPComment comment) throws RestClientResponseException;

	/**
	 * Deletes a comment.
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
	 * Gets a task step result.
	 * 
	 * @param taskStepResultId
	 *                             Task step result ID
	 * @return MLPTaskStepResult
	 */
	MLPTaskStepResult getTaskStepResult(long taskStepResultId);

	/**
	 * Gets all step results for the specified task ID
	 * 
	 * @param taskId
	 *                   Task ID
	 * @return List of step results, which may be empty
	 * 
	 */
	List<MLPTaskStepResult> getTaskStepResults(long taskId);

	/**
	 * Gets a page of task step results that exactly match the search parameters.
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
	 * @return Page of step result objects
	 */
	RestPageResponse<MLPTaskStepResult> searchTaskStepResults(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Creates a task step result.
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
	 * Updates an existing task step result.
	 * 
	 * @param stepResult
	 *                       Step Result data. The stepResultId and taskId fields
	 *                       must be valid.
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateTaskStepResult(MLPTaskStepResult stepResult) throws RestClientResponseException;

	/**
	 * Deletes a task step result.
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
	 * @return List of user notification preferences for the specified user.
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
	 * Gets the member solution IDs in the specified composite solution.
	 * 
	 * @param parentId
	 *                     parent solution ID.
	 * @return List of child solution IDs
	 */
	List<String> getCompositeSolutionMembers(String parentId);

	/**
	 * Adds the specified member to the specified composite solution.
	 * 
	 * @param parentId
	 *                     parent solution ID.
	 * @param childId
	 *                     child solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addCompositeSolutionMember(String parentId, String childId) throws RestClientResponseException;

	/**
	 * Removes the specified member from the specified composite solution.
	 * 
	 * @param parentId
	 *                     parent solution ID.
	 * @param childId
	 *                     child solution ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropCompositeSolutionMember(String parentId, String childId) throws RestClientResponseException;

	/**
	 * Gets the description for a revision and access type.
	 * 
	 * @param revisionId
	 *                           revision ID
	 * @param accessTypeCode
	 *                           access type code
	 * @return MLPRevisionDescription
	 */
	MLPRevisionDescription getRevisionDescription(String revisionId, String accessTypeCode);

	/**
	 * Creates a description for a revision and access type.
	 * 
	 * @param description
	 *                        Revision description to create
	 * @return MLPRevisionDescription
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRevisionDescription createRevisionDescription(MLPRevisionDescription description)
			throws RestClientResponseException;

	/**
	 * Updates an existing description for a revision and access type.
	 * 
	 * @param description
	 *                        Revision description to update
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRevisionDescription(MLPRevisionDescription description) throws RestClientResponseException;

	/**
	 * Deletes a description for a revision and access type.
	 * 
	 * @param revisionId
	 *                           revision ID
	 * @param accessTypeCode
	 *                           access type code
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRevisionDescription(String revisionId, String accessTypeCode) throws RestClientResponseException;

	/**
	 * Gets the document with the specified ID. This is usually metadata about a
	 * user-supplied document stored in Nexus.
	 * 
	 * @param documentId
	 *                       document ID
	 * @return Document object
	 */
	MLPDocument getDocument(String documentId);

	/**
	 * Creates a document. This is usually metadata about a user-supplied document
	 * stored in Nexus.
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
	 * Updates a document. This is usually metadata about a user-supplied document
	 * stored in Nexus.
	 * 
	 * 
	 * @param document
	 *                     Document data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateDocument(MLPDocument document) throws RestClientResponseException;

	/**
	 * Deletes a document. An document can be deleted if is not associated with any
	 * solution revisions; if associations remain the delete will fail.
	 * 
	 * @param documentId
	 *                       document ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteDocument(String documentId) throws RestClientResponseException;

	/**
	 * Gets the documents for a solution revision at the specified access type.
	 * 
	 * @param revisionId
	 *                           revision ID
	 * @param accessTypeCode
	 *                           Access type code; e.g., "PB"
	 * @return List of MLPDocument
	 */
	List<MLPDocument> getSolutionRevisionDocuments(String revisionId, String accessTypeCode);

	/**
	 * Adds a user document to a solution revision at the specified access type.
	 * 
	 * @param revisionId
	 *                           Revision ID
	 * @param accessTypeCode
	 *                           Access type code; e.g., "PB"
	 * @param documentId
	 *                           Document Id
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addSolutionRevisionDocument(String revisionId, String accessTypeCode, String documentId)
			throws RestClientResponseException;

	/**
	 * Removes a user document from a solution revision at the specified access
	 * type.
	 * 
	 * @param revisionId
	 *                           Revision ID
	 * @param accessTypeCode
	 *                           Access type code; e.g., "PB"
	 * @param documentId
	 *                           Document Id
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropSolutionRevisionDocument(String revisionId, String accessTypeCode, String documentId)
			throws RestClientResponseException;

	/**
	 * Gets a publish request.
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
	 * @return Page of publish request objects.
	 */
	RestPageResponse<MLPPublishRequest> getPublishRequests(RestPageRequest pageRequest);

	/**
	 * Searches publish requests for exact matches.
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
	 * @return Page of publish request objects
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
	 * Creates a publish request.
	 * 
	 * @param publishRequest
	 *                           result Publish Request data.
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPPublishRequest createPublishRequest(MLPPublishRequest publishRequest) throws RestClientResponseException;

	/**
	 * Updates a publish request.
	 * 
	 * @param publishRequest
	 *                           Publish Request data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updatePublishRequest(MLPPublishRequest publishRequest) throws RestClientResponseException;

	/**
	 * Deletes a publish request.
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
	 */
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
	 */
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
	 * Gets a page of catalogs.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of objects.
	 */
	RestPageResponse<MLPCatalog> getCatalogs(RestPageRequest pageRequest);

	/**
	 * Searches catalog records for exact matches.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names:
	 *                            accessTypeCode, description, name, origin,
	 *                            publisher, url
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of Catalog objects
	 */
	RestPageResponse<MLPCatalog> searchCatalogs(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets a catalog
	 * 
	 * @param catalogId
	 *                      Catalog ID
	 * @return MLPCatalog; null if not found
	 */
	MLPCatalog getCatalog(String catalogId);

	/**
	 * Creates a catalog.
	 * 
	 * @param catalog
	 *                    Catalog
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPCatalog createCatalog(MLPCatalog catalog) throws RestClientResponseException;

	/**
	 * Updates a catalog.
	 * 
	 * @param catalog
	 *                    Instance to update
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
	 * @return Page of objects; empty if none are found
	 */
	RestPageResponse<MLPSolution> getSolutionsInCatalogs(String[] catalogIds, RestPageRequest pageRequest);

	/**
	 * Gets the catalogs for the specified solution; i.e., all places to which the
	 * solution is published.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return List of objects; empty if none are found
	 */
	List<MLPCatalog> getSolutionCatalogs(String solutionId);

	/**
	 * Adds (publishes) the specified solution to the specified catalog.
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
	 * Drops (unpublishes) the specified solution from the specified catalog.
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
	 * @return Page of task objects.
	 */
	RestPageResponse<MLPTask> getTasks(RestPageRequest pageRequest);

	/**
	 * Gets a page of tasks that exactly match the search parameters.
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
	 * @return Page of step result objects
	 */
	RestPageResponse<MLPTask> searchTasks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Creates a task.
	 * 
	 * @param task
	 *                 Task data. The ID field should be null.
	 * @return Complete object, with generated information including ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPTask createTask(MLPTask task) throws RestClientResponseException;

	/**
	 * Updates the specified task, which must exist.
	 * 
	 * @param task
	 *                 Task data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateTask(MLPTask task) throws RestClientResponseException;

	/**
	 * Deletes the specified task. Cascades the delete to associated task step
	 * result items.
	 * 
	 * @param taskId
	 *                   task ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteTask(long taskId) throws RestClientResponseException;

	/**
	 * Gets a page of right-to-use references.
	 *
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of RTU reference objects
	 */
	RestPageResponse<MLPRtuReference> getRtuReferences(RestPageRequest pageRequest);

	/**
	 * Creates a right-to-use reference entry.
	 * 
	 * @param rtuRef
	 *                   RTU reference object
	 * @return Complete object which wraps the ref
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRtuReference createRtuReference(MLPRtuReference rtuRef) throws RestClientResponseException;

	/**
	 * Deletes a right-to-use reference entry. A ref can be deleted if is not
	 * associated with any other entities; if associations remain the delete will
	 * fail.
	 * 
	 * @param rtuRef
	 *                   RTU reference object
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRtuReference(MLPRtuReference rtuRef) throws RestClientResponseException;

	/**
	 * Gets the right to use record with the specified ID.
	 * 
	 * @param rtuId
	 *                  Right to use ID
	 * @return Right to Use object
	 */
	MLPRightToUse getRightToUse(Long rtuId);

	/**
	 * Gets a page of right-to-use records.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of objects.
	 */
	RestPageResponse<MLPRightToUse> getRightToUses(RestPageRequest pageRequest);

	/**
	 * Searches right-to-use records for exact matches.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: site,
	 *                            solutionId
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of RTU objects
	 */
	RestPageResponse<MLPRightToUse> searchRightToUses(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest);

	/**
	 * Gets a list of right-to-use records for the specified solution and user.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param userId
	 *                       User ID
	 * @return List of objects
	 */
	List<MLPRightToUse> getRightToUses(String solutionId, String userId);

	/**
	 * Creates a right-to-use record.
	 * 
	 * @param rightToUse
	 *                       Right to use data
	 * @return Complete object, with generated information such as ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	MLPRightToUse createRightToUse(MLPRightToUse rightToUse) throws RestClientResponseException;

	/**
	 * Updates the specified right-to-use record.
	 * 
	 * @param rightToUse
	 *                       Right to use data
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void updateRightToUse(MLPRightToUse rightToUse) throws RestClientResponseException;

	/**
	 * Deletes the specified right-to-use record.
	 * 
	 * @param rtuId
	 *                  Right to use ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void deleteRightToUse(Long rtuId) throws RestClientResponseException;

	/**
	 * Maps the specified reference to the specified right-to-use.
	 * 
	 * @param refId
	 *                  Remote LUM system reference ID
	 * @param rtuId
	 *                  Right to Use ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addRefToRtu(String refId, Long rtuId) throws RestClientResponseException;

	/**
	 * Unmaps the specified reference from the specified right-to-use.
	 * 
	 * @param refId
	 *                  Remote LUM system reference ID
	 * @param rtuId
	 *                  Right to Use ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropRefFromRtu(String refId, Long rtuId) throws RestClientResponseException;

	/**
	 * Gets a list of users mapped to the specified right-to-use record.
	 * 
	 * @param rtuId
	 *                  Right-to-Use ID
	 * @return List of MLPUser objects, which may be empty
	 */
	List<MLPUser> getRtuUsers(long rtuId);

	/**
	 * Maps the specified user to the specified right-to-use.
	 * 
	 * @param userId
	 *                   User ID
	 * @param rtuId
	 *                   Right to Use ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void addUserToRtu(String userId, Long rtuId) throws RestClientResponseException;

	/**
	 * Unmaps the specified user from the specified right-to-use.
	 * 
	 * @param userId
	 *                   User ID
	 * @param rtuId
	 *                   Right to Use ID
	 * @throws RestClientResponseException
	 *                                         Error message is in the response body
	 */
	void dropUserFromRtu(String userId, Long rtuId) throws RestClientResponseException;

	/**
	 * Gets the IDs of all the peer's special-access catalogs.
	 * 
	 * @param peerId
	 *                   Peer ID
	 * @return List of String
	 */
	List<String> getPeerAccessCatalogIds(String peerId);

	/**
	 * Marks the specified catalog as accessible to the specified peer. The catalog
	 * must be restricted.
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
	 * Removes the marking that the specified catalog is accessible to the specified
	 * peer.
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
	 * Gets the IDs of ALL the user's favorite catalogs.
	 * 
	 * @param userId
	 *                   User ID
	 * @return List of String
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
	 * @return Page of workbench project objects.
	 */
	RestPageResponse<MLPProject> getProjects(RestPageRequest pageRequest);

	/**
	 * Searches workbench project records for exact matches.
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
	 * @return Page of Project objects
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
	 * Creates a workbench project.
	 * 
	 * @param project
	 *                    Project data. If the ID field is null a new value is
	 *                    generated; otherwise the ID value is used if valid and not
	 *                    already known.
	 * @return Complete object, with generated information such as ID
	 */
	MLPProject createProject(MLPProject project);

	/**
	 * Updates a workbench project.
	 * 
	 * @param project
	 *                    Project data
	 */
	void updateProject(MLPProject project);

	/**
	 * Deletes a workbench project. Cascades the delete; e.g., removes the
	 * association with any notebooks, pipelines, users, etc. Answers bad request if
	 * the ID is not known.
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
	 *                      Project ID.
	 * @return List of Notebook objects; empty if none are found
	 */
	List<MLPNotebook> getProjectNotebooks(String projectId);

	/**
	 * Gets the workbench projects to which the specified notebook is mapped.
	 * 
	 * @param notebookId
	 *                       Notebook ID.
	 * @return List of Project objects; empty if none are found
	 */
	List<MLPProject> getNotebookProjects(String notebookId);

	/**
	 * Gets the workbench pipelines mapped to the specified project ID.
	 * 
	 * @param projectId
	 *                      Project ID.
	 * @return List of Pipeline objects; empty if none are found
	 */
	List<MLPPipeline> getProjectPipelines(String projectId);

	/**
	 * Gets the workbench projects to which the specified pipeline is mapped.
	 * 
	 * @param pipelineId
	 *                       Pipeline ID.
	 * @return List of Project objects; empty if none are found
	 */
	List<MLPProject> getPipelineProjects(String pipelineId);

	/**
	 * Gets a page of workbench notebooks.
	 * 
	 * @param pageRequest
	 *                        Page index, page size and sort information; defaults
	 *                        to page 0 of size 20 if null.
	 * @return Page of workbench notebook objects.
	 */
	RestPageResponse<MLPNotebook> getNotebooks(RestPageRequest pageRequest);

	/**
	 * Searches workbench notebook records for exact matches.
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
	 * @return Page of Notebook objects
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
	 * Creates a workbench notebook.
	 * 
	 * @param notebook
	 *                     Notebook data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known.
	 * @return Complete object, with generated information such as ID
	 */
	MLPNotebook createNotebook(MLPNotebook notebook);

	/**
	 * Updates a workbench notebook.
	 * 
	 * @param notebook
	 *                     Notebook data
	 */
	void updateNotebook(MLPNotebook notebook);

	/**
	 * Deletes a workbench notebook. Cascades the delete; e.g., removes the
	 * association with any projects, users, etc. Answers bad request if the ID is
	 * not known.
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
	 * @return Page of workbench pipeline objects.
	 */
	RestPageResponse<MLPPipeline> getPipelines(RestPageRequest pageRequest);

	/**
	 * Searches workbench pipeline records for exact matches.
	 * 
	 * @param queryParameters
	 *                            Map of field-name, field-value pairs to use as
	 *                            query criteria. Accepts these field names: name,
	 *                            active, userId, version, serviceStatus,
	 *                            repositoryUrl, serviceUrl
	 * @param isOr
	 *                            If true, finds matches on any field-value pair
	 *                            (conditions are OR-ed together); otherwise finds
	 *                            matches on all field-value pairs (conditions are
	 *                            AND-ed together).
	 * @param pageRequest
	 *                            Page index, page size and sort information;
	 *                            defaults to page 0 of size 20 if null.
	 * @return Page of Pipeline objects
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
	 * Creates a workbench pipeline.
	 * 
	 * @param pipeline
	 *                     Pipeline data. If the ID field is null a new value is
	 *                     generated; otherwise the ID value is used if valid and
	 *                     not already known.
	 * @return Complete object, with generated information such as ID
	 */
	MLPPipeline createPipeline(MLPPipeline pipeline);

	/**
	 * Updates a workbench pipeline.
	 * 
	 * @param pipeline
	 *                     Pipeline data
	 */
	void updatePipeline(MLPPipeline pipeline);

	/**
	 * Deletes a workbench pipeline. Cascades the delete; e.g., removes the
	 * association with any projects, users, etc. Answers bad request if the ID is
	 * not known.
	 * 
	 * @param pipelineId
	 *                       pipeline ID
	 */
	void deletePipeline(String pipelineId);

}
