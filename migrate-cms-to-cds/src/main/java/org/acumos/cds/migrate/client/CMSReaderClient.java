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

package org.acumos.cds.migrate.client;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.acumos.cds.migrate.domain.CMSDescription;
import org.acumos.cds.migrate.domain.CMSNameList;
import org.acumos.cds.migrate.domain.CMSRevisionDescription;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST client to read data from Hippo-CMS configured for Acumos. Includes
 * solution images, revision descriptions and revision documents. Only knows the
 * GET endpoints, does not write.
 * 
 * https://wiki.acumos.org/display/PUX/CMS+API
 */
public class CMSReaderClient {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/** URL components after the context */
	private final String ACUMOSCMS_PATH = "acumoscms";
	private final String API_MANUAL_PATH = "api-manual";
	private final String ASSETS_PATH = "assets";
	private final String BINARIES_PATH = "binaries";
	private final String CONTENT_PATH = "content";
	private final String DESCRIPTION_PATH = "description";
	private final String GALLERY_PATH = "gallery";
	private final String SOL_DESCRIPTION_PATH = "solDescription";
	private final String SOLUTION_UC_PATH = "Solution";
	private final String SOLUTION_LC_PATH = "solution";
	private final String SOLUTION_ASSETS_PATH = "solutionAssets";
	private final String SOLUTIONDOCS_PATH = "solutiondocs";
	private final String SOLUTION_IMAGES_PATH = "solutionImages";
	private final String CO_BRAND_LOGO_PATH = "coBrandLogo";
	private final String CAROUSEL_IMAGES_PATH = "carouselImages";
	private final String GLOBAL_PATH = "global";
	private final String TERMS_CONDITION_PATH = "termsCondition";

	/** URL request parameters */
	private final String PATH_PARAM = "path";

	private String baseUrl;
	private RestTemplate restTemplate;

	public CMSReaderClient(final String webapiUrl, final String user, final String pass) {
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
		// Build a client with a credentials provider
		HttpClientBuilder builder = HttpClientBuilder.create();
		if (user != null && pass != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(user, pass));
			builder.setDefaultCredentialsProvider(credsProvider);
		}
		CloseableHttpClient httpClient = builder.build();
		// Create request factory with the client
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);

		// Put the factory in the template
		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
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
	 * @return URI with the specified path segments and query parameters
	 */
	protected URI buildUri(final String[] path, final Map<String, Object> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl);
		for (int p = 0; p < path.length; ++p)
			builder.pathSegment(path[p]);
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
				if (entry.getValue() instanceof Date) {
					// Server expects Date type as Long (not String)
					builder.queryParam(entry.getKey(), ((Date) entry.getValue()).getTime());
				} else if (entry.getValue().getClass().isArray()) {
					Object[] array = (Object[]) entry.getValue();
					for (Object o : array) {
						if (o == null)
							builder.queryParam(entry.getKey(), "null");
						else if (o instanceof Date)
							builder.queryParam(entry.getKey(), ((Date) o).getTime());
						else
							builder.queryParam(entry.getKey(), o.toString());
					}
				} else {
					builder.queryParam(entry.getKey(), entry.getValue().toString());
				}
			}
		}
		return builder.build().encode().toUri();
	}

	/**
	 * Get Solution image name : GET
	 * http://acumos.org:8085/site/api-manual/solutionImages/{solutionId} Response
	 * Body : [image.jpg]
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return Image name
	 */
	public CMSNameList getSolutionImageName(String solutionId) {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, SOLUTION_IMAGES_PATH, solutionId }, null);
		logger.debug("getSolutionImageName: uri {}", uri);
		ResponseEntity<CMSNameList> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSNameList>() {
				});
		return response.getBody();
	}

	/**
	 * Get Binary image:
	 * http://acumos.org:8085/site/binaries/content/gallery/acumoscms/solution/{solutionId}/{imageName}
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param imageName
	 *                       Image name
	 * @return Image
	 */
	public byte[] getSolutionImage(String solutionId, String imageName) {
		URI uri = buildUri(new String[] { BINARIES_PATH, CONTENT_PATH, GALLERY_PATH, ACUMOSCMS_PATH, SOLUTION_LC_PATH,
				solutionId, imageName }, null);
		logger.debug("getSolutionImage: uri {}", uri);
		ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});
		return response.getBody();
	}

	/**
	 * Get Solution Revision Description : GET
	 * http://acumos.org:8085/site/api-manual/Solution/description/{workspace}/{solutionId}/{revisionId}
	 * Returns
	 * 
	 * <pre>
	  {
	  "description":"Test with some blah blah html markup",
	  "solutionId":"e85f4c75-439f-4e4f-8362-6d75187f198f",
	  "revisionId":"aae12a0c-ee4d-4494-b59d-493a0cc794ca"
	  }
	 * </pre>
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @param workspace
	 *                       "workspace" can be "public" or "org"
	 * @return Description object
	 * 
	 */
	public CMSRevisionDescription getRevisionDescription(String solutionId, String revisionId, String workspace) {
		URI uri = buildUri(
				new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, DESCRIPTION_PATH, workspace, solutionId, revisionId },
				null);
		logger.debug("getRevisionDescription: uri {}", uri);
		ResponseEntity<CMSRevisionDescription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSRevisionDescription>() {
				});
		return response.getBody();
	}

	/**
	 * GET
	 * http://acumos.org:8085/site/api-manual/Solution/solutionAssets/{solutionId}/{revisionId}?path={workspace}
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @param workspace
	 *                       "workspace" can be "public" or "org"
	 * @return List of document names
	 */
	public CMSNameList getRevisionDocumentNames(String solutionId, String revisionId, String workspace) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(PATH_PARAM, workspace);
		URI uri = buildUri(
				new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, SOLUTION_ASSETS_PATH, solutionId, revisionId },
				params);
		logger.debug("getRevisionDocumentNames: uri {}", uri);
		ResponseEntity<CMSNameList> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSNameList>() {
				});
		return response.getBody();
	}

	/**
	 * Download Solution Revision Document : GET
	 * http://acumos.org:8085/site/binaries/content/assets/solutiondocs/solution/{solutionId}/{RevisionId}/{workspace}/{documentName}
	 * 
	 * @param solutionId
	 *                         Solution ID
	 * @param revisionId
	 *                         Revision ID
	 * @param workspace
	 *                         "workspace" can be "public" or "org"
	 * @param documentName
	 *                         document name
	 * @return Document content
	 */
	public byte[] getRevisionDocument(String solutionId, String revisionId, String workspace, String documentName) {
		URI uri = buildUri(new String[] { BINARIES_PATH, CONTENT_PATH, ASSETS_PATH, SOLUTIONDOCS_PATH, SOLUTION_LC_PATH,
				solutionId, revisionId, workspace, documentName }, null);
		logger.debug("getRevisionDocument: uri {}", uri);
		ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});
		return response.getBody();
	}

	/**
	 * Downloads the co-brand logo bytes
	 * 
	 * GET
	 * http://acumos.org:8085/site/binaries/content/gallery/acumoscms/global/coBrandLogo
	 * 
	 * @return Bytes
	 */
	public byte[] getCoBrandLogo() {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, GLOBAL_PATH, CO_BRAND_LOGO_PATH }, null);
		logger.debug("getCoBrandLogo: uri {}", uri);
		ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});
		return response.getBody();
	}

	/**
	 * Gets a carousel binary image as byte array
	 * 
	 * GET
	 * http://acumos.org:8085/site/binaries/content/gallery/acumoscms/global/{type}/{name}
	 * 
	 * @param type
	 *                 Carousel image type; one of carousel_background,
	 *                 carousel_infoGraphic, event_carousel_bg, event_carousel_ig
	 * @param name
	 *                 Image name
	 * @return Bytes
	 */
	public byte[] getCarouselImage(String type, String name) {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, CAROUSEL_IMAGES_PATH, type, name }, null);
		logger.debug("getCarouselImage: uri {}", uri);
		ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});
		return response.getBody();
	}

	/**
	 * Gets footer contact info
	 * 
	 * GET
	 * http://acumos.org:8085/site/api-manual/Solution/solDescription?path=global/footer&amp;name=contactinfo
	 * 
	 * Sample:
	 * 
	 * <pre>
	 * {"description":"&lt;p&gt;Contact Info&lt;/p&gt;"}
	 * </pre>
	 * 
	 * @return CMSDescription
	 */
	public CMSDescription getFooterContactInfo() {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("path", "global/footer");
		queryParams.put("name", "contactinfo");
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, SOL_DESCRIPTION_PATH }, queryParams);
		logger.debug("getFooterContactInfo: uri {}", uri);
		ResponseEntity<CMSDescription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSDescription>() {
				});
		return response.getBody();
	}

	/**
	 * Gets footer contact info
	 * 
	 * GET http://acumos.org:8085/site/api-manual/Solution/global/termsCondition
	 * 
	 * Sample:
	 * 
	 * <pre>
	 * {"description":"&lt;p&gt;Footer terms&lt;/p&gt;"}
	 * </pre>
	 * 
	 * @return CMSDescription
	 */
	public CMSDescription getFooterTermsConditions() {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, GLOBAL_PATH, TERMS_CONDITION_PATH }, null);
		logger.debug("getFooterTermsConditions: uri {}", uri);
		ResponseEntity<CMSDescription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSDescription>() {
				});
		return response.getBody();
	}

	/**
	 * Gets onboarding instructions
	 * 
	 * @deprecated This does NOT need to be migrated. 
	 * 
	 * GET
	 * http://acumos.org:8085/site/api-manual/Solution/solDescription?path=global/onboarding-model-screen&amp;name=overview
	 * Sample:
	 * 
	 * <pre>
	 * {"description":"&lt;p&gt;Onboarding overview here&lt;/p&gt;"}
	 * </pre>
	 * 
	 * @return CMSDescription
	 */
	public CMSDescription getOnboardingOverview() {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("path", "global/onboarding-model-screen");
		queryParams.put("name", "overview");
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_UC_PATH, SOL_DESCRIPTION_PATH }, queryParams);
		logger.debug("getOnboardingOverview: uri {}", uri);
		ResponseEntity<CMSDescription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSDescription>() {
				});
		return response.getBody();
	}

}
