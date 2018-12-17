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

package org.acumos.cds.test;

import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.acumos.cds.domain.MLPArtifactFOM;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.domain.MLPSolutionPicture;
import org.acumos.cds.domain.MLPSolutionRevisionFOM;
import org.acumos.cds.domain.MLPUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests getters and setters of server-side domain (model) classes.
 */
public class FOMDomainTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Values for properties
	final long time = new Date().getTime();
	final boolean b1 = true;
	final boolean b2 = false;
	final byte[] by1 = { 0, 1, 2, 3 };
	final Date d1 = new Date(time + 1 * 24 * 60 * 60 * 1000);
	final Date d2 = new Date(time + 2 * 24 * 60 * 60 * 1000);
	final Date d3 = new Date(time + 3 * 24 * 60 * 60 * 1000);
	final Date d4 = new Date(time + 4 * 24 * 60 * 60 * 1000);
	final Date d5 = new Date(time + 5 * 24 * 60 * 60 * 1000);
	final Integer i1 = 1;
	final Integer i2 = 2;
	final Integer i3 = 3;
	final Integer i4 = 4;
	final Integer i5 = 5;
	final Long l1 = 1L;
	final Long l2 = 2L;
	final Long l3 = 3L;
	final Long l4 = 4L;
	final String s1 = "string1";
	final String s2 = "string2";
	final String s3 = "string3";
	final String s4 = "string4";
	final String s5 = "string5";
	final String s6 = "string6";
	final String s7 = "string7";
	final String s8 = "string8";
	final String s9 = "string9";
	final String s10 = "string10";
	final String s11 = "string11";
	final String s12 = "string12";
	final MLPPeer peer1 = new MLPPeer();
	final MLPSolutionFOM sol1 = new MLPSolutionFOM();
	final MLPUser user1 = new MLPUser();
	final Set<MLPSolutionRevisionFOM> revs = new HashSet<>();
	final Set<MLPRevisionDescription> descs = new HashSet<>();

	@Before
	public void setup() {
		peer1.setPeerId("id");
		sol1.setSolutionId("id");
		user1.setUserId("id");
	}

	@Test
	public void testMLPArtifactFOM() throws URISyntaxException {
		MLPArtifactFOM m = new MLPArtifactFOM();
		m.setArtifactId(s1);
		m.setArtifactTypeCode(s2);
		m.setCreated(d1);
		m.setDescription(s3);
		m.setMetadata(s4);
		m.setModified(d2);
		m.setName(s5);
		m.setOwner(user1);
		m.setRevisions(revs);
		m.setSize(i1);
		m.setUri(s7);
		m.setVersion(s8);
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getArtifactTypeCode());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(s4, m.getMetadata());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s5, m.getName());
		Assert.assertEquals(user1, m.getOwner());
		Assert.assertTrue(revs == m.getRevisions());
		Assert.assertEquals(i1, m.getSize());
		Assert.assertEquals(s7, m.getUri());
		Assert.assertEquals(s8, m.getVersion());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		logger.info(m.toMLPArtifact().toString());
	}

	@Test
	public void testMLPSolutionFOM() {
		MLPSolutionFOM m = new MLPSolutionFOM();
		m.setActive(b1);
		m.setCreated(d1);
		m.setMetadata(s2);
		m.setModelTypeCode(s3);
		m.setModified(d2);
		m.setName(s4);
		m.setOrigin(s5);
		m.setOwner(user1);
		m.setSource(peer1);
		m.setRevisions(revs);
		m.setSolutionId(s7);
		m.setToolkitTypeCode(s8);
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getMetadata());
		Assert.assertEquals(s3, m.getModelTypeCode());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s4, m.getName());
		Assert.assertEquals(s5, m.getOrigin());
		Assert.assertEquals(user1, m.getOwner());
		Assert.assertEquals(peer1, m.getSource());
		Assert.assertTrue(revs == m.getRevisions());
		Assert.assertEquals(s7, m.getSolutionId());
		Assert.assertEquals(s8, m.getToolkitTypeCode());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		logger.info(m.toMLPSolution().toString());
	}

	@Test
	public void testMLPSolutionRevisionFOM() {
		Set<MLPArtifactFOM> arts = new HashSet<>();
		MLPSolutionRevisionFOM m = new MLPSolutionRevisionFOM();
		m.setAccessTypeCode(s1);
		m.setArtifacts(arts);
		m.setCreated(d1);
		m.setDescriptions(descs);
		m.setMetadata(s3);
		m.setModified(d2);
		m.setOrigin(s4);
		m.setPublisher(s5);
		m.setRevisionId(s6);
		m.setSolution(sol1);
		m.setSource(peer1);
		m.setUser(user1);
		m.setVersion(s8);
		Assert.assertTrue(arts == m.getArtifacts());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(descs, m.getDescriptions());
		Assert.assertEquals(s3, m.getMetadata());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s4, m.getOrigin());
		Assert.assertEquals(s5, m.getPublisher());
		Assert.assertEquals(s6, m.getRevisionId());
		Assert.assertEquals(sol1, m.getSolution());
		Assert.assertEquals(peer1, m.getSource());
		Assert.assertEquals(user1, m.getUser());
		Assert.assertEquals(s8, m.getVersion());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		logger.info(m.toMLPSolutionRevision().toString());
	}

	@Test
	public void testMLPSolutionImage() {
		MLPSolutionPicture m = new MLPSolutionPicture();
		m.setPicture(by1);
		Assert.assertEquals(by1, m.getPicture());
		logger.info(m.toString());
	}

}
