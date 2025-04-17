package com.product.PriceTrackNotif.model;

/**
 * Represents a product being tracked for price changes. This model is used
 * internally within the application to encapsulate product details such as URL,
 * name, and current price when fetching the details from json file.
 */
public class Product {

	private String productUrl;
	private String productName;
	private double currentPrice;

	public Product(String productUrl, String productName, double currentPrice) {
		super();
		this.productUrl = productUrl;
		this.productName = productName;
		this.currentPrice = currentPrice;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

}
