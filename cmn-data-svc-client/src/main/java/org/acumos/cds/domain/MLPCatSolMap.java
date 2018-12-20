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
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for a row in the catalog - solution mapping table that defines a
 * catalog's members.
 */
@Entity
@IdClass(MLPCatSolMap.CatSolMapPK.class)
@Table(name = MLPCatSolMap.TABLE_NAME)
public class MLPCatSolMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -4441617858662760086L;

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_CAT_SOL_MAP";
	/* package */ static final String CAT_ID_COL_NAME = "CATALOG_ID";
	/* package */ static final String SOL_ID_COL_NAME = "SOLUTION_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class CatSolMapPK implements Serializable {

		private static final long serialVersionUID = 6204617233314697441L;
		private String catalogId;
		private String solutionId;

		public CatSolMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param catalogId
		 *                       catalog ID
		 * @param solutionId
		 *                       solution ID
		 */
		public CatSolMapPK(String catalogId, String solutionId) {
			this.catalogId = catalogId;
			this.solutionId = solutionId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof CatSolMapPK))
				return false;
			CatSolMapPK thatPK = (CatSolMapPK) that;
			return Objects.equals(catalogId, thatPK.catalogId) && Objects.equals(solutionId, thatPK.solutionId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(catalogId, solutionId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[catalogId=" + catalogId + ", solutionId=" + solutionId + "]";
		}

	}

	@Id
	@Column(name = MLPCatSolMap.CAT_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@ApiModelProperty(required = true, value = "Catalog ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	@Id
	@Column(name = MLPCatSolMap.SOL_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@CreationTimestamp
	@Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	// REST clients should not send this property
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Created date", example = "2018-12-16T12:34:56.789Z")
	private Instant created;

	/**
	 * No-arg constructor
	 */
	public MLPCatSolMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 *
	 * @param catalogId
	 *                       catalog ID
	 * @param solutionId
	 *                       solution ID
	 */
	public MLPCatSolMap(String catalogId, String solutionId) {
		if (catalogId == null || solutionId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.catalogId = catalogId;
		this.solutionId = solutionId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPCatSolMap(MLPCatSolMap that) {
		this.created = that.created;
		this.catalogId = that.catalogId;
		this.solutionId = that.solutionId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPCatSolMap))
			return false;
		MLPCatSolMap thatObj = (MLPCatSolMap) that;
		return Objects.equals(catalogId, thatObj.catalogId) && Objects.equals(solutionId, thatObj.solutionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(catalogId, solutionId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[catalogId=" + catalogId + ", solutionId=" + solutionId + "]";
	}

}
