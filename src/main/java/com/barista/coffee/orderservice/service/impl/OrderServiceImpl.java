package com.barista.coffee.orderservice.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.barista.coffee.orderservice.OrderServiceException;
import com.barista.coffee.orderservice.bean.ErrorBean;
import com.barista.coffee.orderservice.bean.OrderRequest;
import com.barista.coffee.orderservice.bean.OrderResponse;
import com.barista.coffee.orderservice.bean.OrderStatusEnum;
import com.barista.coffee.orderservice.bean.OrderUpdateRequest;
import com.barista.coffee.orderservice.bean.Product;
import com.barista.coffee.orderservice.helper.RestClientHelper;
import com.barista.coffee.orderservice.model.Order;
import com.barista.coffee.orderservice.repository.OrderRepository;
import com.barista.coffee.orderservice.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private static final String ERROR_MESSAGE_BCOS_104 = "Order status can not be update!";
	private static final String ERROR_CODE_BCOS_104 = "BCOS-104";
	private static final String ERROR_MESSAGE_BCOS_103 = "Order can not be placed!";
	private static final String ERROR_CODE_BCOS_103 = "BCOS-103";

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestClientHelper restClientHepler;

	@Value("${api.barista.coffee.product.API:http://localhost:8080/v1/product/{name}}")
	private String productAPI;

	@Override
	public OrderResponse placeOrder(OrderRequest orderRequest) {
		// call put API of product service to check and update product quantity
		Product product = this.checkAvailableQuantity(orderRequest);
		if (null != product && null != product.getQuantity()) {
			// if success response create order record
			Order order = this.createOrderRecord(orderRequest, product);

			OrderResponse orderResponse = new OrderResponse();
			orderResponse.setOrderNumber(order.getNumber());
			orderResponse.setOrderStatus(OrderStatusEnum.valueOf(order.getStatus()));
			return orderResponse;
		} else {
			// else throw exception saying order can not be cater for now
			throw new OrderServiceException(HttpStatus.UNPROCESSABLE_ENTITY,
					new ErrorBean(ERROR_CODE_BCOS_103, ERROR_MESSAGE_BCOS_103), null);
		}
	}

	/**
	 *
	 */
	@Override
	public OrderResponse getOrderStatus(Long orderNumber) {
		// bases on order number fetch order record and status
		Order order = orderRepository.findByNumber(orderNumber);
		if (null != order) {
			OrderResponse orderResponse = new OrderResponse();
			orderResponse.setOrderNumber(order.getNumber());
			orderResponse.setOrderStatus(OrderStatusEnum.valueOf(order.getStatus()));
			return orderResponse;
		} else {
			throw new OrderServiceException(HttpStatus.NOT_FOUND,
					new ErrorBean(ERROR_CODE_BCOS_104, ERROR_MESSAGE_BCOS_104), null);
		}
	}

	@Override
	public void updateOrderStatus(OrderUpdateRequest orderUpdateRequest) {
		// bases on order number in request update the status of the order number
		Order order = orderRepository.findByNumber(orderUpdateRequest.getOrderNumber());
		if (null != order
				&& orderUpdateRequest.getOrderStatus().compareTo(OrderStatusEnum.valueOf(order.getStatus().trim())) > 0) {
			order.setStatus(orderUpdateRequest.getOrderStatus().toString());
			orderRepository.save(order);
		} else {
			throw new OrderServiceException(HttpStatus.UNPROCESSABLE_ENTITY,
					new ErrorBean(ERROR_CODE_BCOS_104, ERROR_MESSAGE_BCOS_104), null);
		}
	}

	private Product checkAvailableQuantity(OrderRequest orderRequest) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("name", orderRequest.getCoffee());
		try {
			ResponseEntity<Product> productResponse = restClientHepler.invokeRestEndpoint(HttpMethod.PUT, productAPI,
					orderRequest, parameters, new HttpHeaders(), Product.class);
			return productResponse.getBody();
		} catch (OrderServiceException serviceException) {
			throw serviceException;
		}
	}

	private Order createOrderRecord(OrderRequest orderRequest, Product productResponse) {
		Order order = new Order();
		order.setProductname(productResponse.getProductName());
		order.setAmount(new BigDecimal(productResponse.getAmount().doubleValue()
				* BigDecimal.valueOf(orderRequest.getQuantity()).doubleValue()));
		order.setQuantity(orderRequest.getQuantity());
		order.setStatus(OrderStatusEnum.PLACED.toString());
		order.setDate(new Timestamp(System.currentTimeMillis()));
		order.setNumber(orderRepository.getOrderNumber());
		order = orderRepository.save(order);
		return order;
	}
}