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

package org.acumos.cds.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.config.CodeNameProperties;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("codeNameService")
public class CodeNameServiceImpl implements CodeNameService {

	@Autowired
	private CodeNameProperties codeNameProps;

	/**
	 * Translates a value set name into a set of pairs.
	 * 
	 * Unfortunately the structure of the CodeNameConfiguration class required the
	 * big switch-case to answer appropriately.
	 */
	private Map<String, String> getPairs(CodeNameType type) {
		switch (type) {
		case ACCESS_TYPE:
			return codeNameProps.getAccessType();
		case ARTIFACT_TYPE:
			return codeNameProps.getArtifactType();
		case DEPLOYMENT_STATUS:
			return codeNameProps.getDeploymentStatus();
		case LOGIN_PROVIDER:
			return codeNameProps.getLoginProvider();
		case MESSAGE_SEVERITY:
			return codeNameProps.getMessageSeverity();
		case MODEL_TYPE:
			return codeNameProps.getModelType();
		case NOTIFICATION_DELIVERY_MECHANISM:
			return codeNameProps.getNotificationDeliveryMechanism();
		case PEER_STATUS:
			return codeNameProps.getPeerStatus();
		case PUBLISH_REQUEST_STATUS:
			return codeNameProps.getPublishRequestStatus();
		case STEP_STATUS:
			return codeNameProps.getStepStatus();
		case STEP_TYPE:
			return codeNameProps.getStepType();
		case SUBSCRIPTION_SCOPE:
			return codeNameProps.getSubscriptionScope();
		case TOOLKIT_TYPE:
			return codeNameProps.getToolkitType();
		case VERIFIED_LICENSE:
			return codeNameProps.getVerifiedLicense();
		case VERIFIED_VULNERABILITY:
			return codeNameProps.getVerifiedVulnerability();
		}
		throw new IllegalArgumentException("unimplemented case for type " + type.name());
	}

	@Override
	public List<MLPCodeNamePair> getCodeNamePairs(CodeNameType type) {
		Map<String, String> pairs = getPairs(type);
		List<MLPCodeNamePair> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : pairs.entrySet())
			list.add(new MLPCodeNamePair(entry.getKey(), entry.getValue()));
		return list;
	}

	@Override
	public boolean validateCode(String code, CodeNameType type) {
		Map<String, String> pairs = getPairs(type);
		return pairs.containsKey(code);
	}

}
