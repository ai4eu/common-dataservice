/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.repository.LicenseProfileTemplateRepository;
import org.acumos.cds.repository.UserRepository;
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
 * Answers REST requests to manage license profile templates.
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
@RequestMapping(value = "/" + CCDSConstants.LICENSE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class LicenseController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private LicenseProfileTemplateRepository templateRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets a page of license profile templates, optionally sorted on fields. Returns empty if none are found.", //
			response = MLPLicenseProfileTemplate.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.TEMPLATE_PATH, method = RequestMethod.GET)
	public Page<MLPLicenseProfileTemplate> getLicenseProfileTemplates(Pageable pageable) {
		logger.debug("getLicenseProfileTemplates query {}", pageable);
		return templateRepository.findAll(pageable);
	}

	@ApiOperation(value = "Gets the license profile template for the specified ID. Returns null if not found.", response = MLPLicenseProfileTemplate.class)
	@RequestMapping(value = CCDSConstants.TEMPLATE_PATH + "/{licenseId}", method = RequestMethod.GET)
	public MLPLicenseProfileTemplate getLicenseProfileTemplate(@PathVariable("licenseId") Long licenseId) {
		logger.debug("getLicenseProfileTemplate ID {}", licenseId);
		Optional<MLPLicenseProfileTemplate> da = templateRepository.findById(licenseId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new license profile template. Returns bad request on constraint violation etc.", response = MLPLicenseProfileTemplate.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.TEMPLATE_PATH, method = RequestMethod.POST)
	public Object createLicenseProfileTemplate(@RequestBody MLPLicenseProfileTemplate template,
			HttpServletResponse response) {
		logger.debug("createLicenseProfileTemplate: name {}", template.getTemplateName());
		if (template.getUserId() == null || !userRepository.findById(template.getUserId()).isPresent()) {
			logger.warn("createLicenseProfileTemplate failed on user {}", template.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + template.getUserId(),
					null);
		}
		try {
			// Create a new row
			template.setTemplateId(null);
			Object result = templateRepository.save(template);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.LICENSE_PATH + "/" + CCDSConstants.TEMPLATE_PATH + "/" + template.getTemplateId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createLicenseProfileTemplate took exception {} on data {}", cve.toString(),
					template.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createLicenseProfileTemplate failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing license profile template with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.TEMPLATE_PATH + "/{licenseId}", method = RequestMethod.PUT)
	public Object updateLicenseProfileTemplate(@PathVariable("licenseId") Long licenseId,
			@RequestBody MLPLicenseProfileTemplate licenseTemplate, HttpServletResponse response) {
		logger.debug("updateLicenseProfileTemplate key {}", licenseId);
		// Check for an existing one
		if (!templateRepository.findById(licenseId).isPresent()) {
			logger.warn("updateLicenseProfileTemplate failed on ID {}", licenseId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + licenseId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			licenseTemplate.setTemplateId(licenseId);
			templateRepository.save(licenseTemplate);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateLicenseProfileTemplate took exception {} on data {}", cve.toString(),
					licenseTemplate.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateLicenseProfileTemplate failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the license profile template with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.TEMPLATE_PATH + "/{licenseId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteLicenseProfileTemplate(@PathVariable("licenseId") Long licenseId,
			HttpServletResponse response) {
		logger.debug("deleteLicenseProfileTemplate ID {}", licenseId);
		try {
			templateRepository.deleteById(licenseId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteLicenseProfileTemplate failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteLicenseProfileTemplate failed", ex);
		}
	}

}
