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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.service.CodeNameService;
import org.acumos.cds.transport.ErrorTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Provides getters for all code-name value sets, which are obtained from
 * property sources.
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.CODE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeTableController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private CodeNameService codeNameService;

	@ApiOperation(value = "Gets the list of value set names that can be used to fetch code-name pairs.", //
			response = String.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.PAIR_PATH, method = RequestMethod.GET)
	public List<String> getValueSetNames() {
		logger.debug("getValueSetNames");
		List<String> list = new ArrayList<>();
		for (CodeNameType cn : CodeNameType.values()) {
			list.add(cn.name());
		}
		return list;
	}

	@ApiOperation(value = "Gets the list of code-name pairs for the specified value set. Returns bad request if the value set is not found.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.PAIR_PATH + "/{name}", method = RequestMethod.GET)
	public Object getCodeNamePairs(@PathVariable(CCDSConstants.NAME_PATH) String valueSetName,
			HttpServletResponse response) {
		logger.debug("getCodeNamePairs {}", valueSetName);
		CodeNameType type;
		try {
			type = CodeNameType.valueOf(valueSetName);
			return codeNameService.getCodeNamePairs(type);
		} catch (Exception ex) {
			logger.warn("getCodeNamePairs failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Unexpected value set name " + valueSetName);
		}
	}

	@ApiOperation(value = "Gets the list of access type codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.ACCESS_PATH + "/" + CCDSConstants.TYPE_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getAccessTypeList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.ACCESS_TYPE.name(), response);
	}

	@ApiOperation(value = "Gets the list of artifact type codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.ARTIFACT_PATH + "/"
			+ CCDSConstants.TYPE_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getArtifactTypeList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.ARTIFACT_TYPE.name(), response);
	}

	@ApiOperation(value = "Gets the list of deployment status codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.DEPLOY_PATH + "/"
			+ CCDSConstants.STATUS_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getDeploymentStatusList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.DEPLOYMENT_STATUS.name(), response);
	}

	@ApiOperation(value = "Gets the list of login provider codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.LOGIN_PROVIDER_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getLoginProviderList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.LOGIN_PROVIDER.name(), response);
	}

	@ApiOperation(value = "Gets the list of model type codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.MODEL_PATH + "/" + CCDSConstants.TYPE_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getModelTypeList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.MODEL_TYPE.name(), response);
	}

	@ApiOperation(value = "Gets the list of step status codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.STEP_PATH + "/" + CCDSConstants.STATUS_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getStepStatusList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.STEP_STATUS.name(), response);
	}

	@ApiOperation(value = "Gets the list of step type codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.STEP_PATH + "/" + CCDSConstants.TYPE_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getStepTypeList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.STEP_TYPE.name(), response);
	}

	@ApiOperation(value = "Gets the list of subscription scope codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.SUBSCRIPTION_PATH + "/"
			+ CCDSConstants.TYPE_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getSubscriptionScopes(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.SUBSCRIPTION_SCOPE.name(), response);
	}

	@ApiOperation(value = "Gets the list of toolkit type codes. This is DEPRECATED, use getCodeNamePairs with the appropriate value-set name.", //
			response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.TOOLKIT_PATH + "/"
			+ CCDSConstants.TYPE_PATH, method = RequestMethod.GET)
	@Deprecated
	public Object getToolkitTypeList(HttpServletResponse response) {
		return getCodeNamePairs(CodeNameType.TOOLKIT_TYPE.name(), response);
	}

}
