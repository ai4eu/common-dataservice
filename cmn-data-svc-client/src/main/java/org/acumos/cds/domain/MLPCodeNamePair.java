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

import java.util.Objects;

/**
 * Defines fields, getters and setters to avoid code repetitions.
 */
public class MLPCodeNamePair implements MLPDomainModel {

	private String code;
	private String name;

	public MLPCodeNamePair() {
		// Empty no-arg constructor
	}

	public MLPCodeNamePair(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPCodeNamePair))
			return false;
		MLPCodeNamePair thatObj = (MLPCodeNamePair) that;
		return Objects.equals(code, thatObj.code) && Objects.equals(name, thatObj.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[code=" + code + ", name=" + name + "]";
	}

}
