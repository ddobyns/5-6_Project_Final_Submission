/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * TimerCounterModule.java
 *******************************************************************/
package com.dandobyns.plcsim.core.modules;

import com.dandobyns.plcsim.core.tags.CounterTag;
import com.dandobyns.plcsim.core.tags.TimerTag;

public class TimerCounterModule extends AbstractModule {
  public TimerCounterModule(String name) { super(name); }
  public void addTimer(String name, String addr, String desc, long presetMs) { added(new TimerTag(0, name, addr, desc, presetMs)); }
  public void addCounter(String name, String addr, String desc, int preset) { added(new CounterTag(0, name, addr, desc, preset)); }

  @Override public void tick(long deltaMs) {
    for (var t : tags) if (t instanceof TimerTag tt) tt.tick(deltaMs);
  }
}
