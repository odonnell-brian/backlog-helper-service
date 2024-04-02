package com.brian.backloghelperservice.dao.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.BacklogItemSource;
import com.brian.backloghelperservice.model.BacklogItemStatus;
import com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem.USER_INDEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DdbBacklogItemDaoImplTest {
    private static final BacklogItem BACKLOG_ITEM =
            BacklogItem.builder().title("test game").status(BacklogItemStatus.UNPLAYED).source(BacklogItemSource.BACKLOG).build();

    @Mock
    private DynamoDBMapper mockDdbMapper;

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
                    BacklogItem.builder().title("test game").status(BacklogItemStatus.UNPLAYED).source(BacklogItemSource.BACKLOG).build();
            ddbDao.saveItem(item);

            final ArgumentCaptor<DdbBacklogItem> ddbItemCaptor = ArgumentCaptor.forClass(DdbBacklogItem.class);
            verify(mockDdbMapper).save(ddbItemCaptor.capture());
            verifyNoMoreInteractions(mockDdbMapper);

            assertEquals(item.getTitle(), ddbItemCaptor.getValue().getTitle());
            assertEquals(item.getSource().name(), ddbItemCaptor.getValue().getSource());
            assertEquals(item.getStatus().name(), ddbItemCaptor.getValue().getStatus());
            assertNotNull(ddbItemCaptor.getValue().getId());
            assertNotNull(ddbItemCaptor.getValue().getUserId());
        }
    }

    @Nested
    class GetItem {
        @Test
        public void successfulGet() {
            final String id = "game-id";
            final DdbBacklogItem expectedDdbItem = new DdbBacklogItem(BACKLOG_ITEM);
            when(mockDdbMapper.load(DdbBacklogItem.class, id)).thenReturn(expectedDdbItem);

            final BacklogItem actualItem = ddbDao.getItem(id);
            assertEquals(expectedDdbItem.getTitle(), actualItem.getTitle());
            assertEquals(expectedDdbItem.getSource(), actualItem.getSource().name());
            assertEquals(expectedDdbItem.getStatus(), actualItem.getStatus().name());

            verify(mockDdbMapper).load(DdbBacklogItem.class, id);
            verifyNoMoreInteractions(mockDdbMapper);
        }
    }

    @Nested
    class GetItemsForUser {
        @Test
        public void successfulGet() {
            final String id = "user-id";

            final List<BacklogItem> expectedItems =
                    IntStream.of(2).boxed().map(num -> BacklogItem.builder().title("backlog game " + num)
                            .status(BacklogItemStatus.values()[num])
                            .source(BacklogItemSource.values()[num])
                            .build()).toList();
            final List<DdbBacklogItem> expectedDdbItems = expectedItems.stream().map(DdbBacklogItem::new).toList();

            final PaginatedQueryList<DdbBacklogItem> mockPaginatedQueryList = mock(PaginatedQueryList.class);
            when(mockPaginatedQueryList.stream()).thenReturn(expectedDdbItems.stream());
            when(mockDdbMapper.query(eq(DdbBacklogItem.class), any())).thenReturn(mockPaginatedQueryList);

            final List<BacklogItem> actualItems = ddbDao.getItemsForUser(id);
            final Map<String, BacklogItem> actualItemsByTitle = actualItems.stream().collect(Collectors.toMap(BacklogItem::getTitle,
                    Function.identity()));
            expectedItems.forEach((expectedItem) -> {
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
            assertEquals("userId = :userIdVal", expressionCaptor.getValue().getKeyConditionExpression());
            assertEquals(id, expressionCaptor.getValue().getExpressionAttributeValues().get(":userIdVal").getS());

            verify(mockPaginatedQueryList).stream();
            verifyNoMoreInteractions(mockPaginatedQueryList);
        }
    }
}
