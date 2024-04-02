package com.brian.backloghelperservice.dao.impl;

import static com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem.USER_INDEX;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.brian.backloghelperservice.dao.BacklogItemDao;
import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.dynamodb.DdbBacklogItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/** DAO for saving/reading backlog items from DynamoDb. */
@AllArgsConstructor
public class DdbBacklogItemDaoImpl implements BacklogItemDao {

  private final DynamoDBMapper dynamoMapper;

  @Override
  public void saveItem(final BacklogItem item) {
    final DdbBacklogItem ddbRecord = new DdbBacklogItem(item);
    dynamoMapper.save(ddbRecord);
  }

  @Override
  public BacklogItem getItem(final String id) {
    final DdbBacklogItem ddbRecord = dynamoMapper.load(DdbBacklogItem.class, id);
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
            .withIndexName(USER_INDEX);
    final List<DdbBacklogItem> items = dynamoMapper.query(DdbBacklogItem.class, queryExpression);

    return items.stream().map(DdbBacklogItem::toBacklogItem).collect(Collectors.toList());
  }
}
