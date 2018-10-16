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

package org.acumos.cds.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model for a solution group to support access control. The group definition is
 * an ID-name pair. The ID is used in another table to map solution(s) to
 * group(s).
 */
@Entity
@Table(name = "C_SOLUTION_GROUP")
public class MLPSolutionGroup extends MLPAbstractGroup implements Serializable {

	private static final long serialVersionUID = -327686386522809395L;

	/**
	 * No-arg constructor.
	 */
	public MLPSolutionGroup() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits group ID, which is generated on
	 * save.
	 * 
	 * @param name
	 *            Group name
	 */
	public MLPSolutionGroup(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPSolutionGroup(MLPSolutionGroup that) {
		super(that);
	}

}
