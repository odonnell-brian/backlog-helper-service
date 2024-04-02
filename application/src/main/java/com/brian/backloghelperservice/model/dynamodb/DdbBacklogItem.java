package com.brian.backloghelperservice.model.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.BacklogItemSource;
import com.brian.backloghelperservice.model.BacklogItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/** POJO to represent a backlog item in DynamoDB. */
@Getter
@Setter
@NoArgsConstructor
@DynamoDBTable(tableName = "BacklogItems")
public class DdbBacklogItem {

  public static final String BACKLOG_TABLE_NAME = "BacklogItems";
  public static final String USER_INDEX = "userItems";

  @DynamoDBHashKey private String id;

  @DynamoDBRangeKey
  @DynamoDBIndexHashKey(globalSecondaryIndexName = USER_INDEX)
  private String userId;

  @DynamoDBAttribute private String title;

  @DynamoDBAttribute private String status;

  @DynamoDBAttribute private String source;

  /**
   * Constructor.
   *
   * @param backlogItem The item to convert to a DDB record.
   */
  public DdbBacklogItem(final BacklogItem backlogItem) {
    this.title = backlogItem.getTitle();
    this.status = backlogItem.getStatus().name();
    this.source = backlogItem.getSource().name();
    this.id = UUID.randomUUID().toString();
    this.userId = "TODO";
  }

  /**
   * Converts this DDB record to a BacklogItem.
   *
   * @return The backlog item.
   */
  public BacklogItem toBacklogItem() {
    return BacklogItem.builder()
        .title(title)
        .source(BacklogItemSource.valueOf(source))
        .status(BacklogItemStatus.valueOf(status))
        .build();
  }
}
