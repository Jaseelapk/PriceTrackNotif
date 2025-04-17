package com.product.PriceTrackNotif.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.product.PriceTrackNotif.model.NotifRequest;
import com.product.PriceTrackNotif.model.Notification;
import com.product.PriceTrackNotif.model.Product;
import com.product.PriceTrackNotif.repository.NotifRepository;

@ExtendWith(MockitoExtension.class)
public class NotifServiceTest {

	@Mock
	private NotifRepository notifRepository;

	@Mock
	private EmailService emailService;

	@Mock
	private ProductService productService;

	private NotifRequest request;

	@InjectMocks
	private NotifService notifService;

	// creating a shared sample request and this runs before each test cases
	@BeforeEach
	public void setup() {
		request = new NotifRequest();
		request.setProductUrl("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76");
		request.setDesiredPrice(2);
		request.setFrequency("ALWAYS");
		request.setEmailId("pkjaseela01@gmail.com");

	}

	// Test scenario for successful alert creation
	@Test
	public void testCreateAlert_Success() {
		// Arrange
		Product product = new Product("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76",
				"Batteries", 5);

		// Mocking service calls
		when(productService.fetchProduct(request.getProductUrl())).thenReturn(product);

		// Act
		String result = notifService.createPriceAlert(request);

		// Assert
		ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
		verify(notifRepository, times(1)).save(captor.capture());
		Notification savedNotification = captor.getValue();

		assertEquals("Alert has been set successfully!", result);
		assertEquals("PENDING", savedNotification.getStatus());
		assertEquals(request.getProductUrl(), savedNotification.getProductUrl());
	}

	// Test scenario for condition price drop detected and alert send successfully
	@Test
	public void testPriceDropDetected_AlertSend() {
		// Arrange
		request.setDesiredPrice(5);
		Product product = new Product("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76",
				"Batteries", 5);

		// Mocking service calls
		when(productService.fetchProduct(request.getProductUrl())).thenReturn(product);
		doNothing().when(emailService).sendNotification(anyString(), anyString(), anyString());
		// Act
		String result = notifService.createPriceAlert(request);

		// Assert
		ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
		verify(notifRepository, times(1)).save(captor.capture());
		Notification savedNotification = captor.getValue();

		assertEquals("Price drop detected and alert has been send successfully.", result);
		assertEquals("COMPLETED", savedNotification.getStatus());
		verify(emailService, times(1)).sendNotification(anyString(), anyString(), anyString());
	}

	// Test Scenario for product not found in static json file for price comparison.
	@Test
	public void testProductNot_Found() {
		// Arrange
		request.setProductUrl("https://www.amazon.co.uk/fire-hd-8-2024-release/dp/B0CVDRFHJ6");

		// Mocking service calls
		when(productService.fetchProduct(request.getProductUrl())).thenReturn(null);

		// Act
		String result = notifService.createPriceAlert(request);

		// Assert
		assertEquals("Product not found.", result);
	}
}
