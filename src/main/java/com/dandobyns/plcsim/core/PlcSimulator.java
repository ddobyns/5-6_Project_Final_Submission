/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * PlcSimulator.java
 *******************************************************************/
package com.dandobyns.plcsim.core;

import java.util.Optional;

import com.dandobyns.plcsim.core.modules.DigitalIOModule;
import com.dandobyns.plcsim.core.modules.RegisterModule;
import com.dandobyns.plcsim.core.modules.TimerCounterModule;
import com.dandobyns.plcsim.core.tags.CoilTag;
import com.dandobyns.plcsim.core.tags.CounterTag;
import com.dandobyns.plcsim.core.tags.DigitalInputTag;
import com.dandobyns.plcsim.core.tags.DigitalOutputTag;
import com.dandobyns.plcsim.core.tags.RegisterTag;
import com.dandobyns.plcsim.core.tags.Tag;
import com.dandobyns.plcsim.core.tags.TagType;
import com.dandobyns.plcsim.core.tags.TimerTag;

public class PlcSimulator {
  private final Plc plc;
  private final ScanEngine scan;

  public PlcSimulator(Plc plc, ScanEngine scan) {
    this.plc = plc; this.scan = scan;
  }

  public static PlcSimulator defaultBuild() {
    Plc plc = new Plc();
    plc.addModule(new DigitalIOModule("IO1"));
    plc.addModule(new RegisterModule("REG1"));
    plc.addModule(new TimerCounterModule("TC1"));
    return new PlcSimulator(plc, new ScanEngine(plc));
  }

  public Plc getPlc() { return plc; }

  public boolean createTag(TagType type, String name, String address, String desc) {
    if (plc.findTag(name).isPresent()) return false;
    switch (type) {
      case DIGITAL_INPUT -> plc.module(DigitalIOModule.class).addDigitalInput(name, address, desc);
      case DIGITAL_OUTPUT -> plc.module(DigitalIOModule.class).addDigitalOutput(name, address, desc);
      case REGISTER -> plc.module(RegisterModule.class).addRegister(name, address, desc);
      case TIMER -> plc.module(TimerCounterModule.class).addTimer(name, address, desc, 1000);
      case COUNTER -> plc.module(TimerCounterModule.class).addCounter(name, address, desc, 10);
      default -> { return false; }
    }
    return true;
  }

  public boolean force(String tagName, String valueText) {
    Optional<Tag> t = plc.findTag(tagName);
    if (t.isEmpty()) return false;
    Tag tag = t.get();
    try {
      switch (tag.getType()) {
        case DIGITAL_INPUT, DIGITAL_OUTPUT -> {
          boolean v = Boolean.parseBoolean(valueText);
          if (tag instanceof DigitalInputTag di) { di.setState(v); return true; }
          if (tag instanceof DigitalOutputTag d) { d.write(v); return true; }
          if (tag instanceof CoilTag c) { c.write(v); return true; }
          return false;
        }
        case COIL -> { ((CoilTag)tag).write(Boolean.parseBoolean(valueText)); return true; }
        case REGISTER -> { ((RegisterTag)tag).write(Integer.parseInt(valueText)); return true; }
        case TIMER -> {
          TimerTag tt = (TimerTag) tag;
          if ("reset".equalsIgnoreCase(valueText)) { tt.reset(); return true; }
          if ("enable".equalsIgnoreCase(valueText)) { tt.enable(true); return true; }
          if ("disable".equalsIgnoreCase(valueText)) { tt.enable(false); return true; }
          if (valueText.matches("\\d+")) { tt.setPresetMs(Long.parseLong(valueText)); return true; }
          return false;
        }
        case COUNTER -> {
          CounterTag ct = (CounterTag) tag;
          if ("reset".equalsIgnoreCase(valueText)) { ct.reset(); return true; }
          if ("inc".equalsIgnoreCase(valueText) || "++".equals(valueText)) { ct.increment(); return true; }
          if (valueText.matches("\\d+")) { ct.setPreset(Integer.parseInt(valueText)); return true; }
          return false;
        }
        default -> { return false; }
      }
    } catch (Exception ex) { return false; }
  }

  public long stepOnce() { return scan.scanOnce(); }
}
