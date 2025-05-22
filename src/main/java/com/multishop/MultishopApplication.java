package com.multishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableSpringHttpSession
public class MultishopApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultishopApplication.class, args);
	}

}
