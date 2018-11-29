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
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for site content, basically a key-value store for binary content.
 */
@Entity
@Table(name = "C_SITE_CONTENT")
public class MLPSiteContent extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -8139581648225717817L;

	// Alas the column name "KEY" isn't usable in most databases
	@Id
	@Column(name = "CONTENT_KEY", nullable = false, updatable = false, columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	@ApiModelProperty(required = true, value = "Unique key", example = "site_config_key_1")
	private String contentKey;

	/**
	 * Derby BLOB type allows 2GB. Mysql/Mariadb BLOB type only allows 64KB, that's
	 * too small. But Derby fails to create the table if type LONGBLOB is specified
	 * here. With no columDefinition attribute Derby generates a table AND Spring
	 * validates the MariaDB schema if the column is created as LONGBLOB.
	 * 
	 * Jackson handles base64 encoding.
	 */
	@Lob
	@Column(name = "CONTENT_VAL", nullable = false, length = 2000000 /* DO NOT USE: columnDefinition = "BLOB" */)
	@ApiModelProperty(value = "Value as byte array")
	private byte[] contentValue;

	@Column(name = "MIME_TYPE", nullable = false, columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String mimeType;

	/**
	 * No-arg constructor.
	 */
	public MLPSiteContent() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param contentKey
	 *                         Row ID
	 * @param contentValue
	 *                         Byte array
	 * @param mimeType
	 *                         MIME type of the content value
	 */
	public MLPSiteContent(String contentKey, byte[] contentValue, String mimeType) {
		if (contentKey == null || contentValue == null || mimeType == null)
			throw new IllegalArgumentException("Null not permitted");
		this.contentKey = contentKey;
		this.contentValue = contentValue;
		this.mimeType = mimeType;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSiteContent(MLPSiteContent that) {
		super(that);
		this.contentKey = that.contentKey;
		this.contentValue = that.contentValue;
		this.mimeType = that.mimeType;
	}

	public String getContentKey() {
		return contentKey;
	}

	public void setContentKey(String contentKey) {
		this.contentKey = contentKey;
	}

	public byte[] getContentValue() {
		return contentValue;
	}

	public void setContentValue(byte[] contentValue) {
		this.contentValue = contentValue;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSiteContent))
			return false;
		MLPSiteContent thatObj = (MLPSiteContent) that;
		return Objects.equals(contentKey, thatObj.contentKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contentKey, contentValue, mimeType);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[key=" + contentKey + ", value length="
				+ (contentValue == null ? "null" : Integer.toString(contentValue.length)) + ", mimeType=" + mimeType
				+ ", created=" + getCreated() + ", ...]";
	}

}
