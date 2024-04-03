package com.brian.backloghelperservice.model.dynamodb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.BacklogItemSource;
import com.brian.backloghelperservice.model.BacklogItemStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class DdbBacklogItemTest {
    private static final BacklogItem BACKLOG_ITEM =
            BacklogItem.builder().title("test game").status(BacklogItemStatus.UNPLAYED).source(BacklogItemSource.BACKLOG).build();


    @Test
    public void testConstructor() {
        final DdbBacklogItem ddbItem = new DdbBacklogItem(BACKLOG_ITEM);

        assertEquals(BACKLOG_ITEM.getTitle(), ddbItem.getTitle());
        assertEquals(BACKLOG_ITEM.getStatus().name(), ddbItem.getStatus());
        assertEquals(BACKLOG_ITEM.getSource().name(), ddbItem.getSource());
        assertDoesNotThrow(() -> UUID.fromString(ddbItem.getId()));
        assertEquals("TODO", ddbItem.getUserId());
    }

    @Test
    public void testToBacklogItem() {
        final DdbBacklogItem ddbItem = new DdbBacklogItem(BACKLOG_ITEM);
        final BacklogItem convertedItem = ddbItem.toBacklogItem();

        assertEquals(ddbItem.getTitle(), convertedItem.getTitle());
        assertEquals(ddbItem.getStatus(), convertedItem.getStatus().name());
        assertEquals(ddbItem.getSource(), convertedItem.getSource().name());
    }

}
