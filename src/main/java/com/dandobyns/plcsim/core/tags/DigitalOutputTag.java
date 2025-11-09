package com.dandobyns.plcsim.core.tags;

public class DigitalOutputTag extends Tag {
  private boolean state;
  public DigitalOutputTag(long id, String name, String address, String description) {
    super(id, name, TagType.DIGITAL_OUTPUT, address, description);
  }
  public void write(boolean v){ state=v; }
  public boolean getState(){ return state; }
  public String valueAsString(){ return Boolean.toString(state); }
}
