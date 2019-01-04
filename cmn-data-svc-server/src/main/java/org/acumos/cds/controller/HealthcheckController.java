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

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CdsApplication;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Answers REST requests for the service health.
 */
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthcheckController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private SolutionRepository solutionRepository;

	@ApiOperation(value = "Checks the health of the application by querying the database.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.HEALTHCHECK_PATH, method = RequestMethod.GET)
	public MLPTransportModel getHealth() {
		logger.debug("getHealth enter");
		long count = solutionRepository.count();
		return new SuccessTransport(200, "database reports solution count is " + count);
	}

	@ApiOperation(value = "Gets the value of the MANIFEST.MF property Implementation-Version as written by maven.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.VERSION_PATH, method = RequestMethod.GET)
	public MLPTransportModel getVersion() {
		logger.debug("getVersion enter");
		return new SuccessTransport(200, CdsApplication.getVersion());
	}

}
