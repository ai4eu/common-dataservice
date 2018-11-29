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
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.DeploymentStatusCode;
import org.acumos.cds.LoginProviderCode;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.ModelTypeCode;
import org.acumos.cds.NotificationDeliveryMechanismCode;
import org.acumos.cds.PeerStatusCode;
import org.acumos.cds.StepStatusCode;
import org.acumos.cds.StepTypeCode;
import org.acumos.cds.SubscriptionScopeCode;
import org.acumos.cds.ToolkitTypeCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifactType;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDeploymentStatus;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPLoginProvider;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
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
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Tests server controller classes by sending in requests with the client. The
 * server is launched with a Derby in-memory database.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SuppressWarnings("deprecation")
public class CdsControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Defined in the default application.properties file
	private final String hostname = "localhost";

	// For tripping length constraints
	private final String s64 = "12345678901234567890123456789012345678901234567890123456789012345";

	// From properties
	@Value("${server.contextPath}")
	private String contextPath;
	@Value("${security.user.name}")
	private String userName;
	@Value("${security.user.password}")
	private String password;
	// Created by Spring black magic
	// https://spring.io/guides/gs/testing-web/
	@LocalServerPort
	private int port;

	private ICommonDataServiceRestClient client;

	@Before
	public void createClient() throws Exception {
		// e.g., "http://localhost:8081/ccds"
		logger.info("createClient: port is {}", port);
		URL url = new URL("http", hostname, port, contextPath);
		logger.info("createClient: URL is {}", url);
		client = CommonDataServiceRestClientImpl.getInstance(url.toString(), userName, password);
	}

	@Test
	public void basicSequenceDemo() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			cu.setLoginName("user_login" + new Random().nextInt());
			cu.setLoginHash("user_pass");
			cu.setEmail("basissqedemuser@abc.com");
			cu.setFirstName("First Name");
			cu.setLastName("Last Name");
			cu.setApiToken("apiToken");
			cu.setVerifyTokenHash("verifyToken");
			cu = client.createUser(cu);
			logger.info("Created user {}", cu);

			cu.setMiddleName("middle");
			client.updateUser(cu);

			// query the user to be sure
			MLPUser cu2 = client.getUser(cu.getUserId());
			Assert.assertTrue(cu.getUserId().equals(cu2.getUserId()));

			MLPSolution cs = new MLPSolution();
			cs.setName("solution name");
			cs.setUserId(cu.getUserId());
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("CP");
			cs.setActive(true);
			cs = client.createSolution(cs);
			logger.info("Created solution {}", cs);

			cs.setOrigin("some origin");
			client.updateSolution(cs);

			MLPSolution fetched = client.getSolution(cs.getSolutionId());
			Assert.assertTrue(fetched != null && fetched.getTags() != null);

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId(),
					AccessTypeCode.PB.name());
			cr.setPublisher("Big Data Org");
			cr = client.createSolutionRevision(cr);
			logger.info("Created solution revision {}", cr);

			// Query for the revision
			MLPSolutionRevision crq = client.getSolutionRevision(cs.getSolutionId(), cr.getRevisionId());
			Assert.assertNotNull(crq);

			MLPRevisionDescription rd = new MLPRevisionDescription(cr.getRevisionId(), AccessTypeCode.PB.name(),
					"A public revision description");
			rd = client.createRevisionDescription(rd);

			// query for the description
			MLPRevisionDescription qrd = client.getRevisionDescription(cr.getRevisionId(), AccessTypeCode.PB.name());
			Assert.assertEquals(rd, qrd);

			final String version = "1.0A";
			MLPArtifact ca = new MLPArtifact(version, ArtifactTypeCode.DI.toString(), "artifact name",
					"http://nexus/artifact", cu.getUserId(), 1);
			ca = client.createArtifact(ca);
			Assert.assertNotNull(ca.getArtifactId());
			Assert.assertNotNull(ca.getCreated());
			logger.info("Created artifact {}", ca);

			logger.info("Adding artifact to revision");
			client.addSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());

			MLPStepResult sr = new MLPStepResult(StepTypeCode.OB.toString(), "New Step Result1",
					StepStatusCode.FA.toString(), new Date());
			sr.setSolutionId(cs.getSolutionId());
			sr = client.createStepResult(sr);
			Assert.assertNotNull(sr.getStepResultId());
			logger.info("Created basicsequencedemo step result {}", sr);

			logger.info("Deleting objects");
			client.dropSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());
			client.deleteArtifact(ca.getArtifactId());
			client.deleteRevisionDescription(cr.getRevisionId(), "PB");
			client.deleteSolutionRevision(cs.getSolutionId(), cr.getRevisionId());
			client.deleteSolution(cs.getSolutionId());
			client.deleteUser(cu.getUserId());
		} catch (HttpStatusCodeException ex) {
			logger.error("basicSequenceDemo failed: " + ex.getResponseBodyAsString(), ex);
			throw ex;
		}
	}

	@Test
	public void getServerInfo() throws Exception {
		SuccessTransport health = client.getHealth();
		Assert.assertNotNull(health.getData());
		logger.info("Server health {}", health.getData());

		SuccessTransport version = client.getVersion();
		Assert.assertNotNull(version.getData());
		logger.info("Server version: {}", version.getData());
	}

	@Test
	public void getCodeValueConstants() throws Exception {

		List<String> valueSetNames = client.getValueSetNames();
		Assert.assertEquals(valueSetNames.size(), CodeNameType.values().length);
		for (String vsName : valueSetNames)
			CodeNameType.valueOf(vsName);

		for (CodeNameType name : CodeNameType.values()) {
			List<MLPCodeNamePair> list = client.getCodeNamePairs(name);
			logger.info("getCodeValueConstants: name {} -> values {}", name, list);
			Assert.assertFalse(list.isEmpty());
			// Cannot validate here - list of values is defined by server config
		}

		List<MLPAccessType> act = client.getAccessTypes();
		Assert.assertFalse(act.isEmpty());

		List<MLPArtifactType> art = client.getArtifactTypes();
		Assert.assertFalse(art.isEmpty());

		List<MLPDeploymentStatus> ds = client.getDeploymentStatuses();
		Assert.assertFalse(ds.isEmpty());

		List<MLPLoginProvider> lp = client.getLoginProviders();
		Assert.assertFalse(lp.isEmpty());

		List<MLPModelType> mt = client.getModelTypes();
		Assert.assertFalse(mt.isEmpty());

		List<MLPToolkitType> tt = client.getToolkitTypes();
		Assert.assertFalse(tt.isEmpty());

		List<MLPStepStatus> ss = client.getStepStatuses();
		Assert.assertFalse(ss.isEmpty());

		List<MLPStepType> st = client.getStepTypes();
		Assert.assertFalse(st.isEmpty());

	}

	@Test
	public void testUserLoginProvider() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			cu.setLoginName("user_login");
			cu.setLoginHash("user_pass");
			cu.setEmail("testusrloginprvderuser@abc.com");
			cu.setFirstName("First Name");
			cu.setLastName("Last Name");
			cu = client.createUser(cu);

			MLPUserLoginProvider prov = new MLPUserLoginProvider();
			prov.setUserId(cu.getUserId());
			final String providerCode = LoginProviderCode.FB.name();
			prov.setProviderCode(providerCode);
			final String userLogin = "foobar";
			prov.setProviderUserId(userLogin);
			prov.setRank(1);
			prov.setAccessToken("access token");
			prov = client.createUserLoginProvider(prov);

			prov.setDisplayName("display");
			client.updateUserLoginProvider(prov);

			MLPUserLoginProvider provq = client.getUserLoginProvider(cu.getUserId(), providerCode, userLogin);
			Assert.assertNotNull(provq);

			client.deleteUserLoginProvider(prov);
			client.deleteUser(cu.getUserId());
		} catch (HttpStatusCodeException ex) {
			logger.error("testUserLoginProvider failed", ex);
			throw ex;
		}
	}

	@Test
	public void createSolutionWithArtifacts() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			// Use this repeatedly :)
			RestPageRequest rp = new RestPageRequest(0, 1);

			Date lastLogin = new Date(new Date().getTime() - 60 * 1000);
			MLPUser cu = new MLPUser();
			String unique = Long.toString(new Date().getTime());
			final String loginName = "user-" + unique;
			final String loginPass = "test_client_pass";
			final String apiToken = "test_client_api";
			final String verifyToken = "test_client_verify";
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setApiToken(apiToken);
			cu.setVerifyTokenHash(verifyToken);
			cu.setEmail("createSolArtuser@abc.com");
			final String firstName = "test_" + unique;
			cu.setFirstName(firstName);
			final String lastName = "name create-sol-arts";
			cu.setLastName(lastName);
			cu.setActive(true);
			cu.setLastLogin(lastLogin);
			cu.setLoginPassExpire(new Date());
			final byte[] fakePicture = new byte[] { 1, 2, 3, 4, 5 };
			cu.setPicture(fakePicture);
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			// assertEquals occasionally fails - this allows for millisecond differences
			Assert.assertTrue(Math.abs(cu.getCreated().getTime() - cu.getModified().getTime()) < 3);
			Assert.assertEquals(cu.getLastLogin(), lastLogin);
			// Password must not come back in response
			Assert.assertNull(cu.getLoginHash());
			logger.info("Created user {}", cu);

			MLPUser inactiveUser = new MLPUser("loginNameBogus", "email@address.org", false);
			inactiveUser.setLoginHash("cleartext");
			inactiveUser = client.createUser(inactiveUser);
			Assert.assertNotNull(inactiveUser.getUserId());

			RestPageResponse<MLPUser> users = client.getUsers(rp);
			Assert.assertTrue(users.getNumberOfElements() > 0);
			for (MLPUser u : users.getContent())
				logger.info("Fetched user: " + u);

			MLPUser loggedIn = client.loginUser(loginName, loginPass);
			Assert.assertNotNull(loggedIn);
			logger.info("Logged in successfully, password expires {}", loggedIn.getLoginPassExpire());
			Assert.assertArrayEquals(fakePicture, loggedIn.getPicture());

			MLPUser apiUser = client.loginApiUser(loginName, apiToken);
			Assert.assertNotNull(apiUser);
			Assert.assertEquals(apiUser.getApiToken(), apiToken);
			logger.info("Logged in successfully via API token");

			MLPUser verifyUser = client.verifyUser(loginName, verifyToken);
			Assert.assertNotNull(verifyUser);
			logger.info("Verified successfully via verification token");

			try {
				client.loginUser(inactiveUser.getLoginName(), "some password");
				throw new Exception("Unexpected login of inactive user");
			} catch (HttpStatusCodeException ex) {
				logger.info("Login of inactive user failed as expected");
			}

			// On login failure a record is made
			try {
				client.loginUser(loginName, "bogus");
				throw new Exception("Unexpected login with bad password");
			} catch (HttpStatusCodeException ex) {
				logger.info("Login with bad password failed as expected");
			}
			// Successful login to clear the failure record
			client.loginUser(loginName, loginPass);
			// Trigger the temporary block with 3 failures
			try {
				client.loginUser(loginName, "bogus");
				throw new Exception("Unexpected login with bad password");
			} catch (HttpStatusCodeException ex) {
				logger.info("Login with bad password failed as expected");
			}
			try {
				client.loginUser(loginName, "bogus");
				throw new Exception("Unexpected login with bad password");
			} catch (HttpStatusCodeException ex) {
				logger.info("Login with bad password failed as expected");
			}
			// Third failure triggers a temporary block
			try {
				client.loginUser(loginName, "bogus");
				throw new Exception("Unexpected login with bad password");
			} catch (HttpStatusCodeException ex) {
				logger.info("Login with bad password failed as expected");
			}
			// This should not work even with valid password
			try {
				client.loginUser(loginName, loginPass);
				throw new Exception("Unexpected login while blocked");
			} catch (HttpStatusCodeException ex) {
				logger.info("Login temporary block worked as expected");
			}

			MLPPasswordChangeRequest req = new MLPPasswordChangeRequest(loginPass, "HardToRemember");
			client.updatePassword(cu, req);
			logger.info("Password changed successfully");

			// Ensure inactive user cannot change password
			try {
				MLPPasswordChangeRequest inactiveChangeReq = new MLPPasswordChangeRequest("old", "new");
				client.updatePassword(inactiveUser, inactiveChangeReq);
				throw new Exception("Unexpected update passwd of inactive user");
			} catch (HttpStatusCodeException ex) {
				logger.info("Update passwd of inactive user failed as expected");
			}

			// First an empty result
			HashMap<String, Object> userRestr = new HashMap<>();
			userRestr.put("firstName", "~never~match~hahaha");
			RestPageResponse<MLPUser> emptyUserPage = client.searchUsers(userRestr, false, new RestPageRequest());
			Assert.assertTrue(emptyUserPage.getNumberOfElements() == 0);

			// Now use query parameters that should match
			userRestr.put("active", "true");
			userRestr.put("firstName", firstName);
			userRestr.put("lastName", lastName);
			RestPageResponse<MLPUser> userPage = client.searchUsers(userRestr, false, new RestPageRequest());
			Assert.assertTrue(userPage.getNumberOfElements() == 1);
			MLPUser testUser = userPage.iterator().next();
			// Password must not come back as JSON
			Assert.assertNull(testUser.getLoginHash());

			// Search for the user
			Map<String, String> fieldMap = new HashMap<>();
			fieldMap.put("firstName", "DESC");
			RestPageResponse<MLPUser> lu = client.findUsersBySearchTerm("name", new RestPageRequest(0, 1, fieldMap));
			Assert.assertTrue(lu != null && lu.getContent() != null && lu.getContent().size() > 0);
			MLPUser searchUser = lu.getContent().get(0);
			Assert.assertNull(searchUser.getLoginHash());

			// Check count
			long userCountTrans = client.getUserCount();
			Assert.assertTrue(userCountTrans > 0);

			MLPUserLoginProvider clp = new MLPUserLoginProvider(cu.getUserId(), LoginProviderCode.GH.name(),
					"something", "access token", 0);
			clp = client.createUserLoginProvider(clp);
			logger.info("Created user login provider {}", clp);

			// Fetch all login providers for user
			List<MLPUserLoginProvider> userProvs = client.getUserLoginProviders(cu.getUserId());
			Assert.assertTrue(userProvs != null && userProvs.size() > 0);

			// Create Peer
			MLPPeer pr = new MLPPeer();
			final String peerName = "Peer-" + Long.toString(new Date().getTime());
			pr.setName(peerName);
			pr.setSubjectName("subject name");
			pr.setApiUrl("http://peer-api");
			pr.setContact1("Katherine Globe");
			pr.setStatusCode(PeerStatusCode.AC.name());
			pr = client.createPeer(pr);
			logger.info("Created peer with ID {}", pr.getPeerId());

			pr.setSelf(false);
			client.updatePeer(pr);

			RestPageResponse<MLPPeer> peerPage = client.getPeers(rp);
			Assert.assertTrue(peerPage.getNumberOfElements() > 0);

			// Cover case of zero results
			HashMap<String, Object> peerRestr = new HashMap<>();
			peerRestr.put("name", "bogusbogus~~nevermatch");
			RestPageResponse<MLPPeer> emptyPerSearchResult = client.searchPeers(peerRestr, false,
					new RestPageRequest(0, 1));
			Assert.assertTrue(emptyPerSearchResult.getNumberOfElements() == 0);

			// Conver case of non-zero results
			peerRestr.put("name", peerName);
			Map<String, String> peerFieldMap = new HashMap<>();
			peerFieldMap.put("name", "ASC");
			RestPageResponse<MLPPeer> peerSearchResult = client.searchPeers(peerRestr, false,
					new RestPageRequest(0, 1, peerFieldMap));
			Assert.assertTrue(peerSearchResult.getNumberOfElements() > 0);

			MLPPeer pr2 = client.getPeer(pr.getPeerId());
			Assert.assertEquals(pr.getPeerId(), pr2.getPeerId());

			Assert.assertEquals(0, client.getPeerSubscriptionCount(pr.getPeerId()));

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(),
					SubscriptionScopeCode.FL.name(), AccessTypeCode.PB.toString());
			ps = client.createPeerSubscription(ps);
			logger.info("Created peer subscription {}", ps);

			ps.setMaxArtifactSize(9999L);
			client.updatePeerSubscription(ps);

			long peerSubCount = client.getPeerSubscriptionCount(pr.getPeerId());
			Assert.assertEquals(1, peerSubCount);

			MLPPeerSubscription ps2 = client.getPeerSubscription(ps.getSubId());
			Assert.assertNotNull(ps2);

			List<MLPPeerSubscription> peerSubs = client.getPeerSubscriptions(pr.getPeerId());
			Assert.assertTrue(peerSubs != null && peerSubs.size() > 0);
			logger.info("Fetched list of peer subscriptions of size {}", peerSubs.size());

			MLPPeerSubscription fetchedPeerSub = client.getPeerSubscription(ps.getSubId());
			Assert.assertTrue(fetchedPeerSub != null);
			logger.info("Fetched peer subscriptions {}", fetchedPeerSub);

			MLPArtifact ca = new MLPArtifact();
			final String version = "1.0A";
			ca.setVersion(version);
			ca.setName("artifact name");
			ca.setUri("http://nexus/artifact");
			ca.setUserId(cu.getUserId());
			ca.setArtifactTypeCode(ArtifactTypeCode.DI.toString());
			ca.setSize(1);
			ca = client.createArtifact(ca);
			logger.info("Created artifact with ID {}", ca.getArtifactId());

			ca.setSize(2);
			client.updateArtifact(ca);

			MLPArtifact art2 = client.getArtifact(ca.getArtifactId());
			Assert.assertEquals(ca.getArtifactId(), art2.getArtifactId());

			// Check count
			long artCountTrans = client.getArtifactCount();
			Assert.assertTrue(artCountTrans > 0);

			final String artId = "e007ce63-086f-4f33-84c6-cac270874d81";
			logger.info("Creating artifact with ID {}", artId);
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setArtifactId(artId);
			ca2.setVersion("2.0A");
			ca2.setName("replicated artifact ");
			ca2.setUri("http://other.foo");
			ca2.setArtifactTypeCode(ArtifactTypeCode.CD.toString());
			ca2.setUserId(cu.getUserId());
			ca2.setSize(456);
			ca2 = client.createArtifact(ca2);
			Assert.assertEquals(artId, ca2.getArtifactId());
			logger.info("Created artifact with preset ID {}", artId);
			client.deleteArtifact(ca2.getArtifactId());

			// Get list
			RestPageResponse<MLPArtifact> arts = client.getArtifacts(new RestPageRequest(0, 100, "artifactId"));
			Assert.assertTrue(arts.getNumberOfElements() > 0);
			// Search like
			RestPageResponse<MLPArtifact> likes = client.findArtifactsBySearchTerm("artifact",
					new RestPageRequest(0, 10, "name"));
			Assert.assertTrue(likes.getNumberOfElements() > 0);
			// Search exactly
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("version", version);
			RestPageResponse<MLPArtifact> filtered = client.searchArtifacts(restr, true, new RestPageRequest(0, 10));
			Assert.assertTrue(filtered.getNumberOfElements() > 0);

			// This will get no results but will cover some clauses
			restr.clear();
			restr.put("uri", "http://nowhwere");
			filtered = client.searchArtifacts(restr, true, new RestPageRequest(0, 10));
			Assert.assertTrue(filtered.getNumberOfElements() == 0);

			// Also check that Spring doesn't truncate last path variable
			final String tagName1 = Long.toString(new Date().getTime()) + ".tag.1";
			final String tagName2 = Long.toString(new Date().getTime()) + ".tag.2";
			MLPTag tag1 = new MLPTag(tagName1);
			MLPTag tag2 = new MLPTag(tagName2);
			tag1 = client.createTag(tag1);
			logger.info("Created tag {}", tag1);
			tag2 = client.createTag(tag2);
			// Get list
			RestPageResponse<MLPTag> tags = client.getTags(new RestPageRequest(0, 100));
			Assert.assertTrue(tags.getNumberOfElements() > 0);

			// Tag some users for fun
			client.addUserTag(cu.getUserId(), tagName1);
			try {
				client.addUserTag(cu.getUserId(), tagName1);
				throw new Exception("Unexpected success add user tag");
			} catch (HttpStatusCodeException ex) {
				logger.info("Failed to tag user a second time as expected");
			}
			MLPUser taggedUser = client.getUser(cu.getUserId());
			Assert.assertTrue(taggedUser.getTags().contains(tag1));
			client.dropUserTag(cu.getUserId(), tagName1);

			MLPSolution cs = new MLPSolution("solution name", cu.getUserId(), true);
			cs.setDescription("Tagged solution");
			cs.setModelTypeCode(ModelTypeCode.CL.name());
			cs.setToolkitTypeCode(ToolkitTypeCode.CP.name());
			// This tag should spring into existence here
			MLPTag newTag = new MLPTag("new-solution-tag.3");
			cs.getTags().add(newTag);
			cs = client.createSolution(cs);
			Assert.assertNotNull(cs.getSolutionId());
			Assert.assertFalse(cs.getTags().isEmpty());
			Assert.assertTrue(cs.getTags().contains(newTag));
			logger.info("Created public solution {}", cs);

			byte[] saved = client.getSolutionPicture(cs.getSolutionId());
			Assert.assertNull(saved);
			byte[] image = new byte[] { 0, 1, 2, 3, 4, 5 };
			client.saveSolutionPicture(cs.getSolutionId(), image);
			saved = client.getSolutionPicture(cs.getSolutionId());
			Assert.assertArrayEquals(saved, image);
			client.saveSolutionPicture(cs.getSolutionId(), null);
			saved = client.getSolutionPicture(cs.getSolutionId());
			Assert.assertNull(saved);

			// clean out the instant tag and ensure it was removed
			client.dropSolutionTag(cs.getSolutionId(), newTag.getTag());
			client.deleteTag(newTag);
			cs = client.getSolution(cs.getSolutionId());
			Assert.assertFalse(cs.getTags().contains(newTag));

			// no tags
			MLPSolution csOrg = new MLPSolution("solution organization", cu.getUserId(), true);
			csOrg.setDescription("Untagged solution org");
			csOrg.setModelTypeCode(ModelTypeCode.DS.name());
			csOrg.setToolkitTypeCode(ToolkitTypeCode.SK.name());
			csOrg = client.createSolution(csOrg);
			Assert.assertNotNull(csOrg.getSolutionId());
			logger.info("Created org solution {}", cs);

			MLPSolution inactive = new MLPSolution();
			inactive.setName("inactive solution name");
			inactive.setUserId(cu.getUserId());
			inactive.setDescription("Inactive Data Org");
			inactive.setModelTypeCode(ModelTypeCode.DS.name());
			inactive.setToolkitTypeCode(ToolkitTypeCode.SK.name());
			inactive.setActive(false);
			inactive = client.createSolution(inactive);
			Assert.assertNotNull(inactive.getSolutionId());
			logger.info("Created inactive solution {}", inactive);

			// Check count
			long solCountTrans = client.getSolutionCount();
			Assert.assertTrue(solCountTrans > 0);

			// Increment view count
			Long before = cs.getViewCount();
			logger.info("Incrementing solution view count");
			client.incrementSolutionViewCount(cs.getSolutionId());
			MLPSolution after = client.getSolution(cs.getSolutionId());
			Assert.assertNotEquals(before, after.getViewCount());

			// add and drop tags
			logger.info("Tagging solutions");
			client.addSolutionTag(cs.getSolutionId(), tagName1);
			client.addSolutionTag(cs.getSolutionId(), tagName2);
			client.dropSolutionTag(cs.getSolutionId(), tagName2);
			// New feature: create tag upon adding
			String instantTag = "instant-tag-just-add-water";
			client.addSolutionTag(cs.getSolutionId(), instantTag);

			logger.info("Fetching back newly tagged solution");
			MLPSolution s = client.getSolution(cs.getSolutionId());
			Assert.assertTrue(s != null && !s.getTags().isEmpty());
			logger.info("Solution {}", s);

			// Query for tags
			List<MLPTag> solTags = client.getSolutionTags(cs.getSolutionId());
			Assert.assertTrue(solTags.size() > 1);
			logger.info("Found tags on solution {}", solTags);

			// Clean up the instant mess
			client.dropSolutionTag(cs.getSolutionId(), instantTag);
			client.deleteTag(new MLPTag(instantTag));

			logger.info("Fetching back less tagged solution");
			cs = client.getSolution(cs.getSolutionId());
			Assert.assertTrue(cs != null && !cs.getTags().isEmpty());
			logger.info("Solution tags: {}", cs.getTags());

			logger.info("Getting all solutions");
			RestPageResponse<MLPSolution> page = client.getSolutions(new RestPageRequest(0, 2, "name"));
			Assert.assertTrue(page != null && page.getTotalElements() > 0);

			cs.setDescription("some description");
			client.updateSolution(cs);
			logger.info("Fetching back updated solution");
			MLPSolution updated = client.getSolution(cs.getSolutionId());
			Assert.assertTrue(updated != null && !updated.getTags().isEmpty() && updated.getViewCount() > 0);

			logger.info("Querying for solutions with similar names");
			RestPageResponse<MLPSolution> sl1 = client.findSolutionsBySearchTerm("solution", new RestPageRequest(0, 1));
			Assert.assertTrue(sl1 != null && sl1.getNumberOfElements() > 0);

			logger.info("Querying for solutions by tag");
			RestPageResponse<MLPSolution> sl2 = client.findSolutionsByTag(tagName1, new RestPageRequest(0, 5));
			Assert.assertTrue(sl2 != null && sl2.getNumberOfElements() > 0);

			// Add user access
			client.addSolutionUserAccess(cs.getSolutionId(), inactiveUser.getUserId());

			// Query two ways
			List<MLPUser> solUserAccList = client.getSolutionAccessUsers(cs.getSolutionId());
			Assert.assertTrue(solUserAccList != null && solUserAccList.size() > 0);
			logger.info("Got users with access to solution {}", cs.getSolutionId());
			RestPageResponse<MLPSolution> userSolAccList = client.getUserAccessSolutions(inactiveUser.getUserId(),
					new RestPageRequest(0, 1));
			Assert.assertTrue(userSolAccList != null && userSolAccList.getNumberOfElements() > 0);
			logger.info("Got solutions accessible by user {}", cu.getUserId());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId(), //
					AccessTypeCode.PB.name());
			cr.setAuthors(new AuthorTransport[] { new AuthorTransport("my name", "http://github") });
			cr.setPublisher("publisher 1");
			cr = client.createSolutionRevision(cr);
			Assert.assertNotNull(cr.getRevisionId());
			logger.info("Created solution revision {}", cr.getRevisionId());
			cr.setDescription("Some description");
			client.updateSolutionRevision(cr);

			MLPSolutionRevision crOrg = new MLPSolutionRevision(csOrg.getSolutionId(), "1.0R", cu.getUserId(), //
					AccessTypeCode.OR.name());
			crOrg.setAuthors(new AuthorTransport[] { new AuthorTransport("your name", "email") });
			crOrg.setPublisher("publisher 2");
			crOrg = client.createSolutionRevision(crOrg);
			Assert.assertNotNull(crOrg.getRevisionId());
			logger.info("Created solution revision {}", cr.getRevisionId());

			logger.info("Adding artifact to revision 1");
			client.addSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());

			logger.info("Adding artifact to revision 2");
			client.addSolutionRevisionArtifact(csOrg.getSolutionId(), crOrg.getRevisionId(), ca.getArtifactId());

			logger.info("Querying revision artifacts");
			List<MLPArtifact> revArgs = client.getSolutionRevisionArtifacts(cs.getSolutionId(), cr.getRevisionId());
			Assert.assertFalse(revArgs.isEmpty());

			logger.info("Creating description org for revision 1");
			MLPRevisionDescription revDescOr = new MLPRevisionDescription(cr.getRevisionId(), "OR", "Some text");
			revDescOr = client.createRevisionDescription(revDescOr);
			Assert.assertNotNull(revDescOr.getCreated());
			final String descFoo = "foo";
			revDescOr.setDescription(descFoo);
			client.updateRevisionDescription(revDescOr);
			revDescOr = client.getRevisionDescription(cr.getRevisionId(), "OR");
			Assert.assertNotNull(revDescOr);
			Assert.assertEquals(descFoo, revDescOr.getDescription());
			logger.info("Creating description PB for revision 1");
			MLPRevisionDescription revDescPb = new MLPRevisionDescription(cr.getRevisionId(), "PB", "Some text");
			revDescPb = client.createRevisionDescription(revDescPb);
			Assert.assertNotNull(revDescPb.getCreated());

			logger.info("Creating user document");
			MLPDocument doc = new MLPDocument();
			doc.setName("some name");
			doc.setUri("http://other.user.doc.uri");
			doc.setSize(100);
			doc.setUserId(cu.getUserId());
			doc = client.createDocument(doc);
			Assert.assertNotNull(doc.getDocumentId());
			client.updateDocument(doc);
			doc = client.getDocument(doc.getDocumentId());
			Assert.assertNotNull(doc);

			try {
				MLPDocument doc2 = new MLPDocument("name", "uri", 100, "user");
				doc2.setDocumentId(doc.getDocumentId());
				client.createDocument(doc2);
				throw new Exception("Unexpected success");
			} catch (HttpStatusCodeException ex) {
				logger.info("Failed to create new doc with existing ID as expected");
			}

			final String orgAccessType = "OR";
			logger.info("Associating document to rev 1 at access type OR");
			client.addSolutionRevisionDocument(cr.getRevisionId(), orgAccessType, doc.getDocumentId());
			List<MLPDocument> dl = client.getSolutionRevisionDocuments(cr.getRevisionId(), orgAccessType);
			Assert.assertFalse(dl.isEmpty());

			logger.info("Querying for revisions by solution");
			List<MLPSolutionRevision> revs = client.getSolutionRevisions(new String[] { s.getSolutionId() });
			Assert.assertTrue(revs != null && revs.size() > 0);
			for (MLPSolutionRevision r : revs) {
				logger.info("Solution {} has revision: {}", cs.getSolutionId(), r);
				List<MLPArtifact> al = client.getSolutionRevisionArtifacts(cs.getSolutionId(), cr.getRevisionId());
				for (MLPArtifact a : al)
					logger.info("Solution {}, revision {} has artifact: {}", cs.getSolutionId(), r.getRevisionId(), a);
			}

			logger.info("Querying for active solutions");
			Map<String, Object> activePb = new HashMap<>();
			activePb.put("active", Boolean.TRUE);
			RestPageResponse<MLPSolution> activePbPage = client.searchSolutions(activePb, false,
					new RestPageRequest(0, 10, "name"));
			Assert.assertTrue(activePbPage != null && !activePbPage.getContent().isEmpty());
			logger.info("Active PB solution page count {}", activePbPage.getContent().size());

			logger.info("Querying for inactive solutions");
			Map<String, Object> inactiveSols = new HashMap<>();
			inactiveSols.put("active", Boolean.TRUE);
			RestPageResponse<MLPSolution> inactiveSolList = client.searchSolutions(inactiveSols, false,
					new RestPageRequest());
			Assert.assertTrue(inactiveSolList != null && inactiveSolList.getNumberOfElements() > 0);
			logger.info("Inactive PB solution count {}", inactiveSolList.getNumberOfElements());

			// Portal dynamic search
			logger.info("Querying for any solutions via flexible i/f");
			RestPageResponse<MLPSolution> portalAnyMatches = client.findPortalSolutions(null, null, true, null, 
					null, null, null, null, null, new RestPageRequest(0, 5));
			Assert.assertTrue(portalAnyMatches != null && portalAnyMatches.getNumberOfElements() > 1);

			logger.info("Querying for valid tag on solutions via flexible i/f");
			String[] searchTags = new String[] { tagName1 };
			RestPageResponse<MLPSolution> portalTagMatches = client.findPortalSolutions(null, null, true, null, 
					null, null, searchTags, null, null, new RestPageRequest(0, 5));
			Assert.assertTrue(portalTagMatches != null && portalTagMatches.getNumberOfElements() > 0);

			logger.info("Querying for bogus tag on solutions via flexible i/f");
			String[] bogusTags = new String[] { "bogus" };
			RestPageResponse<MLPSolution> portalTagNoMatches = client.findPortalSolutions(null, null, true, null,
					null, null, bogusTags, null, null, new RestPageRequest(0, 5));
			Assert.assertTrue(portalTagNoMatches != null && portalTagNoMatches.getNumberOfElements() == 0);

			// Check fetch by ID to ensure both are found
			logger.info("Querying for solutions by id");
			String[] ids = { cs.getSolutionId(), csOrg.getSolutionId() };
			RestPageResponse<MLPSolution> idSearchResult = client.findPortalSolutionsByKw(ids, true, null, null, null,
					null, new RestPageRequest(0, 2));
			Assert.assertTrue(idSearchResult != null && idSearchResult.getNumberOfElements() == 2);
			logger.info("Found models by id total " + idSearchResult.getTotalElements());

			// Both keywords must occur in the same field for a match
			logger.info("Querying for solutions by keyword");
			String[] kw = { "solution", "organization" };
			RestPageResponse<MLPSolution> kwSearchResult = client.findPortalSolutionsByKw(kw, true, null, null, null,
					null, new RestPageRequest(0, 2));
			Assert.assertTrue(kwSearchResult != null && kwSearchResult.getNumberOfElements() > 0);
			logger.info("Found models by kw total " + kwSearchResult.getTotalElements());

			logger.info("Querying for solutions by tags");
			String[] allTags = new String[] { tagName1 };
			String[] anyTags = null; // new String[] { tagName2 };
			RestPageResponse<MLPSolution> allAnyTagsSearchResult = client.findPortalSolutionsByKwAndTags(null, true,
					null, null, null, allTags, anyTags, new RestPageRequest(0, 2));
			logger.info("Found models by tag total " + allAnyTagsSearchResult.getTotalElements());
			Assert.assertTrue(allAnyTagsSearchResult != null && allAnyTagsSearchResult.getNumberOfElements() > 0);
			MLPSolution taggedSol = allAnyTagsSearchResult.getContent().get(0);
			Assert.assertTrue(taggedSol.getTags().contains(new MLPTag(tagName1)));

			logger.info("Querying for user solutions via flexible i/f");
			RestPageResponse<MLPSolution> userSols = client.findUserSolutions(null, null, true,
					inactiveUser.getUserId(), null, null, null, new RestPageRequest(0, 5));
			Assert.assertTrue(userSols != null && userSols.getNumberOfElements() > 0);

			// find active solutions
			String[] nameKw = null;
			String[] descKw = null;
			String[] owners = { cu.getUserId() };
			String[] accessTypeCodes = { AccessTypeCode.OR.name(), AccessTypeCode.PB.toString() };
			String[] modelTypeCodes = null;
			String[] authKw = { "github" };
			String[] pubKw = { "publisher" };
			searchTags = null;
			RestPageResponse<MLPSolution> portalActiveMatches = client.findPortalSolutions(nameKw, descKw, true, owners,
					accessTypeCodes, modelTypeCodes, searchTags, authKw, pubKw,
					new RestPageRequest(0, 5));
			Assert.assertTrue(portalActiveMatches != null && portalActiveMatches.getNumberOfElements() > 0);

			// Requires revisions and artifacts!
			String[] searchAccessTypeCodes = new String[] { AccessTypeCode.OR.name() };
			Date anHourAgo = new java.util.Date();
			anHourAgo.setTime(new Date().getTime() - 1000L * 60 * 60);
			RestPageResponse<MLPSolution> sld = client.findSolutionsByDate(true, searchAccessTypeCodes,
					anHourAgo, new RestPageRequest(0, 1));
			Assert.assertTrue(sld != null && sld.getNumberOfElements() > 0);
			logger.info("Found solutions by date: " + sld.getContent().size());

			// Create Solution Rating
			logger.info("Creating solution rating");
			MLPSolutionRating ur = new MLPSolutionRating();
			ur.setSolutionId(cs.getSolutionId());
			ur.setUserId(cu.getUserId());
			ur.setRating(4);
			ur.setTextReview("Awesome");
			ur.setCreated(new Date());
			ur = client.createSolutionRating(ur);
			logger.info("Created solution rating {}", ur);
			MLPSolutionRating rating = client.getSolutionRating(cs.getSolutionId(), cu.getUserId());
			Assert.assertNotNull(rating);
			logger.info("Fetched solution rating {}", rating);
			ur.setTextReview("Yet awesomer");
			ur.setRating(5);
			client.updateSolutionRating(ur);
			logger.info("Updated solution rating {}", ur);
			RestPageResponse<MLPSolutionRating> ratings = client.getSolutionRatings(cs.getSolutionId(),
					new RestPageRequest(0, 1));
			Assert.assertTrue(ratings != null && ratings.getNumberOfElements() > 0);
			logger.info("Solution rating count {}", ratings.getNumberOfElements());

			// check the average rating
			MLPSolution avgRating = client.getSolution(cs.getSolutionId());
			Assert.assertTrue(avgRating.getRatingAverageTenths() > 0);
			logger.info("Computed solution rating average: {}", avgRating.getRatingAverageTenths());

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = client.createSolutionDownload(sd);
			logger.info("Created solution download {}", sd);

			// Query for downloads
			RestPageResponse<MLPSolutionDownload> dnls = client.getSolutionDownloads(cs.getSolutionId(), rp);
			Assert.assertTrue(dnls.getNumberOfElements() > 0);

			// Count the downloads
			MLPSolution downloadStats = client.getSolution(cs.getSolutionId());
			Assert.assertNotNull(downloadStats);
			Assert.assertTrue(downloadStats.getDownloadCount() > 0);
			logger.info("Solution download count is {}", downloadStats.getDownloadCount());

			// Create Solution favorite for a user
			MLPSolutionFavorite sf1 = new MLPSolutionFavorite();
			sf1.setSolutionId(cs.getSolutionId());
			sf1.setUserId(cu.getUserId());
			sf1 = client.createSolutionFavorite(sf1);
			logger.info("Created a favorite solution {}", sf1);

			// Get favorite solutions
			RestPageResponse<MLPSolution> favePage = client.getFavoriteSolutions(cu.getUserId(), rp);
			Assert.assertNotNull(favePage);
			Assert.assertTrue(favePage.getNumberOfElements() > 0);
			for (MLPSolution mlpsol : favePage)
				logger.info("Favorite Solution for user {} is {}, name {}", cu.getUserId(), mlpsol.getSolutionId(),
						mlpsol.getName());
			// Create and update solution deployment
			MLPSolutionDeployment dep = new MLPSolutionDeployment();
			dep.setDeploymentId(UUID.randomUUID().toString());
			dep.setSolutionId(cs.getSolutionId());
			dep.setRevisionId(cr.getRevisionId());
			dep.setUserId(cu.getUserId());
			dep.setDeploymentStatusCode(DeploymentStatusCode.ST.name());
			dep = client.createSolutionDeployment(dep);
			Assert.assertNotNull(dep.getDeploymentId());
			logger.info("Created solution deployent {}", dep);

			// Must be valid JSON
			dep.setDetail("{ \"tag\" : \"value\" }");
			client.updateSolutionDeployment(dep);

			// Query for solution deployments
			RestPageResponse<MLPSolutionDeployment> userDeps = client.getUserDeployments(cu.getUserId(), rp);
			Assert.assertTrue(userDeps != null & userDeps.getNumberOfElements() > 0);
			RestPageResponse<MLPSolutionDeployment> deps = client.getSolutionDeployments(cs.getSolutionId(),
					cr.getRevisionId(), rp);
			Assert.assertTrue(deps != null && deps.getNumberOfElements() > 0);
			RestPageResponse<MLPSolutionDeployment> userSolDeps = client.getUserSolutionDeployments(cs.getSolutionId(),
					cr.getRevisionId(), cu.getUserId(), rp);
			Assert.assertTrue(userSolDeps != null && userSolDeps.getNumberOfElements() > 0);

			// delete the deployment
			client.deleteSolutionDeployment(dep);

			logger.info("Querying for revisions by artifact");
			List<MLPSolutionRevision> revsByArt = client.getSolutionRevisionsForArtifact(ca.getArtifactId());
			Assert.assertTrue(revsByArt != null && revsByArt.size() > 0);
			for (MLPSolutionRevision r : revs)
				logger.info("\tRevision: {}", r);

			// Composite solution support was added very late
			client.addCompositeSolutionMember(cs.getSolutionId(), csOrg.getSolutionId());
			List<String> kids = client.getCompositeSolutionMembers(cs.getSolutionId());
			Assert.assertTrue(kids != null && kids.size() == 1);
			client.dropCompositeSolutionMember(cs.getSolutionId(), csOrg.getSolutionId());
			kids = client.getCompositeSolutionMembers(cs.getSolutionId());
			Assert.assertTrue(kids != null && kids.size() == 0);

			MLPPublishRequest pubReq = new MLPPublishRequest(cs.getSolutionId(), cr.getRevisionId(), cu.getUserId(),
					"PE");
			pubReq = client.createPublishRequest(pubReq);
			Assert.assertNotNull(pubReq.getRequestId());
			pubReq.setComment("foo bar");
			client.updatePublishRequest(pubReq);
			MLPPublishRequest reqFound = client.getPublishRequest(pubReq.getRequestId());
			Assert.assertNotNull(reqFound);
			Assert.assertNotNull(reqFound.getComment());
			Assert.assertTrue(client.isPublishRequestPending(cs.getSolutionId(), cr.getRevisionId()));
			logger.info("First publish request {}", reqFound);
			HashMap<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("solutionId", cs.getSolutionId());
			RestPageResponse<MLPPublishRequest> pubReqPage = client.searchPublishRequests(queryParameters, false,
					new RestPageRequest(0, 5));
			Assert.assertTrue(pubReqPage.getNumberOfElements() > 0);
			client.deletePublishRequest(pubReq.getRequestId());
			try {
				client.getPublishRequest(pubReq.getRequestId());
				throw new Exception("Unexpected find of deleted item");
			} catch (HttpStatusCodeException ex) {
				logger.info("Get of deleted pub req failed as expected");
			}

			if (cleanup) {
				logger.info("Deleting newly created instances");
				client.dropSolutionRevisionDocument(cr.getRevisionId(), "OR", doc.getDocumentId());
				client.deleteDocument(doc.getDocumentId());
				client.deleteRevisionDescription(cr.getRevisionId(), "OR");
				client.deleteRevisionDescription(cr.getRevisionId(), "PB");
				client.dropSolutionTag(cs.getSolutionId(), tagName1);
				client.deleteTag(tag1);
				client.deleteTag(tag2);
				client.dropSolutionUserAccess(cs.getSolutionId(), inactiveUser.getUserId());
				// Server SHOULD cascade deletes.
				client.deleteSolutionRating(ur);
				client.deleteSolutionDownload(sd);
				client.deleteSolutionFavorite(sf1);
				// delete-solution should cascade to sol-rev-art
				client.deleteSolution(cs.getSolutionId());
				client.deleteSolution(csOrg.getSolutionId());
				client.deleteSolution(inactive.getSolutionId());
				client.deleteArtifact(ca.getArtifactId());
				client.deletePeerSubscription(ps.getSubId());
				client.deletePeer(pr.getPeerId());
				client.deleteUserLoginProvider(clp);
				client.deleteUser(cu.getUserId());
				client.deleteUser(inactiveUser.getUserId());

				try {
					MLPSolution deleted = client.getSolution(cs.getSolutionId());
					throw new Exception("Found a deleted solution: " + deleted);
				} catch (HttpClientErrorException ex) {
					// this is expected, the item should not exist
					logger.info("Caught expected exception: " + ex.getResponseBodyAsString());
				}

			} // cleanup

		} catch (HttpStatusCodeException ex) {
			logger.error("createSolutionWithArtifacts failed: " + ex.getResponseBodyAsString(), ex);
			throw ex;
		}

	}

	@Test
	public void testRoleAndFunctions() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			String unique = Long.toString(new Date().getTime());
			final String loginName = "user-" + unique;
			final String loginPass = "test_client_pass";
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setEmail("testrolefnuser@abc.com");
			final String firstName = "test_" + unique;
			cu.setFirstName(firstName);
			final String lastName = "test-role-fn";
			cu.setLastName(lastName);
			cu.setActive(true);
			cu.setLoginPassExpire(new Date());
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());
			logger.info("Created user with ID {}", cu.getUserId());

			MLPRole cr = new MLPRole();
			cr.setName("something or the other");
			cr = client.createRole(cr);
			Assert.assertNotNull(cr.getRoleId());
			logger.info("Created role: {}", cr);

			final String roleName = "My test role";
			cr.setName(roleName);
			client.updateRole(cr);

			MLPRole cr2 = new MLPRole();
			cr2.setName("Second role");
			cr2 = client.createRole(cr2);

			long roleCount = client.getRoleCount();
			Assert.assertTrue(roleCount > 0);

			RestPageResponse<MLPRole> roles = client.getRoles(new RestPageRequest());
			Assert.assertTrue(roles.getNumberOfElements() > 0);

			// Empty result
			HashMap<String, Object> roleRestr = new HashMap<>();
			roleRestr.put("name", "never~ever~match~role");
			RestPageResponse<MLPRole> emptyRoleResult = client.searchRoles(roleRestr, false, new RestPageRequest());
			Assert.assertTrue(emptyRoleResult.getNumberOfElements() == 0);

			// Nonempty result
			roleRestr.put("name", roleName);
			RestPageResponse<MLPRole> roleResult = client.searchRoles(roleRestr, false, new RestPageRequest());
			Assert.assertTrue(roleResult.getNumberOfElements() > 0);

			MLPRoleFunction crf = new MLPRoleFunction();
			final String roleFuncName = "My test role function";
			crf.setName(roleFuncName);
			crf.setRoleId(cr.getRoleId());
			crf = client.createRoleFunction(crf);
			logger.info("Created role function {}", crf);
			Assert.assertNotNull(crf.getRoleFunctionId());

			crf.setName("My test role function updated");
			client.updateRoleFunction(crf);

			MLPRole res = client.getRole(cr.getRoleId());
			Assert.assertNotNull(res.getRoleId());
			logger.info("Retrieved role {}", res);

			List<MLPRoleFunction> fetchedRoleFns = client.getRoleFunctions(cr.getRoleId());
			Assert.assertTrue(fetchedRoleFns != null && fetchedRoleFns.size() > 0);
			MLPRoleFunction roleFnFromList = fetchedRoleFns.get(0);
			logger.info("First role function in list {}", roleFnFromList);

			MLPRoleFunction roleFn = client.getRoleFunction(cr.getRoleId(), roleFnFromList.getRoleFunctionId());
			logger.info("Single role function {}", roleFn);
			Assert.assertNotNull(roleFn);

			logger.info("Adding role 1 for user");
			client.addUserRole(cu.getUserId(), cr.getRoleId());
			List<MLPRole> addedRoles = client.getUserRoles(cu.getUserId());
			Assert.assertTrue(addedRoles != null && addedRoles.size() == 1);

			logger.info("Adding role 2 for user");
			List<String> userIds = new ArrayList<>();
			userIds.add(cu.getUserId());
			client.addUsersInRole(userIds, cr2.getRoleId());

			long role2count = client.getRoleUsersCount(cr2.getRoleId());
			Assert.assertTrue(role2count == 1);
			logger.info("Count of users in role 2: {}", role2count);

			addedRoles = client.getUserRoles(cu.getUserId());
			Assert.assertTrue(addedRoles != null && addedRoles.size() == 2);
			logger.info("Count of roles for user: {}", addedRoles.size());

			logger.info("Dropping role 1 for user");
			client.dropUserRole(cu.getUserId(), cr.getRoleId());

			logger.info("Dropping role 2 for user");
			client.dropUsersInRole(userIds, cr2.getRoleId());

			List<MLPRole> revisedUserRoles = client.getUserRoles(cu.getUserId());
			Assert.assertTrue(revisedUserRoles != null && revisedUserRoles.isEmpty());
			logger.info("User role count is zero");

			List<String> roleIds = new ArrayList<>();
			roleIds.add(cr.getRoleId());
			roleIds.add(cr2.getRoleId());
			client.updateUserRoles(cu.getUserId(), roleIds);
			revisedUserRoles = client.getUserRoles(cu.getUserId());
			Assert.assertTrue(revisedUserRoles != null && revisedUserRoles.size() == 2);
			logger.info("User role count is back at 2");

			client.dropUserRole(cu.getUserId(), cr.getRoleId());
			client.dropUserRole(cu.getUserId(), cr2.getRoleId());
			revisedUserRoles = client.getUserRoles(cu.getUserId());
			Assert.assertTrue(revisedUserRoles != null && revisedUserRoles.size() == 0);
			logger.info("User role count is back to zero");

			logger.info("Deleting role function");
			client.deleteRoleFunction(cr.getRoleId(), crf.getRoleFunctionId());

			logger.info("Deleting role 1");
			client.deleteRole(cr.getRoleId());
			logger.info("Deleting role 2");
			client.deleteRole(cr2.getRoleId());
			client.deleteUser(cu.getUserId());
		} catch (HttpClientErrorException ex) {
			logger.error("testRoleAndFunctions failed with response {}", ex.getResponseBodyAsString());
			throw ex;
		}

	}

	@Test
	public void testNotifications() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			final String loginName = "notif_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu.setEmail("testnotifuser@abc.com");
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPNotification no = new MLPNotification();
			no.setTitle("notif title");
			no.setMessage("notif msg");
			no.setUrl("http://notify.me");
			Date now = new Date();
			// An hour ago
			no.setStart(new Date(now.getTime() - 60 * 60 * 1000));
			// An hour from now
			no.setEnd(new Date(now.getTime() + 60 * 60 * 1000));
			no.setMsgSeverityCode(String.valueOf(MessageSeverityCode.LO));
			no = client.createNotification(no);
			Assert.assertNotNull(no.getNotificationId());

			no.setMessage("New enhanced message");
			client.updateNotification(no);

			MLPNotification no2 = new MLPNotification();
			no2.setTitle("notif2 title");
			no2.setMessage("notif2 msg");
			no2.setUrl("http://notify2.me");
			no2.setStart(new Date(now.getTime() - 60 * 1000));
			no2.setEnd(new Date(now.getTime() + 60 * 1000));
			no2.setMsgSeverityCode(String.valueOf(MessageSeverityCode.HI));
			no2 = client.createNotification(no2);

			long notCountTrans = client.getNotificationCount();
			Assert.assertTrue(notCountTrans > 0);

			RestPageResponse<MLPNotification> notifics = client.getNotifications(new RestPageRequest(0, 100));
			Assert.assertTrue(notifics.getNumberOfElements() == notCountTrans);

			// Assign user to this notification
			client.addUserToNotification(no.getNotificationId(), cu.getUserId());
			// Query for it
			Assert.assertEquals(1, client.getUserUnreadNotificationCount(cu.getUserId()));
			RestPageResponse<MLPUserNotification> userNotifs = client.getUserNotifications(cu.getUserId(), null);
			Assert.assertTrue(userNotifs.iterator().hasNext());
			logger.info("First user notification {}", userNotifs.iterator().next());

			// This next step mimics what a controller will do
			client.setUserViewedNotification(no.getNotificationId(), cu.getUserId());

			userNotifs = client.getUserNotifications(cu.getUserId(), null);
			Assert.assertEquals(0, client.getUserUnreadNotificationCount(cu.getUserId()));

			client.dropUserFromNotification(no.getNotificationId(), cu.getUserId());
			client.deleteNotification(no.getNotificationId());
			client.deleteUser(cu.getUserId());
		} catch (HttpStatusCodeException ex) {
			logger.error("testNotifications failed with response {}", ex.getResponseBodyAsString());
			throw ex;
		}

	}

	@Test
	public void testUserNotificationPreferences() throws Exception {
		MLPUser cu = new MLPUser();
		MLPUserNotifPref usrNotifPref = new MLPUserNotifPref();

		try {
			final String loginName = "notif_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu.setEmail("testusrnotifprefuser.com");
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());

			usrNotifPref.setUserId(cu.getUserId());
			usrNotifPref.setNotfDelvMechCode(String.valueOf(NotificationDeliveryMechanismCode.TX));
			usrNotifPref.setMsgSeverityCode(String.valueOf(MessageSeverityCode.HI));

			usrNotifPref = client.createUserNotificationPreference(usrNotifPref);
			Assert.assertNotNull(usrNotifPref.getUserNotifPrefId());

			usrNotifPref.setNotfDelvMechCode(String.valueOf(NotificationDeliveryMechanismCode.EM));
			client.updateUserNotificationPreference(usrNotifPref);

			MLPUserNotifPref prefById = client.getUserNotificationPreference(usrNotifPref.getUserNotifPrefId());
			Assert.assertNotNull(prefById);

			List<MLPUserNotifPref> usrNotifPrefs = client.getUserNotificationPreferences(cu.getUserId());
			Assert.assertTrue(usrNotifPrefs.iterator().hasNext());
			logger.info("First user notification preference{}", usrNotifPrefs.iterator().next());

		} catch (HttpStatusCodeException ex) {
			logger.error("testUserNotificationPreferences failed with response {}", ex.getResponseBodyAsString());
			throw ex;
		}

		// invalid tests
		try {
			client.getUserNotificationPreferences("bogusUser");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("get user notification preferences failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getUserNotificationPreference(99999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("get user notification preference failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createUserNotificationPreference(new MLPUserNotifPref());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create user notification preference failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createUserNotificationPreference(
					new MLPUserNotifPref(cu.getUserId(), "bogus", MessageSeverityCode.HI.name()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create user notification preference failed on bad deliv code as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createUserNotificationPreference(
					new MLPUserNotifPref(cu.getUserId(), NotificationDeliveryMechanismCode.EM.name(), "bogus"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create user notification preference failed on severity code as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			MLPUserNotifPref unp = new MLPUserNotifPref();
			unp.setUserNotifPrefId(999L);
			client.updateUserNotificationPreference(unp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update user notification preference failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteUserNotificationPreference(999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete user notification prference failed as expected: {}", ex.getResponseBodyAsString());
		}

		client.deleteUserNotificationPreference(usrNotifPref.getUserNotifPrefId());
		client.deleteUser(cu.getUserId());

	}

	@Test
	public void testStepResult() throws Exception {
		try {
			final String name = "Solution ID creation";
			MLPStepResult sr = new MLPStepResult();
			sr.setStepCode(StepTypeCode.OB.name());
			sr.setName(name);
			sr.setStatusCode(String.valueOf(StepStatusCode.SU));
			Date now = new Date();
			sr.setStartDate(new Date(now.getTime() - 60 * 1000));
			sr = client.createStepResult(sr);
			Assert.assertNotNull(sr.getStepResultId());
			logger.info("Created step result " + sr);

			sr.setResult("Some new stack trace result");
			client.updateStepResult(sr);

			MLPStepResult getById = client.getStepResult(sr.getStepResultId());
			Assert.assertNotNull(getById.getStepResultId());

			RestPageResponse<MLPStepResult> stepResults = client.getStepResults(new RestPageRequest(0, 10));
			Assert.assertTrue(stepResults.iterator().hasNext());
			logger.info("First step result {}", stepResults.iterator().next());

			HashMap<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("name", "~bogus~never~match");
			RestPageResponse<MLPStepResult> emptySearchResults = client.searchStepResults(queryParameters, true,
					new RestPageRequest(0, 10));
			Assert.assertTrue(emptySearchResults.getNumberOfElements() == 0);

			queryParameters.put("name", name);
			RestPageResponse<MLPStepResult> searchResults = client.searchStepResults(queryParameters, true,
					new RestPageRequest(0, 10));
			Assert.assertTrue(searchResults.getNumberOfElements() == 1);

			client.deleteStepResult(sr.getStepResultId());
		} catch (HttpStatusCodeException ex) {
			logger.error("testStepResults failed with response {}", ex.getResponseBodyAsString());
			throw ex;
		}

		// invalid tests

		try {
			client.getStepResult(99999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("get step result failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			client.searchStepResults(restr, true, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search step result empty failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("bogus", "value");
			client.searchStepResults(restr, true, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search step result bad field failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createStepResult(new MLPStepResult());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create step result failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createStepResult(new MLPStepResult("bogus", "name", StepStatusCode.FA.name(), new Date()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create step result failed on bad type code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createStepResult(new MLPStepResult(StepTypeCode.OB.name(), "name", "bogus", new Date()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create step result failed on bad status code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPStepResult stepResult = new MLPStepResult();
			stepResult.setStepResultId(999L);
			client.updateStepResult(stepResult);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update step result failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteStepResult(999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete step result failed as expected: {}", ex.getResponseBodyAsString());
		}

	}

	@Test
	public void testSiteConfig() throws Exception {
		final String s64 = "12345678901234567890123456789012345678901234567890123456789012345";
		Assert.assertNull(client.getSiteConfig("bogus"));
		try {
			MLPSiteConfig sc = new MLPSiteConfig("bogus", "{ \"some\" : \"json\" }");
			sc.setUserId("bogus");
			client.createSiteConfig(sc);
			throw new Exception("Unexpected succes on invalid user ID");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create config with bad user as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSiteConfig(new MLPSiteConfig(s64, "{ \"some\" : \"json\" }"));
			throw new Exception("Unexpected success on long key");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create config with long key as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateSiteConfig(new MLPSiteConfig("bogus", "{ \"some\" : \"json\" }"));
			throw new Exception("Unexpected success of bogus update");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update non-existent config as expected {}", ex.getResponseBodyAsString());
		}
		// this should work
		final String key = "controllerTest-siteConfig";
		MLPSiteConfig config = new MLPSiteConfig(key, "{ \"some\" : \"json\" }");
		config = client.createSiteConfig(config);
		Assert.assertNotNull(config.getCreated());
		logger.info("Created site config {}", config);
		config = client.getSiteConfig(key);
		Assert.assertNotNull(config);

		try {
			client.createSiteConfig(config);
			throw new Exception("Unexpected success on dupe");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create dupe config as expected {}", ex.getResponseBodyAsString());
		}
		config.setConfigValue("{ \"other\" : \"stuff\" }");
		client.updateSiteConfig(config);
		try {
			config.setConfigValue(null);
			client.updateSiteConfig(config);
			throw new Exception("Unexpected success of bad config");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update bad config as expected {}", ex.getResponseBodyAsString());
		}
		client.deleteSiteConfig(config.getConfigKey());
		try {
			client.deleteSiteConfig(config.getConfigKey());
			throw new Exception("Unexpected success of delete");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to delete fake config as expected {}", ex.getResponseBodyAsString());
		}
	}

	@Test
	public void testSiteContent() throws Exception {
		final byte[] bytes = { 0, 1, 2, 3, 4, 5 };
		final String s64 = "12345678901234567890123456789012345678901234567890123456789012345";
		Assert.assertNull(client.getSiteContent("bogus"));
		try {
			client.createSiteContent(new MLPSiteContent(s64, bytes, s64));
			throw new Exception("Unexpected success on long key");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create content with long key as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateSiteContent(new MLPSiteContent("bogus", bytes, s64));
			throw new Exception("Unexpected success of bogus update");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update non-existent content as expected {}", ex.getResponseBodyAsString());
		}
		// this should work
		final String key = "controllerTest-siteContent";
		MLPSiteContent content = new MLPSiteContent(key, bytes, "application/octet-stream");
		content = client.createSiteContent(content);
		Assert.assertNotNull(content.getCreated());
		logger.info("Created site config {}", content);
		content = client.getSiteContent(key);
		Assert.assertNotNull(content);

		try {
			client.createSiteContent(content);
			throw new Exception("Unexpected success on dupe");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create dupe config as expected {}", ex.getResponseBodyAsString());
		}
		content.setContentValue(new byte[0]);
		client.updateSiteContent(content);
		try {
			content.setContentValue(null);
			client.updateSiteContent(content);
			throw new Exception("Unexpected success of null content");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update bad content as expected {}", ex.getResponseBodyAsString());
		}
		client.deleteSiteContent(content.getContentKey());
		try {
			client.deleteSiteContent(content.getContentKey());
			throw new Exception("Unexpected success of delete");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to delete fake content as expected {}", ex.getResponseBodyAsString());
		}
	}

	@Test
	public void testThreadsComments() throws Exception {
		MLPUser cu = new MLPUser("commentUser", "coomment@abc.com", true);
		cu = client.createUser(cu);
		Assert.assertNotNull(cu.getUserId());

		MLPSolution cs = new MLPSolution("solution name", cu.getUserId(), true);
		cs = client.createSolution(cs);
		Assert.assertNotNull(cs.getSolutionId());

		MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0", cu.getUserId(),
				AccessTypeCode.PR.name());
		cr = client.createSolutionRevision(cr);
		Assert.assertNotNull(cr.getRevisionId());

		MLPThread thread = client.createThread(new MLPThread(cs.getSolutionId(), cr.getRevisionId()));
		Assert.assertTrue(thread != null && thread.getThreadId() != null);
		RestPageResponse<MLPThread> threads = client.getThreads(new RestPageRequest(0, 1));
		Assert.assertTrue(threads != null && threads.getNumberOfElements() > 0);

		MLPThread retrieved = client.getThread(thread.getThreadId());
		Assert.assertNotNull(retrieved);

		long threadCountById = client.getSolutionRevisionThreadCount(cs.getSolutionId(), cr.getRevisionId());
		Assert.assertTrue(threadCountById > 0);
		RestPageResponse<MLPThread> threadsById = client.getSolutionRevisionThreads(cs.getSolutionId(),
				cr.getRevisionId(), new RestPageRequest(0, 1));
		Assert.assertTrue(threadsById != null && threadsById.getNumberOfElements() > 0);

		long threadCount = client.getThreadCount();
		Assert.assertTrue(threadCount > 0);

		thread.setTitle("thread title");
		client.updateThread(thread);

		// Violate constraints
		try {
			MLPThread t = new MLPThread();
			t.setTitle(s64 + s64);
			client.createThread(t);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed constraints on create as expected {}", ex.getResponseBodyAsString());
		}
		try {
			thread.setTitle(s64 + s64);
			client.updateThread(thread);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed constraints on update as expected {}", ex.getResponseBodyAsString());
		}
		// set back old title
		thread.setTitle("thread title");

		// Recreate should fail
		try {
			client.createThread(thread);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create dupe as expected {}", ex.getResponseBodyAsString());
		}
		MLPThread bogus = new MLPThread();
		bogus.setThreadId("bogus");
		try {
			client.getThread("bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to get missing as expected {}", ex.getResponseBodyAsString());
		}
		// Update of missing should fail
		try {
			client.updateThread(bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update missing as expected {}", ex.getResponseBodyAsString());
		}
		// Delete of missing should fail
		try {
			client.deleteThread("bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to delete missing as expected {}", ex.getResponseBodyAsString());
		}

		MLPComment parent = client.createComment(new MLPComment(thread.getThreadId(), cu.getUserId(), "parent text"));
		Assert.assertTrue(parent != null && parent.getCommentId() != null);
		logger.info("Created parent comment: " + parent.toString());

		parent = client.getComment(thread.getThreadId(), parent.getCommentId());
		Assert.assertNotNull(parent);

		MLPComment reply = new MLPComment(thread.getThreadId(), cu.getUserId(), "child text");
		reply.setParentId(parent.getCommentId());
		reply = client.createComment(reply);
		Assert.assertTrue(reply != null && reply.getCommentId() != null);
		logger.info("Created reply comment: " + reply.toString());

		reply.setText(s64);
		client.updateComment(reply);

		long commentCount = client.getThreadCommentCount(thread.getThreadId());
		Assert.assertTrue(commentCount > 0);

		RestPageResponse<MLPComment> threadComments = client.getThreadComments(thread.getThreadId(),
				new RestPageRequest(0, 1));
		Assert.assertTrue(threadComments != null && threadComments.hasContent());

		long commentCountById = client.getSolutionRevisionCommentCount(cs.getSolutionId(), cr.getRevisionId());
		Assert.assertTrue(commentCountById > 0);

		RestPageResponse<MLPComment> commentsById = client.getSolutionRevisionComments(cs.getSolutionId(),
				cr.getRevisionId(), new RestPageRequest(0, 1));
		Assert.assertTrue(commentsById != null && commentsById.getNumberOfElements() > 0);

		try {
			client.getComment("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to get missing comment as expected {}", ex.getResponseBodyAsString());
		}
		try {
			MLPComment c = new MLPComment("thread", "user", "text");
			c.setParentId("bogus");
			client.createComment(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create comment bad parent as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.createComment(new MLPComment("bogus", "bogus", "text"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create comment bad thread as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.createComment(new MLPComment(thread.getThreadId(), "bogus", "text"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create comment bad user as expected {}", ex.getResponseBodyAsString());
		}
		char[] longCommentChars = new char[8193];
		for (int i = 0; i < longCommentChars.length; ++i)
			longCommentChars[i] = 'x';
		String longCommentString = new String(longCommentChars);
		try {
			MLPComment large = new MLPComment(thread.getThreadId(), cu.getUserId(), longCommentString);
			client.createComment(large);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create comment with large text as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.createComment(reply);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to create dupe as expected {}", ex.getResponseBodyAsString());
		}
		try {
			MLPComment c = new MLPComment("bogus", "bogus", "text");
			c.setCommentId("bogus");
			client.updateComment(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update missing comment as expected {}", ex.getResponseBodyAsString());
			reply.setThreadId(thread.getThreadId());
		}
		try {
			reply.setThreadId("bogus");
			client.updateComment(reply);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update with bad thread as expected {}", ex.getResponseBodyAsString());
			reply.setThreadId(thread.getThreadId());
		}
		try {
			reply.setParentId("bogus");
			client.updateComment(reply);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update with bad parent as expected {}", ex.getResponseBodyAsString());
			reply.setParentId(parent.getCommentId());
		}
		try {
			reply.setUserId("bogus");
			client.updateComment(reply);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update with bad user as expected {}", ex.getResponseBodyAsString());
			reply.setUserId(cu.getUserId());
		}
		try {
			reply.setText(longCommentString);
			client.updateComment(reply);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update existing comment with large text as expected {}",
					ex.getResponseBodyAsString());
			reply.setText("short");
		}
		try {
			client.updateComment(new MLPComment(thread.getThreadId(), "bogus", "text"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to update missing comment as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteComment("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed to delete missing as expected {}", ex.getResponseBodyAsString());
		}

		client.deleteComment(thread.getThreadId(), reply.getCommentId());
		client.deleteComment(thread.getThreadId(), parent.getCommentId());
		client.deleteThread(thread.getThreadId());
		client.deleteSolutionRevision(cs.getSolutionId(), cr.getRevisionId());
		client.deleteSolution(cs.getSolutionId());
		client.deleteUser(cu.getUserId());
	}

	@Test
	public void testPeerSolutionGroups() throws Exception {

		// Need a user to create a solution
		MLPUser cu = null;
		cu = new MLPUser();
		cu.setActive(true);
		final String loginName = "test_user_" + Long.toString(new Date().getTime());
		cu.setLoginName(loginName);
		cu.setEmail("testpeersolgrpuser@abc.com");
		cu = client.createUser(cu);
		Assert.assertNotNull("User ID", cu.getUserId());
		logger.info("Created user " + cu.getUserId());

		MLPSolution cs = new MLPSolution("solutionName", cu.getUserId(), true);
		cs = client.createSolution(cs);
		Assert.assertNotNull("Solution ID", cs.getSolutionId());
		logger.info("Created solution " + cs.getSolutionId());

		final String peerName = "Peer-" + Long.toString(new Date().getTime());
		MLPPeer pr = new MLPPeer(peerName, "x." + Long.toString(new Date().getTime()), "http://peer-api", true, true,
				"contact", PeerStatusCode.AC.name());
		pr = client.createPeer(pr);
		logger.info("Created peer " + pr.getPeerId());

		MLPPeerGroup pg1 = new MLPPeerGroup("peer group one");
		pg1 = client.createPeerGroup(pg1);
		Assert.assertNotNull(pg1.getGroupId());
		logger.info("Created peer group " + pg1.getGroupId());

		pg1.setDescription("some description");
		client.updatePeerGroup(pg1);

		String peerGrpName = "peer group 2";
		MLPPeerGroup pg2 = new MLPPeerGroup(peerGrpName);
		pg2 = client.createPeerGroup(pg2);
		Assert.assertNotNull(pg2.getGroupId());
		logger.info("Created peer group " + pg2.getGroupId());

		RestPageResponse<MLPPeerGroup> peerGroups = client.getPeerGroups(new RestPageRequest(0, 5));
		Assert.assertTrue(peerGroups != null && peerGroups.getNumberOfElements() > 0);

		String solGrpName = "solution group";
		MLPSolutionGroup sg = new MLPSolutionGroup(solGrpName);
		sg = client.createSolutionGroup(sg);
		Assert.assertNotNull(sg.getGroupId());
		logger.info("Created solution group " + sg.getGroupId());

		sg.setDescription("some description");
		client.updateSolutionGroup(sg);

		RestPageResponse<MLPSolutionGroup> solGroups = client.getSolutionGroups(new RestPageRequest(0, 5));
		Assert.assertTrue(solGroups != null && solGroups.getNumberOfElements() > 0);

		client.addPeerToGroup(pr.getPeerId(), pg1.getGroupId());
		RestPageResponse<MLPPeer> peersInGroup = client.getPeersInGroup(pg1.getGroupId(), new RestPageRequest());
		Assert.assertTrue(peersInGroup != null && peersInGroup.getNumberOfElements() > 0);

		RestPageResponse<MLPSolution> solutionsInGroup = null;
		client.addSolutionToGroup(cs.getSolutionId(), sg.getGroupId());
		solutionsInGroup = client.getSolutionsInGroup(sg.getGroupId(), new RestPageRequest());
		Assert.assertTrue(solutionsInGroup != null && solutionsInGroup.getNumberOfElements() > 0);

		RestPageResponse<MLPPeerSolAccMap> maps = null;

		client.mapPeerSolutionGroups(pg1.getGroupId(), sg.getGroupId());
		maps = client.getPeerSolutionGroupMaps(new RestPageRequest());
		Assert.assertTrue(maps != null && maps.getNumberOfElements() > 0);

		long access = client.checkRestrictedAccessSolution(pr.getPeerId(), cs.getSolutionId());
		Assert.assertTrue(access > 0);
		RestPageResponse<MLPSolution> restrSols = client.findRestrictedAccessSolutions(pr.getPeerId(),
				new RestPageRequest(0, 5));
		Assert.assertTrue(restrSols != null && restrSols.getNumberOfElements() > 0);

		client.unmapPeerSolutionGroups(pg1.getGroupId(), sg.getGroupId());
		maps = client.getPeerSolutionGroupMaps(new RestPageRequest());
		Assert.assertTrue(maps != null && maps.getNumberOfElements() == 0);

		client.mapPeerPeerGroups(pg2.getGroupId(), pg1.getGroupId());

		List<MLPPeer> peers = client.getPeerAccess(pr.getPeerId());
		Assert.assertNotNull(peers);

		client.unmapPeerPeerGroups(pg2.getGroupId(), pg1.getGroupId());

		client.dropSolutionFromGroup(cs.getSolutionId(), sg.getGroupId());
		solutionsInGroup = client.getSolutionsInGroup(sg.getGroupId(), new RestPageRequest());
		Assert.assertTrue(solutionsInGroup != null && solutionsInGroup.getNumberOfElements() == 0);

		client.dropPeerFromGroup(pr.getPeerId(), pg1.getGroupId());
		peersInGroup = client.getPeersInGroup(pg1.getGroupId(), new RestPageRequest());
		Assert.assertTrue(peersInGroup != null && peersInGroup.getNumberOfElements() == 0);

		access = client.checkRestrictedAccessSolution(pr.getPeerId(), cs.getSolutionId());
		Assert.assertTrue(access == 0);

		// Invalid cases
		try {
			client.getPeersInGroup(9999999L, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("getPeersInGroup failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getSolutionsInGroup(9999999L, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("getSolutionsInGroup failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionGroup sgx = new MLPSolutionGroup(solGrpName);
			sgx.setGroupId(999L);
			client.updateSolutionGroup(sgx);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPPeerGroup pg = new MLPPeerGroup("peer group");
			pg.setGroupId(999L);
			client.updatePeerGroup(pg);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("updatePeerGroup failed as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			MLPPeerGroup pgAnother = new MLPPeerGroup(peerGrpName);
			client.createPeerGroup(pgAnother);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer group failed due to duplicate group name as expected: {}",
					ex.getResponseBodyAsString());
		}

		try {
			client.createPeerGroup(new MLPPeerGroup());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPPeerGroup pgx = new MLPPeerGroup();
			pgx.setGroupId(1L);
			client.updatePeerGroup(pgx);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update peer group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deletePeerGroup(0L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete peer group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionGroup(new MLPSolutionGroup());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution group failed as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.createSolutionGroup(new MLPSolutionGroup(solGrpName));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution group failed due to duplicate group name as expected: {}",
					ex.getResponseBodyAsString());
		}

		try {
			MLPSolutionGroup sg2 = new MLPSolutionGroup();
			sg2.setGroupId(1L);
			client.updateSolutionGroup(sg2);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteSolutionGroup(0L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete solution group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			long bogus = 0L;
			client.addPeerToGroup("bogus", bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add peer group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			long bogus = 0L;
			client.addPeerToGroup(pr.getPeerId(), bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add peer to group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			long bogus = 0L;
			client.dropPeerFromGroup(pr.getPeerId(), bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop peer from group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			long bogus = 0L;
			client.addSolutionToGroup("bogus", bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add solution to group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			long bogus = 0L;
			client.addSolutionToGroup(cs.getSolutionId(), bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add solution to group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			long bogus = 0L;
			client.dropSolutionFromGroup(cs.getSolutionId(), bogus);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop solution from group failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.mapPeerSolutionGroups(9999L, 9999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("map peer solution groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.mapPeerSolutionGroups(pg1.getGroupId(), 9999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("map peer solution groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.unmapPeerSolutionGroups(9999L, 9999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("unmap peer solution groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.mapPeerPeerGroups(9999L, 9999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("map peer peer groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.mapPeerPeerGroups(pg1.getGroupId(), 9999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("map peer peer groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.mapPeerPeerGroups(pg1.getGroupId(), pg1.getGroupId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("map peer peer groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.unmapPeerPeerGroups(9999L, 9999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("unmap peer peer groups failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.checkRestrictedAccessSolution("peerId", "solutionId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("checkPeerSolutionAccess failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.checkRestrictedAccessSolution(pr.getPeerId(), "solutionId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("checkPeerSolutionAccess failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getPeerAccess("bogus peer id");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("checkPeerSolutionAccess failed as expected: {}", ex.getResponseBodyAsString());
		}

		client.deleteSolutionGroup(sg.getGroupId());
		client.deletePeerGroup(pg2.getGroupId());
		client.deletePeerGroup(pg1.getGroupId());
		client.deleteSolution(cs.getSolutionId());
		client.deletePeer(pr.getPeerId());
		client.deleteUser(cu.getUserId());

	}

	@Test
	public void testErrorConditions() throws Exception {

		MLPUser cu = new MLPUser();
		cu.setUserId(UUID.randomUUID().toString());
		try {
			client.getUser(cu.getUserId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get user failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteUser(cu.getUserId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete user failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createUser(cu);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create user failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateUser(cu);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update user failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		// Value too long
		try {
			client.createUser(new MLPUser(s64, "testloginnamelength@abc.com", true));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create user failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		// This one is supposed to work
		final String loginName = "user-" + Long.toString(new Date().getTime());
		final String sillyGoose = "sillygoose";
		cu = new MLPUser(loginName, "gooduser@abc.com", true);
		cu.setLoginHash(sillyGoose);
		cu = client.createUser(cu);
		try {
			client.createUser(cu);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create user failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			cu.setLoginHash("bogus");
			cu.setEmail(s64);
			client.updateUser(cu);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update user failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.loginUser(loginName, "");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Loging rejected on empty pass, as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.loginUser(loginName, sillyGoose + "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Loging rejected on bad pass, as expected {}", ex.getResponseBodyAsString());
		}

		try {
			MLPUser bogusUser = new MLPUser();
			bogusUser.setUserId("bogus");
			client.updatePassword(bogusUser, new MLPPasswordChangeRequest(sillyGoose + "bogus", "happyfeet"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Password update rejected on bad user, as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.updatePassword(cu, new MLPPasswordChangeRequest(sillyGoose + "bogus", "happyfeet"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Password update rejected invalid old password, as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.updatePassword(cu, new MLPPasswordChangeRequest(sillyGoose, ""));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Password update rejected empty new password as expected {}", ex.getResponseBodyAsString());
		}
		// No way to trigger constraint violation for password change

		try {
			client.addUserRole("bogusUser", "bogusRole");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add role failed on bad user as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.addUserRole(cu.getUserId(), "bogusRole");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add role failed on bad role as expected {}", ex.getResponseBodyAsString());
		}
		List<String> roles = new ArrayList<>();
		roles.add("bogusRole");
		try {
			client.updateUserRoles("boguUser", roles);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update user roles failed on bad user as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateUserRoles(cu.getUserId(), roles);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update user roles failed on bad role as expected {}", ex.getResponseBodyAsString());
		}
		List<String> emptyList = new ArrayList<>();
		try {
			client.addUsersInRole(emptyList, "bogusRole");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("addUsersInRole failed on bad role as expected {}", ex.getResponseBodyAsString());
		}

		try {
			client.addUserTag("bogusUser", "bogusTag");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add user tag failed on bad role as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropUserTag("bogusUser", "bogusTag");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop user tag failed on bad role as expected {}", ex.getResponseBodyAsString());
		}

		String roleNm = "some name";
		// Supposed to succeed
		MLPRole cr = client.createRole(new MLPRole(roleNm, true));
		try {
			client.createRole(cr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			cr.setName(s64 + s64);
			client.updateRole(cr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update role failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addUsersInRole(emptyList, cr.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("addUsersInRole failed on empty list as expected {}", ex.getResponseBodyAsString());
		}
		try {
			MLPRole roleAnother = new MLPRole(roleNm, true);
			client.createRole(roleAnother);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role failed due to duplicate role name as expected: {}", ex.getResponseBodyAsString());
		}

		List<String> users = new ArrayList<>();
		users.add("bogusUser");
		try {
			client.addUsersInRole(users, cr.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("addUsersInRole failed on bad user as expected {}", ex.getResponseBodyAsString());
		}
		users.clear();
		users.add(cu.getUserId());
		try {
			client.dropUsersInRole(users, cr.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("dropUsersInRole failed on not-in-role as expected {}", ex.getResponseBodyAsString());
		}
		// supposed to work
		client.addUsersInRole(users, cr.getRoleId());
		try {
			client.addUsersInRole(users, cr.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("addUsersInRole failed on already-in-role as expected {}", ex.getResponseBodyAsString());
		}
		MLPRole crCustomId = new MLPRole();
		crCustomId.setRoleId(UUID.randomUUID().toString());
		try {
			client.getRole(crCustomId.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get missing role failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteRole(crCustomId.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete  missing role failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createRole(crCustomId);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role failed on missing name as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateRole(crCustomId);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update missing role failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> roleRestr = new HashMap<>();
			client.searchRoles(roleRestr, false, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search role failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> roleRestr = new HashMap<>();
			roleRestr.put("bogus", "value");
			client.searchRoles(roleRestr, false, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search role failed on bogus as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getRoleFunctions("bogus");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get role functions failed on invalid role as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getRoleFunction("bogusRole", "bogusFn");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get role function failed on invalid role as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createRoleFunction(new MLPRoleFunction("roleId", "name"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role fn failed on bad role as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createRoleFunction(new MLPRoleFunction(cr.getRoleId(), s64 + s64));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role fn failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteRoleFunction("roleId", "name");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete role fn failed as expected: {}", ex.getResponseBodyAsString());
		}
		// Supposed to work
		MLPRoleFunction rf = client.createRoleFunction(new MLPRoleFunction(cr.getRoleId(), "otherRoleFuncName"));
		try {
			rf.setName(s64 + s64);
			client.updateRoleFunction(rf);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update role fn failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		String rfId = rf.getRoleFunctionId();
		try {
			rf.setRoleFunctionId("bogus");
			client.updateRoleFunction(rf);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role fn failed on bad role fn id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			rf.setRoleId("bogus");
			client.updateRoleFunction(rf);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create role fn failed on bad role as expected: {}", ex.getResponseBodyAsString());
		}
		rf.setRoleId(cr.getRoleId());
		rf.setRoleFunctionId(rfId);

		// Clean up the role stuff so subsequent Mariadb tests succeed without
		// recreating schema.
		try {
			client.dropUsersInRole(users, cr.getRoleId());
			client.deleteRoleFunction(cr.getRoleId(), rf.getRoleFunctionId());
			client.deleteRole(cr.getRoleId());
			rf = null;
			cr = null;
		} catch (HttpStatusCodeException ex) {
			// Print server error
			logger.error(ex.getResponseBodyAsString());
			throw ex;
		}

		try {
			client.getUserLoginProvider("bogusUser", "bogusCode", "bogusLogin");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("getUserLoginProvider failed on bad user as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.getUserLoginProvider(cu.getUserId(), "bogusCode", "bogusLogin");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("getUserLoginProvider failed on bad code as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.getUserLoginProvider(cu.getUserId(), LoginProviderCode.FB.name(), "bogusLogin");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("getUserLoginProvider failed on bad login as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.createUserLoginProvider(new MLPUserLoginProvider("bogus", "bogus", "something", "access token", 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("createUserLoginProvider failed on bad user as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.createUserLoginProvider(
					new MLPUserLoginProvider(cu.getUserId(), "bogus", "something", "access token", 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("createUserLoginProvider failed on bad code as expected {}", ex.getResponseBodyAsString());
		}
		MLPUserLoginProvider clp = new MLPUserLoginProvider(cu.getUserId(), LoginProviderCode.GH.name(), "something",
				"access token", 1);
		clp = client.createUserLoginProvider(clp);
		Assert.assertNotNull(clp.getCreated());
		try {
			clp.setDisplayName(s64 + s64 + s64 + s64 + s64);
			client.createUserLoginProvider(clp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("createUserLoginProvider failed on constraint as expected {}", ex.getResponseBodyAsString());
		}
		try {
			// display name should violate constraint
			clp.setDisplayName(s64 + s64 + s64 + s64 + s64);
			client.updateUserLoginProvider(clp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("updateUserLoginProvider failed on constraint as expected {}", ex.getResponseBodyAsString());
		}
		try {
			clp.setUserId("bogus");
			client.updateUserLoginProvider(clp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("updateUserLoginProvider failed on bad user as expected {}", ex.getResponseBodyAsString());
		}
		try {
			clp.setUserId(cu.getUserId());
			clp.setProviderCode("bogus");
			client.updateUserLoginProvider(clp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("updateUserLoginProvider failed on bad code as expected {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteUserLoginProvider(clp);
		} catch (HttpStatusCodeException ex) {
			logger.info("deleteUserLoginProvider failed as expected {}", ex.getResponseBodyAsString());
		}

		try {
			Map<String, Object> queryParameters = new HashMap<>();
			client.searchUsers(queryParameters, false, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search users failed on empty query as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			Map<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("bogusFieldName", "bogusFieldFValue");
			client.searchUsers(queryParameters, false, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search users failed on bad field as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			Map<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("picture", new MLPUser());
			client.searchUsers(queryParameters, false, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search users failed on bad type as expected: {}", ex.getResponseBodyAsString());
		}

		MLPSolution cs = new MLPSolution();
		cs.setSolutionId(UUID.randomUUID().toString());
		try {
			client.getSolution(cs.getSolutionId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteSolution(cs.getSolutionId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete solution failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolution(cs);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateSolution(cs);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolution(new MLPSolution("name", s64, true));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolution s = new MLPSolution("name", cu.getUserId(), true);
			s.setModelTypeCode("bogus");
			client.createSolution(s);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution failed on model code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolution s = new MLPSolution("name", cu.getUserId(), true);
			s.setToolkitTypeCode("bogus");
			client.createSolution(s);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution failed on toolkit code as expected: {}", ex.getResponseBodyAsString());
		}
		// This one is supposed to work
		cs = new MLPSolution("sol name", cu.getUserId(), true);
		cs = client.createSolution(cs);
		try {
			client.createSolution(cs);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			cs.setUserId(s64);
			client.updateSolution(cs);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		// restore valid value
		cs.setUserId(cu.getUserId());

		try {
			Map<String, Object> queryParameters = new HashMap<>();
			client.searchSolutions(queryParameters, false, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search solution failed on empty query as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			Map<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("bogusFieldName", "bogusFieldFValue");
			client.searchSolutions(queryParameters, false, new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search solution failed as expected: {}", ex.getResponseBodyAsString());
		}

		MLPSolutionRevision csr = new MLPSolutionRevision();
		csr.setRevisionId(UUID.randomUUID().toString());
		csr.setSolutionId("bogus");
		try {
			client.getSolutionRevision(cs.getSolutionId(), csr.getRevisionId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution revision failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteSolutionRevision("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete solution revision failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteSolutionRevision(cs.getSolutionId(), csr.getRevisionId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete solution revision failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution revision failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution revision failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionRevision s = new MLPSolutionRevision(cs.getSolutionId(), "version", cu.getUserId(), "bogus");
			client.createSolutionRevision(s);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution rev failed on acc code as expected: {}", ex.getResponseBodyAsString());
		}
		csr = new MLPSolutionRevision(cs.getSolutionId(), s64, cu.getUserId(), AccessTypeCode.PR.name());
		try {
			client.createSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		// This one is supposed to work
		final String solRevVersion = "1.0R";
		csr.setVersion(solRevVersion);
		csr = client.createSolutionRevision(csr);
		try {
			client.createSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			csr.setVersion(s64);
			client.updateSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution revision failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution revision failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionRevision r = new MLPSolutionRevision(cs.getSolutionId(), "version", "userId",
					AccessTypeCode.PB.name());
			r.setRevisionId("bogus");
			client.updateSolutionRevision(r);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution revision failed on bad rev as expected: {}", ex.getResponseBodyAsString());
		}
		// Restore valid value
		csr.setVersion(solRevVersion);

		try {
			MLPRevisionDescription rd = new MLPRevisionDescription("bogus", "XX", "bogus");
			rd = client.createRevisionDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision description failed on bad ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPRevisionDescription rd = new MLPRevisionDescription(csr.getRevisionId(), "XX", "bogus");
			rd = client.createRevisionDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision description failed on bad code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPRevisionDescription rd = new MLPRevisionDescription("bogus", "XX", "bogus");
			client.updateRevisionDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update revision description failed on bad ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPRevisionDescription rd = new MLPRevisionDescription(csr.getRevisionId(), "XX", "bogus");
			client.updateRevisionDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update revision description failed on bad code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteRevisionDescription("bogus", "XX");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete revision description failed as expected: {}", ex.getResponseBodyAsString());
		}

		MLPArtifact ca = new MLPArtifact();
		ca.setArtifactId(UUID.randomUUID().toString());
		try {
			client.getArtifact(ca.getArtifactId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteArtifact(ca.getArtifactId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createArtifact(ca);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createArtifact(new MLPArtifact("version", "bogus", "name", "URI", cu.getUserId(), 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create artifact failed on type code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateArtifact(ca);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		// These should succeed
		ca = client.createArtifact(
				new MLPArtifact("version", ArtifactTypeCode.BP.name(), "name", "URI", cu.getUserId(), 1));
		try {
			client.createArtifact(ca);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create artifact failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			client.searchArtifacts(restr, false, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search artifacts failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("bogus", "value");
			client.searchArtifacts(restr, false, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search artifacts failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionRevisionArtifact(cs.getSolutionId(), "bogusRevId", "bogusArtId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add sol rev artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionRevisionArtifact(cs.getSolutionId(), csr.getRevisionId(), "bogusArtId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add sol rev artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		client.addSolutionRevisionArtifact(cs.getSolutionId(), csr.getRevisionId(), ca.getArtifactId());
		try {
			client.dropSolutionRevisionArtifact(cs.getSolutionId(), "bogusRevId", "bogusArtId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop sol rev artifact failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			ca.setUserId(s64);
			client.updateArtifact(ca);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update artifact failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		// Restore valid value
		ca.setUserId(cu.getUserId());

		try {
			client.incrementSolutionViewCount("bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Increment soln view count failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getSolutionDownloads("bogus", new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution downloads failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getSolutionRatings("bogus", new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution ratings failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getSolutionRating("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution rating failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getSolutionRevisionsForArtifact("bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution revisions failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteTag(new MLPTag("bogus"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete tag failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createTag(new MLPTag(s64));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create tag failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		// This should succeed
		MLPTag ct = client.createTag(new MLPTag("tag-" + new Date().toString()));
		try {
			client.createTag(ct);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create tag failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}
		// This should succeed
		client.addSolutionTag(cs.getSolutionId(), ct.getTag());
		// now try adding it again
		try {
			client.addSolutionTag(cs.getSolutionId(), ct.getTag());
		} catch (HttpStatusCodeException ex) {
			logger.info("Add sol tag dupe failed as expected: {}", ex.getResponseBodyAsString());
		}
		// Again this should succeed
		client.dropSolutionTag(cs.getSolutionId(), ct.getTag());
		// Try deleting it again
		try {
			client.dropSolutionTag(cs.getSolutionId(), ct.getTag());
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop sol tag failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			String[] searchTags = new String[] { "%" };
			client.findPortalSolutions(null, null, true, null, null, null, searchTags, null, null,
					new RestPageRequest(0, 1));
			// I have not been able to make findPortalSolutions fail.
			// all arguments are optional; there is no illegal value; etc.
			// so can't throw new Exception("Unexpected success") here.
		} catch (HttpStatusCodeException ex) {
			logger.info("Find portal solutions failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionTag("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add tag failed on invalid solution as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropSolutionTag("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop tag failed on invalid solution as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropSolutionTag("bogus", ct.getTag());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop tag failed on invalid solution as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropSolutionTag(cs.getSolutionId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop tag failedon invalid tag  as expected: {}", ex.getResponseBodyAsString());
		}
		client.deleteTag(ct);

		try {
			MLPSolutionDownload sd = new MLPSolutionDownload(s64, "artId", s64);
			sd.setDownloadId(999L);
			client.deleteSolutionDownload(sd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete sol download failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionDownload sd = new MLPSolutionDownload(s64, "artId", s64);
			sd.setSolutionId(null);
			client.createSolutionDownload(sd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create sol download failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteSolutionRating(new MLPSolutionRating("solutionId", "userId", 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete rating failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionRating(new MLPSolutionRating("solId", "userId", 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create rating failed on bad solution as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionRating(new MLPSolutionRating(cs.getSolutionId(), "userId", 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create rating failed on bad user as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionRating rating = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 0);
			rating.setRating(null);
			client.createSolutionRating(rating);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create rating failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateSolutionRating(new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update rating failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		MLPSolutionRating sr = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 1);
		sr = client.createSolutionRating(sr);
		try {
			sr.setRating(null);
			client.updateSolutionRating(sr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update rating failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.getSolutionDeployments("bogus", "bogus", new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution deployments failed on bad sol ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getSolutionDeployments(cs.getSolutionId(), "bogus", new RestPageRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get solution deployments failed on bad rev ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionDeployment(new MLPSolutionDeployment("bogus", csr.getRevisionId(), cu.getUserId(),
					DeploymentStatusCode.DP.name()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad solution ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionDeployment(new MLPSolutionDeployment(cs.getSolutionId(), "bogus", cu.getUserId(),
					DeploymentStatusCode.DP.name()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad revision ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionDeployment(new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(), "bogus",
					DeploymentStatusCode.DP.name()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad user ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionDeployment solDep = new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(),
					cu.getUserId(), DeploymentStatusCode.DP.name());
			// Field too large
			solDep.setTarget(s64 + s64);
			client.createSolutionDeployment(solDep);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on constraints as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionDeployment(
					new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(), cu.getUserId(), "bogus"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad code as expected: {}", ex.getResponseBodyAsString());
		}

		MLPSolutionDeployment solDep = new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(),
				cu.getUserId(), DeploymentStatusCode.DP.name());
		solDep.setDeploymentId("bogus");
		try {
			client.updateSolutionDeployment(solDep);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution deployment failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteSolutionDeployment(solDep);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete solution deployment failed as expected: {}", ex.getResponseBodyAsString());
		}
		// Should succeed
		solDep = client.createSolutionDeployment(new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(),
				cu.getUserId(), DeploymentStatusCode.DP.name()));
		try {
			client.createSolutionDeployment(solDep);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create dupe solution deployment failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			solDep.setTarget(s64 + s64);
			client.updateSolutionDeployment(solDep);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution deployment failed on constraint as expected: {}",
					ex.getResponseBodyAsString());
		}

		try {
			client.getSolutionAccessUsers("bogus sol ID");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("getSolutionAccessUsers failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionUserAccess(cs.getSolutionId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add user solution ACL bad user failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionUserAccess("solId", cu.getUserId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add user solution ACL bad sol failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropSolutionUserAccess("solId", cu.getUserId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop user solution ACL failed bad sol as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropSolutionUserAccess(cs.getSolutionId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Drop user solution ACL failed bad user as expected: {}", ex.getResponseBodyAsString());
		}

		MLPNotification cn = new MLPNotification();
		cn.setNotificationId(UUID.randomUUID().toString());
		try {
			client.deleteNotification(cn.getNotificationId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete notification failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createNotification(cn);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create notification failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateNotification(cn);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update notification failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			cn.setTitle(s64 + s64);
			cn.setStart(new Date());
			cn.setEnd(new Date());
			client.createNotification(cn);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update notification failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		// This one should work
		cn.setTitle("notif title");
		cn.setMsgSeverityCode(String.valueOf(MessageSeverityCode.HI));
		cn = client.createNotification(cn);
		try {
			client.createNotification(cn);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create dupe notification failed as expected: {}", ex.getResponseBodyAsString());
		}
		cn.setTitle(s64 + s64);
		try {
			client.updateNotification(cn);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create notification failed on constreaint as expected: {}", ex.getResponseBodyAsString());
		}

		MLPPeer cp = new MLPPeer();
		cp.setPeerId(UUID.randomUUID().toString());
		try {
			client.getPeer(cp.getPeerId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get peer failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deletePeer(cp.getPeerId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete peer failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPeer(cp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPeer(new MLPPeer("peer name", "subj name", "api url", false, false, "contact 1", "bogus"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer failed on bad peer stat code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updatePeer(cp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update peer failed as expected: {}", ex.getResponseBodyAsString());
		}
		cp.setSelf(false);
		cp.setContact1("me");
		try {
			cp.setName(s64);
			cp = client.createPeer(cp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		// This one is supposed to work
		cp = client.createPeer(new MLPPeer("peer name", "subj name", "api url", false, false, "contact 1",
				PeerStatusCode.AC.name()));

		try {
			cp = client.createPeer(cp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			cp = client.createPeer(new MLPPeer("another peer name", "subj name", "api url", false, false, "contact 2",
					PeerStatusCode.DC.name()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer failed on duplicate subject name as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			cp.setName(s64);
			client.updatePeer(cp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update peer failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.getPeerSubscriptions("bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get peer subs failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getPeerSubscription(0L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get peer sub failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPeerSubscription(new MLPPeerSubscription("peerId", "userId", "scope", "access"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer sub failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPeerSubscription(
					new MLPPeerSubscription(cp.getPeerId(), cu.getUserId(), "bogus", AccessTypeCode.PB.toString()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer sub failed on bad scope code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPeerSubscription(
					new MLPPeerSubscription(cp.getPeerId(), cu.getUserId(), SubscriptionScopeCode.FL.name(), "bogus"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer sub failed on bad access code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPPeerSubscription ps = new MLPPeerSubscription(cp.getPeerId(), cu.getUserId(), "scope", "access");
			ps.setSelector(
					s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64);
			client.createPeerSubscription(ps);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer sub failed as expected: {}", ex.getResponseBodyAsString());
		}
		// Supposed to work
		MLPPeerSubscription ps = new MLPPeerSubscription(cp.getPeerId(), cu.getUserId(),
				SubscriptionScopeCode.FL.toString(), AccessTypeCode.PB.toString());
		ps = client.createPeerSubscription(ps);
		try {
			ps.setSelector(
					s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64);
			client.updatePeerSubscription(ps);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update peer sub failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			ps.setSubId(999L);
			client.updatePeerSubscription(ps);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update peer sub failed on bad sub ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deletePeerSubscription(999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete peer sub failed on bad sub ID as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			HashMap<String, Object> peerRestr = new HashMap<>();
			client.searchPeers(peerRestr, false, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search peers failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> peerRestr = new HashMap<>();
			peerRestr.put("bogus", "name");
			client.searchPeers(peerRestr, false, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Search peers failed on bad field as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			MLPSolutionFavorite sf = new MLPSolutionFavorite("solId", "userId");
			client.createSolutionFavorite(sf);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create fave sol failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionFavorite sf = new MLPSolutionFavorite(cs.getSolutionId(), "userId");
			client.createSolutionFavorite(sf);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create fave sol failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionFavorite sf = new MLPSolutionFavorite(cs.getSolutionId(), "userId");
			client.deleteSolutionFavorite(sf);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete fave sol failed as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.addUserToNotification("notifId", "userId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add user to notif failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addUserToNotification("notifId", cu.getUserId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add user to notif failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.setUserViewedNotification("notifId", "userId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Set user viewed notif failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.setUserViewedNotification("notifId", cu.getUserId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Add user to notif failed as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.getUserSolutionDeployments("solId", "revId", "userId", new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get user soln deps failed on bad sol id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getUserSolutionDeployments(cs.getSolutionId(), "revId", "userId", new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get user soln deps failed on bad rev id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getUserSolutionDeployments(cs.getSolutionId(), csr.getRevisionId(), "userId",
					new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Get user soln deps failed on bad user id as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			client.getPublishRequest(99999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("get publish request failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			client.searchPublishRequests(restr, true, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search publish request empty failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("bogus", "value");
			client.searchPublishRequests(restr, true, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search publish request bad field failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPublishRequest(new MLPPublishRequest());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create publish request failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPublishRequest(new MLPPublishRequest("bogus", "name", "bogus", "bogus"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create publish request failed on bad status code as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			MLPPublishRequest stepResult = new MLPPublishRequest();
			stepResult.setRequestId(999L);
			client.updatePublishRequest(stepResult);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update publish request failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deletePublishRequest(999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete publish request failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.getDocument("bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("get document failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPDocument doc = new MLPDocument("name", "uri", 100, "bogusUserId");
			client.updateDocument(doc);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update document failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteDocument("bogusId");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete document failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addCompositeSolutionMember("parent", "child");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add composite solution member failed on bad sol id as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.addCompositeSolutionMember(cs.getSolutionId(), "child");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add composite solution member failed on bad sol id as expected: {}",
					ex.getResponseBodyAsString());
		}

		try {
			client.deleteSolutionDeployment(solDep);
			client.dropSolutionRevisionArtifact(cs.getSolutionId(), csr.getRevisionId(), ca.getArtifactId());
			client.deleteSolutionRevision(cs.getSolutionId(), csr.getRevisionId());
			client.deleteArtifact(ca.getArtifactId());
			client.deletePeer(cp.getPeerId());
			client.deleteSolution(cs.getSolutionId());
			client.deleteUser(cu.getUserId());
		} catch (HttpStatusCodeException ex) {
			logger.info("Failed: {}", ex.getResponseBodyAsString());
			throw ex;
		}
	}

}
