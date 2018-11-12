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
import java.util.Objects;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for the change-password request. The client passes this request with
 * both the old password and the new password. The old password in the request
 * must match with the current password in the database in order for the server
 * to proceed the change. This is NOT an entity because there is no table of
 * change requests.
 */
public class MLPPasswordChangeRequest implements MLPEntity, Serializable {
	private static final long serialVersionUID = 1993219468733216332L;

	@Size(max = 50)
	@ApiModelProperty(required = true, value = "Old password in clear text", example = "LongKeysAreHardToCrack54321")
	private String oldLoginPass;

	@Size(max = 50)
	@ApiModelProperty(required = true, value = "New password in clear text", example = "LongKeysAreHardToCrack12345")
	private String newLoginPass;

	/**
	 * No-arg constructor
	 */
	public MLPPasswordChangeRequest() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param oldLoginPass
	 *                         Old password clear text
	 * @param newLoginPass
	 *                         New password clear text
	 */
	public MLPPasswordChangeRequest(String oldLoginPass, String newLoginPass) {
		if (oldLoginPass == null || newLoginPass == null)
			throw new IllegalArgumentException("Null not permitted");
		this.oldLoginPass = oldLoginPass;
		this.newLoginPass = newLoginPass;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPasswordChangeRequest(MLPPasswordChangeRequest that) {
		this.newLoginPass = that.newLoginPass;
		this.oldLoginPass = that.oldLoginPass;
	}

	public String getOldLoginPass() {
		return oldLoginPass;
	}

	public void setOldLoginPass(String oldLoginPass) {
		this.oldLoginPass = oldLoginPass;
	}

	public String getNewLoginPass() {
		return newLoginPass;
	}

	public void setNewLoginPass(String newLoginPass) {
		this.newLoginPass = newLoginPass;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPPasswordChangeRequest))
			return false;
		MLPPasswordChangeRequest thatObj = (MLPPasswordChangeRequest) that;
		return Objects.equals(oldLoginPass, thatObj.oldLoginPass) && Objects.equals(newLoginPass, thatObj.newLoginPass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(oldLoginPass, newLoginPass);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[oldLoginPass=" + oldLoginPass + ", newLoginPass=" + newLoginPass + "]";
	}

}
