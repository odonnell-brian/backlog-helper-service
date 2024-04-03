package com.brian.backloghelperservice.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brian.backloghelperservice.dagger.component.DaggerRequestHandlerComponent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.model.BacklogItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private final BacklogItemDao backlogItemDao;

  public TestHandler() {
    this.backlogItemDao = DaggerRequestHandlerComponent.create().buildBacklogItemDao();
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
    log.info(apiGatewayProxyRequestEvent.getBody());
    log.info("Getting item from DDB");
    final BacklogItem item = backlogItemDao.getItem("123456");
    log.info(item.getTitle());
    return new APIGatewayProxyResponseEvent().withBody("BRIAN").withStatusCode(200);
  }
}
