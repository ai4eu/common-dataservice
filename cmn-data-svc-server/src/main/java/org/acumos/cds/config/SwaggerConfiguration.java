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

import org.acumos.cds.CdsApplication;
import org.acumos.cds.controller.AbstractController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 */
@Configuration
@EnableSwagger2
// The import tells Swagger to use JPA annotations like @NotNull
// https://stackoverflow.com/questions/43771283/springfox-swagger-optional-and-mandatory-fields-in-model
@Import({ springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class })
public class SwaggerConfiguration {

	/**
	 * @return new Docket
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select() //
				.apis(RequestHandlerSelectors.basePackage(AbstractController.class.getPackage().getName())) //
				.paths(PathSelectors.any()) //
				.build() //
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		final String version = CdsApplication.class.getPackage().getImplementationVersion();
		return new ApiInfoBuilder() //
				.title("Acumos Common Data Service REST API") //
				.description("Provides create, retrieve, update and delete (CRUD) services for " //
						+ " Acumos data including solutions, artifacts and users. "//
						+ " All service endpoints require basic HTTP authentication.")
				.termsOfServiceUrl("Terms of service") //
				.contact(new Contact("Acumos Dev Team", //
						"http://acumos.readthedocs.io/en/latest/submodules/common-dataservice/docs/", //
						"noreply@acumos.org")) //
				.license("Apache 2.0 License").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0") //
				.version(version == null ? "version not available" : version) //
				.build();
	}
}
