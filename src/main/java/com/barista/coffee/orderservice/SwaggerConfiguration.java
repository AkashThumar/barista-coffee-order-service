package com.barista.coffee.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.barista.coffee.orderservice.controller"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(this.apiInfo())
				.useDefaultResponseMessages(false);
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfo("Barista Coffee Order Service",
				"Order Service is used for order resource information get/fetch/update/insert/delete", 
				"v1.0",
				null,
				new Contact("Akash Thumar", null,"akashthumar@gmail.com"),
				null,
				null);
	}
}