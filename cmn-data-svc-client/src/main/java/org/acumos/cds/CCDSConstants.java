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

package org.acumos.cds;

/**
 * Publishes constants for the CCDS server and client.
 */
public interface CCDSConstants {

	public static final String X_REQUEST_ID = "X-Request-ID";

	public static final String JUNCTION_QUERY_PARAM = "_j";

	// For error page
	public static final String ERROR_PATH = "/error";

	// Metadata
	public static final String HEALTHCHECK_PATH = "healthcheck";
	public static final String VERSION_PATH = "version";

	// Path components
	public static final String ARTIFACT_PATH = "artifact";
	public static final String COMMENT_PATH = "comment";
	public static final String COMPOSITE_PATH = "comp";
	public static final String CONFIG_PATH = "config";
	public static final String CONTENT_PATH = "content";
	public static final String COUNT_PATH = "count";
	public static final String DATE_PATH = "date";
	public static final String DEPLOY_PATH = "deploy";
	public static final String DESCRIPTION_PATH = "descr";
	public static final String DOCUMENT_PATH = "document";
	public static final String DOWNLOAD_PATH = "dnld";
	public static final String FAVORITE_PATH = "favorite";
	public static final String FUNCTION_PATH = "function";
	public static final String GROUP_PATH = "group";
	public static final String KEYWORD_PATH = "kw";
	public static final String KW_TAG_PATH = "kwtag";
	public static final String NOTIFICATION_PATH = "notif";
	public static final String NOTIFICATION_PREF_PATH = "notifpref";
	public static final String PEER_PATH = "peer";
	public static final String PICTURE_PATH = "pic";
	public static final String PUBLISH_REQUEST_PATH = "pubreq";
	public static final String RATING_PATH = "rating";
	public static final String REVISION_PATH = "revision";
	public static final String ROLE_PATH = "role";
	public static final String SITE_PATH = "site";
	public static final String STEP_RESULT_PATH = "stepresult";
	public static final String SOLUTION_PATH = "solution";
	public static final String SUBSCRIPTION_PATH = "sub";
	public static final String TAG_PATH = "tag";
	public static final String THREAD_PATH = "thread";
	public static final String USER_PATH = "user";
	public static final String VAL_SEQ_PATH = "valseq";
	public static final String VAL_TYPE_PATH = "valtype";
	public static final String VIEW_PATH = "view";
	public static final String WEB_PATH = "web";
	public static final String UNREAD_PATH = "unread";

	// Code-name pairs
	public static final String CODE_PATH = "code";
	public static final String NAME_PATH = "name";
	public static final String PAIR_PATH = "pair";
	// Other paths
	public static final String ACCESS_PATH = "access";
	public static final String LOGIN_PROVIDER_PATH = "logprov";
	public static final String MODEL_PATH = "model";
	public static final String STATUS_PATH = "status";
	public static final String STEP_PATH = "step";
	public static final String TOOLKIT_PATH = "toolkit";
	public static final String TYPE_PATH = "type";
	public static final String VAL_PATH = "val";
	// Actions on entities
	public static final String LIKE_PATH = "like";
	public static final String SEARCH_PATH = "search";
	public static final String PORTAL_PATH = "portal";
	public static final String TERM_PATH = "term";
	public static final String LOGIN_PATH = "login";
	public static final String LOGIN_API_PATH = "loginapi";
	public static final String VERIFY_PATH = "verify";
	public static final String CHPASS_PATH = "chgpw";

	// Actions on datasets
	public static final String DATASET_PATH = "dataset";

	// Portal search interface
	public static final String SEARCH_ACCESS_TYPES = "atc";
	public static final String SEARCH_ACTIVE = "active";
	public static final String SEARCH_USERS = "user";
	public static final String SEARCH_DESC = "desc";
	public static final String SEARCH_KW = "kw";
	public static final String SEARCH_MODEL_TYPES = "mtc";
	public static final String SEARCH_NAME = "name";
	public static final String SEARCH_TAGS = "tag";
	public static final String SEARCH_ALL_TAGS = "alltag";
	public static final String SEARCH_ANY_TAGS = "anytag";
	public static final String SEARCH_VAL_STATUSES = "vsc";
	public static final String SEARCH_DATE = "datems";
	public static final String SEARCH_AUTH = "auth";
	public static final String SEARCH_PUB = "pub";

}
