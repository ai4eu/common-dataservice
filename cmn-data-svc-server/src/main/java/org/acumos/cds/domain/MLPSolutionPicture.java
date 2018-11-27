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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

/**
 * Solution entity with image field. Inherits all simple field mappings from the
 * abstract superclass.
 * 
 * Defined in the server project because it's not exposed to clients.
 */
@Entity
@Table(name = MLPAbstractSolution.TABLE_NAME)
public class MLPSolutionPicture extends MLPAbstractSolution implements Serializable {

	private static final long serialVersionUID = 8547380825640664866L;

	/**
	 * User-supplied picture to decorate the solution.
	 * 
	 * Derby BLOB type allows 2GB. Mysql/Mariadb BLOB type only allows 64KB, that's
	 * too small. But Derby fails to create the table if type LONGBLOB is specified
	 * here. With no columDefinition attribute Derby generates a table AND Spring
	 * validates the MariaDB schema if the column is created as LONGBLOB.
	 * 
	 * Jackson handles base64 encoding.
	 */
	@Lob
	@Column(name = "PICTURE", length = 2000000 /* DO NOT USE: columnDefinition = "BLOB" */)
	@ApiModelProperty(value = "Solution picture as byte array")
	private byte[] picture;

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + getSolutionId() + ", name=" + getName() + ", active="
				+ isActive() + ", modelTypeCode=" + getModelTypeCode() + "picture length="
				+ (picture == null ? "null" : Integer.toString(picture.length)) + ", created=" + getCreated()
				+ ", modified=" + getModified() + "]";
	}

}
