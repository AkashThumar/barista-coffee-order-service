package com.barista.coffee.orderservice;

import org.springframework.http.HttpStatus;

import com.barista.coffee.orderservice.bean.ErrorBean;

public class OrderServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9055024286408971006L;

	private HttpStatus status;
	private ErrorBean errorBean;

	private OrderServiceException() {
	}

	public OrderServiceException(HttpStatus status, ErrorBean errorBean, Throwable cause) {
		super(cause);
		this.status = status;
		this.errorBean = errorBean;
	}
	
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public ErrorBean getErrorBean() {
		return errorBean;
	}

	public void setErrorBean(ErrorBean errorBean) {
		this.errorBean = errorBean;
	}

}
