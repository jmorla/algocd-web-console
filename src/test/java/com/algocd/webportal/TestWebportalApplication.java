package com.algocd.webportal;

import org.springframework.boot.SpringApplication;

public class TestWebportalApplication {

	public static void main(String[] args) {
		SpringApplication.from(WebportalApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
