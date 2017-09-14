package com.erp.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = { "com.erp" })
@EnableAsync
public class ErpMain {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ErpMain.class, args);
	}
}
