package com.product.PriceTrackNotif.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.PriceTrackNotif.model.Notification;
import com.product.PriceTrackNotif.model.Product;
import com.product.PriceTrackNotif.repository.NotifRepository;
import com.product.PriceTrackNotif.service.EmailService;
import com.product.PriceTrackNotif.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class NotifSchedulerTest {

    @Mock
    private NotifRepository notifRepository;

    @Mock
    private EmailService emailService;
    
    @Mock
    private ProductService productService;

    @InjectMocks
    private NotifScheduler notifScheduler;

    private List<Notification> mockNotifications;
    private List<Product> mockProductList;

    
    //Test Scenario for loading pending status record and sending email for only price drop item
    @Test
    public void testCheckPending_Success() {
        // Arrange: Mock the behavior of loading product data
    	 Notification notification1 = new Notification();
 		notification1.setEmailid("pkjaseela01@gmail.com");
 		notification1.setProductUrl("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76");
 		notification1.setDesiredPrice(2);
 		notification1.setFrequency("ALWAYS");
 		notification1.setStatus("PENDING");

 		Notification notification2 = new Notification();
 		notification2.setEmailid("jaseelapk01@gmail.com");
 		notification2.setProductUrl("https://www.amazon.co.uk/Guess-How-Much-Love-You/dp/1406358789");
 		notification2.setDesiredPrice(3);
 		notification2.setFrequency("ALWAYS");
 		notification2.setStatus("PENDING");

         mockNotifications = List.of(notification1, notification2);

         //mock data for product list
         Product product1 = new Product("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76",
 				"Batteries", 2);
         Product product2 = new Product("https://www.amazon.co.uk/Guess-How-Much-Love-You/dp/1406358789",
 				"Story Book", 6);
        

         mockProductList = List.of(product1, product2);

         when(notifRepository.findByStatus("PENDING")).thenReturn(mockNotifications);
     	when(productService.loadProductData()).thenReturn(mockProductList);

    	when(notifRepository.findByStatus("PENDING")).thenReturn(mockNotifications);
    	when(productService.loadProductData()).thenReturn(mockProductList);

    	//Act
    	notifScheduler.checkPendingAlerts();

    	//Assert:Veryfying alert send and status is changed to completed for price droppped product.
    	verify(emailService).sendNotification(eq("pkjaseela01@gmail.com"), anyString(), contains("2"));
        assertEquals("COMPLETED", mockNotifications.get(0).getStatus());
        verify(notifRepository).save(mockNotifications.get(0));
       //Assert: verifying status is still pending for product without price drop
        assertEquals("PENDING", mockNotifications.get(1).getStatus());
        
    }
    
    
    @Test
    void testCheckPendingAlerts_frequencyMismatch_noNotificationSent() {
        // Setting up a notification with frequency MORNING but simulate a late-night time
        Notification notification = new Notification();
        notification.setEmailid("pkjaseela01@gmail.com");
		notification.setProductUrl("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76");
		notification.setDesiredPrice(2);
		notification.setFrequency("MORNING");
		notification.setStatus("PENDING");

		// price drop condition met for product
		Product product = new Product("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76",
				"Batteries", 2); 

        lenient().when(notifRepository.findByStatus("PENDING")).thenReturn(List.of(notification));
        lenient().when(productService.loadProductData()).thenReturn(List.of(product));

        // Manually override the time-check method to simulate frequency mismatch (optional)
        NotifScheduler schedulerSpy = Mockito.spy(notifScheduler);
        lenient().doReturn(false).when(schedulerSpy).isTimeToCheck(eq("MORNING"), any(LocalTime.class));

        schedulerSpy.checkPendingAlerts();

        verify(emailService, never()).sendNotification(anyString(), anyString(), anyString());
        verify(notifRepository, never()).save(any(Notification.class));
    }
    
 
    
    
}
