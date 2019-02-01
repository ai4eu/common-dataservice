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
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifact_;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.service.ArtifactSearchService;
import org.acumos.cds.service.ArtifactService;
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
 * Provides REST endpoints for managing artifacts.
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
@RequestMapping(value = "/" + CCDSConstants.ARTIFACT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtifactController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private ArtifactService artifactService;
	@Autowired
	private ArtifactSearchService artifactSearchService;
	@Autowired
	private SolutionRevisionRepository solutionRevisionRepository;

	@ApiOperation(value = "Gets the count of artifacts.", response = CountTransport.class)
	@RequestMapping(value = "/" + CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getArtifactCount() {
		logger.debug("getArtifactCount");
		long count = artifactRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of artifacts, optionally sorted; empty if none are found.", //
			response = MLPArtifact.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPArtifact> getArtifacts(Pageable pageRequest) {
		logger.debug("getArtifacts {}", pageRequest);
		return artifactRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the entity for the specified ID. Returns null if the ID is not found.", //
			response = MLPArtifact.class)
	@RequestMapping(value = "/{artifactId}", method = RequestMethod.GET)
	public MLPArtifact getArtifact(@PathVariable("artifactId") String artifactId) {
		logger.debug("getArtifact ID {}", artifactId);
		Optional<MLPArtifact> da = artifactRepository.findById(artifactId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Searches for entities with names or descriptions that contain the search term using the like operator; empty if no matches are found.", //
			response = MLPArtifact.class, responseContainer = "List")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.LIKE_PATH, method = RequestMethod.GET)
	public Page<MLPArtifact> like(@RequestParam(CCDSConstants.TERM_PATH) String term, Pageable pageRequest) {
		logger.debug("like pageRequest {}", pageRequest);
		return artifactRepository.findBySearchTerm(term, pageRequest);
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI.
	 */
	@ApiOperation(value = "Searches for artifacts with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPArtifact.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchArtifacts(//
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@RequestParam(name = MLPArtifact_.ARTIFACT_TYPE_CODE, required = false) String artifactTypeCode, //
			@RequestParam(name = MLPArtifact_.NAME, required = false) String name, //
			@RequestParam(name = MLPArtifact_.URI, required = false) String uri, //
			@RequestParam(name = MLPArtifact_.VERSION, required = false) String version, //
			@RequestParam(name = MLPArtifact_.USER_ID, required = false) String userId, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchArtifacts enter");
		boolean isOr = junction != null && "o".equals(junction);
		if (artifactTypeCode == null && name == null && uri == null && version == null && userId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return artifactSearchService.findArtifacts(artifactTypeCode, name, uri, version, userId, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchArtifacts failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchArtifacts failed", ex);
		}
	}

	@ApiOperation(value = "Gets the solution revisions that use the specified artifact ID.", //
			response = MLPSolutionRevision.class, responseContainer = "List")
	@RequestMapping(value = "/{artifactId}/" + CCDSConstants.REVISION_PATH, method = RequestMethod.GET)
	public Iterable<MLPSolutionRevision> getRevisionsForArtifact(@PathVariable("artifactId") String artifactId) {
		logger.debug("getRevisionsForArtifact ID {}", artifactId);
		return solutionRevisionRepository.findByArtifactId(artifactId);
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on bad URI, constraint violation etc.", //
			response = MLPArtifact.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createArtifact(@RequestBody MLPArtifact artifact, HttpServletResponse response) {
		logger.debug("createArtifact artifact {}", artifact);
		try {
			String id = artifact.getArtifactId();
			if (id != null) {
				UUID.fromString(id);
				if (artifactRepository.findById(id).isPresent()) {
					logger.warn("createArtifact failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Validate the URI
			if (artifact.getUri() != null)
				new URI(artifact.getUri());
			// Create a new row
			MLPArtifact result = artifactRepository.save(artifact);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.ARTIFACT_PATH + "/" + artifact.getArtifactId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createArtifact took exception {} on data {}", cve.toString(), artifact.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createArtifact failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on bad URI, constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{artifactId}", method = RequestMethod.PUT)
	public MLPResponse updateArtifact(@PathVariable("artifactId") String artifactId, @RequestBody MLPArtifact artifact,
			HttpServletResponse response) {
		logger.debug("updateArtifact ID {}", artifactId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		Optional<MLPArtifact> existing = artifactRepository.findById(artifactId);
		if (!existing.isPresent()) {
			logger.warn("updateArtifact failed on ID {}", artifactId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + artifactId, null);
		}
		try {
			// Validate the URI
			if (artifact.getUri() != null)
				new URI(artifact.getUri());
			// Use the path-parameter id; don't trust the one in the object
			artifact.setArtifactId(artifactId);
			// Update the existing row
			artifactRepository.save(artifact);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateArtifact took exception {} on data {}", cve.toString(), artifact.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateArtifact failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the artifact with the specified ID. Cascades delete to related records.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{artifactId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteArtifact(@PathVariable("artifactId") String artifactId,
			HttpServletResponse response) {
		logger.debug("deleteArtifact ID {}", artifactId);
		try {
			artifactService.deleteArtifact(artifactId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteArtifact failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteArtifact failed", ex);
		}
	}

}
