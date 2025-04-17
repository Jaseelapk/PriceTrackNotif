package com.product.PriceTrackNotif.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.PriceTrackNotif.model.Product;

import org.springframework.beans.factory.annotation.Value;


/**
 * Service responsible for loading product data and fetching specific product
 * information based on a given URL.
 */
@Service
public class ProductService {
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
	private final ObjectMapper objectMapper;

	@Value("${product.data.file}")
	private String productDataFile;

	public ProductService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Product fetchProduct(String productUrl) {
        if (productUrl == null || productUrl.isEmpty()) {
            logger.warn("Invalid product URL received: '{}'", productUrl);
            throw new IllegalArgumentException("Product URL cannot be null or empty");
        }

        logger.info("Fetching product for URL: {}", productUrl);

        try (InputStream inputStream = getClass().getResourceAsStream(productDataFile)) {
            if (inputStream == null) {
                logger.error("File not found: {}", productDataFile);
                throw new RuntimeException("File not found: " + productDataFile);
            }

            List<Product> products = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});
            Product matchedProduct = products.stream()
                .filter(p -> p.getProductUrl().equalsIgnoreCase(productUrl))
                .findFirst()
                .orElse(null);

            if (matchedProduct != null) {
                logger.debug("Found product: {} with price {}", matchedProduct.getProductName(), matchedProduct.getCurrentPrice());
            } else {
                logger.info("No product found for URL: {}", productUrl);
            }

            return matchedProduct;

        } catch (IOException e) {
            logger.error("Failed to load or parse the file: {}", productDataFile, e);
            throw new RuntimeException("Failed to load or parse the file: " + productDataFile, e);
        }
    }

    public List<Product> loadProductData() {
        logger.info("Loading product data from file: {}", productDataFile);
        try (InputStream inputStream = getClass().getResourceAsStream(productDataFile)) {
            if (inputStream == null) {
                logger.error("File not found: {}", productDataFile);
                throw new RuntimeException("File not found: " + productDataFile);
            }

            List<Product> products = objectMapper.readValue(inputStream, new TypeReference<>() {});
            logger.debug("Loaded {} products from file", products.size());
            return products;

        } catch (IOException e) {
            logger.error("Failed to load the file: {}", productDataFile, e);
            throw new RuntimeException("Failed to load the file: " + productDataFile, e);
        }
    }
}
