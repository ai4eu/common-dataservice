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
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPUserTagMap.UserTagMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the user-tag mapping table. This is in lieu of many-to-one
 * annotations.
 */
@Entity
@IdClass(UserTagMapPK.class)
@Table(name = MLPUserTagMap.TABLE_NAME)
public class MLPUserTagMap implements MLPEntity, Serializable {

	private static final long serialVersionUID = -4323298277808436884L;

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_USER_TAG_MAP";
	/* package */ static final String USER_ID_COL_NAME = "USER_ID";
	/* package */ static final String TAG_COL_NAME = "TAG";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class UserTagMapPK implements Serializable {

		private static final long serialVersionUID = -7947700720724160523L;
		private String userId;
		private String tag;

		public UserTagMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param userId
		 *                   user ID
		 * @param tag
		 *                   Tag
		 */
		public UserTagMapPK(String userId, String tag) {
			this.userId = userId;
			this.tag = tag;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof UserTagMapPK))
				return false;
			UserTagMapPK thatPK = (UserTagMapPK) that;
			return Objects.equals(userId, thatPK.userId) && Objects.equals(tag, thatPK.tag);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userId, tag);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[userId=" + userId + ", tag=" + tag + "]";
		}

	}

	@Id
	@Column(name = MLPUserTagMap.USER_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = MLPUserTagMap.TAG_COL_NAME, nullable = false, updatable = false, columnDefinition = "VARCHAR(32)")
	@Size(max = 32)
	@ApiModelProperty(required = true, example = "Tag1")
	private String tag;

	/**
	 * No-arg constructor
	 */
	public MLPUserTagMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param userId
	 *                   solution ID
	 * @param tag
	 *                   Tag
	 */
	public MLPUserTagMap(String userId, String tag) {
		if (userId == null || tag == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.tag = tag;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserTagMap(MLPUserTagMap that) {
		this.userId = that.userId;
		this.tag = that.tag;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserTagMap))
			return false;
		MLPUserTagMap thatObj = (MLPUserTagMap) that;
		return Objects.equals(userId, thatObj.userId) && Objects.equals(tag, thatObj.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, tag);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", tag=" + tag + "]";
	}

}
