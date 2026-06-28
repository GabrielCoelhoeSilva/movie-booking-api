package com.gabriel.moviebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieBookingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieBookingApiApplication.class, args);
	}

}
