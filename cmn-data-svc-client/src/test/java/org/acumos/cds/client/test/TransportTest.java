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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.transport.CountTransport;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.LoginTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.transport.UsersRoleRequest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

/**
 * Tests getters and setters of transport classes.
 */
public class TransportTest extends AbstractModelTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void testCountTransport() {
		CountTransport t = new CountTransport();
		t = new CountTransport(l1);
		t.setCount(l1);
		Assert.assertEquals(l1, t.getCount());
		logger.info(t.toString());
	}

	@Test
	public void testErrorTransport() {
		ErrorTransport t = new ErrorTransport();
		t = new ErrorTransport(i1, s1);
		t = new ErrorTransport(i1, s1, new Exception());
		t.setError(s1);
		t.setException(s2);
		t.setStatus(i1);
		Assert.assertEquals(s1, t.getError());
		Assert.assertEquals(s2, t.getException());
		Assert.assertEquals(i1, t.getStatus());
		logger.info(t.toString());
	}

	@Test
	public void testLoginTransport() {
		LoginTransport t = new LoginTransport();
		t = new LoginTransport(s1, s1);
		t.setName(s1);
		t.setPass(s2);
		Assert.assertEquals(s1, t.getName());
		Assert.assertEquals(s2, t.getPass());
		logger.info(t.toString());
	}

	@Test
	public void tesRestPageRequest() {
		RestPageRequest t = new RestPageRequest();
		t = new RestPageRequest(i1, i2);
		t = new RestPageRequest(i1, i2, "field1");
		t.setPage(i1);
		t.setSize(i2);
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		fieldToDirectionMap.put("a", "b");
		t.setFieldToDirectionMap(fieldToDirectionMap);
		Assert.assertEquals(i1, t.getPage());
		Assert.assertEquals(i2, t.getSize());
		logger.info(t.toString());
	}

	@Test
	public void testRestPageResponse() {
		RestPageResponse<String> t = new RestPageResponse<>();
		t = new RestPageResponse<>(new ArrayList<String>());
		List<String> content = new ArrayList<>();
		int contentSize = 2;
		for (int i = 0; i < contentSize; ++i)
			content.add(Integer.toString(i));
		t = new RestPageResponse<>(content, PageRequest.of(0, 2), 4);
		Assert.assertTrue(t.isFirst());
		Assert.assertFalse(t.isLast());
		Assert.assertEquals((int) 0, t.getNumber());
		Assert.assertEquals((int) 2, t.getNumberOfElements());
		Assert.assertEquals((int) 2, t.getSize());
		Assert.assertEquals((long) 4, t.getTotalElements());
		Assert.assertEquals((long) 2, t.getTotalPages());
		Assert.assertNull(t.getSort());
		Assert.assertFalse(t.equals(null));
		Assert.assertFalse(t.equals(new RestPageResponse<>()));
		Assert.assertTrue(t.equals(t));
		Assert.assertNotNull(t.hashCode());
		logger.info(t.toString());
	}

	@Test
	public void testSuccessTransport() {
		SuccessTransport t = new SuccessTransport();
		t = new SuccessTransport(i1, s1);
		t.setData(s1);
		t.setStatus(i1);
		Assert.assertEquals(s1, t.getData());
		Assert.assertEquals((int) i1, t.getStatus());
		logger.info(t.toString());
	}

	@Test
	public void testUsersRoleRequest() {
		UsersRoleRequest t = new UsersRoleRequest();
		List<String> l = new ArrayList<>();
		t = new UsersRoleRequest(true, l, s1);
		t.setAdd(false);
		t.setRoleId(s1);
		t.setUserIds(l);
		Assert.assertEquals(false, t.isAdd());
		Assert.assertEquals(s1, t.getRoleId());
		Assert.assertEquals(l, t.getUserIds());
		logger.info(t.toString());
	}

}
