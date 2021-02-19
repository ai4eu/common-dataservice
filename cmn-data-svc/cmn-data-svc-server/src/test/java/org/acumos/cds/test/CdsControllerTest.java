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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPHyperlink;
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Tests server controller classes by sending in requests with the client. The
 * server is launched with a Derby in-memory database.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CdsControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Defined in the default application.properties file
	private final String hostname = "localhost";

	// For tripping length constraints
	private final String s64 = "12345678901234567890123456789012345678901234567890123456789012345";

	// From properties
	@Value("${server.servlet.context-path}")
	private String contextPath;
	@Value("${spring.security.user.name}")
	private String userName;
	@Value("${spring.security.user.password}")
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

			cu.setVerifyTokenHash("other");
			client.updateUser(cu);

			// query the user to be sure
			MLPUser cu2 = client.getUser(cu.getUserId());
			Assert.assertEquals(cu.getUserId(), cu2.getUserId());

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
			Assert.assertNotNull(fetched);
			Assert.assertNotNull(fetched.getTags());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId());
			cr.setPublisher("Big Data Org");
			cr = client.createSolutionRevision(cr);
			logger.info("Created solution revision {}", cr);

			// Query for the revision
			MLPSolutionRevision crq = client.getSolutionRevision(cs.getSolutionId(), cr.getRevisionId());
			Assert.assertNotNull(crq);
			Assert.assertNotNull(crq.getOnboarded());

			final String version = "1.0A";
			MLPArtifact ca = new MLPArtifact(version, "DI", "artifact name", "http://nexus/artifact", cu.getUserId(),
					1);
			ca = client.createArtifact(ca);
			Assert.assertNotNull(ca.getArtifactId());
			Assert.assertNotNull(ca.getCreated());
			logger.info("Created artifact {}", ca);

			logger.info("Adding artifact to revision");
			client.addSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());

			logger.info("Deleting objects");
			// This cascades
			client.deleteSolution(cs.getSolutionId());
			// This does not
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
	public void testCodeValueConstants() throws Exception {
		List<String> valueSetNames = client.getValueSetNames();
		Assert.assertEquals(valueSetNames.size(), CodeNameType.values().length);
		for (String vsName : valueSetNames)
			CodeNameType.valueOf(vsName);

		for (CodeNameType name : CodeNameType.values()) {
			try {
				List<MLPCodeNamePair> list = client.getCodeNamePairs(name);
				logger.info("testCodeValueConstants: name {} -> values {}", name, list);
				Assert.assertFalse(name.toString(), list.isEmpty());
				// Cannot validate here - list of values is defined by server config
			} catch (HttpStatusCodeException ex) {
				logger.error("testCodeValueConstants failed", ex.getResponseBodyAsString());
				throw ex;
			}
		}
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
			final String providerCode = "FB";
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

	@SuppressWarnings("deprecation")
	@Test
	public void createSolutionWithArtifacts() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			// Use this repeatedly :)
			RestPageRequest rp = new RestPageRequest(0, 1);

			MLPUser cu = new MLPUser();
			final String loginName = "artifact-user-";
			final String loginPass = "test_client_pass";
			final String apiToken = "test_client_api_token";
			final String verifyToken = "test_client_verify";
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setApiToken(apiToken);
			cu.setVerifyTokenHash(verifyToken);
			cu.setEmail("createSolArtuser@abc.com");
			final String firstName = "SallieMaie";
			cu.setFirstName(firstName);
			final String lastName = "LastCreateSolArt";
			cu.setLastName(lastName);
			cu.setActive(true);
			cu.setLoginPassExpire(Instant.now());
			final byte[] fakePicture = new byte[] { 1, 2, 3, 4, 5 };
			cu.setPicture(fakePicture);
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			// The API token is lightly encrypted at the server
			// check that cleartext is returned.
			Assert.assertEquals(apiToken, cu.getApiToken());
			// Created and modified are not necessarily equal; allow for minor difference
			Assert.assertTrue(Math.abs(cu.getCreated().getEpochSecond() - cu.getModified().getEpochSecond()) < 2);
			// Hashed passwords must not come back in response
			Assert.assertNull(cu.getLoginHash());
			Assert.assertNull(cu.getVerifyTokenHash());
			logger.info("Created user {}", cu);

			MLPUser inactiveUser = new MLPUser("inactiveUser", "email@address.org", false);
			inactiveUser.setLoginHash("cleartext");
			inactiveUser = client.createUser(inactiveUser);
			Assert.assertNotNull(inactiveUser.getUserId());

			MLPUser visitingUser = new MLPUser("Visitor", "email@read-only.org", true);
			visitingUser.setLoginHash("othervisitor");
			visitingUser = client.createUser(visitingUser);
			Assert.assertNotNull(visitingUser.getUserId());

			RestPageResponse<MLPUser> users = client.getUsers(rp);
			Assert.assertNotNull(users);
			Assert.assertNotEquals(0, users.getNumberOfElements());
			for (MLPUser u : users.getContent())
				logger.info("Fetched user: " + u);

			MLPUser preLogin = client.getUser(cu.getUserId());
			Assert.assertNull(preLogin.getLastLogin());
			MLPUser loggedIn = client.loginUser(loginName, loginPass);
			Assert.assertNotNull(loggedIn);
			Assert.assertNotNull(loggedIn.getLastLogin());
			logger.info("Logged in successfully, password expires {}", loggedIn.getLoginPassExpire());
			Assert.assertArrayEquals(fakePicture, loggedIn.getPicture());

			// manual update of last login should not be necessary
			Instant lastLogin = Instant.now();
			cu.setLastLogin(lastLogin);
			client.updateUser(cu);
			cu = client.getUser(cu.getUserId());
			// The instant is not returned from Mysql perfectly, off by millis, why?
			long diff = Math.abs(lastLogin.getEpochSecond() - cu.getLastLogin().getEpochSecond());
			Assert.assertTrue(diff < 100);

			MLPUser apiUser = client.loginApiUser(loginName, apiToken);
			Assert.assertNotNull(apiUser);
			Assert.assertEquals(apiUser.getApiToken(), apiToken);
			logger.info("Logged in successfully via API token");

			MLPUser verifyUser = client.verifyUser(loginName, verifyToken);
			Assert.assertNotNull(verifyUser);
			// Last-login should be updated yet again here
			Assert.assertTrue(loggedIn.getLastLogin().compareTo(verifyUser.getLastLogin()) < 0);
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
				logger.info("Login temporary block worked as expected: {}", ex.getResponseBodyAsString());
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
			userRestr.put("firstName", "~one~");
			userRestr.put("middleName", "~two`~");
			userRestr.put("lastName", "~three~");
			userRestr.put("orgName", "~four~");
			userRestr.put("email", "~five~");
			userRestr.put("loginName", "~six~");
			RestPageResponse<MLPUser> emptyUserPage = client.searchUsers(userRestr, false, new RestPageRequest());
			Assert.assertEquals(0, emptyUserPage.getNumberOfElements());

			// Now use query parameters that should match
			userRestr.clear();
			userRestr.put("active", "true");
			userRestr.put("firstName", firstName);
			userRestr.put("lastName", lastName);
			RestPageResponse<MLPUser> userPage = client.searchUsers(userRestr, false, new RestPageRequest());
			Assert.assertEquals(1, userPage.getNumberOfElements());
			MLPUser testUser = userPage.iterator().next();
			// Password must not come back as JSON
			Assert.assertNull(testUser.getLoginHash());

			// Search for the active user by name
			Map<String, String> fieldMap = new HashMap<>();
			fieldMap.put("firstName", "DESC");
			RestPageResponse<MLPUser> lu = client.findUsersBySearchTerm(firstName, new RestPageRequest(0, 1, fieldMap));
			Assert.assertNotNull(lu);
			Assert.assertNotNull(lu.getContent());
			Assert.assertNotEquals(0, lu.getContent().size());
			MLPUser searchUser = lu.getContent().get(0);
			Assert.assertNull(searchUser.getLoginHash());

			// Check count
			long userCountTrans = client.getUserCount();
			Assert.assertNotEquals(0, userCountTrans);

			MLPUserLoginProvider clp = new MLPUserLoginProvider(cu.getUserId(), "GH", "something", "access token", 0);
			clp = client.createUserLoginProvider(clp);
			logger.info("Created user login provider {}", clp);

			// Fetch all login providers for user
			List<MLPUserLoginProvider> userProvs = client.getUserLoginProviders(cu.getUserId());
			Assert.assertNotNull(userProvs);
			Assert.assertNotEquals(0, userProvs.size());

			// Create Peer
			MLPPeer pr = new MLPPeer();
			final String peerName = "PeerCreateSolArts";
			pr.setName(peerName);
			pr.setSubjectName("subject name");
			pr.setApiUrl("http://peer-api");
			pr.setContact1("Katherine Globe");
			pr.setStatusCode("AC");
			pr = client.createPeer(pr);
			pr.setSelf(false);
			logger.info("Created peer with ID {}", pr.getPeerId());

			pr.setSelf(true);
			client.updatePeer(pr);
			pr = client.getPeer(pr.getPeerId());
			Assert.assertTrue(pr.isSelf());

			RestPageResponse<MLPPeer> peerPage = client.getPeers(rp);
			Assert.assertNotEquals(0, peerPage.getNumberOfElements());

			// Cover case of zero results
			HashMap<String, Object> peerRestr = new HashMap<>();
			peerRestr.put("name", "bogusbogus~~nevermatch");
			RestPageResponse<MLPPeer> emptyPerSearchResult = client.searchPeers(peerRestr, false,
					new RestPageRequest(0, 1));
			Assert.assertEquals(0, emptyPerSearchResult.getNumberOfElements());

			// Cover case of non-zero results
			peerRestr.clear();
			peerRestr.put("self", Boolean.TRUE);
			Map<String, String> peerFieldMap = new HashMap<>();
			peerFieldMap.put("name", "ASC");
			RestPageResponse<MLPPeer> peerSearchResult = client.searchPeers(peerRestr, false,
					new RestPageRequest(0, 1, peerFieldMap));
			Assert.assertNotEquals(0, peerSearchResult.getNumberOfElements());

			MLPPeer pr2 = client.getPeer(pr.getPeerId());
			Assert.assertEquals(pr.getPeerId(), pr2.getPeerId());

			Assert.assertEquals(0, client.getPeerSubscriptionCount(pr.getPeerId()));

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId());
			ps = client.createPeerSubscription(ps);
			logger.info("Created peer subscription {}", ps);

			ps.setMaxArtifactSize(9999L);
			client.updatePeerSubscription(ps);

			long peerSubCount = client.getPeerSubscriptionCount(pr.getPeerId());
			Assert.assertEquals(1, peerSubCount);

			MLPPeerSubscription ps2 = client.getPeerSubscription(ps.getSubId());
			Assert.assertNotNull(ps2);

			List<MLPPeerSubscription> peerSubs = client.getPeerSubscriptions(pr.getPeerId());
			Assert.assertNotNull(peerSubs);
			Assert.assertNotEquals(0, peerSubs.size());
			logger.info("Fetched list of peer subscriptions of size {}", peerSubs.size());

			MLPPeerSubscription fetchedPeerSub = client.getPeerSubscription(ps.getSubId());
			Assert.assertNotNull(fetchedPeerSub);
			logger.info("Fetched peer subscriptions {}", fetchedPeerSub);

			MLPArtifact ca = new MLPArtifact();
			final String version = "1.0A";
			ca.setVersion(version);
			ca.setName("artifact name");
			ca.setUri("http://nexus/artifact");
			ca.setUserId(cu.getUserId());
			ca.setArtifactTypeCode("DI");
			ca.setSize(1);
			ca = client.createArtifact(ca);
			logger.info("Created artifact with ID {}", ca.getArtifactId());

			ca.setSize(2);
			client.updateArtifact(ca);

			MLPArtifact art2 = client.getArtifact(ca.getArtifactId());
			Assert.assertEquals(ca.getArtifactId(), art2.getArtifactId());

			// Check count
			long artCountTrans = client.getArtifactCount();
			Assert.assertNotEquals(0, artCountTrans);

			final String artId = "e007ce63-086f-4f33-84c6-cac270874d81";
			logger.info("Creating artifact with ID {}", artId);
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setArtifactId(artId);
			ca2.setVersion("2.0A");
			ca2.setName("replicated artifact ");
			ca2.setUri("http://other.foo");
			ca2.setArtifactTypeCode("CD");
			ca2.setUserId(cu.getUserId());
			ca2.setSize(456);
			ca2 = client.createArtifact(ca2);
			Assert.assertEquals(artId, ca2.getArtifactId());
			logger.info("Created artifact with preset ID {}", artId);
			client.deleteArtifact(ca2.getArtifactId());

			// Get list
			RestPageResponse<MLPArtifact> arts = client.getArtifacts(new RestPageRequest(0, 100, "artifactId"));
			Assert.assertNotEquals(0, arts.getNumberOfElements());
			// Search like
			RestPageResponse<MLPArtifact> likes = client.findArtifactsBySearchTerm("artifact",
					new RestPageRequest(0, 10, "name"));
			Assert.assertNotEquals(0, likes.getNumberOfElements());
			// Search exactly
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("version", version);
			RestPageResponse<MLPArtifact> filtered = client.searchArtifacts(restr, true, new RestPageRequest(0, 10));
			Assert.assertNotEquals(0, filtered.getNumberOfElements());

			// This will get no results but will cover some clauses
			restr.clear();
			restr.put("uri", "http://nowhwere");
			filtered = client.searchArtifacts(restr, true, new RestPageRequest(0, 10));
			Assert.assertEquals(0, filtered.getNumberOfElements());

			// Also check that Spring doesn't truncate last path variable
			final String tagName1 = Long.toString(Instant.now().getEpochSecond()) + ".tag.1";
			final String tagName2 = Long.toString(Instant.now().getEpochSecond()) + ".tag.2";
			MLPTag tag1 = new MLPTag(tagName1);
			MLPTag tag2 = new MLPTag(tagName2);
			tag1 = client.createTag(tag1);
			logger.info("Created tag {}", tag1);
			tag2 = client.createTag(tag2);
			// Get list
			RestPageResponse<MLPTag> tags = client.getTags(new RestPageRequest(0, 100));
			Assert.assertNotEquals(0, tags.getNumberOfElements());

			// Tag some users for fun
			client.addUserTag(cu.getUserId(), tagName1);
			// Force creation of tag
			final String otherTag = "tag-" + Long.toString(Instant.now().getEpochSecond());
			client.addUserTag(cu.getUserId(), otherTag);
			client.dropUserTag(cu.getUserId(), otherTag);
			client.deleteTag(new MLPTag(otherTag));
			try {
				client.addUserTag(cu.getUserId(), tagName1);
				throw new Exception("Unexpected success add user tag");
			} catch (HttpStatusCodeException ex) {
				logger.info("Failed to tag user a second time as expected");
			}
			MLPUser taggedUser = client.getUser(cu.getUserId());
			Assert.assertTrue(taggedUser.getTags().contains(tag1));
			client.dropUserTag(cu.getUserId(), tagName1);

			MLPCatalog catPub = client
					.createCatalog(new MLPCatalog("PB", false, "public catalog name", "me", "http://pub.acumos.org"));
			Assert.assertNotNull("Catalog ID", catPub.getCatalogId());
			logger.info("Created catalog {}", catPub);

			MLPCatalog catRes = client.createCatalog(
					new MLPCatalog("RS", false, "restricted catalog name", "them", "http://private.acumos.org"));
			Assert.assertNotNull("Catalog ID", catRes.getCatalogId());
			logger.info("Created catalog {}", catRes);

			MLPSolution cs = new MLPSolution("solution name", cu.getUserId(), true);
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("CP");
			// This tag should spring into existence here
			MLPTag newTag = new MLPTag("new-solution-tag.3");
			cs.getTags().add(newTag);
			cs = client.createSolution(cs);
			Assert.assertNotNull(cs.getSolutionId());
			Assert.assertFalse(cs.getTags().isEmpty());
			Assert.assertTrue(cs.getTags().contains(newTag));
			logger.info("Created public solution {}", cs);

			Assert.assertTrue(client.isUserAccessToSolution(cu.getUserId(), cs.getSolutionId()));
			Assert.assertFalse(client.isUserAccessToSolution(inactiveUser.getUserId(), cs.getSolutionId()));
			client.addSolutionToCatalog(cs.getSolutionId(), catPub.getCatalogId());
			Assert.assertEquals(1, client.getCatalogSolutionCount(catPub.getCatalogId()));
			Assert.assertTrue(client.isUserAccessToSolution(inactiveUser.getUserId(), cs.getSolutionId()));

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

			// no tags, published to restr cat, and the name is searched below
			MLPSolution csRes = new MLPSolution("solution restricted", cu.getUserId(), true);
			csRes.setModelTypeCode("DS");
			csRes.setToolkitTypeCode("SK");
			csRes = client.createSolution(csRes);
			Assert.assertNotNull(csRes.getSolutionId());
			logger.info("Created solution {}", csRes);

			client.addSolutionToCatalog(csRes.getSolutionId(), catRes.getCatalogId());
			Assert.assertEquals(1, client.getCatalogSolutionCount(catRes.getCatalogId()));

			MLPSolution csActPriv = new MLPSolution("solution active private", cu.getUserId(), true);
			csActPriv.setModelTypeCode("DS");
			csActPriv.setToolkitTypeCode("SK");
			csActPriv = client.createSolution(csActPriv);
			Assert.assertNotNull(csActPriv.getSolutionId());
			logger.info("Created solution {}", csActPriv);

			MLPSolution inactive = new MLPSolution("inactive solution name", cu.getUserId(), false);
			inactive.setModelTypeCode("DS");
			inactive.setToolkitTypeCode("SK");
			inactive = client.createSolution(inactive);
			Assert.assertNotNull(inactive.getSolutionId());
			logger.info("Created inactive solution {}", inactive);

			// Check count
			long solCountTrans = client.getSolutionCount();
			Assert.assertNotEquals(0, solCountTrans);

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
			Assert.assertNotNull(s);
			Assert.assertNotNull(s.getTags());
			Assert.assertFalse(s.getTags().isEmpty());
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
			Assert.assertNotNull(cs);
			Assert.assertNotNull(cs.getTags());
			Assert.assertFalse(cs.getTags().isEmpty());
			;
			logger.info("Solution tags: {}", cs.getTags());

			logger.info("Getting all solutions");
			RestPageResponse<MLPSolution> page = client.getSolutions(new RestPageRequest(0, 2, "name"));
			Assert.assertNotNull(page);
			Assert.assertNotEquals(0, page.getTotalElements());

			cs.setOrigin("some origin");
			client.updateSolution(cs);
			logger.info("Fetching back updated solution");
			MLPSolution updated = client.getSolution(cs.getSolutionId());
			Assert.assertNotNull(updated);
			Assert.assertNotNull(updated.getTags());
			Assert.assertFalse(updated.getTags().isEmpty());
			Assert.assertNotNull(updated.getViewCount());
			Assert.assertNotEquals(new Long(0), updated.getViewCount());

			logger.info("Querying for solutions with similar names");
			RestPageResponse<MLPSolution> sl1 = client.findSolutionsBySearchTerm("solution", new RestPageRequest(0, 1));
			Assert.assertNotNull(sl1);
			Assert.assertNotEquals(0, sl1.getNumberOfElements());

			logger.info("Querying for solutions by tag");
			RestPageResponse<MLPSolution> sl2 = client.findSolutionsByTag(tagName1, new RestPageRequest(0, 5));
			Assert.assertNotNull(sl2);
			Assert.assertNotEquals(0, sl2.getNumberOfElements());

			// Add user access for this inactive user
			client.addSolutionUserAccess(cs.getSolutionId(), inactiveUser.getUserId());

			// Query two ways
			List<MLPUser> solUserAccList = client.getSolutionAccessUsers(cs.getSolutionId());
			Assert.assertNotNull(solUserAccList);
			Assert.assertNotEquals(0, solUserAccList.size());
			logger.info("Got users with access to solution {}", cs.getSolutionId());
			RestPageResponse<MLPSolution> userSolAccList = client.getUserAccessSolutions(inactiveUser.getUserId(),
					new RestPageRequest(0, 1));
			Assert.assertNotNull(userSolAccList);
			Assert.assertNotEquals(0, userSolAccList.getNumberOfElements());
			logger.info("Got solutions accessible by user {}", cu.getUserId());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId());
			cr.setAuthors(new AuthorTransport[] { new AuthorTransport("my name", "http://github") });
			cr.setPublisher("publisher 1");
			cr = client.createSolutionRevision(cr);
			Assert.assertNotNull(cr.getRevisionId());
			logger.info("Created solution revision {}", cr.getRevisionId());
			cr.setVerifiedLicense("FA");
			cr.setVerifiedVulnerability("FA");
			client.updateSolutionRevision(cr);

			MLPSolutionRevision crOrg = new MLPSolutionRevision(csRes.getSolutionId(), "1.0R", cu.getUserId());
			crOrg.setAuthors(new AuthorTransport[] { new AuthorTransport("your name", "email") });
			crOrg.setPublisher("publisher 2");
			crOrg = client.createSolutionRevision(crOrg);
			Assert.assertNotNull(crOrg.getRevisionId());
			logger.info("Created solution revision {}", cr.getRevisionId());

			logger.info("Adding artifact to revision 1");
			client.addSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());

			logger.info("Adding artifact to revision 2");
			client.addSolutionRevisionArtifact(csRes.getSolutionId(), crOrg.getRevisionId(), ca.getArtifactId());

			logger.info("Querying revision artifacts");
			List<MLPArtifact> revArgs = client.getSolutionRevisionArtifacts(cs.getSolutionId(), cr.getRevisionId());
			Assert.assertFalse(revArgs.isEmpty());

			logger.info("Creating description for revision 1, catalog restr");
			MLPRevCatDescription revCatDesc1 = new MLPRevCatDescription(cr.getRevisionId(), catRes.getCatalogId(),
					"Secret text");
			revCatDesc1 = client.createRevCatDescription(revCatDesc1);
			Assert.assertNotNull(revCatDesc1.getCreated());
			final String descFoo = "foo";
			revCatDesc1.setDescription(descFoo);
			client.updateRevCatDescription(revCatDesc1);
			revCatDesc1 = client.getRevCatDescription(cr.getRevisionId(), catRes.getCatalogId());
			Assert.assertNotNull(revCatDesc1);
			Assert.assertEquals(descFoo, revCatDesc1.getDescription());

			logger.info("Creating description for revision 1, catalog public");
			MLPRevCatDescription revCatDesc2 = new MLPRevCatDescription(cr.getRevisionId(), catPub.getCatalogId(),
					"Public text");
			revCatDesc2 = client.createRevCatDescription(revCatDesc2);
			Assert.assertNotNull(revCatDesc2.getCreated());

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

			logger.info("Associating document to rev 1, catalog 1");
			client.addRevisionCatalogDocument(cr.getRevisionId(), catPub.getCatalogId(), doc.getDocumentId());
			List<MLPDocument> dl = client.getRevisionCatalogDocuments(cr.getRevisionId(), catPub.getCatalogId());
			Assert.assertFalse(dl.isEmpty());

			logger.info("Querying for revisions by solution");
			List<MLPSolutionRevision> revs = client.getSolutionRevisions(new String[] { s.getSolutionId() });
			Assert.assertNotNull(revs);
			Assert.assertNotEquals(0, revs.size());
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
			Assert.assertNotNull(activePbPage);
			Assert.assertFalse(activePbPage.getContent().isEmpty());
			logger.info("Active PB solution page count {}", activePbPage.getContent().size());

			logger.info("Querying for inactive solutions");
			Map<String, Object> inactiveSols = new HashMap<>();
			inactiveSols.put("active", Boolean.TRUE);
			RestPageResponse<MLPSolution> inactiveSolList = client.searchSolutions(inactiveSols, false,
					new RestPageRequest());
			Assert.assertNotNull(inactiveSolList);
			Assert.assertNotEquals(0, inactiveSolList.getNumberOfElements());
			logger.info("Inactive PB solution count {}", inactiveSolList.getNumberOfElements());

			logger.info("Cover search-solution code");
			Map<String, Object> coverSols = new HashMap<>();
			coverSols.put("active", Boolean.TRUE);
			coverSols.put("name", "~1");
			coverSols.put("userId", "~2");
			coverSols.put("sourceId", "~3");
			coverSols.put("modelTypeCode", "~4");
			coverSols.put("toolkitTypeCode", "~5");
			coverSols.put("origin", "~6");
			RestPageResponse<MLPSolution> coverSolList = client.searchSolutions(coverSols, false,
					new RestPageRequest());
			Assert.assertFalse(coverSolList.hasContent());

			// Portal dynamic search
			logger.info("Querying for any solutions via flexible i/f");
			RestPageResponse<MLPSolution> portalAnyMatches = client.findPortalSolutions(null, null, true, null, null,
					null, null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(portalAnyMatches);
			Assert.assertTrue(portalAnyMatches.getNumberOfElements() > 1);

			logger.info("Querying for valid tag on solutions via flexible i/f");
			String[] searchTags = new String[] { tagName1 };
			RestPageResponse<MLPSolution> portalTagMatches = client.findPortalSolutions(null, null, true, null, null,
					searchTags, null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(portalTagMatches);
			Assert.assertNotEquals(0, portalTagMatches.getNumberOfElements());

			logger.info("Querying for bogus tag on solutions via flexible i/f");
			String[] bogusTags = new String[] { "bogus" };
			RestPageResponse<MLPSolution> portalTagNoMatches = client.findPortalSolutions(null, null, true, null, null,
					bogusTags, null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(portalTagNoMatches);
			Assert.assertEquals(0, portalTagNoMatches.getNumberOfElements());

			// Check fetch by ID to find both
			logger.info("Querying for solutions by id");
			String[] ids = { cs.getSolutionId(), csRes.getSolutionId() };
			String[] catalogIds = { catPub.getCatalogId() };
			RestPageResponse<MLPSolution> idSearchResult = client.findPublishedSolutionsByKwAndTags(ids, true, null,
					null, null, null, catalogIds, new RestPageRequest(0, 2));
			Assert.assertNotNull(idSearchResult);
			Assert.assertEquals(1, idSearchResult.getNumberOfElements());
			logger.info("Found models by id total " + idSearchResult.getTotalElements());

			// Both keywords must occur in the same field for a match
			logger.info("Querying for solutions by keyword");
			String[] kw = { "solution", "restricted" };
			RestPageResponse<MLPSolution> kwSearchResult = client.findPublishedSolutionsByKwAndTags(kw, true, null,
					null, null, null, null, new RestPageRequest(0, 2));
			Assert.assertNotNull(kwSearchResult);
			Assert.assertNotEquals(0, kwSearchResult.getNumberOfElements());
			logger.info("Found models by kw total " + kwSearchResult.getTotalElements());

			logger.info("Querying for solutions by tags");
			String[] allTags = new String[] { tagName1 };
			String[] anyTags = null; // new String[] { tagName2 };
			RestPageResponse<MLPSolution> allAnyTagsSearchResult = client.findPublishedSolutionsByKwAndTags(null, true,
					null, null, allTags, anyTags, null, new RestPageRequest(0, 2));
			logger.info("Found models by tag total " + allAnyTagsSearchResult.getTotalElements());
			Assert.assertNotNull(allAnyTagsSearchResult);
			Assert.assertNotEquals(0, allAnyTagsSearchResult.getNumberOfElements());
			MLPSolution taggedSol = allAnyTagsSearchResult.getContent().get(0);
			Assert.assertTrue(taggedSol.getTags().contains(new MLPTag(tagName1)));

			logger.info("Querying for solutions by catalog");
			RestPageResponse<MLPSolution> ctlgSearchResult = client.findPublishedSolutionsByKwAndTags(null, true, null,
					null, null, null, new String[] { catPub.getCatalogId() }, new RestPageRequest(0, 2));
			Assert.assertNotNull(ctlgSearchResult);
			Assert.assertNotEquals(0, ctlgSearchResult.getNumberOfElements());

			logger.info("Querying for solutions by bogus catalog");
			RestPageResponse<MLPSolution> noCtlgSearchResult = client.findPublishedSolutionsByKwAndTags(null, true,
					null, null, null, null, new String[] { "bogus" }, new RestPageRequest(0, 2));
			Assert.assertNotNull(noCtlgSearchResult);
			Assert.assertEquals(0, noCtlgSearchResult.getNumberOfElements());

			// Requires revisions and artifacts!
			Instant anHourAgo = Instant.now().minusSeconds(60 * 60);
			RestPageResponse<MLPSolution> modWithCat = client.findPublishedSolutionsByDate(
					new String[] { catPub.getCatalogId() }, anHourAgo, new RestPageRequest(0, 1));
			Assert.assertNotNull(modWithCat);
			Assert.assertNotEquals(0, modWithCat.getNumberOfElements());
			logger.info("Found published solutions by cat and date: " + modWithCat.getContent().size());
			RestPageResponse<MLPSolution> modNoCat = client.findPublishedSolutionsByDate(null, anHourAgo,
					new RestPageRequest(0, 1));
			Assert.assertNotNull(modNoCat);
			Assert.assertNotEquals(0, modNoCat.getNumberOfElements());
			logger.info("Found published solutions by modified date only: " + modNoCat.getContent().size());

			// Check this finds solutions by shared-with-user ID
			logger.info("Querying for published user solutions");
			RestPageResponse<MLPSolution> userSols = client.findUserSolutions(true, true, cu.getUserId(), null, null,
					null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(userSols);
			Assert.assertNotEquals(0, userSols.getNumberOfElements());

			logger.info("Querying for published visting user solutions");
			RestPageResponse<MLPSolution> pubVisitorSols = client.findUserSolutions(true, true,
					visitingUser.getUserId(), null, null, null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(pubVisitorSols);
			Assert.assertEquals(0, pubVisitorSols.getNumberOfElements());

			logger.info("Querying for unpub active user solutions");
			RestPageResponse<MLPSolution> unpubUserSols = client.findUserSolutions(true, false, cu.getUserId(), null,
					null, null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(unpubUserSols);
			Assert.assertNotEquals(0, unpubUserSols.getNumberOfElements());

			logger.info("Querying for unpub inactive user solutions");
			RestPageResponse<MLPSolution> unpubInactUserSols = client.findUserSolutions(true, false,
					inactiveUser.getUserId(), null, null, null, null, new RestPageRequest(0, 5));
			Assert.assertNotNull(unpubInactUserSols);
			Assert.assertEquals(0, unpubInactUserSols.getNumberOfElements());

			// find active solutions
			String[] nameKw = null;
			String[] descKw = null;
			String[] owners = { cu.getUserId() };
			String[] modelTypeCodes = null;
			String[] authKw = { "github" };
			String[] pubKw = { "publisher" };
			searchTags = null;
			RestPageResponse<MLPSolution> portalActiveMatches = client.findPortalSolutions(nameKw, descKw, true, owners,
					modelTypeCodes, searchTags, authKw, pubKw, new RestPageRequest(0, 5));
			Assert.assertNotNull(portalActiveMatches);
			Assert.assertNotEquals(0, portalActiveMatches.getNumberOfElements());

			// Create Solution Rating
			logger.info("Creating solution rating");
			MLPSolutionRating ur = new MLPSolutionRating();
			ur.setSolutionId(cs.getSolutionId());
			ur.setUserId(cu.getUserId());
			ur.setRating(4);
			ur.setTextReview("Awesome");
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
			Assert.assertNotNull(ratings);
			Assert.assertEquals(1, ratings.getNumberOfElements());
			logger.info("Solution rating count {}", ratings.getNumberOfElements());

			// check the average rating
			MLPSolution solWithRating = client.getSolution(cs.getSolutionId());
			Assert.assertNotNull(solWithRating.getRatingAverageTenths());
			Assert.assertTrue(solWithRating.getRatingAverageTenths() > 0);
			logger.info("Computed solution rating average: {}", solWithRating.getRatingAverageTenths());

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = client.createSolutionDownload(sd);
			Assert.assertNotNull(sd.getDownloadId());
			logger.info("Created solution download {}", sd);

			// Query for downloads
			RestPageResponse<MLPSolutionDownload> dnls = client.getSolutionDownloads(cs.getSolutionId(), rp);
			Assert.assertNotEquals(0, dnls.getNumberOfElements());

			// Count the downloads
			MLPSolution solWithDownloads = client.getSolution(cs.getSolutionId());
			Assert.assertNotNull(solWithDownloads);
			Long downloadCount = solWithDownloads.getDownloadCount();
			Assert.assertEquals(new Long(1), downloadCount);
			logger.info("Solution download count is {}", solWithDownloads.getDownloadCount());

			// Attempt to update the download count, which is blocked
			solWithDownloads.setDownloadCount(999999L);
			client.updateSolution(solWithDownloads);
			solWithDownloads = client.getSolution(cs.getSolutionId());
			Assert.assertEquals(downloadCount, solWithDownloads.getDownloadCount());

			// Create Solution favorite for a user
			MLPSolutionFavorite sf1 = new MLPSolutionFavorite();
			sf1.setSolutionId(cs.getSolutionId());
			sf1.setUserId(cu.getUserId());
			sf1 = client.createSolutionFavorite(sf1);
			logger.info("Created a favorite solution {}", sf1);

			// Get favorite solutions
			RestPageResponse<MLPSolution> favePage = client.getFavoriteSolutions(cu.getUserId(), rp);
			Assert.assertNotNull(favePage);
			Assert.assertNotEquals(0, favePage.getNumberOfElements());
			for (MLPSolution mlpsol : favePage)
				logger.info("Favorite Solution for user {} is {}, name {}", cu.getUserId(), mlpsol.getSolutionId(),
						mlpsol.getName());
			// Create and update solution deployment
			MLPSolutionDeployment dep = new MLPSolutionDeployment();
			dep.setDeploymentId(UUID.randomUUID().toString());
			dep.setSolutionId(cs.getSolutionId());
			dep.setRevisionId(cr.getRevisionId());
			dep.setUserId(cu.getUserId());
			dep.setDeploymentStatusCode("ST");
			dep = client.createSolutionDeployment(dep);
			Assert.assertNotNull(dep.getDeploymentId());
			logger.info("Created solution deployent {}", dep);

			// Must be valid JSON
			dep.setDetail("{ \"tag\" : \"value\" }");
			client.updateSolutionDeployment(dep);

			// Query for solution deployments
			RestPageResponse<MLPSolutionDeployment> userDeps = client.getUserDeployments(cu.getUserId(), rp);
			Assert.assertNotNull(userDeps);
			Assert.assertNotEquals(0, userDeps.getNumberOfElements());
			RestPageResponse<MLPSolutionDeployment> deps = client.getSolutionDeployments(cs.getSolutionId(),
					cr.getRevisionId(), rp);
			Assert.assertNotNull(deps);
			Assert.assertNotEquals(0, deps.getNumberOfElements());
			RestPageResponse<MLPSolutionDeployment> userSolDeps = client.getUserSolutionDeployments(cs.getSolutionId(),
					cr.getRevisionId(), cu.getUserId(), rp);
			Assert.assertNotNull(userSolDeps);
			Assert.assertNotEquals(0, userSolDeps.getNumberOfElements());

			// delete the deployment
			client.deleteSolutionDeployment(dep);

			logger.info("Querying for revisions by artifact");
			List<MLPSolutionRevision> revsByArt = client.getSolutionRevisionsForArtifact(ca.getArtifactId());
			Assert.assertNotNull(revsByArt);
			Assert.assertNotEquals(0, revsByArt.size());
			for (MLPSolutionRevision r : revs)
				logger.info("\tRevision: {}", r);

			// Composite solution support was added very late
			client.addCompositeSolutionMember(cs.getSolutionId(), csRes.getSolutionId());
			List<String> kids = client.getCompositeSolutionMembers(cs.getSolutionId());
			Assert.assertNotNull(kids);
			Assert.assertEquals(1, kids.size());
			client.dropCompositeSolutionMember(cs.getSolutionId(), csRes.getSolutionId());
			kids = client.getCompositeSolutionMembers(cs.getSolutionId());
			Assert.assertNotNull(kids);
			Assert.assertEquals(0, kids.size());

			MLPPublishRequest pubReq = new MLPPublishRequest(cs.getSolutionId(), cr.getRevisionId(), cu.getUserId(),
					catPub.getCatalogId(), "PE");
			pubReq = client.createPublishRequest(pubReq);
			Assert.assertNotNull(pubReq.getRequestId());
			pubReq.setComment("foo bar");
			client.updatePublishRequest(pubReq);

			RestPageResponse<MLPPublishRequest> reqPage = client.getPublishRequests(new RestPageRequest(0, 5));
			Assert.assertTrue(reqPage.hasContent());

			MLPPublishRequest reqFound = client.getPublishRequest(pubReq.getRequestId());
			Assert.assertNotNull(reqFound);
			Assert.assertNotNull(reqFound.getComment());
			Assert.assertTrue(client.isPublishRequestPending(cs.getSolutionId(), cr.getRevisionId()));
			logger.info("First publish request {}", reqFound);
			HashMap<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("solutionId", cs.getSolutionId());
			RestPageResponse<MLPPublishRequest> pubReqPage = client.searchPublishRequests(queryParameters, false,
					new RestPageRequest(0, 5));
			Assert.assertNotEquals(0, pubReqPage.getNumberOfElements());
			client.deletePublishRequest(pubReq.getRequestId());
			Assert.assertNull(client.getPublishRequest(pubReq.getRequestId()));

			if (cleanup) {
				// Call the many different delete methods here directly to cover the code,
				// ignore the cascade behavior for solution, revision and artifact entities.
				logger.info("Deleting newly created instances");
				client.dropRevisionCatalogDocument(cr.getRevisionId(), catPub.getCatalogId(), doc.getDocumentId());
				client.deleteDocument(doc.getDocumentId());
				client.deleteRevCatDescription(cr.getRevisionId(), catPub.getCatalogId());
				client.deleteRevCatDescription(cr.getRevisionId(), catRes.getCatalogId());
				client.dropSolutionTag(cs.getSolutionId(), tagName1);
				client.deleteTag(tag1);
				client.deleteTag(tag2);
				client.dropSolutionFromCatalog(csRes.getSolutionId(), catRes.getCatalogId());
				client.dropSolutionFromCatalog(cs.getSolutionId(), catPub.getCatalogId());
				client.dropSolutionUserAccess(cs.getSolutionId(), inactiveUser.getUserId());
				client.deleteSolutionRating(ur);
				client.deleteSolutionDownload(sd);
				client.deleteSolutionFavorite(sf1);
				client.dropSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());
				client.deleteArtifact(ca.getArtifactId());
				client.deleteSolutionRevision(cs.getSolutionId(), cr.getRevisionId());
				client.deleteSolution(cs.getSolutionId());
				client.deleteSolution(csRes.getSolutionId());
				client.deleteSolution(inactive.getSolutionId());
				client.deleteSolution(csActPriv.getSolutionId());
				client.deleteCatalog(catPub.getCatalogId());
				client.deleteCatalog(catRes.getCatalogId());
				client.deletePeerSubscription(ps.getSubId());
				client.deletePeer(pr.getPeerId());
				client.deleteUserLoginProvider(clp);
				client.deleteUser(cu.getUserId());
				client.deleteUser(visitingUser.getUserId());
				client.deleteUser(inactiveUser.getUserId());
				Assert.assertNull(client.getSolution(cs.getSolutionId()));
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
			final String loginName = "user_role_function";
			final String loginPass = "test_client_pass";
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setEmail("testrolefnuser@abc.com");
			final String firstName = "FirstRoleFn";
			cu.setFirstName(firstName);
			final String lastName = "LastRoleFn";
			cu.setLastName(lastName);
			cu.setActive(true);
			cu.setLoginPassExpire(Instant.now());
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());
			logger.info("Created user with ID {}", cu.getUserId());

			MLPRole cr = new MLPRole();
			cr.setName("test role and functions role");
			cr = client.createRole(cr);
			Assert.assertNotNull(cr.getRoleId());
			logger.info("Created role: {}", cr);

			final String roleName = "test roleFn name updated";
			cr.setName(roleName);
			client.updateRole(cr);

			MLPRole cr2 = new MLPRole();
			cr2.setName("Second role");
			cr2 = client.createRole(cr2);

			long roleCount = client.getRoleCount();
			Assert.assertNotEquals(0, roleCount);

			RestPageResponse<MLPRole> roles = client.getRoles(new RestPageRequest());
			Assert.assertNotEquals(0, roles.getNumberOfElements());

			// Empty result
			HashMap<String, Object> roleRestr = new HashMap<>();
			roleRestr.put("name", "never~ever~match~role");
			RestPageResponse<MLPRole> emptyRoleResult = client.searchRoles(roleRestr, false, new RestPageRequest());
			Assert.assertEquals(0, emptyRoleResult.getNumberOfElements());

			// Nonempty result
			roleRestr.put("name", roleName);
			RestPageResponse<MLPRole> roleResult = client.searchRoles(roleRestr, false, new RestPageRequest());
			Assert.assertNotEquals(0, roleResult.getNumberOfElements());

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
			Assert.assertNotNull(fetchedRoleFns);
			Assert.assertNotEquals(0, fetchedRoleFns.size());
			MLPRoleFunction roleFnFromList = fetchedRoleFns.get(0);
			logger.info("First role function in list {}", roleFnFromList);

			MLPRoleFunction roleFn = client.getRoleFunction(cr.getRoleId(), roleFnFromList.getRoleFunctionId());
			logger.info("Single role function {}", roleFn);
			Assert.assertNotNull(roleFn);

			logger.info("Adding role 1 for user");
			client.addUserRole(cu.getUserId(), cr.getRoleId());
			List<MLPRole> addedRoles = client.getUserRoles(cu.getUserId());
			Assert.assertNotNull(addedRoles);
			Assert.assertEquals(1, addedRoles.size());

			logger.info("Adding role 2 for user");
			List<String> userIds = new ArrayList<>();
			userIds.add(cu.getUserId());
			client.addUsersInRole(userIds, cr2.getRoleId());

			long role2count = client.getRoleUsersCount(cr2.getRoleId());
			Assert.assertEquals(1, role2count);
			logger.info("Count of users in role 2: {}", role2count);

			RestPageResponse<MLPUser> role2Users = client.getRoleUsers(cr2.getRoleId(), new RestPageRequest());
			Assert.assertEquals(1, role2Users.getNumberOfElements());
			logger.info("Size of user list in role 2: {}", role2Users.getNumberOfElements());

			addedRoles = client.getUserRoles(cu.getUserId());
			Assert.assertNotNull(addedRoles);
			Assert.assertEquals(2, addedRoles.size());
			logger.info("Count of roles for user: {}", addedRoles.size());

			logger.info("Dropping role 1 for user");
			client.dropUserRole(cu.getUserId(), cr.getRoleId());

			logger.info("Dropping role 2 for user");
			client.dropUsersInRole(userIds, cr2.getRoleId());

			List<MLPRole> revisedUserRoles = client.getUserRoles(cu.getUserId());
			Assert.assertNotNull(revisedUserRoles);
			Assert.assertTrue(revisedUserRoles.isEmpty());
			logger.info("User role count is zero");

			List<String> roleIds = new ArrayList<>();
			roleIds.add(cr.getRoleId());
			roleIds.add(cr2.getRoleId());
			client.updateUserRoles(cu.getUserId(), roleIds);
			revisedUserRoles = client.getUserRoles(cu.getUserId());
			Assert.assertNotNull(revisedUserRoles);
			Assert.assertEquals(2, revisedUserRoles.size());
			logger.info("User role count is back at 2");

			client.dropUserRole(cu.getUserId(), cr.getRoleId());
			client.dropUserRole(cu.getUserId(), cr2.getRoleId());
			revisedUserRoles = client.getUserRoles(cu.getUserId());
			Assert.assertNotNull(revisedUserRoles);
			Assert.assertEquals(0, revisedUserRoles.size());
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
			cu.setLoginName("notifuser");
			cu.setEmail("testnotifuser@abc.com");
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPNotification no = new MLPNotification();
			no.setTitle("notif1 title");
			no.setMessage("notif msg");
			no.setUrl("http://notify1.me");
			no.setMsgSeverityCode("LO");
			// An hour ago
			no.setStart(Instant.now().minusSeconds(61 * 60));
			// An hour from now
			no.setEnd(Instant.now().plusSeconds(61 * 60));
			no = client.createNotification(no);
			Assert.assertNotNull(no.getNotificationId());

			no.setMessage("New enhanced message");
			client.updateNotification(no);

			MLPNotification no2 = new MLPNotification();
			no2.setTitle("notif2 title");
			no2.setMessage("notif2 msg");
			no2.setUrl("http://notify2.me");
			no2.setMsgSeverityCode(String.valueOf("HI"));
			// 5 minutes ago
			no2.setStart(Instant.now().minusSeconds(5 * 60));
			// 5 minutes from now
			no2.setEnd(Instant.now().plusSeconds(5 * 60));
			no2 = client.createNotification(no2);

			// A populated database may yield a large number
			long notCountTrans = client.getNotificationCount();
			Assert.assertNotEquals(0, notCountTrans);

			int limit = 100;
			RestPageResponse<MLPNotification> notifics = client.getNotifications(new RestPageRequest(0, limit));
			Assert.assertNotEquals(0, notifics.getNumberOfElements());
			Assert.assertEquals(notCountTrans > limit ? limit : notCountTrans, notifics.getNumberOfElements());

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
			client.deleteNotification(no2.getNotificationId());
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
			cu.setLoginName("user_notif_pref");
			cu.setEmail("testusrnotifprefuser.com");
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());

			usrNotifPref.setUserId(cu.getUserId());
			usrNotifPref.setNotfDelvMechCode("TX");
			usrNotifPref.setMsgSeverityCode("HI");

			usrNotifPref = client.createUserNotificationPreference(usrNotifPref);
			Assert.assertNotNull(usrNotifPref.getUserNotifPrefId());

			usrNotifPref.setNotfDelvMechCode("EM");
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
		Assert.assertTrue(client.getUserNotificationPreferences("bogusUser").isEmpty());
		Assert.assertNull(client.getUserNotificationPreference(99999L));
		try {
			client.createUserNotificationPreference(new MLPUserNotifPref());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create user notification preference failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createUserNotificationPreference(new MLPUserNotifPref(cu.getUserId(), "bogus", "HI"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create user notification preference failed on bad deliv code as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createUserNotificationPreference(new MLPUserNotifPref(cu.getUserId(), "EM", "bogus"));
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
	public void testTaskStepResult() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 110; i += 10)
			sb.append("1234567890");
		String longName = sb.toString();
		MLPUser cu = new MLPUser("user_task_step", "test@user.task.step.com", true);
		MLPTask ct = null;
		MLPTaskStepResult sr1 = null, sr2 = null;
		try {
			cu = client.createUser(cu);
			Assert.assertNotNull(cu.getUserId());

			final String taskName = "DoWorkQuickly";
			ct = new MLPTask("OB", taskName, cu.getUserId(), "SU");
			ct = client.createTask(ct);
			Assert.assertNotNull(ct.getTaskId());

			ct.setTrackingId("foo");
			client.updateTask(ct);

			Assert.assertNotNull(client.getTask(ct.getTaskId()));

			RestPageResponse<MLPTask> tasks = client.getTasks(new RestPageRequest(0, 5));
			Assert.assertEquals(1, tasks.getNumberOfElements());

			HashMap<String, Object> taskParms = new HashMap<>();
			taskParms.put("trackingId", "~bogus~never~match");
			RestPageResponse<MLPTask> taskSearchResults = client.searchTasks(taskParms, false,
					new RestPageRequest(0, 10));
			Assert.assertEquals(0, taskSearchResults.getNumberOfElements());

			taskParms.clear();
			taskParms.put("name", taskName);
			taskSearchResults = client.searchTasks(taskParms, true, new RestPageRequest(0, 10));
			Assert.assertEquals(1, taskSearchResults.getNumberOfElements());

			final String stepName = "Collusion creation";
			sr1 = new MLPTaskStepResult(ct.getTaskId(), stepName, "SU", Instant.now().minusSeconds(60));
			sr1 = client.createTaskStepResult(sr1);
			Assert.assertNotNull(sr1.getStepResultId());
			logger.info("Created step result " + sr1);

			sr2 = client.createTaskStepResult(
					new MLPTaskStepResult(ct.getTaskId(), "second", "FA", Instant.now().minusSeconds(60)));
			Assert.assertNotNull(sr2.getStepResultId());
			logger.info("Created step result " + sr2);

			sr1.setResult("Some new stack trace result");
			client.updateTaskStepResult(sr1);

			MLPTaskStepResult getById = client.getTaskStepResult(sr1.getStepResultId());
			Assert.assertNotNull(getById.getStepResultId());

			List<MLPTaskStepResult> stepResults = client.getTaskStepResults(sr1.getTaskId());
			Assert.assertTrue(stepResults.iterator().hasNext());
			logger.info("First step result {}", stepResults.iterator().next());

			HashMap<String, Object> stepResParms = new HashMap<>();
			stepResParms.put("stepCode", "~bogus~never~match");
			stepResParms.put("solutionId", "~bogus~never~match");
			stepResParms.put("revisionId", "~bogus~never~match");
			stepResParms.put("artifactId", "~bogus~never~match");
			stepResParms.put("userId", "~bogus~never~match");
			stepResParms.put("statusCode", "~bogus~never~match");
			stepResParms.put("name", "~bogus~never~match");
			RestPageResponse<MLPTaskStepResult> emptySearchResults = client.searchTaskStepResults(stepResParms, false,
					new RestPageRequest(0, 10));
			Assert.assertEquals(0, emptySearchResults.getNumberOfElements());

			stepResParms.put("name", stepName);
			RestPageResponse<MLPTaskStepResult> searchResults = client.searchTaskStepResults(stepResParms, true,
					new RestPageRequest(0, 10));
			Assert.assertEquals(1, searchResults.getNumberOfElements());

		} catch (HttpStatusCodeException ex) {
			logger.error("testStepResults failed with response {}", ex.getResponseBodyAsString());
			throw ex;
		}

		// invalid tests

		Assert.assertNull(client.getTask(9999L));
		Assert.assertNull(client.getTaskStepResult(9999L));

		try {
			client.createTask(new MLPTask("OB", sb.toString(), cu.getUserId(), "SU"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create task on long name failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			ct.setName(longName);
			client.updateTask(ct);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update task on long name failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.searchTasks(new HashMap<>(), true, new RestPageRequest(0, 10));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search tasks on empty failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("bogus", "value");
			client.searchTasks(new HashMap<>(), true, new RestPageRequest(0, 10));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search asksfield failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPTask task = new MLPTask("OB", "name", cu.getUserId(), "SU");
			task.setTaskId(999L);
			client.updateTask(task);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update task failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteTask(999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete task failed as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			sr1.setName(longName);
			client.updateTaskStepResult(sr1);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update task on long name failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.searchTaskStepResults(new HashMap<>(), true, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search step result empty failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("bogus", "value");
			client.searchTaskStepResults(restr, true, new RestPageRequest(0, 1));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search step result bad field failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createTaskStepResult(new MLPTaskStepResult(0L, longName, "IP", Instant.now()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create step result on long name failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createTaskStepResult(new MLPTaskStepResult(0L, "bog", "us", Instant.now()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create step result failed on missing task as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createTaskStepResult(new MLPTaskStepResult(ct.getTaskId(), "name", "XY", Instant.now()));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create step result failed on bad status code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPTaskStepResult stepResult = new MLPTaskStepResult(0L, "bog", "us", Instant.now());
			stepResult.setStepResultId(999L);
			client.updateTaskStepResult(stepResult);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update step result failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteTaskStepResult(999L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete step result failed as expected: {}", ex.getResponseBodyAsString());
		}

		// delete #1 directly
		client.deleteTaskStepResult(sr1.getStepResultId());
		// this should cascade delete to #2
		client.deleteTask(ct.getTaskId());
		Assert.assertNull(client.getTaskStepResult(sr2.getTaskId()));
		client.deleteUser(cu.getUserId());
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
		RestPageResponse<MLPSiteConfig> configs = client.getSiteConfigs(new RestPageRequest(0, 10));
		Assert.assertNotEquals(0, configs.getTotalElements());

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
		RestPageResponse<MLPSiteContent> contents = client.getSiteContents(new RestPageRequest(0, 10));
		Assert.assertNotEquals(0, contents.getTotalElements());

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

		MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0", cu.getUserId());
		cr.setVerifiedLicense("FA");
		cr.setVerifiedVulnerability("FA");
		cr = client.createSolutionRevision(cr);
		Assert.assertNotNull(cr.getRevisionId());

		MLPThread thread = client.createThread(new MLPThread(cs.getSolutionId(), cr.getRevisionId()));
		Assert.assertNotNull(thread);
		Assert.assertNotNull(thread.getThreadId());
		RestPageResponse<MLPThread> threads = client.getThreads(new RestPageRequest(0, 1));
		Assert.assertNotNull(threads);
		Assert.assertNotEquals(0, threads.getNumberOfElements());

		MLPThread retrieved = client.getThread(thread.getThreadId());
		Assert.assertNotNull(retrieved);

		long threadCountById = client.getSolutionRevisionThreadCount(cs.getSolutionId(), cr.getRevisionId());
		Assert.assertNotEquals(0, threadCountById);
		RestPageResponse<MLPThread> threadsById = client.getSolutionRevisionThreads(cs.getSolutionId(),
				cr.getRevisionId(), new RestPageRequest(0, 1));
		Assert.assertNotNull(threadsById);
		Assert.assertNotEquals(0, threadsById.getNumberOfElements());

		long threadCount = client.getThreadCount();
		Assert.assertNotEquals(0, threadCount);

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
		Assert.assertNull(client.getThread("bogus"));
		MLPThread bogus = new MLPThread();
		bogus.setThreadId("bogus");
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
		Assert.assertNotNull(parent);
		Assert.assertNotNull(parent.getCommentId());
		logger.info("Created parent comment: " + parent.toString());

		parent = client.getComment(thread.getThreadId(), parent.getCommentId());
		Assert.assertNotNull(parent);

		MLPComment reply = new MLPComment(thread.getThreadId(), cu.getUserId(), "child text");
		reply.setParentId(parent.getCommentId());
		reply = client.createComment(reply);
		Assert.assertNotNull(reply);
		Assert.assertNotNull(reply.getCommentId());
		logger.info("Created reply comment: " + reply.toString());

		reply.setText(s64);
		client.updateComment(reply);

		long commentCount = client.getThreadCommentCount(thread.getThreadId());
		Assert.assertNotEquals(0, commentCount);

		RestPageResponse<MLPComment> threadComments = client.getThreadComments(thread.getThreadId(),
				new RestPageRequest(0, 1));
		Assert.assertNotNull(threadComments);
		Assert.assertTrue(threadComments.hasContent());

		long commentCountById = client.getSolutionRevisionCommentCount(cs.getSolutionId(), cr.getRevisionId());
		Assert.assertNotEquals(0, commentCountById);

		RestPageResponse<MLPComment> commentsById = client.getSolutionRevisionComments(cs.getSolutionId(),
				cr.getRevisionId(), new RestPageRequest(0, 1));
		Assert.assertNotNull(commentsById);
		Assert.assertNotEquals(0, commentsById.getNumberOfElements());

		Assert.assertNull(client.getComment("bogus", "bogus"));
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
			MLPComment fakeComment = new MLPComment(thread.getThreadId(), "bogus", "text");
			fakeComment.setCommentId("bogus");
			client.updateComment(fakeComment);
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
	public void testCatalogs() throws Exception {

		MLPUser cu;
		MLPPeer pr;
		MLPRole cr1;
		MLPRole cr2;
		MLPSolution cs;
		MLPCatalog pubCat;
		MLPCatalog resCat;

		try {
			cu = new MLPUser();
			cu.setEmail("testcatalog@abc.com");
			cu.setActive(true);
			cu.setLoginName("cataloguser");
			cu = client.createUser(cu);
			Assert.assertNotNull("User ID", cu.getUserId());
			logger.info("Created user {}", cu);

			cr1 = new MLPRole();
			cr1.setName("test catalogs role 1");
			cr1 = client.createRole(cr1);
			Assert.assertNotNull(cr1.getRoleId());
			logger.info("Created role: {}", cr1);

			cr2 = new MLPRole();
			cr2.setName("test cats role 2");
			cr2 = client.createRole(cr2);
			Assert.assertNotNull(cr2.getRoleId());
			logger.info("Created role: {}", cr2);

			client.addUserRole(cu.getUserId(), cr1.getRoleId());
			List<MLPRole> addedRoles = client.getUserRoles(cu.getUserId());
			Assert.assertEquals(1, addedRoles.size());

			pr = new MLPPeer("Peer-Name-Test-Cat", "cat.fqdn.subject.name.a.b.c", "http://peer-api", true, false,
					"contact", "AC");
			pr = client.createPeer(pr);
			Assert.assertNotNull("Peer ID", pr.getPeerId());
			logger.info("Created peer {}", pr);

			cs = new MLPSolution("solutionName 1 for cat", cu.getUserId(), true);
			cs = client.createSolution(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			logger.info("Created solution {}", cs);

			pubCat = client.createCatalog(new MLPCatalog("PB", true, "public for cat test", "publ", "http://pub.org"));
			Assert.assertNotNull("Catalog ID", pubCat.getCatalogId());
			logger.info("Created catalog {}", pubCat);
			pubCat.setDescription("Catalog description");
			client.updateCatalog(pubCat);

			resCat = client.createCatalog(
					new MLPCatalog("RS", false, "restr for cat test", "them", "http://restr.acumos.org"));
			Assert.assertNotNull("Catalog ID", resCat.getCatalogId());
			logger.info("Created catalog {}", resCat);

			RestPageResponse<MLPCatalog> catalogs = client.getCatalogs(new RestPageRequest(0, 2, "name"));
			Assert.assertNotNull(catalogs);
			Assert.assertNotEquals(0, catalogs.getNumberOfElements());

			List<String> catPubs = client.getCatalogPublishers();
			Assert.assertFalse(catPubs.isEmpty());

			HashMap<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("name", pubCat.getName());
			RestPageResponse<MLPCatalog> searchCatalogs = client.searchCatalogs(queryParameters, true,
					new RestPageRequest(0, 2, "name"));
			Assert.assertEquals(1, searchCatalogs.getNumberOfElements());

			MLPCatalog c2 = client.getCatalog(pubCat.getCatalogId());
			Assert.assertEquals(pubCat, c2);

			Assert.assertTrue(client.isPeerAccessToCatalog(pr.getPeerId(), pubCat.getCatalogId()));
			Assert.assertFalse(client.isPeerAccessToCatalog(pr.getPeerId(), resCat.getCatalogId()));
			Assert.assertFalse(client.isPeerAccessToSolution(pr.getPeerId(), cs.getSolutionId()));

			client.addSolutionToCatalog(cs.getSolutionId(), pubCat.getCatalogId());
			Assert.assertTrue(client.isPeerAccessToSolution(pr.getPeerId(), cs.getSolutionId()));

			RestPageResponse<MLPSolution> sols = client.getSolutionsInCatalogs(new String[] { pubCat.getCatalogId() },
					new RestPageRequest());
			Assert.assertNotNull(sols);
			Assert.assertEquals(1, sols.getNumberOfElements());

			List<MLPCatalog> cats = client.getSolutionCatalogs(cs.getSolutionId());
			Assert.assertNotNull(cats);
			Assert.assertEquals(1, cats.size());

			client.addPeerAccessCatalog(pr.getPeerId(), resCat.getCatalogId());
			List<String> peerAcc = client.getPeerAccessCatalogIds(pr.getPeerId());
			Assert.assertEquals(1, peerAcc.size());
			List<MLPPeer> accessPeers = client.getCatalogAccessPeers(resCat.getCatalogId());
			Assert.assertEquals(1, accessPeers.size());

			client.addUserFavoriteCatalog(cu.getUserId(), pubCat.getCatalogId());
			List<String> userFavs = client.getUserFavoriteCatalogIds(cu.getUserId());
			Assert.assertEquals(1, userFavs.size());

			List<MLPRole> catRoles = client.getCatalogRoles(resCat.getCatalogId());
			Assert.assertTrue(catRoles.isEmpty());

			logger.info("Adding role 1 for restr catalog");
			client.addCatalogRole(resCat.getCatalogId(), cr1.getRoleId());
			catRoles = client.getCatalogRoles(resCat.getCatalogId());
			Assert.assertTrue(catRoles.contains(cr1));

			logger.info("Adding role 2 for restr catalog");
			List<String> ids = new ArrayList<>();
			ids.add(resCat.getCatalogId());
			client.addCatalogsInRole(ids, cr2.getRoleId());
			catRoles = client.getCatalogRoles(resCat.getCatalogId());
			Assert.assertTrue(catRoles.contains(cr2));

			logger.info("Dropping role 2 for restr catalog");
			client.dropCatalogsInRole(ids, cr2.getRoleId());
			catRoles = client.getCatalogRoles(resCat.getCatalogId());
			Assert.assertFalse(catRoles.contains(cr2));

			long roleCatCount = client.getRoleCatalogsCount(cr1.getRoleId());
			Assert.assertEquals(1L, roleCatCount);

			RestPageResponse<MLPCatalog> roleCats = client.getRoleCatalogs(cr1.getRoleId(), new RestPageRequest(0, 2));
			Assert.assertTrue(roleCats.getContent().contains(resCat));

			logger.info("Updating restr catalog role");
			List<String> updRoles = new ArrayList<>();
			updRoles.add(cr1.getRoleId());
			updRoles.add(cr2.getRoleId());
			client.updateCatalogRoles(resCat.getCatalogId(), updRoles);
			catRoles = client.getCatalogRoles(resCat.getCatalogId());
			Assert.assertTrue(catRoles.contains(cr1));
			Assert.assertTrue(catRoles.contains(cr2));

			// MariaDB has several accessible catalogs, not just the two created here
			RestPageResponse<MLPCatalog> accessibleCats = client.getUserAccessCatalogs(cu.getUserId(),
					new RestPageRequest(0, 5));
			logger.info("User acc catalog count {}", accessibleCats.getContent().size());
			Assert.assertTrue(accessibleCats.getContent().contains(pubCat));
			Assert.assertTrue(accessibleCats.getContent().contains(resCat));
		} catch (HttpStatusCodeException ex) {
			logger.error("testCatalogs failed {}", ex.getResponseBodyAsString());
			throw ex;
		}

		try {
			MLPCatalog c = new MLPCatalog("PB", false, "name", "someone", "http://pub.org");
			c.setCatalogId("bogus");
			client.createCatalog(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create catalog failed on bad ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPCatalog c = new MLPCatalog("PB", false, "name", "IDK", "http://pub.org");
			c.setCatalogId(pubCat.getCatalogId());
			client.createCatalog(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create catalog failed on existing ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPCatalog c = new MLPCatalog("xx", false, "name", "who's on first", "http://pub.org");
			client.createCatalog(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create catalog failed on bad access code as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPCatalog c = new MLPCatalog("PB", false, "name", "other", "/inval:d@-*url");
			client.createCatalog(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create catalog failed on bad URL as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPCatalog c = new MLPCatalog("PB", false, "name", "bogus", "http://pub.org");
			c.setCatalogId("bogus");
			client.updateCatalog(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update missing catalog failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPCatalog c = new MLPCatalog(pubCat);
			c.setAccessTypeCode("bogus");
			client.updateCatalog(c);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update catalog failed on bad acc type as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionToCatalog("bogus", pubCat.getCatalogId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add solution to catalog failed on bad sol id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addSolutionToCatalog(cs.getSolutionId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add solution to catalog failed on bad cat id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addPeerAccessCatalog("bogus", pubCat.getCatalogId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add peer access catalog failed on bad peer id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addPeerAccessCatalog(pr.getPeerId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add peer access catalog failed on bad cat id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropPeerAccessCatalog("bogus", pubCat.getCatalogId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("drop peer access catalog failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addUserFavoriteCatalog("bogus", pubCat.getCatalogId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add user fave catalog failed on bad user id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addUserFavoriteCatalog(cu.getUserId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add user fave catalog failed on bad cat id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropUserFavoriteCatalog("bogus", pubCat.getCatalogId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("drop user fave catalog failed on bad user id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addCatalogRole("BOGUS", cr1.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add catalog role failed on bad cat ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addCatalogRole(pubCat.getCatalogId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add catalog role failed on bad role as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.updateCatalogRoles("bogus", new ArrayList<String>());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("drop catalog role failed on bad cat id as expected: {}", ex.getResponseBodyAsString());
		}

		client.dropPeerAccessCatalog(pr.getPeerId(), resCat.getCatalogId());
		Assert.assertEquals(0, client.getPeerAccessCatalogIds(cu.getUserId()).size());
		client.dropUserFavoriteCatalog(cu.getUserId(), pubCat.getCatalogId());
		Assert.assertEquals(0, client.getUserFavoriteCatalogIds(cu.getUserId()).size());
		client.dropSolutionFromCatalog(cs.getSolutionId(), pubCat.getCatalogId());
		Assert.assertEquals(0,
				client.getSolutionsInCatalogs(new String[] { pubCat.getCatalogId() }, new RestPageRequest())
						.getNumberOfElements());
		client.dropCatalogRole(resCat.getCatalogId(), cr2.getRoleId());
		client.dropCatalogRole(resCat.getCatalogId(), cr1.getRoleId());
		client.deleteCatalog(resCat.getCatalogId());
		client.deleteCatalog(pubCat.getCatalogId());
		client.deleteSolution(cs.getSolutionId());
		client.deletePeer(pr.getPeerId());
		client.deleteUser(cu.getUserId());
		client.deleteRole(cr2.getRoleId());
		client.deleteRole(cr1.getRoleId());
	}

	@Test
	public void testWorkbenchArtifacts() throws Exception {
		MLPUser cu;
		MLPProject cpr;
		MLPNotebook cnb;
		MLPPipeline cpl;

		try {
			// Need a user to create anything
			cu = null;
			cu = new MLPUser();
			cu.setEmail("testWorkbenchArtifacts@abc.com");
			cu.setActive(true);
			cu.setLoginName("workbenchuser");
			cu = client.createUser(cu);
			Assert.assertNotNull("User ID", cu.getUserId());
			logger.info("Created user {}", cu);

			HashMap<String, Object> userIdRestr = new HashMap<>();
			userIdRestr.put("userId", cu.getUserId());

			final String projName = "proj name 1";
			cpr = new MLPProject(projName, cu.getUserId(), "ver");
			cpr.setServiceStatusCode("AC");
			cpr.setRepositoryUrl("http://repo.url.io:12345");
			cpr = client.createProject(cpr);
			Assert.assertNotNull("Project ID", cpr.getProjectId());
			logger.info("Created project {}", cpr);
			cpr.setDescription("desc");
			client.updateProject(cpr);
			MLPProject updCpr = client.getProject(cpr.getProjectId());
			Assert.assertNotNull(updCpr.getDescription());
			RestPageResponse<MLPProject> projects = client.getProjects(new RestPageRequest(0, 5));
			Assert.assertNotEquals(0, projects.getNumberOfElements());
			RestPageResponse<MLPProject> srchProj = client.searchProjects(userIdRestr, false,
					new RestPageRequest(0, 5));
			Assert.assertEquals(1, srchProj.getNumberOfElements());

			cnb = new MLPNotebook("nb name 1", cu.getUserId(), "ver", "JP");
			cnb.setServiceStatusCode("AC");
			cnb.setRepositoryUrl("http://repo.url.io:12345");
			cnb.setServiceUrl("http://service.url.io:54321");
			cnb = client.createNotebook(cnb);
			Assert.assertNotNull("Notebook ID", cnb.getNotebookId());
			logger.info("Created notebook {}", cnb);
			cnb.setDescription("desc");
			client.updateNotebook(cnb);
			MLPNotebook updCnb = client.getNotebook(cnb.getNotebookId());
			Assert.assertNotNull(updCnb.getDescription());
			RestPageResponse<MLPNotebook> notebooks = client.getNotebooks(new RestPageRequest(0, 5));
			Assert.assertNotEquals(0, notebooks.getNumberOfElements());
			RestPageResponse<MLPNotebook> srchNb = client.searchNotebooks(userIdRestr, false,
					new RestPageRequest(0, 5));
			Assert.assertEquals(1, srchNb.getNumberOfElements());

			cpl = new MLPPipeline("pl name 1", cu.getUserId(), "vers");
			cpl.setServiceStatusCode("AC");
			cpl.setRepositoryUrl("http://repo.url.io:12345");
			cpl.setServiceUrl("http://service.url.io:54321");
			cpl = client.createPipeline(cpl);
			Assert.assertNotNull("Pipeline ID", cpl.getPipelineId());
			logger.info("Created pipeline {}", cpl);
			cpl.setDescription("desc");
			client.updatePipeline(cpl);
			MLPPipeline updCpl = client.getPipeline(cpl.getPipelineId());
			Assert.assertNotNull(updCpl.getDescription());
			RestPageResponse<MLPPipeline> pipelines = client.getPipelines(new RestPageRequest(0, 5));
			Assert.assertNotEquals(0, pipelines.getNumberOfElements());
			RestPageResponse<MLPPipeline> srchPl = client.searchPipelines(userIdRestr, false,
					new RestPageRequest(0, 5));
			Assert.assertEquals(1, srchPl.getNumberOfElements());

			client.addProjectNotebook(cpr.getProjectId(), cnb.getNotebookId());
			List<MLPNotebook> nbList = client.getProjectNotebooks(cpr.getProjectId());
			Assert.assertFalse(nbList.isEmpty());
			List<MLPProject> nbProjList = client.getNotebookProjects(cnb.getNotebookId());
			Assert.assertFalse(nbProjList.isEmpty());

			client.addProjectPipeline(cpr.getProjectId(), cpl.getPipelineId());
			List<MLPPipeline> plList = client.getProjectPipelines(cpr.getProjectId());
			Assert.assertFalse(plList.isEmpty());
			List<MLPProject> plProjList = client.getPipelineProjects(cpl.getPipelineId());
			Assert.assertFalse(plProjList.isEmpty());

		} catch (HttpStatusCodeException ex) {
			logger.error("testWorkbenchArtifacts failed", ex.getResponseBodyAsString());
			throw ex;
		}

		try {
			client.createProject(cpr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on existing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createProject(new MLPProject());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createProject(new MLPProject("name", "foo", "extra super long version should blow up"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on long ver as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPProject badIdUpdate = new MLPProject(cpr);
			badIdUpdate.setProjectId("999L");
			client.updateProject(badIdUpdate);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPProject constrVioUpdate = new MLPProject(cpr);
			constrVioUpdate.setVersion("super long string to hit the size constraint");
			client.updateProject(constrVioUpdate);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on bad ver as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteProject("999");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createNotebook(cnb);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on existing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createNotebook(new MLPNotebook());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createNotebook(new MLPNotebook("name", "foo", "extra super long version should blow up", "JP"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on long ver as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPNotebook badIdUpdate = new MLPNotebook(cnb);
			badIdUpdate.setNotebookId("999L");
			client.updateNotebook(badIdUpdate);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPNotebook constrVioUpdate = new MLPNotebook(cnb);
			constrVioUpdate.setVersion("super long string to hit the size constraint");
			client.updateNotebook(constrVioUpdate);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on bad ver as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteNotebook("999");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPipeline(cpl);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on existing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPipeline(new MLPPipeline());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createPipeline(new MLPPipeline("name", "foo", "extra super long version should blow up"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on long ver as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPPipeline bogusUpdate = new MLPPipeline(cpl);
			bogusUpdate.setPipelineId("999L");
			client.updatePipeline(bogusUpdate);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPPipeline constrVioUpdate = new MLPPipeline(cpl);
			constrVioUpdate.setVersion("super long string to hit the size constraint");
			client.updatePipeline(constrVioUpdate);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on bad ver as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deletePipeline("999");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addProjectNotebook("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add proj nb failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addProjectNotebook(cpr.getProjectId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info(" failed  as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropProjectNotebook("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("drop proj nb failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addProjectPipeline("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("add proj pl failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.addProjectPipeline(cpr.getProjectId(), "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info(" failed  as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.dropProjectPipeline("bogus", "bogus");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("drop proj pl failed on bad id as expected: {}", ex.getResponseBodyAsString());
		}
		HashMap<String, Object> emptyRestr = new HashMap<>();
		emptyRestr.put("unknown", "key");
		try {
			client.searchProjects(emptyRestr, false, new RestPageRequest(0, 5));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search proj failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.searchNotebooks(emptyRestr, false, new RestPageRequest(0, 5));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search nb failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.searchPipelines(emptyRestr, false, new RestPageRequest(0, 5));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("search pl failed on empty as expected: {}", ex.getResponseBodyAsString());
		}

		client.dropProjectPipeline(cpr.getProjectId(), cpl.getPipelineId());
		List<MLPPipeline> plList = client.getProjectPipelines(cpr.getProjectId());
		Assert.assertTrue(plList.isEmpty());

		client.dropProjectNotebook(cpr.getProjectId(), cnb.getNotebookId());
		List<MLPNotebook> nbList = client.getProjectNotebooks(cpr.getProjectId());
		Assert.assertTrue(nbList.isEmpty());

		client.deletePipeline(cpl.getPipelineId());
		Assert.assertNull(client.getPipeline(cpl.getPipelineId()));
		client.deleteNotebook(cnb.getNotebookId());
		Assert.assertNull(client.getNotebook(cnb.getNotebookId()));
		client.deleteProject(cpr.getProjectId());
		Assert.assertNull(client.getProject(cpr.getProjectId()));
		client.deleteUser(cu.getUserId());
	}

	@Test
	public void testLicenseArtifacts() throws Exception {
		MLPUser cu;
		MLPLicenseProfileTemplate templ;
		try {
			// Need a user to create anything
			cu = null;
			cu = new MLPUser();
			cu.setEmail("testLicenseArtifacts@abc.com");
			cu.setActive(true);
			cu.setLoginName("licuser");
			cu = client.createUser(cu);
			Assert.assertNotNull("User ID", cu.getUserId());
			logger.info("Created user {}", cu);
			RestPageResponse<MLPLicenseProfileTemplate> templs = client
					.getLicenseProfileTemplates(new RestPageRequest(0, 5));
			templ = new MLPLicenseProfileTemplate("lic name", " { \"foo\":\"bar\" }", 1, cu.getUserId());
			templ = client.createLicenseProfileTemplate(templ);
			Assert.assertNotNull(templ.getTemplateId());
			templs = client.getLicenseProfileTemplates(new RestPageRequest(0, 5));
			Assert.assertFalse(templs.isEmpty());
			long prio = 7;
			templ.setPriority(prio);
			client.updateLicenseProfileTemplate(templ);
			templ = client.getLicenseProfileTemplate(templ.getTemplateId());
			Assert.assertEquals(prio, templ.getPriority());
			templs = client.getLicenseProfileTemplates(new RestPageRequest(0, 5));
			Assert.assertNotEquals(0, templs.getNumberOfElements());
		} catch (HttpStatusCodeException ex) {
			logger.error("testLicenseArtifacts failed", ex.getResponseBodyAsString());
			throw ex;
		}

		try {
			client.createLicenseProfileTemplate(new MLPLicenseProfileTemplate());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on empty as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.createLicenseProfileTemplate(
					new MLPLicenseProfileTemplate("name", " { \"tag\" : \"value\" } ", 1, "bogusUserId"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("create failed on missing user as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPLicenseProfileTemplate t = new MLPLicenseProfileTemplate("name", " { \"tag\" : \"value\" } ", 1,
					"bogusUserId");
			t.setTemplateId(999L);
			client.updateLicenseProfileTemplate(t);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("update failed on missing as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			client.deleteLicenseProfileTemplate(0L);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("delete failed on missing as expected: {}", ex.getResponseBodyAsString());
		}

		client.deleteLicenseProfileTemplate(templ.getTemplateId());
		Assert.assertNull(client.getLicenseProfileTemplate(templ.getTemplateId()));
		client.deleteUser(cu.getUserId());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testErrorConditions() throws Exception {

		MLPUser cu = new MLPUser();
		cu.setUserId(UUID.randomUUID().toString());
		Assert.assertNull(client.getUser(cu.getUserId()));
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
		final String loginName = "user-error-condition";
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

		String roleNm = "role-error-condition";
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
		Assert.assertNull(client.getRole(crCustomId.getRoleId()));
		try {
			client.deleteRole(crCustomId.getRoleId());
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete missing role failed as expected: {}", ex.getResponseBodyAsString());
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

		Assert.assertNull(client.getUserLoginProvider("bogusUser", "bogusCode", "bogusLogin"));
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
		MLPUserLoginProvider clp = new MLPUserLoginProvider(cu.getUserId(), "GH", "something", "access token", 1);
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
		Assert.assertNull(client.getSolution(cs.getSolutionId()));
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
		Assert.assertNull(client.getSolutionRevision(cs.getSolutionId(), csr.getRevisionId()));
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
			csr = new MLPSolutionRevision(cs.getSolutionId(), s64, cu.getUserId());
			client.createSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			csr = new MLPSolutionRevision(cs.getSolutionId(), "v1", cu.getUserId());
			csr.setVerifiedLicense("bogus");
			client.createSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision failed on enum as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			csr = new MLPSolutionRevision(cs.getSolutionId(), "v1", cu.getUserId());
			csr.setVerifiedVulnerability("bogus");
			client.createSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision failed on enum as expected: {}", ex.getResponseBodyAsString());
		}
		// This one is supposed to work
		final String solRevVersion = "1.0R";
		csr = new MLPSolutionRevision(cs.getSolutionId(), solRevVersion, cu.getUserId());
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
			// undo
			csr.setVersion("v1");
			logger.info("Update solution revision failed on constraints as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			csr.setVerifiedLicense("bogus");
			client.updateSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			// undo
			csr.setVerifiedLicense(null);
			logger.info("Update solution revision failed on enum as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			csr.setVerifiedVulnerability("bogus");
			client.updateSolutionRevision(csr);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			// undo
			csr.setVerifiedVulnerability(null);
			logger.info("Update solution revision failed on enum as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionRevision r = new MLPSolutionRevision(cs.getSolutionId(), "version", "userId");
			r.setRevisionId("bogus");
			client.updateSolutionRevision(r);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update solution revision failed on bad rev as expected: {}", ex.getResponseBodyAsString());
		}
		// Restore valid value
		csr.setVersion(solRevVersion);

		try {
			MLPRevCatDescription rd = new MLPRevCatDescription("bogus", "XX", "bogus");
			rd = client.createRevCatDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision description failed on bad ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPRevCatDescription rd = new MLPRevCatDescription(csr.getRevisionId(), "XX", "bogus");
			rd = client.createRevCatDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create revision description failed on bad cat ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			MLPRevCatDescription rd = new MLPRevCatDescription("bogus", "XX", "bogus");
			client.updateRevCatDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update revision description failed on bad ID as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPRevCatDescription rd = new MLPRevCatDescription(csr.getRevisionId(), "XX", "bogus");
			client.updateRevCatDescription(rd);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update revision description failed on bad cat ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.deleteRevCatDescription("bogus", "XX");
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Delete revision description failed as expected: {}", ex.getResponseBodyAsString());
		}

		MLPArtifact ca = new MLPArtifact();
		ca.setArtifactId(UUID.randomUUID().toString());
		Assert.assertNull(client.getArtifact(ca.getArtifactId()));
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
		ca = client.createArtifact(new MLPArtifact("version", "BP", "name", "URI", cu.getUserId(), 1));
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

		Assert.assertFalse(client.getSolutionDownloads("bogus", new RestPageRequest(0, 1)).hasContent());
		Assert.assertFalse(client.getSolutionRatings("bogus", new RestPageRequest(0, 1)).hasContent());
		Assert.assertNull(client.getSolutionRating("bogus", "bogus"));
		Assert.assertTrue(client.getSolutionRevisionsForArtifact("bogus").isEmpty());

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
		MLPTag ct = client.createTag(new MLPTag("tag-error-condition"));
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
			client.findPortalSolutions(null, null, true, null, null, searchTags, null, null, new RestPageRequest(0, 1));
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
			logger.info("Drop tag failedon invalid tag as expected: {}", ex.getResponseBodyAsString());
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
			sd.setSolutionId("bogus");
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

		Assert.assertFalse(client.getSolutionDeployments("bogus", "bogus", new RestPageRequest()).hasContent());
		try {
			client.createSolutionDeployment(
					new MLPSolutionDeployment("bogus", csr.getRevisionId(), cu.getUserId(), "DP"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad solution ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionDeployment(
					new MLPSolutionDeployment(cs.getSolutionId(), "bogus", cu.getUserId(), "DP"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad revision ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			client.createSolutionDeployment(
					new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(), "bogus", "DP"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create solution deployment failed on bad user ID as expected: {}",
					ex.getResponseBodyAsString());
		}
		try {
			MLPSolutionDeployment solDep = new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(),
					cu.getUserId(), "DP");
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
				cu.getUserId(), "DP");
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
		solDep = client.createSolutionDeployment(
				new MLPSolutionDeployment(cs.getSolutionId(), csr.getRevisionId(), cu.getUserId(), "DP"));
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

		Assert.assertTrue(client.getSolutionAccessUsers("bogus sol ID").isEmpty());
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
			cn.setStart(Instant.now());
			cn.setEnd(Instant.now());
			client.createNotification(cn);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Update notification failed on constraint as expected: {}", ex.getResponseBodyAsString());
		}
		// This one should work
		cn.setTitle("notif title in error condn test");
		cn.setMsgSeverityCode("HI");
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
		client.deleteNotification(cn.getNotificationId());

		MLPPeer cp = new MLPPeer();
		cp.setPeerId(UUID.randomUUID().toString());
		Assert.assertNull(client.getPeer(cp.getPeerId()));
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
		cp = client.createPeer(new MLPPeer("peer name", "subj name", "api url", false, false, "contact 1", "AC"));

		try {
			cp = client.createPeer(cp);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer failed on dupe as expected: {}", ex.getResponseBodyAsString());
		}

		try {
			cp = client.createPeer(
					new MLPPeer("another peer name", "subj name", "api url", false, false, "contact 2", "DC"));
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

		Assert.assertTrue(client.getPeerSubscriptions("bogus").isEmpty());
		Assert.assertNull(client.getPeerSubscription(0L));
		try {
			client.createPeerSubscription(new MLPPeerSubscription("peerId", "userId"));
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer sub failed as expected: {}", ex.getResponseBodyAsString());
		}
		try {
			MLPPeerSubscription ps = new MLPPeerSubscription(cp.getPeerId(), cu.getUserId());
			ps.setSelector(
					s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64 + s64);
			client.createPeerSubscription(ps);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("Create peer sub failed as expected: {}", ex.getResponseBodyAsString());
		}
		// Supposed to work
		MLPPeerSubscription ps = new MLPPeerSubscription(cp.getPeerId(), cu.getUserId());
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
		Assert.assertFalse(
				client.getUserSolutionDeployments("solId", "revId", "userId", new RestPageRequest(0, 1)).hasContent());

		Assert.assertNull(client.getPublishRequest(99999L));
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
			client.createPublishRequest(new MLPPublishRequest("bogus", "name", "bogus", "bogus", "bogus"));
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
		Assert.assertNull(client.getDocument("bogus"));
		try {
			MLPDocument doc = new MLPDocument("name", "uri", 100, "bogusUserId");
			doc.setDocumentId("bogus");
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
			client.saveSolutionPicture("bogus", new byte[0]);
			throw new Exception("Unexpected success");
		} catch (HttpStatusCodeException ex) {
			logger.info("save sol pic failed on bad ID as expected: {}", ex.getResponseBodyAsString());
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

	@Test
	public void testHyperlinksSourceTargetRevisions() throws Exception {
		try {
			MLPUser cu = new MLPUser("user_login1", "login1user@abc.com", true);
			cu.setLoginHash("user_pass");
			cu.setFirstName("First Name");
			cu.setLastName("Last Name");
			cu.setEmail(cu.getLoginName() + "@nowhere.com");
			cu = client.createUser(cu);
			logger.info("Created user {}", cu);

			MLPSolution cs = new MLPSolution("solution name", cu.getUserId(), true);
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("CP");
			cs = client.createSolution(cs);
			logger.info("Created solution {}", cs);

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId());
			cr.setPublisher("Big Data Org");
			cr = client.createSolutionRevision(cr);
			logger.info("Created solution revision {}", cr);

			MLPArtifact ca = new MLPArtifact("1.0A", "DI", "artifact name", "http://nexus/artifact", cu.getUserId(), 1);
			ca = client.createArtifact(ca);
			logger.info("Created artifact {}", ca);

			logger.info("Adding artifact to revision");
			client.addSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());

			MLPHyperlink ch = new MLPHyperlink("custom_dataset", "http://custom/dataset");
			ch = client.createHyperlink(ch);
			Assert.assertNotNull(ch);
			Assert.assertNotNull(ch.getHyperlinkId());
			logger.info("Created hyperlink {}", ch);

			logger.info("Adding hyperlink to revision");
			client.addSolutionRevisionHyperlink(cr.getRevisionId(), ch.getHyperlinkId());

			logger.info("Searching for hyperlinks of revision {}", cr);
			List<MLPHyperlink> hyperlinks = client.getSolutionRevisionHyperlinks(cr.getRevisionId());
			Assert.assertFalse(hyperlinks.isEmpty());
			Assert.assertNotNull(hyperlinks.get(0));
			Assert.assertNotNull(hyperlinks.get(0).getHyperlinkId());
			Assert.assertEquals(ch.getHyperlinkId(), hyperlinks.get(0).getHyperlinkId());
			logger.info("Hyperlink found: {}", hyperlinks.get(0));

			MLPSolutionRevision cr2 = new MLPSolutionRevision(cs.getSolutionId(), "2.0R", cu.getUserId());
			cr2.setPublisher("Big Data Org");
			cr2 = client.createSolutionRevision(cr2);
			logger.info("Created solution revision {}", cr2);

			MLPSolutionRevision cr3 = new MLPSolutionRevision(cs.getSolutionId(), "3.0R", cu.getUserId());
			cr3.setPublisher("Big Data Org");
			cr3 = client.createSolutionRevision(cr3);
			logger.info("Created solution revision {}", cr3);

			client.addSolutionRevisionTargetSolutionRevision(cr.getRevisionId(), cr2.getRevisionId());
			logger.info("Created relationship source revision {} to target revision {}", cr, cr2);

			logger.info("Searching for targets of revision {}", cr);
			List<MLPSolutionRevision> targets = client.getSolutionRevisionTargetSolutionRevisions(cr.getRevisionId());
			Assert.assertFalse(targets.isEmpty());
			Assert.assertNotNull(targets.get(0));
			Assert.assertNotNull(targets.get(0).getRevisionId());
			Assert.assertEquals(cr2.getRevisionId(), targets.get(0).getRevisionId());
			logger.info("Target revision: {}", targets.get(0));

			client.addSolutionRevisionSourceSolutionRevision(cr3.getRevisionId(), cr2.getRevisionId());
			logger.info("Created relationship source revision {} to target revision {}", cr2, cr3);

			logger.info("Searching for sources of revision {}", cr3);
			List<MLPSolutionRevision> sources = client.getSolutionRevisionSourceSolutionRevisions(cr3.getRevisionId());
			Assert.assertFalse(sources.isEmpty());
			Assert.assertNotNull(sources.get(0));
			Assert.assertNotNull(sources.get(0).getRevisionId());
			Assert.assertEquals(cr2.getRevisionId(), sources.get(0).getRevisionId());
			logger.info("Source revision: {}", sources.get(0));

			logger.info("Deleting objects");
			client.dropSolutionRevisionArtifact(cs.getSolutionId(), cr.getRevisionId(), ca.getArtifactId());
			client.deleteArtifact(ca.getArtifactId());
			client.deleteHyperlink(ch.getHyperlinkId());
			client.dropSolutionRevisionTargetSolutionRevision(cr.getRevisionId(), cr2.getRevisionId());
			client.dropSolutionRevisionSourceSolutionRevision(cr3.getRevisionId(), cr2.getRevisionId());
			client.deleteSolutionRevision(cs.getSolutionId(), cr.getRevisionId());
			client.deleteSolutionRevision(cs.getSolutionId(), cr2.getRevisionId());
			client.deleteSolutionRevision(cs.getSolutionId(), cr3.getRevisionId());
			client.deleteSolution(cs.getSolutionId());
			client.deleteUser(cu.getUserId());
		} catch (HttpStatusCodeException ex) {
			logger.error("testHyperlinksSourceTargetRevisions failed: " + ex.getResponseBodyAsString(), ex);
			throw ex;
		}
	}
}
