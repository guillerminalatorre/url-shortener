package com.codefactory.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlShortenerApplication {

	public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);

        //TODO: Add trace id to logs for better tracking
        // add chache to improve performance (Redis) and exception handling for it
	}

}
