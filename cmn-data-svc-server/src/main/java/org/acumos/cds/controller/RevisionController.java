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
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRevCatDocMap;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.CatalogRepository;
import org.acumos.cds.repository.RevCatDescriptionRepository;
import org.acumos.cds.repository.RevCatDocMapRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Answers REST requests to get, add, update and delete revisions. A revision is
 * a collection of artifacts. A revision cannot exist without a solution, but an
 * artifact can exist without a revision.
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
@RequestMapping(value = "/" + CCDSConstants.REVISION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class RevisionController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private CatalogRepository catalogRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private RevCatDescriptionRepository revCatDescRepository;
	@Autowired
	private RevCatDocMapRepository revCatDocMapRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;

	@ApiOperation(value = "Gets the artifacts for the specified solution revision. Answers empty if none are found.", response = MLPArtifact.class, responseContainer = "List")
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ARTIFACT_PATH, method = RequestMethod.GET)
	public Iterable<MLPArtifact> getSolutionRevisionArtifacts(@PathVariable("revisionId") String revisionId) {
		logger.debug("getSolutionRevisionArtifacts: revisionId {}", revisionId);
		return artifactRepository.findByRevision(revisionId);
	}

	@ApiOperation(value = "Adds the specified artifact to the specified solution revision. Returns bad request on constraint violation etc.", response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ARTIFACT_PATH
			+ "/{artifactId}", method = RequestMethod.POST)
	public MLPTransportModel addSolutionRevisionArtifact(@PathVariable("revisionId") String revisionId,
			@PathVariable("artifactId") String artifactId, HttpServletResponse response) {
		logger.debug("addSolutionRevisionArtifact: revisionId {} artifactId {}", revisionId, artifactId);
		if (!revisionRepository.findById(revisionId).isPresent()) {
			logger.warn("addSolutionRevisionArtifact failed on rev ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		if (!artifactRepository.findById(artifactId).isPresent()) {
			logger.warn("addSolutionRevisionArtifact failed on art ID {}", artifactId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + artifactId, null);
		}
		try {
			MLPSolRevArtMap map = new MLPSolRevArtMap(revisionId, artifactId);
			solRevArtMapRepository.save(map);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			logger.warn("addSolutionRevisionArtifact failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "addSolutionRevisionArtifact failed", ex);
		}
	}

	@ApiOperation(value = "Removes the specified artifact from the specified solution revision.", response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ARTIFACT_PATH
			+ "/{artifactId}", method = RequestMethod.DELETE)
	public MLPTransportModel dropSolutionRevisionArtifact(@PathVariable("revisionId") String revisionId,
			@PathVariable("artifactId") String artifactId, HttpServletResponse response) {
		logger.debug("dropSolutionRevisionArtifact: revisionId {} artifactId {}", revisionId, artifactId);
		try {
			solRevArtMapRepository.deleteById(new MLPSolRevArtMap.SolRevArtMapPK(revisionId, artifactId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			logger.warn("dropSolutionRevisionArtifact failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionRevisionArtifact failed", ex);
		}
	}

	@ApiOperation(value = "Gets the description for the specified revision and catalog. Returns null if not found.", //
			response = MLPRevCatDescription.class)
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.GET)
	public MLPResponse getRevCatDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId) {
		logger.debug("getRevCatDescription: revisionId {} catalogId {}", revisionId, catalogId);
		Optional<MLPRevCatDescription> da = revCatDescRepository
				.findById(new MLPRevCatDescription.RevCatDescriptionPK(revisionId, catalogId));
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new description for the specified revision and catalog. Returns bad request if an ID is not found.", //
			response = MLPRevCatDescription.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.POST)
	public MLPResponse createRevCatDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId, @RequestBody MLPRevCatDescription description,
			HttpServletResponse response) {
		logger.debug("createRevCatDescription: revisionId {} catalogId {}", revisionId, catalogId);
		if (!revisionRepository.findById(revisionId).isPresent()) {
			logger.warn("createRevisionDescription failed on ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		if (!catalogRepository.findById(catalogId).isPresent()) {
			logger.warn("createRevisionDescription failed on ID {}", catalogId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + catalogId, null);
		}
		try {
			// Use the validated values
			description.setRevisionId(revisionId);
			description.setCatalogId(catalogId);
			// Create a new row
			return revCatDescRepository.save(description);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createRevCatDescription took exception {} on data {}", cve.toString(), description.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createRevCatDescription failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing description with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.PUT)
	public MLPTransportModel updateRevCatDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId, @RequestBody MLPRevCatDescription description,
			HttpServletResponse response) {
		logger.debug("updateRevCatDescription: revisionId {} catalogId {}", revisionId, catalogId);
		// Check the existing one
		if (!revCatDescRepository.findById(new MLPRevCatDescription.RevCatDescriptionPK(revisionId, catalogId))
				.isPresent()) {
			logger.warn("updateRevisionDescription failed on ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					NO_ENTRY_WITH_ID + revisionId + "/" + catalogId, null);
		}
		try {
			// Use the validated values
			description.setRevisionId(revisionId);
			description.setCatalogId(catalogId);
			revCatDescRepository.save(description);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateRevCatDescription took exception {} on data {}", cve.toString(), description.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateRevisionDescription failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the description with the specified IDs. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.DELETE)
	public MLPTransportModel deleteRevCatDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId, HttpServletResponse response) {
		logger.debug("deleteRevCatDescription: revisionId {} catalogId {}", revisionId, catalogId);
		try {
			revCatDescRepository.deleteById(new MLPRevCatDescription.RevCatDescriptionPK(revisionId, catalogId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteRevCatDescription failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRevCatDescription failed", ex);
		}
	}

	@ApiOperation(value = "Gets the documents for the specified revision and catalog.", //
			response = MLPDocument.class, responseContainer = "List")
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DOCUMENT_PATH, method = RequestMethod.GET)
	public Iterable<MLPDocument> getRevCatDocuments(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId) {
		logger.debug("getRevCatDocuments: revisionId {} catalogId {}", revisionId, catalogId);
		return revCatDocMapRepository.findByRevisionCatalog(revisionId, catalogId);
	}

	@ApiOperation(value = "Adds a user document to the specified revision and catalog.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DOCUMENT_PATH + "/{documentId}", method = RequestMethod.POST)
	public SuccessTransport addRevisionDocument(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId, @PathVariable("documentId") String documentId) {
		logger.debug("addRevisionDocument: revisionId {} catalogId {} documentId {}", revisionId, catalogId,
				documentId);
		MLPRevCatDocMap map = new MLPRevCatDocMap(revisionId, catalogId, documentId);
		revCatDocMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Removes a document from the specified revision and catalog.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.DOCUMENT_PATH + "/{documentId}", method = RequestMethod.DELETE)
	public SuccessTransport dropRevisionDocument(@PathVariable("revisionId") String revisionId,
			@PathVariable("catalogId") String catalogId, @PathVariable("documentId") String documentId) {
		logger.debug("dropRevisionDocument: revisionId {} catalogId {} documentId {}", revisionId, catalogId,
				documentId);
		revCatDocMapRepository.deleteById(new MLPRevCatDocMap.RevCatDocMapPK(revisionId, catalogId, documentId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}
}