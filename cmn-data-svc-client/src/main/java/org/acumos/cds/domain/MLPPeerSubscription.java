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
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a peer subscription.
 */
@Entity
@Table(name = "C_PEER_SUB")
public class MLPPeerSubscription extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 1622011489027160908L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SUB_ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(readOnly = true, value = "Generated")
	private Long subId;

	@Column(name = "PEER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Peer ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String peerId;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "SCOPE_TYPE", nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Subscription scope type code", example = "RF")
	private String scopeType;

	@Column(name = "ACCESS_TYPE", nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Access type code", example = "PB")
	private String accessType;

	@Column(name = "SELECTOR", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	@ApiModelProperty(value = "Selector as JSON", example = "{ \"modelTypeCode\" : \"CL\" }")
	private String selector;

	@Column(name = "OPTIONS", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	@ApiModelProperty(value = "Options as JSON (not implemented)", example = "{ \"jsonTag\" : \"jsonValue\" }")
	private String options;

	@Column(name = "REFRESH_INTERVAL", columnDefinition = "INT")
	@ApiModelProperty(value = "Refresh interval", example = "60")
	private Long refreshInterval;

	@Column(name = "MAX_ARTIFACT_SIZE", columnDefinition = "INT")
	@ApiModelProperty(value = "Maximum artifact size")
	private Long maxArtifactSize;

	@Column(name = "PROCESSED_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Millisec since the Epoch", example = "1521202458867")
	private Date processed;

	/**
	 * No-arg constructor
	 */
	public MLPPeerSubscription() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits subscription ID, which is
	 * generated on save.
	 * 
	 * @param peerId
	 *                       Peer ID
	 * @param userId
	 *                       User ID, the operator
	 * @param scopeType
	 *                       Peer subscription scope type
	 * @param accessType
	 *                       Peer subscription access type
	 */
	public MLPPeerSubscription(String peerId, String userId, String scopeType, String accessType) {
		if (peerId == null || userId == null || scopeType == null || accessType == null)
			throw new IllegalArgumentException("Null not permitted");
		this.peerId = peerId;
		this.userId = userId;
		this.scopeType = scopeType;
		this.accessType = accessType;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPeerSubscription(MLPPeerSubscription that) {
		super(that);
		this.accessType = that.accessType;
		this.maxArtifactSize = that.maxArtifactSize;
		this.options = that.options;
		this.userId = that.userId;
		this.peerId = that.peerId;
		this.processed = that.processed;
		this.refreshInterval = that.refreshInterval;
		this.scopeType = that.scopeType;
		this.selector = that.selector;
		this.subId = that.subId;
	}

	public Long getSubId() {
		return subId;
	}

	public void setSubId(Long subId) {
		this.subId = subId;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * @return seconds
	 */
	public Long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @param refreshInterval
	 *                            seconds
	 */
	public void setRefreshInterval(Long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	/**
	 * @return bytes
	 */
	public Long getMaxArtifactSize() {
		return maxArtifactSize;
	}

	/**
	 * @param maxArtifactSize
	 *                            bytes
	 */
	public void setMaxArtifactSize(Long maxArtifactSize) {
		this.maxArtifactSize = maxArtifactSize;
	}

	public String getScopeType() {
		return scopeType;
	}

	/**
	 * @param scopeType
	 *                      A valid scope-type code
	 */
	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

	public String getAccessType() {
		return accessType;
	}

	/**
	 * @param accessType
	 *                       A valid access-type code
	 */
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Date getProcessed() {
		return processed;
	}

	public void setProcessed(Date created) {
		this.processed = created;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPPeerSubscription))
			return false;
		MLPPeerSubscription thatObj = (MLPPeerSubscription) that;
		return Objects.equals(subId, thatObj.subId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subId, peerId, selector, options);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[subId=" + subId + ", peerId=" + peerId + ", refreshInterval="
				+ refreshInterval + ", ...]";
	}

}
