/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the user - catalog favorite mapping table.
 */
@Entity
@IdClass(MLPUserCatFavMap.UserCatFavMapPK.class)
@Table(name = MLPUserCatFavMap.TABLE_NAME)
public class MLPUserCatFavMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -6453404733408485102L;
	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_USER_CAT_FAV_MAP";
	/* package */ static final String USER_ID_COL_NAME = "USER_ID";
	/* package */ static final String CATALOG_ID_COL_NAME = "CATALOG_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class UserCatFavMapPK implements Serializable {

		private static final long serialVersionUID = 7659377533787614089L;
		private String userId;
		private String catalogId;

		public UserCatFavMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param userId
		 *                      User ID
		 * @param catalogId
		 *                      Catalog ID
		 */
		public UserCatFavMapPK(String userId, String catalogId) {
			this.userId = userId;
			this.catalogId = catalogId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof UserCatFavMapPK))
				return false;
			UserCatFavMapPK thatPK = (UserCatFavMapPK) that;
			return Objects.equals(userId, thatPK.userId) && Objects.equals(catalogId, thatPK.catalogId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userId, catalogId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[userId=" + userId + ", catalogId=" + catalogId + "]";
		}

	}

	@Id
	@Column(name = MLPUserCatFavMap.USER_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "User ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID (UUID)", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = MLPUserCatFavMap.CATALOG_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Catalog ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Catalog ID (UUID)", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	/**
	 * No-arg constructor
	 */
	public MLPUserCatFavMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 *
	 * 
	 * @param userId
	 *                      user ID
	 * @param catalogId
	 *                      catalog ID
	 */
	public MLPUserCatFavMap(String userId, String catalogId) {
		if (userId == null || catalogId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.catalogId = catalogId;

	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserCatFavMap(MLPUserCatFavMap that) {
		this.userId = that.userId;
		this.catalogId = that.catalogId;

	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserCatFavMap))
			return false;
		MLPUserCatFavMap thatObj = (MLPUserCatFavMap) that;
		return Objects.equals(userId, thatObj.userId) && Objects.equals(catalogId, thatObj.catalogId);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, catalogId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", catalogId=" + catalogId + "]";
	}

}
