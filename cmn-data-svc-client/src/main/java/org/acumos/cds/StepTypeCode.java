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
 * This enum defines Acumos step type codes.
 * 
 * @deprecated Use
 *             {@link org.acumos.cds.client.ICommonDataServiceRestClient#getCodeNamePairs(CodeNameType)}
 */
@Deprecated
public enum StepTypeCode {

	OB("Onboarding");

	private String stepName;

	private StepTypeCode(final String stepName) {
		this.stepName = stepName;
	}

	public String getStepName() {
		return stepName;
	}

}
