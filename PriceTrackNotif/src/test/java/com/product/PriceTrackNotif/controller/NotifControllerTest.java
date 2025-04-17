package com.product.PriceTrackNotif.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.PriceTrackNotif.model.NotifRequest;
import com.product.PriceTrackNotif.service.NotifService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(NotifController.class)
public class NotifControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private NotifService notifService;

	private NotifRequest request;

	private final ObjectMapper objectMapper = new ObjectMapper();

	// creating a shared sample request and this runs before each test cases
	@BeforeEach
	public void setup() {
		request = new NotifRequest();
		request.setProductUrl("https://www.amazon.co.uk/Duracell-CR2032-Lithium-Coin-Batteries/dp/B01CG0TO76");
		request.setDesiredPrice(2);
		request.setFrequency("ALWAYS");
		request.setEmailId("pkjaseela01@gmail.com");
	}

	// Test Scenario for successful alert creation response from service class
	@Test
	public void testCreatePriceAlert_Success() throws Exception {
		// Arrange
		when(notifService.createPriceAlert(any())).thenReturn("Alert has been set successfully!");

		// Act
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/configure/alerts")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn();
		// parsing response
		int status = result.getResponse().getStatus();
		String content = result.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(content, Map.class);

		// Assert
		assertEquals(200, status);
		assertTrue(responseMap.containsKey("message"));
		assertEquals("Alert has been set successfully!", responseMap.get("message"));
	}

	// Test Scenario for invalid input, here given the input 'DAILY' as frequency
	// which is considered invalid
	@Test
	public void testInvalidInput_Success() throws Exception {
		// Arrange
		request.setFrequency("DAILY");

		// Act
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/configure/alerts")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn();
		// parsing response
		int status = result.getResponse().getStatus();
		String content = result.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(content, Map.class);

		// Assert
		assertEquals(400, status);
		assertTrue(responseMap.containsKey("message"));
		assertEquals("Invalid request. Please check the inputs.", responseMap.get("message"));
	}
}
