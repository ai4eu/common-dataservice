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

import java.util.HashMap;
import java.util.Map;

/**
 * Pagination and sorting information that can be transmitted via GET query
 * parameters. A sort criterion is technically optional but for reproducible
 * results one (or more) should always be specified.
 */
public class RestPageRequest {

	private Integer page;
	private Integer size;
	private Map<String, String> fieldToDirectionMap;

	/**
	 * Builds an empty object
	 */
	public RestPageRequest() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor that sets the field-to-direction map to null. This
	 * should only be used in special cases, because a field should always be
	 * specified to avoid random ordering of results.
	 * 
	 * @see #RestPageRequest(int, int, String...)
	 * @param page
	 *                 Page to fetch indexed from zero
	 * @param size
	 *                 Size of page
	 */
	public RestPageRequest(int page, int size) {
		this(page, size, (String[]) null);
	}

	/**
	 * Convenience constructor
	 * 
	 * @param page
	 *                   Page to fetch, indexed from zero
	 * @param size
	 *                   Size of page
	 * @param fields
	 *                   Sort fields, defaults to ascending order
	 */
	public RestPageRequest(int page, int size, String... fields) {
		this.page = page;
		this.size = size;
		if (fields != null && fields.length > 0) {
			fieldToDirectionMap = new HashMap<>();
			for (String f : fields)
				fieldToDirectionMap.put(f, "ASC");
		}
	}

	/**
	 * 
	 * @param page
	 *                          Page to fetch indexed from zero
	 * @param size
	 *                          Size of page
	 * @param fieldToDirMap
	 *                          Map of sort fields and direction ("ASC" or "DESC")
	 */
	public RestPageRequest(int page, int size, Map<String, String> fieldToDirMap) {
		this.page = page;
		this.size = size;
		this.fieldToDirectionMap = fieldToDirMap;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * Gets the field-to-direction map. This is initially null.
	 * 
	 * @return Map of sort field names and direction ("ASC" or "DESC")
	 * 
	 */
	public Map<String, String> getFieldToDirectionMap() {
		return fieldToDirectionMap;
	}

	/**
	 * Sets the field-to-direction map.
	 * 
	 * @param fieldToDirMap
	 *                          Map of sort field names and direction ("ASC" or
	 *                          "DESC")
	 */
	public void setFieldToDirectionMap(Map<String, String> fieldToDirMap) {
		this.fieldToDirectionMap = fieldToDirMap;
	}

}
