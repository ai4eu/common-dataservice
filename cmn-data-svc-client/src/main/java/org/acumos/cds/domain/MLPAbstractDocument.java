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
 * Base model for a user document. Maps all simple columns; maps no complex
 * columns that a subclass might want to map in alternate ways. For example the
 * user ID column is not mapped here; that is a reference to an MLPUser entity
 * ID, and could be exposed as a string or as an object via Hibernate magic.
 */
@MappedSuperclass
public abstract class MLPAbstractDocument extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -388921848287330617L;

	/* package */ static final String TABLE_NAME = "C_DOCUMENT";

	/**
	 * Must be an entry in the solution revision table
	 */
	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "DOCUMENT_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String documentId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Name cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, value = "Document name", example = "user-guide.rst")
	private String name;

	@Column(name = "VERSION", columnDefinition = "VARCHAR(25)")
	@Size(max = 25)
	@ApiModelProperty(value = "Free-text version string", example = "v1.0")
	private String version;

	@Column(name = "URI", nullable = false, columnDefinition = "VARCHAR(512)")
	@NotNull(message = "URI cannot be null")
	@Size(max = 512)
	@ApiModelProperty(required = true, value = "Document URI", example = "http://nexus.company.com/group/version/document_name")
	private String uri;

	@Column(name = "SIZE", nullable = false, columnDefinition = "INT")
	@ApiModelProperty(required = true, value = "Size in bytes on the disk", example = "65536")
	private Integer size;

	/**
	 * No-arg constructor
	 */
	public MLPAbstractDocument() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits document ID, which is generated
	 * on save.
	 * 
	 * @param name
	 *                 File name
	 * @param uri
	 *                 URI where document can be accessed
	 * @param size
	 *                 Size of document in bytes
	 * @throws IllegalArgumentException
	 *                                      if the URI violates RFC 2396
	 */
	public MLPAbstractDocument(String name, String uri, int size) {
		if (name == null || uri == null)
			throw new IllegalArgumentException("Null not permitted");
		setUri(uri);
		this.name = name;
		this.uri = uri;
		this.size = size;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPAbstractDocument(MLPAbstractDocument that) {
		super(that);
		this.documentId = that.documentId;
		this.name = that.name;
		this.size = that.size;
		this.uri = that.uri;
		this.version = that.version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[documentId=" + getDocumentId() + ", name=" + getName() + ", created="
				+ getCreated() + ", modified=" + getModified() + "]";
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

}
