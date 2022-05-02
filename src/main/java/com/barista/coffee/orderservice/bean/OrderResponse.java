package com.barista.coffee.orderservice.bean;

import java.io.Serializable;

public class OrderResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5917632573148481691L;
	
	private Long orderNumber;
	private OrderStatusEnum orderStatus;

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public OrderStatusEnum getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatusEnum orderStatus) {
		this.orderStatus = orderStatus;
	}

}
