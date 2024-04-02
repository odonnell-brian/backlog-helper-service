package com.brian.backloghelperservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** POJO representing a backlog item. */
@Getter
@AllArgsConstructor
@Builder
public class BacklogItem {
  /** The game's title. */
  private final String title;

  /** The current status of the backlog item (i.e completed or unplayed, etc). */
  private BacklogItemStatus status;

  /** The source of the backlog item. */
  private BacklogItemSource source;
}
