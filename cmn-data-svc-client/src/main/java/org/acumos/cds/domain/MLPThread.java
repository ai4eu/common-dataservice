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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;

/**
 * A thread of comments. A thread is bound to a single solution revision.
 */
@Entity
@Table(name = "C_THREAD")
public class MLPThread implements MLPEntity, Serializable {

	private static final long serialVersionUID = -8103629633174998747L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "THREAD_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String threadId;

	@Column(name = "SOLUTION_ID", nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "REVISION_ID", nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Column(name = "TITLE", columnDefinition = "VARCHAR(100)")
	@Size(max = 100)
	@ApiModelProperty(value = "Thread title", example = "How to model this?")
	private String title;

	/**
	 * No-arg constructor
	 */
	public MLPThread() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits thread ID, which is generated
	 * on save.
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 */
	public MLPThread(String solutionId, String revisionId) {
		if (solutionId == null || revisionId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.revisionId = revisionId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPThread(MLPThread that) {
		this.revisionId = that.revisionId;
		this.solutionId = that.solutionId;
		this.threadId = that.threadId;
		this.title = that.title;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPThread))
			return false;
		MLPThread thatObj = (MLPThread) that;
		return Objects.equals(threadId, thatObj.threadId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(threadId, solutionId, revisionId, title);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[threadId=" + threadId + ", solutionId=" + solutionId + ", revisionId="
				+ revisionId + ", title=" + title + "]";
	}

}
