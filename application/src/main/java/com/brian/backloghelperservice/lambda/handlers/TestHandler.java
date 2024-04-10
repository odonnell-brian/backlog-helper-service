package com.brian.backloghelperservice.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dagger.component.DaggerRequestHandlerComponent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.model.BacklogItem;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHandler extends BaseRequestHandler<List<BacklogItem>> {

  @Getter private final BacklogItemDao backlogItemDao;

  /** Constructor. */
  public TestHandler() {
    super();
    this.backlogItemDao = DaggerRequestHandlerComponent.create().buildBacklogItemDao();
  }

  @Override
  protected List<BacklogItem> doHandleRequest(
      final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
    final String userId = DdbBacklogItemDaoImpl.DEFAULT_USER_ID; // TODO: Add support for multiple users.
    return backlogItemDao.getItemsForUser(userId);
  }
}
