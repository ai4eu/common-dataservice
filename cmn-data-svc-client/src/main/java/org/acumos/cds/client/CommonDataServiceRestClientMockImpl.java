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

package org.acumos.cds.client;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPRtuReference;
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
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Provides a mock implementation of the Common Data Service REST client.
 * Accepts objects via setters and keeps a references for later return by
 * corresponding getter methods.
 */
public class CommonDataServiceRestClientMockImpl implements ICommonDataServiceRestClient {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static ICommonDataServiceRestClient getInstance(String webapiUrl, String user, String pass) {
		return new CommonDataServiceRestClientMockImpl(webapiUrl, user, pass);
	}

	public static ICommonDataServiceRestClient getInstance(String webapiUrl, RestTemplate restTemplate) {
		return new CommonDataServiceRestClientMockImpl(webapiUrl, restTemplate);
	}

	private RestTemplate restTemplate = null;
	private SuccessTransport health = new SuccessTransport(200, "mock health");
	private SuccessTransport version = new SuccessTransport(200, "mock version");
	private long solutionCount = 0;
	private RestPageResponse<MLPSolution> solutions;
	private RestPageResponse<MLPSolution> solutionsBySearchTerm;
	private RestPageResponse<MLPSolution> solutionsByTag;
	private MLPSolution solutionById = new MLPSolution();
	private MLPSolution solution = new MLPSolution();
	private List<MLPSolutionRevision> solutionRevisionListById;
	private List<MLPSolutionRevision> solutionRevisionListByIdList;
	private MLPSolutionRevision solutionRevisionById = new MLPSolutionRevision();
	private List<MLPSolutionRevision> solutionRevisionsForArtifact;
	private MLPSolutionRevision solutionRevision;
	private List<MLPArtifact> solutionRevisionArtifacts;
	private RestPageResponse<MLPTag> tags;
	private MLPTag tag = new MLPTag();
	private List<MLPTag> solutionTags;
	private long artifactCount = 0;
	private RestPageResponse<MLPArtifact> artifacts;
	private RestPageResponse<MLPArtifact> artifactsBySearchTerm;
	private RestPageResponse<MLPArtifact> searchArtifacts;
	private MLPArtifact artifactById = new MLPArtifact();
	private MLPArtifact artifact = new MLPArtifact();
	private long userCount = 0;
	private RestPageResponse<MLPUser> users;
	private RestPageResponse<MLPUser> usersBySearchTerm;
	private RestPageResponse<MLPUser> searchUsers;
	private MLPUser loginUser = new MLPUser();
	private MLPUser userById = new MLPUser();
	private MLPUser user = new MLPUser();
	private List<MLPRole> userRoles;
	private long roleUsersCount = 0;
	private MLPUserLoginProvider userLoginProviderById = new MLPUserLoginProvider();
	private List<MLPUserLoginProvider> userLoginProviders;
	private MLPUserLoginProvider userLoginProvider = new MLPUserLoginProvider();
	private long roleCount = 0;
	private RestPageResponse<MLPRole> searchRoles;
	private RestPageResponse<MLPRole> roles;
	private MLPRole roleById = new MLPRole();
	private MLPRole role = new MLPRole();
	private List<MLPRoleFunction> roleFunctions;
	private MLPRoleFunction roleFunctionById = new MLPRoleFunction();
	private MLPRoleFunction roleFunction = new MLPRoleFunction();
	private RestPageResponse<MLPPeer> peers;
	private RestPageResponse<MLPPeer> searchPeers;
	private MLPPeer peerById = new MLPPeer();
	private MLPPeer peer = new MLPPeer();
	private List<MLPPeerSubscription> peerSubscriptions = new ArrayList<>();
	private MLPPeerSubscription peerSubscriptionById = new MLPPeerSubscription();
	private MLPPeerSubscription peerSubscription = new MLPPeerSubscription();
	private RestPageResponse<MLPSolutionDownload> solutionDownloads;
	private MLPSolutionDownload solutionDownload = new MLPSolutionDownload();
	private RestPageResponse<MLPSolution> favoriteSolutions;
	private MLPSolutionFavorite solutionFavorite = new MLPSolutionFavorite();
	private RestPageResponse<MLPSolutionRating> solutionRatings;
	private MLPSolutionRating solutionRating = new MLPSolutionRating();
	private long notificationCount = 0;
	private RestPageResponse<MLPNotification> notifications;
	private MLPNotification notification;
	private RestPageResponse<MLPUserNotification> userNotifications;
	private MLPUserNotifPref usrNotifPref;
	private List<MLPUserNotifPref> userNotifPreferences;
	private MLPTaskStepResult stepResult;
	private List<MLPUser> solutionAccessUsers;
	private RestPageResponse<MLPSolution> userAccessSolutions;
	private RestPageResponse<MLPSolutionDeployment> userDeployments;
	private RestPageResponse<MLPSolutionDeployment> solutionDeployments;
	private RestPageResponse<MLPSolutionDeployment> userSolutionDeployments;
	private MLPSolutionDeployment solutionDeployment = new MLPSolutionDeployment();
	private MLPSiteConfig siteConfig = new MLPSiteConfig();
	private MLPSiteConfig siteConfigByKey = new MLPSiteConfig();
	private MLPSiteContent siteContent = new MLPSiteContent();
	private MLPSiteContent siteContentByKey = new MLPSiteContent();
	private long threadCount = 0;
	private MLPSolutionRating userSolutionRating = new MLPSolutionRating();
	private RestPageResponse<MLPThread> threads;
	private MLPThread thread = new MLPThread();
	private MLPThread threadById = new MLPThread();
	private long threadCommentCount = 0;
	private RestPageResponse<MLPComment> threadComments;
	private MLPComment comment = new MLPComment();
	private MLPComment commentById = new MLPComment();
	private RestPageResponse<MLPSolution> portalSolutions;
	private RestPageResponse<MLPSolution> searchSolutions;
	private RestPageResponse<MLPThread> solutionRevisionThreads;
	private RestPageResponse<MLPComment> solutionRevisionComments;
	private List<MLPTaskStepResult> stepResults;
	private RestPageResponse<MLPTaskStepResult> searchStepResults;
	private MLPUserNotifPref usrNotifPrefById = null;
	private RestPageResponse<MLPSolution> solutionsByDate;
	private MLPTaskStepResult stepResultById;
	private List<MLPCodeNamePair> pairs;
	private List<String> valueSetNames;
	private String cachedRequestId;
	private List<String> solutionMembers;
	private RestPageResponse<MLPSolution> userSolutions;
	private long solutionRevisionCommentCount;
	private MLPRevisionDescription description;
	private MLPDocument document;
	private MLPDocument documentById;
	private List<MLPDocument> solutionRevisionDocuments;
	private MLPPublishRequest publishRequestById;
	private RestPageResponse<MLPPublishRequest> publishRequests;
	private RestPageResponse<MLPPublishRequest> searchPublishRequests;
	private MLPPublishRequest publishRequest;
	private long userNotificationCount;
	private byte[] solutionImage;
	private MLPCatalog catalog;
	private RestPageResponse<MLPCatalog> catalogs;
	private RestPageResponse<MLPSolution> solutionsInCatalogs;
	private List<MLPCatalog> solutionCatalogs;
	private long catalogSolutionCount;
	private MLPTask taskById;
	private RestPageResponse<MLPTask> tasks;
	private RestPageResponse<MLPTask> searchTasks;
	private MLPTask task;
	private MLPRightToUse rightToUse;
	private RestPageResponse<MLPRightToUse> rightToUses;
	private List<MLPRightToUse> rightToUseList;
	private RestPageResponse<MLPRtuReference> refs;
	private MLPRtuReference ref;
	private RestPageResponse<MLPSiteConfig> siteConfigs;
	private RestPageResponse<MLPSiteContent> siteContents;
	private RestPageResponse<MLPProject> projects;
	private MLPProject projectById;
	private MLPProject project;
	private RestPageResponse<MLPNotebook> notebooks;
	private MLPNotebook notebookById;
	private MLPNotebook notebook;
	private RestPageResponse<MLPPipeline> pipelines;
	private MLPPipeline pipelineById;
	private MLPPipeline pipeline;
	private RestPageResponse<MLPProject> searchProjects;
	private RestPageResponse<MLPNotebook> searchNotebooks;
	private RestPageResponse<MLPPipeline> searchPipelines;
	private List<String> peerAccessCatalogIds;
	private List<String> userFavoriteCatalogIds;

	/**
	 * No-argument constructor.
	 */
	public CommonDataServiceRestClientMockImpl() {

		logger.info("Ctor 1");

		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		mlpPeer.setCreated(Instant.now());
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		List<MLPPeer> peerList = new ArrayList<>();
		peerList.add(mlpPeer);
		peers = new RestPageResponse<>(peerList);

		MLPNotification mlpNotification = new MLPNotification();
		mlpNotification.setCreated(Instant.now());
		mlpNotification.setMessage("notification created");
		mlpNotification.setModified(Instant.now());
		mlpNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbrt");
		mlpNotification.setTitle("Notification");
		mlpNotification.setUrl("http://notify.com");
		mlpNotification.setStart(Instant.now());
		mlpNotification.setEnd(Instant.now());
	}

	public CommonDataServiceRestClientMockImpl(final String webapiUrl, final String user, final String pass) {
		this();
		logger.info("Ctor 2: webapiUrl={}, user={}, pass={}", webapiUrl, user, pass);
	}

	public CommonDataServiceRestClientMockImpl(final String webapiUrl, final RestTemplate restTemplate) {
		this();
		this.restTemplate = restTemplate;
		logger.info("Ctor 3: webapiUrl={}", webapiUrl);
	}

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setHealth(SuccessTransport health) {
		this.health = health;
	}

	@Override
	public SuccessTransport getHealth() {
		return health;
	}

	public void setVersion(SuccessTransport version) {
		this.version = version;
	}

	@Override
	public SuccessTransport getVersion() {
		return version;
	}

	public void setValueSetNames(List<String> names) {
		this.valueSetNames = names;
	}

	@Override
	public List<String> getValueSetNames() {
		return valueSetNames;
	}

	public void setCodeNamePairs(List<MLPCodeNamePair> pairs) {
		this.pairs = pairs;
	}

	@Override
	public List<MLPCodeNamePair> getCodeNamePairs(CodeNameType type) {
		return pairs;
	}

	public void setSolutionCount(Long solutionCount) {
		this.solutionCount = solutionCount;
	}

	@Override
	public long getSolutionCount() {
		return solutionCount;
	}

	public void setSolutions(RestPageResponse<MLPSolution> solutions) {
		this.solutions = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutions(RestPageRequest pageRequest) {
		return solutions;
	}

	public void setSolutionsBySearchTerm(RestPageResponse<MLPSolution> solutions) {
		this.solutionsBySearchTerm = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		return solutionsBySearchTerm;
	}

	public void setSolutionsByTag(RestPageResponse<MLPSolution> solutions) {
		this.solutionsByTag = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsByTag(String tag, RestPageRequest pageRequest) {
		return solutionsByTag;
	}

	public void setSolutionsByDate(RestPageResponse<MLPSolution> solutions) {
		this.solutionsByDate = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsByDate(boolean active, String[] accessTypeCodes, Instant i,
			RestPageRequest pageRequest) {
		return solutionsByDate;
	}

	public void setSolutionById(MLPSolution solution) {
		this.solutionById = solution;
	}

	@Override
	public MLPSolution getSolution(String solutionId) {
		return solutionById;
	}

	public void setSolution(MLPSolution solution) {
		this.solution = solution;
	}

	@Override
	public MLPSolution createSolution(MLPSolution solution) {
		return this.solution;
	}

	@Override
	public void updateSolution(MLPSolution solution) {
		this.solution = solution;
	}

	@Override
	public void incrementSolutionViewCount(String solutionId) {
		// What to mock here?
	}

	@Override
	public void deleteSolution(String solutionId) {
		// What to mock here?
	}

	public void setSolutionRevisionsById(List<MLPSolutionRevision> list) {
		this.solutionRevisionListById = list;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String solutionId) {
		return solutionRevisionListById;
	}

	public void setSolutionRevisionsByIdList(List<MLPSolutionRevision> list) {
		this.solutionRevisionListByIdList = list;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String[] solutionIds) {
		return solutionRevisionListByIdList;
	}

	public void setSolutionRevisionById(MLPSolutionRevision revision) {
		this.solutionRevisionById = revision;
	}

	@Override
	public MLPSolutionRevision getSolutionRevision(String solutionId, String revisionId) {
		return solutionRevisionById;
	}

	public void setSolutionRevisionsForArtifact(List<MLPSolutionRevision> list) {
		solutionRevisionsForArtifact = list;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisionsForArtifact(String artifactId) {
		return solutionRevisionsForArtifact;
	}

	public void setSolutionRevision(MLPSolutionRevision revision) {
		solutionRevision = revision;
	}

	@Override
	public MLPSolutionRevision createSolutionRevision(MLPSolutionRevision revision) {
		return solutionRevision;
	}

	@Override
	public void updateSolutionRevision(MLPSolutionRevision revision) {
		solutionRevision = revision;
	}

	@Override
	public void deleteSolutionRevision(String solutionId, String revisionId) {
		// What to mock here?
	}

	public void setSolutionRevisionArtifacts(List<MLPArtifact> artifacts) {
		solutionRevisionArtifacts = artifacts;
	}

	@Override
	public List<MLPArtifact> getSolutionRevisionArtifacts(String solutionId, String revisionId) {
		return solutionRevisionArtifacts;
	}

	@Override
	public void addSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId) {
		// What to mock here?
	}

	@Override
	public void dropSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId) {
		// What to mock here?
	}

	public void setTags(RestPageResponse<MLPTag> tags) {
		this.tags = tags;
	}

	@Override
	public RestPageResponse<MLPTag> getTags(RestPageRequest pageRequest) {
		return tags;
	}

	public void setTag(MLPTag tag) {
		this.tag = tag;
	}

	@Override
	public MLPTag createTag(MLPTag tag) {
		return this.tag;
	}

	@Override
	public void deleteTag(MLPTag tag) {
		// what to mock?
	}

	public void setSolutionTags(List<MLPTag> tags) {
		this.solutionTags = tags;
	}

	@Override
	public List<MLPTag> getSolutionTags(String solutionId) {
		return solutionTags;
	}

	@Override
	public void addSolutionTag(String solutionId, String tag) {
		// what to mock?
	}

	@Override
	public void dropSolutionTag(String solutionId, String tag) {
		// what to mock?
	}

	public void setArtifactCount(long count) {
		this.artifactCount = count;
	}

	@Override
	public long getArtifactCount() {
		return artifactCount;
	}

	public void setArtifacts(RestPageResponse<MLPArtifact> artifacts) {
		this.artifacts = artifacts;
	}

	@Override
	public RestPageResponse<MLPArtifact> getArtifacts(RestPageRequest pageRequest) {
		return artifacts;
	}

	public void setArtifactsBySearchTerm(RestPageResponse<MLPArtifact> artifacts) {
		this.artifactsBySearchTerm = artifacts;
	}

	@Override
	public RestPageResponse<MLPArtifact> findArtifactsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		return artifactsBySearchTerm;
	}

	public void setSearchArtifacts(RestPageResponse<MLPArtifact> artifacts) {
		this.searchArtifacts = artifacts;
	}

	@Override
	public RestPageResponse<MLPArtifact> searchArtifacts(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return searchArtifacts;
	}

	public void setArtifactById(MLPArtifact artifact) {
		this.artifactById = artifact;
	}

	@Override
	public MLPArtifact getArtifact(String artifactId) {
		return artifactById;
	}

	public void setArtifact(MLPArtifact artifact) {
		this.artifact = artifact;
	}

	@Override
	public MLPArtifact createArtifact(MLPArtifact artifact) {
		return this.artifact;
	}

	@Override
	public void updateArtifact(MLPArtifact artifact) {
		this.artifact = artifact;
	}

	@Override
	public void deleteArtifact(String artifactId) {
		// How to mock?
	}

	public void setUserCount(long count) {
		this.userCount = count;
	}

	@Override
	public long getUserCount() {
		return userCount;
	}

	public void setUsers(RestPageResponse<MLPUser> users) {
		this.users = users;
	}

	@Override
	public RestPageResponse<MLPUser> getUsers(RestPageRequest pageRequest) {
		return users;
	}

	public void setUsersBySearchTerm(RestPageResponse<MLPUser> users) {
		this.usersBySearchTerm = users;
	}

	@Override
	public RestPageResponse<MLPUser> findUsersBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		return usersBySearchTerm;
	}

	public void setSearchUsers(RestPageResponse<MLPUser> users) {
		this.searchUsers = users;
	}

	@Override
	public RestPageResponse<MLPUser> searchUsers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return searchUsers;
	}

	public void setLoginUser(MLPUser user) {
		this.loginUser = user;
	}

	@Override
	public MLPUser loginUser(String name, String pass) {
		return loginUser;
	}

	@Override
	public MLPUser loginApiUser(String name, String token) {
		return loginUser;
	}

	@Override
	public MLPUser verifyUser(String name, String token) {
		return loginUser;
	}

	public void setUserById(MLPUser user) {
		this.userById = user;
	}

	@Override
	public MLPUser getUser(String userId) {
		return userById;
	}

	public void setUser(MLPUser user) {
		this.user = user;
	}

	@Override
	public MLPUser createUser(MLPUser user) {
		return this.user;
	}

	@Override
	public void updateUser(MLPUser user) {
		this.user = user;
	}

	@Override
	public void deleteUser(String userId) {
		// How to mock?
	}

	public void setUserRoles(List<MLPRole> roles) {
		this.userRoles = roles;
	}

	@Override
	public List<MLPRole> getUserRoles(String userId) {
		return userRoles;
	}

	@Override
	public void addUserRole(String userId, String roleId) {
		// How to mock?
	}

	@Override
	public void updateUserRoles(String userId, List<String> roleIds) {
		// How to mock?
	}

	@Override
	public void dropUserRole(String userId, String roleId) {
		// How to mock?
	}

	@Override
	public void addUsersInRole(List<String> userIds, String roleId) {
		// How to mock?
	}

	@Override
	public void dropUsersInRole(List<String> userIds, String roleId) {
		// How to mock?
	}

	public void setRoleUsersCount(long count) {
		this.roleUsersCount = count;
	}

	@Override
	public long getRoleUsersCount(String roleId) {
		return roleUsersCount;
	}

	public void setUserLoginProviderById(MLPUserLoginProvider provider) {
		this.userLoginProviderById = provider;
	}

	@Override
	public MLPUserLoginProvider getUserLoginProvider(String userId, String providerCode, String providerLogin) {
		return userLoginProviderById;
	}

	public void setUserLoginProviders(List<MLPUserLoginProvider> providers) {
		this.userLoginProviders = providers;
	}

	@Override
	public List<MLPUserLoginProvider> getUserLoginProviders(String userId) {
		return userLoginProviders;
	}

	public void setUserLoginProvider(MLPUserLoginProvider provider) {
		this.userLoginProvider = provider;
	}

	@Override
	public MLPUserLoginProvider createUserLoginProvider(MLPUserLoginProvider provider) {
		return this.userLoginProvider;
	}

	@Override
	public void updateUserLoginProvider(MLPUserLoginProvider provider) {
		this.userLoginProvider = provider;
	}

	@Override
	public void deleteUserLoginProvider(MLPUserLoginProvider provider) {
		// How to mock?
	}

	public void setRoleCount(long count) {
		this.roleCount = count;
	}

	@Override
	public long getRoleCount() {
		return roleCount;
	}

	public void setSearchRoles(RestPageResponse<MLPRole> roles) {
		this.searchRoles = roles;
	}

	@Override
	public RestPageResponse<MLPRole> searchRoles(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return searchRoles;
	}

	public void setRoles(RestPageResponse<MLPRole> roles) {
		this.roles = roles;
	}

	@Override
	public RestPageResponse<MLPRole> getRoles(RestPageRequest pageRequest) {
		return roles;
	}

	public void setRoleById(MLPRole role) {
		this.roleById = role;
	}

	@Override
	public MLPRole getRole(String roleId) {
		return roleById;
	}

	public void setRole(MLPRole role) {
		this.role = role;
	}

	@Override
	public MLPRole createRole(MLPRole role) {
		return this.role;
	}

	@Override
	public void updateRole(MLPRole role) {
		this.role = role;
	}

	@Override
	public void deleteRole(String roleId) {
		// How to mock?
	}

	public void setRoleFunctions(List<MLPRoleFunction> functions) {
		this.roleFunctions = functions;
	}

	@Override
	public List<MLPRoleFunction> getRoleFunctions(String roleId) {
		return roleFunctions;
	}

	public void setRoleFunctionById(MLPRoleFunction function) {
		this.roleFunctionById = function;
	}

	@Override
	public MLPRoleFunction getRoleFunction(String roleId, String roleFunctionId) {
		return roleFunctionById;
	}

	public void setRoleFunction(MLPRoleFunction function) {
		this.roleFunction = function;
	}

	@Override
	public MLPRoleFunction createRoleFunction(MLPRoleFunction roleFunction) {
		return this.roleFunction;
	}

	@Override
	public void updateRoleFunction(MLPRoleFunction roleFunction) {
		this.roleFunction = roleFunction;
	}

	@Override
	public void deleteRoleFunction(String roleId, String roleFunctionId) {
		// How to mock?
	}

	public void setPeers(RestPageResponse<MLPPeer> peers) {
		this.peers = peers;
	}

	@Override
	public RestPageResponse<MLPPeer> getPeers(RestPageRequest pageRequest) {
		return peers;
	}

	public void setSearchPeers(RestPageResponse<MLPPeer> peers) {
		this.searchPeers = peers;
	}

	@Override
	public RestPageResponse<MLPPeer> searchPeers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return searchPeers;
	}

	public void setPeerById(MLPPeer peer) {
		this.peerById = peer;
	}

	@Override
	public MLPPeer getPeer(String peerId) {
		return peerById;
	}

	public void setPeer(MLPPeer peer) {
		this.peer = peer;
	}

	@Override
	public MLPPeer createPeer(MLPPeer peer) {
		return this.peer;
	}

	@Override
	public void updatePeer(MLPPeer peer) {
		this.peer = peer;
	}

	@Override
	public void deletePeer(String peerId) {
		// How to mock?
	}

	@Override
	public long getPeerSubscriptionCount(String peerId) {
		return peerSubscriptions.size();
	}

	public void setPeerSubscriptions(List<MLPPeerSubscription> subs) {
		this.peerSubscriptions = subs;
	}

	@Override
	public List<MLPPeerSubscription> getPeerSubscriptions(String peerId) {
		return peerSubscriptions;
	}

	public void setPeerSubscriptionById(MLPPeerSubscription sub) {
		this.peerSubscriptionById = sub;
	}

	@Override
	public MLPPeerSubscription getPeerSubscription(Long subscriptionId) {
		return peerSubscriptionById;
	}

	public void setPeerSubscription(MLPPeerSubscription peerSub) {
		this.peerSubscription = peerSub;
	}

	@Override
	public MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub) {
		return this.peerSubscription;
	}

	@Override
	public void updatePeerSubscription(MLPPeerSubscription peerSub) {
		this.peerSubscription = peerSub;
	}

	@Override
	public void deletePeerSubscription(Long subscriptionId) {
		// How to mock?
	}

	public void setSolutionDownloads(RestPageResponse<MLPSolutionDownload> downloads) {
		this.solutionDownloads = downloads;
	}

	@Override
	public RestPageResponse<MLPSolutionDownload> getSolutionDownloads(String solutionId, RestPageRequest pageRequest) {
		return solutionDownloads;
	}

	public void setSolutionDownload(MLPSolutionDownload download) {
		this.solutionDownload = download;
	}

	@Override
	public MLPSolutionDownload createSolutionDownload(MLPSolutionDownload download) {
		return this.solutionDownload;
	}

	@Override
	public void deleteSolutionDownload(MLPSolutionDownload download) {
		// How to mock?
	}

	public void setFavoriteSolutions(RestPageResponse<MLPSolution> solutions) {
		this.favoriteSolutions = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> getFavoriteSolutions(String userId, RestPageRequest pageRequest) {
		return favoriteSolutions;
	}

	public void setSolutionFavorite(MLPSolutionFavorite favorite) {
		this.solutionFavorite = favorite;
	}

	@Override
	public MLPSolutionFavorite createSolutionFavorite(MLPSolutionFavorite favorite) {
		return this.solutionFavorite;
	}

	@Override
	public void deleteSolutionFavorite(MLPSolutionFavorite favorite) {
		// How to mock?
	}

	public void setSolutionRatings(RestPageResponse<MLPSolutionRating> ratings) {
		this.solutionRatings = ratings;
	}

	@Override
	public RestPageResponse<MLPSolutionRating> getSolutionRatings(String solutionId, RestPageRequest pageRequest) {
		return solutionRatings;
	}

	public void setUserSolutionRating(MLPSolutionRating rating) {
		this.userSolutionRating = rating;
	}

	@Override
	public MLPSolutionRating getSolutionRating(String solutionId, String userId) {
		return userSolutionRating;
	}

	public void setSolutionRating(MLPSolutionRating rating) {
		this.solutionRating = rating;
	}

	@Override
	public MLPSolutionRating createSolutionRating(MLPSolutionRating rating) {
		return solutionRating;
	}

	@Override
	public void updateSolutionRating(MLPSolutionRating rating) {
		this.solutionRating = rating;
	}

	@Override
	public void deleteSolutionRating(MLPSolutionRating rating) {
		// How to mock?
	}

	public void setNotificationCount(long count) {
		this.notificationCount = count;
	}

	@Override
	public long getNotificationCount() {
		return notificationCount;
	}

	public void setNotifications(RestPageResponse<MLPNotification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public RestPageResponse<MLPNotification> getNotifications(RestPageRequest pageRequest) {
		return notifications;
	}

	public void setNotification(MLPNotification notification) {
		this.notification = notification;
	}

	@Override
	public MLPNotification createNotification(MLPNotification notification) {
		return this.notification;
	}

	@Override
	public void updateNotification(MLPNotification notification) {
		this.notification = notification;
	}

	@Override
	public void deleteNotification(String notificationId) {
		// How to mock?
	}

	public void setUserNotificationCount(long count) {
		this.userNotificationCount = count;
	}

	@Override
	public long getUserUnreadNotificationCount(String userId) {
		return this.userNotificationCount;
	}

	public void setUserNotifications(RestPageResponse<MLPUserNotification> notifications) {
		this.userNotifications = notifications;
	}

	@Override
	public RestPageResponse<MLPUserNotification> getUserNotifications(String userId, RestPageRequest pageRequest) {
		return this.userNotifications;
	}

	@Override
	public void addUserToNotification(String notificationId, String userId) {
		// How to mock?
	}

	@Override
	public void dropUserFromNotification(String notificationId, String userId) {
		// How to mock?
	}

	@Override
	public void setUserViewedNotification(String notificationId, String userId) {
		// How to mock?
	}

	public void setUserNotificationPreference(MLPUserNotifPref usrNotifPref) {
		this.usrNotifPref = usrNotifPref;
	}

	@Override
	public MLPUserNotifPref createUserNotificationPreference(MLPUserNotifPref usrNotifPref) {
		return this.usrNotifPref;
	}

	public void setUserNotificationPreferences(List<MLPUserNotifPref> usrNotifPref) {
		this.userNotifPreferences = usrNotifPref;
	}

	@Override
	public List<MLPUserNotifPref> getUserNotificationPreferences(String userId) {
		return this.userNotifPreferences;
	}

	public void setUserNotificationPreferenceById(MLPUserNotifPref usrNotifPref) {
		this.usrNotifPrefById = usrNotifPref;
	}

	@Override
	public MLPUserNotifPref getUserNotificationPreference(Long userNotifPrefID) {
		return this.usrNotifPrefById;
	}

	@Override
	public void deleteUserNotificationPreference(Long usrNotifprefId) {
		// How to mock?
	}

	@Override
	public void updateUserNotificationPreference(MLPUserNotifPref usrNotifpref) {
		// How to mock?
	}

	public void setSolutionAccessUsers(List<MLPUser> users) {
		this.solutionAccessUsers = users;
	}

	@Override
	public List<MLPUser> getSolutionAccessUsers(String solutionId) {
		return solutionAccessUsers;
	}

	public void setUserAccessSolutions(RestPageResponse<MLPSolution> solutions) {
		this.userAccessSolutions = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {
		return userAccessSolutions;
	}

	@Override
	public void addSolutionUserAccess(String solutionId, String userId) {
		// How to mock?
	}

	@Override
	public void dropSolutionUserAccess(String solutionId, String userId) {
		// How to mock?
	}

	@Override
	public void updatePassword(MLPUser user, MLPPasswordChangeRequest changeRequest) {
		// How to mock?
	}

	public void setUserDeployments(RestPageResponse<MLPSolutionDeployment> deployments) {
		this.userDeployments = deployments;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserDeployments(String userId, RestPageRequest pageRequest) {
		return userDeployments;
	}

	public void setSolutionDeployments(RestPageResponse<MLPSolutionDeployment> deployments) {
		this.solutionDeployments = deployments;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getSolutionDeployments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		return solutionDeployments;
	}

	public void setUserSolutionDeployments(RestPageResponse<MLPSolutionDeployment> deployments) {
		this.userSolutionDeployments = deployments;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserSolutionDeployments(String solutionId, String revisionId,
			String userId, RestPageRequest pageRequest) {
		return userSolutionDeployments;
	}

	public void setSolutionDeployment(MLPSolutionDeployment deployment) {
		this.solutionDeployment = deployment;
	}

	@Override
	public MLPSolutionDeployment createSolutionDeployment(MLPSolutionDeployment deployment) {
		return this.solutionDeployment;
	}

	@Override
	public void updateSolutionDeployment(MLPSolutionDeployment deployment) {
		this.solutionDeployment = deployment;
	}

	@Override
	public void deleteSolutionDeployment(MLPSolutionDeployment deployment) {
		// How to mock?
	}

	public void setSiteConfigByKey(MLPSiteConfig config) {
		this.siteConfigByKey = config;
	}

	public void setSiteConfigs(RestPageResponse<MLPSiteConfig> siteConfigs) {
		this.siteConfigs = siteConfigs;
	}

	@Override
	public RestPageResponse<MLPSiteConfig> getSiteConfigs(RestPageRequest pageRequest) {
		return siteConfigs;
	}

	@Override
	public MLPSiteConfig getSiteConfig(String configKey) {
		return siteConfigByKey;
	}

	public void setSiteConfig(MLPSiteConfig config) {
		this.siteConfig = config;
	}

	@Override
	public MLPSiteConfig createSiteConfig(MLPSiteConfig config) {
		return this.siteConfig;
	}

	@Override
	public void updateSiteConfig(MLPSiteConfig config) {
		this.siteConfig = config;
	}

	@Override
	public void deleteSiteConfig(String configKey) {
		// How to mock?
	}

	public void setSiteContents(RestPageResponse<MLPSiteContent> siteContents) {
		this.siteContents = siteContents;
	}

	@Override
	public RestPageResponse<MLPSiteContent> getSiteContents(RestPageRequest pageRequest) {
		return siteContents;
	}

	public void setSiteContentByKey(MLPSiteContent content) {
		this.siteContentByKey = content;
	}

	@Override
	public MLPSiteContent getSiteContent(String contentKey) {
		return siteContentByKey;
	}

	public void setSiteContent(MLPSiteContent content) {
		this.siteContent = content;
	}

	@Override
	public MLPSiteContent createSiteContent(MLPSiteContent content) {
		return this.siteContent;
	}

	@Override
	public void updateSiteContent(MLPSiteContent content) {
		this.siteContent = content;
	}

	@Override
	public void deleteSiteContent(String contentKey) {
		// How to mock?
	}

	public void setThreadCount(long count) {
		this.threadCount = count;
	}

	@Override
	public long getThreadCount() {
		return threadCount;
	}

	public void setThreads(RestPageResponse<MLPThread> threads) {
		this.threads = threads;
	}

	@Override
	public RestPageResponse<MLPThread> getThreads(RestPageRequest pageRequest) {
		return this.threads;
	}

	public void setThreadById(MLPThread thread) {
		this.threadById = thread;
	}

	@Override
	public MLPThread getThread(String threadId) {
		return threadById;
	}

	public void setThread(MLPThread thread) {
		this.thread = thread;
	}

	@Override
	public MLPThread createThread(MLPThread thread) {
		return this.thread;
	}

	@Override
	public void updateThread(MLPThread thread) {
		this.thread = thread;
	}

	@Override
	public void deleteThread(String threadId) {
		// How to mock?
	}

	public void setThreadCommentCount(long count) {
		this.threadCommentCount = count;
	}

	@Override
	public long getThreadCommentCount(String threadId) {
		return threadCommentCount;
	}

	public void setThreadComments(RestPageResponse<MLPComment> comments) {
		this.threadComments = comments;
	}

	@Override
	public RestPageResponse<MLPComment> getThreadComments(String threadId, RestPageRequest pageRequest) {
		return this.threadComments;
	}

	public void setCommentById(MLPComment comment) {
		this.commentById = comment;
	}

	@Override
	public MLPComment getComment(String threadId, String commentId) {
		return this.commentById;
	}

	public void setComment(MLPComment comment) {
		this.comment = comment;
	}

	@Override
	public MLPComment createComment(MLPComment comment) {
		return this.comment;
	}

	@Override
	public void updateComment(MLPComment comment) {
		this.comment = comment;
	}

	@Override
	public void deleteComment(String threadId, String commentId) {
		// How to mock?
	}

	public void setPortalSolutions(RestPageResponse<MLPSolution> solutions) {
		this.portalSolutions = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String[] userIds, String[] accessTypeCodes, String[] modelTypeCodes, String[] tags,
			String[] authKws, String[] pubKws, RestPageRequest pageRequest) {
		return this.portalSolutions;
	}

	@Override
	public RestPageResponse<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active,
			String[] userIds, String[] accessTypeCodes, String[] modelTypeCodes, String[] allTags, String[] anyTags,
			String[] catalogIds, RestPageRequest pageRequest) {
		return this.portalSolutions;
	}

	public void setUserSolutions(RestPageResponse<MLPSolution> solutions) {
		this.userSolutions = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> findUserSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String userId, String[] accessTypeCodes, String[] modelTypeCodes, String[] tags,
			RestPageRequest pageRequest) {
		return this.userSolutions;
	}

	public void setSearchSolutions(RestPageResponse<MLPSolution> solutions) {
		this.searchSolutions = solutions;
	}

	@Override
	public RestPageResponse<MLPSolution> searchSolutions(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchSolutions;
	}

	public long getSolutionRevisionThreadCount(String solutionId, String revisionId) {
		return this.solutionRevisionThreads.getSize();
	}

	public void setSolutionRevisionThreads(RestPageResponse<MLPThread> threads) {
		this.solutionRevisionThreads = threads;
	}

	@Override
	public RestPageResponse<MLPThread> getSolutionRevisionThreads(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		return this.solutionRevisionThreads;
	}

	public void setSolutionRevisionCommentCount(long count) {
		this.solutionRevisionCommentCount = count;
	}

	@Override
	public long getSolutionRevisionCommentCount(String solutionId, String revisionId) {
		return this.solutionRevisionCommentCount;
	}

	public void setSolutionRevisionComments(RestPageResponse<MLPComment> comments) {
		this.solutionRevisionComments = comments;
	}

	@Override
	public RestPageResponse<MLPComment> getSolutionRevisionComments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		return this.solutionRevisionComments;
	}

	public void setStepResultById(MLPTaskStepResult stepResult) {
		this.stepResultById = stepResult;
	}

	@Override
	public MLPTaskStepResult getTaskStepResult(long stepResultId) {
		return stepResultById;
	}

	public void setStepResults(List<MLPTaskStepResult> results) {
		this.stepResults = results;
	}

	@Override
	public List<MLPTaskStepResult> getTaskStepResults(long taskId) {
		return stepResults;
	}

	public void setSearchStepResults(RestPageResponse<MLPTaskStepResult> results) {
		this.searchStepResults = results;
	}

	@Override
	public RestPageResponse<MLPTaskStepResult> searchTaskStepResults(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchStepResults;
	}

	public void setStepResult(MLPTaskStepResult result) {
		this.stepResult = result;
	}

	@Override
	public MLPTaskStepResult createTaskStepResult(MLPTaskStepResult stepResult) {
		return this.stepResult;
	}

	@Override
	public void updateTaskStepResult(MLPTaskStepResult stepResult) {
		this.stepResult = stepResult;
	}

	@Override
	public void deleteTaskStepResult(long stepResultId) {
		// How to mock?
	}

	@Override
	public void setRequestId(String requestId) {
		this.cachedRequestId = requestId;
		logger.info("set request id {}", this.cachedRequestId);
	}

	public void setCompositeSolutionMembers(List<String> members) {
		this.solutionMembers = members;
	}

	@Override
	public List<String> getCompositeSolutionMembers(String parentId) {
		return solutionMembers;
	}

	@Override
	public void addCompositeSolutionMember(String parentId, String childId) {
		// what to mock?
	}

	@Override
	public void dropCompositeSolutionMember(String parentId, String childId) {
		// what to mock?
	}

	public void setRevisionDescription(MLPRevisionDescription description) {
		this.description = description;
	}

	@Override
	public MLPRevisionDescription getRevisionDescription(String revisionId, String accessTypeCode) {
		return this.description;
	}

	@Override
	public MLPRevisionDescription createRevisionDescription(MLPRevisionDescription description) {
		return this.description;
	}

	@Override
	public void updateRevisionDescription(MLPRevisionDescription description) {
		this.description = description;
	}

	@Override
	public void deleteRevisionDescription(String revisionId, String accessTypeCode) {
		// How to mock?
	}

	public void setDocumentById(MLPDocument document) {
		this.documentById = document;
	}

	@Override
	public MLPDocument getDocument(String documentId) {
		return this.documentById;
	}

	public void setDocument(MLPDocument document) {
		this.document = document;
	}

	@Override
	public MLPDocument createDocument(MLPDocument document) {
		return this.document;
	}

	@Override
	public void updateDocument(MLPDocument document) {
		this.document = document;
	}

	@Override
	public void deleteDocument(String documentId) {
		// How to mock?
	}

	public void setSolutionRevisionDocuments(List<MLPDocument> documents) {
		this.solutionRevisionDocuments = documents;
	}

	@Override
	public List<MLPDocument> getSolutionRevisionDocuments(String revisionId, String accessTypeCode) {
		return solutionRevisionDocuments;
	}

	@Override
	public void addSolutionRevisionDocument(String revisionId, String accessTypeCode, String documentId) {
		// what to mock?
	}

	@Override
	public void dropSolutionRevisionDocument(String revisionId, String accessTypeCode, String documentId) {
		// what to mock?
	}

	public void setPublishRequestById(MLPPublishRequest publishRequest) {
		this.publishRequestById = publishRequest;
	}

	@Override
	public MLPPublishRequest getPublishRequest(long publishRequestId) {
		return publishRequestById;
	}

	public void setPublishRequests(RestPageResponse<MLPPublishRequest> results) {
		this.publishRequests = results;
	}

	@Override
	public RestPageResponse<MLPPublishRequest> getPublishRequests(RestPageRequest pageRequest) {
		return publishRequests;
	}

	public void setSearchPublishRequests(RestPageResponse<MLPPublishRequest> results) {
		this.searchPublishRequests = results;
	}

	@Override
	public RestPageResponse<MLPPublishRequest> searchPublishRequests(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchPublishRequests;
	}

	@Override
	public boolean isPublishRequestPending(String solutionId, String revisionId) {
		return false;
	}

	public void setPublishRequest(MLPPublishRequest result) {
		this.publishRequest = result;
	}

	@Override
	public MLPPublishRequest createPublishRequest(MLPPublishRequest publishRequest) {
		return this.publishRequest;
	}

	@Override
	public void updatePublishRequest(MLPPublishRequest publishRequest) {
		this.publishRequest = publishRequest;
	}

	@Override
	public void deletePublishRequest(long publishRequestId) {
		// How to mock?
	}

	@Override
	public void addUserTag(String userId, String tag) {
		// what to mock?
	}

	@Override
	public void dropUserTag(String userId, String tag) {
		// what to mock?
	}

	@Override
	public byte[] getSolutionPicture(String solutionId) {
		return this.solutionImage;
	}

	@Override
	public void saveSolutionPicture(String solutionId, byte[] image) {
		this.solutionImage = image;
	}

	public void setCatalog(MLPCatalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public MLPCatalog getCatalog(String catalogId) {
		return this.catalog;
	}

	public void setCatalogs(RestPageResponse<MLPCatalog> catalogs) {
		this.catalogs = catalogs;
	}

	@Override
	public RestPageResponse<MLPCatalog> getCatalogs(RestPageRequest pageRequest) {
		return this.catalogs;
	}

	@Override
	public MLPCatalog createCatalog(MLPCatalog catalog) {
		return this.catalog;
	}

	@Override
	public void updateCatalog(MLPCatalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public void deleteCatalog(String catalogId) {
		this.catalog = null;
	}

	public void setCatalogSolutionCount(long count) {
		this.catalogSolutionCount = count;
	}

	@Override
	public long getCatalogSolutionCount(String catalogId) {
		return this.catalogSolutionCount;
	}

	public void setSolutionsInCatalogs(RestPageResponse<MLPSolution> sols) {
		this.solutionsInCatalogs = sols;
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutionsInCatalogs(String[] catalogIds, RestPageRequest pageRequest) {
		return solutionsInCatalogs;
	}

	public void setSolutionCatalogs(List<MLPCatalog> cats) {
		this.solutionCatalogs = cats;
	}

	@Override
	public List<MLPCatalog> getSolutionCatalogs(String solId) {
		return this.solutionCatalogs;
	}

	@Override
	public void addSolutionToCatalog(String solutionId, String catalogId) {
		// How to mock?
	}

	@Override
	public void dropSolutionFromCatalog(String solutionId, String catalogId) {
		// How to mock?
	}

	public void setTaskById(MLPTask task) {
		this.taskById = task;
	}

	@Override
	public MLPTask getTask(long taskId) {
		return taskById;
	}

	public void setTasks(RestPageResponse<MLPTask> results) {
		this.tasks = results;
	}

	@Override
	public RestPageResponse<MLPTask> getTasks(RestPageRequest pageRequest) {
		return tasks;
	}

	public void setSearchTasks(RestPageResponse<MLPTask> results) {
		this.searchTasks = results;
	}

	@Override
	public RestPageResponse<MLPTask> searchTasks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchTasks;
	}

	public void setTask(MLPTask result) {
		this.task = result;
	}

	@Override
	public MLPTask createTask(MLPTask task) {
		return this.task;
	}

	@Override
	public void updateTask(MLPTask task) {
		this.task = task;
	}

	@Override
	public void deleteTask(long taskId) {
	}

	public void setRtuReferences(RestPageResponse<MLPRtuReference> refs) {
		this.refs = refs;
	}

	@Override
	public RestPageResponse<MLPRtuReference> getRtuReferences(RestPageRequest pageRequest) {
		return refs;
	}

	public void setRtuReference(MLPRtuReference ref) {
		this.ref = ref;
	}

	@Override
	public MLPRtuReference createRtuReference(MLPRtuReference ref) {
		return this.ref;
	}

	@Override
	public void deleteRtuReference(MLPRtuReference ref) {
		// what to mock?
	}

	public void setRightToUse(MLPRightToUse rtu) {
		this.rightToUse = rtu;
	}

	@Override
	public MLPRightToUse getRightToUse(Long rtuId) {
		return this.rightToUse;
	}

	public void setRightToUses(RestPageResponse<MLPRightToUse> rtus) {
		this.rightToUses = rtus;
	}

	@Override
	public RestPageResponse<MLPRightToUse> getRightToUses(RestPageRequest pageRequest) {
		return this.rightToUses;
	}

	@Override
	public RestPageResponse<MLPRightToUse> searchRightToUses(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.rightToUses;
	}

	public void setRightToUseList(List<MLPRightToUse> rtus) {
		this.rightToUseList = rtus;
	}

	@Override
	public List<MLPRightToUse> getRightToUses(String solutionId, String userId) {
		return this.rightToUseList;
	}

	@Override
	public MLPRightToUse createRightToUse(MLPRightToUse rightToUse) {
		return this.rightToUse;
	}

	@Override
	public void updateRightToUse(MLPRightToUse rightToUse) {
		this.rightToUse = rightToUse;
	}

	@Override
	public void deleteRightToUse(Long rtuId) {
		this.rightToUse = null;
	}

	@Override
	public void addRefToRtu(String refId, Long rtuId) {
		// How to mock?
	}

	@Override
	public void dropRefFromRtu(String refId, Long rtuId) {
		// How to mock?
	}

	@Override
	public void addUserToRtu(String userId, Long rtuId) {
		// How to mock?
	}

	@Override
	public void dropUserFromRtu(String userId, Long rtuId) {
		// How to mock?
	}

	public void setProjects(RestPageResponse<MLPProject> results) {
		this.projects = results;
	}

	@Override
	public RestPageResponse<MLPProject> getProjects(RestPageRequest pageRequest) {
		return projects;
	}

	public void setSearchProjects(RestPageResponse<MLPProject> results) {
		this.searchProjects = results;
	}

	@Override
	public RestPageResponse<MLPProject> searchProjects(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchProjects;
	}

	public void setProjectById(MLPProject object) {
		this.projectById = object;
	}

	@Override
	public MLPProject getProject(String projectId) {
		return projectById;
	}

	public void setProject(MLPProject object) {
		this.project = object;
	}

	@Override
	public MLPProject createProject(MLPProject project) {
		return this.project;
	}

	@Override
	public void updateProject(MLPProject project) {
		this.project = project;
	}

	@Override
	public void deleteProject(String projectId) {
		this.project = null;
	}

	@Override
	public List<MLPNotebook> getProjectNotebooks(String projectId) {
		return Collections.emptyList();
	}

	@Override
	public List<MLPPipeline> getProjectPipelines(String projectId) {
		return Collections.emptyList();
	}

	public void setNotebooks(RestPageResponse<MLPNotebook> results) {
		this.notebooks = results;
	}

	@Override
	public RestPageResponse<MLPNotebook> getNotebooks(RestPageRequest pageRequest) {
		return notebooks;
	}

	public void setSearchNotebooks(RestPageResponse<MLPNotebook> results) {
		this.searchNotebooks = results;
	}

	@Override
	public RestPageResponse<MLPNotebook> searchNotebooks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchNotebooks;
	}

	public void setNotebookById(MLPNotebook object) {
		this.notebookById = object;
	}

	@Override
	public MLPNotebook getNotebook(String notebookId) {
		return notebookById;
	}

	public void setNotebook(MLPNotebook object) {
		this.notebook = object;
	}

	@Override
	public MLPNotebook createNotebook(MLPNotebook notebook) {
		return this.notebook;
	}

	@Override
	public void updateNotebook(MLPNotebook notebook) {
		this.notebook = notebook;
	}

	@Override
	public void deleteNotebook(String notebookId) {
		this.notebook = null;
	}

	public void setPipelines(RestPageResponse<MLPPipeline> results) {
		this.pipelines = results;
	}

	@Override
	public RestPageResponse<MLPPipeline> getPipelines(RestPageRequest pageRequest) {
		return pipelines;
	}

	public void setSearchPipelines(RestPageResponse<MLPPipeline> results) {
		this.searchPipelines = results;
	}

	@Override
	public RestPageResponse<MLPPipeline> searchPipelines(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		return this.searchPipelines;
	}

	public void setPipelineById(MLPPipeline object) {
		this.pipelineById = object;
	}

	@Override
	public MLPPipeline getPipeline(String pipelineId) {
		return this.pipelineById;
	}

	public void setPipeline(MLPPipeline object) {
		this.pipeline = object;
	}

	@Override
	public MLPPipeline createPipeline(MLPPipeline pipeline) {
		return this.pipeline;
	}

	@Override
	public void updatePipeline(MLPPipeline pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public void deletePipeline(String pipelineId) {
		this.pipeline = null;
	}

	@Override
	public void addProjectNotebook(String projectId, String notebookId) {
		// How to mock?
	}

	@Override
	public void dropProjectNotebook(String projectId, String notebookId) {
		// How to mock?
	}

	@Override
	public void addProjectPipeline(String projectId, String pipelineId) {
		// How to mock?
	}

	@Override
	public void dropProjectPipeline(String projectId, String pipelineId) {
		// How to mock?
	}

	public void setPeerAccessCatalogIds(List<String> ids) {
		this.peerAccessCatalogIds = ids;
	}

	@Override
	public List<String> getPeerAccessCatalogIds(String peerId) {
		return this.peerAccessCatalogIds;
	}

	@Override
	public void addPeerAccessCatalog(String peerId, String catalogId) {
		// How to mock?
	}

	@Override
	public void dropPeerAccessCatalog(String peerId, String catalogId) {
		// How to mock?
	}

	public void setUserFavoriteCatalogIds(List<String> ids) {
		this.userFavoriteCatalogIds = ids;
	}

	@Override
	public List<String> getUserFavoriteCatalogIds(String userId) {
		return this.userFavoriteCatalogIds;
	}

	@Override
	public void addUserFavoriteCatalog(String userId, String catalogId) {
		// How to mock?
	}

	@Override
	public void dropUserFavoriteCatalog(String userId, String catalogId) {
		// How to mock?
	}

}
