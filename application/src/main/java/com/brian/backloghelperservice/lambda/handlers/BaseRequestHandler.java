package com.brian.backloghelperservice.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brian.backloghelperservice.BacklogHelperServiceNotFoundException;
import com.brian.backloghelperservice.exception.BacklogHelperServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

/** Base class for handling API gateway requests. */
@Slf4j
public abstract class BaseRequestHandler<T>
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Getter private final ObjectMapper objectMapper;

  /** Constructor. */
  public BaseRequestHandler() {
    this.objectMapper = new ObjectMapper();
  }


  /**
   * Constructor for unit tests.
   * @param objectMapper For serialization.
   */
  public BaseRequestHandler(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {

    final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
    try {
      final T responseObj = doHandleRequest(apiGatewayProxyRequestEvent, context);
      final String serializedResponseBody = (responseObj != null) ? serializeObject(responseObj) : "";
      response.withBody(serializedResponseBody);
      response.withStatusCode(HttpStatus.SC_OK);
    } catch (final BacklogHelperServiceNotFoundException e) {
      log.error("", e);
      response.withStatusCode(HttpStatus.SC_NOT_FOUND);
    } catch (final Throwable t) {
      log.error("", t);
      response.withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    return response;
  }

  private String serializeObject(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (final Exception e) {
      throw new BacklogHelperServiceException("Unable to serialize response.", e);
    }
  }

  protected <U> U deserializeString(final Class<U> clazz, final String serializedObj) {
    try {
    return objectMapper.readValue(serializedObj, clazz);
    } catch (final Exception e) {
      throw new BacklogHelperServiceException("Unable to deserialize object.", e);
    }
  }

  /**
   * Performs the business logic to handle the request.
   *
   * @param apiGatewayProxyRequestEvent The API gateway request.
   * @param context The lambda context.
   * @return The object that will be serialized into the response body.
   */
  protected abstract T doHandleRequest(
      final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context);
}
