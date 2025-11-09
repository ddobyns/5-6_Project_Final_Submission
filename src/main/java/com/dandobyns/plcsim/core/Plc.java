/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * Plc.java
 *******************************************************************/
package com.dandobyns.plcsim.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dandobyns.plcsim.core.modules.DeviceModule;
import com.dandobyns.plcsim.core.tags.Tag;

public class Plc {
  private final List<DeviceModule> modules = new ArrayList<>();
  private final Map<String, Tag> tagsByName = new LinkedHashMap<>();

  public <T extends DeviceModule> T module(Class<T> clazz) {
    return modules.stream().filter(clazz::isInstance).map(clazz::cast).findFirst()
        .orElseThrow(() -> new IllegalStateException("Module not found: " + clazz.getSimpleName()));
  }

  public void addModule(DeviceModule m) {
    modules.add(m);
    m.tags().forEach(t -> tagsByName.put(t.getName(), t));
    m.onTagAdded(t -> tagsByName.put(t.getName(), t));
    m.onTagRemoved(t -> tagsByName.remove(t.getName()));
  }

  public Optional<Tag> findTag(String name) { return Optional.ofNullable(tagsByName.get(name)); }
  public Collection<Tag> allTags() { return tagsByName.values(); }
  public List<DeviceModule> getModules() { return modules; }
}
