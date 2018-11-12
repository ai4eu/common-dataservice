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

import org.acumos.cds.domain.MLPRevisionDescription.RevDescPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * This revision description entity supports the Portal feature of storing
 * different descriptions for a given access type and a single model (revision).
 * E.g., store a company-visible description and a public-visible description
 * for the same thing without creating separate CDS revisions. Expected to
 * contain HTML (not plain text, not a binary stream).
 * 
 * Was originally stored in a different database; this will allow migration.
 */
@Entity
@IdClass(RevDescPK.class)
@Table(name = "C_REVISION_DESC")
public class MLPRevisionDescription extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 6949987343881297064L;

	/* package */ static final String REVISION_ID_COL_NAME = "REVISION_ID";
	/* package */ static final String ACCESS_TYPE_CODE_COL_NAME = "ACCESS_TYPE_CD";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class RevDescPK implements Serializable {

		private static final long serialVersionUID = -5574867760483934826L;
		private String revisionId;
		private String accessTypeCode;

		public RevDescPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param revisionId
		 *                           revision ID
		 * @param accessTypeCode
		 *                           access type code
		 */
		public RevDescPK(String revisionId, String accessTypeCode) {
			this.revisionId = revisionId;
			this.accessTypeCode = accessTypeCode;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof RevDescPK))
				return false;
			RevDescPK thatPK = (RevDescPK) that;
			return Objects.equals(revisionId, thatPK.revisionId)
					&& Objects.equals(accessTypeCode, thatPK.accessTypeCode);
		}

		@Override
		public int hashCode() {
			return Objects.hash(revisionId, accessTypeCode);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[revisionId=" + revisionId + ", accessTypeCode=" + accessTypeCode + "]";
		}

	}

	/**
	 * Must be an entry in the solution revision table
	 */
	@Id
	@Column(name = REVISION_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Revision ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	/**
	 * The Access Type Code value set is defined by server-side configuration.
	 */
	@Id
	@Column(name = ACCESS_TYPE_CODE_COL_NAME, nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Access type code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(value = "Access type code that is valid for this site", example = "PB")
	private String accessTypeCode;

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
	@Size(max = 2 * 1024 * 1024)
	@ApiModelProperty(value = "Text description up to 2MB")
	private String description;

	/**
	 * No-arg constructor
	 */
	public MLPRevisionDescription() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param revisionId
	 *                           Revision ID
	 * @param accessTypeCode
	 *                           Access type code appropriate for the site
	 * @param description
	 *                           Description text
	 */
	public MLPRevisionDescription(String revisionId, String accessTypeCode, String description) {
		if (revisionId == null || accessTypeCode == null || description == null)
			throw new IllegalArgumentException("Null not permitted");
		this.revisionId = revisionId;
		this.accessTypeCode = accessTypeCode;
		this.description = description;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRevisionDescription(MLPRevisionDescription that) {
		super(that);
		this.revisionId = that.revisionId;
		this.accessTypeCode = that.accessTypeCode;
		this.description = that.description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + getRevisionId() + ", accessTypeCode=" + getAccessTypeCode()
				+ ", description=" + getDescription() + ", created=" + getCreated() + ", modified=" + getModified()
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, accessTypeCode);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRevisionDescription))
			return false;
		MLPRevisionDescription thatObj = (MLPRevisionDescription) that;
		return Objects.equals(revisionId, thatObj.revisionId) && Objects.equals(accessTypeCode, thatObj.accessTypeCode)
				&& Objects.equals(description, thatObj.description);
	}

}
