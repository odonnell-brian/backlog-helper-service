package com.brian.backloghelperservice.dao.impl;

import static com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem.USER_INDEX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.brian.backloghelperservice.BacklogHelperServiceNotFoundException;
import com.brian.backloghelperservice.exception.BacklogHelperServiceException;
import com.brian.backloghelperservice.exception.BacklogHelperServiceRetryableException;
import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.BacklogItemSource;
import com.brian.backloghelperservice.model.BacklogItemStatus;
import com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.brian.backloghelperservice.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DdbBacklogItemDaoImplTest {
  private static final BacklogItem BACKLOG_ITEM =
      BacklogItem.builder()
          .title("test game")
          .status(BacklogItemStatus.UNPLAYED)
          .source(BacklogItemSource.BACKLOG)
          .build();

  private static final String USER_ID = "user-id";

  @Mock private DynamoDBMapper mockDdbMapper;

  private DdbBacklogItemDaoImpl ddbDao;

  @BeforeEach
  public void setUp() {
    ddbDao = new DdbBacklogItemDaoImpl(mockDdbMapper);
  }

  @Nested
  class SaveItem {
    @Test
    public void successfulSave() {
      final BacklogItem item =
          BacklogItem.builder()
              .title("test game")
              .status(BacklogItemStatus.UNPLAYED)
              .source(BacklogItemSource.BACKLOG)
              .build();
      ddbDao.saveItem(item, USER_ID);

      final ArgumentCaptor<DdbBacklogItem> ddbItemCaptor =
          ArgumentCaptor.forClass(DdbBacklogItem.class);
      verify(mockDdbMapper).save(ddbItemCaptor.capture());
      verifyNoMoreInteractions(mockDdbMapper);

      assertEquals(item.getTitle(), ddbItemCaptor.getValue().getTitle());
      assertEquals(item.getSource().name(), ddbItemCaptor.getValue().getSource());
      assertEquals(item.getStatus().name(), ddbItemCaptor.getValue().getStatus());
      assertNotNull(ddbItemCaptor.getValue().getId());
      assertEquals(USER_ID, ddbItemCaptor.getValue().getUserId());
    }
  }

  @Nested
  class GetItem {
    @Test
    public void successfulGet() {
      final String id = "game-id";
      final DdbBacklogItem expectedDdbItem = new DdbBacklogItem(BACKLOG_ITEM, USER_ID);
      when(mockDdbMapper.load(DdbBacklogItem.class, id, USER_ID)).thenReturn(expectedDdbItem);

      final BacklogItem actualItem = ddbDao.getItem(id, USER_ID);
      assertEquals(expectedDdbItem.getTitle(), actualItem.getTitle());
      assertEquals(expectedDdbItem.getSource(), actualItem.getSource().name());
      assertEquals(expectedDdbItem.getStatus(), actualItem.getStatus().name());

      verify(mockDdbMapper).load(DdbBacklogItem.class, id, USER_ID);
      verifyNoMoreInteractions(mockDdbMapper);
    }
  }

  @Nested
  class GetItemsForUser {
    @Test
    public void successfulGet() {
      final String id = "user-id";

      final List<BacklogItem> expectedItems = TestUtils.generateBacklogItems(2);
      final List<DdbBacklogItem> expectedDdbItems =
          expectedItems.stream().map(item -> new DdbBacklogItem(item, id)).toList();

      final PaginatedQueryList<DdbBacklogItem> mockPaginatedQueryList =
          mock(PaginatedQueryList.class);
      when(mockPaginatedQueryList.stream()).thenReturn(expectedDdbItems.stream());
      when(mockDdbMapper.query(eq(DdbBacklogItem.class), any())).thenReturn(mockPaginatedQueryList);

      final List<BacklogItem> actualItems = ddbDao.getItemsForUser(id);
      final Map<String, BacklogItem> actualItemsByTitle =
          actualItems.stream()
              .collect(Collectors.toMap(BacklogItem::getTitle, Function.identity()));
      expectedItems.forEach(
          (expectedItem) -> {
            final BacklogItem actualItem = actualItemsByTitle.get(expectedItem.getTitle());
            assertNotNull(actualItem);
            assertEquals(expectedItem.getTitle(), actualItem.getTitle());
            assertEquals(expectedItem.getStatus(), actualItem.getStatus());
            assertEquals(expectedItem.getSource(), actualItem.getSource());
          });

      final ArgumentCaptor<DynamoDBQueryExpression<DdbBacklogItem>> expressionCaptor =
          ArgumentCaptor.forClass(DynamoDBQueryExpression.class);
      verify(mockDdbMapper).query(eq(DdbBacklogItem.class), expressionCaptor.capture());
      verifyNoMoreInteractions(mockDdbMapper);

      assertEquals(USER_INDEX, expressionCaptor.getValue().getIndexName());
      assertFalse(expressionCaptor.getValue().isConsistentRead());
      assertEquals("userId = :userIdVal", expressionCaptor.getValue().getKeyConditionExpression());
      assertEquals(
          id, expressionCaptor.getValue().getExpressionAttributeValues().get(":userIdVal").getS());

      verify(mockPaginatedQueryList).stream();
      verifyNoMoreInteractions(mockPaginatedQueryList);
    }
  }

  @Nested
  class HandlesExceptions {
    @Test
    public void throughputExceededException() {
      testException(ProvisionedThroughputExceededException.class, BacklogHelperServiceRetryableException.class);
    }

    @Test
    public void conditionalCheckFailedException() {
      testException(ConditionalCheckFailedException.class, BacklogHelperServiceNotFoundException.class);
    }

    @Test
    public void unexpectedException() {
      testException(NullPointerException.class, BacklogHelperServiceException.class);
    }

    private <T extends Throwable, U extends Throwable> void testException(final Class<T> toThrow, final Class<U> expectedException) {
      final String id = "game-id";
      when(mockDdbMapper.load(DdbBacklogItem.class, id, USER_ID)).thenThrow(toThrow);

      assertThrows(expectedException, () -> ddbDao.getItem(id, USER_ID));

      verify(mockDdbMapper).load(DdbBacklogItem.class, id, USER_ID);
      verifyNoMoreInteractions(mockDdbMapper);
    }
  }
}
