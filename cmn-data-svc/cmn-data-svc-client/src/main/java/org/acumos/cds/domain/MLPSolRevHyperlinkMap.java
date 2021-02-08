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

import org.acumos.cds.domain.MLPSolRevHyperlinkMap.SolRevHyperlinkMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the (solution-)revision-hyperlink mapping table. This is in
 * lieu of many-to-one annotations.
 */
@Entity
@IdClass(SolRevHyperlinkMapPK.class)
@Table(name = MLPSolRevHyperlinkMap.TABLE_NAME)
public class MLPSolRevHyperlinkMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 8874636412884364041L;

	/* package */ static final String TABLE_NAME = "C_SOL_REV_HYPERLINK_MAP";
	/* package */ static final String REVISION_ID_COL_NAME = "REVISION_ID";
	/* package */ static final String HYPERLINK_ID_COL_NAME = "HYPERLINK_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolRevHyperlinkMapPK implements Serializable {

		private static final long serialVersionUID = -6867485258876699884L;

		private String revisionId;
		private String hyperlinkId;

		public SolRevHyperlinkMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param revisionId
		 *                       revision ID
		 * @param hyperlinkId
		 *                       hyperlink ID
		 */
		public SolRevHyperlinkMapPK(String revisionId, String hyperlinkId) {
			this.revisionId = revisionId;
			this.hyperlinkId = hyperlinkId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolRevHyperlinkMapPK))
				return false;
			SolRevHyperlinkMapPK thatPK = (SolRevHyperlinkMapPK) that;
			return Objects.equals(revisionId, thatPK.revisionId) && Objects.equals(hyperlinkId, thatPK.hyperlinkId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(revisionId, hyperlinkId);
		}

	}

	@Id
	@Column(name = REVISION_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Revision ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Id
	@Column(name = HYPERLINK_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Hyperlink ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String hyperlinkId;

	public MLPSolRevHyperlinkMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param revisionId
	 *                       revision ID
	 * @param hyperlinkId
	 *                       hyperlink ID
	 */
	public MLPSolRevHyperlinkMap(String revisionId, String hyperlinkId) {
		if (revisionId == null || hyperlinkId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.revisionId = revisionId;
		this.hyperlinkId = hyperlinkId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolRevHyperlinkMap(MLPSolRevHyperlinkMap that) {
		this.hyperlinkId = that.hyperlinkId;
		this.revisionId = that.revisionId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getHyperlinkId() {
		return hyperlinkId;
	}

	public void setHyperlinkId(String hyperlinkId) {
		this.hyperlinkId = hyperlinkId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolRevHyperlinkMap))
			return false;
		MLPSolRevHyperlinkMap thatObj = (MLPSolRevHyperlinkMap) that;
		return Objects.equals(revisionId, thatObj.revisionId) && Objects.equals(hyperlinkId, thatObj.hyperlinkId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, hyperlinkId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + revisionId + ", hyperlinkId=" + hyperlinkId + "]";
	}

}
