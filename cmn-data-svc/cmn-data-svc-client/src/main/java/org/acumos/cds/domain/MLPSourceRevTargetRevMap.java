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
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPSourceRevTargetRevMap.SourceRevTargetRevMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the composite solution mapping table. This is in lieu of
 * many-to-one annotations.
 */
@Entity
@IdClass(SourceRevTargetRevMapPK.class)
@Table(name = "C_SOURCE_SOL_REV_TARGET_SOL_REV_MAP")
public class MLPSourceRevTargetRevMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -759259140805228328L;

	@Id
	@Column(name = "SOURCE_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Source ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Source revision ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String sourceId;

	@Id
	@Column(name = "TARGET_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Target ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Target revision ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String targetId;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SourceRevTargetRevMapPK implements Serializable {

		private static final long serialVersionUID = -8276055073639401816L;

		private String sourceId;
		private String targetId;

		public SourceRevTargetRevMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param sourceId
		 *                     solution ID
		 * @param targetId
		 *                     solution ID
		 */
		public SourceRevTargetRevMapPK(String sourceId, String targetId) {
			this.sourceId = sourceId;
			this.targetId = targetId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SourceRevTargetRevMapPK))
				return false;
			SourceRevTargetRevMapPK thatPK = (SourceRevTargetRevMapPK) that;
			return Objects.equals(sourceId, thatPK.sourceId) && Objects.equals(targetId, thatPK.targetId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(sourceId, targetId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[sourceId=" + sourceId + ", targetId=" + targetId + "]";
		}

	}

	/**
	 * No-arg constructor
	 */
	public MLPSourceRevTargetRevMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param sourceId
	 *                     solution ID
	 * @param targetId
	 *                     child ID
	 */
	public MLPSourceRevTargetRevMap(String sourceId, String targetId) {
		if (sourceId == null || targetId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.sourceId = sourceId;
		this.targetId = targetId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSourceRevTargetRevMap(MLPSourceRevTargetRevMap that) {
		this.sourceId = that.sourceId;
		this.targetId = that.targetId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSourceRevTargetRevMap))
			return false;
		MLPSourceRevTargetRevMap thatObj = (MLPSourceRevTargetRevMap) that;
		return Objects.equals(sourceId, thatObj.sourceId) && Objects.equals(targetId, thatObj.targetId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceId, targetId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[sourceId=" + sourceId + ", targetId=" + targetId + "]";
	}

}
