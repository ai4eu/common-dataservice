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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "C_NOTIF_USER_PREF")
public class MLPUserNotifPref implements MLPEntity, Serializable {

	private static final long serialVersionUID = -2662643459576197438L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(readOnly = true, value = "Generated record ID")
	private Long userNotifPrefId;

	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "User ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "NOTIF_DELV_MECH_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Notification delivery mechanism code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Delivery mechanism code valid for this site", example = "EM")
	private String notfDelvMechCode;

	@Column(name = "MSG_SEVERITY_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Message severity code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Message severity code valid for this site", example = "LO")
	private String msgSeverityCode;

	/**
	 * No-arg constructor.
	 */
	public MLPUserNotifPref() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits record ID, which is generated
	 * on save.
	 * 
	 * @param userId
	 *            notification recipient
	 * @param notfDelvMechCode
	 *            user preferred notification delivery mechanism
	 * @param msgSeverityCode
	 *            message severity code
	 */
	public MLPUserNotifPref(String userId, String notfDelvMechCode, String msgSeverityCode) {
		if (userId == null || notfDelvMechCode == null || msgSeverityCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.notfDelvMechCode = notfDelvMechCode;
		this.msgSeverityCode = msgSeverityCode;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPUserNotifPref(MLPUserNotifPref that) {
		this.msgSeverityCode = that.msgSeverityCode;
		this.notfDelvMechCode = that.notfDelvMechCode;
		this.userId = that.userId;
		this.userNotifPrefId = that.userNotifPrefId;
	}

	public Long getUserNotifPrefId() {
		return userNotifPrefId;
	}

	public void setUserNotifPrefId(Long userNotifPrefId) {
		this.userNotifPrefId = userNotifPrefId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNotfDelvMechCode() {
		return notfDelvMechCode;
	}

	public void setNotfDelvMechCode(String notfDelvMechCode) {
		this.notfDelvMechCode = notfDelvMechCode;
	}

	public String getMsgSeverityCode() {
		return msgSeverityCode;
	}

	public void setMsgSeverityCode(String msgSeverityCode) {
		this.msgSeverityCode = msgSeverityCode;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserNotifPref))
			return false;
		MLPUserNotifPref thatObj = (MLPUserNotifPref) that;
		return Objects.equals(userNotifPrefId, thatObj.userNotifPrefId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userNotifPrefId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userNotifPrefId=" + userNotifPrefId + ", userId=" + userId
				+ ", notfDelvMechCode=" + notfDelvMechCode + ", msgSeverityCode=" + msgSeverityCode + "]";
	}

}
