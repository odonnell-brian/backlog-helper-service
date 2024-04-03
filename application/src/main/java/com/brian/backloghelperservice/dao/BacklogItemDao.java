package com.brian.backloghelperservice.dao;

import com.brian.backloghelperservice.model.BacklogItem;
import java.util.List;

/** Interface for interacting with the persistence layer for backlog items. */
public interface BacklogItemDao {

  /**
   * Saves an item to the persistence layer.
   *
   * @param item The item to save.
   */
  void saveItem(final BacklogItem item);

  /**
   * Gets an item from the persistence layer.
   *
   * @param id The id of the item to retrieve.
   * @return The item.
   */
  BacklogItem getItem(final String id);

  /**
   * Gets all backlog items for the given user.
   *
   * @param userId The id of the user.
   * @return All the user's backlog items.
   */
  List<BacklogItem> getItemsForUser(final String userId);
}
