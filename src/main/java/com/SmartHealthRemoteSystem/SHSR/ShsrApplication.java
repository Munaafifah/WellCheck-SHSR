package com.SmartHealthRemoteSystem.SHSR;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

  
@SpringBootApplication
@ComponentScan(basePackages = "com.SmartHealthRemoteSystem.SHSR")
public class ShsrApplication{


	public static void main(String[] args) {
		SpringApplication.run(ShsrApplication.class, args);

	}
}


//trying
