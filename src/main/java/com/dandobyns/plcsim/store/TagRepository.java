/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * TagRepository.java - Interface for Tag data access.
 *******************************************************************/
package com.dandobyns.plcsim.store;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
  void upsert(TagRow row);
  Optional<TagRow> findByName(String name);
  List<TagRow> findAll();
  boolean deleteByName(String name);
  int deleteAll();
}
