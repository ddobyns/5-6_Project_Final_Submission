/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * DigitalIOModule.java
 *******************************************************************/
package com.dandobyns.plcsim.core.modules;

import com.dandobyns.plcsim.core.tags.DigitalInputTag;
import com.dandobyns.plcsim.core.tags.DigitalOutputTag;

public class DigitalIOModule extends AbstractModule {
  public DigitalIOModule(String name) { super(name); }
  public void addDigitalInput(String name, String addr, String desc) { added(new DigitalInputTag(0, name, addr, desc)); }
  public void addDigitalOutput(String name, String addr, String desc) { added(new DigitalOutputTag(0, name, addr, desc)); }
}
