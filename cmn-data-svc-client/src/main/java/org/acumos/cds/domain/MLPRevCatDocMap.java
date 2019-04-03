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

import org.acumos.cds.domain.MLPRevCatDocMap.RevCatDocMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the solution-revision-document mapping table. This is in
 * lieu of many-to-one annotations.
 */
@Entity
@IdClass(RevCatDocMapPK.class)
@Table(name = MLPRevCatDocMap.TABLE_NAME)
public class MLPRevCatDocMap implements MLPDomainModel, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5044016892125329643L;
	/* package */ static final String TABLE_NAME = "C_REV_CAT_DOC_MAP";
	/* package */ static final String REVISION_ID_COL_NAME = "REVISION_ID";
	/* package */ static final String CATALOG_ID_COL_NAME = "CATALOG_ID";
	/* package */ static final String DOCUMENT_ID_COL_NAME = "DOCUMENT_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class RevCatDocMapPK implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3991827180200458489L;
		private String revisionId;
		private String catalogId;
		private String documentId;

		public RevCatDocMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param revisionId
		 *                       revision ID
		 * @param catalogId
		 *                       catalog ID
		 * @param documentId
		 *                       document ID
		 */
		public RevCatDocMapPK(String revisionId, String catalogId, String documentId) {
			this.revisionId = revisionId;
			this.catalogId = catalogId;
			this.documentId = documentId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof RevCatDocMapPK))
				return false;
			RevCatDocMapPK thatPK = (RevCatDocMapPK) that;
			return Objects.equals(revisionId, thatPK.revisionId) && Objects.equals(catalogId, thatPK.catalogId)
					&& Objects.equals(documentId, thatPK.documentId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(revisionId, catalogId, documentId);
		}

	}

	@Id
	@Column(name = REVISION_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Revision ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Id
	@Column(name = CATALOG_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Catalog ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	@Id
	@Column(name = DOCUMENT_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Document ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String documentId;

	public MLPRevCatDocMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param revisionId
	 *                       revision ID
	 * @param catalogId
	 *                       catalog ID
	 * @param documentId
	 *                       document ID
	 */
	public MLPRevCatDocMap(String revisionId, String catalogId, String documentId) {
		if (revisionId == null || catalogId == null || documentId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.revisionId = revisionId;
		this.catalogId = catalogId;
		this.documentId = documentId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRevCatDocMap(MLPRevCatDocMap that) {
		this.revisionId = that.revisionId;
		this.catalogId = that.catalogId;
		this.documentId = that.documentId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRevCatDocMap))
			return false;
		MLPRevCatDocMap thatObj = (MLPRevCatDocMap) that;
		return Objects.equals(revisionId, thatObj.revisionId) && Objects.equals(catalogId, thatObj.catalogId)
				&& Objects.equals(documentId, thatObj.documentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, catalogId, documentId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + revisionId + ", accessTypeCode=" + catalogId
				+ ", documentId=" + documentId + "]";
	}

}
