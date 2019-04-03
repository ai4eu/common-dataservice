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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPRevCatDescription.RevCatDescriptionPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * This entity supports the feature of catalog-specific descriptions for every
 * solution revision. E.g., store a cat-1 description that is different from
 * cat-2 description for the same revision without creating separate CDS
 * revisions. Expected to contain HTML (not plain text, not a binary stream).
 */
@Entity
@IdClass(RevCatDescriptionPK.class)
@Table(name = "C_REV_CAT_DESC")
public class MLPRevCatDescription extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 1506072750532903976L;
	/* package */ static final String REVISION_ID_COL_NAME = "REVISION_ID";
	/* package */ static final String CATALOG_ID_COL_NAME = "CATALOG_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class RevCatDescriptionPK implements Serializable {

		private static final long serialVersionUID = -1127705264162077675L;
		private String revisionId;
		private String catalogId;

		public RevCatDescriptionPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param revisionId
		 *                       revision ID
		 * @param catalogId
		 *                       catalog ID
		 */
		public RevCatDescriptionPK(String revisionId, String catalogId) {
			this.revisionId = revisionId;
			this.catalogId = catalogId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof RevCatDescriptionPK))
				return false;
			RevCatDescriptionPK thatPK = (RevCatDescriptionPK) that;
			return Objects.equals(revisionId, thatPK.revisionId) && Objects.equals(catalogId, thatPK.catalogId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(catalogId, revisionId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[revisionId=" + revisionId + ", catalogId=" + catalogId + "]";
		}

	}

	/**
	 * Must be an entry in the solution revision table
	 */
	@Id
	@Column(name = REVISION_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Revision ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Revision ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	/**
	 * Must be an entry in the catalog table
	 */
	@Id
	@Column(name = CATALOG_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Catalog ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Catalog ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	/**
	 * Description text. Use a generous limit to allow encoded in-line images.
	 *
	 * Derby supports CLOB (up to 2GB); Mysql/Mariadb supports LONGTEXT (4GB).
	 * Without a column definition Hibernate assumes LONGTEXT for MariaDB. Omit the
	 * Hibernate column definition in the column annotation so this works on both
	 * databases.
	 */
	@Lob
	@Column(name = "DESCRIPTION", nullable = false)
	@NotNull(message = "Description cannot be null")
	@Size(max = 2 * 1024 * 1024)
	@ApiModelProperty(required = true, value = "Text description up to 2MB")
	private String description;

	/**
	 * No-arg constructor
	 */
	public MLPRevCatDescription() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param revisionId
	 *                        Revision ID
	 * @param catalogId
	 *                        Catalog ID
	 * @param description
	 *                        Description text
	 */
	public MLPRevCatDescription(String revisionId, String catalogId, String description) {
		if (revisionId == null || catalogId == null || description == null)
			throw new IllegalArgumentException("Null not permitted");
		this.revisionId = revisionId;
		this.catalogId = catalogId;
		this.description = description;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRevCatDescription(MLPRevCatDescription that) {
		super(that);
		this.revisionId = that.revisionId;
		this.catalogId = that.catalogId;
		this.description = that.description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + getRevisionId() + ", catalogId=" + getCatalogId()
				+ ", description=" + getDescription() + ", created=" + getCreated() + ", modified=" + getModified()
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, catalogId);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRevCatDescription))
			return false;
		MLPRevCatDescription thatObj = (MLPRevCatDescription) that;
		return Objects.equals(revisionId, thatObj.revisionId) && Objects.equals(catalogId, thatObj.catalogId)
				&& Objects.equals(description, thatObj.description);
	}

}
