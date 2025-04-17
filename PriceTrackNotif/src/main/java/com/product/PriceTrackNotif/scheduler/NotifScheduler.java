package com.product.PriceTrackNotif.scheduler;

import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.PriceTrackNotif.model.Notification;
import com.product.PriceTrackNotif.model.Product;
import com.product.PriceTrackNotif.repository.NotifRepository;
import com.product.PriceTrackNotif.service.EmailService;
import com.product.PriceTrackNotif.service.ProductService;


/**
 * Scheduled service responsible for checking pending price drop alerts
 * and triggering email notifications when conditions are met.
 */
@Service
public class NotifScheduler {
	private static final Logger logger = LoggerFactory.getLogger(NotifScheduler.class);
	private final NotifRepository notifRepository;
	private final EmailService emailService;
	private final ProductService productService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public NotifScheduler(NotifRepository notifRepository, EmailService emailService, ProductService productService) {
		this.notifRepository = notifRepository;
		this.emailService = emailService;
		this.productService = productService;
	}

	@Scheduled(fixedRate = 60000) // every minute
	public void checkPendingAlerts() {
		LocalTime now = LocalTime.now();
		logger.info("Running scheduled alert check at {}", now);

		List<Notification> pendingAlerts = notifRepository.findByStatus("PENDING");
		logger.debug("Found {} pending notifications", pendingAlerts.size());

		List<Product> productList = productService.loadProductData();
		logger.debug("Loaded {} products from product service", productList.size());
		for (Notification notification : pendingAlerts) {
			if (!isTimeToCheck(notification.getFrequency(), now)) {
				logger.debug("Skipping notification {} due to frequency '{}'", notification.getId(),
						notification.getFrequency());
				continue;
			}
			productList.stream().filter(product -> product.getProductUrl().equals(notification.getProductUrl()))
					.findFirst().ifPresent(product -> {
						logger.debug("Checking product '{}' with current price {}", product.getProductName(),
								product.getCurrentPrice());
						if (product.getCurrentPrice() <= notification.getDesiredPrice()) {
							logger.info("Price drop matched for product '{}'! Sending email to {}",
									product.getProductName(), notification.getEmailId());

							String subject = "Price Drop Alert for " + product.getProductName();
							String body = "The price for your product " + product.getProductName() + " is $"
									+ product.getCurrentPrice() + "\nClick to purchase: " + product.getProductUrl();

							emailService.sendNotification(notification.getEmailId(), subject, body);
							notification.setStatus("COMPLETED");
							notifRepository.save(notification);
							logger.debug("Updated notification {} status to COMPLETED", notification.getId());
						} else {
							logger.debug("Product '{}' did not meet desired price ({} > {})", product.getProductName(),
									product.getCurrentPrice(), notification.getDesiredPrice());
						}
					});
		}
	}

	public boolean isTimeToCheck(String frequency, LocalTime now) {
		return switch (frequency.toUpperCase()) {
		case "ALWAYS" -> true;
		case "MORNING" -> now.isAfter(LocalTime.of(6, 0)) && now.isBefore(LocalTime.of(9, 0));
		case "MIDNIGHT" -> now.isAfter(LocalTime.of(1, 0)) && now.isBefore(LocalTime.of(3, 0));
		default -> false;
		};
	}

}
