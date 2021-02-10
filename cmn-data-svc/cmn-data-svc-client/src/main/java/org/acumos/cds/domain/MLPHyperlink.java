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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model for a user-provided hyperlink referencing a resource that
 * supports a model.
 * 
 * The hyperlink should NOT reference a @MLPAbstractDocument,
 * a @MLPAbstractArtifact, or a @MLPAbstractSolution as these
 * entities are related to the solution revisions in separate mapping
 * tables.
 * 
 * Inherits all simple field mappings from the abstract superclass.
 * 
 * Participates in a many-to-many relationship with a solution revision via a
 * mapping table, but has no annotations for that.
 */
@Entity
@Table(name = MLPAbstractHyperlink.TABLE_NAME)
public class MLPHyperlink extends MLPAbstractHyperlink implements Serializable {

	private static final long serialVersionUID = -9093136818605540603L;

	/**
	 * No-arg constructor.
	 */
	public MLPHyperlink() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits document ID, which is generated
	 * on save.
	 * 
	 * @param name
	 *                   Name
	 * @param uri
	 *                   URI where the document can be accessed
	 * @throws IllegalArgumentException
	 *                                      if the URI value violates RFC 2396
	 */
	public MLPHyperlink(String name, String uri) {
		super(name, uri);
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPHyperlink(MLPHyperlink that) {
		super(that);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[hyperlinkId=" + getHyperlinkId() + ", name=" + getName()
		+ ", uri=" + getUri() + ", created=" + getCreated()
				+ ", modified=" + getModified() + "]";
	}

}
