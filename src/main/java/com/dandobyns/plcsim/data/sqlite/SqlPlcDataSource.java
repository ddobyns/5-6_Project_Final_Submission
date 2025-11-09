/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * SqlPlcDataSource.java
 *******************************************************************/
package com.dandobyns.plcsim.data.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dandobyns.plcsim.data.PlcDataSource;
import com.dandobyns.plcsim.data.PlcSample;

public class SqlPlcDataSource implements PlcDataSource {
  private Connection conn;

  public void connect(String sqlitePath) {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
      try (Statement s = conn.createStatement()) {
        s.execute("PRAGMA foreign_keys = ON");
        s.execute("""
          CREATE TABLE IF NOT EXISTS plc_samples(
            uid INTEGER PRIMARY KEY,
            product_type TEXT NOT NULL,
            humidity REAL NOT NULL,
            temperature REAL NOT NULL,
            age INTEGER NOT NULL,
            quantity INTEGER NOT NULL,
            mttf INTEGER NOT NULL
          )
        """);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Connect failed: " + e.getMessage(), e);
    }
  }

  @Override
  public List<PlcSample> readAll() {
    final String sql =
        "SELECT uid, product_type, humidity, temperature, age, quantity, mttf " +
        "FROM plc_samples ORDER BY uid";
    final List<PlcSample> out = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        out.add(new PlcSample(
            rs.getInt("uid"),
            rs.getString("product_type"),
            rs.getDouble("humidity"),
            rs.getDouble("temperature"),
            rs.getInt("age"),
            rs.getInt("quantity"),
            rs.getInt("mttf")
        ));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return out;
  }

  @Override
  public Optional<PlcSample> findByUid(int uid) {
    final String sql =
        "SELECT uid, product_type, humidity, temperature, age, quantity, mttf " +
        "FROM plc_samples WHERE uid = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, uid);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();
        return Optional.of(new PlcSample(
            rs.getInt("uid"),
            rs.getString("product_type"),
            rs.getDouble("humidity"),
            rs.getDouble("temperature"),
            rs.getInt("age"),
            rs.getInt("quantity"),
            rs.getInt("mttf")
        ));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    if (conn != null) {
      try { conn.close(); } catch (SQLException ignored) {}
    }
  }
}
