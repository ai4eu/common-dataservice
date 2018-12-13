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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.acumos.cds.domain.MLPTag;

public abstract class AbstractModelTest {

	// Values for properties
	final long time = new Date().getTime();
	final boolean b1 = true;
	final boolean b2 = false;
	final byte[] by1 = { 0, 1, 2, 3 };
	final Timestamp t1 = new Timestamp(time + 1 * 24 * 60 * 60 * 1000);
	final Timestamp t2 = new Timestamp(time + 2 * 24 * 60 * 60 * 1000);
	final Timestamp t3 = new Timestamp(time + 3 * 24 * 60 * 60 * 1000);
	final Timestamp t4 = new Timestamp(time + 4 * 24 * 60 * 60 * 1000);
	final Timestamp t5 = new Timestamp(time + 5 * 24 * 60 * 60 * 1000);
	final Timestamp t6 = new Timestamp(time + 6 * 24 * 60 * 60 * 1000);
	final Integer i1 = 1;
	final Integer i2 = 2;
	final Integer i3 = 3;
	final Integer i4 = 4;
	final Integer i5 = 5;
	final Long l1 = 1L;
	final Long l2 = 2L;
	final Long l3 = 3L;
	final Long l4 = 4L;
	final String s1 = "string1";
	final String s2 = "string2";
	final String s3 = "string3";
	final String s4 = "string4";
	final String s5 = "string5";
	final String s6 = "string6";
	final String s7 = "string7";
	final String s8 = "string8";
	final String s9 = "string9";
	final String s10 = "string10";
	final String s11 = "string11";
	final String s12 = "string12";
	final String s13 = "string13";
	final Set<MLPTag> tags = new HashSet<>();
	final String u1 = "http://foo.com";

	public AbstractModelTest() {
		tags.add(new MLPTag("taggy"));
	}

}
