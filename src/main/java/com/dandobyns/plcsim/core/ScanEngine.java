/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * ScanEngine.java
 *******************************************************************/
package com.dandobyns.plcsim.core;

import com.dandobyns.plcsim.core.modules.DeviceModule;
import com.dandobyns.plcsim.core.tags.Tag;

public class ScanEngine {
  private final Plc plc;
  public ScanEngine(Plc plc) { this.plc = plc; }
  public long scanOnce() {
    long start = System.nanoTime();
    for (DeviceModule m : plc.getModules()) m.tick(50);
    for (Tag t : plc.allTags()) t.evaluate();
    return (System.nanoTime() - start) / 1_000_000;
  }
}
