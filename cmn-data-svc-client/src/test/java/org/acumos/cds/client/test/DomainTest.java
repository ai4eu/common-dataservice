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
import java.net.URISyntaxException;

import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifactType;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPCompSolMap;
import org.acumos.cds.domain.MLPDeploymentStatus;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPLoginProvider;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerGrpMemMap;
import org.acumos.cds.domain.MLPPeerPeerAccMap;
import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.acumos.cds.domain.MLPPeerStatus;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolGrpMemMap;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolRevDocMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionGroup;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPUserRoleMap;
import org.acumos.cds.domain.MLPUserTagMap;
import org.acumos.cds.transport.AuthorTransport;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests getters and setters of client-side domain (model) classes.
 */
@SuppressWarnings("deprecation")
public class DomainTest extends AbstractModelTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private void checkMLPArtifact(MLPArtifact m) {
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getArtifactTypeCode());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(s4, m.getMetadata());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s5, m.getName());
		Assert.assertEquals(s6, m.getUserId());
		Assert.assertEquals(i1, m.getSize());
		Assert.assertEquals(u1, m.getUri());
		Assert.assertEquals(s8, m.getVersion());
	}

	@Test
	public void testMLPArtifact() throws URISyntaxException {
		MLPArtifact m = new MLPArtifact(s1, s1, s1, s1, s1, i1);
		m = new MLPArtifact();
		m.setArtifactId(s1);
		m.setArtifactTypeCode(s2);
		m.setCreated(d1);
		m.setDescription(s3);
		m.setMetadata(s4);
		m.setModified(d2);
		m.setName(s5);
		m.setUserId(s6);
		m.setSize(i1);
		m.setUri(u1);
		m.setVersion(s8);
		checkMLPArtifact(m);
		m = new MLPArtifact(m);
		checkMLPArtifact(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPArtifact(null, null, null, null, null, 0);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		try {
			m.setUri("http://");
			Assert.assertTrue("Unexpected success", false);
		} catch (URISyntaxException ex) {
			// bogus URI is rejected
		}
	}

	@Test
	public void testMLPCodeNamePair() {
		MLPCodeNamePair m = new MLPCodeNamePair(s1, s1);
		m = new MLPCodeNamePair();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			String n = null;
			new MLPPeerGroup(n);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPComment(MLPComment m) {
		Assert.assertEquals(s1, m.getCommentId());
		Assert.assertEquals(s2, m.getParentId());
		Assert.assertEquals(s3, m.getText());
		Assert.assertEquals(s4, m.getThreadId());
		Assert.assertEquals(s6, m.getUserId());
	}

	@Test
	public void testMLPComment() {
		MLPComment m = new MLPComment(s1, s2, s3);
		m = new MLPComment();
		m.setCommentId(s1);
		m.setParentId(s2);
		m.setText(s3);
		m.setThreadId(s4);
		m.setUserId(s6);
		checkMLPComment(m);
		m = new MLPComment(m);
		checkMLPComment(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPComment(null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPCompSolMap(MLPCompSolMap m) {
		Assert.assertEquals(s1, m.getParentId());
		Assert.assertEquals(s2, m.getChildId());
	}

	@Test
	public void testMLPCompSolMap() {
		MLPCompSolMap m = new MLPCompSolMap(s1, s1);
		m = new MLPCompSolMap();
		m.setParentId(s1);
		m.setChildId(s2);
		checkMLPCompSolMap(m);
		m = new MLPCompSolMap(m);
		checkMLPCompSolMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPCompSolMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPCompSolMap.CompSolMapPK pk = new MLPCompSolMap.CompSolMapPK();
		pk = new MLPCompSolMap.CompSolMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPDocument(MLPDocument m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDocumentId());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s2, m.getName());
		Assert.assertEquals(i1, m.getSize());
		Assert.assertEquals(u1, m.getUri());
		Assert.assertEquals(s4, m.getUserId());
		Assert.assertEquals(s5, m.getVersion());
	}

	@Test
	public void testMLPDocument() throws URISyntaxException {
		MLPDocument m = new MLPDocument(s1, s1, i1, s1);
		m = new MLPDocument();
		m.setCreated(d1);
		m.setDocumentId(s1);
		m.setModified(d2);
		m.setName(s2);
		m.setSize(i1);
		m.setUri(u1);
		m.setUserId(s4);
		m.setVersion(s5);
		checkMLPDocument(m);
		m = new MLPDocument(m);
		checkMLPDocument(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPDocument(null, null, 0, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		try {
			m.setUri("http://");
			Assert.assertTrue("Unexpected success", false);
		} catch (URISyntaxException ex) {
			// bogus URI is rejected
		}
	}

	private void checkMLPNotification(MLPNotification m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getEnd());
		Assert.assertEquals(s1, m.getMessage());
		Assert.assertEquals(d3, m.getModified());
		Assert.assertEquals(s2, m.getNotificationId());
		Assert.assertEquals(d4, m.getStart());
		Assert.assertEquals(s3, m.getTitle());
		Assert.assertEquals(s4, m.getUrl());
		Assert.assertEquals(s6, m.getMsgSeverityCode());
	}

	@Test
	public void testMLPNotification() {
		MLPNotification m = new MLPNotification(s1, s6, d1, d1);
		m = new MLPNotification();
		m.setCreated(d1);
		m.setEnd(d2);
		m.setMessage(s1);
		m.setModified(d3);
		m.setNotificationId(s2);
		m.setStart(d4);
		m.setTitle(s3);
		m.setUrl(s4);
		m.setMsgSeverityCode(s6);
		checkMLPNotification(m);
		m = new MLPNotification(m);
		checkMLPNotification(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPNotification(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPNotifUserMap(MLPNotifUserMap m) {
		Assert.assertEquals(s1, m.getNotificationId());
		Assert.assertEquals(s2, m.getUserId());
		Assert.assertEquals(d1, m.getViewed());
	}

	@Test
	public void testMLPNotifUserMap() {
		MLPNotifUserMap m = new MLPNotifUserMap();
		m = new MLPNotifUserMap(s1, s2);
		m.setNotificationId(s1);
		m.setUserId(s2);
		m.setViewed(d1);
		checkMLPNotifUserMap(m);
		m = new MLPNotifUserMap(m);
		checkMLPNotifUserMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPNotifUserMap.NotifUserMapPK pk = new MLPNotifUserMap.NotifUserMapPK();
		pk = new MLPNotifUserMap.NotifUserMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
		try {
			new MLPNotifUserMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPPasswordChangeRequest(MLPPasswordChangeRequest m) {
		Assert.assertEquals(s1, m.getNewLoginPass());
		Assert.assertEquals(s2, m.getOldLoginPass());
	}

	@Test
	public void testMLPPasswordChangeRequest() {
		MLPPasswordChangeRequest m = new MLPPasswordChangeRequest(s1, s1);
		m = new MLPPasswordChangeRequest();
		m.setNewLoginPass(s1);
		m.setOldLoginPass(s2);
		checkMLPPasswordChangeRequest(m);
		m = new MLPPasswordChangeRequest(m);
		checkMLPPasswordChangeRequest(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPasswordChangeRequest(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPPeer(MLPPeer m) {
		Assert.assertEquals(s1, m.getApiUrl());
		Assert.assertEquals(s2, m.getContact1());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(b1, m.isLocal());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s4, m.getName());
		Assert.assertEquals(s5, m.getPeerId());
		Assert.assertEquals(b2, m.isSelf());
		Assert.assertEquals(s6, m.getStatusCode());
		Assert.assertEquals(s7, m.getSubjectName());
		Assert.assertEquals(s9, m.getWebUrl());
	}

	@Test
	public void testMLPPeer() {
		MLPPeer m = new MLPPeer(s1, s1, s1, b1, b1, s1, s1);
		m = new MLPPeer();
		m.setApiUrl(s1);
		m.setContact1(s2);
		m.setCreated(d1);
		m.setDescription(s3);
		m.setLocal(b1);
		m.setModified(d2);
		m.setName(s4);
		m.setPeerId(s5);
		m.setSelf(b2);
		m.setStatusCode(s6);
		m.setSubjectName(s7);
		m.setWebUrl(s9);
		checkMLPPeer(m);
		m = new MLPPeer(m);
		checkMLPPeer(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPeer(null, null, null, b1, b1, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPPeerGroup(MLPPeerGroup m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(l1, m.getGroupId());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s2, m.getName());
	}

	@Test
	public void testMLPPeerGroup() {
		MLPPeerGroup m = new MLPPeerGroup(s1);
		m = new MLPPeerGroup();
		m.setCreated(d1);
		m.setDescription(s1);
		m.setGroupId(l1);
		m.setModified(d2);
		m.setName(s2);
		checkMLPPeerGroup(m);
		m = new MLPPeerGroup(m);
		checkMLPPeerGroup(m);
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			String n = null;
			new MLPPeerGroup(n);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPPeerSubscription(MLPPeerSubscription m) {
		Assert.assertEquals(s1, m.getAccessType());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(l1, m.getMaxArtifactSize());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s2, m.getOptions());
		Assert.assertEquals(s3, m.getUserId());
		Assert.assertEquals(s4, m.getPeerId());
		Assert.assertEquals(d3, m.getProcessed());
		Assert.assertEquals(l2, m.getRefreshInterval());
		Assert.assertEquals(s5, m.getScopeType());
		Assert.assertEquals(s6, m.getSelector());
		Assert.assertEquals(l3, m.getSubId());
	}

	@Test
	public void testMLPPeerSubscription() {
		MLPPeerSubscription m = new MLPPeerSubscription(s1, s2, s3, s4);
		m = new MLPPeerSubscription();
		m.setAccessType(s1);
		m.setCreated(d1);
		m.setMaxArtifactSize(l1);
		m.setModified(d2);
		m.setOptions(s2);
		m.setUserId(s3);
		m.setPeerId(s4);
		m.setProcessed(d3);
		m.setRefreshInterval(l2);
		m.setScopeType(s5);
		m.setSelector(s6);
		m.setSubId(l3);
		checkMLPPeerSubscription(m);
		m = new MLPPeerSubscription(m);
		checkMLPPeerSubscription(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPeerSubscription(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPPeerGrpMemMap(MLPPeerGrpMemMap m) {
		Assert.assertEquals(l1, m.getGroupId());
		Assert.assertEquals(s1, m.getPeerId());
	}

	@Test
	public void testMLPPeerGrpMemMap() {
		MLPPeerGrpMemMap m = new MLPPeerGrpMemMap(l1, s1);
		m = new MLPPeerGrpMemMap();
		m.setGroupId(l1);
		m.setPeerId(s1);
		checkMLPPeerGrpMemMap(m);
		m = new MLPPeerGrpMemMap(m);
		checkMLPPeerGrpMemMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPPeerGrpMemMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPPeerGrpMemMap.PeerGrpMemMapPK pk = new MLPPeerGrpMemMap.PeerGrpMemMapPK();
		pk = new MLPPeerGrpMemMap.PeerGrpMemMapPK(l1, s1);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPPeerPeerAccMap(MLPPeerPeerAccMap m) {
		Assert.assertEquals(l1, m.getPrincipalPeerGroupId());
		Assert.assertEquals(l2, m.getResourcePeerGroupId());
	}

	@Test
	public void testMLPPeerPeerAccMap() {
		MLPPeerPeerAccMap m = new MLPPeerPeerAccMap(l1, l2);
		m = new MLPPeerPeerAccMap();
		m.setPrincipalPeerGroupId(l1);
		m.setResourcePeerGroupId(l2);
		checkMLPPeerPeerAccMap(m);
		m = new MLPPeerPeerAccMap(m);
		checkMLPPeerPeerAccMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPPeerPeerAccMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPPeerPeerAccMap.PeerPeerAccMapPK pk = new MLPPeerPeerAccMap.PeerPeerAccMapPK();
		pk = new MLPPeerPeerAccMap.PeerPeerAccMapPK(l1, l2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPPeerSolAccMap(MLPPeerSolAccMap m) {
		Assert.assertEquals(l1, m.getPeerGroupId());
		Assert.assertEquals(l2, m.getSolutionGroupId());
	}

	@Test
	public void testMLPPeerSolAccMap() {
		MLPPeerSolAccMap m = new MLPPeerSolAccMap(l1, l2, true);
		m = new MLPPeerSolAccMap();
		m.setPeerGroupId(l1);
		m.setSolutionGroupId(l2);
		checkMLPPeerSolAccMap(m);
		m = new MLPPeerSolAccMap(m);
		checkMLPPeerSolAccMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPPeerSolAccMap(null, null, false);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPPeerSolAccMap.PeerSolAccMapPK pk = new MLPPeerSolAccMap.PeerSolAccMapPK();
		pk = new MLPPeerSolAccMap.PeerSolAccMapPK(l1, l2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPPublishRequest(MLPPublishRequest m) {
		Assert.assertEquals(s1, m.getComment());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertTrue(l1 == m.getRequestId());
		Assert.assertEquals(s2, m.getRequestUserId());
		Assert.assertEquals(s3, m.getReviewUserId());
		Assert.assertEquals(s4, m.getRevisionId());
		Assert.assertEquals(s5, m.getSolutionId());
		Assert.assertEquals(s6, m.getStatusCode());
	}

	@Test
	public void testMLPPublishRequest() {
		MLPPublishRequest m = new MLPPublishRequest(s1, s2, s3, s4);
		m = new MLPPublishRequest();
		m.setComment(s1);
		m.setCreated(d1);
		m.setModified(d2);
		m.setRequestId(l1);
		m.setRequestUserId(s2);
		m.setReviewUserId(s3);
		m.setRevisionId(s4);
		m.setSolutionId(s5);
		m.setStatusCode(s6);
		checkMLPPublishRequest(m);
		m = new MLPPublishRequest(m);
		checkMLPPublishRequest(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPublishRequest(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPRevisionDescription(MLPRevisionDescription m) {
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getDescription());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s3, m.getRevisionId());
	}

	@Test
	public void testMLPRevisionDescription() {
		MLPRevisionDescription m = new MLPRevisionDescription(s1, s1, s1);
		m = new MLPRevisionDescription();
		m.setAccessTypeCode(s1);
		m.setCreated(d1);
		m.setDescription(s2);
		m.setModified(d2);
		m.setRevisionId(s3);
		checkMLPRevisionDescription(m);
		m = new MLPRevisionDescription(m);
		checkMLPRevisionDescription(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPRevisionDescription(null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPRole(MLPRole m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRoleId());
	}

	@Test
	public void testMLPRole() {
		MLPRole m = new MLPRole(s1, b1);
		m = new MLPRole();
		m.setCreated(d1);
		m.setModified(d2);
		m.setName(s1);
		m.setRoleId(s2);
		checkMLPRole(m);
		m = new MLPRole(m);
		checkMLPRole(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPRole(null, true);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPRoleFunction(MLPRoleFunction m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRoleFunctionId());
		Assert.assertEquals(s3, m.getRoleId());
	}

	@Test
	public void testMLPRoleFunction() {
		MLPRoleFunction m = new MLPRoleFunction(s1, s1);
		m = new MLPRoleFunction();
		m.setCreated(d1);
		m.setModified(d2);
		m.setName(s1);
		m.setRoleFunctionId(s2);
		m.setRoleId(s3);
		checkMLPRoleFunction(m);
		m = new MLPRoleFunction(m);
		checkMLPRoleFunction(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPRoleFunction(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPSiteConfig(MLPSiteConfig m) {
		Assert.assertEquals(s1, m.getConfigKey());
		Assert.assertEquals(s2, m.getConfigValue());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s3, m.getUserId());
	}

	@Test
	public void testMLPSiteConfig() {
		MLPSiteConfig m = new MLPSiteConfig(s1, s1);
		m = new MLPSiteConfig();
		m.setConfigKey(s1);
		m.setConfigValue(s2);
		m.setCreated(d1);
		m.setModified(d2);
		m.setUserId(s3);
		checkMLPSiteConfig(m);
		m = new MLPSiteConfig(m);
		checkMLPSiteConfig(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSiteConfig(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPSolRevArtMap(MLPSolRevArtMap m) {
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getRevisionId());
	}

	@Test
	public void testMLPSolRevArtMap() {
		MLPSolRevArtMap m = new MLPSolRevArtMap(s1, s1);
		m = new MLPSolRevArtMap();
		m.setArtifactId(s1);
		m.setRevisionId(s2);
		checkMLPSolRevArtMap(m);
		m = new MLPSolRevArtMap(m);
		checkMLPSolRevArtMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolRevArtMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPSolRevArtMap.SolRevArtMapPK pk = new MLPSolRevArtMap.SolRevArtMapPK();
		pk = new MLPSolRevArtMap.SolRevArtMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPSolRevDocMap(MLPSolRevDocMap m) {
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(s2, m.getDocumentId());
		Assert.assertEquals(s3, m.getRevisionId());
	}

	@Test
	public void testMLPSolRevDocMap() {
		MLPSolRevDocMap m = new MLPSolRevDocMap(s1, s1, s1);
		m = new MLPSolRevDocMap();
		m.setAccessTypeCode(s1);
		m.setDocumentId(s2);
		m.setRevisionId(s3);
		checkMLPSolRevDocMap(m);
		m = new MLPSolRevDocMap(m);
		checkMLPSolRevDocMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolRevDocMap(null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPSolRevDocMap.SolRevDocMapPK pk = new MLPSolRevDocMap.SolRevDocMapPK();
		pk = new MLPSolRevDocMap.SolRevDocMapPK(s1, s2, s3);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPSolTagMap(MLPSolTagMap m) {
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getTag());
	}

	@Test
	public void testMLPSolTagMap() {
		MLPSolTagMap m = new MLPSolTagMap(s1, s1);
		m = new MLPSolTagMap();
		m.setSolutionId(s1);
		m.setTag(s2);
		checkMLPSolTagMap(m);
		m = new MLPSolTagMap(m);
		checkMLPSolTagMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPSolTagMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPSolTagMap.SolTagMapPK pk = new MLPSolTagMap.SolTagMapPK();
		pk = new MLPSolTagMap.SolTagMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPSolGrpMemMap(MLPSolGrpMemMap m) {
		Assert.assertEquals(l1, m.getGroupId());
		Assert.assertEquals(s1, m.getSolutionId());
	}

	@Test
	public void testMLPSolGrpMemMap() {
		MLPSolGrpMemMap m = new MLPSolGrpMemMap(l1, s1);
		m = new MLPSolGrpMemMap();
		m.setGroupId(l1);
		m.setSolutionId(s1);
		checkMLPSolGrpMemMap(m);
		m = new MLPSolGrpMemMap(m);
		checkMLPSolGrpMemMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPSolGrpMemMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPSolGrpMemMap.SolGrpMemMapPK pk = new MLPSolGrpMemMap.SolGrpMemMapPK();
		pk = new MLPSolGrpMemMap.SolGrpMemMapPK(l1, s1);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPSolUserAccMap(MLPSolUserAccMap m) {
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getUserId());
	}

	@Test
	public void testMLPSolUserAccMap() {
		MLPSolUserAccMap m = new MLPSolUserAccMap(s1, s1);
		m = new MLPSolUserAccMap(m);
		m = new MLPSolUserAccMap();
		m.setSolutionId(s1);
		m.setUserId(s2);
		checkMLPSolUserAccMap(m);
		m = new MLPSolUserAccMap(m);
		checkMLPSolUserAccMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolUserAccMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPSolUserAccMap.SolUserAccessMapPK pk = new MLPSolUserAccMap.SolUserAccessMapPK();
		pk = new MLPSolUserAccMap.SolUserAccessMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPSolution(MLPSolution m) {
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(l1, m.getDownloadCount());
		Assert.assertEquals(b2, m.isFeatured());
		Assert.assertEquals(d2, m.getLastDownload());
		Assert.assertEquals(s2, m.getMetadata());
		Assert.assertEquals(s3, m.getModelTypeCode());
		Assert.assertEquals(d3, m.getModified());
		Assert.assertEquals(s4, m.getName());
		Assert.assertEquals(s5, m.getOrigin());
		Assert.assertEquals(s6, m.getUserId());
		Assert.assertEquals(l2, m.getRatingAverageTenths());
		Assert.assertEquals(l3, m.getRatingCount());
		Assert.assertEquals(s10, m.getSolutionId());
		Assert.assertEquals(s8, m.getSourceId());
		Assert.assertEquals(tags, m.getTags());
		Assert.assertEquals(s9, m.getToolkitTypeCode());
		Assert.assertEquals(l4, m.getViewCount());
	}

	@Test
	public void testMLPSolution() {
		MLPSolution m = new MLPSolution(s1, s1, b1);
		m = new MLPSolution();
		m.setActive(b1);
		m.setCreated(d1);
		m.setDescription(s1);
		m.setDownloadCount(l1);
		m.setFeatured(b2);
		m.setLastDownload(d2);
		m.setMetadata(s2);
		m.setModelTypeCode(s3);
		m.setModified(d3);
		m.setName(s4);
		m.setOrigin(s5);
		m.setUserId(s6);
		m.setRatingAverageTenths(l2);
		m.setRatingCount(l3);
		m.setSolutionId(s7);
		m.setSourceId(s8);
		m.setTags(tags);
		m.setToolkitTypeCode(s9);
		m.setSolutionId(s10);
		m.setViewCount(l4);
		checkMLPSolution(m);
		m = new MLPSolution(m);
		checkMLPSolution(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolution(null, null, true);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPSolutionDeployment(MLPSolutionDeployment m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDeploymentId());
		Assert.assertEquals(s2, m.getDeploymentStatusCode());
		Assert.assertEquals(s3, m.getDetail());
		Assert.assertEquals(s4, m.getRevisionId());
		Assert.assertEquals(s5, m.getSolutionId());
		Assert.assertEquals(s6, m.getTarget());
		Assert.assertEquals(s7, m.getUserId());
	}

	@Test
	public void testMLPSolutionDeployment() {
		MLPSolutionDeployment m = new MLPSolutionDeployment(s1, s1, s1, s1);
		m = new MLPSolutionDeployment();
		m.setCreated(d1);
		m.setDeploymentId(s1);
		m.setDeploymentStatusCode(s2);
		m.setDetail(s3);
		m.setModified(d2);
		m.setRevisionId(s4);
		m.setSolutionId(s5);
		m.setTarget(s6);
		m.setUserId(s7);
		checkMLPSolutionDeployment(m);
		m = new MLPSolutionDeployment(m);
		checkMLPSolutionDeployment(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolutionDeployment(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPSolutionDownload(MLPSolutionDownload m) {
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(l1, m.getDownloadId());
		Assert.assertEquals(s2, m.getSolutionId());
		Assert.assertEquals(s3, m.getUserId());
		Assert.assertEquals(d1, m.getDownloadDate());
	}

	@Test
	public void testMLPSolutionDownload() {
		MLPSolutionDownload m = new MLPSolutionDownload(s1, s2, s3);
		m = new MLPSolutionDownload(m);
		m = new MLPSolutionDownload();
		m.setArtifactId(s1);
		m.setDownloadId(l1);
		m.setSolutionId(s2);
		m.setUserId(s3);
		m.setDownloadDate(d1);
		checkMLPSolutionDownload(m);
		m = new MLPSolutionDownload(m);
		checkMLPSolutionDownload(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolutionDownload(null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPSolutionFavorite(MLPSolutionFavorite m) {
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getUserId());
	}

	@Test
	public void testMLPSolutionFavorite() {
		MLPSolutionFavorite m = new MLPSolutionFavorite(s1, s1);
		m = new MLPSolutionFavorite();
		m.setSolutionId(s1);
		m.setUserId(s2);
		checkMLPSolutionFavorite(m);
		m = new MLPSolutionFavorite(m);
		checkMLPSolutionFavorite(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolutionFavorite(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPSolutionFavorite.SolutionFavoritePK pk = new MLPSolutionFavorite.SolutionFavoritePK();
		pk = new MLPSolutionFavorite.SolutionFavoritePK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPSolutionGroup(MLPSolutionGroup m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(l1, m.getGroupId());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s2, m.getName());
	}

	@Test
	public void testMLPSolutionGroup() {
		MLPSolutionGroup m = new MLPSolutionGroup(s1);
		m = new MLPSolutionGroup();
		m.setCreated(d1);
		m.setDescription(s1);
		m.setGroupId(l1);
		m.setModified(d2);
		m.setName(s2);
		checkMLPSolutionGroup(m);
		m = new MLPSolutionGroup(m);
		checkMLPSolutionGroup(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			String n = null;
			new MLPSolutionGroup(n);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPSolutionRating(MLPSolutionRating m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(i1, m.getRating());
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getTextReview());
		Assert.assertEquals(s3, m.getUserId());
	}

	@Test
	public void testMLPSolutionRating() {
		MLPSolutionRating m = new MLPSolutionRating(s1, s1, i1);
		m = new MLPSolutionRating();
		m.setCreated(d1);
		m.setRating(i1);
		m.setSolutionId(s1);
		m.setTextReview(s2);
		m.setUserId(s3);
		checkMLPSolutionRating(m);
		m = new MLPSolutionRating(m);
		checkMLPSolutionRating(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolutionRating(null, null, 0);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPSolutionRating.SolutionRatingPK pk = new MLPSolutionRating.SolutionRatingPK();
		pk = new MLPSolutionRating.SolutionRatingPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private final AuthorTransport author0 = new AuthorTransport("name1", "contact1");
	private final AuthorTransport author1 = new AuthorTransport("name2", "contact2");

	private void checkMLPSolutionRevision(MLPSolutionRevision m) {
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getDescription());
		Assert.assertEquals(s3, m.getMetadata());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s4, m.getOrigin());
		Assert.assertEquals(s5, m.getPublisher());
		Assert.assertEquals(s6, m.getRevisionId());
		Assert.assertEquals(s7, m.getSolutionId());
		Assert.assertEquals(s8, m.getSourceId());
		Assert.assertEquals(s9, m.getUserId());
		Assert.assertEquals(s11, m.getVerifiedLicense());
		Assert.assertEquals(s12, m.getVerifiedVulnerability());
		Assert.assertEquals(s13, m.getVersion());
		Assert.assertEquals(author1, m.getAuthors()[1]);
	}

	@Test
	public void testMLPSolutionRevision() {
		MLPSolutionRevision m = new MLPSolutionRevision(s1, s1, s1, s1);
		m = new MLPSolutionRevision();
		m.setAccessTypeCode(s1);
		AuthorTransport[] authors = new AuthorTransport[] { author0, author1 };
		m.setAuthors(authors);
		m.setCreated(d1);
		m.setDescription(s2);
		m.setMetadata(s3);
		m.setModified(d2);
		m.setOrigin(s4);
		m.setPublisher(s5);
		m.setRevisionId(s6);
		m.setSolutionId(s7);
		m.setSourceId(s8);
		m.setUserId(s9);
		m.setVerifiedLicense(s11);
		m.setVerifiedVulnerability(s12);
		m.setVersion(s13);
		checkMLPSolutionRevision(m);
		m = new MLPSolutionRevision(m);
		checkMLPSolutionRevision(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSolutionRevision(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		try {
			m.setAuthors(new AuthorTransport[] { new AuthorTransport("\t", "\n") });
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// characters are rejected
		}
	}

	private void checkMLPStepResult(MLPStepResult m) {
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(d1, m.getEndDate());
		Assert.assertEquals(s2, m.getName());
		Assert.assertEquals(s3, m.getResult());
		Assert.assertEquals(s4, m.getRevisionId());
		Assert.assertEquals(s5, m.getSolutionId());
		Assert.assertEquals(d2, m.getStartDate());
		Assert.assertEquals(s6, m.getStatusCode());
		Assert.assertEquals(s7, m.getStepCode());
	}

	@Test
	public void testMLPStepResult() {
		MLPStepResult m = new MLPStepResult(s1, s2, s3, d1);
		m = new MLPStepResult();
		m.setArtifactId(s1);
		m.setEndDate(d1);
		m.setName(s2);
		m.setResult(s3);
		m.setRevisionId(s4);
		m.setSolutionId(s5);
		m.setStartDate(d2);
		m.setStatusCode(s6);
		m.setStepCode(s7);
		checkMLPStepResult(m);
		m = new MLPStepResult(m);
		checkMLPStepResult(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPStepResult(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPTag(MLPTag m) {
		Assert.assertEquals(s1, m.getTag());
	}

	@Test
	public void testMLPTag() {
		MLPTag m = new MLPTag(s1);
		m = new MLPTag();
		m.setTag(s1);
		checkMLPTag(m);
		m = new MLPTag(m);
		checkMLPTag(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			String n = null;
			new MLPTag(n);
			new MLPTag("");
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null or empty arg is rejected
		}
	}

	private void checkMLPThread(MLPThread m) {
		Assert.assertEquals(s1, m.getRevisionId());
		Assert.assertEquals(s2, m.getSolutionId());
		Assert.assertEquals(s3, m.getThreadId());
		Assert.assertEquals(s4, m.getTitle());
	}

	@Test
	public void testMLPThread() {
		MLPThread m = new MLPThread(s1, s1);
		m = new MLPThread(m);
		m = new MLPThread();
		m.setRevisionId(s1);
		m.setSolutionId(s2);
		m.setThreadId(s3);
		m.setTitle(s4);
		checkMLPThread(m);
		m = new MLPThread(m);
		checkMLPThread(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPThread(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPUser(MLPUser m) {
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(s1, m.getApiToken());
		Assert.assertEquals(s2, m.getAuthToken());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s3, m.getEmail());
		Assert.assertEquals(s4, m.getFirstName());
		Assert.assertEquals(d2, m.getLastLogin());
		Assert.assertEquals(s5, m.getLastName());
		Assert.assertEquals(new Short((short) 0), m.getLoginFailCount());
		Assert.assertEquals(d3, m.getLoginFailDate());
		Assert.assertEquals(s6, m.getLoginHash());
		Assert.assertEquals(s7, m.getLoginName());
		Assert.assertEquals(d4, m.getLoginPassExpire());
		Assert.assertEquals(s8, m.getMiddleName());
		Assert.assertEquals(s9, m.getOrgName());
		Assert.assertArrayEquals(by1, m.getPicture());
		Assert.assertEquals(tags, m.getTags());
		Assert.assertEquals(s10, m.getUserId());
		Assert.assertEquals(s11, m.getVerifyTokenHash());
		Assert.assertEquals(d5, m.getVerifyExpiration());
	}

	@Test
	public void testMLPUser() {
		MLPUser m = new MLPUser(s1, s10, b1);
		m = new MLPUser();
		m.setActive(b1);
		m.setApiToken(s1);
		m.setAuthToken(s2);
		m.setCreated(d1);
		m.setEmail(s3);
		m.setFirstName(s4);
		m.setLastLogin(d2);
		m.setLastName(s5);
		m.setLoginFailCount((short) 0);
		m.setLoginFailDate(d3);
		m.setLoginHash(s6);
		m.setLoginName(s7);
		m.setLoginPassExpire(d4);
		m.setMiddleName(s8);
		m.setModified(d4);
		m.setOrgName(s9);
		m.setPicture(by1);
		m.setTags(tags);
		m.setUserId(s10);
		m.setVerifyTokenHash(s11);
		m.setVerifyExpiration(d5);
		checkMLPUser(m);
		m = new MLPUser(m);
		checkMLPUser(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPUser(null, null, true);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null login name is rejected
		}
		try {
			new MLPUser(s1, null, true);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null email is rejected
		}
	}

	private void checkMLPUserLoginProvider(MLPUserLoginProvider m) {
		Assert.assertEquals(s1, m.getAccessToken());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getDisplayName());
		Assert.assertEquals(s3, m.getImageUrl());
		Assert.assertEquals(s4, m.getProfileUrl());
		Assert.assertEquals(s5, m.getProviderCode());
		Assert.assertEquals(s6, m.getProviderUserId());
		Assert.assertEquals(i1, m.getRank());
		Assert.assertEquals(s7, m.getRefreshToken());
		Assert.assertEquals(s8, m.getSecret());
		Assert.assertEquals(s9, m.getUserId());
	}

	@Test
	public void testMLPUserLoginProvider() {
		MLPUserLoginProvider m = new MLPUserLoginProvider(s1, s1, s1, s1, i1);
		m = new MLPUserLoginProvider();
		m.setAccessToken(s1);
		m.setCreated(d1);
		m.setDisplayName(s2);
		m.setImageUrl(s3);
		m.setModified(d2);
		m.setProfileUrl(s4);
		m.setProviderCode(s5);
		m.setProviderUserId(s6);
		m.setRank(i1);
		m.setRefreshToken(s7);
		m.setSecret(s8);
		m.setUserId(s9);
		checkMLPUserLoginProvider(m);
		m = new MLPUserLoginProvider(m);
		checkMLPUserLoginProvider(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPUserLoginProvider(null, null, null, null, 0);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPUserLoginProvider.UserLoginProviderPK pk = new MLPUserLoginProvider.UserLoginProviderPK();
		pk = new MLPUserLoginProvider.UserLoginProviderPK(s1, s2, s3);
		MLPUserLoginProvider.UserLoginProviderPK pk2 = new MLPUserLoginProvider.UserLoginProviderPK(s1, s2, s3);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk2));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPUserNotification(MLPUserNotification m) {
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getEnd());
		Assert.assertEquals(s1, m.getMessage());
		Assert.assertEquals(d3, m.getModified());
		Assert.assertEquals(s2, m.getNotificationId());
		Assert.assertEquals(d4, m.getStart());
		Assert.assertEquals(s3, m.getTitle());
		Assert.assertEquals(s4, m.getUrl());
		Assert.assertEquals(d5, m.getViewed());
	}

	@Test
	public void testMLPUserNotification() {
		MLPUserNotification m = new MLPUserNotification(s1, s1, s1, s1, d1, d1, d1);
		m = new MLPUserNotification();
		m.setCreated(d1);
		m.setEnd(d2);
		m.setMessage(s1);
		m.setModified(d3);
		m.setNotificationId(s2);
		m.setStart(d4);
		m.setTitle(s3);
		m.setUrl(s4);
		m.setViewed(d5);
		checkMLPUserNotification(m);
		m = new MLPUserNotification(m);
		checkMLPUserNotification(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	private void checkMLPUserRoleMap(MLPUserRoleMap m) {
		Assert.assertEquals(s1, m.getRoleId());
		Assert.assertEquals(s2, m.getUserId());
	}

	@Test
	public void testMLPUserRoleMap() {
		MLPUserRoleMap m = new MLPUserRoleMap(s1, s1);
		m = new MLPUserRoleMap();
		m.setRoleId(s1);
		m.setUserId(s2);
		checkMLPUserRoleMap(m);
		m = new MLPUserRoleMap(m);
		checkMLPUserRoleMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPUserRoleMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPUserRoleMap.UserRoleMapPK pk = new MLPUserRoleMap.UserRoleMapPK();
		pk = new MLPUserRoleMap.UserRoleMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPUserTagMap(MLPUserTagMap m) {
		Assert.assertEquals(s1, m.getUserId());
		Assert.assertEquals(s2, m.getTag());
	}

	@Test
	public void testMLPUserTagMap() {
		MLPUserTagMap m = new MLPUserTagMap(s1, s1);
		m = new MLPUserTagMap();
		m.setUserId(s1);
		m.setTag(s2);
		checkMLPUserTagMap(m);
		m = new MLPUserTagMap(m);
		checkMLPUserTagMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPUserTagMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPUserTagMap.UserTagMapPK pk = new MLPUserTagMap.UserTagMapPK();
		pk = new MLPUserTagMap.UserTagMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	// Collect all the deprecated types together below

	@Test
	public void testMLPAccessType() {
		MLPAccessType m = new MLPAccessType();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPArtifactType() {
		MLPArtifactType m = new MLPArtifactType();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPDeploymentStatus() {
		MLPDeploymentStatus m = new MLPDeploymentStatus();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPLoginProvider() {
		MLPLoginProvider m = new MLPLoginProvider();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPModelType() {
		MLPModelType m = new MLPModelType();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPPeerStatus() {
		MLPPeerStatus m = new MLPPeerStatus();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPStepStatus() {
		MLPStepStatus m = new MLPStepStatus();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPStepType() {
		MLPStepType m = new MLPStepType();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPToolkitType() {
		MLPToolkitType m = new MLPToolkitType();
		m.setCode(s1);
		m.setName(s2);
		Assert.assertEquals(s1, m.getCode());
		Assert.assertEquals(s2, m.getName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

}
