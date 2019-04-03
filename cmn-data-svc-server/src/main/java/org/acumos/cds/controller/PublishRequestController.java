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
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPPublishRequest_;
import org.acumos.cds.repository.PublishRequestRepository;
import org.acumos.cds.service.PublishRequestSearchService;
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
 * Answers REST requests to get, add, update and delete publication requests.
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
@RequestMapping(value = "/" + CCDSConstants.PUBLISH_REQUEST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class PublishRequestController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private PublishRequestRepository publishRequestRepository;
	@Autowired
	private PublishRequestSearchService publishRequestSearchService;

	@ApiOperation(value = "Gets a page of publish requests, optionally sorted on fields. Returns empty if none are found.", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPPublishRequest> getPublishRequests(Pageable pageRequest) {
		logger.debug("getPublishRequests {}", pageRequest);
		return publishRequestRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the publish request for the specified ID. Returns null if the ID is not found.", //
			response = MLPPublishRequest.class)
	@RequestMapping(value = "/{requestId}", method = RequestMethod.GET)
	public MLPResponse getPublishRequest(@PathVariable("requestId") long requestId) {
		logger.debug("getPublishRequest: requestId {}", requestId);
		Optional<MLPPublishRequest> sr = publishRequestRepository.findById(requestId);
		return sr.isPresent() ? sr.get() : null;
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI.
	 */
	@ApiOperation(value = "Searches for publish requests with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchPublishRequests(@ApiParam(value = "Junction", allowableValues = "a,o") //
	@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@RequestParam(name = MLPPublishRequest_.SOLUTION_ID, required = false) String solutionId, //
			@RequestParam(name = MLPPublishRequest_.REVISION_ID, required = false) String revisionId, //
			@RequestParam(name = MLPPublishRequest_.REQUEST_USER_ID, required = false) String requestUserId, //
			@RequestParam(name = MLPPublishRequest_.REVIEW_USER_ID, required = false) String reviewUserId, //
			@RequestParam(name = MLPPublishRequest_.STATUS_CODE, required = false) String statusCode, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchPublishRequests enter");
		boolean isOr = junction != null && "o".equals(junction);
		if (solutionId == null && revisionId == null && requestUserId == null && reviewUserId == null
				&& statusCode == null) {
			logger.warn("searchPublishRequests missing query");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return publishRequestSearchService.findPublishRequests(solutionId, revisionId, requestUserId, reviewUserId,
					statusCode, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchPublishRequests failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchPublishRequests failed", ex);
		}
	}

	@ApiOperation(value = "Creates a new publish request with a generated ID. Returns bad request on constraint violation etc.", //
			response = MLPPublishRequest.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createPublishRequest(@RequestBody MLPPublishRequest pubReq, HttpServletResponse response) {
		logger.debug("createPublishRequest: enter");
		try {
			// Validate enum codes
			super.validateCode(pubReq.getStatusCode(), CodeNameType.PUBLISH_REQUEST_STATUS);
			// Force creation of new ID
			pubReq.setRequestId(null);
			MLPPublishRequest result = publishRequestRepository.save(pubReq);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.PUBLISH_REQUEST_PATH + "/" + result.getRequestId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPublishRequest took exception {} on data {}", cve.toString(), pubReq.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPublishRequest failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing publish request with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{requestId}", method = RequestMethod.PUT)
	public MLPTransportModel updatePublishRequest(@PathVariable("requestId") long requestId,
			@RequestBody MLPPublishRequest pubReq, HttpServletResponse response) {
		logger.debug("updatePublishRequest: requestId {}", requestId);
		// Check the existing one
		if (!publishRequestRepository.findById(requestId).isPresent()) {
			logger.warn("updatePublishRequest failed on ID {}", requestId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + requestId, null);
		}
		try {
			super.validateCode(pubReq.getStatusCode(), CodeNameType.PUBLISH_REQUEST_STATUS);
			// Use the path-parameter id; don't trust the one in the object
			pubReq.setRequestId(requestId);
			// Update the existing row
			publishRequestRepository.save(pubReq);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updatePublishRequest took exception {} on data {}", cve.toString(), pubReq.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePublishRequest failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the publish request with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{requestId}", method = RequestMethod.DELETE)
	public MLPTransportModel deletePublishRequest(@PathVariable("requestId") long requestId,
			HttpServletResponse response) {
		logger.debug("deletePublishRequest: requestId {}", requestId);
		try {
			publishRequestRepository.deleteById(requestId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deletePublishRequest failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePublishRequest failed", ex);
		}
	}

}
