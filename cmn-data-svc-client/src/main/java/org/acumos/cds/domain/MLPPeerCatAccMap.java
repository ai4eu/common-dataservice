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
 * Model for a row in the peer - catalog access mapping table. This is in lieu
 * of many-to-one annotations.
 */
@Entity
@IdClass(MLPPeerCatAccMap.PeerCatAccMapPK.class)
@Table(name = MLPPeerCatAccMap.TABLE_NAME)
public class MLPPeerCatAccMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -3899018504907344297L;
	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_PEER_CAT_ACC_MAP";
	/* package */ static final String PEER_ID_COL_NAME = "PEER_ID";
	/* package */ static final String CATALOG_ID_COL_NAME = "CATALOG_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class PeerCatAccMapPK implements Serializable {

		private static final long serialVersionUID = -5020178691226208809L;
		private String peerId;
		private String catalogId;

		public PeerCatAccMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param peerId
		 *                      peer ID
		 * @param catalogId
		 *                      catalog ID
		 */
		public PeerCatAccMapPK(String peerId, String catalogId) {
			this.peerId = peerId;
			this.catalogId = catalogId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof PeerCatAccMapPK))
				return false;
			PeerCatAccMapPK thatPK = (PeerCatAccMapPK) that;
			return Objects.equals(peerId, thatPK.peerId) && Objects.equals(catalogId, thatPK.catalogId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(peerId, catalogId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[peerId=" + peerId + ", catalogId=" + catalogId + "]";
		}

	}

	@Id
	@Column(name = MLPPeerCatAccMap.PEER_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Peer ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "Peer ID (UUID)", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String peerId;

	@Id
	@Column(name = MLPPeerCatAccMap.CATALOG_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Catalog ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "Catalog ID (UUID)", required = true, example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String catalogId;

	/**
	 * No-arg constructor
	 */
	public MLPPeerCatAccMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 *
	 * 
	 * @param peerId
	 *                      peer ID
	 * @param catalogId
	 *                      catalog ID
	 */
	public MLPPeerCatAccMap(String peerId, String catalogId) {
		if (peerId == null || catalogId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.peerId = peerId;
		this.catalogId = catalogId;

	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPeerCatAccMap(MLPPeerCatAccMap that) {
		this.peerId = that.peerId;
		this.catalogId = that.catalogId;

	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPPeerCatAccMap))
			return false;
		MLPPeerCatAccMap thatObj = (MLPPeerCatAccMap) that;
		return Objects.equals(peerId, thatObj.peerId) && Objects.equals(catalogId, thatObj.catalogId);
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(peerId, catalogId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[peerId=" + peerId + ", catalogId=" + catalogId + "]";
	}

}
