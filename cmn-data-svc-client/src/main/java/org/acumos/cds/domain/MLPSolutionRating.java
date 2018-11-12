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

import org.acumos.cds.domain.MLPSolutionRating.SolutionRatingPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a solution rating.
 */
@Entity
@IdClass(SolutionRatingPK.class)
@Table(name = "C_SOLUTION_RATING")
public class MLPSolutionRating extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 9215493501583093052L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolutionRatingPK implements Serializable {
		static final long serialVersionUID = -7002323089797601628L;
		private String solutionId;
		private String userId;

		public SolutionRatingPK() {
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
		public SolutionRatingPK(String solutionId, String userId) {
			this.solutionId = solutionId;
			this.userId = userId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolutionRatingPK))
				return false;
			SolutionRatingPK thatPK = (SolutionRatingPK) that;
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

	@Id
	@Column(name = "SOLUTION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "SolutionID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Id
	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "RATING", nullable = false, columnDefinition = "SMALLINT")
	@NotNull(message = "Rating cannot be null")
	@ApiModelProperty(required = true, example = "1")
	private Integer rating;

	@Column(name = "TEXT_REVIEW", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	@ApiModelProperty(example = "Free-text comments")
	private String textReview;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionRating() {
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
	 * @param rating
	 *                       Numeric rating
	 */
	public MLPSolutionRating(String solutionId, String userId, Integer rating) {
		if (solutionId == null || userId == null || rating == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.userId = userId;
		this.rating = rating;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolutionRating(MLPSolutionRating that) {
		super(that);
		this.rating = that.rating;
		this.solutionId = that.solutionId;
		this.textReview = that.textReview;
		this.userId = that.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getTextReview() {
		return textReview;
	}

	public void setTextReview(String textReview) {
		this.textReview = textReview;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolutionRating))
			return false;
		MLPSolutionRating thatPK = (MLPSolutionRating) that;
		return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(userId, thatPK.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(solutionId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + solutionId + ", userId=" + userId + ", rating=" + rating
				+ ", textReview=" + textReview + ", created=" + getCreated() + "]";
	}

}
