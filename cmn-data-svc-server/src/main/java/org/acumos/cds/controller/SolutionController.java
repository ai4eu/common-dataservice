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

package org.acumos.cds.controller;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCompSolMap;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionPicture;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRating.SolutionRatingPK;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.CatSolMapRepository;
import org.acumos.cds.repository.CompSolMapRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolTagMapRepository;
import org.acumos.cds.repository.SolUserAccMapRepository;
import org.acumos.cds.repository.SolutionDeploymentRepository;
import org.acumos.cds.repository.SolutionDownloadRepository;
import org.acumos.cds.repository.SolutionFavoriteRepository;
import org.acumos.cds.repository.SolutionPictureRepository;
import org.acumos.cds.repository.SolutionRatingRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.StepResultRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.service.SolutionSearchService;
import org.acumos.cds.transport.CountTransport;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.util.ApiPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A solution is a collection of revisions. A revision points to a collection of
 * artifacts. A revision cannot exist without a solution, but a solution can
 * exist without a revision (altho it will not be found by searches).
 * <P>
 * Validation design decisions:
 * <OL>
 * <LI>Keep queries fast, so check nothing on read.</LI>
 * <LI>Provide useful messages on failure, so check everything on write.</LI>
 * <LI>Also see:
 * https://stackoverflow.com/questions/942951/rest-api-error-return-good-practices
 * </LI>
 * </OL>
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.SOLUTION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class SolutionController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private CatSolMapRepository catSolMapRepository;
	@Autowired
	private CompSolMapRepository compSolMapRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private SolTagMapRepository solTagMapRepository;
	@Autowired
	private SolUserAccMapRepository solUserAccMapRepository;
	@Autowired
	private SolutionDeploymentRepository solutionDeploymentRepository;
	@Autowired
	private SolutionDownloadRepository solutionDownloadRepository;
	@Autowired
	private SolutionFavoriteRepository solutionFavoriteRepository;
	@Autowired
	private SolutionRatingRepository solutionRatingRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolutionPictureRepository solutionPictureRepository;
	@Autowired
	private SolutionRevisionRepository solutionRevisionRepository;
	@Autowired
	private SolutionSearchService solutionSearchService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StepResultRepository stepResultRepository;

	/**
	 * Updates the cached value(s) for solution downloads.
	 * 
	 * Is extra concurrency control required here? A spring controller is a
	 * singleton, but what about threads?
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	private void updateSolutionDownloadStats(String solutionId) {
		long count = solutionDownloadRepository.countSolutionDownloads(solutionId);
		Optional<MLPSolution> da = solutionRepository.findById(solutionId);
		if (count > 0 && da.isPresent()) {
			MLPSolution sol = da.get();
			sol.setDownloadCount(count);
			sol.setLastDownload(Instant.now());
			solutionRepository.save(sol);
		}
	}

	/**
	 * Updates the cached value(s) for solution ratings.
	 * 
	 * Is extra concurrency control required here? A spring controller is a
	 * singleton, but what about threads?
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	private void updateSolutionRatingStats(String solutionId) {
		long count = solutionRatingRepository.countSolutionRatings(solutionId);
		Double avg = solutionRatingRepository.getSolutionRatingAverage(solutionId);
		if (count == 0 || avg == null) {
			logger.warn("updateSolutionRatingStats failed on ID {}", solutionId);
		} else {
			Optional<MLPSolution> da = solutionRepository.findById(solutionId);
			// Because the count and average are both present, the solution exists.
			// Add this no-op to silence a Sonar warning.
			if (da.isPresent()) {
				MLPSolution sol = da.get();
				sol.setRatingCount(count);
				sol.setRatingAverageTenths(Math.round(10 * avg));
				solutionRepository.save(sol);
			}
		}
	}

	@ApiOperation(value = "Gets the count of solutions.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getSolutionCount() {
		logger.debug("getSolutionCount");
		long count = solutionRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets the solution for the specified ID. Returns null if the ID is not found.", //
			response = MLPSolution.class)
	@RequestMapping(value = "/{solutionId}", method = RequestMethod.GET)
	public MLPSolution getSolution(@PathVariable("solutionId") String solutionId) {
		logger.debug("getSolution: ID {}", solutionId);
		Optional<MLPSolution> da = solutionRepository.findById(solutionId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Gets a page of solutions, optionally sorted. Answers empty if none are found.", response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPSolution> getSolutions(Pageable pageable) {
		logger.debug("getSolutions {}", pageable);
		return solutionRepository.findAll(pageable);
	}

	@ApiOperation(value = "Searches for entities with names or descriptions that contain the search term using the like operator. Answers empty if none are found.", //
			response = MLPSolution.class, responseContainer = "Page")
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.LIKE_PATH, method = RequestMethod.GET)
	public Page<MLPSolution> findSolutionsBySearchTerm(@RequestParam(CCDSConstants.TERM_PATH) String term,
			Pageable pageRequest) {
		logger.debug("findSolutionsBySearchTerm {}", term);
		return solutionRepository.findBySearchTerm(term, pageRequest);
	}

	@ApiOperation(value = "Gets a page of solutions matching the specified tag. Answers empty if none are found.", response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.TAG_PATH, method = RequestMethod.GET)
	public Object findSolutionsByTag(@RequestParam("tag") String tag, Pageable pageRequest) {
		logger.debug("findSolutionsByTag {}", tag);
		return solutionRepository.findByTag(tag, pageRequest);
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many class field names.
	 */
	private static final String NAME = "name";
	private static final String ACTIVE = "active";
	private static final String USER_ID = "userId";
	private static final String SOURCE_ID = "sourceId";
	private static final String MODEL_TYPE_CODE = "modelTypeCode";
	private static final String TOOLKIT_TYPE_CODE = "toolkitTypeCode";
	private static final String ORIGIN = "origin";

	@ApiOperation(value = "Searches for solutions with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchSolutions( //
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Name") //
			@RequestParam(name = NAME, required = false) String name, //
			@ApiParam(value = "Active") //
			@RequestParam(name = ACTIVE, required = false) Boolean active, //
			@ApiParam(value = "User ID") //
			@RequestParam(name = USER_ID, required = false) String userId, //
			@ApiParam(value = "Source ID") //
			@RequestParam(name = SOURCE_ID, required = false) String sourceId, //
			@ApiParam(value = "Model type code") //
			@RequestParam(name = MODEL_TYPE_CODE, required = false) String modelTypeCode, //
			@ApiParam(value = "Toolkit type code") //
			@RequestParam(name = TOOLKIT_TYPE_CODE, required = false) String toolkitTypeCode, //
			@ApiParam(value = "Origin URI") //
			@RequestParam(name = ORIGIN, required = false) String origin, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchSolutions enter");
		boolean isOr = junction != null && "o".equals(junction);
		if (name == null && active == null && userId == null && sourceId == null && modelTypeCode == null
				&& toolkitTypeCode == null && origin == null) {
			logger.warn("searchSolutions missing query");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return solutionSearchService.searchSolutions(name, active, userId, sourceId, modelTypeCode, toolkitTypeCode,
					origin, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchSolutions failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchSolutions failed", ex);
		}
	}

	@ApiOperation(value = "Finds solutions with attribute values and/or child attribute values matching the field name - field value pairs specified as query parameters. " //
			+ "Supports faceted search; i.e., check for kw1 in name, kw2 in description and so on.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/"
			+ CCDSConstants.PORTAL_PATH, method = RequestMethod.GET)
	public Object findPortalSolutions( //
			@ApiParam(value = "Active Y/N", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = true) boolean active, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			@ApiParam(value = "Model type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_MODEL_TYPES, required = false) String[] modelTypeCodes, //
			@ApiParam(value = "User IDs", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_USERS, required = false) String[] userIds, //
			@ApiParam(value = "Tags", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_TAGS, required = false) String[] tags, //
			@ApiParam(value = "Name key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_NAME, required = false) String[] nameKws, //
			@ApiParam(value = "Description key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_DESC, required = false) String[] descKws, //
			@ApiParam(value = "Author key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_AUTH, required = false) String[] authKws, //
			@ApiParam(value = "Publisher key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_PUB, required = false) String[] pubKws, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("findPortalSolutions: active {} nameKws {}", active, nameKws);
		try {
			return solutionSearchService.findPortalSolutions(nameKws, descKws, active, userIds, modelTypeCodes,
					accTypeCodes, tags, authKws, pubKws, pageRequest);
		} catch (Exception ex) {
			logger.error("findPortalSolutions failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findPortalSolutions failed", ex);
		}
	}

	@ApiOperation(value = "Finds solutions matching the specified attribute values and/or child attribute values " //
			+ " with flexible handling of tags to allow all/any matches. "
			+ " Checks multiple fields for the supplied keywords, including ID, name, description etc.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.PORTAL_PATH + "/"
			+ CCDSConstants.KW_TAG_PATH, method = RequestMethod.GET)
	public Object findPortalSolutionsByKwAndTags( //
			@ApiParam(value = "Active Y/N", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = true) boolean active, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			@ApiParam(value = "Model type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_MODEL_TYPES, required = false) String[] modelTypeCodes, //
			@ApiParam(value = "Key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_KW, required = false) String[] kws, //
			@ApiParam(value = "User IDs", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_USERS, required = false) String[] userIds, //
			@ApiParam(value = "All tags, solution must have every one", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ALL_TAGS, required = false) String[] allTags, //
			@ApiParam(value = "Any tags, solution must have at least one", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ANY_TAGS, required = false) String[] anyTags, //
			@ApiParam(value = "Catalog ID", allowMultiple = false) //
			@RequestParam(name = CCDSConstants.SEARCH_CATALOG, required = false) String catalogId, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("findPortalSolutionsByKwAndTags: active {} kw {}", active, kws);
		try {
			return solutionSearchService.findPortalSolutionsByKwAndTags(kws, active, userIds, modelTypeCodes,
					accTypeCodes, allTags, anyTags, catalogId, pageRequest);
		} catch (Exception ex) {
			logger.error("findPortalSolutionsByKwAndTags failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findPortalSolutionsByKwAndTags failed", ex);
		}
	}

	@ApiOperation(value = "Finds user-accessible solutions matching the specified attribute values. "
			+ "Keywords are processed using LIKE-operator search.  Does not search any child entities.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.USER_PATH, method = RequestMethod.GET)
	public Object findUserSolutions( //
			@ApiParam(value = "Active Y/N", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = true) boolean active, //
			@ApiParam(value = "User ID", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_USERS, required = true) String userId, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			@ApiParam(value = "Model type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_MODEL_TYPES, required = false) String[] modelTypeCodes, //
			@ApiParam(value = "Name key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_NAME, required = false) String[] nameKws, //
			@ApiParam(value = "Description key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_DESC, required = false) String[] descKws, //
			@ApiParam(value = "Tags", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_TAGS, required = false) String[] tags, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("findUserSolutions: active {} userId {}", active, userId);
		try {
			return solutionSearchService.findUserSolutions(nameKws, descKws, active, userId, modelTypeCodes,
					accTypeCodes, tags, pageRequest);
		} catch (Exception ex) {
			logger.error("findUserSolutions failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findUserSolutions failed", ex);
		}
	}

	@ApiOperation(value = "Finds solutions based on specified date, active status and access type query parameters. " //
			+ "Limits result to solutions modified after the specified time, expressed in milliseconds since the Epoch.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.DATE_PATH, method = RequestMethod.GET)
	public Object findSolutionsByDate( //
			@ApiParam(value = "Milliseconds since the Epoch", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_INSTANT, required = true) long millis, //
			@ApiParam(value = "Active Y/N") //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = false) boolean active, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("findSolutionsByDate: date {}", millis);
		Instant ts = Instant.ofEpochMilli(millis);
		try {
			return solutionSearchService.findSolutionsByModifiedDate(active, accTypeCodes, ts, pageRequest);
		} catch (Exception ex) {
			logger.error("findSolutionsByDate failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findSolutionsByDate failed", ex);
		}
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPSolution.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createSolution(@RequestBody MLPSolution solution, HttpServletResponse response) {
		logger.debug("createSolution: enter");
		try {
			// Validate enum codes
			if (solution.getModelTypeCode() != null)
				super.validateCode(solution.getModelTypeCode(), CodeNameType.MODEL_TYPE);
			if (solution.getToolkitTypeCode() != null)
				super.validateCode(solution.getToolkitTypeCode(), CodeNameType.TOOLKIT_TYPE);
			String id = solution.getSolutionId();
			if (id != null) {
				UUID.fromString(id);
				if (solutionRepository.findById(id).isPresent()) {
					logger.warn("createSolution failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Solution exists with ID " + id);
				}
			}
			// Cascade manually - create user-supplied tags as needed
			createMissingTags(solution.getTags());
			// Create a new row
			// ALSO send back the model for client convenience
			MLPSolution persisted = solutionRepository.save(solution);
			// This is a hack to create the location path.
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.SOLUTION_PATH + "/" + persisted.getSolutionId());
			return persisted;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSolution failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolution failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}", method = RequestMethod.PUT)
	public MLPTransportModel updateSolution(@PathVariable("solutionId") String solutionId,
			@RequestBody MLPSolution solution, HttpServletResponse response) {
		logger.debug("updateSolution: ID {}", solutionId);
		// Check the existing one
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("updateSolution failed on ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		try {
			// Validate enum codes
			if (solution.getModelTypeCode() != null)
				super.validateCode(solution.getModelTypeCode(), CodeNameType.MODEL_TYPE);
			if (solution.getToolkitTypeCode() != null)
				super.validateCode(solution.getToolkitTypeCode(), CodeNameType.TOOLKIT_TYPE);
			// Use the path-parameter id; don't trust the one in the object
			solution.setSolutionId(solutionId);
			// Cascade manually - create user-supplied tags as needed
			createMissingTags(solution.getTags());
			solutionRepository.save(solution);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolution failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolution failed", cve);
		}
	}

	@ApiOperation(value = "Increments the view count of the specified solution (special case of update). Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.VIEW_PATH, method = RequestMethod.PUT)
	public MLPTransportModel incrementViewCount(@PathVariable("solutionId") String solutionId,
			HttpServletResponse response) {
		logger.debug("incrementViewCount: ID {}", solutionId);
		// Check the existing one; the update command doesn't fail on invalid ID
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("incrementViewCount failed on ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		try {
			// Have the database do the increment to avoid race conditions
			solutionRepository.incrementViewCount(solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// Should never happen
			logger.error("incrementViewCount failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "incrementViewCount failed", ex);
		}
	}

	/*
	 * Originally this was declared void and accordingly returned nothing. But when
	 * used in SpringBoot, after invoking the method it would look for a ThymeLeaf
	 * template, fail to find it, then throw internal server error.
	 */
	@ApiOperation(value = "Deletes the solution with the specified ID. Cascades the delete to related entities. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSolution(@PathVariable("solutionId") String solutionId,
			HttpServletResponse response) {
		logger.debug("deleteSolution: ID {}", solutionId);
		try {
			// Manually cascade the delete
			solutionDeploymentRepository.deleteBySolutionId(solutionId);
			compSolMapRepository.deleteByParentId(solutionId);
			solTagMapRepository.deleteBySolutionId(solutionId);
			solutionDownloadRepository.deleteBySolutionId(solutionId);
			solutionRatingRepository.deleteBySolutionId(solutionId);
			catSolMapRepository.deleteBySolutionId(solutionId);
			solUserAccMapRepository.deleteBySolutionId(solutionId);
			solutionFavoriteRepository.deleteBySolutionId(solutionId);
			stepResultRepository.deleteBySolutionId(solutionId);
			for (MLPSolutionRevision r : solutionRevisionRepository.findBySolutionIdIn(new String[] { solutionId })) {
				for (MLPArtifact a : artifactRepository.findByRevision(r.getRevisionId()))
					solRevArtMapRepository
							.deleteById(new MLPSolRevArtMap.SolRevArtMapPK(r.getRevisionId(), a.getArtifactId()));
				// do NOT delete artifacts!
				solutionRevisionRepository.delete(r);
			}
			solutionRepository.deleteById(solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolution failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolution failed", ex);
		}
	}

	/*
	 * Spring will split the list if the path variable is declared as String array
	 * or List of String.
	 */
	@ApiOperation(value = "Gets all revisions for the specified solution IDs. Answers empty if none are found.", //
			response = MLPSolutionRevision.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH, method = RequestMethod.GET)
	public Iterable<MLPSolutionRevision> getListOfRevisions(@PathVariable("solutionId") String[] solutionIds) {
		logger.debug("getListOfRevisions: solution IDs {}", Arrays.toString(solutionIds));
		return solutionRevisionRepository.findBySolutionIdIn(solutionIds);
	}

	@ApiOperation(value = "Gets the revision for the specified ID. Returns null if the ID is not found.", //
			response = MLPSolution.class)
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.GET)
	public MLPSolutionRevision getSolutionRevision(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId) {
		logger.debug("getSolutionRevision: solutionId {} revisionId {}", solutionId, revisionId);
		Optional<MLPSolutionRevision> da = solutionRevisionRepository.findById(revisionId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new revision and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPSolutionRevision.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH, method = RequestMethod.POST)
	public MLPResponse createSolutionRevision(@PathVariable("solutionId") String solutionId,
			@RequestBody MLPSolutionRevision revision, HttpServletResponse response) {
		logger.debug("createSolutionRevision: solutionId {}", solutionId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("createSolutionRevision failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		try {
			// Validate enum codes
			if (revision.getAccessTypeCode() != null)
				super.validateCode(revision.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			if (revision.getVerifiedLicense() != null)
				super.validateCode(revision.getVerifiedLicense(), CodeNameType.VERIFIED_LICENSE);
			if (revision.getVerifiedVulnerability() != null)
				super.validateCode(revision.getVerifiedVulnerability(), CodeNameType.VERIFIED_VULNERABILITY);
			String id = revision.getRevisionId();
			if (id != null) {
				UUID.fromString(id);
				if (solutionRevisionRepository.findById(id).isPresent()) {
					logger.warn("createSolutionRevision failed on rev ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Revision exists with ID " + id);
				}
			}
			// Ensure correct solution ID
			revision.setSolutionId(solutionId);
			// Create a new row
			return solutionRevisionRepository.save(revision);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSolutionRevision failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolutionRevision failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing revision with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.PUT)
	public MLPTransportModel updateSolutionRevision(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @RequestBody MLPSolutionRevision revision,
			HttpServletResponse response) {
		logger.debug("updateSolutionRevision: solution ID {}, revision ID {}", solutionId, revisionId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("updateSolutionRevision failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (!solutionRevisionRepository.findById(revisionId).isPresent()) {
			logger.warn("updateSolutionRevision failed on rev ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		try {
			// Validate enum codes
			if (revision.getAccessTypeCode() != null)
				super.validateCode(revision.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			if (revision.getVerifiedLicense() != null)
				super.validateCode(revision.getVerifiedLicense(), CodeNameType.VERIFIED_LICENSE);
			if (revision.getVerifiedVulnerability() != null)
				super.validateCode(revision.getVerifiedVulnerability(), CodeNameType.VERIFIED_VULNERABILITY);
			// Use the validated values
			revision.setRevisionId(revisionId);
			revision.setSolutionId(solutionId);
			solutionRevisionRepository.save(revision);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolutionRevision failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionRevision failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the revision with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSolutionRevision(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, HttpServletResponse response) {
		logger.debug("deleteSolutionRevision: solutionId {} revisionId {}", solutionId, revisionId);
		try {
			solutionRevisionRepository.deleteById(revisionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionRevision failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRevision failed", ex);
		}
	}

	@ApiOperation(value = "Gets all tags for the specified solution. Answers empty if none are found.", response = MLPTag.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.TAG_PATH, method = RequestMethod.GET)
	public Iterable<MLPTag> getTagsForSolution(@PathVariable("solutionId") String solutionId) {
		logger.debug("getTagsForSolution: solutionId {}", solutionId);
		return tagRepository.findBySolution(solutionId);
	}

	@ApiOperation(value = "Adds a tag to the solution. Creates the tag if necessary. Returns bad request if the solution ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.TAG_PATH + "/{tag}", method = RequestMethod.POST)
	public MLPTransportModel addSolutionTag(@PathVariable("solutionId") String solutionId,
			@PathVariable("tag") String tag, HttpServletResponse response) {
		logger.debug("addSolutionTag: solutionId {} tag {}", solutionId, tag);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("addSolutionTag failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solTagMapRepository.findById(new MLPSolTagMap.SolTagMapPK(solutionId, tag)).isPresent()) {
			logger.warn("addSolutionTag failed on existing tag {}", tag);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Already has tag " + tag, null);
		}
		if (!tagRepository.findById(tag).isPresent()) {
			// Tags are cheap & easy to create, so make life easy for client
			tagRepository.save(new MLPTag(tag));
			logger.debug("addSolutionTag: created tag {}", tag);
		}
		solTagMapRepository.save(new MLPSolTagMap(solutionId, tag));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Drops a tag from the solution. Returns bad request if either is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.TAG_PATH + "/{tag}", method = RequestMethod.DELETE)
	public MLPTransportModel dropSolutionTag(@PathVariable("solutionId") String solutionId,
			@PathVariable("tag") String tag, HttpServletResponse response) {
		logger.debug("dropSolutionTag: solutionId {} tag {}", solutionId, tag);
		try {
			solTagMapRepository.deleteById(new MLPSolTagMap.SolTagMapPK(solutionId, tag));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropSolutionTag failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionTag failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of download records for the specified solution ID. Returns empty if none are found.", //
			response = MLPSolutionDownload.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.DOWNLOAD_PATH, method = RequestMethod.GET)
	public Page<MLPSolutionDownload> getSolutionDownloads(@PathVariable("solutionId") String solutionId,
			Pageable pageRequest) {
		logger.debug("getSolutionDownloads: solutionId {}", solutionId);
		return solutionDownloadRepository.findBySolutionId(solutionId, pageRequest);
	}

	@ApiOperation(value = "Creates a new solution download record. Returns bad request on constraint violation etc.", //
			response = MLPSolutionDownload.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.DOWNLOAD_PATH + "/" + CCDSConstants.ARTIFACT_PATH
			+ "/{artifactId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.POST)
	public MLPResponse createSolutionDownload(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, @PathVariable("artifactId") String artifactId,
			@RequestBody MLPSolutionDownload sd, HttpServletResponse response) {
		logger.debug("createSolutionDownload: solutionId {} userId {} artifactId {}", solutionId, userId, artifactId);
		try {
			// Create a new row using path IDs
			sd.setSolutionId(solutionId);
			sd.setUserId(userId);
			sd.setArtifactId(artifactId);
			MLPSolutionDownload result = solutionDownloadRepository.save(sd);
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.SOLUTION_PATH + "/" + sd.getSolutionId() + "/"
					+ CCDSConstants.DOWNLOAD_PATH + sd.getDownloadId());
			// Update cache
			updateSolutionDownloadStats(solutionId);
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.error("createSolutionDownload failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "createSolutionDownload failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the download with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.DOWNLOAD_PATH
			+ "/{downloadId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSolutionDownload(@PathVariable("solutionId") String solutionId,
			@PathVariable("downloadId") Long downloadId, HttpServletResponse response) {
		logger.debug("deleteSolutionDownload: solutionId {} downloadId {}", solutionId, downloadId);
		try { // Build a key for fetch
			solutionDownloadRepository.deleteById(downloadId);
			// Update cache!
			updateSolutionDownloadStats(solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionDownload failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionDownload failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of user ratings for the specified solution. Returns empty if none are found.", //
			response = MLPSolutionRating.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH, method = RequestMethod.GET)
	public Object getListOfSolutionRating(@PathVariable("solutionId") String solutionId, Pageable pageRequest) {
		logger.debug("getListOfSolutionRating: solutionId {}", solutionId);
		return solutionRatingRepository.findBySolutionId(solutionId, pageRequest);
	}

	@ApiOperation(value = "Gets an existing solution rating. Returns null if not found", //
			response = MLPSolutionRating.class)
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.GET)
	public MLPSolutionRating getSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId) {
		logger.debug("getSolutionRating: solutionId {} userId {}", solutionId, userId);
		SolutionRatingPK pk = new SolutionRatingPK(solutionId, userId);
		Optional<MLPSolutionRating> da = solutionRatingRepository.findById(pk);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new solution rating. Returns bad request on constrain violation etc.", response = MLPSolutionRating.class)
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.POST)
	public Object createSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, @RequestBody MLPSolutionRating sr, HttpServletResponse response) {
		logger.debug("createSolutionRating: solutionId {} userId {}", solutionId, userId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("createSolutionRating failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("createSolutionRating failed on user ID {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		try {
			// Use path IDs
			sr.setSolutionId(solutionId);
			sr.setUserId(userId);
			Object result = solutionRatingRepository.save(sr);
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.SOLUTION_PATH + "/" + solutionId + "/"
					+ CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH + "/" + userId);
			// Update cache
			updateSolutionRatingStats(solutionId);
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSolutionRating failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolutionRating failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.PUT)
	public Object updateSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, @RequestBody MLPSolutionRating sr, HttpServletResponse response) {
		logger.debug("updateSolutionRating: solutionId {} userId {}", solutionId, userId);
		// Check the existing one
		SolutionRatingPK pk = new SolutionRatingPK(solutionId, userId);
		if (!solutionRatingRepository.findById(pk).isPresent()) {
			logger.warn("updateSolutionRating failed on key {}", pk);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + pk, null);
		}
		try {
			// Use path IDs
			sr.setSolutionId(solutionId);
			sr.setUserId(userId);
			solutionRatingRepository.save(sr);
			// Update cache!
			updateSolutionRatingStats(solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolutionRating failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionRating failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the rating with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.debug("deleteSolutionRating: solutionId {} userId {}", solutionId, userId);
		try {
			// Build a key for fetch
			SolutionRatingPK pk = new SolutionRatingPK(solutionId, userId);
			solutionRatingRepository.deleteById(pk);
			// Update cache!
			updateSolutionRatingStats(solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionRating failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionRating failed", ex);
		}
	}

	@ApiOperation(value = "Gets access-control list of users for the specified solution.", //
			response = MLPUser.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.USER_PATH + "/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	public Iterable<MLPUser> getSolutionACL(@PathVariable("solutionId") String solutionId) {
		logger.debug("getSolutionACL: solutionId {}", solutionId);
		return solUserAccMapRepository.getUsersForSolution(solutionId);
	}

	@ApiOperation(value = "Adds a user to the ACL for the specified solution. Returns bad request if an ID is not found", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.POST)
	public Object addUserToSolutionACL(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.debug("addUserToSolutionACL: solution {}, user {}", solutionId, userId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("addUserToSolutionACL failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("addUserToSolutionACL failed on user ID {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		solUserAccMapRepository.save(new MLPSolUserAccMap(solutionId, userId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Drops a user from the ACL for the specified solution. Returns bad request if an ID is not found", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.DELETE)
	public Object dropUserFromSolutionACL(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.debug("dropUserFromSolutionACL: solution {}, user {}", solutionId, userId);
		try {
			solUserAccMapRepository.deleteById(new MLPSolUserAccMap.SolUserAccessMapPK(solutionId, userId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropUserFromSolutionACL failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropUserFromSolutionACL failed", ex);
		}

	}

	@ApiOperation(value = "Gets a page of solutions with the specified user in the ACL, optionally sorted on fields. Answers empty if none are found.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	public Page<MLPSolution> getAccessibleSolutions(@PathVariable("userId") String userId, Pageable pageable) {
		logger.debug("getAccessibleSolutions: user {}", userId);
		return solUserAccMapRepository.getSolutionsForUser(userId, pageable);
	}

	@ApiOperation(value = "Gets a page of deployments for the specified solution and revision IDs. Returns empty if none are found.", //
			response = MLPSolutionDeployment.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH, method = RequestMethod.GET)
	public Page<MLPSolutionDeployment> getSolutionDeployments(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, Pageable pageRequest) {
		logger.debug("getSolutionDeployments: solutionId {} revisionId {}", solutionId, revisionId);
		return solutionDeploymentRepository.findBySolutionIdAndRevisionId(solutionId, revisionId, pageRequest);
	}

	@ApiOperation(value = "Gets a page of deployments for the specified solution, revision and user IDs. Returns empty if none are found.", //
			response = MLPSolutionDeployment.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/" + CCDSConstants.USER_PATH
			+ "/{userId}/" + CCDSConstants.DEPLOY_PATH, method = RequestMethod.GET)
	public Page<MLPSolutionDeployment> getUserSolutionRevisionDeployments(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("userId") String userId,
			Pageable pageRequest) {
		logger.debug("getUserSolutionRevisionDeployments: solutionId {} revisionId {} userId {}", solutionId,
				revisionId, userId);
		return solutionDeploymentRepository.findBySolutionIdAndRevisionIdAndUserId(solutionId, revisionId, userId,
				pageRequest);
	}

	@ApiOperation(value = "Creates a new deployment record for the specified solution and revision. Returns bad request if an ID is not found.", //
			response = MLPSolutionDeployment.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH, method = RequestMethod.POST)
	public Object createSolutionDeployment(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @RequestBody MLPSolutionDeployment sd,
			HttpServletResponse response) {
		logger.debug("createSolutionDeployment: solutionId {} revisionId {}", solutionId, revisionId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("createSolutionDeployment failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (!solutionRevisionRepository.findById(revisionId).isPresent()) {
			logger.warn("createSolutionDeployment failed on rev ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		if (!userRepository.findById(sd.getUserId()).isPresent()) {
			logger.warn("createSolutionDeployment failed on usr ID {}", sd.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + sd.getUserId(), null);
		}
		try {
			// Validate enum code
			super.validateCode(sd.getDeploymentStatusCode(), CodeNameType.DEPLOYMENT_STATUS);
			// Validate ID if present
			String id = sd.getDeploymentId();
			if (id != null) {
				UUID.fromString(id);
				if (solutionDeploymentRepository.findById(id).isPresent()) {
					logger.warn("createSolutionDeployment failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Deployment exists with ID " + id);
				}
			}
			// Create a new row
			// Use path IDs
			sd.setSolutionId(solutionId);
			sd.setRevisionId(revisionId);
			// do NOT null out the deployment ID
			MLPSolutionDeployment result = solutionDeploymentRepository.save(sd);
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.SOLUTION_PATH + "/" + sd.getSolutionId() + "/" + CCDSConstants.REVISION_PATH
							+ revisionId + CCDSConstants.DEPLOY_PATH + "/" + sd.getDeploymentId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.error("createSolutionDeployment failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "createSolutionDeployment failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing deployment record with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH + "/{deploymentId}", method = RequestMethod.PUT)
	public MLPTransportModel updateSolutionDeployment(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("deploymentId") String deploymentId,
			@RequestBody MLPSolutionDeployment sd, HttpServletResponse response) {
		logger.debug("updateSolutionDeployment: solutionId {} revisionId {} deploymentId {}", solutionId, revisionId,
				deploymentId);
		if (!solutionDeploymentRepository.findById(deploymentId).isPresent()) {
			logger.warn("updateSolutionDeployment failed on ID {}", deploymentId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + deploymentId, null);
		}
		try {
			// Create a new row
			// Use path IDs
			sd.setSolutionId(solutionId);
			sd.setRevisionId(revisionId);
			sd.setDeploymentId(deploymentId);
			solutionDeploymentRepository.save(sd);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolutionDeployment failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionDeployment failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the deployment record with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH + "/{deploymentId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSolutionDeployment(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("deploymentId") String deploymentId,
			HttpServletResponse response) {
		logger.debug("updateSolutionDeployment: solutionId {} revisionId {} deploymentId {}", solutionId, revisionId,
				deploymentId);
		try {
			solutionDeploymentRepository.deleteById(deploymentId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionDeployment failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionDeployment failed", ex);
		}
	}

	@ApiOperation(value = "Gets all child solution IDs used in the specified composite solution. Answers empty if none are found.", //
			response = String.class, responseContainer = "List")
	@RequestMapping(value = "/{parentId}/" + CCDSConstants.COMPOSITE_PATH, method = RequestMethod.GET)
	public Iterable<String> getCompositeSolutionMembers(@PathVariable("parentId") String parentId) {
		logger.debug("getCompositeSolutionMembers: parentId {}", parentId);
		Iterable<MLPCompSolMap> result = compSolMapRepository.findByParentId(parentId);
		List<String> children = new ArrayList<>();
		Iterator<MLPCompSolMap> kids = result.iterator();
		while (kids.hasNext())
			children.add(kids.next().getChildId());
		return children;
	}

	@ApiOperation(value = "Adds a child to the parent composite solution. Returns bad request if an ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{parentId}/" + CCDSConstants.COMPOSITE_PATH + "/{childId}", method = RequestMethod.POST)
	public MLPTransportModel addCompositeSolutionMember(@PathVariable("parentId") String parentId,
			@PathVariable("childId") String childId, HttpServletResponse response) {
		logger.debug("addCompositeSolutionMember: parentId {} childId {}", parentId, childId);
		if (!solutionRepository.findById(parentId).isPresent()) {
			logger.warn("addCompositeSolutionMember failed on parent ID {}", parentId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + parentId, null);
		}
		if (!solutionRepository.findById(childId).isPresent()) {
			logger.warn("addCompositeSolutionMember failed on child ID {}", childId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + childId, null);
		}
		compSolMapRepository.save(new MLPCompSolMap(parentId, childId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Drops a child from the parent composite solution.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = SuccessTransport.class) })
	@RequestMapping(value = "/{parentId}/" + CCDSConstants.COMPOSITE_PATH + "/{childId}", method = RequestMethod.DELETE)
	public SuccessTransport dropCompositeSolutionMember(@PathVariable("parentId") String parentId,
			@PathVariable("childId") String childId) {
		logger.debug("dropCompositeSolutionMember: parentId {} childId {}", parentId, childId);
		compSolMapRepository.delete(new MLPCompSolMap(parentId, childId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Gets the image for the specified solution ID. Returns null if the ID is not found.", //
			response = MLPSolution.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = byte[].class) })
	@RequestMapping(value = "/{solutionId}/"
			+ CCDSConstants.PICTURE_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Object getSolutionPicture(@PathVariable("solutionId") String solutionId) {
		logger.debug("getSolutionPicture: ID {}", solutionId);
		Optional<MLPSolutionPicture> da = solutionPictureRepository.findById(solutionId);
		return da.isPresent() ? da.get().getPicture() : null;
	}

	@ApiOperation(value = "Saves a solution image. Returns bad request if the ID is not found or the image is too large.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.PICTURE_PATH, method = RequestMethod.PUT)
	public Object saveSolutionPicture(@PathVariable("solutionId") String solutionId,
			@RequestBody(required = false) byte[] picture, HttpServletResponse response) {
		logger.debug("saveSolutionPicture: ID {} pic len {}", solutionId, picture == null ? -1 : picture.length);
		Optional<MLPSolutionPicture> da = solutionPictureRepository.findById(solutionId);
		if (!da.isPresent()) {
			logger.warn("saveSolutionPicture failed on ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		try {
			MLPSolutionPicture pic = da.get();
			pic.setPicture(picture);
			solutionPictureRepository.save(pic);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("saveSolutionPicture failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "saveSolutionPicture failed", cve);
		}
	}

}