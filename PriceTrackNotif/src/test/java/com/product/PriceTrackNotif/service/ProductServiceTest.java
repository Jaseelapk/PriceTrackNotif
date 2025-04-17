package com.product.PriceTrackNotif.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.PriceTrackNotif.model.Product;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ObjectMapper objectMapper;

	private List<Product> mockProductList;

	private static final String TEST_FILE_PATH = "/productPrice.json";

	@BeforeEach
	public void setup() {
		// Initialize mock product data
		mockProductList = List.of(
				new Product("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76",
						"Batteries", 5),
				new Product("https://www.amazon.co.uk/Guess-How-Much-Love-You/dp/1406358789", "Story Book", 6));

		// Inject the file path property into the ProductService instance
		ReflectionTestUtils.setField(productService, "productDataFile", TEST_FILE_PATH);
	}

	// Test Scenario Product exists in the JSON file
	@Test
	public void testFetchProduct_Found() throws Exception {
		// Arrange
		String productUrl = "https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76";

		// Mock ObjectMapper behavior
		when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockProductList);

		// Act
		Product result = productService.fetchProduct(productUrl);

		// Assert
		assertEquals(productUrl, result.getProductUrl());
	}

	// Test Scenario Product not found in the JSON file
	@Test
	public void testFetchProduct_NotFound() throws Exception {
		// Arrange
		String productUrl = "https://www.amazon.co.uk/nonexistent-product";

		// Mock ObjectMapper behavior
		when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockProductList);

		// Act
		Product result = productService.fetchProduct(productUrl);

		// Assert
		assertNull(result);
	}

	// Test Scenario Successful data loading
	@Test
	public void testLoadProductData_Success() throws Exception {
		// Arrange
		when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockProductList);

		// Act
		List<Product> result = productService.loadProductData();

		// Assert
		assertEquals(2, result.size());
		assertEquals("Batteries", result.get(0).getProductName());
		assertEquals("Story Book", result.get(1).getProductName());
	}
}
