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
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.cds.repository.SiteConfigRepository;
import org.acumos.cds.repository.SiteContentRepository;
import org.acumos.cds.repository.UserRepository;
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
 * Answers REST requests to manage site configuration and site content entries.
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
@RequestMapping(value = "/" + CCDSConstants.SITE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private SiteConfigRepository siteConfigRepository;
	@Autowired
	private SiteContentRepository siteContentRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets the site configuration value for the specified key. Returns null if not found.", response = MLPSiteConfig.class)
	@RequestMapping(value = CCDSConstants.CONFIG_PATH + "/{configKey}", method = RequestMethod.GET)
	public MLPSiteConfig getSiteConfig(@PathVariable("configKey") String configKey) {
		logger.debug("getSiteConfig key {}", configKey);
		Optional<MLPSiteConfig> da = siteConfigRepository.findById(configKey);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new site configuration record. Returns bad request on constraint violation etc.", response = MLPSiteConfig.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CONFIG_PATH, method = RequestMethod.POST)
	public Object createSiteConfig(@RequestBody MLPSiteConfig siteConfig, HttpServletResponse response) {
		logger.debug("createSiteConfig: key {}", siteConfig.getConfigKey());
		if (siteConfigRepository.findById(siteConfig.getConfigKey()).isPresent()) {
			logger.warn("createSiteConfig failed on key {}", siteConfig.getConfigKey());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Key exists: " + siteConfig.getConfigKey(),
					null);
		}
		// UserID is optional
		if (siteConfig.getUserId() != null && !userRepository.findById(siteConfig.getUserId()).isPresent()) {
			logger.warn("createSiteConfig failed on user {}", siteConfig.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + siteConfig.getUserId(),
					null);
		}
		try {
			Object result = siteConfigRepository.save(siteConfig);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.SITE_PATH + "/" + CCDSConstants.CONFIG_PATH + "/" + siteConfig.getConfigKey());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSiteConfig failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSiteConfig failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing config record with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CONFIG_PATH + "/{configKey}", method = RequestMethod.PUT)
	public Object updateSiteConfig(@PathVariable("configKey") String configKey, @RequestBody MLPSiteConfig siteConfig,
			HttpServletResponse response) {
		logger.debug("updateSiteConfig key {}", configKey);
		// Check for an existing one
		if (!siteConfigRepository.findById(configKey).isPresent()) {
			logger.warn("updateSiteConfig failed on key {}", configKey);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + configKey, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			siteConfig.setConfigKey(configKey);
			siteConfigRepository.save(siteConfig);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSiteConfig failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSiteConfig failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the config record with the specified key. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CONFIG_PATH + "/{configKey}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSiteConfig(@PathVariable("configKey") String configKey,
			HttpServletResponse response) {
		logger.debug("deleteSiteConfig key {}", configKey);
		try {
			siteConfigRepository.deleteById(configKey);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSiteConfig failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSiteConfig failed", ex);
		}
	}

	@ApiOperation(value = "Gets the site content value for the specified key. Answers null if the key is not found.", response = MLPSiteContent.class)
	@RequestMapping(value = CCDSConstants.CONTENT_PATH + "/{contentKey}", method = RequestMethod.GET)
	public MLPSiteContent getSiteContent(@PathVariable("contentKey") String contentKey) {
		logger.debug("getSiteContent key {}", contentKey);
		Optional<MLPSiteContent> da = siteContentRepository.findById(contentKey);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new site content record. Returns bad request on constraint violation etc.", response = MLPSiteContent.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CONTENT_PATH, method = RequestMethod.POST)
	public Object createSiteContent(@RequestBody MLPSiteContent siteContent, HttpServletResponse response) {
		logger.debug("createSiteContent: key {}", siteContent.getContentKey());
		if (siteContentRepository.findById(siteContent.getContentKey()).isPresent()) {
			logger.warn("createSiteContent failed on key {}", siteContent.getContentKey());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Key exists: " + siteContent.getContentKey(),
					null);
		}
		try {
			Object result = siteContentRepository.save(siteContent);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.SITE_PATH + "/" + CCDSConstants.CONTENT_PATH + "/" + siteContent.getContentKey());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSiteContent failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSiteContent failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing content record with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CONTENT_PATH + "/{contentKey}", method = RequestMethod.PUT)
	public Object updateSiteContent(@PathVariable("contentKey") String contentKey,
			@RequestBody MLPSiteContent siteContent, HttpServletResponse response) {
		logger.debug("updateSiteContent key {}", contentKey);
		// Check for an existing one
		if (!siteContentRepository.findById(contentKey).isPresent()) {
			logger.warn("updateSiteContent failed on key {}", contentKey);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + contentKey, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			siteContent.setContentKey(contentKey);
			siteContentRepository.save(siteContent);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSiteContent failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSiteContent failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the content record with the specified key. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CONTENT_PATH + "/{contentKey}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteSiteContent(@PathVariable("contentKey") String contentKey,
			HttpServletResponse response) {
		logger.debug("deleteSiteContent key {}", contentKey);
		try {
			siteContentRepository.deleteById(contentKey);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSiteContent failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSiteContent failed", ex);
		}
	}

}
