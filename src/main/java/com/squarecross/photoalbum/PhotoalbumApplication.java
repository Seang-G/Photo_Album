package com.squarecross.photoalbum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing
@ComponentScan//(basePackages = "org.springframework.security.crypto.password")
@SpringBootApplication//(exclude = { SecurityAutoConfiguration.class})
public class PhotoalbumApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoalbumApplication.class, args);
	}

}
