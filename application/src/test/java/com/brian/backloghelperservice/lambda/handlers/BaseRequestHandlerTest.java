package com.brian.backloghelperservice.lambda.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brian.backloghelperservice.BacklogHelperServiceNotFoundException;
import com.brian.backloghelperservice.exception.BacklogHelperServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaseRequestHandlerTest {
  @Mock private ObjectMapper mockObjectMapper;

  private TestRequestHandler requestHandler;

  @BeforeEach
  public void setUp() {
    requestHandler = new TestRequestHandler(mockObjectMapper);
  }

  @Nested
  class HandleRequest {
    @Test
    public void success() throws Exception {
      final APIGatewayProxyRequestEvent mockRequest = mock(APIGatewayProxyRequestEvent.class);
      final Context mockContext = mock(Context.class);

      final String requestBody = "request-body";
      when(mockRequest.getBody()).thenReturn(requestBody);
      when(mockObjectMapper.writeValueAsString(requestBody)).thenReturn(requestBody);

      final APIGatewayProxyResponseEvent actualResponse = requestHandler.handleRequest(mockRequest, mockContext);

      assertEquals(HttpStatus.SC_OK, actualResponse.getStatusCode());
      assertEquals(requestBody, actualResponse.getBody());

      verify(mockRequest).getBody();
      verifyNoMoreInteractions(mockRequest);

      verify(mockObjectMapper).writeValueAsString(requestBody);
      verifyNoMoreInteractions(mockObjectMapper);
    }

    @Test
    public void handlesNotFoundExceptions() {
      requestHandler.setExceptionToThrow(new BacklogHelperServiceNotFoundException("boom"));

      final APIGatewayProxyResponseEvent actualResponse = requestHandler.handleRequest(null, null);
      assertEquals(HttpStatus.SC_NOT_FOUND, actualResponse.getStatusCode());
      assertNull(actualResponse.getBody());
    }

    @Test
    public void handlesUnexpectedExceptions() {
      requestHandler.setExceptionToThrow(new NullPointerException("boom"));

      final APIGatewayProxyResponseEvent actualResponse = requestHandler.handleRequest(null, null);
      assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertNull(actualResponse.getBody());
    }

    @Test
    public void handlesSerializationError() throws Exception {
      final APIGatewayProxyRequestEvent mockRequest = mock(APIGatewayProxyRequestEvent.class);
      final Context mockContext = mock(Context.class);

      final String requestBody = "request-body";
      when(mockRequest.getBody()).thenReturn(requestBody);
      when(mockObjectMapper.writeValueAsString(requestBody)).thenThrow(JsonProcessingException.class);

      final APIGatewayProxyResponseEvent actualResponse = requestHandler.handleRequest(mockRequest, mockContext);

      assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertNull(actualResponse.getBody());

      verify(mockRequest).getBody();
      verifyNoMoreInteractions(mockRequest);

      verify(mockObjectMapper).writeValueAsString(requestBody);
      verifyNoMoreInteractions(mockObjectMapper);
    }
  }

  @Nested
  class DeserializeString {
    @Test
    public void success() throws Exception {
      final String serializedValue = "brian";
      final String deserializedValue = "de-brian";
      final Class<String> clazz = String.class;

      when(mockObjectMapper.readValue(serializedValue, clazz)).thenReturn(deserializedValue);
      assertEquals(deserializedValue, requestHandler.deserializeString(clazz, serializedValue));

      verify(mockObjectMapper).readValue(serializedValue, clazz);
      verifyNoMoreInteractions(mockObjectMapper);
    }

    @Test
    public void handlesException() throws Exception {
      final String serializedValue = "brian";
      final Class<String> clazz = String.class;

      when(mockObjectMapper.readValue(serializedValue, clazz)).thenThrow(JsonProcessingException.class);

      assertThrows(BacklogHelperServiceException.class, () -> requestHandler.deserializeString(clazz, serializedValue));

      verify(mockObjectMapper).readValue(serializedValue, clazz);
      verifyNoMoreInteractions(mockObjectMapper);
    }
  }

  static class TestRequestHandler extends BaseRequestHandler<String> {

    private Optional<RuntimeException> exceptionToThrow;

    TestRequestHandler(final ObjectMapper objectMapper) {
      super(objectMapper);
      exceptionToThrow = Optional.empty();
    }

    @Override
    protected String doHandleRequest(
        final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {

      if (exceptionToThrow.isPresent()) {
        throw exceptionToThrow.get();
      }

      return apiGatewayProxyRequestEvent.getBody();
    }

    public void setExceptionToThrow(final RuntimeException exceptionToThrow) {
      this.exceptionToThrow = Optional.ofNullable(exceptionToThrow);
    }
  }
}
