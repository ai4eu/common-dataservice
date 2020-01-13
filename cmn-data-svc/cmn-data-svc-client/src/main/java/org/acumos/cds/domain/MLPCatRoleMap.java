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

import org.acumos.cds.domain.MLPCatRoleMap.CatalogRoleMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the catalog-role mapping table. This is in lieu of
 * many-to-one annotations.
 */
@Entity
@IdClass(CatalogRoleMapPK.class)
@Table(name = "C_CAT_ROLE_MAP")
public class MLPCatRoleMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -2838371756150754964L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class CatalogRoleMapPK implements Serializable {

		private static final long serialVersionUID = -5793904223614948578L;
		private String catalogId;
		private String roleId;

		public CatalogRoleMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param catalogId
		 *                      catalog ID
		 * @param roleId
		 *                      role ID
		 */
		public CatalogRoleMapPK(String catalogId, String roleId) {
			this.catalogId = catalogId;
			this.roleId = roleId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof CatalogRoleMapPK))
				return false;
			CatalogRoleMapPK thatPK = (CatalogRoleMapPK) that;
			return Objects.equals(catalogId, thatPK.catalogId) && Objects.equals(roleId, thatPK.roleId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(catalogId, roleId);
		}

	}

	@Id
	@Column(name = "CATALOG_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Catalog ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Catalog ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	@Id
	@Column(name = "ROLE_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Role ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Role ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String roleId;

	public MLPCatRoleMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the
	 * catalog must supply to create a valid instance.
	 * 
	 * @param catalogId
	 *                      catalog ID
	 * @param roleId
	 *                      role ID
	 */
	public MLPCatRoleMap(String catalogId, String roleId) {
		if (catalogId == null || roleId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.catalogId = catalogId;
		this.roleId = roleId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPCatRoleMap(MLPCatRoleMap that) {
		this.catalogId = that.catalogId;
		this.roleId = that.roleId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPCatRoleMap))
			return false;
		MLPCatRoleMap thatObj = (MLPCatRoleMap) that;
		return Objects.equals(catalogId, thatObj.catalogId) && Objects.equals(roleId, thatObj.roleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(catalogId, roleId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[catalogId=" + catalogId + ", roleId=" + roleId + "]";
	}

}
