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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCompSolMap;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRating.SolutionRatingPK;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionValidation;
import org.acumos.cds.domain.MLPSolutionValidation.SolutionValidationPK;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.CompSolMapRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolTagMapRepository;
import org.acumos.cds.repository.SolUserAccMapRepository;
import org.acumos.cds.repository.SolutionDeploymentRepository;
import org.acumos.cds.repository.SolutionDownloadRepository;
import org.acumos.cds.repository.SolutionFavoriteRepository;
import org.acumos.cds.repository.SolutionRatingRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.SolutionValidationRepository;
import org.acumos.cds.repository.SolutionWebRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A solution is a collection of revisions. A revision points to a collection of
 * artifacts. A revision cannot exist without a solution, but a solution can
 * exist without a revision (altho it will not be found by searches).
 */
@Controller
@RequestMapping(value = "/" + CCDSConstants.SOLUTION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class SolutionController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
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
	private SolutionRevisionRepository solutionRevisionRepository;
	@Autowired
	private SolutionSearchService solutionSearchService;
	@Autowired
	private SolutionValidationRepository solutionValidationRepository;
	@Autowired
	private SolutionWebRepository solutionWebRepository;
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
		Long count = solutionDownloadRepository.getSolutionDownloadCount(solutionId);
		if (count != null) {
			MLPSolutionWeb stats = solutionWebRepository.findOne(solutionId);
			stats.setDownloadCount(count);
			stats.setLastDownload(new Date());
			solutionWebRepository.save(stats);
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
		Long count = solutionRatingRepository.getSolutionRatingCount(solutionId);
		Double avg = solutionRatingRepository.getSolutionRatingAverage(solutionId);
		if (count != null && avg != null) {
			MLPSolutionWeb stats = solutionWebRepository.findOne(solutionId);
			stats.setRatingCount(count);
			stats.setRatingAverageTenths(Math.round(10 * avg));
			solutionWebRepository.save(stats);
		}
	}

	@ApiOperation(value = "Gets the count of solutions.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public CountTransport getSolutionCount() {
		logger.info("getSolutionCount");
		Long count = solutionRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of solutions, optionally sorted.", response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPSolution> getSolutions(Pageable pageable, HttpServletResponse response) {
		logger.info("getSolutions {}", pageable);
		return solutionRepository.findAll(pageable);
	}

	@ApiOperation(value = "Searches for entities with names or descriptions that contain the search term using the like operator.", //
			response = MLPSolution.class, responseContainer = "Page")
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.LIKE_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPSolution> findSolutionsByLikeKeyword(@RequestParam(CCDSConstants.TERM_PATH) String term,
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("findSolutionsByLikeKeyword {}", term);
		return solutionRepository.findBySearchTerm(term, pageRequest);
	}

	@ApiOperation(value = "Gets a page of solutions matching the specified tag.", response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.TAG_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object findSolutionsByTag(@RequestParam("tag") String tag, Pageable pageRequest,
			HttpServletResponse response) {
		logger.info("findSolutionsByTag {}", tag);
		return solutionRepository.findByTag(tag, pageRequest);
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many class field names.
	 */
	private static final String nameField = "name";
	private static final String activeField = "active";
	private static final String userIdField = "userId";
	private static final String sourceIdField = "sourceId";
	private static final String modelTypeCodeField = "modelTypeCode";
	private static final String toolkitTypeCodeField = "toolkitTypeCode";
	private static final String originField = "origin";

	@ApiOperation(value = "Searches for peers with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object searchSolutions( //
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Name") //
			@RequestParam(name = nameField, required = false) String name, //
			@ApiParam(value = "Active") //
			@RequestParam(name = activeField, required = false) Boolean active, //
			@ApiParam(value = "User ID") //
			@RequestParam(name = userIdField, required = false) String userId, //
			@ApiParam(value = "Source ID") //
			@RequestParam(name = sourceIdField, required = false) String sourceId, //
			@ApiParam(value = "Model type code") //
			@RequestParam(name = modelTypeCodeField, required = false) String modelTypeCode, //
			@ApiParam(value = "Toolkit type code") //
			@RequestParam(name = toolkitTypeCodeField, required = false) String toolkitTypeCode, //
			@ApiParam(value = "Origin URI") //
			@RequestParam(name = originField, required = false) String origin, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("searchSolutions enter");
		boolean isOr = junction != null && "o".equals(junction);
		Map<String, Object> queryParameters = new HashMap<>();
		if (name != null)
			queryParameters.put(nameField, name);
		if (active != null)
			queryParameters.put(activeField, active);
		if (userId != null)
			queryParameters.put(userIdField, userId);
		if (sourceId != null)
			queryParameters.put(sourceIdField, sourceId);
		if (modelTypeCode != null)
			queryParameters.put(modelTypeCodeField, modelTypeCode);
		if (toolkitTypeCode != null)
			queryParameters.put(toolkitTypeCodeField, toolkitTypeCode);
		if (origin != null)
			queryParameters.put(originField, origin);
		if (queryParameters.size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return solutionSearchService.findSolutions(queryParameters, isOr, pageRequest);
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
	@ResponseBody
	public Object findPortalSolutions( //
			@ApiParam(value = "Active Y/N", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = true) boolean active, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			@ApiParam(value = "Model type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_MODEL_TYPES, required = false) String[] modelTypeCodes, //
			@ApiParam(value = "Validation status codes (deprecated)", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_VAL_STATUSES, required = false) String[] valStatusCodes, //
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
		logger.info("findPortalSolutions: active {} nameKws {}", active, nameKws);
		try {
			return solutionSearchService.findPortalSolutions(nameKws, descKws, active, userIds, modelTypeCodes,
					accTypeCodes, valStatusCodes, tags, authKws, pubKws, pageRequest);
		} catch (Exception ex) {
			logger.error("findPortalSolutions failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findPortalSolutions failed", ex);
		}
	}

	@ApiOperation(value = "Finds solutions matching the specified attribute values and/or child attribute values. " //
			+ "Checks multiple fields for the supplied keywords, including ID, name, description etc.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.PORTAL_PATH + "/"
			+ CCDSConstants.KEYWORD_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object findPortalSolutionsByKw( //
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
			@ApiParam(value = "Tags", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_TAGS, required = false) String[] tags, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("findPortalSolutionsByKw: active {} kw {}", active, kws);
		try {
			return solutionSearchService.findPortalSolutionsByKw(kws, active, userIds, modelTypeCodes, accTypeCodes,
					tags, pageRequest);
		} catch (Exception ex) {
			logger.error("findPortalSolutionsByKw failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findPortalSolutionsByKw failed", ex);
		}
	}

	@ApiOperation(value = "Finds solutions matching the specified attribute values and/or child attribute values " //
			+ " with flexible handling of tags to allow all/any matches. "
			+ " Checks multiple fields for the supplied keywords, including ID, name, description etc.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH + "/" + CCDSConstants.PORTAL_PATH + "/"
			+ CCDSConstants.KW_TAG_PATH, method = RequestMethod.GET)
	@ResponseBody
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
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("findPortalSolutionsByKwAndTags: active {} kw {}", active, kws);
		try {
			return solutionSearchService.findPortalSolutionsByKwAndTags(kws, active, userIds, modelTypeCodes,
					accTypeCodes, allTags, anyTags, pageRequest);
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
	@ResponseBody
	public Object findUserSolutions( //
			@ApiParam(value = "Active Y/N", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = true) boolean active, //
			@ApiParam(value = "User ID", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_USERS, required = true) String userId, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			@ApiParam(value = "Model type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_MODEL_TYPES, required = false) String[] modelTypeCodes, //
			@ApiParam(value = "Validation status codes (deprecated)", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_VAL_STATUSES, required = false) String[] valStatusCodes, //
			@ApiParam(value = "Name key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_NAME, required = false) String[] nameKws, //
			@ApiParam(value = "Description key words", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_DESC, required = false) String[] descKws, //
			@ApiParam(value = "Tags", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_TAGS, required = false) String[] tags, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("findUserSolutions: active {} userId {}", active, userId);
		try {
			return solutionSearchService.findUserSolutions(nameKws, descKws, active, userId, modelTypeCodes,
					accTypeCodes, valStatusCodes, tags, pageRequest);
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
	@ResponseBody
	public Object findSolutionsByDate( //
			@ApiParam(value = "Milliseconds since the Epoch", required = true) //
			@RequestParam(name = CCDSConstants.SEARCH_DATE, required = true) long dateMillis, //
			@ApiParam(value = "Active Y/N") //
			@RequestParam(name = CCDSConstants.SEARCH_ACTIVE, required = false) boolean active, //
			@ApiParam(value = "Access type codes", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_ACCESS_TYPES, required = false) String[] accTypeCodes, //
			@ApiParam(value = "Validation status codes (deprecated)", allowMultiple = true) //
			@RequestParam(name = CCDSConstants.SEARCH_VAL_STATUSES, required = false) String[] valStatusCodes, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("findSolutionsByDate: date {}", dateMillis);
		Date date = new Date(dateMillis);
		try {
			return solutionSearchService.findSolutionsByModifiedDate(active, accTypeCodes, valStatusCodes, date,
					pageRequest);
		} catch (Exception ex) {
			logger.error("findSolutionsByDate failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "findSolutionsByDate failed", ex);
		}

	}

	@ApiOperation(value = "Gets the solution for the specified ID. Returns bad request if the ID is not found.", //
			response = MLPSolution.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getSolution(@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		logger.info("getSolution: ID {}", solutionId);
		MLPSolution da = solutionRepository.findOne(solutionId);
		if (da == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		return da;
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPSolution.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createSolution(@RequestBody MLPSolution solution, HttpServletResponse response) {
		logger.info("createSolution: enter");
		try {
			// Validate enum codes
			if (solution.getModelTypeCode() != null)
				super.validateCode(solution.getModelTypeCode(), CodeNameType.MODEL_TYPE);
			if (solution.getToolkitTypeCode() != null)
				super.validateCode(solution.getToolkitTypeCode(), CodeNameType.TOOLKIT_TYPE);
			String id = solution.getSolutionId();
			if (id != null) {
				UUID.fromString(id);
				if (solutionRepository.findOne(id) != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Solution exists with ID " + id);
				}
			}
			// Ensure web stat object is empty
			solution.setWebStats(null);
			// Cascade manually - create user-supplied tags as needed
			createMissingTags(solution.getTags());
			// Create a new row
			// ALSO send back the model for client convenience
			MLPSolution persisted = solutionRepository.save(solution);
			// Cascade manually - create an empty web stats entry.
			solutionWebRepository.save(new MLPSolutionWeb(persisted.getSolutionId()));
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
	@ResponseBody
	public Object updateSolution(@PathVariable("solutionId") String solutionId, @RequestBody MLPSolution solution,
			HttpServletResponse response) {
		logger.info("updateSolution: ID {}", solutionId);
		// Get the existing one
		MLPSolution existing = solutionRepository.findOne(solutionId);
		if (existing == null) {
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
			// Discard any stats object; updates don't happen via this interface
			solution.setWebStats(null);
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
	@ResponseBody
	public Object incrementViewCount(@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		logger.info("incrementViewCount: ID {}", solutionId);
		// Get the existing one; the update command doesn't fail on invalid ID
		MLPSolutionWeb existing = solutionWebRepository.findOne(solutionId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		try {
			// Have the database do the increment to avoid race conditions
			solutionWebRepository.incrementViewCount(solutionId);
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
	@ResponseBody
	public MLPTransportModel deleteSolution(@PathVariable("solutionId") String solutionId,
			HttpServletResponse response) {
		logger.info("deleteSolution: ID {}", solutionId);
		try {
			// Manually cascade the delete
			solutionDeploymentRepository.deleteBySolutionId(solutionId);
			compSolMapRepository.deleteByParentId(solutionId);
			solTagMapRepository.deleteBySolutionId(solutionId);
			solutionDownloadRepository.deleteBySolutionId(solutionId);
			solutionRatingRepository.deleteBySolutionId(solutionId);
			solutionValidationRepository.deleteBySolutionId(solutionId);
			solUserAccMapRepository.deleteBySolutionId(solutionId);
			solutionFavoriteRepository.deleteBySolutionId(solutionId);
			stepResultRepository.deleteBySolutionId(solutionId);
			// The web stats are annotated as optional, so be cautious when deleting
			MLPSolutionWeb webStats = solutionWebRepository.findOne(solutionId);
			if (webStats != null)
				solutionWebRepository.delete(solutionId);
			for (MLPSolutionRevision r : solutionRevisionRepository.findBySolutionIdIn(new String[] { solutionId })) {
				for (MLPArtifact a : artifactRepository.findByRevision(r.getRevisionId()))
					solRevArtMapRepository
							.delete(new MLPSolRevArtMap.SolRevArtMapPK(r.getRevisionId(), a.getArtifactId()));
				// do NOT delete artifacts!
				solutionRevisionRepository.delete(r);
			}
			solutionRepository.delete(solutionId);
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
	@ApiOperation(value = "Gets a list of revisions for the specified solution IDs.", //
			response = MLPSolutionRevision.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Iterable<MLPSolutionRevision> getListOfRevisions(@PathVariable("solutionId") String[] solutionIds) {
		logger.info("getListOfRevisions: solution IDs {}", Arrays.toString(solutionIds));
		return solutionRevisionRepository.findBySolutionIdIn(solutionIds);
	}

	@ApiOperation(value = "Gets the revision for the specified ID. Returns bad request if the ID is not found.", //
			response = MLPSolution.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionRevision(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, HttpServletResponse response) {
		logger.info("getSolutionRevision: solutionId {} revisionId {}", solutionId, revisionId);
		MLPSolutionRevision da = solutionRevisionRepository.findOne(revisionId);
		if (da == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		return da;
	}

	@ApiOperation(value = "Creates a new solution revision and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPSolutionRevision.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object createSolutionRevision(@PathVariable("solutionId") String solutionId,
			@RequestBody MLPSolutionRevision revision, HttpServletResponse response) {
		logger.info("createSolutionRevision: solutionId {}", solutionId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		try {
			// Validate enum codes
			if (revision.getAccessTypeCode() != null)
				super.validateCode(revision.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			if (revision.getValidationStatusCode() != null)
				super.validateCode(revision.getValidationStatusCode(), CodeNameType.VALIDATION_STATUS);
			String id = revision.getRevisionId();
			if (id != null) {
				UUID.fromString(id);
				if (solutionRevisionRepository.findOne(id) != null) {
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

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateSolutionRevision(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @RequestBody MLPSolutionRevision revision,
			HttpServletResponse response) {
		logger.info("updateSolutionRevision: solution ID {}, revision ID {}", solutionId, revisionId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solutionRevisionRepository.findOne(revisionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		try {
			// Validate enum codes
			if (revision.getAccessTypeCode() != null)
				super.validateCode(revision.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			if (revision.getValidationStatusCode() != null)
				super.validateCode(revision.getValidationStatusCode(), CodeNameType.VALIDATION_STATUS);
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

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object deleteSolutionRevision(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, HttpServletResponse response) {
		logger.info("deleteSolutionRevision: solutionId {} revisionId {}", solutionId, revisionId);
		try {
			solutionRevisionRepository.delete(revisionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionRevision failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRevision failed", ex);
		}
	}

	@ApiOperation(value = "Gets a list of tags for the specified solution.", response = MLPTag.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.TAG_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Iterable<MLPTag> getTagsForSolution(@PathVariable("solutionId") String solutionId) {
		logger.info("getTagsForSolution: solutionId {}", solutionId);
		return tagRepository.findBySolution(solutionId);
	}

	@ApiOperation(value = "Adds a tag to the solution. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.TAG_PATH + "/{tag}", method = RequestMethod.POST)
	@ResponseBody
	public Object addSolutionTag(@PathVariable("solutionId") String solutionId, @PathVariable("tag") String tag,
			HttpServletResponse response) {
		logger.info("addSolutionTag: solutionId {} tag {}", solutionId, tag);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		} else if (solTagMapRepository.findOne(new MLPSolTagMap.SolTagMapPK(solutionId, tag)) != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Already has tag " + tag, null);
		}
		if (tagRepository.findOne(tag) == null) {
			// Tags are cheap & easy to create, so make life easy for client
			tagRepository.save(new MLPTag(tag));
			logger.info("addSolutionTag: created tag {}", tag);
		}
		solTagMapRepository.save(new MLPSolTagMap(solutionId, tag));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Drops a tag from the solution. Returns bad request if not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.TAG_PATH + "/{tag}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropSolutionTag(@PathVariable("solutionId") String solutionId, @PathVariable("tag") String tag,
			HttpServletResponse response) {
		logger.info("dropSolutionTag: solutionId {} tag {}", solutionId, tag);
		try {
			solTagMapRepository.delete(new MLPSolTagMap.SolTagMapPK(solutionId, tag));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropSolutionTag failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionTag failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of download records for the specified solution ID. Returns bad request if the ID is not found.", //
			response = MLPSolutionDownload.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.DOWNLOAD_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionDownloads(@PathVariable("solutionId") String solutionId, Pageable pageRequest,
			HttpServletResponse response) {
		logger.info("getSolutionDownloads: solutionId {}", solutionId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		return solutionDownloadRepository.findBySolutionId(solutionId, pageRequest);
	}

	@ApiOperation(value = "Creates a new solution download record. Returns bad request on constraint violation etc.", //
			response = MLPSolutionDownload.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.DOWNLOAD_PATH + "/" + CCDSConstants.ARTIFACT_PATH
			+ "/{artifactId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public Object createSolutionDownload(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, @PathVariable("artifactId") String artifactId,
			@RequestBody MLPSolutionDownload sd, HttpServletResponse response) {
		logger.info("createSolutionDownload: solutionId {} userId {} artifactId {}", solutionId, userId, artifactId);
		try {
			// Create a new row using path IDs
			sd.setSolutionId(solutionId);
			sd.setUserId(userId);
			sd.setArtifactId(artifactId);
			Object result = solutionDownloadRepository.save(sd);
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

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.DOWNLOAD_PATH
			+ "/{downloadId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteSolutionDownload(@PathVariable("solutionId") String solutionId,
			@PathVariable("downloadId") Long downloadId, HttpServletResponse response) {
		logger.info("deleteSolutionDownload: solutionId {} downloadId {}", solutionId, downloadId);
		try { // Build a key for fetch
			solutionDownloadRepository.delete(downloadId);
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

	@ApiOperation(value = "Gets all user ratings for the specified solution. Returns bad request if the ID is not found.", //
			response = MLPSolutionRating.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getListOfSolutionRating(@PathVariable("solutionId") String solutionId, Pageable pageRequest,
			HttpServletResponse response) {
		logger.info("getListOfSolutionRating: solutionId {}", solutionId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		return solutionRatingRepository.findBySolutionId(solutionId, pageRequest);
	}

	@ApiOperation(value = "Gets an existing solution rating. Returns bad request if the ID is not found", //
			response = MLPSolutionRating.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.info("getSolutionRating: solutionId {} userId {}", solutionId, userId);
		SolutionRatingPK pk = new SolutionRatingPK(solutionId, userId);
		MLPSolutionRating da = solutionRatingRepository.findOne(pk);
		if (da == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + pk);
		}
		return da;
	}

	@ApiOperation(value = "Creates a new solution rating. Returns bad request on constrain violation etc.", response = MLPSolutionRating.class)
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public Object createSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, @RequestBody MLPSolutionRating sr, HttpServletResponse response) {
		logger.info("createSolutionRating: solutionId {} userId {}", solutionId, userId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (userRepository.findOne(userId) == null) {
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
			// e.g., EmptyResultDataAccessException is NOT an internal server error
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
	@ResponseBody
	public Object updateSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, @RequestBody MLPSolutionRating sr, HttpServletResponse response) {
		logger.info("updateSolutionRating: solutionId {} userId {}", solutionId, userId);
		// Get the existing one
		SolutionRatingPK pk = new SolutionRatingPK(solutionId, userId);
		MLPSolutionRating existing = solutionRatingRepository.findOne(pk);
		if (existing == null) {
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
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolutionRating failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionRating failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.RATING_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteSolutionRating(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.info("deleteSolutionRating: solutionId {} userId {}", solutionId, userId);
		try {
			// Build a key for fetch
			SolutionRatingPK pk = new SolutionRatingPK(solutionId, userId);
			solutionRatingRepository.delete(pk);
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

	@ApiOperation(value = "Gets web metadata for the specified solution including average rating and total download count. Returns bad request if the ID is not found.", //
			response = MLPSolutionWeb.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.WEB_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionWebStats(@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		logger.info("getSolutionWebStats: solutionId {}", solutionId);
		MLPSolutionWeb stats = solutionWebRepository.findOne(solutionId);
		if (stats == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		return stats;
	}

	@ApiOperation(value = "Gets access-control list of users for the specified solution.", //
			response = MLPUser.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.USER_PATH + "/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Iterable<MLPUser> getSolutionACL(@PathVariable("solutionId") String solutionId) {
		logger.info("getSolutionACL: solutionId {}", solutionId);
		return solUserAccMapRepository.getUsersForSolution(solutionId);
	}

	@ApiOperation(value = "Adds a user to the ACL for the specified solution. Returns bad request if an ID is not found", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object addUserToSolutionACL(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.info("addUserToSolutionACL: solution {}, user {}", solutionId, userId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		} else if (userRepository.findOne(userId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		} else {
			solUserAccMapRepository.save(new MLPSolUserAccMap(solutionId, userId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		}
	}

	@ApiOperation(value = "Drops a user from the ACL for the specified solution. Returns bad request if an ID is not found", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropUserFromSolutionACL(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.info("dropUserFromSolutionACL: solution {}, user {}", solutionId, userId);
		try {
			solUserAccMapRepository.delete(new MLPSolUserAccMap.SolUserAccessMapPK(solutionId, userId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropUserFromSolutionACL failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropUserFromSolutionACL failed", ex);
		}

	}

	@ApiOperation(value = "Gets a page of solutions with the specified user in the ACL, optionally sorted on fields.", //
			response = MLPSolution.class, responseContainer = "List")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPSolution> getAccessibleSolutions(@PathVariable("userId") String userId, Pageable pageable,
			HttpServletResponse response) {
		logger.info("getAccessibleSolutions: user {}", userId);
		return solUserAccMapRepository.getSolutionsForUser(userId, pageable);
	}

	@ApiOperation(value = "Gets validation results for the specified solution and revision. Returns bad request if an ID is not found", //
			response = MLPSolutionValidation.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.VALIDATION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getListOfSolutionValidations(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, HttpServletResponse response) {
		logger.info("getListOfSolutionValidations: solutionId {} revisionId {}", solutionId, revisionId);
		Iterable<MLPSolutionValidation> items = solutionValidationRepository.findBySolutionIdAndRevisionId(solutionId,
				revisionId);
		if (items == null || !items.iterator().hasNext()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					NO_ENTRY_WITH_ID + solutionId + ", " + revisionId, null);
		}
		return items;
	}

	@ApiOperation(value = "Creates a new solution validation record. Returns bad request if an ID is not found.", response = MLPSolutionValidation.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.VALIDATION_PATH + "/{taskId}", method = RequestMethod.POST)
	@ResponseBody
	public Object createSolutionValidation(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("taskId") String taskId,
			@RequestBody MLPSolutionValidation sv, HttpServletResponse response) {
		logger.info("createSolutionValidation: solutionId {} revisionId {} taskId {}", solutionId, revisionId, taskId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solutionRevisionRepository.findOne(revisionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		try {
			// Validate enum codes
			if (sv.getValidationStatusCode() != null)
				super.validateCode(sv.getValidationStatusCode(), CodeNameType.VALIDATION_STATUS);
			// type is required
			super.validateCode(sv.getValidationTypeCode(), CodeNameType.VALIDATION_TYPE);
			// Use path IDs
			sv.setSolutionId(solutionId);
			sv.setRevisionId(revisionId);
			sv.setTaskId(taskId);
			Object result = solutionValidationRepository.save(sv);
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.SOLUTION_PATH + "/" + solutionId + "/" + CCDSConstants.REVISION_PATH + "/"
							+ revisionId + "/" + CCDSConstants.VALIDATION_PATH + "/" + taskId);
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSolutionValidation failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolutionValidation failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.VALIDATION_PATH + "/{taskId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateSolutionValidation(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("taskId") String taskId,
			@RequestBody MLPSolutionValidation sv, HttpServletResponse response) {
		logger.info("updateSolutionValidation: solutionId {} revisionId {} taskId {}", solutionId, revisionId, taskId);
		// Get the existing one
		SolutionValidationPK pk = new SolutionValidationPK(solutionId, revisionId, taskId);
		MLPSolutionValidation existing = solutionValidationRepository.findOne(pk);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + pk, null);
		}
		try {
			// Validate enum codes
			if (sv.getValidationStatusCode() != null)
				super.validateCode(sv.getValidationStatusCode(), CodeNameType.VALIDATION_STATUS);
			// type is required
			super.validateCode(sv.getValidationTypeCode(), CodeNameType.VALIDATION_TYPE);
			// Use path IDs
			sv.setSolutionId(solutionId);
			sv.setRevisionId(revisionId);
			sv.setTaskId(taskId);
			solutionValidationRepository.save(sv);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolutionValidation failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionValidation failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.VALIDATION_PATH + "/{taskId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteSolutionValidation(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("taskId") String taskId,
			HttpServletResponse response) {
		logger.info("deleteSolutionValidation: solutionId {} revisionId {} taskId {}", solutionId, revisionId, taskId);
		try {
			SolutionValidationPK pk = new SolutionValidationPK(solutionId, revisionId, taskId);
			solutionValidationRepository.delete(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionValidation failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionValidation failed", ex);
		}
	}

	@ApiOperation(value = "Gets the deployments for the specified solution and revision IDs. Returns bad request if an ID is not found.", //
			response = MLPSolutionDeployment.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionDeployments(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, Pageable pageRequest, HttpServletResponse response) {
		logger.info("getSolutionDeployments: solutionId {} revisionId {}", solutionId, revisionId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solutionRevisionRepository.findOne(revisionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		return solutionDeploymentRepository.findBySolutionIdAndRevisionId(solutionId, revisionId, pageRequest);
	}

	@ApiOperation(value = "Gets the deployments for the specified solution, revision and user IDs. Returns bad request if an ID is not found.", //
			response = MLPSolutionDeployment.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/" + CCDSConstants.USER_PATH
			+ "/{userId}/" + CCDSConstants.DEPLOY_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getUserSolutionRevisionDeployments(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("userId") String userId, Pageable pageRequest,
			HttpServletResponse response) {
		logger.info("getUserSolutionRevisionDeployments: solutionId {} revisionId {} userId {}", solutionId, revisionId,
				userId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solutionRevisionRepository.findOne(revisionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		if (userRepository.findOne(userId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		return solutionDeploymentRepository.findBySolutionIdAndRevisionIdAndUserId(solutionId, revisionId, userId,
				pageRequest);
	}

	@ApiOperation(value = "Creates a new deployment record for the specified solution and revision. Returns bad request if an ID is not found.", //
			response = MLPSolutionDeployment.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object createSolutionDeployment(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @RequestBody MLPSolutionDeployment sd,
			HttpServletResponse response) {
		logger.info("createSolutionDeployment: solutionId {} revisionId {}", solutionId, revisionId);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solutionRevisionRepository.findOne(revisionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		if (userRepository.findOne(sd.getUserId()) == null) {
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
				if (solutionDeploymentRepository.findOne(id) != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Deployment exists with ID " + id);
				}
			}
			// Create a new row
			// Use path IDs
			sd.setSolutionId(solutionId);
			sd.setRevisionId(revisionId);
			// do NOT null out the deployment ID
			Object result = solutionDeploymentRepository.save(sd);
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

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH + "/{deploymentId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateSolutionDeployment(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("deploymentId") String deploymentId,
			@RequestBody MLPSolutionDeployment sd, HttpServletResponse response) {
		logger.info("updateSolutionDeployment: solutionId {} revisionId {} deploymentId {}", solutionId, revisionId,
				deploymentId);
		if (solutionDeploymentRepository.findOne(deploymentId) == null) {
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

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{solutionId}/" + CCDSConstants.REVISION_PATH + "/{revisionId}/"
			+ CCDSConstants.DEPLOY_PATH + "/{deploymentId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteSolutionDeployment(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("deploymentId") String deploymentId,
			HttpServletResponse response) {
		logger.info("updateSolutionDeployment: solutionId {} revisionId {} deploymentId {}", solutionId, revisionId,
				deploymentId);
		try {
			solutionDeploymentRepository.delete(deploymentId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionDeployment failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionDeployment failed", ex);
		}
	}

	@ApiOperation(value = "Gets a list of child solution IDs used in the specified composite solution.", //
			response = String.class, responseContainer = "List")
	@RequestMapping(value = "/{parentId}/" + CCDSConstants.COMPOSITE_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Iterable<String> getCompositeSolutionMembers(@PathVariable("parentId") String parentId) {
		logger.info("getCompositeSolutionMembers: parentId {}", parentId);
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
	@ResponseBody
	public Object addCompositeSolutionMember(@PathVariable("parentId") String parentId,
			@PathVariable("childId") String childId, HttpServletResponse response) {
		logger.info("addCompositeSolutionMember: parentId {} childId {}", parentId, childId);
		if (solutionRepository.findOne(parentId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + parentId, null);
		} else if (solutionRepository.findOne(childId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + childId, null);
		} else {
			compSolMapRepository.save(new MLPCompSolMap(parentId, childId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		}
	}

	@ApiOperation(value = "Drops a child from the parent composite solution. Returns bad request if an ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{parentId}/" + CCDSConstants.COMPOSITE_PATH + "/{childId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropCompositeSolutionMember(@PathVariable("parentId") String parentId,
			@PathVariable("childId") String childId, HttpServletResponse response) {
		logger.info("dropCompositeSolutionMember: parentId {} childId {}", parentId, childId);
		try {
			compSolMapRepository.delete(new MLPCompSolMap(parentId, childId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropCompositeSolutionMember failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropCompositeSolutionMember failed", ex);
		}
	}

}