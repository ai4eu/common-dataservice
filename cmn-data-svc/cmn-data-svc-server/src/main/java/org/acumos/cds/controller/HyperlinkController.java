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
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPHyperlink;
import org.acumos.cds.repository.HyperlinkRepository;
import org.acumos.cds.service.HyperlinkService;
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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A hyperlink is a URI towards an external resource pointed by a revision.
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.HYPERLINK_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class HyperlinkController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private HyperlinkRepository hyperlinkRepository;
	@Autowired
	private HyperlinkService hyperlinkService;

	@ApiOperation(value = "Gets the count of hyperlinks.", response = CountTransport.class)
	@RequestMapping(value = "/" + CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getHyperlinkCount() {
		logger.debug("getHyperlinkCount");
		long count = hyperlinkRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets the hyperlink for the specified ID. Returns null if the ID is not found.", //
			response = MLPHyperlink.class)
	@RequestMapping(value = "/{hyperlinkId}", method = RequestMethod.GET)
	public MLPHyperlink getHyperlink(@PathVariable("hyperlinkId") String hyperlinkId) {
		logger.debug("getHyperlink: ID {}", hyperlinkId);
		Optional<MLPHyperlink> da = hyperlinkRepository.findById(hyperlinkId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Gets a page of hyperlinks, optionally sorted. Answers empty if none are found.", response = MLPHyperlink.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPHyperlink> getHyperlinks(Pageable pageable) {
		logger.debug("getHyperlinks {}", pageable);
		return hyperlinkRepository.findAll(pageable);
	}

	@ApiOperation(value = "Searches for hyperlinks with names or descriptions that contain the search term using the like operator. Answers empty if none are found.", //
			response = MLPHyperlink.class, responseContainer = "Page")
	@RequestMapping(value = "/" + CCDSConstants.LIKE_PATH, method = RequestMethod.GET)
	public Page<MLPHyperlink> findHyperlinksBySearchTerm(@RequestParam(CCDSConstants.TERM_PATH) String term,
			Pageable pageRequest) {
		logger.debug("findHyperlinksBySearchTerm {}", term);
		return hyperlinkRepository.findBySearchTerm(term, pageRequest);
	}

	
	@ApiOperation(value = "Creates a new hyperlink and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPHyperlink.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createHyperlink(@RequestBody MLPHyperlink hyperlink, HttpServletResponse response) {
		logger.debug("createHyperlink: enter");
		try {
			String id = hyperlink.getHyperlinkId();
			if (id != null) {
				UUID.fromString(id);
				if (hyperlinkRepository.findById(id).isPresent()) {
					logger.warn("createHyperlink failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Hyperlink exists with ID " + id);
				}
			}
			// Create a new row
			// ALSO send back the model for client convenience
			MLPHyperlink persisted = hyperlinkRepository.save(hyperlink);
			// This is a hack to create the location path.
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.HYPERLINK_PATH + "/" + persisted.getHyperlinkId());
			return persisted;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createHyperlink took exception {} on data {}", cve.toString(), hyperlink.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createHyperlink failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing hyperlink with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{hyperlinkId}", method = RequestMethod.PUT)
	public MLPTransportModel updateHyperlink(@PathVariable("hyperlinkId") String hyperlinkId,
			@RequestBody MLPHyperlink hyperlink, HttpServletResponse response) {
		logger.debug("updateHyperlink: ID {}", hyperlinkId);
		// Check the existing one
		if (!hyperlinkRepository.findById(hyperlinkId).isPresent()) {
			logger.warn("updateHyperlink failed on ID {}", hyperlinkId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + hyperlinkId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			hyperlink.setHyperlinkId(hyperlinkId);
			hyperlinkRepository.save(hyperlink);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateHyperlink took exception {} on data {}", cve.toString(), hyperlink.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateHyperlink failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the hyperlink with the specified ID. Cascades the delete to related entities. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{hyperlinkId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteHyperlink(@PathVariable("hyperlinkId") String hyperlinkId,
			HttpServletResponse response) {
		logger.debug("deleteHyperlink: ID {}", hyperlinkId);
		try {
			// Manually cascade the delete
			hyperlinkService.deleteHyperlink(hyperlinkId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// The most likely failure here is invalid/missing ID.
			// But if the cascade code above is incomplete then this
			// will fail with a constraint violation exception.
			Exception cve = findConstraintViolationException(ex);
			logger.warn("deleteHyperlink failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteHyperlink failed", cve);
		}
	}
}