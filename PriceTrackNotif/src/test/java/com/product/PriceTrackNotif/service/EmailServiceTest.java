package com.product.PriceTrackNotif.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private EmailService emailService;
	@Value("${spring.mail.username}")
	private String fromEmail;
	private String toEmail;
	private String subject;
	private String body;

	// Initialize test data
	@BeforeEach
	public void setup() {
		toEmail = "pkjaseela01@gmail.com";
		subject = "Test Email";
		body = "This is for testing.";
	}

	// Test Scenario for sending email successfully
	@Test
	public void testSendNotification_Success() {
		// Act
		emailService.sendNotification(toEmail, subject, body);

		// Capture the email message sent to JavaMailSender
		ArgumentCaptor<SimpleMailMessage> captor = forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());

		// Assert that the email details match the expected values
		SimpleMailMessage capturedMessage = captor.getValue();
		assertEquals(fromEmail, capturedMessage.getFrom());
		assertEquals(toEmail, capturedMessage.getTo()[0]);

	}

	// Test Scenario for failure in sending email
	@Test
    public void testSendNotification_Failure() {
        // Arrange
        doThrow(new MailException("Failed to send email") {
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendNotification(toEmail, subject, body);
        });

        // Assert
        assertEquals("Failed to send email to " + toEmail, exception.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}
