/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * sqlite/SqliteTagRepository.java - SQLite implementation of TagRepository.
 *******************************************************************/
package com.dandobyns.plcsim.store.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dandobyns.plcsim.core.tags.TagType;
import com.dandobyns.plcsim.store.TagRepository;
import com.dandobyns.plcsim.store.TagRow;

public class SqliteTagRepository implements TagRepository {
  private final Connection conn;
  public SqliteTagRepository(Connection conn) { this.conn = conn; }

  @Override public void upsert(TagRow r) {
    String sql = """
      INSERT INTO tags(name, type, address, description, value_bool, value_int, preset_ms, accum_ms, preset_count, accum_count, latched, last_change_ms)
      VALUES(?,?,?,?,?,?,?,?,?,?,?,?)
      ON CONFLICT(name) DO UPDATE SET
        type=excluded.type, address=excluded.address, description=excluded.description,
        value_bool=excluded.value_bool, value_int=excluded.value_int,
        preset_ms=excluded.preset_ms, accum_ms=excluded.accum_ms,
        preset_count=excluded.preset_count, accum_count=excluded.accum_count,
        latched=excluded.latched, last_change_ms=excluded.last_change_ms
    """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, r.name);
      ps.setString(2, r.type.name());
      ps.setString(3, r.address);
      ps.setString(4, r.description == null ? "" : r.description);
      if (r.value_bool == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, r.value_bool);
      if (r.value_int == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, r.value_int);
      if (r.preset_ms == null) ps.setNull(7, Types.INTEGER); else ps.setLong(7, r.preset_ms);
      if (r.accum_ms == null) ps.setNull(8, Types.INTEGER); else ps.setLong(8, r.accum_ms);
      if (r.preset_count == null) ps.setNull(9, Types.INTEGER); else ps.setInt(9, r.preset_count);
      if (r.accum_count == null) ps.setNull(10, Types.INTEGER); else ps.setInt(10, r.accum_count);
      if (r.latched == null) ps.setNull(11, Types.INTEGER); else ps.setInt(11, r.latched);
      ps.setNull(12, Types.INTEGER);
      ps.executeUpdate();
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  private static TagRow fromRs(ResultSet rs) throws SQLException {
    TagRow r = new TagRow();
    r.id = rs.getLong("id");
    r.name = rs.getString("name");
    r.type = TagType.valueOf(rs.getString("type"));
    r.address = rs.getString("address");
    r.description = rs.getString("description");
    { int v = rs.getInt("value_bool"); r.value_bool = rs.wasNull()? null : v; }
    { int v = rs.getInt("value_int"); r.value_int = rs.wasNull()? null : v; }
    { long v = rs.getLong("preset_ms"); r.preset_ms = rs.wasNull()? null : v; }
    { long v = rs.getLong("accum_ms");  r.accum_ms  = rs.wasNull()? null : v; }
    { int v = rs.getInt("preset_count"); r.preset_count = rs.wasNull()? null : v; }
    { int v = rs.getInt("accum_count");  r.accum_count  = rs.wasNull()? null : v; }
    { int v = rs.getInt("latched");      r.latched      = rs.wasNull()? null : v; }
    return r;
  }

  @Override public Optional<TagRow> findByName(String name) {
    String sql = "SELECT * FROM tags WHERE name = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();
        return Optional.of(fromRs(rs));
      }
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  @Override public List<TagRow> findAll() {
    String sql = "SELECT * FROM tags ORDER BY name";
    List<TagRow> list = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) list.add(fromRs(rs));
      return list;
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  @Override public boolean deleteByName(String name) {
    String sql = "DELETE FROM tags WHERE name=?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  @Override public int deleteAll() {
    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tags")) {
      return ps.executeUpdate();
    } catch (SQLException e) { throw new RuntimeException(e); }
  }
}
