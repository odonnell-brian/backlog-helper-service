package com.brian.backloghelperservice.lambda.handlers.item;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dagger.component.DaggerRequestHandlerComponent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.lambda.handlers.BaseRequestHandler;
import com.brian.backloghelperservice.model.BacklogItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Handler for GET requests to /items
 */
public class GetItemsHandler extends BaseRequestHandler<List<BacklogItem>> {

    private final BacklogItemDao backlogItemDao;

    /**
     * Constructor.
     */
    public GetItemsHandler() {
        super();
        this.backlogItemDao = DaggerRequestHandlerComponent.create().buildBacklogItemDao();
    }

    /**
     * Constructor for testing.
     *
     * @param objectMapper For serialization.
     * @param itemDao Data accessor for interacting with persistence layer.
     */
    public GetItemsHandler(final BacklogItemDao itemDao, final ObjectMapper objectMapper) {
        super(objectMapper);
        this.backlogItemDao = itemDao;
    }

    @Override
    protected List<BacklogItem> doHandleRequest(
            final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        final String userId = DdbBacklogItemDaoImpl.DEFAULT_USER_ID; // TODO: Add support for multiple users.
        return backlogItemDao.getItemsForUser(userId);
    }

}
