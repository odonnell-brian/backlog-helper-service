package com.brian.backloghelperservice.dao.impl;

import static com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem.USER_INDEX;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.brian.backloghelperservice.BacklogHelperServiceNotFoundException;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.exception.BacklogHelperServiceException;
import com.brian.backloghelperservice.exception.BacklogHelperServiceRetryableException;
import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.inject.Inject;

/** DAO for saving/reading backlog items from DynamoDb. */
public class DdbBacklogItemDaoImpl implements BacklogItemDao {

  // Just a placeholder until users are actually implemented.
  public static final String DEFAULT_USER_ID = "TODO";

  private final DynamoDBMapper dynamoMapper;

  /**
   * Constructor
   *
   * @param dynamoMapper DDB mapper used to interact with DDB tables.
   */
  @Inject
  public DdbBacklogItemDaoImpl(final DynamoDBMapper dynamoMapper) {
    this.dynamoMapper = dynamoMapper;
  }

  @Override
  public void saveItem(final BacklogItem item, final String userId) {
    final DdbBacklogItem ddbRecord = new DdbBacklogItem(item, userId);
    doCallWithExceptionHandling(
        () -> {
          dynamoMapper.save(ddbRecord);
          return true; // callable has to have a return type.
        });
  }

  @Override
  public BacklogItem getItem(final String id, final String userId) {
    final DdbBacklogItem ddbRecord =
        doCallWithExceptionHandling(() -> dynamoMapper.load(DdbBacklogItem.class, id, userId));
    return ddbRecord.toBacklogItem();
  }

  @Override
  public List<BacklogItem> getItemsForUser(final String userId) {

    final Map<String, AttributeValue> attributeValues = new HashMap<>();
    attributeValues.put(":userIdVal", new AttributeValue().withS(userId));

    final DynamoDBQueryExpression<DdbBacklogItem> queryExpression =
        new DynamoDBQueryExpression<DdbBacklogItem>()
            .withKeyConditionExpression("userId = :userIdVal")
            .withExpressionAttributeValues(attributeValues)
            .withConsistentRead(false) // Has to be false when querying a GSI.
            .withIndexName(USER_INDEX);
    final List<DdbBacklogItem> items =
        doCallWithExceptionHandling(
            () -> dynamoMapper.query(DdbBacklogItem.class, queryExpression));

    return items.stream().map(DdbBacklogItem::toBacklogItem).collect(Collectors.toList());
  }

  private <T> T doCallWithExceptionHandling(final Callable<T> callable) {
    try {
      return callable.call();
    } catch (final ProvisionedThroughputExceededException e) {
      throw new BacklogHelperServiceRetryableException("Too many requests", e);
    } catch (final ConditionalCheckFailedException e) {
      throw new BacklogHelperServiceNotFoundException("Unable to find item.", e);
    } catch (final Throwable t) {
      throw new BacklogHelperServiceException("Unexpected error calling DynamoDB", t);
    }
  }
}
