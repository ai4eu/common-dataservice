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

import org.acumos.cds.domain.MLPRtuUserMap.RtuUserMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the RTU-User mapping table.
 */
@Entity
@IdClass(RtuUserMapPK.class)
@Table(name = "C_RTU_USER_MAP")
public class MLPRtuUserMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -2217853879633176402L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class RtuUserMapPK implements Serializable {

		private static final long serialVersionUID = -868788814975612902L;
		private Long rtuId;
		private String userId;

		public RtuUserMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param rtuId
		 *                   right to use ID
		 * @param userId
		 *                   user ID
		 */
		public RtuUserMapPK(Long rtuId, String userId) {
			this.rtuId = rtuId;
			this.userId = userId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof RtuUserMapPK))
				return false;
			RtuUserMapPK thatPK = (RtuUserMapPK) that;
			return Objects.equals(rtuId, thatPK.rtuId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(rtuId, userId);
		}

	}

	@Id
	@Column(name = "RTU_ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Right-to-USE ID", example = "12345")
	private Long rtuId;

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	public MLPRtuUserMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param rtuId
	 *                   right to use ID
	 * @param userId
	 *                   user ID
	 */
	public MLPRtuUserMap(Long rtuId, String userId) {
		if (userId == null || rtuId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.rtuId = rtuId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRtuUserMap(MLPRtuUserMap that) {
		this.rtuId = that.rtuId;
		this.userId = that.userId;
	}

	public Long getRightToUseId() {
		return rtuId;
	}

	public void setRightToUseId(Long rtuId) {
		this.rtuId = rtuId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRtuUserMap))
			return false;
		MLPRtuUserMap thatObj = (MLPRtuUserMap) that;
		return Objects.equals(rtuId, thatObj.rtuId) && Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rtuId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[rtuId=" + rtuId + ", userId=" + userId + "]";
	}

}
