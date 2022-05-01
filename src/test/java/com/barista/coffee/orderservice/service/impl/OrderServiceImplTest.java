package com.barista.coffee.orderservice.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.barista.coffee.orderservice.model.Order;
import com.barista.coffee.orderservice.repository.OrderRepository;

@SpringBootTest
public class OrderServiceImplTest {
	
	@Autowired
	OrderRepository orderRepository;
	
	@Test
	public void testOrderTable() {
		Order order = new Order();
		order.setProductname("Barista");
		order.setQuantity(10);
		order.setAmount(BigDecimal.valueOf(50));
		order.setDate(new Timestamp(System.currentTimeMillis()));
		order.setNumber(orderRepository.getOrderNumber());
		order.setStatus("RECEIVED");
		
		orderRepository.save(order);
	}

}
