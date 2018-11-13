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
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPSolRevArtMap.SolRevArtMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the solution-revision-artifact mapping table. This is in
 * lieu of many-to-one annotations.
 */
@Entity
@IdClass(SolRevArtMapPK.class)
@Table(name = MLPSolRevArtMap.TABLE_NAME)
public class MLPSolRevArtMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 1721531464277548759L;

	/* package */ static final String TABLE_NAME = "C_SOL_REV_ART_MAP";
	/* package */ static final String REVISION_ID_COL_NAME = "REVISION_ID";
	/* package */ static final String ARTIFACT_ID_COL_NAME = "ARTIFACT_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolRevArtMapPK implements Serializable {

		private static final long serialVersionUID = -8398992391141954629L;
		private String revisionId;
		private String artifactId;

		public SolRevArtMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param revisionId
		 *                       revision ID
		 * @param artifactId
		 *                       artifact ID
		 */
		public SolRevArtMapPK(String revisionId, String artifactId) {
			this.revisionId = revisionId;
			this.artifactId = artifactId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolRevArtMapPK))
				return false;
			SolRevArtMapPK thatPK = (SolRevArtMapPK) that;
			return Objects.equals(revisionId, thatPK.revisionId) && Objects.equals(artifactId, thatPK.artifactId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(revisionId, artifactId);
		}

	}

	@Id
	@Column(name = REVISION_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Id
	@Column(name = ARTIFACT_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String artifactId;

	public MLPSolRevArtMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param revisionId
	 *                       revision ID
	 * @param artifactId
	 *                       artifact ID
	 */
	public MLPSolRevArtMap(String revisionId, String artifactId) {
		if (revisionId == null || artifactId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.revisionId = revisionId;
		this.artifactId = artifactId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolRevArtMap(MLPSolRevArtMap that) {
		this.artifactId = that.artifactId;
		this.revisionId = that.revisionId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolRevArtMap))
			return false;
		MLPSolRevArtMap thatObj = (MLPSolRevArtMap) that;
		return Objects.equals(revisionId, thatObj.revisionId) && Objects.equals(artifactId, thatObj.artifactId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, artifactId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + revisionId + ", artifactId=" + artifactId + "]";
	}

}
