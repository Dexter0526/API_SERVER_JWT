package com.api.dex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//@SpringBootApplication(exclude = {MongoAutoConfiguration.class, // spring boot 에서 default 사용하는거 뺌
//		MongoDataAutoConfiguration.class,  // 현재 하이버네이트 방언이 Mysql 로 되어있음.
//		MultipartAutoConfiguration.class}) // Tomcat 의 Multipart Library 사용하기 때문에 spring boot 의 Multipart 기능을 exclude 함.
@SpringBootApplication
public class DexApplication extends SpringBootServletInitializer {
	public static final String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.yml,"
			+ "classpath:aws.yml";

//	public static void main(String[] args) {
//		SpringApplication.run(DexApplication.class, args);
//	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(DexApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);
	}
//	@Override protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(DexApplication.class);
//	}

}
