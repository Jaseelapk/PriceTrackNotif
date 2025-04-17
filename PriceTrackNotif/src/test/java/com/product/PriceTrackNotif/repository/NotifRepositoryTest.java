package com.product.PriceTrackNotif.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.product.PriceTrackNotif.model.Notification;
import com.product.PriceTrackNotif.repository.NotifRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class NotifRepositoryTest {

	@Autowired
	private NotifRepository notifRepository;

	@Test
	public void testSaveNotification_Success() {

		// Arrange
		Notification notification = new Notification();
		notification.setEmailid("pkjaseela01@gmail.com");
		notification.setProductUrl("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76");
		notification.setDesiredPrice(5);
		notification.setFrequency("ALWAYS");
		notification.setStatus("PENDING");
		notification.setCreatedAt(LocalDateTime.now());

		// Act
		Notification savedNotification = notifRepository.save(notification);

		// Assert
		assertThat(savedNotification).isNotNull();
		assertThat(savedNotification.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindByStatus_Success() {
		// Arrange
		Notification notification1 = new Notification();
		notification1.setEmailid("pkjaseela01@gmail.com");
		notification1.setProductUrl("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76");
		notification1.setDesiredPrice(5);
		notification1.setFrequency("ALWAYS");
		notification1.setStatus("PENDING");
		notification1.setCreatedAt(LocalDateTime.now());

		Notification notification2 = new Notification();
		notification2.setEmailid("jaseelapk01@gmail.com");
		notification2.setProductUrl("https://www.amazon.co.uk/Guess-How-Much-Love-You/dp/1406358789");
		notification2.setDesiredPrice(6);
		notification2.setFrequency("ALWAYS");
		notification2.setStatus("COMPLETED");
		notification2.setCreatedAt(LocalDateTime.now());
		notifRepository.save(notification1);
		notifRepository.save(notification2);

		// Act
		List<Notification> pendingNotifs = notifRepository.findByStatus("PENDING");

		// Assert
		assertThat(pendingNotifs).hasSize(1);
		assertThat(pendingNotifs.get(0).getEmailId()).isEqualTo("pkjaseela01@gmail.com");
	}

	// Test case in case no pending records found
	@Test
	public void testFindByStatus_NoMatchingNotifications() {
		// Act
		List<Notification> notifications = notifRepository.findByStatus("PENDING");
		// Assert
		assertThat(notifications).isEmpty();
	}

}
