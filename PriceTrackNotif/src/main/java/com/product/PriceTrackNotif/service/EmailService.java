package com.product.PriceTrackNotif.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for sending email notifications to users.
 */
@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendNotification(String toEmail, String subject, String body) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(toEmail);
			message.setSubject(subject);
			message.setText(body);

			logger.info("Sending email to {} with subject: {}", toEmail, subject);
			mailSender.send(message);
			logger.debug("Email successfully sent to {}", toEmail);

		} catch (Exception e) {
			logger.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
		}
	}
}