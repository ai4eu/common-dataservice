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
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPCompSolMap;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerCatAccMap;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProjNotebookMap;
import org.acumos.cds.domain.MLPProjPipelineMap;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolRevDocMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserCatFavMap;
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
public class DomainTest extends AbstractModelTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private void checkMLPArtifact(MLPArtifact m) {
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getArtifactTypeCode());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(s4, m.getMetadata());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s5, m.getName());
		Assert.assertEquals(s6, m.getUserId());
		Assert.assertEquals(i1, m.getSize());
		Assert.assertEquals(u1, m.getUri());
		Assert.assertEquals(s8, m.getVersion());
	}

	@Test
	public void testMLPArtifact() {
		MLPArtifact m = new MLPArtifact(s1, s1, s1, s1, s1, i1);
		m = new MLPArtifact();
		m.setArtifactId(s1);
		m.setArtifactTypeCode(s2);
		m.setCreated(t1);
		m.setDescription(s3);
		m.setMetadata(s4);
		m.setModified(t2);
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
		} catch (IllegalArgumentException ex) {
			// bogus URI is rejected
		}
	}

	private void checkMLPCatalog(MLPCatalog m) {
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(s2, m.getCatalogId());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s4, m.getName());
		Assert.assertEquals(s5, m.getOrigin());
		Assert.assertEquals(u1, m.getUrl());
	}

	@Test
	public void testMLPCatalog() throws MalformedURLException {
		MLPCatalog m = new MLPCatalog(s1, s1, s1);
		m = new MLPCatalog();
		m.setAccessTypeCode(s1);
		m.setCreated(t1);
		m.setCatalogId(s2);
		m.setDescription(s3);
		m.setModified(t2);
		m.setName(s4);
		m.setOrigin(s5);
		m.setUrl(u1);
		checkMLPCatalog(m);
		m = new MLPCatalog(m);
		checkMLPCatalog(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			String n = null;
			new MLPCatalog(n, n, n);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPCatSolMap(MLPCatSolMap m) {
		Assert.assertEquals(s1, m.getCatalogId());
		Assert.assertEquals(s2, m.getSolutionId());
	}

	@Test
	public void testMLPCatSolMap() {
		MLPCatSolMap m = new MLPCatSolMap(s1, s1);
		m = new MLPCatSolMap();
		m.setCatalogId(s1);
		m.setSolutionId(s2);
		checkMLPCatSolMap(m);
		m = new MLPCatSolMap(m);
		checkMLPCatSolMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		try {
			new MLPCatSolMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		logger.info(m.toString());
		MLPCatSolMap.CatSolMapPK pk = new MLPCatSolMap.CatSolMapPK();
		pk = new MLPCatSolMap.CatSolMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
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
			new MLPCodeNamePair(n, n);
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s1, m.getDocumentId());
		Assert.assertEquals(t2, m.getModified());
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
		m.setCreated(t1);
		m.setDocumentId(s1);
		m.setModified(t2);
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
		} catch (IllegalArgumentException ex) {
			// bogus URI is rejected
		}
	}

	private void checkMLPNotebook(MLPNotebook m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(s2, m.getKernelTypeCode());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s3, m.getName());
		Assert.assertEquals(s4, m.getNotebookId());
		Assert.assertEquals(s5, m.getNotebookTypeCode());
		Assert.assertEquals(u1, m.getRepositoryUrl());
		Assert.assertEquals(s6, m.getServiceStatusCode());
		Assert.assertEquals(u2, m.getServiceUrl());
		Assert.assertEquals(s7, m.getUserId());
		Assert.assertEquals(s8, m.getVersion());
	}

	@Test
	public void testMLPNotebook() {
		MLPNotebook m = new MLPNotebook(s1, s1, s1, s1, s1);
		m = new MLPNotebook();
		m.setCreated(t1);
		m.setDescription(s1);
		m.setKernelTypeCode(s2);
		m.setModified(t2);
		m.setName(s3);
		m.setNotebookId(s4);
		m.setNotebookTypeCode(s5);
		m.setRepositoryUrl(u1);
		m.setServiceStatusCode(s6);
		m.setServiceUrl(u2);
		m.setUserId(s7);
		m.setVersion(s8);
		checkMLPNotebook(m);
		m = new MLPNotebook(m);
		checkMLPNotebook(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPNotebook(null, null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPNotification(MLPNotification m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getEnd());
		Assert.assertEquals(s1, m.getMessage());
		Assert.assertEquals(t3, m.getModified());
		Assert.assertEquals(s2, m.getNotificationId());
		Assert.assertEquals(t4, m.getStart());
		Assert.assertEquals(s3, m.getTitle());
		Assert.assertEquals(s4, m.getUrl());
		Assert.assertEquals(s6, m.getMsgSeverityCode());
	}

	@Test
	public void testMLPNotification() {
		MLPNotification m = new MLPNotification(s1, s6, t1, t1);
		m = new MLPNotification();
		m.setCreated(t1);
		m.setEnd(t2);
		m.setMessage(s1);
		m.setModified(t3);
		m.setNotificationId(s2);
		m.setStart(t4);
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
		Assert.assertEquals(t1, m.getViewed());
	}

	@Test
	public void testMLPNotifUserMap() {
		MLPNotifUserMap m = new MLPNotifUserMap();
		m = new MLPNotifUserMap(s1, s2);
		m.setNotificationId(s1);
		m.setUserId(s2);
		m.setViewed(t1);
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(b1, m.isLocal());
		Assert.assertEquals(t2, m.getModified());
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
		m.setCreated(t1);
		m.setDescription(s3);
		m.setLocal(b1);
		m.setName(s4);
		m.setModified(t2);
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

	private void checkMLPPeerCatAccMap(MLPPeerCatAccMap m) {
		Assert.assertEquals(s1, m.getPeerId());
		Assert.assertEquals(s2, m.getCatalogId());
	}

	@Test
	public void testMLPPeerCatAccMap() {
		MLPPeerCatAccMap m = new MLPPeerCatAccMap(s1, s1);
		m = new MLPPeerCatAccMap(m);
		m = new MLPPeerCatAccMap();
		m.setPeerId(s1);
		m.setCatalogId(s2);
		checkMLPPeerCatAccMap(m);
		m = new MLPPeerCatAccMap(m);
		checkMLPPeerCatAccMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPeerCatAccMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPPeerCatAccMap.PeerCatAccMapPK pk = new MLPPeerCatAccMap.PeerCatAccMapPK();
		pk = new MLPPeerCatAccMap.PeerCatAccMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPPeerSubscription(MLPPeerSubscription m) {
		Assert.assertEquals(s1, m.getAccessType());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(l1, m.getMaxArtifactSize());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s2, m.getOptions());
		Assert.assertEquals(s3, m.getUserId());
		Assert.assertEquals(s4, m.getPeerId());
		Assert.assertEquals(t3, m.getProcessed());
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
		m.setCreated(t1);
		m.setMaxArtifactSize(l1);
		m.setModified(t2);
		m.setOptions(s2);
		m.setUserId(s3);
		m.setPeerId(s4);
		m.setProcessed(t3);
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

	private void checkMLPPipeline(MLPPipeline m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s2, m.getName());
		Assert.assertEquals(s3, m.getPipelineId());
		Assert.assertEquals(u1, m.getRepositoryUrl());
		Assert.assertEquals(s4, m.getServiceStatusCode());
		Assert.assertEquals(u2, m.getServiceUrl());
		Assert.assertEquals(s5, m.getUserId());
		Assert.assertEquals(s6, m.getVersion());
	}

	@Test
	public void testMLPPipeline() {
		MLPPipeline m = new MLPPipeline(s1, s1, s1);
		m = new MLPPipeline();
		m.setCreated(t1);
		m.setDescription(s1);
		m.setModified(t2);
		m.setName(s2);
		m.setPipelineId(s3);
		m.setRepositoryUrl(u1);
		m.setServiceStatusCode(s4);
		m.setServiceUrl(u2);
		m.setUserId(s5);
		m.setVersion(s6);
		checkMLPPipeline(m);
		m = new MLPPipeline(m);
		checkMLPPipeline(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPipeline(null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPProject(MLPProject m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s2, m.getName());
		Assert.assertEquals(s3, m.getProjectId());
		Assert.assertEquals(u1, m.getRepositoryUrl());
		Assert.assertEquals(s4, m.getServiceStatusCode());
		Assert.assertEquals(s5, m.getUserId());
		Assert.assertEquals(s6, m.getVersion());
	}

	@Test
	public void testMLPProject() {
		MLPProject m = new MLPProject(s1, s1, s1);
		m = new MLPProject();
		m.setCreated(t1);
		m.setDescription(s1);
		m.setModified(t2);
		m.setName(s2);
		m.setProjectId(s3);
		m.setRepositoryUrl(u1);
		m.setServiceStatusCode(s4);
		m.setUserId(s5);
		m.setVersion(s6);
		checkMLPProject(m);
		m = new MLPProject(m);
		checkMLPProject(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPProject(null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPProjNbMap(MLPProjNotebookMap m) {
		Assert.assertEquals(s1, m.getProjectId());
		Assert.assertEquals(s2, m.getNotebookId());
	}

	@Test
	public void testMLPProjNbMap() {
		MLPProjNotebookMap m = new MLPProjNotebookMap(s1, s1);
		m = new MLPProjNotebookMap(m);
		m = new MLPProjNotebookMap();
		m.setProjectId(s1);
		m.setNotebookId(s2);
		checkMLPProjNbMap(m);
		m = new MLPProjNotebookMap(m);
		checkMLPProjNbMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPProjNotebookMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPProjNotebookMap.ProjNbMapPK pk = new MLPProjNotebookMap.ProjNbMapPK();
		pk = new MLPProjNotebookMap.ProjNbMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPProjPlMap(MLPProjPipelineMap m) {
		Assert.assertEquals(s1, m.getProjectId());
		Assert.assertEquals(s2, m.getPipelineId());
	}

	@Test
	public void testMLPProjPlMap() {
		MLPProjPipelineMap m = new MLPProjPipelineMap(s1, s1);
		m = new MLPProjPipelineMap(m);
		m = new MLPProjPipelineMap();
		m.setProjectId(s1);
		m.setPipelineId(s2);
		checkMLPProjPlMap(m);
		m = new MLPProjPipelineMap(m);
		checkMLPProjPlMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPProjPipelineMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPProjNotebookMap.ProjNbMapPK pk = new MLPProjNotebookMap.ProjNbMapPK();
		pk = new MLPProjNotebookMap.ProjNbMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPPublishRequest(MLPPublishRequest m) {
		Assert.assertEquals(s1, m.getCatalogId());
		Assert.assertEquals(s2, m.getComment());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertTrue(l1 == m.getRequestId());
		Assert.assertEquals(s3, m.getRequestUserId());
		Assert.assertEquals(s4, m.getReviewUserId());
		Assert.assertEquals(s5, m.getRevisionId());
		Assert.assertEquals(s6, m.getSolutionId());
		Assert.assertEquals(s7, m.getStatusCode());
	}

	@Test
	public void testMLPPublishRequest() {
		MLPPublishRequest m = new MLPPublishRequest(s1, s2, s3, s4, s5);
		m = new MLPPublishRequest();
		m.setCatalogId(s1);
		m.setComment(s2);
		m.setCreated(t1);
		m.setModified(t2);
		m.setRequestId(l1);
		m.setRequestUserId(s3);
		m.setReviewUserId(s4);
		m.setRevisionId(s5);
		m.setSolutionId(s6);
		m.setStatusCode(s7);
		checkMLPPublishRequest(m);
		m = new MLPPublishRequest(m);
		checkMLPPublishRequest(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPPublishRequest(null, null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPRevisionDescription(MLPRevisionDescription m) {
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s2, m.getDescription());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s3, m.getRevisionId());
	}

	@Test
	public void testMLPRevisionDescription() {
		MLPRevisionDescription m = new MLPRevisionDescription(s1, s1, s1);
		m = new MLPRevisionDescription();
		m.setAccessTypeCode(s1);
		m.setCreated(t1);
		m.setDescription(s2);
		m.setModified(t2);
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

	private void checkMLPRightToUse(MLPRightToUse m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(l1, m.getRtuId());
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(b1, m.isSite());
		Assert.assertEquals(1, m.getRtuReferences().size());
	}

	@Test
	public void testMLPRightToUse() {
		MLPRightToUse m = new MLPRightToUse(s1, b1);
		m = new MLPRightToUse();
		m.setCreated(t1);
		m.setModified(t2);
		m.setRtuId(l1);
		m.setSolutionId(s1);
		m.setSite(b1);
		m.getRtuReferences().add(new MLPRtuReference("ref"));
		checkMLPRightToUse(m);
		m = new MLPRightToUse(m);
		checkMLPRightToUse(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPRightToUse(null, true);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
	}

	private void checkMLPRtuReference(MLPRtuReference m) {
		Assert.assertEquals(s1, m.getRef());
	}

	@Test
	public void testMLPRtuReference() {
		MLPRtuReference m = new MLPRtuReference(s1);
		m = new MLPRtuReference();
		m.setRef(s1);
		checkMLPRtuReference(m);
		m = new MLPRtuReference(m);
		checkMLPRtuReference(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			String n = null;
			new MLPRtuReference(n);
			new MLPRtuReference("");
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null or empty arg is rejected
		}
	}

	private void checkMLPRole(MLPRole m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRoleId());
	}

	@Test
	public void testMLPRole() {
		MLPRole m = new MLPRole(s1, b1);
		m = new MLPRole();
		m.setCreated(t1);
		m.setModified(t2);
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRoleFunctionId());
		Assert.assertEquals(s3, m.getRoleId());
	}

	@Test
	public void testMLPRoleFunction() {
		MLPRoleFunction m = new MLPRoleFunction(s1, s1);
		m = new MLPRoleFunction();
		m.setCreated(t1);
		m.setModified(t2);
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s3, m.getUserId());
	}

	@Test
	public void testMLPSiteConfig() {
		MLPSiteConfig m = new MLPSiteConfig(s1, s1);
		m = new MLPSiteConfig();
		m.setConfigKey(s1);
		m.setConfigValue(s2);
		m.setCreated(t1);
		m.setModified(t2);
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

	private void checkMLPSiteContent(MLPSiteContent m) {
		Assert.assertEquals(s1, m.getContentKey());
		Assert.assertEquals(by1, m.getContentValue());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s2, m.getMimeType());
		Assert.assertEquals(t2, m.getModified());
	}

	@Test
	public void testMLPSiteContent() {
		MLPSiteContent m = new MLPSiteContent(s1, by1, s1);
		m = new MLPSiteContent();
		m.setContentKey(s1);
		m.setContentValue(by1);
		m.setCreated(t1);
		m.setMimeType(s2);
		m.setModified(t2);
		checkMLPSiteContent(m);
		m = new MLPSiteContent(m);
		checkMLPSiteContent(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPSiteContent(null, null, null);
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

	@SuppressWarnings("deprecation")
	private void checkMLPSolution(MLPSolution m) {
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(l1, m.getDownloadCount());
		Assert.assertEquals(b2, m.isFeatured());
		Assert.assertEquals(t2, m.getLastDownload());
		Assert.assertEquals(s2, m.getMetadata());
		Assert.assertEquals(s3, m.getModelTypeCode());
		Assert.assertEquals(t3, m.getModified());
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

	@SuppressWarnings("deprecation")
	@Test
	public void testMLPSolution() {
		MLPSolution m = new MLPSolution(s1, s1, b1);
		m = new MLPSolution();
		m.setActive(b1);
		m.setCreated(t1);
		m.setDownloadCount(l1);
		m.setFeatured(b2);
		m.setLastDownload(t2);
		m.setMetadata(s2);
		m.setModelTypeCode(s3);
		m.setModified(t3);
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
		Assert.assertEquals(t1, m.getCreated());
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
		m.setCreated(t1);
		m.setDeploymentId(s1);
		m.setDeploymentStatusCode(s2);
		m.setDetail(s3);
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
		Assert.assertEquals(t1, m.getDownloadDate());
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
		m.setDownloadDate(t1);
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

	private void checkMLPSolutionRating(MLPSolutionRating m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(i1, m.getRating());
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getTextReview());
		Assert.assertEquals(s3, m.getUserId());
	}

	@Test
	public void testMLPSolutionRating() {
		MLPSolutionRating m = new MLPSolutionRating(s1, s1, i1);
		m = new MLPSolutionRating();
		m.setCreated(t1);
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s3, m.getMetadata());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(t3, m.getOnboarded());
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
		m.setCreated(t1);
		m.setMetadata(s3);
		m.setModified(t2);
		m.setOnboarded(t3);
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

	private void checkMLPTaskStepResult(MLPTaskStepResult m) {
		Assert.assertEquals(t1, m.getEndDate());
		Assert.assertEquals(s2, m.getName());
		Assert.assertEquals(s3, m.getResult());
		Assert.assertEquals(t2, m.getStartDate());
		Assert.assertEquals(s6, m.getStatusCode());
	}

	@Test
	public void testMLPTaskStepResult() {
		MLPTaskStepResult m = new MLPTaskStepResult(l1, s1, s2, t1);
		m = new MLPTaskStepResult();
		m.setEndDate(t1);
		m.setName(s2);
		m.setResult(s3);
		m.setStartDate(t2);
		m.setStatusCode(s6);
		checkMLPTaskStepResult(m);
		m = new MLPTaskStepResult(m);
		checkMLPTaskStepResult(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPTaskStepResult(0L, null, null, null);
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

	private void checkMLPTask(MLPTask m) {
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRevisionId());
		Assert.assertEquals(s3, m.getSolutionId());
		Assert.assertEquals(s4, m.getStatusCode());
		Assert.assertEquals(s5, m.getTaskCode());
		Assert.assertEquals(l1, m.getTaskId());
		Assert.assertEquals(s6, m.getTrackingId());
		Assert.assertEquals(s7, m.getUserId());
	}

	@Test
	public void testMLPTask() {
		MLPTask m = new MLPTask(s1, s2, s3, s4);
		m = new MLPTask();
		m.setCreated(t1);
		m.setModified(t2);
		m.setName(s1);
		m.setRevisionId(s2);
		m.setSolutionId(s3);
		m.setStatusCode(s4);
		m.setTaskCode(s5);
		m.setTaskId(l1);
		m.setTrackingId(s6);
		m.setUserId(s7);
		checkMLPTask(m);
		m = new MLPTask(m);
		checkMLPTask(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPTask(null, null, null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(s3, m.getEmail());
		Assert.assertEquals(s4, m.getFirstName());
		Assert.assertEquals(t2, m.getLastLogin());
		Assert.assertEquals(s5, m.getLastName());
		Assert.assertEquals(new Short((short) 0), m.getLoginFailCount());
		Assert.assertEquals(t3, m.getLoginFailDate());
		Assert.assertEquals(s6, m.getLoginHash());
		Assert.assertEquals(s7, m.getLoginName());
		Assert.assertEquals(t4, m.getLoginPassExpire());
		Assert.assertEquals(s8, m.getMiddleName());
		Assert.assertEquals(t5, m.getModified());
		Assert.assertEquals(s9, m.getOrgName());
		Assert.assertArrayEquals(by1, m.getPicture());
		Assert.assertEquals(tags, m.getTags());
		Assert.assertEquals(s10, m.getUserId());
		Assert.assertEquals(s11, m.getVerifyTokenHash());
		Assert.assertEquals(t6, m.getVerifyExpiration());
	}

	@Test
	public void testMLPUser() {
		MLPUser m = new MLPUser(s1, s10, b1);
		m = new MLPUser();
		m.setActive(b1);
		m.setApiToken(s1);
		m.setAuthToken(s2);
		m.setCreated(t1);
		m.setEmail(s3);
		m.setFirstName(s4);
		m.setLastLogin(t2);
		m.setLastName(s5);
		m.setLoginFailCount((short) 0);
		m.setLoginFailDate(t3);
		m.setLoginHash(s6);
		m.setLoginName(s7);
		m.setLoginPassExpire(t4);
		m.setMiddleName(s8);
		m.setModified(t5);
		m.setOrgName(s9);
		m.setPicture(by1);
		m.setTags(tags);
		m.setUserId(s10);
		m.setVerifyTokenHash(s11);
		m.setVerifyExpiration(t6);
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

	private void checkMLPUserCatFavMap(MLPUserCatFavMap m) {
		Assert.assertEquals(s1, m.getUserId());
		Assert.assertEquals(s2, m.getCatalogId());
	}

	@Test
	public void testMLPUserCatFavMap() {
		MLPUserCatFavMap m = new MLPUserCatFavMap(s1, s1);
		m = new MLPUserCatFavMap(m);
		m = new MLPUserCatFavMap();
		m.setUserId(s1);
		m.setCatalogId(s2);
		checkMLPUserCatFavMap(m);
		m = new MLPUserCatFavMap(m);
		checkMLPUserCatFavMap(m);
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		try {
			new MLPUserCatFavMap(null, null);
			Assert.assertTrue("Unexpected success", false);
		} catch (IllegalArgumentException iae) {
			// null arg is rejected
		}
		MLPUserCatFavMap.UserCatFavMapPK pk = new MLPUserCatFavMap.UserCatFavMapPK();
		pk = new MLPUserCatFavMap.UserCatFavMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	private void checkMLPUserLoginProvider(MLPUserLoginProvider m) {
		Assert.assertEquals(s1, m.getAccessToken());
		Assert.assertEquals(t1, m.getCreated());
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
		m.setCreated(t1);
		m.setDisplayName(s2);
		m.setImageUrl(s3);
		m.setModified(t2);
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
		Assert.assertEquals(t1, m.getCreated());
		Assert.assertEquals(t2, m.getEnd());
		Assert.assertEquals(s1, m.getMessage());
		Assert.assertEquals(t3, m.getModified());
		Assert.assertEquals(s2, m.getNotificationId());
		Assert.assertEquals(t4, m.getStart());
		Assert.assertEquals(s3, m.getTitle());
		Assert.assertEquals(s4, m.getUrl());
		Assert.assertEquals(t5, m.getViewed());
	}

	@Test
	public void testMLPUserNotification() {
		MLPUserNotification m = new MLPUserNotification(s1, s1, s1, s1, t1, t1, t1);
		m = new MLPUserNotification();
		m.setCreated(t1);
		m.setEnd(t2);
		m.setMessage(s1);
		m.setModified(t3);
		m.setNotificationId(s2);
		m.setStart(t4);
		m.setTitle(s3);
		m.setUrl(s4);
		m.setViewed(t5);
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

}
