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

import java.time.Instant;
import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;

/**
 * A user notification object has all the notification fields plus a "viewed"
 * field. This is a transport model, to carry results of a HQL join out to
 * client, not an entity.
 */
public class MLPUserNotification extends MLPNotification {

	private static final long serialVersionUID = -6305213486711160636L;

	@ApiModelProperty(value = "Millisec since the Epoch", example = "1521202458867")
	private Instant viewed;

	/**
	 * No-arg constructor
	 */
	public MLPUserNotification() {
		// no-arg constructor
	}

	/**
	 * Constructor for use in repository method
	 * 
	 * @param notificationId
	 *                           Notification ID
	 * @param title
	 *                           Notification title
	 * @param message
	 *                           Notification message
	 * @param url
	 *                           Notification URL
	 * @param start
	 *                           Notification start timestamp
	 * @param end
	 *                           Notification end timestamp
	 * @param viewed
	 *                           Notification viewed timestamp
	 */
	public MLPUserNotification(String notificationId, String title, String message, String url, Instant start,
			Instant end, Instant viewed) {
		setNotificationId(notificationId);
		setTitle(title);
		setMessage(message);
		setUrl(url);
		setStart(start);
		setEnd(end);
		this.viewed = viewed;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserNotification(MLPUserNotification that) {
		super(that);
		this.viewed = that.viewed;
	}

	public Instant getViewed() {
		return viewed;
	}

	public void setViewed(Instant viewed) {
		this.viewed = viewed;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserNotification))
			return false;
		MLPUserNotification thatObj = (MLPUserNotification) that;
		return super.equals(that) && Objects.equals(viewed, thatObj.viewed);
	}

	@Override
	public int hashCode() {
		return super.hashCode() + Objects.hash(viewed);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[id=" + getNotificationId() + ", title=" + getTitle() + ", URL=" + getUrl()
				+ ", start=" + getStart() + ", end=" + getEnd() + ", viewed=" + this.viewed + "]";
	}

}
