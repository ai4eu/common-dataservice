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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Models the structure of a Spring Page container that transports results of
 * type T in response to a query for a chunk of content.
 * 
 * Example result from a find-all repository method in Spring-Boot 1.5:
 * 
 * <PRE>
  { 
    "content": [ "1", "2" ],
    "first":true,
    "last":false,
    "number":0,
    "numberOfElements":2,
    "size":2,
    "sort":[{"direction":"ASC","property":"name","ignoreCase":false,"nullHandling":"NATIVE","ascending":true,"descending":false}],
    "totalElements":328,
    "totalPages":164
  }
 * </PRE>
 * 
 * Example result from a find-all repository method in Spring-Boot 2.1:
 * 
 * <PRE>
{
   "content": [ "3" ],
   "pageable": {
     "sort": {
       "sorted": false,
       "unsorted": true,
       "empty": true
     },
     "offset": 0,
     "pageSize": 20,
     "pageNumber": 0,
    "unpaged": false,
    "paged": true
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "number": 0,
  "numberOfElements": 1,
  "first": true,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "empty": false
} *
 * </PRE>
 * 
 * Supposed to preserve the chunk request parameters but only keeps the page
 * number and page size; parses yet fails to preserve the sort criteria.
 * 
 * https://blog.thecookinkitchen.com/how-to-consume-page-response-from-a-service-in-spring-boot-97293c18ba
 * https://jira.spring.io/browse/DATACMNS-1061
 * 
 * @param <T>
 *                Wrapped model class
 */
public class RestPageResponse<T> extends PageImpl<T> {

	private static final long serialVersionUID = 5835593096562217592L;

	/**
	 * No-arg constructor builds an object with no contents.
	 */
	public RestPageResponse() {
		super(new ArrayList<T>());
	}

	/**
	 * Builds an object with the specified list as the contents.
	 * 
	 * @param content
	 *                    List of content
	 */
	public RestPageResponse(List<T> content) {
		super(content);
	}

	/**
	 * Builds an object with the specified content.
	 * 
	 * @param content
	 *                     List of content
	 * @param pageable
	 *                     Object that implements Pageable
	 * @param total
	 *                     Total count of elements in all pages
	 */
	public RestPageResponse(List<T> content, Pageable pageable, long total) {
		super(content, pageable, total);
	}

	/*
	 * Constructor for the Jackson deserializer. Discards the values of fields
	 * pageable, sort, totalPages, first, last and numberOfElements. All but sort
	 * can be computed from the content, number, size and totalElements; sort is
	 * discarded.
	 * 
	 * https://stackoverflow.com/questions/34647303/spring-resttemplate-with-
	 * paginated-api
	 */
	@SuppressWarnings("unused")
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public RestPageResponse(//
			@JsonProperty("content") List<T> content, // PageImpl
			@JsonProperty("number") int number, // PageImpl
			@JsonProperty("size") int size, // PageImpl
			@JsonProperty("totalElements") long totalElements, // PageImpl
			@JsonProperty("pageable") JsonNode pageable, //
			@JsonProperty("sort") JsonNode sort, //
			@JsonProperty("totalPages") int totalPages, // computed
			@JsonProperty("first") boolean first, // computed
			@JsonProperty("last") boolean last, // computed
			@JsonProperty("empty") boolean empty, // computed
			@JsonProperty("numberOfElements") int numberOfElements // computed
	) {
		super(content, PageRequest.of(number, size), totalElements);
	}

	private Sort sort;

	// @JsonDeserialize(using = CustomSortDeserializer.class)
	public void setSort(Sort sort) {
		this.sort = sort;
	}

	@Override
	public Sort getSort() {
		return sort;
	}
}
