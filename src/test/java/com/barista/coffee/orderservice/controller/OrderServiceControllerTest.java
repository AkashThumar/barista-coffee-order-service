package com.barista.coffee.orderservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.validation.Validation;
import javax.validation.Validator;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.barista.coffee.orderservice.OrderServiceControllerAdvice;
import com.barista.coffee.orderservice.bean.OrderRequest;
import com.barista.coffee.orderservice.bean.OrderStatusEnum;
import com.barista.coffee.orderservice.bean.OrderUpdateRequest;
import com.barista.coffee.orderservice.service.impl.OrderServiceImpl;

@SpringBootTest
public class OrderServiceControllerTest {

	@InjectMocks
	OrderServiceController orderServiceController;

	@Mock
	private OrderServiceImpl orderService;

	private Validator validator;

	private MockMvc mockMvc;

	@BeforeEach()
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		this.mockMvc = MockMvcBuilders.standaloneSetup(orderServiceController)
				.setControllerAdvice(OrderServiceControllerAdvice.class).build();

		ReflectionTestUtils.setField(orderServiceController, "validator", validator);
	}

	@Test
	public void testPlaceOrder_BadRequest() throws Exception {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCoffee("Coffee");
		orderRequest.setQuantity(0);
		mockMvc.perform(post("/v1/order/buy").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.prepareRequest(orderRequest)))
				.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}

	@Test
	public void testPlaceOrder() throws Exception {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCoffee("Coffee");
		orderRequest.setQuantity(10);
		mockMvc.perform(post("/v1/order/buy").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.prepareRequest(orderRequest))).andExpect(status().is(HttpStatus.CREATED.value()));
	}

	@Test
	public void testGetOrder() throws Exception {
		mockMvc.perform(get("/v1/order/buy").queryParam("orderNumber", "1234"))
				.andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	public void testUpdateOrderStatus_BadRequest() throws Exception {
		OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
		orderUpdateRequest.setOrderNumber(1234l);
		orderUpdateRequest.setOrderStatus(null);
		mockMvc.perform(put("/v1/order/buy").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.prepareRequest(orderUpdateRequest)))
				.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}

	@Test
	public void testUpdateOrderStatus() throws Exception {
		OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
		orderUpdateRequest.setOrderNumber(1234l);
		orderUpdateRequest.setOrderStatus(OrderStatusEnum.READY);
		mockMvc.perform(put("/v1/order/buy").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.prepareRequest(orderUpdateRequest)))
				.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	public String prepareRequest(Object request) {
		return new JSONObject(request).toString();
	}
}
