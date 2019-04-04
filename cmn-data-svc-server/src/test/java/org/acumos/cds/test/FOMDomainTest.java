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
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.acumos.cds.domain.MLPArtifactFOM;
import org.acumos.cds.domain.MLPCatalog;
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
	final boolean b1 = true;
	final boolean b2 = false;
	final byte[] by1 = { 0, 1, 2, 3 };
	final Instant t1 = Instant.now().plusSeconds(1 * 24 * 60 * 60);
	final Instant t2 = Instant.now().plusSeconds(2 * 24 * 60 * 60);
	final Instant t3 = Instant.now().plusSeconds(3 * 24 * 60 * 60);
	final Instant t4 = Instant.now().plusSeconds(4 * 24 * 60 * 60);
	final Instant t5 = Instant.now().plusSeconds(5 * 24 * 60 * 60);
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
	final Set<MLPUser> accessUsers = new HashSet<>();
	final Set<MLPCatalog> catalogs = new HashSet<>();

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
		m.setCreated(t1);
		m.setDescription(s3);
		m.setMetadata(s4);
		m.setModified(t2);
		m.setName(s5);
		m.setOwner(user1);
		m.setRevisions(revs);
		m.setSize(i1);
		m.setUri(s7);
		m.setVersion(s8);
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getArtifactTypeCode());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(s4, m.getMetadata());
		Assert.assertEquals(t2, m.getModified());
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
		m.setAccessUsers(accessUsers);
		m.setActive(b1);
		m.setCatalogs(catalogs);
		m.setCreated(t1);
		m.setMetadata(s2);
		m.setModelTypeCode(s3);
		m.setModified(t2);
		m.setName(s4);
		m.setOrigin(s5);
		m.setSource(peer1);
		m.setRevisions(revs);
		m.setSolutionId(s7);
		m.setToolkitTypeCode(s8);
		m.setUser(user1);
		Assert.assertEquals(accessUsers, m.getAccessUsers());
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(catalogs, m.getCatalogs());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s2, m.getMetadata());
		Assert.assertEquals(s3, m.getModelTypeCode());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s4, m.getName());
		Assert.assertEquals(s5, m.getOrigin());
		Assert.assertEquals(peer1, m.getSource());
		Assert.assertEquals(revs, m.getRevisions());
		Assert.assertEquals(s7, m.getSolutionId());
		Assert.assertEquals(s8, m.getToolkitTypeCode());
		Assert.assertEquals(user1, m.getUser());
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
		m.setArtifacts(arts);
		m.setCreated(t1);
		m.setDescriptions(descs);
		m.setMetadata(s1);
		m.setModified(t2);
		m.setOrigin(s2);
		m.setPublisher(s3);
		m.setRevisionId(s4);
		m.setSolution(sol1);
		m.setSource(peer1);
		m.setUser(user1);
		m.setVersion(s5);
		Assert.assertTrue(arts == m.getArtifacts());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(descs, m.getDescriptions());
		Assert.assertEquals(s1, m.getMetadata());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s2, m.getOrigin());
		Assert.assertEquals(s3, m.getPublisher());
		Assert.assertEquals(s4, m.getRevisionId());
		Assert.assertEquals(sol1, m.getSolution());
		Assert.assertEquals(peer1, m.getSource());
		Assert.assertEquals(user1, m.getUser());
		Assert.assertEquals(s5, m.getVersion());
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
