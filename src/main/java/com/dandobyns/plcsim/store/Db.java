/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * Db.java - Manages SQLite database connection.
 *******************************************************************/
package com.dandobyns.plcsim.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Db implements AutoCloseable {
  private final String path;
  private Connection conn;

  public Db(String path) { this.path = path; }

  public Connection get() {
    if (conn == null) {
      try {
        conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement s = conn.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
      } catch (SQLException e) { throw new RuntimeException(e); }
    }
    return conn;
  }

  @Override public void close() {
    if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
  }
}
