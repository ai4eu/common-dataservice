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

package org.acumos.cds.transport;

import java.util.Objects;

/**
 * Trivial model to transport a pair of strings.
 */
public class AuthorTransport {
	private String name;
	private String contact;

	/**
	 * Builds an empty object.
	 */
	public AuthorTransport() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified values.
	 * 
	 * @param name
	 *                    name to transport.
	 * @param contact
	 *                    contact to transport.
	 */
	public AuthorTransport(String name, String contact) {
		this.name = name;
		this.contact = contact;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof AuthorTransport))
			return false;
		AuthorTransport thatObj = (AuthorTransport) that;
		return Objects.equals(name, thatObj.name) && Objects.equals(contact, thatObj.contact);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, contact);
	}

}
