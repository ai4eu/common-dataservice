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
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPNotifUserMap.NotifUserMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the notification-user mapping table. This is in lieu of a
 * many-to-one annotation on the user object
 */
@Entity
@IdClass(NotifUserMapPK.class)
@Table(name = "C_NOTIF_USER_MAP")
public class MLPNotifUserMap implements MLPEntity, Serializable {

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class NotifUserMapPK implements Serializable {

		private static final long serialVersionUID = 2003081937035770563L;

		private String notificationId;
		private String userId;

		public NotifUserMapPK() {
			// no-arg constructor
		}

		/**
		 * Builds an instance with the required fields.
		 * 
		 * @param notificationId
		 *                           Notification ID
		 * @param userId
		 *                           User ID
		 */
		public NotifUserMapPK(String notificationId, String userId) {
			this.notificationId = notificationId;
			this.userId = userId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof NotifUserMapPK))
				return false;
			NotifUserMapPK thatPK = (NotifUserMapPK) that;
			return Objects.equals(notificationId, thatPK.notificationId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(notificationId, userId);
		}

	}

	private static final long serialVersionUID = 1904416594943304721L;

	@Id
	@Column(name = "NOTIFICATION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Notification ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String notificationId;

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	// No auto-update behaviors here, neither Hibernate nor Mysql
	@Column(name = "VIEWED_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Millisec since the Epoch", example = "1521202458867")
	private Timestamp viewed;

	public MLPNotifUserMap() {
		// no-arg constructor
	}

	/**
	 * Builds an instance with the required fields.
	 * 
	 * @param notificationId
	 *                           Notification ID
	 * @param userId
	 *                           User ID
	 */
	public MLPNotifUserMap(String notificationId, String userId) {
		if (notificationId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.notificationId = notificationId;
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPNotifUserMap(MLPNotifUserMap that) {
		this.notificationId = that.notificationId;
		this.userId = that.userId;
		this.viewed = that.viewed;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getViewed() {
		return viewed;
	}

	public void setViewed(Timestamp viewed) {
		this.viewed = viewed;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPNotifUserMap))
			return false;
		MLPNotifUserMap thatObj = (MLPNotifUserMap) that;
		return Objects.equals(notificationId, thatObj.notificationId) && Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(notificationId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[notifcationId=" + notificationId + ", userId=" + userId + ", viewed="
				+ viewed + "]";
	}

}
