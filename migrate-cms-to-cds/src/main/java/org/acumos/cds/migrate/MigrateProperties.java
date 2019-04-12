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

package org.acumos.cds.migrate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Property utility class to publish properties. Ensures required keys are
 * present.
 */
public class MigrateProperties extends Properties {

	private static final long serialVersionUID = 8247904389301104301L;

	public static final String MIGRATE_DATA_TYPE = "migrate.data.type";
	public static final String CDS_URL = "cds.url";
	public static final String CDS_USER = "cds.user";
	public static final String CDS_PASS = "cds.pass";
	public static final String CMS_URL = "cms.url";
	public static final String CMS_USER = "cms.user";
	public static final String CMS_PASS = "cms.pass";
	public static final String NEXUS_URL = "nexus.url";
	public static final String NEXUS_USER = "nexus.user";
	public static final String NEXUS_PASS = "nexus.pass";
	public static final String NEXUS_PREFIX = "nexus.prefix";

	private String[] requiredProps = { MIGRATE_DATA_TYPE, CDS_URL, CDS_USER, CDS_PASS, CMS_URL, CMS_USER, CMS_PASS,
			NEXUS_URL, NEXUS_USER, NEXUS_PASS, NEXUS_PREFIX };

	public MigrateProperties() throws IOException {
		this("migrate.properties");
	}

	public MigrateProperties(final String fileName) throws IOException {
		InputStream propertiesStream = new FileInputStream(fileName);
		load(propertiesStream);
		propertiesStream.close();
		for (String r : requiredProps)
			if (getProperty(r) == null)
				throw new IllegalArgumentException("Missing property key: " + r);
	}

}
