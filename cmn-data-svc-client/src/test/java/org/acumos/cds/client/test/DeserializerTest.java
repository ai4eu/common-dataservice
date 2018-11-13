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
package org.acumos.cds.client.test;

import java.lang.invoke.MethodHandles;

import org.acumos.cds.transport.RestPageResponse;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeserializerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Content emitted by Spring-Boot v1.5.x REST endpoint
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testRestPageResponse15() {
		String springBoot15 = "{" //
				+ " \"content\":[\"1\",\"2\",\"3\"],\"first\":true,\"last\":false, " //
				+ " \"number\":0,\"numberOfElements\":3,\"size\":3,\"totalElements\":90,\"totalPages\":30, " //
				+ " \"sort\":[{\"direction\":\"ASC\",\"property\":\"name\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":true,\"descending\":false}] "//
				+ " }";
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode sb15 = mapper.readTree(springBoot15);
			logger.info("testRestPageResponse15: JSON in {}", sb15.toString());
			RestPageResponse r = mapper.readValue(springBoot15, RestPageResponse.class);
			logger.info("testRestPageResponse15: JSON out {}", mapper.writeValueAsString(r));
			Assert.assertNotNull(r.getContent());
			Assert.assertEquals(r.getContent().size(), 3);
			Assert.assertTrue(r.isFirst());
			Assert.assertFalse(r.isLast());
			Assert.assertEquals(r.getNumber(), 0);
			Assert.assertEquals(r.getNumberOfElements(), 3);
			Assert.assertEquals(r.getSize(), 3);
			Assert.assertEquals(r.getTotalPages(), 30);
			Assert.assertEquals(r.getTotalElements(), 90);
		} catch (Exception ex) {
			logger.error("testRestPageResponse15 failed", ex);
		}
	}

	/*
	 * Content emitted by Spring-Boot v2.1.0 REST endpoint
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testRestPageResponse21() {
		String springBoot21 = "{"//
				+ " \"content\":[\"4\",\"5\",\"6\"], \"empty\": false, "//
				+ " \"pageable\": { \"sort\": {\"sorted\": false, \"unsorted\": true, \"empty\": true },"
				+ " \"offset\": 0, \"pageSize\": 20, \"pageNumber\": 0, \"unpaged\": false, \"paged\": true },"
				+ " \"last\": true, \"totalPages\": 1, \"totalElements\": 1, \"size\": 20, \"number\": 0, "
				+ " \"numberOfElements\": 1,  \"first\": true, \"sort\": { \"sorted\": false, \"unsorted\": true, \"empty\": true }"
				+ "}";
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode sb21 = mapper.readTree(springBoot21);
			logger.info("testRestPageResponse21: JSON in {}", sb21.toString());
			RestPageResponse s = mapper.readValue(springBoot21, RestPageResponse.class);
			logger.info("testRestPageResponse21: JSON out {}", mapper.writeValueAsString(s));
			Assert.assertNotNull(s.getContent());
			Assert.assertEquals(s.getContent().size(), 3);
			Assert.assertTrue(s.isFirst());
			Assert.assertTrue(s.isLast());
			Assert.assertEquals(s.getNumber(), 0);
			Assert.assertEquals(s.getNumberOfElements(), 3);
			Assert.assertEquals(s.getSize(), 20);
			Assert.assertEquals(s.getTotalPages(), 1);
			Assert.assertEquals(s.getTotalElements(), 3);

		} catch (Exception ex) {
			logger.error("testRestPageResponse21 failed", ex);
		}
	}

}
