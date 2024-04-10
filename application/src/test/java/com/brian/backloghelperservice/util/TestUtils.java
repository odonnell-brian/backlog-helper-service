package com.brian.backloghelperservice.util;

import com.brian.backloghelperservice.model.BacklogItem;
import com.brian.backloghelperservice.model.BacklogItemSource;
import com.brian.backloghelperservice.model.BacklogItemStatus;
import java.util.List;
import java.util.stream.IntStream;

public class TestUtils {

    public static List<BacklogItem> generateBacklogItems(final int count) {
        return IntStream.of(count)
                .boxed()
                .map(
                        num ->
                                BacklogItem.builder()
                                        .title("backlog game " + num)
                                        .status(BacklogItemStatus.values()[num % BacklogItemStatus.values().length])
                                        .source(BacklogItemSource.values()[num % BacklogItemSource.values().length])
                                        .build())
                .toList();
    }

}
