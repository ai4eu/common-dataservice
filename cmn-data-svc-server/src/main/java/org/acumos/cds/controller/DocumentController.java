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
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.repository.DocumentRepository;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Provides REST endpoints for managing documents.
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
@RequestMapping(value = "/" + CCDSConstants.DOCUMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private DocumentRepository documentRepository;

	@ApiOperation(value = "Gets the entity for the specified ID. Returns null if the ID is not found.", //
			response = MLPDocument.class)
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public MLPResponse getDocument(@PathVariable("documentId") String documentId) {
		logger.debug("getDocument ID {}", documentId);
		Optional<MLPDocument> da = documentRepository.findById(documentId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on bad URI, constraint violation etc.", //
			response = MLPDocument.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createDocument(@RequestBody MLPDocument document, HttpServletResponse response) {
		logger.debug("createDocument entry");
		try {
			String id = document.getDocumentId();
			if (id != null) {
				UUID.fromString(id);
				if (documentRepository.findById(id).isPresent()) {
					logger.warn("createDocument: failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, ENTRY_EXISTS_WITH_ID + id);
				}
			}
			// Validate the URI
			if (document.getUri() != null)
				new URI(document.getUri());
			// Create a new row
			MLPDocument result = documentRepository.save(document);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.DOCUMENT_PATH + "/" + document.getDocumentId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createDocument took exception {} on data {}", cve.toString(), document.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createDocument failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on bad URI, constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{documentId}", method = RequestMethod.PUT)
	public MLPResponse updateDocument(@PathVariable("documentId") String documentId, @RequestBody MLPDocument document,
			HttpServletResponse response) {
		logger.debug("updateDocument ID {}", documentId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		Optional<MLPDocument> existing = documentRepository.findById(documentId);
		if (!existing.isPresent()) {
			logger.warn("updateDocument: failed on ID {}", documentId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + documentId, null);
		}
		try {
			// Validate the URI
			if (document.getUri() != null)
				new URI(document.getUri());
			// Use the path-parameter id; don't trust the one in the object
			document.setDocumentId(documentId);
			// Update the existing row
			documentRepository.save(document);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateDocument took exception {} on data {}", cve.toString(), document.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateDocument failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{documentId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteDocument(@PathVariable("documentId") String documentId,
			HttpServletResponse response) {
		logger.debug("deleteDocument ID {}", documentId);
		try {
			documentRepository.deleteById(documentId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteDocument failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteDocument failed", ex);
		}
	}

}
