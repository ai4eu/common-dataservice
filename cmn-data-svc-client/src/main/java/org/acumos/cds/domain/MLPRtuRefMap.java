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
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPRtuRefMap.RtuRefMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the RTU-Reference mapping table.
 */
@Entity
@IdClass(RtuRefMapPK.class)
@Table(name = MLPRtuRefMap.TABLE_NAME)
public class MLPRtuRefMap implements MLPDomainModel, Serializable {

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_RTU_REF_MAP";
	/* package */ static final String RTU_ID_COL_NAME = "RTU_ID";
	/* package */ static final String REF_ID_COL_NAME = "REF_ID";

	private static final long serialVersionUID = 4526691268857817931L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class RtuRefMapPK implements Serializable {

		private static final long serialVersionUID = 2703702939029665333L;
		private Long rtuId;
		private String refId;

		public RtuRefMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param rtuId
		 *                  right to use ID
		 * @param refId
		 *                  reference ID
		 */
		public RtuRefMapPK(Long rtuId, String refId) {
			this.rtuId = rtuId;
			this.refId = refId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof RtuRefMapPK))
				return false;
			RtuRefMapPK thatPK = (RtuRefMapPK) that;
			return Objects.equals(rtuId, thatPK.rtuId) && Objects.equals(refId, thatPK.refId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(rtuId, refId);
		}

	}

	@Id
	@Column(name = RTU_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Right-to-USE ID", example = "12345")
	private Long rtuId;

	@Id
	@Column(name = REF_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Reference ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String refId;

	public MLPRtuRefMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that must be
	 * supplied to create a valid instance.
	 * 
	 * @param rtuId
	 *                  right to use ID
	 * @param refId
	 *                  reference ID
	 */
	public MLPRtuRefMap(Long rtuId, String refId) {
		if (refId == null || rtuId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.rtuId = rtuId;
		this.refId = refId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRtuRefMap(MLPRtuRefMap that) {
		this.rtuId = that.rtuId;
		this.refId = that.refId;
	}

	public Long getRightToUseId() {
		return rtuId;
	}

	public void setRightToUseId(Long rtuId) {
		this.rtuId = rtuId;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRtuRefMap))
			return false;
		MLPRtuRefMap thatObj = (MLPRtuRefMap) that;
		return Objects.equals(rtuId, thatObj.rtuId) && Objects.equals(refId, thatObj.refId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rtuId, refId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[rtuId=" + rtuId + ", refId=" + refId + "]";
	}

}
