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

import org.acumos.cds.domain.MLPSolTagMap.SolTagMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the solution-tag mapping table. This is in lieu of
 * many-to-one annotations.
 */
@Entity
@IdClass(SolTagMapPK.class)
@Table(name = MLPSolTagMap.TABLE_NAME)
public class MLPSolTagMap implements MLPDomainModel, Serializable {

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_SOL_TAG_MAP";
	/* package */ static final String SOL_ID_COL_NAME = "SOLUTION_ID";
	/* package */ static final String TAG_COL_NAME = "TAG";

	private static final long serialVersionUID = -7814665924253912856L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolTagMapPK implements Serializable {

		private static final long serialVersionUID = -503957020456645384L;
		private String solutionId;
		private String tag;

		public SolTagMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param solutionId
		 *                       solution ID
		 * @param tag
		 *                       Tag
		 */
		public SolTagMapPK(String solutionId, String tag) {
			this.solutionId = solutionId;
			this.tag = tag;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolTagMapPK))
				return false;
			SolTagMapPK thatPK = (SolTagMapPK) that;
			return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(tag, thatPK.tag);
		}

		@Override
		public int hashCode() {
			return Objects.hash(solutionId, tag);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[solutionId=" + solutionId + ", tag=" + tag + "]";
		}

	}

	@Id
	@Column(name = MLPSolTagMap.SOL_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Solution ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Id
	@Column(name = MLPSolTagMap.TAG_COL_NAME, nullable = false, updatable = false, columnDefinition = "VARCHAR(32)")
	@NotNull(message = "Tag cannot be null")
	@Size(max = 32)
	@ApiModelProperty(required = true, example = "Tag1")
	private String tag;

	/**
	 * No-arg constructor
	 */
	public MLPSolTagMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param solutionId
	 *                       solution ID
	 * @param tag
	 *                       Tag
	 */
	public MLPSolTagMap(String solutionId, String tag) {
		if (solutionId == null || tag == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.tag = tag;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolTagMap(MLPSolTagMap that) {
		this.solutionId = that.solutionId;
		this.tag = that.tag;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolTagMap))
			return false;
		MLPSolTagMap thatObj = (MLPSolTagMap) that;
		return Objects.equals(solutionId, thatObj.solutionId) && Objects.equals(tag, thatObj.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(solutionId, tag);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + solutionId + ", tag=" + tag + "]";
	}

}
