package com.barista.coffee.orderservice.controller;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barista.coffee.orderservice.OrderServiceException;
import com.barista.coffee.orderservice.bean.ErrorBean;
import com.barista.coffee.orderservice.bean.OrderRequest;
import com.barista.coffee.orderservice.bean.OrderResponse;
import com.barista.coffee.orderservice.bean.OrderUpdateRequest;
import com.barista.coffee.orderservice.service.OrderService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class OrderServiceController {

	@Autowired
	private Validator validator;

	@Autowired
	private OrderService orderService;

	@ApiOperation(value = "Order place API", notes = "Order place API", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Order placed Successfully", response = OrderResponse.class),
			@ApiResponse(code = 400, message = "Bad request parameters"),
			@ApiResponse(code = 422, message = "Order can not be created", response = ErrorBean.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ErrorBean.class) })
	@PostMapping(value = "/v1/order/buy", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
		Set<ConstraintViolation<OrderRequest>> validationErrors = validator.validate(orderRequest, Default.class);
		if (null != validationErrors && !validationErrors.isEmpty()) {
			String message = validationErrors.stream().findFirst().get().getMessage();
			throw new OrderServiceException(HttpStatus.UNPROCESSABLE_ENTITY, new ErrorBean("BCOS-100", message), null);
		} else {
			return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED);
		}
	}

	@ApiOperation(value = "Order status GET API", notes = "Order status GET API", httpMethod = "GET")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Order status get successfully", response = OrderResponse.class),
			@ApiResponse(code = 400, message = "Bad request parameters"),
			@ApiResponse(code = 404, message = "Order not found!", response = ErrorBean.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ErrorBean.class) })
	@GetMapping(value = "/v1/order/buy", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getOrder(@RequestParam(name = "orderNumber", required = true) Long orderNumber) {
		return new ResponseEntity<>(orderService.getOrderStatus(orderNumber), HttpStatus.OK);
	}

	@ApiOperation(value = "Order status update API", notes = "Order status update API", httpMethod = "PUT")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Order status updated Successfully"),
			@ApiResponse(code = 400, message = "Bad request parameters"),
			@ApiResponse(code = 422, message = "Order status can be updated!", response = ErrorBean.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ErrorBean.class) })
	@PutMapping(value = "/v1/order/buy", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateOrderStatus(@RequestBody OrderUpdateRequest orderUpdateRequest) {
		Set<ConstraintViolation<OrderUpdateRequest>> validationErrors = validator.validate(orderUpdateRequest,
				Default.class);
		if (null != validationErrors && !validationErrors.isEmpty()) {
			String message = validationErrors.stream().findFirst().get().getMessage();
			throw new OrderServiceException(HttpStatus.UNPROCESSABLE_ENTITY, new ErrorBean("BCOS-101", message), null);
		} else {
			orderService.updateOrderStatus(orderUpdateRequest);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

}
