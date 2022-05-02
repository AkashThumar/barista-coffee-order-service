package com.barista.coffee.orderservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.barista.coffee.orderservice.model.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

	@Query(nativeQuery = true, value = "select nextval('coffee_shop.order_number_seq')")
	public Long getOrderNumber();
	
	public Order findByNumber(Long number);
}
