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
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for solution download, which includes artifactID.
 */
@Entity
@Table(name = "C_SOLUTION_DOWNLOAD")
public class MLPSolutionDownload implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 8190007610178155564L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	// The "native" annotations work in Hibernate 5.3 with Mariadb 10.2.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "DOWNLOAD_ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Generated")
	private Long downloadId;

	@Column(name = "SOLUTION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "SolutionID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "ARTIFACT_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "ArtifactID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String artifactId;

	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@CreationTimestamp
	@Column(name = "DOWNLOAD_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	// REST clients should not send this property
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY)
	private Instant downloadDate;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionDownload() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits download ID, which is generated
	 * on save.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param artifactId
	 *                       Artifact ID
	 * @param userId
	 *                       User ID
	 */
	public MLPSolutionDownload(String solutionId, String artifactId, String userId) {
		if (solutionId == null || artifactId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.artifactId = artifactId;
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolutionDownload(MLPSolutionDownload that) {
		this.artifactId = that.artifactId;
		this.downloadDate = that.downloadDate;
		this.downloadId = that.downloadId;
		this.solutionId = that.solutionId;
		this.userId = that.userId;
	}

	public Long getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(Long downloadId) {
		this.downloadId = downloadId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Instant getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(Instant downloadDate) {
		this.downloadDate = downloadDate;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolutionDownload))
			return false;
		MLPSolutionDownload thatObj = (MLPSolutionDownload) that;
		return Objects.equals(downloadId, thatObj.downloadId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(downloadId, solutionId, artifactId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[downloadId=" + downloadId + ", solutionId=" + solutionId + ", artifactId="
				+ artifactId + ", userId=" + userId + ", downloadDate=" + downloadDate + "]";
	}
}
