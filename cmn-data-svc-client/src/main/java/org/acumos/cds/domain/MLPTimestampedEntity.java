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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.annotations.ApiModelProperty;

/**
 * Defines createdDate and modifiedDate fields, getters and setters to avoid
 * code repetitions.
 * 
 * Spring has a bit of magic for everything, must use @MappedSuperclass here.
 */
@MappedSuperclass
public abstract class MLPTimestampedEntity implements MLPEntity {

	@CreationTimestamp
	@Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT '0000-00-00 00:00:00'")
	// REST clients should not send this property
	@ApiModelProperty(readOnly = true, value = "Set by system")
	private Date created;

	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE", nullable = false, columnDefinition = "TIMESTAMP")
	// REST clients should not send this property
	@ApiModelProperty(readOnly = true, value = "Set by system")
	private Date modified;

	public MLPTimestampedEntity() {
		// no-arg constructor
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPTimestampedEntity(MLPTimestampedEntity that) {
		this.created = that.created;
		this.modified = that.modified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

}
