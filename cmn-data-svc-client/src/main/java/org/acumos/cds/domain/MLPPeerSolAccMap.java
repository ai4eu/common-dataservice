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
 * Model for a row in the peer group - solution group mapping table. This is in
 * lieu of many-to-one annotations.
 */
@Entity
@IdClass(MLPPeerSolAccMap.PeerSolAccMapPK.class)
@Table(name = MLPPeerSolAccMap.TABLE_NAME)
public class MLPPeerSolAccMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -7224936316204257890L;

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_PEER_SOL_ACC_MAP";
	/* package */ static final String PEER_GROUP_ID_COL_NAME = "PEER_GROUP_ID";
	/* package */ static final String SOL_GROUP_ID_COL_NAME = "SOL_GROUP_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class PeerSolAccMapPK implements Serializable {

		private static final long serialVersionUID = -7542694444490460426L;

		private Long peerGroupId;
		private Long solutionGroupId;

		public PeerSolAccMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param peerGroupId
		 *                        peer group ID
		 * @param solGroupId
		 *                        solution group ID
		 */
		public PeerSolAccMapPK(Long peerGroupId, Long solGroupId) {
			this.peerGroupId = peerGroupId;
			this.solutionGroupId = solGroupId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof PeerSolAccMapPK))
				return false;
			PeerSolAccMapPK thatPK = (PeerSolAccMapPK) that;
			return Objects.equals(peerGroupId, thatPK.peerGroupId)
					&& Objects.equals(solutionGroupId, thatPK.solutionGroupId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(peerGroupId, solutionGroupId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[peerGroupId=" + peerGroupId + ", solutionGroupId=" + solutionGroupId
					+ "]";
		}

	}

	@Id
	@Column(name = MLPPeerSolAccMap.PEER_GROUP_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Peer group ID", example = "1")
	private Long peerGroupId;

	@Id
	@Column(name = MLPPeerSolAccMap.SOL_GROUP_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Solution group ID", example = "1")
	private Long solutionGroupId;

	@Column(name = "GRANTED_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
	@ApiModelProperty(required = true)
	private boolean granted;

	@CreationTimestamp
	@Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	// REST clients should not send this property
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY)
	private Instant created;

	/**
	 * No-arg constructor
	 */
	public MLPPeerSolAccMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 *
	 * 
	 * @param peerGroupId
	 *                        peer group ID
	 * @param solGroupId
	 *                        solution group ID
	 * @param granted
	 *                        Access granted
	 */
	public MLPPeerSolAccMap(Long peerGroupId, Long solGroupId, boolean granted) {
		if (peerGroupId == null || solGroupId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.peerGroupId = peerGroupId;
		this.solutionGroupId = solGroupId;
		this.granted = granted;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPeerSolAccMap(MLPPeerSolAccMap that) {
		this.created = that.created;
		this.granted = that.granted;
		this.peerGroupId = that.peerGroupId;
		this.solutionGroupId = that.solutionGroupId;
	}

	public Long getPeerGroupId() {
		return peerGroupId;
	}

	public void setPeerGroupId(Long peerGroupId) {
		this.peerGroupId = peerGroupId;
	}

	public Long getSolutionGroupId() {
		return solutionGroupId;
	}

	public void setSolutionGroupId(Long solutionGroupId) {
		this.solutionGroupId = solutionGroupId;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
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
		if (!(that instanceof MLPPeerSolAccMap))
			return false;
		MLPPeerSolAccMap thatObj = (MLPPeerSolAccMap) that;
		return Objects.equals(peerGroupId, thatObj.peerGroupId)
				&& Objects.equals(solutionGroupId, thatObj.solutionGroupId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(peerGroupId, solutionGroupId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[peerGroupId=" + peerGroupId + ", solutionGroupId=" + solutionGroupId + "]";
	}

}
