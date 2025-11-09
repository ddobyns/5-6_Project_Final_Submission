/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 4 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * DeviceModule.java
 *******************************************************************/
package com.dandobyns.plcsim.core.modules;

import java.util.List;
import java.util.function.Consumer;

import com.dandobyns.plcsim.core.tags.Tag;

public interface DeviceModule {
  String name();
  List<Tag> tags();
  void initialize();
  void tick(long deltaMs);
  void onTagAdded(Consumer<Tag> callback);
  void onTagRemoved(Consumer<Tag> callback);
}
