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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.PublishRequestStatusCode;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
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
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPRtuUserMap;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
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
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPUserRoleMap;
import org.acumos.cds.logging.AcumosLogConstants;
import org.acumos.cds.transport.CountTransport;
import org.acumos.cds.transport.LoginTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.transport.UsersRoleRequest;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * <P>
 * Provides methods for accessing the Common Data Service API via REST. Supports
 * basic HTTP authentication. Clients should use the one of the getInstance
 * methods; e.g., {@link #getInstance(String, String, String)}.
 * </P>
 *
 * <P>
 * The server sets an HTTP error code on a bad request or failure and returns
 * the details to the client. On receiving a non-200-class response, the Spring
 * RestTemplate throws
 * {@link org.springframework.web.client.HttpStatusCodeException}. Clients
 * should catch that exception and fetch error details by calling that class's
 * getResponseBodyAsString() method.
 * </P>
 */
public class CommonDataServiceRestClientImpl implements ICommonDataServiceRestClient {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Base URL of the server
	 */
	private final String baseUrl;
	/**
	 * Spring REST template is constructed once and used repeatedly.
	 */
	private final RestTemplate restTemplate;
	/**
	 * Request ID optionally set by client to send to server.
	 */
	private String requestId;

	/**
	 * Intercepts requests sent via the RestTemplate used in this implementation.
	 */
	private class CDSClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
		/**
		 * Adds headers with values set by user:
		 * <UL>
		 * <LI>X-Request-ID with user value; adds a generated value if no value is set.
		 * </UL>
		 */
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			request.getHeaders().add(AcumosLogConstants.Headers.REQUEST_ID,
					requestId == null ? generateRequestId() : requestId);
			return execution.execute(request, body);
		}

		private final Random random = new Random();

		/**
		 * Generates a request ID. <BR>
		 * https://blog.bandwidth.com/a-recipe-for-adding-correlation-ids-in-java-microservices/
		 * 
		 * @return Base-62 encoded random long value.
		 */
		private String generateRequestId() {
			long randomNum = random.nextLong();
			return encodeBase62(randomNum);
		}

		private static final String BASE_62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

		/**
		 * Encodes the given Long in base 62. <BR>
		 * https://blog.bandwidth.com/a-recipe-for-adding-correlation-ids-in-java-microservices/
		 * 
		 * @param n
		 *              Number to encode
		 * @return Long encoded as base 62
		 */
		private String encodeBase62(long n) {
			StringBuilder builder = new StringBuilder();
			// NOTE: Appending builds a reverse encoded string. The most significant value
			// is at the end of the string. You could prepend(insert) but appending
			// is slightly better performance and order doesn't matter here.
			// perform the first selection using unsigned ops to get negative
			// numbers down into positive signed range.
			long index = Long.remainderUnsigned(n, 62);
			builder.append(BASE_62_CHARS.charAt((int) index));
			n = Long.divideUnsigned(n, 62);
			// now the long is unsigned, can just do regular math ops
			while (n > 0) {
				builder.append(BASE_62_CHARS.charAt((int) (n % 62)));
				n /= 62;
			}
			return builder.toString();
		}
	}

	/**
	 * Creates an instance to access the remote endpoint using the specified
	 * credentials.
	 * 
	 * If user and pass are both supplied, uses basic HTTP authentication; if either
	 * one is missing, no authentication is used.
	 * 
	 * Clients should use the static method
	 * {@link #getInstance(String, String, String, String)} instead of this
	 * constructor.
	 * 
	 * @param webapiUrl
	 *                      URL of the web endpoint with hostname and port
	 * @param user
	 *                      user name; ignored if null
	 * @param pass
	 *                      password; ignored if null
	 * @param proxyUrl
	 *                      URL of the proxy with hostname and port; ignored if null
	 */
	public CommonDataServiceRestClientImpl(final String webapiUrl, final String user, final String pass,
			final String proxyUrl) {
		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		// Validate the URLs
		URL url = null;
		try {
			url = new URL(webapiUrl);
			baseUrl = url.toExternalForm();
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("Failed to parse URL: " + webapiUrl, ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
		HttpHost proxyHost = null;
		if (proxyUrl != null) {
			try {
				url = new URL(proxyUrl);
			} catch (MalformedURLException ex) {
				throw new IllegalArgumentException("Failed to parse URL: " + proxyUrl, ex);
			}
			proxyHost = new HttpHost(url.getHost(), url.getPort());
		}
		// Build a client with a credentials provider
		HttpClientBuilder builder = HttpClientBuilder.create();
		if (user != null && pass != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(user, pass));
			builder.setDefaultCredentialsProvider(credsProvider);
		}
		// Add proxy if supplied
		if (proxyHost != null)
			builder.setProxy(proxyHost);
		CloseableHttpClient httpClient = builder.build();
		// Create request factory with the client
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);

		// Put the factory in the template
		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
		// Add request interceptor
		restTemplate.getInterceptors().add(new CDSClientHttpRequestInterceptor());
	}

	/**
	 * Creates an instance to access the remote endpoint using the specified
	 * template, which allows HTTP credentials, proxy, choice of route, etc.
	 * 
	 * Clients should use the static method
	 * {@link #getInstance(String, RestTemplate)} instead of this constructor.
	 * 
	 * @param webapiUrl
	 *                         URL of the web endpoint
	 * @param restTemplate
	 *                         REST template to use for connections
	 */
	public CommonDataServiceRestClientImpl(final String webapiUrl, final RestTemplate restTemplate) {
		if (webapiUrl == null || restTemplate == null)
			throw new IllegalArgumentException("Null not permitted");
		URL url = null;
		try {
			url = new URL(webapiUrl);
			baseUrl = url.toExternalForm();
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("Failed to parse URL", ex);
		}
		this.restTemplate = restTemplate;
	}

	/**
	 * Gets an instance to access a remote endpoint using the specified URL and
	 * credentials. This factory method should be used instead of a constructor.
	 * 
	 * @param webapiUrl
	 *                      URL of the web endpoint with host and port
	 * @param user
	 *                      user name; ignored if null
	 * @param pass
	 *                      password; ignored if null
	 * @return Instance of ICommonDataServiceRestClient
	 */
	public static ICommonDataServiceRestClient getInstance(String webapiUrl, String user, String pass) {
		return new CommonDataServiceRestClientImpl(webapiUrl, user, pass, null);
	}

	/**
	 * Gets an instance to access a remote endpoint using the specified URL,
	 * credentials and proxy. This factory method should be used instead of a
	 * constructor.
	 * 
	 * @param webapiUrl
	 *                      URL of the web endpoint with host and port
	 * @param user
	 *                      user name; ignored if null
	 * @param pass
	 *                      password; ignored if null
	 * @param proxyUrl
	 *                      URL of the proxy with hostname and port
	 * @return Instance of ICommonDataServiceRestClient
	 */
	public static ICommonDataServiceRestClient getInstance(String webapiUrl, String user, String pass,
			String proxyUrl) {
		return new CommonDataServiceRestClientImpl(webapiUrl, user, pass, proxyUrl);
	}

	/**
	 * Gets an instance to access a remote endpoint using the specified template.
	 * This factory method should be used instead of a constructor.
	 * 
	 * @param webapiUrl
	 *                         URL of the web endpoint with host and port
	 * @param restTemplate
	 *                         REST template
	 * @return Instance of ICommonDataServiceRestClient
	 */
	public static ICommonDataServiceRestClient getInstance(String webapiUrl, RestTemplate restTemplate) {
		return new CommonDataServiceRestClientImpl(webapiUrl, restTemplate);
	}

	/**
	 * Privileged access for subclasses.
	 * 
	 * @return RestTemplate configured for access to remote CDS server.
	 */
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	/**
	 * Builds URI by adding specified path segments and query parameters to the base
	 * URL. Converts an array of values to a series of parameters with the same
	 * name; e.g., "find foo in list [a,b]" becomes request parameters
	 * "foo=a&amp;foo=b".
	 * 
	 * @param path
	 *                        Array of path segments
	 * @param queryParams
	 *                        key-value pairs; ignored if null or empty. Gives
	 *                        special treatment to Date-type values, Array values,
	 *                        and null values inside arrays.
	 * @param pageRequest
	 *                        page, size and sort specification; ignored if null.
	 * @return URI with the specified path segments and query parameters
	 */
	protected URI buildUri(final String[] path, final Map<String, Object> queryParams, RestPageRequest pageRequest) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl);
		for (int p = 0; p < path.length; ++p) {
			if (path[p] == null)
				throw new IllegalArgumentException("Unexpected null at index " + Integer.toString(p));
			builder.pathSegment(path[p]);
		}
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
				if (entry.getValue() instanceof Instant) {
					// Server expects point-in-time as Long (not String)
					builder.queryParam(entry.getKey(), ((Instant) entry.getValue()).toEpochMilli());
				} else if (entry.getValue().getClass().isArray()) {
					Object[] array = (Object[]) entry.getValue();
					for (Object o : array) {
						if (o == null)
							builder.queryParam(entry.getKey(), "null");
						else if (o instanceof Instant)
							builder.queryParam(entry.getKey(), ((Instant) o).toEpochMilli());
						else
							builder.queryParam(entry.getKey(), o.toString());
					}
				} else {
					builder.queryParam(entry.getKey(), entry.getValue().toString());
				}
			}
		}
		if (pageRequest != null) {
			if (pageRequest.getSize() != null)
				builder.queryParam("page", Integer.toString(pageRequest.getPage()));
			if (pageRequest.getPage() != null)
				builder.queryParam("size", Integer.toString(pageRequest.getSize()));
			if (pageRequest.getFieldToDirectionMap() != null && pageRequest.getFieldToDirectionMap().size() > 0) {
				for (Map.Entry<String, String> entry : pageRequest.getFieldToDirectionMap().entrySet()) {
					String value = entry.getKey() + (entry.getValue() == null ? "" : ("," + entry.getValue()));
					builder.queryParam("sort", value);
				}
			}
		}
		return builder.build().encode().toUri();
	}

	@Override
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public SuccessTransport getHealth() {
		URI uri = buildUri(new String[] { CCDSConstants.HEALTHCHECK_PATH }, null, null);
		logger.debug("getHealth: uri {}", uri);
		ResponseEntity<SuccessTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<SuccessTransport>() {
				});
		return response.getBody();
	}

	@Override
	public SuccessTransport getVersion() {
		URI uri = buildUri(new String[] { CCDSConstants.VERSION_PATH }, null, null);
		logger.debug("getVersion: uri {}", uri);
		ResponseEntity<SuccessTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<SuccessTransport>() {
				});
		return response.getBody();
	}

	@Override
	public List<String> getValueSetNames() {
		URI uri = buildUri(new String[] { CCDSConstants.CODE_PATH, CCDSConstants.PAIR_PATH }, null, null);
		logger.debug("getValueSetNames: uri {}", uri);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPCodeNamePair> getCodeNamePairs(CodeNameType valueSetName) {
		URI uri = buildUri(new String[] { CCDSConstants.CODE_PATH, CCDSConstants.PAIR_PATH, valueSetName.name() }, null,
				null);
		logger.debug("getCodeNamePairs: uri {}", uri);
		ResponseEntity<List<MLPCodeNamePair>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPCodeNamePair>>() {
				});
		return response.getBody();
	}

	@Override
	public long getSolutionCount() {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getSolutionCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutions(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH }, null, pageRequest);
		logger.debug("getSolutions: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		parms.put(CCDSConstants.TERM_PATH, searchTerm);
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.SEARCH_PATH, CCDSConstants.LIKE_PATH }, parms,
				pageRequest);
		logger.debug("findSolutionsBySearchTerm: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsByTag(String tag, RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		parms.put(CCDSConstants.TAG_PATH, tag);
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.SEARCH_PATH, CCDSConstants.TAG_PATH }, parms,
				pageRequest);
		logger.debug("findSolutionsByTag: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String[] userIds, String[] modelTypeCodes, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		// This is required
		parms.put(CCDSConstants.SEARCH_ACTIVE, active);
		if (nameKeywords != null && nameKeywords.length > 0)
			parms.put(CCDSConstants.SEARCH_NAME, nameKeywords);
		if (descriptionKeywords != null && descriptionKeywords.length > 0)
			parms.put(CCDSConstants.SEARCH_DESC, descriptionKeywords);
		if (userIds != null && userIds.length > 0)
			parms.put(CCDSConstants.SEARCH_USERS, userIds);
		if (modelTypeCodes != null && modelTypeCodes.length > 0)
			parms.put(CCDSConstants.SEARCH_MODEL_TYPES, modelTypeCodes);
		if (tags != null && tags.length > 0)
			parms.put(CCDSConstants.SEARCH_TAGS, tags);
		if (authorKeywords != null && authorKeywords.length > 0)
			parms.put(CCDSConstants.SEARCH_AUTH, authorKeywords);
		if (publisherKeywords != null && publisherKeywords.length > 0)
			parms.put(CCDSConstants.SEARCH_PUB, publisherKeywords);
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.SEARCH_PATH, CCDSConstants.PORTAL_PATH },
				parms, pageRequest);
		logger.debug("findPortalSolutions: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolution> findPublishedSolutionsByKwAndTags(String[] keywords, boolean active,
			String[] userIds, String[] modelTypeCodes, String[] allTags, String[] anyTags, String[] catalogIds,
			RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		// This is the only required parameter.
		parms.put(CCDSConstants.SEARCH_ACTIVE, active);
		if (keywords != null && keywords.length > 0)
			parms.put(CCDSConstants.SEARCH_KW, keywords);
		if (userIds != null && userIds.length > 0)
			parms.put(CCDSConstants.SEARCH_USERS, userIds);
		if (modelTypeCodes != null && modelTypeCodes.length > 0)
			parms.put(CCDSConstants.SEARCH_MODEL_TYPES, modelTypeCodes);
		if (allTags != null && allTags.length > 0)
			parms.put(CCDSConstants.SEARCH_ALL_TAGS, allTags);
		if (anyTags != null && anyTags.length > 0)
			parms.put(CCDSConstants.SEARCH_ANY_TAGS, anyTags);
		if (catalogIds != null && catalogIds.length > 0)
			parms.put(CCDSConstants.SEARCH_CATALOG, catalogIds);
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.SEARCH_PATH,
				CCDSConstants.PORTAL_PATH, CCDSConstants.KW_TAG_PATH }, parms, pageRequest);
		logger.debug("findPortalSolutionsByKwAndTags: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolution> findUserSolutions(boolean active, boolean published, String userId,
			String[] nameKeywords, String[] descriptionKeywords, String[] modelTypeCodes, String[] tags,
			RestPageRequest pageRequest) {
		if (userId == null || userId.length() == 0)
			throw new IllegalArgumentException("userId argument is required");
		HashMap<String, Object> parms = new HashMap<>();
		// First three are required
		parms.put(CCDSConstants.SEARCH_ACTIVE, active);
		parms.put(CCDSConstants.SEARCH_PUBLISHED, published);
		parms.put(CCDSConstants.SEARCH_USERS, userId);
		// Rest are optional
		if (nameKeywords != null && nameKeywords.length > 0)
			parms.put(CCDSConstants.SEARCH_NAME, nameKeywords);
		if (descriptionKeywords != null && descriptionKeywords.length > 0)
			parms.put(CCDSConstants.SEARCH_DESC, descriptionKeywords);
		if (modelTypeCodes != null && modelTypeCodes.length > 0)
			parms.put(CCDSConstants.SEARCH_MODEL_TYPES, modelTypeCodes);
		if (tags != null && tags.length > 0)
			parms.put(CCDSConstants.SEARCH_TAGS, tags);
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.SEARCH_PATH, CCDSConstants.USER_PATH }, parms,
				pageRequest);
		logger.debug("findUserSolutions: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolution> searchSolutions(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchSolutions: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolution getSolution(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId }, null, null);
		logger.debug("getSolution: uri {}", uri);
		ResponseEntity<MLPSolution> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPSolution>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolution createSolution(MLPSolution solution) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH }, null, null);
		logger.debug("createSolution: uri {}", uri);
		return restTemplate.postForObject(uri, solution, MLPSolution.class);
	}

	@Override
	public void updateSolution(MLPSolution solution) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solution.getSolutionId() }, null, null);
		logger.debug("updateSolution: url {}", uri);
		restTemplate.put(uri, solution);
	}

	@Override
	public void incrementSolutionViewCount(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.VIEW_PATH }, null,
				null);
		logger.debug("incrementSolutionViewCount: url {}", uri);
		restTemplate.put(uri, null);
	}

	@Override
	public void deleteSolution(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId }, null, null);
		logger.debug("deleteSolution: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String solutionId) {
		return getSolutionRevisions(new String[] { solutionId });
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String[] solutionIds) {
		// Send solution IDs as a CSV list
		String csvSolIds = String.join(",", solutionIds);
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, csvSolIds, CCDSConstants.REVISION_PATH }, null,
				null);
		logger.debug("getSolutionRevisions: uri {}", uri);
		ResponseEntity<List<MLPSolutionRevision>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPSolutionRevision>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisionsForArtifact(String artifactId) {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, artifactId, CCDSConstants.REVISION_PATH }, null,
				null);
		logger.debug("getSolutionRevisionsForArtifact: uri {}", uri);
		ResponseEntity<List<MLPSolutionRevision>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPSolutionRevision>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionRevision getSolutionRevision(String solutionId, String revisionId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.REVISION_PATH, revisionId }, null,
				null);
		logger.debug("getSolutionRevision: uri {}", uri);
		ResponseEntity<MLPSolutionRevision> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPSolutionRevision>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionRevision createSolutionRevision(MLPSolutionRevision revision) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, revision.getSolutionId(), CCDSConstants.REVISION_PATH },
				null, null);
		logger.debug("createSolutionRevision: uri {}", uri);
		return restTemplate.postForObject(uri, revision, MLPSolutionRevision.class);
	}

	@Override
	public void updateSolutionRevision(MLPSolutionRevision revision) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, revision.getSolutionId(),
				CCDSConstants.REVISION_PATH, revision.getRevisionId() }, null, null);
		logger.debug("updateSolutionRevision: uri {}", uri);
		restTemplate.put(uri, revision);
	}

	@Override
	public void deleteSolutionRevision(String solutionId, String revisionId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.REVISION_PATH, revisionId }, null,
				null);
		logger.debug("deleteSolutionRevision: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPArtifact> getSolutionRevisionArtifacts(String solutionIdIgnored, String revisionId) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.ARTIFACT_PATH }, null,
				null);
		logger.debug("getSolutionRevisionArtifacts: uri {}", uri);
		ResponseEntity<List<MLPArtifact>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPArtifact>>() {
				});
		return response.getBody();
	}

	@Override
	public void addSolutionRevisionArtifact(String solutionIdIgnored, String revisionId, String artifactId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.ARTIFACT_PATH, artifactId }, null,
				null);
		logger.debug("addSolutionRevisionArtifact: url {}", uri);
		restTemplate.postForLocation(uri, null);
	}

	@Override
	public void dropSolutionRevisionArtifact(String solutionIdIgnored, String revisionId, String artifactId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.ARTIFACT_PATH, artifactId }, null,
				null);
		logger.debug("dropSolutionRevisionArtifact: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPTag> getTags(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.TAG_PATH }, null, pageRequest);
		logger.debug("getTags: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPTag>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPTag>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPTag createTag(MLPTag tag) {
		URI uri = buildUri(new String[] { CCDSConstants.TAG_PATH }, null, null);
		logger.debug("createTag: uri {}", uri);
		return restTemplate.postForObject(uri, tag, MLPTag.class);
	}

	@Override
	public void deleteTag(MLPTag tag) {
		URI uri = buildUri(new String[] { CCDSConstants.TAG_PATH, tag.getTag() }, null, null);
		logger.debug("deleteTag: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPTag> getSolutionTags(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.TAG_PATH }, null,
				null);
		logger.debug("getSolutionTags: uri {}", uri);
		ResponseEntity<List<MLPTag>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPTag>>() {
				});
		return response.getBody();
	}

	@Override
	public void addSolutionTag(String solutionId, String tag) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.TAG_PATH, tag }, null,
				null);
		logger.debug("addSolutionTag: uri {}", uri);
		restTemplate.postForLocation(uri, null);
	}

	@Override
	public void dropSolutionTag(String solutionId, String tag) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.TAG_PATH, tag }, null,
				null);
		logger.debug("dropSolutionTag: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getArtifactCount() {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getArtifactCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPArtifact> getArtifacts(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH }, null, pageRequest);
		logger.debug("getArtifacts: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPArtifact>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPArtifact>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPArtifact> findArtifactsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		parms.put(CCDSConstants.TERM_PATH, searchTerm);
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, CCDSConstants.LIKE_PATH }, parms, pageRequest);
		logger.debug("findArtifactsBySearchTerm: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPArtifact>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPArtifact>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPArtifact> searchArtifacts(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchArtifacts: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPArtifact>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPArtifact>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPArtifact getArtifact(String artifactId) {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, artifactId }, null, null);
		logger.debug("getArtifact: uri {}", uri);
		ResponseEntity<MLPArtifact> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPArtifact>() {
				});
		return response.getBody();
	}

	@Override
	public MLPArtifact createArtifact(MLPArtifact artifact) {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH }, null, null);
		logger.debug("createArtifact: url {}", uri);
		return restTemplate.postForObject(uri, artifact, MLPArtifact.class);
	}

	@Override
	public void updateArtifact(MLPArtifact art) {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, art.getArtifactId() }, null, null);
		logger.debug("updateArtifact: uri {}", uri);
		restTemplate.put(uri, art);
	}

	@Override
	public void deleteArtifact(String artifactId) {
		URI uri = buildUri(new String[] { CCDSConstants.ARTIFACT_PATH, artifactId }, null, null);
		logger.debug("deleteArtifact: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getUserCount() {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getUserCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPUser> getUsers(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH }, null, pageRequest);
		logger.debug("getUsers: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPUser>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPUser>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPUser> findUsersBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		parms.put(CCDSConstants.TERM_PATH, searchTerm);
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.LIKE_PATH }, parms, pageRequest);
		logger.debug("findUsersBySearchTerm: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPUser>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPUser>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPUser> searchUsers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchUsers: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPUser>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPUser>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPUser loginUser(String name, String pass) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.LOGIN_PATH }, null, null);
		logger.debug("loginUser: uri {}", uri);
		LoginTransport credentials = new LoginTransport(name, pass);
		return restTemplate.postForObject(uri, credentials, MLPUser.class);
	}

	@Override
	public MLPUser loginApiUser(String name, String token) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.LOGIN_API_PATH }, null, null);
		logger.debug("loginApiUser: uri {}", uri);
		LoginTransport credentials = new LoginTransport(name, token);
		return restTemplate.postForObject(uri, credentials, MLPUser.class);
	}

	@Override
	public MLPUser verifyUser(String name, String token) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.VERIFY_PATH }, null, null);
		logger.debug("verifyUser: uri {}", uri);
		LoginTransport credentials = new LoginTransport(name, token);
		return restTemplate.postForObject(uri, credentials, MLPUser.class);
	}

	@Override
	public MLPUser getUser(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId }, null, null);
		logger.debug("getUser: uri {}", uri);
		ResponseEntity<MLPUser> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPUser>() {
				});
		return response.getBody();
	}

	@Override
	public MLPUser createUser(MLPUser solution) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH }, null, null);
		logger.debug("createUser: uri {}", uri);
		return restTemplate.postForObject(uri, solution, MLPUser.class);
	}

	@Override
	public void updateUser(MLPUser user) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, user.getUserId() }, null, null);
		logger.debug("updateUser: url {}", uri);
		restTemplate.put(uri, user);
	}

	@Override
	public void updatePassword(MLPUser user, MLPPasswordChangeRequest changeRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, user.getUserId(), CCDSConstants.CHPASS_PATH }, null,
				null);
		logger.debug("updatePassword: url {}", uri);
		restTemplate.put(uri, changeRequest);
	}

	@Override
	public void deleteUser(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId }, null, null);
		logger.debug("deleteUser: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPUserLoginProvider getUserLoginProvider(String userId, String providerCode, String providerLogin) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.LOGIN_PROVIDER_PATH,
				providerCode, CCDSConstants.LOGIN_PATH, providerLogin }, null, null);
		logger.debug("getUserLoginProvider: url {}", uri);
		ResponseEntity<MLPUserLoginProvider> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPUserLoginProvider>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPUserLoginProvider> getUserLoginProviders(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.LOGIN_PROVIDER_PATH }, null,
				null);
		logger.debug("getUserLoginProviders: url {}", uri);
		ResponseEntity<List<MLPUserLoginProvider>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPUserLoginProvider>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPUserLoginProvider createUserLoginProvider(MLPUserLoginProvider provider) {
		URI uri = buildUri(
				new String[] { CCDSConstants.USER_PATH, provider.getUserId(), CCDSConstants.LOGIN_PROVIDER_PATH,
						provider.getProviderCode(), CCDSConstants.LOGIN_PATH, provider.getProviderUserId() },
				null, null);
		logger.debug("createUserLoginProvider: url {}", uri);
		return restTemplate.postForObject(uri, provider, MLPUserLoginProvider.class);
	}

	@Override
	public void updateUserLoginProvider(MLPUserLoginProvider provider) {
		URI uri = buildUri(
				new String[] { CCDSConstants.USER_PATH, provider.getUserId(), CCDSConstants.LOGIN_PROVIDER_PATH,
						provider.getProviderCode(), CCDSConstants.LOGIN_PATH, provider.getProviderUserId() },
				null, null);
		logger.debug("updateUserLoginProvider: url {}", uri);
		restTemplate.put(uri, provider);
	}

	@Override
	public void deleteUserLoginProvider(MLPUserLoginProvider provider) {
		URI uri = buildUri(
				new String[] { CCDSConstants.USER_PATH, provider.getUserId(), CCDSConstants.LOGIN_PROVIDER_PATH,
						provider.getProviderCode(), CCDSConstants.LOGIN_PATH, provider.getProviderUserId() },
				null, null);
		logger.debug("deleteUserLoginProvider: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPRole> getRoles(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH }, null, pageRequest);
		logger.debug("getRoles: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPRole>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPRole>>() {
				});
		return response.getBody();
	}

	@Override
	public long getRoleCount() {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getRoleCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPRole> searchRoles(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchRoles: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPRole>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPRole>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPRole> getUserRoles(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.ROLE_PATH }, null, null);
		logger.debug("getUserRoles: uri {}", uri);
		ResponseEntity<List<MLPRole>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPRole>>() {
				});
		return response.getBody();
	}

	@Override
	public void addUserRole(String userId, String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.ROLE_PATH, roleId }, null,
				null);
		logger.debug("addUserRole: uri {}", uri);
		MLPUserRoleMap map = new MLPUserRoleMap(userId, roleId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void updateUserRoles(String userId, List<String> roleIds) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.ROLE_PATH }, null, null);
		logger.debug("updateUserRoles: uri {}", uri);
		restTemplate.put(uri, roleIds);
	}

	@Override
	public void dropUserRole(String userId, String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.ROLE_PATH, roleId }, null,
				null);
		logger.debug("dropUserRole: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public void addUsersInRole(List<String> userIds, String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.ROLE_PATH, roleId }, null, null);
		logger.debug("addUsersInRole: uri {}", uri);
		UsersRoleRequest request = new UsersRoleRequest(true, userIds, roleId);
		restTemplate.put(uri, request);
	}

	@Override
	public void dropUsersInRole(List<String> userIds, String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, CCDSConstants.ROLE_PATH, roleId }, null, null);
		logger.debug("dropUsersInRole: uri {}", uri);
		UsersRoleRequest request = new UsersRoleRequest(false, userIds, roleId);
		restTemplate.put(uri, request);
	}

	@Override
	public long getRoleUsersCount(String roleId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.USER_PATH, CCDSConstants.ROLE_PATH, roleId, CCDSConstants.COUNT_PATH },
				null, null);
		logger.debug("getRoleUsersCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public MLPRole getRole(String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, roleId }, null, null);
		logger.debug("getRole: uri {}", uri);
		ResponseEntity<MLPRole> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPRole>() {
				});
		return response.getBody();
	}

	@Override
	public MLPRole createRole(MLPRole role) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH }, null, null);
		logger.debug("createRole: uri {}", uri);
		return restTemplate.postForObject(uri, role, MLPRole.class);
	}

	@Override
	public void updateRole(MLPRole role) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, role.getRoleId() }, null, null);
		logger.debug("updateRole: url {}", uri);
		restTemplate.put(uri, role);
	}

	@Override
	public void deleteRole(String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, roleId }, null, null);
		logger.debug("deleteRole: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPRoleFunction> getRoleFunctions(String roleId) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, roleId, CCDSConstants.FUNCTION_PATH }, null, null);
		logger.debug("getRoleFunctions: uri {}", uri);
		ResponseEntity<List<MLPRoleFunction>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPRoleFunction>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPRoleFunction getRoleFunction(String roleId, String roleFunctionId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.ROLE_PATH, roleId, CCDSConstants.FUNCTION_PATH, roleFunctionId }, null,
				null);
		logger.debug("getRoleFunction: uri {}", uri);
		ResponseEntity<MLPRoleFunction> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPRoleFunction>() {
				});
		return response.getBody();
	}

	@Override
	public MLPRoleFunction createRoleFunction(MLPRoleFunction roleFunction) {
		URI uri = buildUri(
				new String[] { CCDSConstants.ROLE_PATH, roleFunction.getRoleId(), CCDSConstants.FUNCTION_PATH }, null,
				null);
		logger.debug("createRoleFunction: uri {}", uri);
		return restTemplate.postForObject(uri, roleFunction, MLPRoleFunction.class);
	}

	@Override
	public void updateRoleFunction(MLPRoleFunction roleFunction) {
		URI uri = buildUri(new String[] { CCDSConstants.ROLE_PATH, roleFunction.getRoleId(),
				CCDSConstants.FUNCTION_PATH, roleFunction.getRoleFunctionId() }, null, null);
		logger.debug("updateRoleFunction: uri {}", uri);
		restTemplate.put(uri, roleFunction);
	}

	@Override
	public void deleteRoleFunction(String roleId, String roleFunctionId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.ROLE_PATH, roleId, CCDSConstants.FUNCTION_PATH, roleFunctionId }, null,
				null);
		logger.debug("deleteRoleFunction: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPPeer> getPeers(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH }, null, pageRequest);
		logger.debug("getPeers: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPPeer>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPPeer>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPPeer> searchPeers(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchPeers: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPPeer>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPPeer>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPPeer getPeer(String peerId) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, peerId }, null, null);
		logger.debug("getPeer: uri {}", uri);
		ResponseEntity<MLPPeer> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPPeer>() {
				});
		return response.getBody();
	}

	@Override
	public MLPPeer createPeer(MLPPeer solution) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH }, null, null);
		logger.debug("createPeer: uri {}", uri);
		return restTemplate.postForObject(uri, solution, MLPPeer.class);
	}

	@Override
	public void updatePeer(MLPPeer peer) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, peer.getPeerId() }, null, null);
		logger.debug("updatePeer: url {}", uri);
		restTemplate.put(uri, peer);
	}

	@Override
	public void deletePeer(String peerId) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, peerId }, null, null);
		logger.debug("deletePeer: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPSolutionDownload> getSolutionDownloads(String solutionId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.DOWNLOAD_PATH }, null,
				pageRequest);
		logger.debug("getSolutionDownloads: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolutionDownload>> response = restTemplate.exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<RestPageResponse<MLPSolutionDownload>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionDownload createSolutionDownload(MLPSolutionDownload download) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, download.getSolutionId(),
				CCDSConstants.DOWNLOAD_PATH, CCDSConstants.ARTIFACT_PATH, download.getArtifactId(),
				CCDSConstants.USER_PATH, download.getUserId(), }, null, null);
		logger.debug("createSolutionDownload: uri {}", uri);
		return restTemplate.postForObject(uri, download, MLPSolutionDownload.class);
	}

	@Override
	public void deleteSolutionDownload(MLPSolutionDownload sd) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, sd.getSolutionId(), CCDSConstants.DOWNLOAD_PATH,
				Long.toString(sd.getDownloadId()) }, null, null);
		logger.debug("deleteSolutionDownload: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPSolution> getFavoriteSolutions(String userId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.FAVORITE_PATH,
				CCDSConstants.SOLUTION_PATH }, null, pageRequest);
		logger.debug("getFavoriteSolutions: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionFavorite createSolutionFavorite(MLPSolutionFavorite solfav) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, solfav.getUserId(), CCDSConstants.FAVORITE_PATH,
				CCDSConstants.SOLUTION_PATH, solfav.getSolutionId() }, null, null);
		logger.debug("createSolutionFavorite: uri {}", uri);
		return restTemplate.postForObject(uri, solfav, MLPSolutionFavorite.class);
	}

	@Override
	public void deleteSolutionFavorite(MLPSolutionFavorite solfav) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, solfav.getUserId(), CCDSConstants.FAVORITE_PATH,
				CCDSConstants.SOLUTION_PATH, solfav.getSolutionId() }, null, null);
		logger.debug("deleteSolutionFavorite: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPSolutionRating> getSolutionRatings(String solutionId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.RATING_PATH }, null,
				pageRequest);
		logger.debug("getSolutionRatings: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolutionRating>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolutionRating>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionRating getSolutionRating(String solutionId, String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.RATING_PATH,
				CCDSConstants.USER_PATH, userId }, null, null);
		logger.debug("getSolutionRating: uri {}", uri);
		ResponseEntity<MLPSolutionRating> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPSolutionRating>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionRating createSolutionRating(MLPSolutionRating rating) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, rating.getSolutionId(),
				CCDSConstants.RATING_PATH, CCDSConstants.USER_PATH, rating.getUserId() }, null, null);
		logger.debug("createSolutionRating: uri {}", uri);
		return restTemplate.postForObject(uri, rating, MLPSolutionRating.class);
	}

	@Override
	public void updateSolutionRating(MLPSolutionRating rating) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, rating.getSolutionId(),
				CCDSConstants.RATING_PATH, CCDSConstants.USER_PATH, rating.getUserId() }, null, null);
		logger.debug("updateSolutionRating: url {}", uri);
		restTemplate.put(uri, rating);
	}

	@Override
	public void deleteSolutionRating(MLPSolutionRating rating) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, rating.getSolutionId(),
				CCDSConstants.RATING_PATH, CCDSConstants.USER_PATH, rating.getUserId() }, null, null);
		logger.debug("deleteSolutionRating: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getPeerSubscriptionCount(String peerId) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, peerId, CCDSConstants.SUBSCRIPTION_PATH,
				CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getPeerSubscriptionCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public List<MLPPeerSubscription> getPeerSubscriptions(String peerId) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, peerId, CCDSConstants.SUBSCRIPTION_PATH }, null,
				null);
		logger.debug("getPeerSubscriptions: uri {}", uri);
		ResponseEntity<List<MLPPeerSubscription>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPPeerSubscription>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPPeerSubscription getPeerSubscription(Long subscriptionId) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, CCDSConstants.SUBSCRIPTION_PATH,
				Long.toString(subscriptionId) }, null, null);
		logger.debug("getPeerSubscription: uri {}", uri);
		ResponseEntity<MLPPeerSubscription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPPeerSubscription>() {
				});
		return response.getBody();
	}

	@Override
	public MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, CCDSConstants.SUBSCRIPTION_PATH }, null, null);
		logger.debug("createPeerSubscription: uri {}", uri);
		return restTemplate.postForObject(uri, peerSub, MLPPeerSubscription.class);
	}

	@Override
	public void updatePeerSubscription(MLPPeerSubscription peerSub) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, CCDSConstants.SUBSCRIPTION_PATH,
				Long.toString(peerSub.getSubId()) }, null, null);
		logger.debug("updatePeerSubscription: url {}", uri);
		restTemplate.put(uri, peerSub);
	}

	@Override
	public void deletePeerSubscription(Long subscriptionId) {
		URI uri = buildUri(new String[] { CCDSConstants.PEER_PATH, CCDSConstants.SUBSCRIPTION_PATH,
				Long.toString(subscriptionId) }, null, null);
		logger.debug("deletePeerSubscription: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getNotificationCount() {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getNotificationCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPNotification> getNotifications(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH }, null, pageRequest);
		logger.debug("getNotifications: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPNotification>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPNotification>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPNotification createNotification(MLPNotification notification) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH }, null, null);
		logger.debug("createNotification: uri {}", uri);
		return restTemplate.postForObject(uri, notification, MLPNotification.class);
	}

	@Override
	public void updateNotification(MLPNotification notification) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, notification.getNotificationId() }, null,
				null);
		logger.debug("updateNotification: url {}", uri);
		restTemplate.put(uri, notification);
	}

	@Override
	public void deleteNotification(String notificationId) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, notificationId }, null, null);
		logger.debug("deleteNotification: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getUserUnreadNotificationCount(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.USER_PATH, userId,
				CCDSConstants.UNREAD_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getUserUnreadNotificationCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPUserNotification> getUserNotifications(String userId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.USER_PATH, userId }, null,
				pageRequest);
		logger.debug("getUserNotifications: url {}", uri);
		ResponseEntity<RestPageResponse<MLPUserNotification>> response = restTemplate.exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<RestPageResponse<MLPUserNotification>>() {
				});
		return response.getBody();
	}

	@Override
	public void addUserToNotification(String notificationId, String userId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.NOTIFICATION_PATH, notificationId, CCDSConstants.USER_PATH, userId }, null,
				null);
		logger.debug("addNotificationUser: url {}", uri);
		MLPNotifUserMap map = new MLPNotifUserMap(notificationId, userId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropUserFromNotification(String notificationId, String userId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.NOTIFICATION_PATH, notificationId, CCDSConstants.USER_PATH, userId }, null,
				null);
		logger.debug("dropNotificationUser: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public void setUserViewedNotification(String notificationId, String userId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.NOTIFICATION_PATH, notificationId, CCDSConstants.USER_PATH, userId }, null,
				null);
		logger.debug("addNotificationUser: url {}", uri);
		MLPNotifUserMap map = new MLPNotifUserMap(notificationId, userId);
		map.setViewed(Instant.now());
		restTemplate.put(uri, map);
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserDeployments(String userId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.DEPLOY_PATH }, null,
				pageRequest);
		logger.debug("getUserDeployments: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolutionDeployment>> response = restTemplate.exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<RestPageResponse<MLPSolutionDeployment>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getSolutionDeployments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.REVISION_PATH,
				revisionId, CCDSConstants.DEPLOY_PATH }, null, pageRequest);
		logger.debug("getSolutionDeployments: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolutionDeployment>> response = restTemplate.exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<RestPageResponse<MLPSolutionDeployment>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserSolutionDeployments(String solutionId, String revisionId,
			String userId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.REVISION_PATH,
				revisionId, CCDSConstants.USER_PATH, userId, CCDSConstants.DEPLOY_PATH }, null, pageRequest);
		logger.debug("getUserSolutionDeployments: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolutionDeployment>> response = restTemplate.exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<RestPageResponse<MLPSolutionDeployment>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSolutionDeployment createSolutionDeployment(MLPSolutionDeployment deployment) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, deployment.getSolutionId(),
				CCDSConstants.REVISION_PATH, deployment.getRevisionId(), CCDSConstants.DEPLOY_PATH }, null, null);
		logger.debug("createSolutionDeployment: uri {}", uri);
		return restTemplate.postForObject(uri, deployment, MLPSolutionDeployment.class);
	}

	@Override
	public void updateSolutionDeployment(MLPSolutionDeployment deployment) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, deployment.getSolutionId(), CCDSConstants.REVISION_PATH,
						deployment.getRevisionId(), CCDSConstants.DEPLOY_PATH, deployment.getDeploymentId() },
				null, null);
		logger.debug("updateSolutionDeployment: url {}", uri);
		restTemplate.put(uri, deployment);
	}

	@Override
	public void deleteSolutionDeployment(MLPSolutionDeployment deployment) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, deployment.getSolutionId(), CCDSConstants.REVISION_PATH,
						deployment.getRevisionId(), CCDSConstants.DEPLOY_PATH, deployment.getDeploymentId() },
				null, null);
		logger.debug("deleteSolutionDeployment: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPSiteConfig> getSiteConfigs(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONFIG_PATH }, null, null);
		logger.debug("getSiteConfigs: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSiteConfig>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSiteConfig>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSiteConfig getSiteConfig(String configKey) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONFIG_PATH, configKey }, null, null);
		logger.debug("getSiteConfig: uri {}", uri);
		ResponseEntity<MLPSiteConfig> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPSiteConfig>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSiteConfig createSiteConfig(MLPSiteConfig config) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONFIG_PATH }, null, null);
		logger.debug("createSiteConfig: uri {}", uri);
		return restTemplate.postForObject(uri, config, MLPSiteConfig.class);
	}

	@Override
	public void updateSiteConfig(MLPSiteConfig config) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONFIG_PATH, config.getConfigKey() },
				null, null);
		logger.debug("updateSiteConfig: url {}", uri);
		restTemplate.put(uri, config);
	}

	@Override
	public void deleteSiteConfig(String configKey) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONFIG_PATH, configKey }, null, null);
		logger.debug("deleteSiteConfig: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPSiteContent> getSiteContents(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONTENT_PATH }, null, null);
		logger.debug("getSiteContents: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSiteContent>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSiteContent>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSiteContent getSiteContent(String contentKey) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONTENT_PATH, contentKey }, null,
				null);
		logger.debug("getSiteContent: uri {}", uri);
		ResponseEntity<MLPSiteContent> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPSiteContent>() {
				});
		return response.getBody();
	}

	@Override
	public MLPSiteContent createSiteContent(MLPSiteContent config) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONTENT_PATH }, null, null);
		logger.debug("createSiteContent: uri {}", uri);
		return restTemplate.postForObject(uri, config, MLPSiteContent.class);
	}

	@Override
	public void updateSiteContent(MLPSiteContent config) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONTENT_PATH, config.getContentKey() },
				null, null);
		logger.debug("updateSiteContent: url {}", uri);
		restTemplate.put(uri, config);
	}

	@Override
	public void deleteSiteContent(String contentKey) {
		URI uri = buildUri(new String[] { CCDSConstants.SITE_PATH, CCDSConstants.CONTENT_PATH, contentKey }, null,
				null);
		logger.debug("deleteSiteContent: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getThreadCount() {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getThreadCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPThread> getThreads(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH }, null, pageRequest);
		logger.debug("getThreads: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPThread>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPThread>>() {
				});
		return response.getBody();
	}

	@Override
	public long getSolutionRevisionThreadCount(String solutionId, String revisionId) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getSolutionRevisionThreadCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPThread> getSolutionRevisionThreads(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.REVISION_PATH, revisionId }, null, pageRequest);
		logger.debug("getSolutionRevisionThreads: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPThread>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPThread>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPThread getThread(String threadId) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, threadId }, null, null);
		logger.debug("getThread: uri {}", uri);
		ResponseEntity<MLPThread> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPThread>() {
				});
		return response.getBody();
	}

	@Override
	public MLPThread createThread(MLPThread thread) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH }, null, null);
		logger.debug("createThread: uri {}", uri);
		return restTemplate.postForObject(uri, thread, MLPThread.class);
	}

	@Override
	public void updateThread(MLPThread thread) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, thread.getThreadId() }, null, null);
		logger.debug("updateThread: url {}", uri);
		restTemplate.put(uri, thread);
	}

	@Override
	public void deleteThread(String threadId) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, threadId }, null, null);
		logger.debug("deleteThread: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getThreadCommentCount(String threadId) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, threadId, CCDSConstants.COMMENT_PATH,
				CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getCommentCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPComment> getThreadComments(String threadId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, threadId, CCDSConstants.COMMENT_PATH }, null,
				pageRequest);
		logger.debug("getThreadComments: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPComment>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPComment>>() {
				});
		return response.getBody();
	}

	@Override
	public long getSolutionRevisionCommentCount(String solutionId, String revisionId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.THREAD_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
						CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.COMMENT_PATH, CCDSConstants.COUNT_PATH },
				null, null);
		logger.debug("getSolutionRevisionCommentCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPComment> getSolutionRevisionComments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.COMMENT_PATH }, null, pageRequest);
		logger.debug("getSolutionRevisionComments: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPComment>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPComment>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPComment getComment(String threadId, String commentId) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, threadId, CCDSConstants.COMMENT_PATH, commentId },
				null, null);
		logger.debug("getComment: uri {}", uri);
		ResponseEntity<MLPComment> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPComment>() {
				});
		return response.getBody();
	}

	@Override
	public MLPComment createComment(MLPComment comment) {
		URI uri = buildUri(
				new String[] { CCDSConstants.THREAD_PATH, comment.getThreadId(), CCDSConstants.COMMENT_PATH }, null,
				null);
		logger.debug("createComment: uri {}", uri);
		return restTemplate.postForObject(uri, comment, MLPComment.class);
	}

	@Override
	public void updateComment(MLPComment comment) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, comment.getThreadId(), CCDSConstants.COMMENT_PATH,
				comment.getCommentId() }, null, null);
		logger.debug("updateComment: url {}", uri);
		restTemplate.put(uri, comment);
	}

	@Override
	public void deleteComment(String threadId, String commentId) {
		URI uri = buildUri(new String[] { CCDSConstants.THREAD_PATH, threadId, CCDSConstants.COMMENT_PATH, commentId },
				null, null);
		logger.debug("deleteComment: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPTaskStepResult getTaskStepResult(long stepResultId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.TASK_PATH, CCDSConstants.STEP_RESULT_PATH, Long.toString(stepResultId) },
				null, null);
		logger.debug("getTaskStepResult: uri {}", uri);
		ResponseEntity<MLPTaskStepResult> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPTaskStepResult>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPTaskStepResult> getTaskStepResults(long taskId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.TASK_PATH, Long.toString(taskId), CCDSConstants.STEP_RESULT_PATH }, null,
				null);
		logger.debug("getTaskStepResults: uri {}", uri);
		ResponseEntity<List<MLPTaskStepResult>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPTaskStepResult>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPTaskStepResult> searchTaskStepResults(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(
				new String[] { CCDSConstants.TASK_PATH, CCDSConstants.STEP_RESULT_PATH, CCDSConstants.SEARCH_PATH },
				copy, pageRequest);
		logger.debug("searchTaskStepResults: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPTaskStepResult>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPTaskStepResult>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPTaskStepResult createTaskStepResult(MLPTaskStepResult stepResult) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH, CCDSConstants.STEP_RESULT_PATH }, null, null);
		logger.debug("addTaskStepResult: uri {}", uri);
		return restTemplate.postForObject(uri, stepResult, MLPTaskStepResult.class);
	}

	@Override
	public void updateTaskStepResult(MLPTaskStepResult stepResult) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH, CCDSConstants.STEP_RESULT_PATH,
				Long.toString(stepResult.getStepResultId()) }, null, null);
		logger.debug("updateTaskStepResult: url {}", uri);
		restTemplate.put(uri, stepResult);
	}

	@Override
	public void deleteTaskStepResult(long stepResultId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.TASK_PATH, CCDSConstants.STEP_RESULT_PATH, Long.toString(stepResultId) },
				null, null);
		logger.debug("deleteTaskStepResult: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPUserNotifPref getUserNotificationPreference(Long usrNotifPrefId) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.NOTIFICATION_PREF_PATH,
				Long.toString(usrNotifPrefId) }, null, null);
		logger.debug("getUserNotificationPreference: url {}", uri);
		ResponseEntity<MLPUserNotifPref> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPUserNotifPref>() {
				});
		return response.getBody();
	}

	@Override
	public MLPUserNotifPref createUserNotificationPreference(MLPUserNotifPref usrNotifPref) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.NOTIFICATION_PREF_PATH }, null,
				null);
		logger.debug("createUserNotificationPreference: uri {}", uri);
		return restTemplate.postForObject(uri, usrNotifPref, MLPUserNotifPref.class);
	}

	@Override
	public void updateUserNotificationPreference(MLPUserNotifPref usrNotifPref) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.NOTIFICATION_PREF_PATH,
				Long.toString(usrNotifPref.getUserNotifPrefId()) }, null, null);
		logger.debug("updateUserNotificationPreference: url {}", uri);
		restTemplate.put(uri, usrNotifPref);
	}

	@Override
	public void deleteUserNotificationPreference(Long userNotifPrefId) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.NOTIFICATION_PREF_PATH,
				Long.toString(userNotifPrefId) }, null, null);
		logger.debug("deleteUserNotificationPreference: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPUserNotifPref> getUserNotificationPreferences(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.NOTIFICATION_PATH, CCDSConstants.NOTIFICATION_PREF_PATH,
				CCDSConstants.USER_PATH, userId }, null, null);
		logger.debug("getUserNotificationPreferences: url {}", uri);
		ResponseEntity<List<MLPUserNotifPref>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPUserNotifPref>>() {
				});
		return response.getBody();
	}

	@Override
	public List<String> getCompositeSolutionMembers(String parentId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, parentId, CCDSConstants.COMPOSITE_PATH }, null,
				null);
		logger.debug("getCompositeSolutionMembers: uri {}", uri);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	@Override
	public void addCompositeSolutionMember(String parentId, String childId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, parentId, CCDSConstants.COMPOSITE_PATH, childId }, null,
				null);
		logger.debug("addCompositeSolutionMember: uri {}", uri);
		restTemplate.postForLocation(uri, null);
	}

	@Override
	public void dropCompositeSolutionMember(String parentId, String childId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.SOLUTION_PATH, parentId, CCDSConstants.COMPOSITE_PATH, childId }, null,
				null);
		logger.debug("dropCompositeSolutionMember: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPRevCatDescription getRevCatDescription(String revisionId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.CATALOG_PATH,
				catalogId, CCDSConstants.DESCRIPTION_PATH }, null, null);
		logger.debug("getRevCatDescription: uri {}", uri);
		ResponseEntity<MLPRevCatDescription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPRevCatDescription>() {
				});
		return response.getBody();
	}

	@Override
	public MLPRevCatDescription createRevCatDescription(MLPRevCatDescription description) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, description.getRevisionId(),
				CCDSConstants.CATALOG_PATH, description.getCatalogId(), CCDSConstants.DESCRIPTION_PATH }, null, null);
		logger.debug("createRevCatDescription: uri {}", uri);
		return restTemplate.postForObject(uri, description, MLPRevCatDescription.class);
	}

	@Override
	public void updateRevCatDescription(MLPRevCatDescription description) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, description.getRevisionId(),
				CCDSConstants.CATALOG_PATH, description.getCatalogId(), CCDSConstants.DESCRIPTION_PATH }, null, null);
		logger.debug("updateRevisionDescription: uri {}", uri);
		restTemplate.put(uri, description);
	}

	@Override
	public void deleteRevCatDescription(String revisionId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.CATALOG_PATH,
				catalogId, CCDSConstants.DESCRIPTION_PATH }, null, null);
		logger.debug("deleteRevCatDescription: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPDocument getDocument(String documentId) {
		URI uri = buildUri(new String[] { CCDSConstants.DOCUMENT_PATH, documentId }, null, null);
		logger.debug("getDocument: uri {}", uri);
		ResponseEntity<MLPDocument> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPDocument>() {
				});
		return response.getBody();
	}

	@Override
	public MLPDocument createDocument(MLPDocument document) {
		URI uri = buildUri(new String[] { CCDSConstants.DOCUMENT_PATH }, null, null);
		logger.debug("createDocument: url {}", uri);
		return restTemplate.postForObject(uri, document, MLPDocument.class);
	}

	@Override
	public void updateDocument(MLPDocument art) {
		URI uri = buildUri(new String[] { CCDSConstants.DOCUMENT_PATH, art.getDocumentId() }, null, null);
		logger.debug("updateDocument: uri {}", uri);
		restTemplate.put(uri, art);
	}

	@Override
	public void deleteDocument(String documentId) {
		URI uri = buildUri(new String[] { CCDSConstants.DOCUMENT_PATH, documentId }, null, null);
		logger.debug("deleteDocument: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPDocument> getRevisionCatalogDocuments(String revisionId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.CATALOG_PATH,
				catalogId, CCDSConstants.DOCUMENT_PATH }, null, null);
		logger.debug("getRevisionCatalogDocuments: uri {}", uri);
		ResponseEntity<List<MLPDocument>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPDocument>>() {
				});
		return response.getBody();
	}

	@Override
	public void addRevisionCatalogDocument(String revisionId, String catalogId, String documentId) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.CATALOG_PATH,
				catalogId, CCDSConstants.DOCUMENT_PATH, documentId }, null, null);
		logger.debug("addRevisionCatalogDocument: url {}", uri);
		restTemplate.postForLocation(uri, null);
	}

	@Override
	public void dropRevisionCatalogDocument(String revisionId, String catalogId, String documentId) {
		URI uri = buildUri(new String[] { CCDSConstants.REVISION_PATH, revisionId, CCDSConstants.CATALOG_PATH,
				catalogId, CCDSConstants.DOCUMENT_PATH, documentId }, null, null);
		logger.debug("dropRevisionCatalogDocument: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPPublishRequest getPublishRequest(long publishRequestId) {
		URI uri = buildUri(new String[] { CCDSConstants.PUBLISH_REQUEST_PATH, Long.toString(publishRequestId) }, null,
				null);
		logger.debug("getPublishRequest: uri {}", uri);
		ResponseEntity<MLPPublishRequest> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPPublishRequest>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPPublishRequest> getPublishRequests(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.PUBLISH_REQUEST_PATH }, null, pageRequest);
		logger.debug("getPublishRequests: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPPublishRequest>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPPublishRequest>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPPublishRequest> searchPublishRequests(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.PUBLISH_REQUEST_PATH, CCDSConstants.SEARCH_PATH }, copy,
				pageRequest);
		logger.debug("searchPublishRequests: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPPublishRequest>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPPublishRequest>>() {
				});
		return response.getBody();
	}

	@Override
	public boolean isPublishRequestPending(String solutionId, String revisionId) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("solutionId", solutionId);
		map.put("revisionId", revisionId);
		map.put("statusCode", PublishRequestStatusCode.PE.toString());
		RestPageResponse<MLPPublishRequest> reqs = searchPublishRequests(map, false, new RestPageRequest(0, 1));
		return reqs.getNumberOfElements() == 1;
	}

	@Override
	public MLPPublishRequest createPublishRequest(MLPPublishRequest publishRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.PUBLISH_REQUEST_PATH }, null, null);
		logger.debug("createPublishRequest: uri {}", uri);
		return restTemplate.postForObject(uri, publishRequest, MLPPublishRequest.class);
	}

	@Override
	public void updatePublishRequest(MLPPublishRequest publishRequest) {
		URI uri = buildUri(
				new String[] { CCDSConstants.PUBLISH_REQUEST_PATH, Long.toString(publishRequest.getRequestId()) }, null,
				null);
		logger.debug("updatePublishRequest: url {}", uri);
		restTemplate.put(uri, publishRequest);
	}

	@Override
	public void deletePublishRequest(long publishRequestId) {
		URI uri = buildUri(new String[] { CCDSConstants.PUBLISH_REQUEST_PATH, Long.toString(publishRequestId) }, null,
				null);
		logger.debug("deletePublishRequest: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public void addUserTag(String userId, String tag) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.TAG_PATH, tag }, null, null);
		logger.debug("addUserTag: uri {}", uri);
		restTemplate.postForLocation(uri, null);
	}

	@Override
	public void dropUserTag(String userId, String tag) {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, userId, CCDSConstants.TAG_PATH, tag }, null, null);
		logger.debug("dropUserTag: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public byte[] getSolutionPicture(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.PICTURE_PATH }, null,
				null);
		logger.debug("getSolutionImage: uri {}", uri);
		ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});
		return response.getBody();
	}

	@Override
	public void saveSolutionPicture(String solutionId, byte[] image) {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solutionId, CCDSConstants.PICTURE_PATH }, null,
				null);
		logger.debug("saveSolutionImage: uri {}", uri);
		restTemplate.put(uri, image);
	}

	@Override
	public RestPageResponse<MLPCatalog> getCatalogs(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH }, null, pageRequest);
		logger.debug("getCatalogs: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPCatalog>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPCatalog>>() {
				});
		return response.getBody();
	}

	@Override
	public List<String> getCatalogPublishers() {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, CCDSConstants.PUBLISHERS_PATH }, null, null);
		logger.debug("getCatalogPublishers: uri {}", uri);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPCatalog> searchCatalogs(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchCatalogs: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPCatalog>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPCatalog>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPCatalog getCatalog(String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, catalogId }, null, null);
		logger.debug("getCatalog: uri {}", uri);
		ResponseEntity<MLPCatalog> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPCatalog>() {
				});
		return response.getBody();
	}

	@Override
	public MLPCatalog createCatalog(MLPCatalog catalog) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH }, null, null);
		logger.debug("createCatalog: uri {}", uri);
		return restTemplate.postForObject(uri, catalog, MLPCatalog.class);
	}

	@Override
	public void updateCatalog(MLPCatalog catalog) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, catalog.getCatalogId() }, null, null);
		logger.debug("updateCatalog: url {}", uri);
		restTemplate.put(uri, catalog);
	}

	@Override
	public void deleteCatalog(String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, catalogId }, null, null);
		logger.debug("deleteCatalog: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public long getCatalogSolutionCount(String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, catalogId, CCDSConstants.SOLUTION_PATH,
				CCDSConstants.COUNT_PATH }, null, null);
		logger.debug("getCatalogSolutionCount: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount();
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutionsInCatalogs(String[] catalogIds, RestPageRequest pageRequest) {
		HashMap<String, Object> parms = new HashMap<>();
		parms.put(CCDSConstants.SEARCH_CATALOG, catalogIds);
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, CCDSConstants.SOLUTION_PATH }, parms,
				pageRequest);
		logger.debug("getSolutionsInCatalogs: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPCatalog> getSolutionCatalogs(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, CCDSConstants.SOLUTION_PATH, solutionId }, null,
				null);
		logger.debug("getSolutionCatalogs: uri {}", uri);
		ResponseEntity<List<MLPCatalog>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPCatalog>>() {
				});
		return response.getBody();
	}

	@Override
	public void addSolutionToCatalog(String solutionId, String catalogId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.CATALOG_PATH, catalogId, CCDSConstants.SOLUTION_PATH, solutionId }, null,
				null);
		logger.debug("addSolutionToCatalog: url {}", uri);
		MLPCatSolMap map = new MLPCatSolMap(catalogId, solutionId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropSolutionFromCatalog(String solutionId, String catalogId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.CATALOG_PATH, catalogId, CCDSConstants.SOLUTION_PATH, solutionId }, null,
				null);
		logger.debug("dropSolutionFromCatalog: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPTask getTask(long taskId) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH, Long.toString(taskId) }, null, null);
		logger.debug("getTask: uri {}", uri);
		ResponseEntity<MLPTask> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPTask>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPTask> getTasks(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH }, null, pageRequest);
		logger.debug("getTasks: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPTask>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPTask>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPTask> searchTasks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchTasks: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPTask>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPTask>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPTask createTask(MLPTask task) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH }, null, null);
		logger.debug("createTask: uri {}", uri);
		return restTemplate.postForObject(uri, task, MLPTask.class);
	}

	@Override
	public void updateTask(MLPTask task) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH, Long.toString(task.getTaskId()) }, null, null);
		logger.debug("updateTask: url {}", uri);
		restTemplate.put(uri, task);
	}

	@Override
	public void deleteTask(long taskId) {
		URI uri = buildUri(new String[] { CCDSConstants.TASK_PATH, Long.toString(taskId) }, null, null);
		logger.debug("deleteTask: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPRtuReference> getRtuReferences(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, CCDSConstants.REFERENCE_PATH }, null, pageRequest);
		logger.debug("getRtuReferences: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPRtuReference>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPRtuReference>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPRtuReference createRtuReference(MLPRtuReference ref) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, CCDSConstants.REFERENCE_PATH }, null, null);
		logger.debug("createRtuReference: uri {}", uri);
		return restTemplate.postForObject(uri, ref, MLPRtuReference.class);
	}

	@Override
	public void deleteRtuReference(MLPRtuReference ref) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, CCDSConstants.REFERENCE_PATH, ref.getRef() }, null,
				null);
		logger.debug("deleteRtuReference: uri {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public MLPRightToUse getRightToUse(Long rtuId) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId) }, null, null);
		logger.debug("getRightToUse: uri {}", uri);
		ResponseEntity<MLPRightToUse> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPRightToUse>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPRightToUse> getRightToUses(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH }, null, pageRequest);
		logger.debug("getRightToUses: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPRightToUse>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPRightToUse>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPRightToUse> searchRightToUses(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, CCDSConstants.SEARCH_PATH }, copy, pageRequest);
		logger.debug("searchRightToUses: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPRightToUse>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPRightToUse>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPRightToUse> getRightToUses(String solutionId, String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.USER_PATH, userId }, null, null);
		logger.debug("getRightToUses: uri {}", uri);
		ResponseEntity<List<MLPRightToUse>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPRightToUse>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPRightToUse createRightToUse(MLPRightToUse rightToUse) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH }, null, null);
		logger.debug("createRightToUse: uri {}", uri);
		return restTemplate.postForObject(uri, rightToUse, MLPRightToUse.class);
	}

	@Override
	public void updateRightToUse(MLPRightToUse rightToUse) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, Long.toString(rightToUse.getRtuId()) }, null, null);
		logger.debug("updateRightToUse: url {}", uri);
		restTemplate.put(uri, rightToUse);
	}

	@Override
	public void deleteRightToUse(Long rtuId) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId) }, null, null);
		logger.debug("deleteRightToUse: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public void addRefToRtu(String refId, Long rtuId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId), CCDSConstants.REFERENCE_PATH, refId },
				null, null);
		logger.debug("addRefToRtu: url {}", uri);
		MLPRtuUserMap map = new MLPRtuUserMap(rtuId, refId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropRefFromRtu(String refId, Long rtuId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId), CCDSConstants.REFERENCE_PATH, refId },
				null, null);
		logger.debug("dropRefFromRtu: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPRightToUse> getRtusByReference(String referenceId) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, CCDSConstants.REFERENCE_PATH, referenceId }, null,
				null);
		logger.debug("getRtusByReference: uri {}", uri);
		ResponseEntity<List<MLPRightToUse>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPRightToUse>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPUser> getRtuUsers(long rtuId) {
		URI uri = buildUri(new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId), CCDSConstants.USER_PATH }, null,
				null);
		logger.debug("getRtuUsers: uri {}", uri);
		ResponseEntity<List<MLPUser>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPUser>>() {
				});
		return response.getBody();
	}

	@Override
	public void addUserToRtu(String userId, Long rtuId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId), CCDSConstants.USER_PATH, userId }, null,
				null);
		logger.debug("addUserToRtu: url {}", uri);
		MLPRtuUserMap map = new MLPRtuUserMap(rtuId, userId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropUserFromRtu(String userId, Long rtuId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.RTU_PATH, Long.toString(rtuId), CCDSConstants.USER_PATH, userId }, null,
				null);
		logger.debug("dropUserFromRtu: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPProject> getProjects(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH }, null,
				pageRequest);
		logger.debug("getProjects: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPProject>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPProject>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPProject> searchProjects(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(
				new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, CCDSConstants.SEARCH_PATH },
				copy, pageRequest);
		logger.debug("searchProjects: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPProject>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPProject>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPProject getProject(String projectId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId }, null,
				null);
		logger.debug("getProject: uri {}", uri);
		ResponseEntity<MLPProject> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPProject>() {
				});
		return response.getBody();
	}

	@Override
	public MLPProject createProject(MLPProject project) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH }, null, null);
		logger.debug("createProject: url {}", uri);
		return restTemplate.postForObject(uri, project, MLPProject.class);
	}

	@Override
	public void updateProject(MLPProject project) {
		URI uri = buildUri(
				new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, project.getProjectId() }, null,
				null);
		logger.debug("updateProject: uri {}", uri);
		restTemplate.put(uri, project);
	}

	@Override
	public void deleteProject(String projectId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId }, null,
				null);
		logger.debug("deleteProject: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public void addProjectNotebook(String projectId, String notebookId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId,
				CCDSConstants.NOTEBOOK_PATH, notebookId }, null, null);
		logger.debug("addProjectNotebook: url {}", uri);
		MLPProjNotebookMap map = new MLPProjNotebookMap(projectId, notebookId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropProjectNotebook(String projectId, String notebookId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId,
				CCDSConstants.NOTEBOOK_PATH, notebookId }, null, null);
		logger.debug("dropProjectNotebook: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public void addProjectPipeline(String projectId, String pipelineId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId,
				CCDSConstants.PIPELINE_PATH, pipelineId }, null, null);
		logger.debug("addProjectPipeline: url {}", uri);
		MLPProjPipelineMap map = new MLPProjPipelineMap(projectId, pipelineId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropProjectPipeline(String projectId, String pipelineId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId,
				CCDSConstants.PIPELINE_PATH, pipelineId }, null, null);
		logger.debug("dropProjectPipeline: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<MLPNotebook> getProjectNotebooks(String projectId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId,
				CCDSConstants.NOTEBOOK_PATH }, null, null);
		logger.debug("getProjectNotebooks: uri {}", uri);
		ResponseEntity<List<MLPNotebook>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPNotebook>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPProject> getNotebookProjects(String notebookId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH, notebookId,
				CCDSConstants.PROJECT_PATH }, null, null);
		logger.debug("getNotebookProjects: uri {}", uri);
		ResponseEntity<List<MLPProject>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPProject>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPPipeline> getProjectPipelines(String projectId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PROJECT_PATH, projectId,
				CCDSConstants.PIPELINE_PATH }, null, null);
		logger.debug("getProjectPipelines: uri {}", uri);
		ResponseEntity<List<MLPPipeline>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPPipeline>>() {
				});
		return response.getBody();
	}

	@Override
	public List<MLPProject> getPipelineProjects(String pipelineId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH, pipelineId,
				CCDSConstants.PROJECT_PATH }, null, null);
		logger.debug("getPipelineProjects: uri {}", uri);
		ResponseEntity<List<MLPProject>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPProject>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPNotebook> getNotebooks(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH }, null,
				pageRequest);
		logger.debug("getNotebooks: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPNotebook>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPNotebook>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPNotebook> searchNotebooks(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(
				new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH, CCDSConstants.SEARCH_PATH },
				copy, pageRequest);
		logger.debug("searchNotebooks: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPNotebook>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPNotebook>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPNotebook getNotebook(String notebookId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH, notebookId }, null,
				null);
		logger.debug("getProject: uri {}", uri);
		ResponseEntity<MLPNotebook> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPNotebook>() {
				});
		return response.getBody();
	}

	@Override
	public MLPNotebook createNotebook(MLPNotebook notebook) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH }, null, null);
		logger.debug("createNotebook: url {}", uri);
		return restTemplate.postForObject(uri, notebook, MLPNotebook.class);
	}

	@Override
	public void updateNotebook(MLPNotebook notebook) {
		URI uri = buildUri(
				new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH, notebook.getNotebookId() },
				null, null);
		logger.debug("updateNotebook: uri {}", uri);
		restTemplate.put(uri, notebook);
	}

	@Override
	public void deleteNotebook(String notebookId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.NOTEBOOK_PATH, notebookId }, null,
				null);
		logger.debug("deleteNotebook: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public RestPageResponse<MLPPipeline> getPipelines(RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH }, null,
				pageRequest);
		logger.debug("getPipelines: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPPipeline>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPPipeline>>() {
				});
		return response.getBody();
	}

	@Override
	public RestPageResponse<MLPPipeline> searchPipelines(Map<String, Object> queryParameters, boolean isOr,
			RestPageRequest pageRequest) {
		Map<String, Object> copy = new HashMap<>(queryParameters);
		copy.put(CCDSConstants.JUNCTION_QUERY_PARAM, isOr ? "o" : "a");
		URI uri = buildUri(
				new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH, CCDSConstants.SEARCH_PATH },
				copy, pageRequest);
		logger.debug("searchPipelines: uri {}", uri);
		ResponseEntity<RestPageResponse<MLPPipeline>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPPipeline>>() {
				});
		return response.getBody();
	}

	@Override
	public MLPPipeline getPipeline(String pipelineId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH, pipelineId }, null,
				null);
		logger.debug("getPipeline: uri {}", uri);
		ResponseEntity<MLPPipeline> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<MLPPipeline>() {
				});
		return response.getBody();
	}

	@Override
	public MLPPipeline createPipeline(MLPPipeline pipeline) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH }, null, null);
		logger.debug("createPipeline: url {}", uri);
		return restTemplate.postForObject(uri, pipeline, MLPPipeline.class);
	}

	@Override
	public void updatePipeline(MLPPipeline pipeline) {
		URI uri = buildUri(
				new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH, pipeline.getPipelineId() },
				null, null);
		logger.debug("updatePipeline: uri {}", uri);
		restTemplate.put(uri, pipeline);
	}

	@Override
	public void deletePipeline(String pipelineId) {
		URI uri = buildUri(new String[] { CCDSConstants.WORKBENCH_PATH, CCDSConstants.PIPELINE_PATH, pipelineId }, null,
				null);
		logger.debug("deletePipeline: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<String> getUserFavoriteCatalogIds(String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, CCDSConstants.USER_PATH, userId,
				CCDSConstants.FAVORITE_PATH }, null, null);
		logger.debug("getUserFavoriteCatalogIds: uri {}", uri);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	@Override
	public void addUserFavoriteCatalog(String userId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, catalogId, CCDSConstants.USER_PATH, userId,
				CCDSConstants.FAVORITE_PATH }, null, null);
		logger.debug("addUserFavoriteCatalog: url {}", uri);
		MLPUserCatFavMap map = new MLPUserCatFavMap(userId, catalogId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropUserFavoriteCatalog(String userId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.CATALOG_PATH, catalogId, CCDSConstants.USER_PATH, userId,
				CCDSConstants.FAVORITE_PATH }, null, null);
		logger.debug("dropUserFavoriteCatalog: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public List<String> getPeerAccessCatalogIds(String peerId) {
		URI uri = buildUri(
				new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.PEER_PATH, peerId, CCDSConstants.CATALOG_PATH },
				null, null);
		logger.debug("getPeerAccessCatalogIds: uri {}", uri);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	@Override
	public void addPeerAccessCatalog(String peerId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.PEER_PATH, peerId,
				CCDSConstants.CATALOG_PATH, catalogId }, null, null);
		logger.debug("addPeerAccessCatalog: url {}", uri);
		MLPPeerCatAccMap map = new MLPPeerCatAccMap(peerId, catalogId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropPeerAccessCatalog(String peerId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.PEER_PATH, peerId,
				CCDSConstants.CATALOG_PATH, catalogId }, null, null);
		logger.debug("dropPeerAccessCatalog: url {}", uri);
		restTemplate.delete(uri);
	}

	@Override
	public boolean isPeerAccessToCatalog(String peerId, String catalogId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.PEER_PATH, peerId,
				CCDSConstants.CATALOG_PATH, catalogId }, null, null);
		logger.debug("isPeerAccessToCatalog: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount() != 0;
	}

	@Override
	public boolean isPeerAccessToSolution(String peerId, String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.PEER_PATH, peerId,
				CCDSConstants.SOLUTION_PATH, solutionId }, null, null);
		logger.debug("isPeerAccessToSolution: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount() != 0;
	}

	@Override
	public List<MLPUser> getSolutionAccessUsers(String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.USER_PATH }, null, null);
		logger.debug("getSolutionAccessUsers: url {}", uri);
		ResponseEntity<List<MLPUser>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MLPUser>>() {
				});
		return response.getBody();
	}

	@Override
	public boolean isUserAccessToSolution(String userId, String solutionId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.USER_PATH, userId,
				CCDSConstants.SOLUTION_PATH, solutionId }, null, null);
		logger.debug("isUserAccessToSolution: uri {}", uri);
		ResponseEntity<CountTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CountTransport>() {
				});
		return response.getBody().getCount() != 0;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.USER_PATH, userId,
				CCDSConstants.SOLUTION_PATH }, null, pageRequest);
		logger.debug("getUserAccessSolutions: url {}", uri);
		ResponseEntity<RestPageResponse<MLPSolution>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestPageResponse<MLPSolution>>() {
				});
		return response.getBody();
	}

	@Override
	public void addSolutionUserAccess(String solutionId, String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.USER_PATH, userId, }, null, null);
		logger.debug("addSolutionUserAccess: url {}", uri);
		MLPSolUserAccMap map = new MLPSolUserAccMap(solutionId, userId);
		restTemplate.postForObject(uri, map, SuccessTransport.class);
	}

	@Override
	public void dropSolutionUserAccess(String solutionId, String userId) {
		URI uri = buildUri(new String[] { CCDSConstants.ACCESS_PATH, CCDSConstants.SOLUTION_PATH, solutionId,
				CCDSConstants.USER_PATH, userId, }, null, null);
		logger.debug("dropSolutionUserAccess: url {}", uri);
		restTemplate.delete(uri);
	}

}
