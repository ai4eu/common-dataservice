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
import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;

/**
 * Base model for a hyperlink. Maps all simple columns; maps no complex
 * columns that a subclass might want to map in alternate ways.
 */
@MappedSuperclass
public abstract class MLPAbstractHyperlink extends MLPTimestampedEntity implements Serializable {	

	private static final long serialVersionUID = 2051565654403834500L;

	/* package */ static final String TABLE_NAME = "C_HYPERLINK";

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "HYPERLINK_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String hyperlinkId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@Size(max = 100)
	@ApiModelProperty(required = true, value = "Hyperlink name", example = "Remote solution")
	private String name;

	@Column(name = "URI", nullable = false, columnDefinition = "VARCHAR(512)")
	@NotNull(message = "URI")
	@Size(max = 512)
	@ApiModelProperty(required = true, value = "Hyperlink URI", example = "http://mywebsite.com/dataset/data.json")
	private String uri;

	/**
	 * No-arg constructor
	 */
	public MLPAbstractHyperlink() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits hyperlink ID, which is generated
	 * on save.
	 * 
	 * @param name
	 *                 Hyperlink name
	 * @param uri
	 *                 URI of the resource pointed by this hyperlink
	 * @throws IllegalArgumentException
	 *                                      if the URI violates RFC 2396
	 */
	public MLPAbstractHyperlink(String name, String uri) {
		if (uri == null)
			throw new IllegalArgumentException("Null not permitted");
		setUri(uri);
		this.name = name;
		this.uri = uri;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPAbstractHyperlink(MLPAbstractHyperlink that) {
		super(that);
		this.hyperlinkId = that.hyperlinkId;
		this.name = that.name;
		this.uri = that.uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	/**
	 * Sets the URI.
	 * 
	 * @param uri
	 *                The URI to set
	 * @throws IllegalArgumentException
	 *                                      if the value is not null and violates
	 *                                      RFC 2396
	 */
	public void setUri(String uri) {
		if (uri != null)
			try {
				new URI(uri);
			} catch (URISyntaxException ex) {
				throw new IllegalArgumentException(ex);
			}
		this.uri = uri;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[hyperlink=" + getHyperlinkId() + ", name=" + getName() + ", created="
				+ getCreated() + ", modified=" + getModified() + "]";
	}

	public String getHyperlinkId() {
		return hyperlinkId;
	}

	public void setHyperlinkId(String hyperlinkId) {
		this.hyperlinkId = hyperlinkId;
	}

}
