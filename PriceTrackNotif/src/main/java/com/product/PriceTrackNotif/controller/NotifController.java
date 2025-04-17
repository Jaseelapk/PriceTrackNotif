package com.product.PriceTrackNotif.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.PriceTrackNotif.model.NotifRequest;
import com.product.PriceTrackNotif.service.NotifService;

/**
 * NotifController is responsible for handling HTTP requests related to alert configurations.
 * It exposes an endpoint for users to create price alerts for products.
 */
@RestController
@RequestMapping("/configure/alerts")
public class NotifController {

	private static final Logger logger = LoggerFactory.getLogger(NotifController.class);
	@Autowired
	NotifService notifservice;

	@PostMapping
	public ResponseEntity<Map<String, String>> createAlert(@RequestBody NotifRequest request) {
		logger.info("Received alert creation request: {}", request);
		if (request.getProductUrl() == null || request.getProductUrl().isBlank() || request.getEmailId() == null
				|| request.getEmailId().isBlank() || request.getDesiredPrice() <= 0
				|| (!request.getFrequency().equals("ALWAYS") && !request.getFrequency().equals("MORNING")
						&& !request.getFrequency().equals("MIDNIGHT"))) {
			logger.warn("Invalid request received: {}", request);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Invalid request. Please check the inputs."));
		}

		logger.info("Alert created successfully for URL: {}", request.getProductUrl());
		String result = notifservice.createPriceAlert(request);
		return ResponseEntity.ok(Map.of("message", result));
	}
}
