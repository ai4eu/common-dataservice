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
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.repository.CatSolMapRepository;
import org.acumos.cds.repository.CatalogRepository;
import org.acumos.cds.repository.SolutionRepository;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Provides REST endpoints for managing catalogs.
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
@RequestMapping(value = "/" + CCDSConstants.CATALOG_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private CatalogRepository catalogRepository;
	@Autowired
	private CatSolMapRepository catSolMapRepository;
	@Autowired
	private SolutionRepository solutionRepository;

	@ApiOperation(value = "Gets a page of catalogs, optionally sorted. Answers empty if none are found.", response = MLPCatalog.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPCatalog> getCatalogs(Pageable pageable) {
		logger.debug("getCatalogs {}", pageable);
		return catalogRepository.findAll(pageable);
	}

	@ApiOperation(value = "Gets the catalog for the specified ID. Returns null if the ID is not found.", //
			response = MLPCatalog.class)
	@RequestMapping(value = "/{catalogId}", method = RequestMethod.GET)
	public MLPCatalog getCatalog(@PathVariable("catalogId") String catalogId) {
		logger.debug("getCatalog ID {}", catalogId);
		Optional<MLPCatalog> da = catalogRepository.findById(catalogId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new catalog and generates an ID if needed. Returns bad request on bad URL, constraint violation etc.", //
			response = MLPCatalog.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createCatalog(@RequestBody MLPCatalog catalog, HttpServletResponse response) {
		logger.debug("createCatalog entry");
		try {
			String id = catalog.getCatalogId();
			if (id != null) {
				UUID.fromString(id);
				if (catalogRepository.findById(id).isPresent()) {
					logger.warn("createCatalog: failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			if (catalog.getAccessTypeCode() != null)
				super.validateCode(catalog.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			if (catalog.getUrl() != null)
				new URL(catalog.getUrl());
			// Create a new row
			MLPCatalog result = catalogRepository.save(catalog);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.CATALOG_PATH + "/" + catalog.getCatalogId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createCatalog took exception {} on data {}", cve.toString(), catalog.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createCatalog failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing catalog with the supplied data. Returns bad request on bad URI, constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{catalogId}", method = RequestMethod.PUT)
	public MLPResponse updateCatalog(@PathVariable("catalogId") String catalogId, @RequestBody MLPCatalog catalog,
			HttpServletResponse response) {
		logger.debug("updateCatalog ID {}", catalogId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		Optional<MLPCatalog> existing = catalogRepository.findById(catalogId);
		if (!existing.isPresent()) {
			logger.warn("updateCatalog: failed on ID {}", catalogId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + catalogId, null);
		}
		try {
			if (catalog.getAccessTypeCode() != null)
				super.validateCode(catalog.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			if (catalog.getUrl() != null)
				new URL(catalog.getUrl());
			// Use the path-parameter id; don't trust the one in the object
			catalog.setCatalogId(catalogId);
			// Update the existing row
			catalogRepository.save(catalog);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateCatalog took exception {} on data {}", cve.toString(), catalog.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateCatalog failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the catalog with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{catalogId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteCatalog(@PathVariable("catalogId") String catalogId, HttpServletResponse response) {
		logger.debug("deleteCatalog ID {}", catalogId);
		try {
			catalogRepository.deleteById(catalogId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteCatalog failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteCatalog failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of solutions in the specified catalog, optionally sorted; empty if none are found.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/{catalogId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	public Object getSolutionsInCatalog(@PathVariable("catalogId") String catalogId, Pageable pageRequest) {
		logger.debug("getSolutionsInCatalog catalogId {}", catalogId);
		return catSolMapRepository.findSolutionsByCatalogId(catalogId, pageRequest);
	}

	@ApiOperation(value = "Adds the specified solution to the specified catalog. Answers bad request if an ID is invalid.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{catalogId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.POST)
	public MLPResponse addSolutionToCatalog(@PathVariable("catalogId") String catalogId,
			@PathVariable("solutionId") String solutionId, @RequestBody MLPCatSolMap map,
			HttpServletResponse response) {
		logger.debug("addSolutionToCatalog catalogId {} solutionId {}", catalogId, solutionId);
		if (!catalogRepository.findById(catalogId).isPresent()) {
			logger.warn("addSolutionToCatalog: failed on cat ID {}", catalogId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + catalogId, null);
		}
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("addSolutionToCatalog: failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		// Use path parameters only
		map.setCatalogId(catalogId);
		map.setSolutionId(solutionId);
		catSolMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Removes the specified solution from the specified solution.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{catalogId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.DELETE)
	public MLPTransportModel dropSolutionFromCatalog(@PathVariable("catalogId") String catalogId,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		logger.debug("dropSolutionFromCatalog catalogId {} solutionId {}", catalogId, solutionId);
		try {
			MLPCatSolMap.CatSolMapPK pk = new MLPCatSolMap.CatSolMapPK(catalogId, solutionId);
			catSolMapRepository.deleteById(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropSolutionFromCatalog failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionFromCatalog failed", ex);
		}
	}

}
