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

package org.acumos.cds.service;

import java.util.List;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPCodeNamePair;

/**
 * Defines a method to fetch a list of code-name pairs. These are read from
 * properties (not the database) so calling this a "service" is a stretch.
 */
public interface CodeNameService {

	/**
	 * Gets the list of code-name pairs in the specified value set.
	 * 
	 * @param type
	 *                 Name of the code-name value set; e.g., Toolkit Type.
	 * @return List of MLPCodeNamePair objects
	 */
	List<MLPCodeNamePair> getCodeNamePairs(CodeNameType type);

	/**
	 * Validates the specified code against the specified value set.
	 * 
	 * @param code
	 *                 Code to check
	 * @param type
	 *                 Name of the code-name value set; e.g., Toolkit Type.
	 * @return true if the code is known, otherwise false
	 */
	boolean validateCode(String code, CodeNameType type);

}
