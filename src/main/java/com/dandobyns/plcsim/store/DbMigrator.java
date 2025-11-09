/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * Db.java - Manages SQLite database connection.
 *******************************************************************/
package com.dandobyns.plcsim.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbMigrator {
  private DbMigrator() {}

  public static void ensureSchema(Connection c) {
    try (Statement s = c.createStatement()) {
      // TAGS table
      s.execute("""
        CREATE TABLE IF NOT EXISTS tags(
          id             INTEGER PRIMARY KEY AUTOINCREMENT,
          name           TEXT    NOT NULL UNIQUE,
          type           TEXT    NOT NULL,         -- DIGITAL_INPUT, DIGITAL_OUTPUT, COIL, REGISTER, TIMER, COUNTER
          address        TEXT    NOT NULL,
          description    TEXT    NOT NULL DEFAULT '',
          value_bool     INTEGER,                  -- 0/1 for digital or coil
          value_int      INTEGER,                  -- for register
          preset_ms      INTEGER,                  -- for timer
          accum_ms       INTEGER,                  -- for timer
          preset_count   INTEGER,                  -- for counter
          accum_count    INTEGER,                  -- for counter
          latched        INTEGER,                  -- 0/1 for coil
          last_change_ms INTEGER
        );
      """);

      // Minimal EVENTS table (for audit/logging)
      s.execute("""
        CREATE TABLE IF NOT EXISTS events(
          id            INTEGER PRIMARY KEY AUTOINCREMENT,
          timestamp_ms  INTEGER NOT NULL,
          level         TEXT    NOT NULL,       -- INFO/WARN/ERROR
          message       TEXT    NOT NULL,
          tag_name      TEXT,
          old_value     TEXT,
          new_value     TEXT
        );
      """);

      // (Optional) stubs if you expand later:
      s.execute("CREATE TABLE IF NOT EXISTS scenarios(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, description TEXT);");
      s.execute("""
        CREATE TABLE IF NOT EXISTS scenario_steps(
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          scenario_id INTEGER NOT NULL,
          at_ms INTEGER NOT NULL,
          tag_name TEXT NOT NULL,
          action TEXT NOT NULL,
          value_bool INTEGER,
          value_int INTEGER,
          FOREIGN KEY(scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE
        );
      """);
    } catch (SQLException e) {
      throw new RuntimeException("Schema migration failed: " + e.getMessage(), e);
    }
  }
}

