/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * ConsoleUI.java
 *******************************************************************/
package com.dandobyns.plcsim;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.dandobyns.plcsim.core.PlcSimulator;
import com.dandobyns.plcsim.core.tags.Tag;
import com.dandobyns.plcsim.core.tags.TagType;
import com.dandobyns.plcsim.data.PlcDataSource;
import com.dandobyns.plcsim.data.PlcSample;
import com.dandobyns.plcsim.data.sqlite.SqlPlcDataSource;
import com.dandobyns.plcsim.store.Db;
import com.dandobyns.plcsim.store.DbMigrator;
import com.dandobyns.plcsim.store.EventRepository;
import com.dandobyns.plcsim.store.EventRow;
import com.dandobyns.plcsim.store.TagRepository;
import com.dandobyns.plcsim.store.TagRow;
import com.dandobyns.plcsim.store.sqlite.SqliteEventRepository;
import com.dandobyns.plcsim.store.sqlite.SqliteTagRepository;

public class ConsoleUI {
  private final PlcSimulator sim = PlcSimulator.defaultBuild();
  private final Scanner in = new Scanner(System.in);

  // Week 3 sample data source (unchanged)
  private PlcDataSource sqlSamples = null;

  // Week 4: app database (SQLite) for project persistence
  private final Db appDb = new Db("plc_sim.db");
  private final TagRepository tagRepo = new SqliteTagRepository(appDb.get());
  private final EventRepository eventRepo = new SqliteEventRepository(appDb.get());

  public void start() {
    // Ensure schema once
    DbMigrator.ensureSchema(appDb.get());

    while (true) {
      System.out.println("""
        === CLICK PLC SIM ===
        1) Add Digital Input
        2) Add Digital Output
        3) Add Register
        4) Add Timer
        5) Add Counter
        6) List Tags (watch table)
        7) Force Tag Value  (also logs to DB)
        8) Step Scan Once
        9) Read Sample SQL (plc_samples) → list 10
        A) Lookup Sample by UID (plc_samples)
        ---
        B) DB: Ensure schema
        S) DB: Save ALL tags to DB (upsert)
        L) DB: Load tags from DB (add if missing)
        D) DB: Delete tag by NAME
        E) DB: Show last 10 events
        0) Exit
        """);
      System.out.print("Choose: ");
      String c = in.nextLine().trim();
      switch (c) {
        case "1" -> addTag(TagType.DIGITAL_INPUT);
        case "2" -> addTag(TagType.DIGITAL_OUTPUT);
        case "3" -> addTag(TagType.REGISTER);
        case "4" -> addTag(TagType.TIMER);
        case "5" -> addTag(TagType.COUNTER);
        case "6" -> listTags();
        case "7" -> forceValue();
        case "8" -> stepScan();
        case "9" -> listFromSampleSql();
        case "A", "a" -> lookupSampleByUid();
        case "B", "b" -> dbEnsure();
        case "S", "s" -> dbSaveAllTags();
        case "L", "l" -> dbLoadTags();
        case "D", "d" -> dbDeleteTag();
        case "E", "e" -> dbShowRecentEvents();
        case "0" -> { closeAll(); System.out.println("Bye!"); return; }
        default -> System.out.println("Invalid.");
      }
    }
  }

  private void addTag(TagType type) {
    System.out.print("Name (e.g., X1, Y2, DS1, T1, C1): ");
    String name = in.nextLine().trim();
    System.out.print("Address: ");
    String addr = in.nextLine().trim();
    System.out.print("Description: ");
    String desc = in.nextLine().trim();
    boolean ok = sim.createTag(type, name, addr, desc);
    System.out.println(ok ? "CREATE OK" : "CREATE FAIL (duplicate name?)");
  }

  private void listTags() {
    System.out.println("NAME | TYPE | ADDRESS | VALUE");
    sim.getPlc().allTags().forEach(t -> System.out.println(t.toString()));
  }

  private void forceValue() {
    System.out.print("Tag name: ");
    String name = in.nextLine().trim();
    Optional<Tag> ot = sim.getPlc().findTag(name);
    if (ot.isEmpty()) { System.out.println("No such tag."); return; }
    Tag tag = ot.get();
    String oldVal = tag.valueAsString();

    System.out.print("Value (true/false or int or TIMER: enable/disable/reset/presetMs): ");
    String v = in.nextLine().trim();
    boolean ok = sim.force(name, v);
    System.out.println(ok ? "UPDATE OK" : "UPDATE FAIL (name/type mismatch?)");

    // Log to DB
    if (ok) {
      String newVal = tag.valueAsString();
      eventRepo.append(new EventRow(System.currentTimeMillis(), "INFO",
          "FORCE " + name + " = " + v, name, oldVal, newVal));
    }
  }

  private void stepScan() {
    long ms = sim.stepOnce();
    System.out.println("SCAN DONE in " + ms + " ms");
  }

  // ---------- Sample (Week 3) ----------
  private void ensureSampleSql() {
    if (sqlSamples == null) {
      sqlSamples = new SqlPlcDataSource();
      sqlSamples.connect("plc_data.db");
    }
  }
  private void listFromSampleSql() {
    ensureSampleSql();
    List<PlcSample> rows = sqlSamples.readAll();
    System.out.println("UID | PRODUCT | H | T | AGE | QTY | MTTF");
    rows.stream().limit(10).forEach(r -> System.out.println(r.toString()));
  }
  private void lookupSampleByUid() {
    ensureSampleSql();
    System.out.print("UID: ");
    int uid = Integer.parseInt(in.nextLine().trim());
    Optional<PlcSample> row = sqlSamples.findByUid(uid);
    System.out.println(row.map(Object::toString).orElse("Not found"));
  }

  // ---------- DB (Week 4) ----------
  private void dbEnsure() {
    DbMigrator.ensureSchema(appDb.get());
    System.out.println("DB schema OK.");
  }

  private void dbSaveAllTags() {
    int n = 0;
    for (Tag t : sim.getPlc().allTags()) {
      tagRepo.upsert(TagRow.fromTag(t));
      n++;
    }
    System.out.println("Upserted " + n + " tag(s).");
  }

  private void dbLoadTags() {
    List<TagRow> rows = tagRepo.findAll();
    int created = 0;
    for (TagRow r : rows) {
      if (sim.getPlc().findTag(r.name).isPresent()) continue; // skip duplicates
      Tag t = r.toTag();
      boolean ok = sim.createTag(t.getType(), t.getName(), t.getAddress(), t.getDescription());
      if (ok) created++;
    }
    System.out.println("Loaded " + created + " new tag(s) from DB.");
  }

  private void dbDeleteTag() {
    System.out.print("Tag name to delete (DB): ");
    String name = in.nextLine().trim();
    boolean ok = tagRepo.deleteByName(name);
    System.out.println(ok ? "DELETE OK" : "DELETE: not found");
  }

  private void dbShowRecentEvents() {
    List<EventRow> rows = eventRepo.recent(10);
    if (rows.isEmpty()) { System.out.println("(no events)"); return; }
    for (EventRow e : rows) {
      System.out.printf("%d | %s | %s | %s | %s -> %s%n",
          e.timestamp_ms, e.level, e.message,
          e.tag_name == null ? "-" : e.tag_name,
          e.old_value == null ? "-" : e.old_value,
          e.new_value == null ? "-" : e.new_value);
    }
  }

  private void closeAll() {
    try { if (sqlSamples != null) sqlSamples.close(); } catch (Exception ignored) {}
    appDb.close();
  }
}
