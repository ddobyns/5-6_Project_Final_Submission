/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * sqlite/SqliteEventRepository.java - SQLite implementation of EventRepository.
 *******************************************************************/
package com.dandobyns.plcsim.store.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dandobyns.plcsim.store.EventRepository;
import com.dandobyns.plcsim.store.EventRow;

public class SqliteEventRepository implements EventRepository {
  private final Connection conn;
  public SqliteEventRepository(Connection conn) { this.conn = conn; }

  @Override public void append(EventRow e) {
    String sql = "INSERT INTO events(timestamp_ms, level, message, tag_name, old_value, new_value) VALUES(?,?,?,?,?,?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, e.timestamp_ms);
      ps.setString(2, e.level);
      ps.setString(3, e.message);
      ps.setString(4, e.tag_name);
      ps.setString(5, e.old_value);
      ps.setString(6, e.new_value);
      ps.executeUpdate();
    } catch (SQLException ex) { throw new RuntimeException(ex); }
  }

  @Override public List<EventRow> recent(int limit) {
    String sql = "SELECT * FROM events ORDER BY id DESC LIMIT ?";
    List<EventRow> out = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, limit);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          EventRow e = new EventRow();
          e.id = rs.getLong("id");
          e.timestamp_ms = rs.getLong("timestamp_ms");
          e.level = rs.getString("level");
          e.message = rs.getString("message");
          e.tag_name = rs.getString("tag_name");
          e.old_value = rs.getString("old_value");
          e.new_value = rs.getString("new_value");
          out.add(e);
        }
      }
    } catch (SQLException ex) { throw new RuntimeException(ex); }
    return out;
  }
}
