package com.example.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CourseCatalogueApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseCatalogueApplication.class, args);
	}


}
