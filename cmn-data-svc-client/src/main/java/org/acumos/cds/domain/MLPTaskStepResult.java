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

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for a step result, a part of a task.
 */
@Entity
@Table(name = "C_STEP_RESULT")
public class MLPTaskStepResult implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -595148641870461125L;

	// In case the parent needs to refer to this column by name
	public static final String TASK_ID_COL_NAME = "TASK_ID";

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	// The "native" annotations work in Hibernate 5.3 with Mariadb 10.2.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(value = "Generated", accessMode = AccessMode.READ_ONLY)
	private Long stepResultId;

	@Column(name = TASK_ID_COL_NAME, nullable = false, columnDefinition = "INT")
	@NotNull(message = "TaskId cannot be null")
	@ApiModelProperty(required = true, value = "Task ID", example = "1")
	private Long taskId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Step name cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, value = "Step name", example = "Create widget")
	private String name;

	@Column(name = "STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Status code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Status code", example = "SU")
	private String statusCode;

	@Column(name = "RESULT", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(value = "Step result details")
	private String result;

	@Column(name = "START_DATE", nullable = false, columnDefinition = "TIMESTAMP")
	@NotNull(message = "Start timestamp cannot be null")
	@ApiModelProperty(required = true, value = "Start timestamp", example = "2018-12-16T12:34:56.789Z")
	private Instant startDate;

	@Column(name = "END_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "End timestamp", example = "2018-12-16T12:34:56.789Z")
	private Instant endDate;

	/**
	 * No-arg constructor
	 */
	public MLPTaskStepResult() {
		// no-arg constructor
		startDate = Instant.now();
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits step result ID, which is
	 * generated on save.
	 * 
	 * @param taskId
	 *                       Task ID
	 * @param name
	 *                       Step name
	 * @param statusCode
	 *                       Step status code
	 * @param startDate
	 *                       Start date
	 */
	public MLPTaskStepResult(long taskId, String name, String statusCode, Instant startDate) {
		if (name == null || statusCode == null || startDate == null)
			throw new IllegalArgumentException("Null not permitted");
		this.taskId = taskId;
		this.name = name;
		this.statusCode = statusCode;
		this.startDate = startDate;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPTaskStepResult(MLPTaskStepResult that) {
		this.endDate = that.endDate;
		this.name = that.name;
		this.result = that.result;
		this.startDate = that.startDate;
		this.statusCode = that.statusCode;
		this.stepResultId = that.stepResultId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getStepResultId() {
		return stepResultId;
	}

	public void setStepResultId(Long stepResultId) {
		this.stepResultId = stepResultId;
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPTaskStepResult))
			return false;
		MLPTaskStepResult thatObj = (MLPTaskStepResult) that;
		return Objects.equals(stepResultId, thatObj.stepResultId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stepResultId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[stepResultId=" + stepResultId + "name=" + name + "statusCode=" + statusCode
				+ "result=" + result + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

}
