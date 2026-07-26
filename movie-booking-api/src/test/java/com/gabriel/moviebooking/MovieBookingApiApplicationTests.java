package com.gabriel.moviebooking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableCaching
@ActiveProfiles("test")
class MovieBookingApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
