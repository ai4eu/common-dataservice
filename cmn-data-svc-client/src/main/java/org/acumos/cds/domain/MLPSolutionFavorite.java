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

import org.acumos.cds.domain.MLPSolutionFavorite.SolutionFavoritePK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for solution favorite.
 */
@Entity
@IdClass(SolutionFavoritePK.class)
@Table(name = "C_SOLUTION_FAVORITE")
public class MLPSolutionFavorite implements MLPEntity, Serializable {

	private static final long serialVersionUID = 8515419501527090148L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolutionFavoritePK implements Serializable {

		private static final long serialVersionUID = -7793316362724380437L;
		private String solutionId;
		private String userId;

		public SolutionFavoritePK() {
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
		public SolutionFavoritePK(String solutionId, String userId) {
			this.solutionId = solutionId;
			this.userId = userId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolutionFavoritePK))
				return false;
			SolutionFavoritePK thatPK = (SolutionFavoritePK) that;
			return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(solutionId, userId);
		}

	}

	@Id
	@Column(name = "SOLUTION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "SolutionID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionFavorite() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param userId
	 *                       User ID
	 */
	public MLPSolutionFavorite(String solutionId, String userId) {
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
	public MLPSolutionFavorite(MLPSolutionFavorite that) {
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
		if (!(that instanceof MLPSolutionFavorite))
			return false;
		MLPSolutionFavorite thatObj = (MLPSolutionFavorite) that;
		return Objects.equals(solutionId, thatObj.solutionId) && Objects.equals(userId, thatObj.userId);
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
