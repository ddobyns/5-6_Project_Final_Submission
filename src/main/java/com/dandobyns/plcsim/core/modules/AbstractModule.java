/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * AbstractModule.java
 *******************************************************************/
 package com.dandobyns.plcsim.core.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.dandobyns.plcsim.core.tags.Tag;

public abstract class AbstractModule implements DeviceModule {
  protected final String moduleName;
  protected final List<Tag> tags = new ArrayList<>();
  private Consumer<Tag> addCb = t -> {};
  private Consumer<Tag> remCb = t -> {};

  protected AbstractModule(String moduleName) { this.moduleName = moduleName; }
  public String name() { return moduleName; }
  public List<Tag> tags() { return tags; }
  public void initialize() {}
  public void tick(long deltaMs) {}
  public void onTagAdded(Consumer<Tag> cb) { this.addCb = cb == null ? t -> {} : cb; }
  public void onTagRemoved(Consumer<Tag> cb) { this.remCb = cb == null ? t -> {} : cb; }

  protected void added(Tag t){ tags.add(t); addCb.accept(t); }
  protected void removed(Tag t){ tags.remove(t); remCb.accept(t); }
}
