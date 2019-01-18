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
 * Model for a task, which gathers step results
 */
@Entity
@Table(name = "C_TASK")
public class MLPTask extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 6341959458688536809L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	// The "native" annotations work in Hibernate 5.3 with Mariadb 10.2.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Generated")
	private Long taskId;

	@Column(name = "TASK_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Task code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(value = "Task code", required = true, example = "OB")
	private String taskCode;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "User ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Task name cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, example = "Task name")
	private String name;

	@Column(name = "STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Status code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(value = "Status code", required = true, example = "SU")
	private String statusCode;

	@Column(name = "TRACKING_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String trackingId;

	@Column(name = "SOLUTION_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "REVISION_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	/**
	 * No-arg constructor
	 */
	public MLPTask() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits task ID, which is generated on
	 * save.
	 * 
	 * @param taskTypeCode
	 *                         Two-letter task type code
	 * @param name
	 *                         Task name
	 * @param userId
	 *                         User ID
	 * @param statusCode
	 *                         Status code
	 */
	public MLPTask(String taskTypeCode, String name, String userId, String statusCode) {
		if (taskTypeCode == null || name == null || userId == null || statusCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.statusCode = statusCode;
		this.taskCode = taskTypeCode;
		this.name = name;
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPTask(MLPTask that) {
		super(that);
		this.name = that.name;
		this.statusCode = that.statusCode;
		this.taskCode = that.taskCode;
		this.taskId = that.taskId;
		this.trackingId = that.trackingId;
		this.userId = that.userId;
		this.revisionId = that.revisionId;
		this.solutionId = that.solutionId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPTask))
			return false;
		MLPTask thatObj = (MLPTask) that;
		return Objects.equals(taskId, thatObj.taskId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(taskId, name, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[taskId=" + taskId + ", taskCode=" + taskCode + ", userId=" + userId
				+ ", name=" + name + ", statusCode=" + statusCode + ", trackingId=" + trackingId + ", solutionId="
				+ solutionId + "revisionId=" + revisionId + "]";
	}

}
