/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.cds.migrate.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CarouselSlide {

	public String name;
	public Boolean graphicImgEnabled;
	public String headline;
	public String infoImageAling; // sic
	public Integer number;
	public Boolean slideEnabled;
	public String supportingContent;
	public String textAling; // sic

	// For images in CMS
	public String bgImageUrl;
	// For images in CMS
	public String infoImageUrl;
	// For images in CDS
	public Integer uniqueKey;
	// For images in CDS
	public Boolean hasInfographic;
	// For images in CDS
	public String bgImgKey;
	// For images in CDS
	public String infoImgKey;

}
