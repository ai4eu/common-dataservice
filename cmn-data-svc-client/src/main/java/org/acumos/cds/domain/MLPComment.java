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
 * A comment in a thread.
 */
@Entity
@Table(name = "C_COMMENT")
public class MLPComment extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 4666733550951208705L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "COMMENT_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String commentId;

	@Column(name = "THREAD_ID", nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Generated thread ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String threadId;

	@Column(name = "PARENT_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "Parent comment ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String parentId;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "TEXT", nullable = false, columnDefinition = "VARCHAR(8192)")
	@Size(max = 8192)
	@ApiModelProperty(required = true, value = "The comment text", example = "Best model ever")
	private String text;

	/**
	 * No-arg constructor
	 */
	public MLPComment() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits comment ID, which is generated
	 * on save.
	 * 
	 * @param threadId
	 *                     Thread ID
	 * @param userId
	 *                     User ID of author
	 * @param text
	 *                     Comment text
	 */
	public MLPComment(String threadId, String userId, String text) {
		if (threadId == null || userId == null || text == null)
			throw new IllegalArgumentException("Null not permitted");
		this.threadId = threadId;
		this.userId = userId;
		this.text = text;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPComment(MLPComment that) {
		super(that);
		this.commentId = that.commentId;
		this.parentId = that.parentId;
		this.text = that.text;
		this.threadId = that.threadId;
		this.userId = that.userId;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPComment))
			return false;
		MLPComment thatObj = (MLPComment) that;
		return Objects.equals(commentId, thatObj.commentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(commentId, threadId, userId, text);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[commentId=" + commentId + ", threadId=" + threadId + ", userId=" + userId
				+ ", text=" + text + "]";
	}

}
