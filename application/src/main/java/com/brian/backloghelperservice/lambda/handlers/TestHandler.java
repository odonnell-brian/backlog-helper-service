package com.brian.backloghelperservice.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class TestHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
    context.getLogger().log(apiGatewayProxyRequestEvent.getBody());
    return new APIGatewayProxyResponseEvent().withBody("BRIAN").withStatusCode(200);
  }
}
