package com.dandobyns.plcsim.core.tags;

public class CoilTag extends Tag {
  private boolean state;
  private boolean latched;
  public CoilTag(long id, String name, String address, String description, boolean latched) {
    super(id, name, TagType.COIL, address, description);
    this.latched = latched;
  }
  public void setLatched(boolean l){ latched=l; }
  public void write(boolean v){ state = v || (latched && state); }
  public boolean getState(){ return state; }
  public String valueAsString(){ return (state ? "true" : "false") + (latched ? " (latched)" : ""); }
}
