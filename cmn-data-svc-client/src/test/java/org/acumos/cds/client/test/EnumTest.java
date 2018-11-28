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

package org.acumos.cds.client.test;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.DeploymentStatusCode;
import org.acumos.cds.LoginProviderCode;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.ModelTypeCode;
import org.acumos.cds.NotificationDeliveryMechanismCode;
import org.acumos.cds.PeerStatusCode;
import org.acumos.cds.SubscriptionScopeCode;
import org.acumos.cds.ToolkitTypeCode;
import org.junit.Assert;
import org.junit.Test;

/**
 * This increases coverage.
 */
@SuppressWarnings("deprecation")
public class EnumTest {

	@Test
	public void testEnums() {
		Assert.assertTrue(AccessTypeCode.values().length > 0);
		Assert.assertTrue(ArtifactTypeCode.values().length > 0);
		Assert.assertTrue(DeploymentStatusCode.values().length > 0);
		Assert.assertTrue(LoginProviderCode.values().length > 0);
		Assert.assertTrue(MessageSeverityCode.values().length > 0);
		Assert.assertTrue(ModelTypeCode.values().length > 0);
		Assert.assertTrue(NotificationDeliveryMechanismCode.values().length > 0);
		Assert.assertTrue(PeerStatusCode.values().length > 0);
		Assert.assertTrue(SubscriptionScopeCode.values().length > 0);
		Assert.assertTrue(ToolkitTypeCode.values().length > 0);
	}
}
