package com.brian.backloghelperservice.lambda.handlers.item;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import com.brian.backloghelperservice.model.BacklogItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PutItemHandlerTest {
  @Mock private ObjectMapper mockObjectMapper;
  @Mock private BacklogItemDao mockItemDao;
  @Mock private Context mockContext;

  private PutItemHandler handler;

  @BeforeEach
  public void instantiateHandler() {
    handler = new PutItemHandler(mockItemDao, mockObjectMapper);
  }

  @AfterEach
  public void verifyUnusedMocks() {
    verifyNoInteractions(mockContext);
  }

  @Nested
  class DoHandleRequest {
    @Test
    public void success() throws Exception {
      final BacklogItem newItem = BacklogItem.builder().build();

      final String messageBody = "brian";
      final APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(messageBody);

      when(mockObjectMapper.readValue(messageBody, BacklogItem.class)).thenReturn(newItem);

      assertNull(handler.doHandleRequest(request, mockContext));

      verify(mockItemDao).saveItem(newItem, DdbBacklogItemDaoImpl.DEFAULT_USER_ID);
      verifyNoMoreInteractions(mockItemDao);

      verify(mockObjectMapper).readValue(messageBody, BacklogItem.class);
      verifyNoMoreInteractions(mockObjectMapper);
    }
  }
}
