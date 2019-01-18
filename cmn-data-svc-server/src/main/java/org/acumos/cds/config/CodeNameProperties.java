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

package org.acumos.cds.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Publishes enumerated value sets, aka code-name pairs, from property sources
 * chosen dynamically by Spring, which most likely includes the file
 * application.properties.
 * 
 * The field names must match the keys in the application.properties file,
 * including the key prefix as annotated below. The following example publishes
 * two code-name pairs in the toolkitType value set:
 * 
 * <PRE>
   code-name.toolkit-type.PY=Python
   code-name.toolkit-type.JA=Java
 * </PRE>
 * 
 * Every value set has its own field with associated getter and setter here. I
 * wanted to make this data driven instead, just match on a prefix etc., but I
 * found no way to gain access to the full set of properties and iterate over
 * them to find matches. Spring's Environment class only provides getProperty().
 */
@Configuration
@ConfigurationProperties(prefix = "code-name")
public class CodeNameProperties {

	private Map<String, String> accessType;
	private Map<String, String> artifactType;
	private Map<String, String> deploymentStatus;
	private Map<String, String> loginProvider;
	private Map<String, String> messageSeverity;
	private Map<String, String> modelType;
	private Map<String, String> notificationDeliveryMechanism;
	private Map<String, String> peerStatus;
	private Map<String, String> publishRequestStatus;
	private Map<String, String> subscriptionScope;
	private Map<String, String> taskStepStatus;
	private Map<String, String> taskType;
	private Map<String, String> toolkitType;
	private Map<String, String> verifiedLicense;
	private Map<String, String> verifiedVulnerability;

	public Map<String, String> getAccessType() {
		return accessType;
	}

	public void setAccessType(Map<String, String> accessType) {
		this.accessType = accessType;
	}

	public Map<String, String> getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(Map<String, String> artifactType) {
		this.artifactType = artifactType;
	}

	public Map<String, String> getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(Map<String, String> deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

	public Map<String, String> getLoginProvider() {
		return loginProvider;
	}

	public void setLoginProvider(Map<String, String> loginProvider) {
		this.loginProvider = loginProvider;
	}

	public Map<String, String> getMessageSeverity() {
		return messageSeverity;
	}

	public void setMessageSeverity(Map<String, String> messageSeverity) {
		this.messageSeverity = messageSeverity;
	}

	public Map<String, String> getModelType() {
		return modelType;
	}

	public void setModelType(Map<String, String> modelType) {
		this.modelType = modelType;
	}

	public Map<String, String> getNotificationDeliveryMechanism() {
		return notificationDeliveryMechanism;
	}

	public void setNotificationDeliveryMechanism(Map<String, String> notificationDeliveryMechanism) {
		this.notificationDeliveryMechanism = notificationDeliveryMechanism;
	}

	public Map<String, String> getPeerStatus() {
		return peerStatus;
	}

	public void setPeerStatus(Map<String, String> peerStatus) {
		this.peerStatus = peerStatus;
	}

	public Map<String, String> getPublishRequestStatus() {
		return publishRequestStatus;
	}

	public void setPublishRequestStatus(Map<String, String> publishRequestStatus) {
		this.publishRequestStatus = publishRequestStatus;
	}

	public Map<String, String> getSubscriptionScope() {
		return subscriptionScope;
	}

	public void setSubscriptionScope(Map<String, String> subscriptionScope) {
		this.subscriptionScope = subscriptionScope;
	}

	public Map<String, String> getTaskStepStatus() {
		return taskStepStatus;
	}

	public void setTaskStepStatus(Map<String, String> stepStatus) {
		this.taskStepStatus = stepStatus;
	}

	public Map<String, String> getTaskType() {
		return taskType;
	}

	public void setTaskType(Map<String, String> taskType) {
		this.taskType = taskType;
	}

	public Map<String, String> getToolkitType() {
		return toolkitType;
	}

	public void setToolkitType(Map<String, String> toolkitType) {
		this.toolkitType = toolkitType;
	}

	public Map<String, String> getVerifiedLicense() {
		return verifiedLicense;
	}

	public void setVerifiedLicense(Map<String, String> verifiedLicense) {
		this.verifiedLicense = verifiedLicense;
	}

	public Map<String, String> getVerifiedVulnerability() {
		return verifiedVulnerability;
	}

	public void setVerifiedVulnerability(Map<String, String> verifiedVulnerability) {
		this.verifiedVulnerability = verifiedVulnerability;
	}

}
