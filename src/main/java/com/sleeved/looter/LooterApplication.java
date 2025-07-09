package com.sleeved.looter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sleeved.looter")
public class LooterApplication {

	public static void main(String[] args) {
        SpringApplication.run(LooterApplication.class, args);
	}

}
