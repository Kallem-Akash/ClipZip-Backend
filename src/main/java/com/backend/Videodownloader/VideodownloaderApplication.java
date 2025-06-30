package com.backend.Videodownloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RapidApiProperties.class)
public class VideodownloaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideodownloaderApplication.class, args);
	}

}
