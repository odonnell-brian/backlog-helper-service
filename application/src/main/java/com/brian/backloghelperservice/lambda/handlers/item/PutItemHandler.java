package com.brian.backloghelperservice.lambda.handlers.item;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dagger.component.DaggerRequestHandlerComponent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.lambda.handlers.BaseRequestHandler;
import com.brian.backloghelperservice.model.BacklogItem;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PutItemHandler extends BaseRequestHandler<Void> {

    private final BacklogItemDao backlogItemDao;

    /**
     * Constructor.
     */
    public PutItemHandler() {
        super();
        this.backlogItemDao = DaggerRequestHandlerComponent.create().buildBacklogItemDao();
    }

    /**
     * Constructor for testing.
     *
     * @param objectMapper For serialization.
     * @param itemDao Data accessor for interacting with persistence layer.
     */
    public PutItemHandler(final BacklogItemDao itemDao, final ObjectMapper objectMapper) {
        super(objectMapper);
        this.backlogItemDao = itemDao;
    }

    @Override
    protected Void doHandleRequest(
            final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        final String userId = DdbBacklogItemDaoImpl.DEFAULT_USER_ID; // TODO: update this to support multiple users.
        final BacklogItem newItem = deserializeString(BacklogItem.class, apiGatewayProxyRequestEvent.getBody());

        backlogItemDao.saveItem(newItem, userId);

        return null;
    }

}
