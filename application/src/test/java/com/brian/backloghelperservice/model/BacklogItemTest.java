package com.brian.backloghelperservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BacklogItemTest {

  @Test
  public void testPojo() {
    final String title = "test game";
    final BacklogItemSource source = BacklogItemSource.BACKLOG;
    final BacklogItemStatus status = BacklogItemStatus.PLAYING;

    final BacklogItem item =
        BacklogItem.builder().title(title).source(source).status(status).build();
    assertEquals(title, item.getTitle());
    assertEquals(source, item.getSource());
    assertEquals(status, item.getStatus());
  }
}
