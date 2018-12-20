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

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for a row in the peer group - peer group mapping table.
 */
@Entity
@IdClass(MLPPeerPeerAccMap.PeerPeerAccMapPK.class)
@Table(name = MLPPeerPeerAccMap.TABLE_NAME)
public class MLPPeerPeerAccMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -8184725585346886508L;

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_PEER_PEER_ACC_MAP";
	/* package */ static final String PRINCIPAL_GROUP_ID_COL_NAME = "PRINCIPAL_GROUP_ID";
	/* package */ static final String RESOURCE_GROUP_ID_COL_NAME = "RESOURCE_GROUP_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class PeerPeerAccMapPK implements Serializable {

		private static final long serialVersionUID = 2122352771147637818L;
		private Long principalPeerGroupId;
		private Long resourcePeerGroupId;

		public PeerPeerAccMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param principalPeerGroupId
		 *                                 peer group ID
		 * @param resourcePeerGroupId
		 *                                 peer group ID
		 */
		public PeerPeerAccMapPK(Long principalPeerGroupId, Long resourcePeerGroupId) {
			this.principalPeerGroupId = principalPeerGroupId;
			this.resourcePeerGroupId = resourcePeerGroupId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof PeerPeerAccMapPK))
				return false;
			PeerPeerAccMapPK thatPK = (PeerPeerAccMapPK) that;
			return Objects.equals(principalPeerGroupId, thatPK.principalPeerGroupId)
					&& Objects.equals(resourcePeerGroupId, thatPK.resourcePeerGroupId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(principalPeerGroupId, resourcePeerGroupId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[principalPeerGroupId=" + principalPeerGroupId
					+ ", resourcePeerGroupId=" + resourcePeerGroupId + "]";
		}

	}

	@Id
	@Column(name = MLPPeerPeerAccMap.PRINCIPAL_GROUP_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Principal peer group ID", example = "1")
	private Long principalPeerGroupId;

	@Id
	@Column(name = MLPPeerPeerAccMap.RESOURCE_GROUP_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Resource peer group ID", example = "2")
	private Long resourcePeerGroupId;

	@CreationTimestamp
	@Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	// REST clients should not send this property
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Created date", example = "2018-12-16T12:34:56.789Z")
	private Instant created;

	/**
	 * No-arg constructor
	 */
	public MLPPeerPeerAccMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 *
	 * 
	 * @param principalPeerGroupId
	 *                                 peer group ID
	 * @param resourcePeerGroupId
	 *                                 peer group ID
	 */
	public MLPPeerPeerAccMap(Long principalPeerGroupId, Long resourcePeerGroupId) {
		if (principalPeerGroupId == null || resourcePeerGroupId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.principalPeerGroupId = principalPeerGroupId;
		this.resourcePeerGroupId = resourcePeerGroupId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPeerPeerAccMap(MLPPeerPeerAccMap that) {
		this.principalPeerGroupId = that.principalPeerGroupId;
		this.resourcePeerGroupId = that.resourcePeerGroupId;
	}

	public Long getPrincipalPeerGroupId() {
		return principalPeerGroupId;
	}

	public void setPrincipalPeerGroupId(Long principalPeerGroupId) {
		this.principalPeerGroupId = principalPeerGroupId;
	}

	public Long getResourcePeerGroupId() {
		return resourcePeerGroupId;
	}

	public void setResourcePeerGroupId(Long resourcePeerGroupId) {
		this.resourcePeerGroupId = resourcePeerGroupId;
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
		if (!(that instanceof MLPPeerPeerAccMap))
			return false;
		MLPPeerPeerAccMap thatObj = (MLPPeerPeerAccMap) that;
		return Objects.equals(principalPeerGroupId, thatObj.principalPeerGroupId)
				&& Objects.equals(resourcePeerGroupId, thatObj.resourcePeerGroupId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(principalPeerGroupId, resourcePeerGroupId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[principalPeerGroupId=" + principalPeerGroupId + ", resourcePeerGroupId="
				+ resourcePeerGroupId + "]";
	}

}
