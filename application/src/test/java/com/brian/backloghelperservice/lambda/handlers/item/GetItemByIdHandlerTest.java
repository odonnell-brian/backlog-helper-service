package com.brian.backloghelperservice.lambda.handlers.item;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.model.BacklogItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetItemByIdHandlerTest {

  @Mock private ObjectMapper mockObjectMapper;
  @Mock private BacklogItemDao mockItemDao;
  @Mock private Context mockContext;

  private GetItemByIdHandler handler;

  @BeforeEach
  public void instantiateHandler() {
    handler = new GetItemByIdHandler(mockItemDao, mockObjectMapper);
  }

  @AfterEach
  public void verifyUnusedMocks() {
    verifyNoInteractions(mockObjectMapper, mockContext);
  }

  @Nested
  class DoHandleRequest {
    @Test
    public void success() {
      final String itemId = "item-id";
      final APIGatewayProxyRequestEvent request =
          new APIGatewayProxyRequestEvent()
              .withPathParameters(Map.of(GetItemByIdHandler.ID_PATH_PARAM, itemId));

      final BacklogItem mockItem = mock(BacklogItem.class);
      when(mockItemDao.getItem(itemId, DdbBacklogItemDaoImpl.DEFAULT_USER_ID)).thenReturn(mockItem);

      assertEquals(mockItem, handler.doHandleRequest(request, mockContext));
      verify(mockItemDao).getItem(itemId, DdbBacklogItemDaoImpl.DEFAULT_USER_ID);
      verifyNoMoreInteractions(mockItemDao);
    }
  }
}
