package com.product.PriceTrackNotif.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.product.PriceTrackNotif.model.NotifRequest;
import com.product.PriceTrackNotif.model.Notification;
import com.product.PriceTrackNotif.model.Product;
import com.product.PriceTrackNotif.repository.NotifRepository;

/**
 * Service class responsible for handling business logic related to
 * creating and managing price drop alerts.
 */
@Service
public class NotifService {
	private static final Logger logger = LoggerFactory.getLogger(NotifService.class);
	private final EmailService emailService;
	private final ProductService productService;
	private final NotifRepository notifrepository;

	public NotifService(EmailService emailService, ProductService productService, NotifRepository notifrepository) {
		this.emailService = emailService;
		this.productService = productService;
		this.notifrepository = notifrepository;
	}

	public String createPriceAlert(NotifRequest request) {
		logger.info("Received price alert request for URL: {} and email: {}", request.getProductUrl(),
				request.getEmailId());

		Product product = productService.fetchProduct(request.getProductUrl());

		if (product == null) {
			logger.warn("Product not found for URL: {}", request.getProductUrl());
			return "Product not found.";
		}

		Notification notification = new Notification();
		notification.setProductUrl(request.getProductUrl());
		notification.setDesiredPrice(request.getDesiredPrice());
		notification.setEmailid(request.getEmailId());
		notification.setFrequency(request.getFrequency());
		notification.setCreatedAt(LocalDateTime.now());

		if (product.getCurrentPrice() <= request.getDesiredPrice()) {
			logger.info("Price drop matched for product '{}'. Sending email to {}", product.getProductName(),
					request.getEmailId());

			String subject = "Price Drop Alert for " + product.getProductName();
			String body = "The price for your product " + product.getProductName() + " is $" + product.getCurrentPrice()
					+ "\nClick to purchase: " + product.getProductUrl();

			emailService.sendNotification(request.getEmailId(), subject, body);
			notification.setStatus("COMPLETED");
			notifrepository.save(notification);

			logger.debug("Notification saved with status COMPLETED for product: {}", product.getProductName());
			return "Price drop detected and alert has been sent successfully.";
		}

		notification.setStatus("PENDING");
		notifrepository.save(notification);
		logger.debug("Notification saved with status PENDING for product: {}", product.getProductName());
		return "Alert has been set successfully!";
	}
}