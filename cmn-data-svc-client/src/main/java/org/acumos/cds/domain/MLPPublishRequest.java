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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for requesting approval to publish a solution to a catalog.
 */
@Entity
@Table(name = "C_PUBLISH_REQUEST")
public class MLPPublishRequest extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 6831584627951828598L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	// The "native" annotations work in Hibernate 5.3 with Mariadb 10.2.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "REQUEST_ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Generated", example = "12345")
	// Use object to allow null value
	private Long requestId;

	@Column(name = "SOLUTION_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "SolutionID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "Solution ID (UUID)", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "REVISION_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "RevisionID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "Revision ID (UUID)", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Column(name = "CATALOG_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "CatalogID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "Catalog ID (UUID)", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	@Column(name = "REQ_USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Requesting user ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "Requesting user ID (UUID)", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String requestUserId;

	@Column(name = "RVW_USER_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "Reviewing user ID (UUID)", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String reviewUserId;

	@Column(name = "STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Request status code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(value = "Status code", required = true, example = "AP")
	private String statusCode;

	@Column(name = "COMMENT", columnDefinition = "VARCHAR(8192)")
	@Size(max = 8192)
	@ApiModelProperty(value = "Approve or decline reason")
	private String comment;

	/**
	 * No-arg constructor
	 */
	public MLPPublishRequest() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits request ID, which is generated
	 * on save.
	 * 
	 * @param solutionId
	 *                          Solution ID
	 * @param revisionId
	 *                          Revision ID
	 * @param requestUserId
	 *                          Request user ID
	 * @param catalogId
	 *                          Destination catalog ID
	 * @param statusCode
	 *                          Request status code
	 */
	public MLPPublishRequest(String solutionId, String revisionId, String requestUserId, String catalogId,
			String statusCode) {
		if (solutionId == null || revisionId == null || requestUserId == null || catalogId == null
				|| statusCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.requestUserId = requestUserId;
		this.catalogId = catalogId;
		this.statusCode = statusCode;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPublishRequest(MLPPublishRequest that) {
		super(that);
		this.catalogId = that.catalogId;
		this.comment = that.comment;
		this.requestId = that.requestId;
		this.requestUserId = that.requestUserId;
		this.reviewUserId = that.reviewUserId;
		this.revisionId = that.revisionId;
		this.solutionId = that.solutionId;
		this.statusCode = that.statusCode;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getRequestUserId() {
		return requestUserId;
	}

	public void setRequestUserId(String requestUserId) {
		this.requestUserId = requestUserId;
	}

	public String getReviewUserId() {
		return reviewUserId;
	}

	public void setReviewUserId(String reviewUserId) {
		this.reviewUserId = reviewUserId;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPPublishRequest))
			return false;
		MLPPublishRequest thatObj = (MLPPublishRequest) that;
		return Objects.equals(requestId, thatObj.requestId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId, catalogId, requestUserId, revisionId, solutionId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[requestId=" + requestId + ", solutionId=" + solutionId + ", revisionId="
				+ revisionId + ", requestUserId=" + requestUserId + ", catalogId=" + catalogId + ", statusCode="
				+ statusCode + "]";
	}

}
