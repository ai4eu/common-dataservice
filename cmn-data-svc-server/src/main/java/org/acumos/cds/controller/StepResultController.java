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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.repository.StepResultRepository;
import org.acumos.cds.service.StepResultSearchService;
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

@RestController
@RequestMapping(value = "/" + CCDSConstants.STEP_RESULT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class StepResultController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private StepResultRepository stepResultRepository;
	@Autowired
	private StepResultSearchService stepResultSearchService;

	@ApiOperation(value = "Gets a page of step results, optionally sorted on fields.", //
			response = MLPStepResult.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPStepResult> getStepResults(Pageable pageRequest) {
		logger.debug("getStepResults {}", pageRequest);
		return stepResultRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the step result for the specified ID. Returns bad request if the ID is not found.", //
			response = MLPStepResult.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{stepResultId}", method = RequestMethod.GET)
	public Object getStepResult(@PathVariable("stepResultId") Long stepResultId, HttpServletResponse response) {
		logger.debug("getStepResult: stepResultId {}", stepResultId);
		MLPStepResult sr = stepResultRepository.findOne(stepResultId);
		if (sr == null) {
			logger.warn("getStepResult failed on ID {}", stepResultId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + stepResultId, null);
		}
		return sr;
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many class field names.
	 */
	private static final String trackingIdField = "trackingId";
	private static final String stepCodeField = "stepCode";
	private static final String solutionIdField = "solutionId";
	private static final String revisionIdField = "revisionId";
	private static final String artifactIdField = "artifiactId";
	private static final String userIdField = "userId";
	private static final String statusCodeField = "statusCode";
	private static final String nameField = "name";

	@ApiOperation(value = "Searches for requests with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchStepResults(@ApiParam(value = "Junction", allowableValues = "a,o") //
	@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Tracking ID") //
			@RequestParam(name = trackingIdField, required = false) String trackingId, //
			@ApiParam(value = "Step code") //
			@RequestParam(name = stepCodeField, required = false) String stepCode, //
			@ApiParam(value = "Solution ID") //
			@RequestParam(name = solutionIdField, required = false) String solutionId, //
			@ApiParam(value = "Revision ID") //
			@RequestParam(name = revisionIdField, required = false) String revisionId, //
			@ApiParam(value = "Artifact ID") //
			@RequestParam(name = artifactIdField, required = false) String artifactId, //
			@ApiParam(value = "User ID") //
			@RequestParam(name = userIdField, required = false) String userId, //
			@ApiParam(value = "Name") //
			@RequestParam(name = nameField, required = false) String name, //
			@ApiParam(value = "Status code") //
			@RequestParam(name = statusCodeField, required = false) String statusCode, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchStepResults enter");
		boolean isOr = junction != null && "o".equals(junction);
		Map<String, Object> queryParameters = new HashMap<>();
		if (trackingId != null)
			queryParameters.put(trackingIdField, trackingId);
		if (stepCode != null)
			queryParameters.put(stepCodeField, stepCode);
		if (solutionId != null)
			queryParameters.put(solutionIdField, solutionId);
		if (revisionId != null)
			queryParameters.put(revisionIdField, revisionId);
		if (artifactId != null)
			queryParameters.put(artifactIdField, artifactId);
		if (userId != null)
			queryParameters.put(userIdField, userId);
		if (name != null)
			queryParameters.put(nameField, name);
		if (statusCode != null)
			queryParameters.put(statusCodeField, statusCode);
		if (queryParameters.size() == 0) {
			logger.warn("searchStepResults missing query");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return stepResultSearchService.findStepResults(queryParameters, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchStepResults failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchStepResults failed", ex);
		}
	}

	@ApiOperation(value = "Creates a new entity with a generated ID. Returns bad request on constraint violation etc.", //
			response = MLPStepResult.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public Object createStepResult(@RequestBody MLPStepResult stepResult, HttpServletResponse response) {
		logger.debug("createStepResult: enter");
		try {
			// Validate enum codes
			super.validateCode(stepResult.getStepCode(), CodeNameType.STEP_TYPE);
			super.validateCode(stepResult.getStatusCode(), CodeNameType.STEP_STATUS);
			// Force creation of new ID
			stepResult.setStepResultId(null);
			MLPStepResult result = stepResultRepository.save(stepResult);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.STEP_RESULT_PATH + "/" + result.getStepResultId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createStepResult failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createStepResult failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{stepResultId}", method = RequestMethod.PUT)
	public Object updateStepResult(@PathVariable("stepResultId") Long stepResultId,
			@RequestBody MLPStepResult stepResult, HttpServletResponse response) {
		logger.debug("updateStepResult: stepResultId {}", stepResultId);
		// Get the existing one
		MLPStepResult existing = stepResultRepository.findOne(stepResultId);
		if (existing == null) {
			logger.warn("updateStepResult failed on ID {}", stepResultId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + stepResultId, null);
		}
		try {
			// Validate enum codes
			super.validateCode(stepResult.getStepCode(), CodeNameType.STEP_TYPE);
			super.validateCode(stepResult.getStatusCode(), CodeNameType.STEP_STATUS);
			// Use the path-parameter id; don't trust the one in the object
			stepResult.setStepResultId(stepResultId);
			// Update the existing row
			stepResultRepository.save(stepResult);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateStepResult failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateStepResult failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{stepResultId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteStepResult(@PathVariable("stepResultId") Long stepResultId,
			HttpServletResponse response) {
		logger.debug("deleteStepResult: stepResultId {}", stepResultId);
		try {
			stepResultRepository.delete(stepResultId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteStepResult failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteStepResult failed", ex);
		}
	}

}
