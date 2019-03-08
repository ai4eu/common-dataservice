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

import org.acumos.cds.domain.MLPSolUserAccMap.SolUserAccessMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the solution-user-access mapping table.
 */
@Entity
@IdClass(SolUserAccessMapPK.class)
@Table(name = MLPSolUserAccMap.TABLE_NAME)
public class MLPSolUserAccMap implements MLPDomainModel, Serializable {

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_SOL_USER_ACCESS_MAP";
	/* package */ static final String SOL_ID_COL_NAME = "SOLUTION_ID";
	/* package */ static final String USER_ID_COL_NAME = "USER_ID";

	private static final long serialVersionUID = 8809818075005891800L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolUserAccessMapPK implements Serializable {

		private static final long serialVersionUID = 501173361575972604L;
		private String solutionId;
		private String userId;

		public SolUserAccessMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param solutionId
		 *                       solution ID
		 * @param userId
		 *                       user ID
		 */
		public SolUserAccessMapPK(String solutionId, String userId) {
			this.solutionId = solutionId;
			this.userId = userId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolUserAccessMapPK))
				return false;
			SolUserAccessMapPK thatPK = (SolUserAccessMapPK) that;
			return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(solutionId, userId);
		}

	}

	@Id
	@Column(name = SOL_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Solution ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Id
	@Column(name = USER_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "User ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	public MLPSolUserAccMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param userId
	 *                       user ID
	 */
	public MLPSolUserAccMap(String solutionId, String userId) {
		if (solutionId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolUserAccMap(MLPSolUserAccMap that) {
		this.solutionId = that.solutionId;
		this.userId = that.userId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
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
		if (!(that instanceof MLPSolUserAccMap))
			return false;
		MLPSolUserAccMap thatPK = (MLPSolUserAccMap) that;
		return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(userId, thatPK.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(solutionId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + solutionId + ", userId=" + userId + "]";
	}

}
