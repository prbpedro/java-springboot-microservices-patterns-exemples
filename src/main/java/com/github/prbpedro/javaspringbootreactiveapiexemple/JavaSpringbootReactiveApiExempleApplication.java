package com.github.prbpedro.javaspringbootreactiveapiexemple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@SpringBootApplication(exclude = R2dbcAutoConfiguration.class)
public class JavaSpringbootReactiveApiExempleApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaSpringbootReactiveApiExempleApplication.class, args);
	}

}
