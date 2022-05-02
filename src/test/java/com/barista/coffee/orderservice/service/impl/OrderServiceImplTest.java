package com.barista.coffee.orderservice.service.impl;

import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.barista.coffee.orderservice.OrderServiceException;
import com.barista.coffee.orderservice.bean.ErrorBean;
import com.barista.coffee.orderservice.bean.OrderRequest;
import com.barista.coffee.orderservice.bean.OrderStatusEnum;
import com.barista.coffee.orderservice.bean.OrderUpdateRequest;
import com.barista.coffee.orderservice.bean.Product;
import com.barista.coffee.orderservice.helper.RestClientHelper;
import com.barista.coffee.orderservice.model.Order;
import com.barista.coffee.orderservice.repository.OrderRepository;

@SpringBootTest
public class OrderServiceImplTest {

	@InjectMocks
	OrderServiceImpl orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private RestClientHelper restClientHepler;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testPlaceOrder() {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCoffee("coffee");
		orderRequest.setQuantity(1);

		Product product = new Product();
		product.setProductName("coffee");
		product.setQuantity(10);
		product.setAmount(new BigDecimal(50));

		Order savedOrderRecord = new Order();
		savedOrderRecord.setStatus("COMPLETED");
		savedOrderRecord.setNumber(123l);

		Mockito.when(restClientHepler.invokeRestEndpoint(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(product, HttpStatus.OK));
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(savedOrderRecord);

		Assertions.assertNotNull(orderService.placeOrder(orderRequest));
	}

	@Test
	public void testPlaceOrder_OrderCanNotBePlaced() {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCoffee("coffee");
		orderRequest.setQuantity(1);

		Mockito.when(restClientHepler.invokeRestEndpoint(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any()))
				.thenThrow(new OrderServiceException(HttpStatus.UNPROCESSABLE_ENTITY,
						new ErrorBean("not enough quantity", "not enough quantity"), null));

		try {
			orderService.placeOrder(orderRequest);
		} catch (OrderServiceException serviceException) {
			Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, serviceException.getStatus());
		}
	}

	@Test
	public void testPlaceOrder_QuantityNull() {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCoffee("coffee");
		orderRequest.setQuantity(1);

		Product product = new Product();
		product.setProductName("coffee");
		product.setQuantity(null);
		product.setAmount(new BigDecimal(50));

		Order savedOrderRecord = new Order();
		savedOrderRecord.setStatus("COMPLETED");
		savedOrderRecord.setNumber(123l);

		Mockito.when(restClientHepler.invokeRestEndpoint(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

		try {
			orderService.placeOrder(orderRequest);
		} catch (OrderServiceException serviceException) {
			Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, serviceException.getStatus());
		}
	}
	
	@Test
	public void testGetOrderStatus() {
		Order order = new Order();
		order.setNumber(1234l);
		order.setStatus(OrderStatusEnum.PLACED.toString());
		Mockito.when(orderRepository.findByNumber(Mockito.anyLong())).thenReturn(order);
		Assertions.assertNotNull(orderService.getOrderStatus(1234l));
	}
	
	@Test
	public void testGetOrderStatus_NotFound() {
		Mockito.when(orderRepository.findByNumber(Mockito.anyLong())).thenReturn(null);
		try {
			orderService.getOrderStatus(1234l);
		} catch (OrderServiceException serviceException) {
			Assertions.assertEquals(HttpStatus.NOT_FOUND, serviceException.getStatus());
		}
	}
	
	@Test
	public void testUpdateOrderStatus() {
		OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
		orderUpdateRequest.setOrderNumber(1234l);
		orderUpdateRequest.setOrderStatus(OrderStatusEnum.READY);
		
		Order order = new Order();
		order.setNumber(1234l);
		order.setStatus(OrderStatusEnum.PLACED.toString());
		
		Mockito.when(orderRepository.findByNumber(Mockito.anyLong())).thenReturn(order);
		orderService.updateOrderStatus(orderUpdateRequest);
		Mockito.verify(orderRepository, times(1)).save(Mockito.any());
	}
	
	@Test
	public void testUpdateOrderStatus_OldStatusUpdateRequest() {
		OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
		orderUpdateRequest.setOrderNumber(1234l);
		orderUpdateRequest.setOrderStatus(OrderStatusEnum.PLACED);
		
		Order order = new Order();
		order.setNumber(1234l);
		order.setStatus(OrderStatusEnum.PLACED.toString());
		
		try {
			orderService.updateOrderStatus(orderUpdateRequest);
		} catch (OrderServiceException serviceException) {
			Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, serviceException.getStatus());
		}
	}
}
