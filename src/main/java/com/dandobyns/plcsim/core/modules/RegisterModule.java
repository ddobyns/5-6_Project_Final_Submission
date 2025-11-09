/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * RegisterModule.java
 *******************************************************************/
package com.dandobyns.plcsim.core.modules;

import com.dandobyns.plcsim.core.tags.RegisterTag;

public class RegisterModule extends AbstractModule {
  public RegisterModule(String name) { super(name); }
  public void addRegister(String name, String addr, String desc) { added(new RegisterTag(0, name, addr, desc, 0)); }
}
