/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import java.net.MalformedURLException;
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;

/**
 * Attributes on every ML Workbench entity
 */
@MappedSuperclass
public abstract class MLPAbstractWorkbenchArtifact extends MLPTimestampedEntity {

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Name cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, value = "Name", example = "My workbench pipeline")
	private String name;

	/**
	 * Inactive means archived or deleted.
	 */
	@Column(name = "ACTIVE_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
	@NotNull(message = "Active flag cannot be null")
	@Type(type = "yes_no")
	@ApiModelProperty(required = true, value = "Boolean indicator")
	private boolean active;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID (UUID)", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "VERSION", nullable = false, columnDefinition = "VARCHAR(25)")
	@NotNull(message = "Version cannot be null")
	@Size(max = 25)
	@ApiModelProperty(required = true, value = "Free-text version string", example = "v1.0")
	private String version;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	@ApiModelProperty(value = "Description", example = "The pipeline is full.")
	private String description;

	@Column(name = "SERVICE_STATUS_CD", columnDefinition = "CHAR(2)")
	@Size(max = 2)
	@ApiModelProperty(value = "Two-character service status code", example = "AB")
	private String serviceStatusCode;

	@Column(name = "REPOSITORY_URL", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(value = "Repository URL", example = "http://my.company.com/repo/pipeline1")
	private String repositoryUrl;

	/**
	 * No-arg constructor
	 */
	public MLPAbstractWorkbenchArtifact() {
		// no-arg constructor
	}

	/**
	 * 
	 * @param name
	 *                    Artifact name
	 * @param active
	 *                    True or false
	 * @param userId
	 *                    ID of a valid user
	 * @param version
	 *                    Version string
	 */
	public MLPAbstractWorkbenchArtifact(String name, boolean active, String userId, String version) {
		if (name == null || userId == null || version == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
		this.active = active;
		this.userId = userId;
		this.version = version;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPAbstractWorkbenchArtifact(MLPAbstractWorkbenchArtifact that) {
		super(that);
		this.active = that.active;
		this.description = that.description;
		this.name = that.name;
		this.repositoryUrl = that.repositoryUrl;
		this.serviceStatusCode = that.serviceStatusCode;
		this.userId = that.userId;
		this.version = that.version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceStatusCode() {
		return serviceStatusCode;
	}

	public void setServiceStatusCode(String serviceStatusCode) {
		this.serviceStatusCode = serviceStatusCode;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * Sets the URL
	 * 
	 * @param theUrl
	 *                   URL to set
	 * @throws IllegalArgumentException
	 *                                      on malformed URLs
	 */
	public void setRepositoryUrl(String theUrl) {
		try {
			if (theUrl != null)
				new URL(theUrl);
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException(ex);
		}
		this.repositoryUrl = theUrl;
	}

}
