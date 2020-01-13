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
 * This enum defines Acumos publish request status codes. The knowledge of these
 * values is deeply encoded into the system, they are extremely difficult to
 * change, so it's safe to use this enum in Java code.
 * 
 * The value set may also be obtained by calling method
 * {@link org.acumos.cds.client.ICommonDataServiceRestClient#getCodeNamePairs(CodeNameType)}.
 */
public enum PublishRequestStatusCode {

	PE("Pending"), //
	AP("Approved"), //
	DE("Declined"), //
	WD("Withdrawn");

	private String codeName;

	private PublishRequestStatusCode(final String codeName) {
		this.codeName = codeName;
	}

	public String getCodeeName() {
		return codeName;
	}
}
