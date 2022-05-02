package com.barista.coffee.orderservice.bean;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrderRequest {

	@NotBlank(message = "coffee can not be null or empty")
	private String coffee;
	
	@NotNull(message = "quantity can not be null")
	@Min(value = 1, message = "quantity must be atleast 1")
	private Integer quantity;

	public String getCoffee() {
		return coffee;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
