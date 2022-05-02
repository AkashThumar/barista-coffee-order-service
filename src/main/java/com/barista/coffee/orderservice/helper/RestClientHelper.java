package com.barista.coffee.orderservice.helper;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.barista.coffee.orderservice.OrderServiceException;
import com.barista.coffee.orderservice.bean.ErrorBean;

@Component
public class RestClientHelper {

	private static RestTemplate restTemplate;

	@PostConstruct
	public RestTemplate getRestTemplate() {
		if (null == restTemplate) {
			restTemplate = new RestTemplate();
		}
		return restTemplate;
	}

	public <T> ResponseEntity<T> invokeRestEndpoint(HttpMethod httpMethod, String url, Object requestPayload,
			Map<String, String> parameters, HttpHeaders headers, Class<T> responseType) {
		try {
			ResponseEntity<T> responseEntity = null;
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> httpEntity = new HttpEntity<>(this.convertToJsonString(requestPayload), httpHeaders);
			if (null != parameters && !parameters.isEmpty()) {
				responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, responseType, parameters);
			} else {
				responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, responseType);
			}
			return responseEntity;
		} catch (HttpClientErrorException clientErrorException) {
			throw new OrderServiceException(clientErrorException.getStatusCode(),
					new ErrorBean("BCOS-" + clientErrorException.getRawStatusCode(),
							clientErrorException.getResponseBodyAsString()),
					clientErrorException);
		} catch (HttpStatusCodeException statusCodeException) {
			throw new OrderServiceException(statusCodeException.getStatusCode(),
					new ErrorBean("BCOS-" + statusCodeException.getRawStatusCode(),
							statusCodeException.getResponseBodyAsString()),
					statusCodeException);
		} catch (RestClientException restClientException) {
			throw new OrderServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
					new ErrorBean("BCOS-501", "Technical Error! Please try again later."), restClientException);
		} catch (Exception exception) {
			throw new OrderServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
					new ErrorBean("BCOS-502", "Technical Error! Please try again later."), exception);
		}
	}

	public String convertToJsonString(Object object) {
		if (null == object)
			return null;
		else {
			JSONObject jsonObject = new JSONObject(object);
			return null != jsonObject ? jsonObject.toString() : null;
		}
	}
}
