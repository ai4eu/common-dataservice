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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.acumos.cds.CCDSConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import springfox.documentation.annotations.ApiIgnore;

/**
 * Returns JSON on error within the Spring-managed context. Does not fire for
 * anything else; e.g., resource not found outside the context. If trace is
 * requested via request parameter ("?trace=true") and available, adds stack
 * trace information to the standard JSON error response.
 * 
 * Excluded from Swagger API documentation.
 * 
 * https://stackoverflow.com/questions/25356781/spring-boot-remove-whitelabel-error-page
 */
@ApiIgnore
@RestController
public class SimpleErrorController implements ErrorController {

	private static final String TRACE = "trace";
	private final ErrorAttributes errorAttributes;

	/**
	 * Constructor
	 * 
	 * @param errorAttributes
	 *                            error attributes must not be null
	 */
	@Autowired
	public SimpleErrorController(ErrorAttributes errorAttributes) {
		Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
		this.errorAttributes = errorAttributes;
	}

	@Override
	public String getErrorPath() {
		return CCDSConstants.ERROR_PATH;
	}

	/**
	 * Builds a map with error details
	 * 
	 * @param aRequest
	 *                     HttpServletRequest
	 * @return Map of String to Object
	 */
	@RequestMapping(CCDSConstants.ERROR_PATH)
	public Map<String, Object> error(HttpServletRequest aRequest) {
		Map<String, Object> body = getErrorAttributes(aRequest, getTraceParameter(aRequest));
		body.put("decorated-by", SimpleErrorController.class.getName());
		String trace = (String) body.get(TRACE);
		if (trace != null) {
			String[] lines = trace.split("\n\t");
			body.put(TRACE, lines);
		}
		return body;
	}

	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter(TRACE);
		if (parameter == null)
			return false;
		return !"false".equalsIgnoreCase(parameter);
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
		return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

}
