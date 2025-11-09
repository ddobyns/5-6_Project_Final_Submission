/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * ConsoleUI.java
 *******************************************************************/
package com.dandobyns.plcsim;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.dandobyns.plcsim.core.PlcSimulator;
import com.dandobyns.plcsim.core.tags.TagType;
import com.dandobyns.plcsim.data.PlcSample;
import com.dandobyns.plcsim.data.SqlPlcDataSource;
import com.dandobyns.plcsim.data.sqlite.PlcDataSource;

public class ConsoleUI {
  private final PlcSimulator sim = PlcSimulator.defaultBuild();
  private final Scanner in = new Scanner(System.in);   // <-- only ONE declaration
  private PlcDataSource sql = null;

  public void start() {
    while (true) {
      System.out.println("""
        === CLICK PLC SIM (Week 3) ===
        1) Add Digital Input
        2) Add Digital Output
        3) Add Register
        4) Add Timer
        5) Add Counter
        6) List Tags (watch table)
        7) Force Tag Value
        8) Step Scan Once
        9) Connect to SQL and list 10 rows
        A) Lookup by UID (SQL)
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
        case "9" -> listFromSql();
        case "A", "a" -> lookupByUid();
        case "0" -> { System.out.println("Bye!"); return; }
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
    System.out.print("Value (true/false or int or TIMER: enable/disable/reset/presetMs): ");
    String v = in.nextLine().trim();
    boolean ok = sim.force(name, v);
    System.out.println(ok ? "UPDATE OK" : "UPDATE FAIL (name/type mismatch?)");
  }

  private void stepScan() {
    long ms = sim.stepOnce();
    System.out.println("SCAN DONE in " + ms + " ms");
  }

  private void ensureSql() {
    if (sql == null) {
      sql = new SqlPlcDataSource();
      sql.connect("plc_data.db");
    }
  }

  private void listFromSql() {
    ensureSql();
    List<PlcSample> rows = sql.readAll();
    System.out.println("UID | PRODUCT | H | T | AGE | QTY | MTTF");
    rows.stream().limit(10).forEach(r -> System.out.println(r.toString()));
  }

  private void lookupByUid() {
    ensureSql();
    System.out.print("UID: ");
    int uid = Integer.parseInt(in.nextLine().trim());
    Optional<PlcSample> row = sql.findByUid(uid);
    System.out.println(row.map(Object::toString).orElse("Not found"));
  }
}
