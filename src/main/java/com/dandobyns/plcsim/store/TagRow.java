/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * TagRow.java - Represents a row in the tags table.
 *******************************************************************/
package com.dandobyns.plcsim.store;

import com.dandobyns.plcsim.core.tags.CoilTag;
import com.dandobyns.plcsim.core.tags.CounterTag;
import com.dandobyns.plcsim.core.tags.DigitalInputTag;
import com.dandobyns.plcsim.core.tags.DigitalOutputTag;
import com.dandobyns.plcsim.core.tags.RegisterTag;
import com.dandobyns.plcsim.core.tags.Tag;
import com.dandobyns.plcsim.core.tags.TagType;
import com.dandobyns.plcsim.core.tags.TimerTag;

public class TagRow {
  public Long id;
  public String name;
  public TagType type;
  public String address;
  public String description;
  public Integer value_bool;
  public Integer value_int;
  public Long preset_ms;
  public Long accum_ms;
  public Integer preset_count;
  public Integer accum_count;
  public Integer latched;

  public static TagRow fromTag(Tag t) {
    TagRow r = new TagRow();
    r.id = t.getId();
    r.name = t.getName();
    r.type = t.getType();
    r.address = t.getAddress();
    r.description = t.getDescription();
    switch (t.getType()) {
      case DIGITAL_INPUT -> r.value_bool = (t instanceof DigitalInputTag di && di.getState()) ? 1 : 0;
      case DIGITAL_OUTPUT -> r.value_bool = (t instanceof DigitalOutputTag d && d.getState()) ? 1 : 0;
      case COIL -> {
        CoilTag c = (CoilTag)t;
        r.value_bool = c.getState() ? 1 : 0;
        r.latched = 1; // display flag only
      }
      case REGISTER -> r.value_int = (t instanceof RegisterTag rg) ? rg.read() : 0;
      case TIMER -> {
        if (t instanceof TimerTag tt) {
          r.preset_ms = tt == null ? 1000L : null; // not strictly needed here
        }
      }
      case COUNTER -> { /* no-op in memory */ }
    }
    return r;
  }

  /** Convert a DB row back to a polymorphic Tag. */
  public Tag toTag() {
    String desc = description == null ? "" : description;
    switch (type) {
      case DIGITAL_INPUT   -> { DigitalInputTag di = new DigitalInputTag(id == null ? 0 : id, name, address, desc); di.setState(value_bool != null && value_bool == 1); return di; }
      case DIGITAL_OUTPUT  -> { DigitalOutputTag d  = new DigitalOutputTag(id == null ? 0 : id, name, address, desc); d.write(value_bool != null && value_bool == 1); return d; }
      case COIL            -> { CoilTag c = new CoilTag(id == null ? 0 : id, name, address, desc, (latched!=null && latched==1)); c.write(value_bool != null && value_bool == 1); return c; }
      case REGISTER        -> { return new RegisterTag(id == null ? 0 : id, name, address, desc, value_int == null ? 0 : value_int); }
      case TIMER           -> { return new TimerTag(id == null ? 0 : id, name, address, desc, preset_ms == null ? 1000L : preset_ms); }
      case COUNTER         -> { return new CounterTag(id == null ? 0 : id, name, address, desc, preset_count == null ? 10 : preset_count); }
      default -> throw new IllegalArgumentException("Unknown type: " + type);
    }
  }
}
