package com.product.PriceTrackNotif.model;

/**
 * Data Transfer Object representing a user's request to create a price alert.
 * This object is used to transfer data from the client to the server when
 * setting up a new price tracking notification.
 */
public class NotifRequest {
	private String productUrl;
	private double desiredPrice;
	private String frequency;
	private String emailId;

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public double getDesiredPrice() {
		return desiredPrice;
	}

	public void setDesiredPrice(double desiredPrice) {
		this.desiredPrice = desiredPrice;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

}
