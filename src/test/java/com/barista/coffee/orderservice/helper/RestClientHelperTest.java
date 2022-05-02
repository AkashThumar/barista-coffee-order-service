package com.barista.coffee.orderservice.helper;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.barista.coffee.orderservice.OrderServiceException;

@SpringBootTest
public class RestClientHelperTest {

	@InjectMocks
	RestClientHelper restClientHelper;

	private RestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		restTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(restClientHelper, "restTemplate", restTemplate);
	}

	@Test
	public void testInvokeRestEndpoint() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("name", "test");
		Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class), Mockito.any(Map.class)))
				.thenReturn(new ResponseEntity<Object>("payload", HttpStatus.OK));
		Assertions.assertNotNull(restClientHelper.invokeRestEndpoint(HttpMethod.GET,
				"http://localhost:8080/v1/product/{name}", null, parameters, new HttpHeaders(), String.class));
	}

	@Test
	public void testInvokeRestEndpoint_NullParameters_ClientError() {
		Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "client error"));
		try {
			restClientHelper.invokeRestEndpoint(HttpMethod.GET, "http://localhost:8080/v1/product/{name}", null, null,
					new HttpHeaders(), String.class);
		} catch (OrderServiceException exception) {
			Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		}
	}

	@Test
	public void testInvokeRestEndpoint_EmptyParameters_StatusError() {
		Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class)))
				.thenThrow(new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "status error") {
				});
		try {
			restClientHelper.invokeRestEndpoint(HttpMethod.GET, "http://localhost:8080/v1/product/{name}", null,
					new HashMap<>(), new HttpHeaders(), String.class);
		} catch (OrderServiceException exception) {
			Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
		}
	}

	@Test
	public void testInvokeRestEndpoint_RestClientError() {
		Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class)))
				.thenThrow(new RestClientException("rest client exception"));
		try {
			restClientHelper.invokeRestEndpoint(HttpMethod.GET, "http://localhost:8080/v1/product/{name}", null, null,
					new HttpHeaders(), String.class);
		} catch (OrderServiceException exception) {
			Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
		}
	}

}
