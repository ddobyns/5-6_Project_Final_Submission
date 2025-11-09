package com.dandobyns.plcsim.core.tags;

public class DigitalInputTag extends Tag {
  private boolean state;
  public DigitalInputTag(long id, String name, String address, String description) {
    super(id, name, TagType.DIGITAL_INPUT, address, description);
  }
  public void setState(boolean v){ state=v; }
  public boolean getState(){ return state; }
  public String valueAsString(){ return Boolean.toString(state); }
}
