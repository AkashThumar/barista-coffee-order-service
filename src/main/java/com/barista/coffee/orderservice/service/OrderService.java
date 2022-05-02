package com.barista.coffee.orderservice.service;

import com.barista.coffee.orderservice.bean.OrderRequest;
import com.barista.coffee.orderservice.bean.OrderResponse;
import com.barista.coffee.orderservice.bean.OrderUpdateRequest;

public interface OrderService {
	
	public OrderResponse placeOrder(OrderRequest orderRequest);
	
	public OrderResponse getOrderStatus(Long orderNumber);
	
	public void updateOrderStatus(OrderUpdateRequest orderUpdateRequest);
	
}
