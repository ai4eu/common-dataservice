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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for an artifact. Inherits all simple field mappings from the abstract
 * superclass.
 * 
 * Participates in a many-to-many relationship with a solution revision via a
 * mapping table, but has no annotations for that.
 */
@Entity
@Table(name = MLPAbstractArtifact.TABLE_NAME)
public class MLPArtifact extends MLPAbstractArtifact implements Serializable {

	private static final long serialVersionUID = 814823907210569812L;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	/**
	 * No-arg constructor.
	 */
	public MLPArtifact() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits artifact ID, which is generated
	 * on save.
	 * 
	 * @param version
	 *                             Version string
	 * @param artifactTypeCode
	 *                             Valid artifact type code
	 * @param name
	 *                             Name
	 * @param uri
	 *                             URI
	 * @param userId
	 *                             User ID
	 * @param size
	 *                             Length
	 * @throws IllegalArgumentException
	 *                                      if the URI value violates RFC 2396
	 */
	public MLPArtifact(String version, String artifactTypeCode, String name, String uri, String userId, int size) {
		super(version, artifactTypeCode, name, uri, size);
		if (userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPArtifact(MLPArtifact that) {
		super(that);
		this.userId = that.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[artifactId=" + getArtifactId() + ", userId=" + userId
				+ ", artifactTypeCode=" + getArtifactTypeCode() + ", description=" + getDescription() + ", version="
				+ getVersion() + ", uri=" + getUri() + ", created=" + getCreated() + ", modified=" + getModified()
				+ "]";
	}

}
