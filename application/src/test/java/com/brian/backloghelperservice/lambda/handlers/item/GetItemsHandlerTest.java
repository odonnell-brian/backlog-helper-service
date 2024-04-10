package com.brian.backloghelperservice.lambda.handlers.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetItemsHandlerTest {

    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock private BacklogItemDao mockItemDao;
    @Mock private Context mockContext;

    private GetItemsHandler handler;

    @BeforeEach
    public void instantiateHandler() {
        handler = new GetItemsHandler(mockItemDao, mockObjectMapper);
    }

    @AfterEach
    public void verifyUnusedMocks() {
        verifyNoInteractions(mockObjectMapper, mockContext);
    }

    @Nested
    class DoHandleRequest {
        @Test
        public void success() {
            final APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

            final List<BacklogItem> items = TestUtils.generateBacklogItems(3);
            when(mockItemDao.getItemsForUser(DdbBacklogItemDaoImpl.DEFAULT_USER_ID)).thenReturn(items);

            final List<BacklogItem> actualItems = handler.doHandleRequest(request, mockContext);

            assertEquals(items, actualItems);

            verify(mockItemDao).getItemsForUser(DdbBacklogItemDaoImpl.DEFAULT_USER_ID);
            verifyNoMoreInteractions(mockItemDao);
        }
    }

}
