package com.brian.backloghelperservice.lambda.handlers.item;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dagger.component.DaggerRequestHandlerComponent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.lambda.handlers.BaseRequestHandler;
import com.brian.backloghelperservice.model.BacklogItem;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles Requests to GET /items/{itemId}
 */
public class GetItemByIdHandler extends BaseRequestHandler<BacklogItem> {

    public static final String ID_PATH_PARAM = "itemId";

    private final BacklogItemDao backlogItemDao;

    /**
     * Constructor.
     */
    public GetItemByIdHandler() {
        super();
        this.backlogItemDao = DaggerRequestHandlerComponent.create().buildBacklogItemDao();
    }

    /**
     * Constructor for testing.
     *
     * @param objectMapper For serialization.
     * @param itemDao Data accessor for interacting with persistence layer.
     */
    public GetItemByIdHandler(final BacklogItemDao itemDao, final ObjectMapper objectMapper) {
        super(objectMapper);
        this.backlogItemDao = itemDao;
    }

    @Override
    protected BacklogItem doHandleRequest(
            final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        final String itemId = apiGatewayProxyRequestEvent.getPathParameters().get(ID_PATH_PARAM);
        final String userId = DdbBacklogItemDaoImpl.DEFAULT_USER_ID; // TODO: update this to support multiple users.
        return backlogItemDao.getItem(itemId, userId);
    }
}
