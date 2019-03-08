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

import org.acumos.cds.domain.MLPSolRevDocMap.SolRevDocMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the solution-revision-document mapping table. This is in
 * lieu of many-to-one annotations.
 */
@Entity
@IdClass(SolRevDocMapPK.class)
@Table(name = MLPSolRevDocMap.TABLE_NAME)
public class MLPSolRevDocMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -1521666035352658419L;
	/* package */ static final String TABLE_NAME = "C_SOL_REV_DOC_MAP";
	/* package */ static final String REVISION_ID_COL_NAME = "REVISION_ID";
	/* package */ static final String ACCESS_TYPE_CODE_COL_NAME = "ACCESS_TYPE_CD";
	/* package */ static final String DOCUMENT_ID_COL_NAME = "DOCUMENT_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolRevDocMapPK implements Serializable {

		private static final long serialVersionUID = 2467981222252697720L;
		private String revisionId;
		private String accessTypeCode;
		private String documentId;

		public SolRevDocMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param revisionId
		 *                           revision ID
		 * @param accessTypeCode
		 *                           public, private etc.
		 * @param documentId
		 *                           document ID
		 */
		public SolRevDocMapPK(String revisionId, String accessTypeCode, String documentId) {
			this.revisionId = revisionId;
			this.accessTypeCode = accessTypeCode;
			this.documentId = documentId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolRevDocMapPK))
				return false;
			SolRevDocMapPK thatPK = (SolRevDocMapPK) that;
			return Objects.equals(revisionId, thatPK.revisionId)
					&& Objects.equals(accessTypeCode, thatPK.accessTypeCode)
					&& Objects.equals(documentId, thatPK.documentId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(revisionId, accessTypeCode, documentId);
		}

	}

	@Id
	@Column(name = REVISION_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Revision ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Id
	@Column(name = ACCESS_TYPE_CODE_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Access type code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Access type code", example = "PB")
	private String accessTypeCode;

	@Id
	@Column(name = DOCUMENT_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Document ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String documentId;

	public MLPSolRevDocMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param revisionId
	 *                           revision ID
	 * @param accessTypeCode
	 *                           access type code
	 * @param documentId
	 *                           document ID
	 */
	public MLPSolRevDocMap(String revisionId, String accessTypeCode, String documentId) {
		if (revisionId == null || accessTypeCode == null || documentId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.revisionId = revisionId;
		this.accessTypeCode = accessTypeCode;
		this.documentId = documentId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolRevDocMap(MLPSolRevDocMap that) {
		this.revisionId = that.revisionId;
		this.accessTypeCode = that.accessTypeCode;
		this.documentId = that.documentId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getAccessTypeCode() {
		return accessTypeCode;
	}

	public void setAccessTypeCode(String accessTypeCode) {
		this.accessTypeCode = accessTypeCode;
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
		if (!(that instanceof MLPSolRevDocMap))
			return false;
		MLPSolRevDocMap thatObj = (MLPSolRevDocMap) that;
		return Objects.equals(revisionId, thatObj.revisionId) && Objects.equals(accessTypeCode, thatObj.accessTypeCode)
				&& Objects.equals(documentId, thatObj.documentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, accessTypeCode, documentId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + revisionId + ", accessTypeCode=" + accessTypeCode
				+ ", documentId=" + documentId + "]";
	}

}
