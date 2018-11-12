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

import org.acumos.cds.domain.MLPUserLoginProvider.UserLoginProviderPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for user information obtained from a social login provider.
 * 
 * Primary key is defined on userId, providerCode, providerUserId.
 */
@Entity
@IdClass(UserLoginProviderPK.class)
@Table(name = "C_USER_LOGIN_PROVIDER")
public class MLPUserLoginProvider extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 939345324516269571L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class UserLoginProviderPK implements Serializable {

		static final long serialVersionUID = 8381906956069338391L;
		private String userId;
		private String providerCode;
		private String providerUserId;

		public UserLoginProviderPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param userId
		 *                           user IDN
		 * @param providerCode
		 *                           provider code
		 * @param providerUserId
		 *                           provider user ID
		 */
		public UserLoginProviderPK(String userId, String providerCode, String providerUserId) {
			this.userId = userId;
			this.providerCode = providerCode;
			this.providerUserId = providerUserId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof UserLoginProviderPK))
				return false;
			UserLoginProviderPK thatPk = (UserLoginProviderPK) that;
			return Objects.equals(thatPk.userId, userId) && Objects.equals(thatPk.providerCode, providerCode)
					&& Objects.equals(thatPk.providerUserId, providerUserId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userId, providerCode, providerUserId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[userId=" + userId + ", providerCode=" + providerCode
					+ ", providerUserId=" + providerUserId + "]";
		}

	}

	@Id
	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = "PROVIDER_CD", nullable = false, updatable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "ProviderCode cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Login provider code valid for this site", example = "GH")
	private String providerCode;

	@Id
	@Column(name = "PROVIDER_USER_ID", nullable = false, columnDefinition = "VARCHAR(256)")
	@NotNull(message = "ProviderUserId cannot be null")
	@Size(max = 256)
	@ApiModelProperty(required = true, value = "User name at the login provider", example = "myuserid")
	private String providerUserId;

	@Column(name = "RANK", nullable = false, columnDefinition = "SMALLINT")
	@NotNull(message = "Rank cannot be null")
	@ApiModelProperty(required = true, value = "Relative rank among login providers")
	private Integer rank;

	@Column(name = "DISPLAY_NAME", columnDefinition = "VARCHAR(256)")
	@Size(max = 256)
	@ApiModelProperty(value = "Display name")
	private String displayName;

	@Column(name = "PROFILE_URL", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(value = "Profile URL")
	private String profileUrl;

	@Column(name = "IMAGE_URL", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(value = "Image URL")
	private String imageUrl;

	@Column(name = "SECRET", columnDefinition = "VARCHAR(256)")
	@Size(max = 256)
	@ApiModelProperty(value = "Shared secret")
	private String secret;

	@Column(name = "ACCESS_TOKEN", nullable = false, columnDefinition = "VARCHAR(256)")
	@NotNull(message = "AccessToken cannot be null")
	@Size(max = 256)
	@ApiModelProperty(required = true, value = "Access token")
	private String accessToken;

	@Column(name = "REFRESH_TOKEN", columnDefinition = "VARCHAR(256)")
	@Size(max = 256)
	@ApiModelProperty(value = "Refresh token")
	private String refreshToken;

	/**
	 * No-arg constructor
	 */
	public MLPUserLoginProvider() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param userId
	 *                           User ID
	 * @param providerCode
	 *                           Code for provider lookup table
	 * @param providerUserId
	 *                           User's ID at the provider
	 * @param accessToken
	 *                           Access token generated by the provider
	 * @param rank
	 *                           Ranking among user's login providers
	 */
	public MLPUserLoginProvider(String userId, String providerCode, String providerUserId, String accessToken,
			int rank) {
		if (userId == null || providerCode == null || providerUserId == null || accessToken == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.providerCode = providerCode;
		this.providerUserId = providerUserId;
		this.accessToken = accessToken;
		this.rank = rank;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserLoginProvider(MLPUserLoginProvider that) {
		super(that);
		this.accessToken = that.accessToken;
		this.displayName = that.displayName;
		this.imageUrl = that.imageUrl;
		this.profileUrl = that.profileUrl;
		this.providerCode = that.providerCode;
		this.providerUserId = that.providerUserId;
		this.rank = that.rank;
		this.refreshToken = that.refreshToken;
		this.secret = that.secret;
		this.userId = that.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserLoginProvider))
			return false;
		MLPUserLoginProvider thatObj = (MLPUserLoginProvider) that;
		return Objects.equals(thatObj.userId, userId) && Objects.equals(thatObj.providerCode, providerCode)
				&& Objects.equals(thatObj.providerUserId, providerUserId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, providerCode, providerUserId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", providerCode=" + providerCode + ", providerUserId="
				+ providerUserId + ", ...]";
	}

}
