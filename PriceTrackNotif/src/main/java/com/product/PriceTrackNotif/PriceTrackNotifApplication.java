package com.product.PriceTrackNotif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PriceTrackNotifApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceTrackNotifApplication.class, args);
	}

}
