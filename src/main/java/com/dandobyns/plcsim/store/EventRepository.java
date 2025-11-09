/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * EventRepository.java - Interface for Event data access.
 *******************************************************************/
package com.dandobyns.plcsim.store;

import java.util.List;

public interface EventRepository {
  void append(EventRow e);
  List<EventRow> recent(int limit);
}
