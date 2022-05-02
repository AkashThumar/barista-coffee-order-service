package com.barista.coffee.orderservice.bean;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class OrderUpdateRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8159947749087229739L;

	@NotNull(message = "Order Number can not be null")
	private Long orderNumber;

	@NotNull(message = "Order Status can not be null")
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
