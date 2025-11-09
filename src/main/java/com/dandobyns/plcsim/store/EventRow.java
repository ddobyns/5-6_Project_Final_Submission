/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * Db.java - Manages SQLite database connection.
 *******************************************************************/
package com.dandobyns.plcsim.store;

public class EventRow {
  public Long id;
  public Long timestamp_ms;
  public String level;
  public String message;
  public String tag_name;
  public String old_value;
  public String new_value;

  public EventRow() {}
  public EventRow(long ts, String level, String msg, String tag, String oldVal, String newVal) {
    this.timestamp_ms = ts; this.level = level; this.message = msg;
    this.tag_name = tag; this.old_value = oldVal; this.new_value = newVal;
  }
}
