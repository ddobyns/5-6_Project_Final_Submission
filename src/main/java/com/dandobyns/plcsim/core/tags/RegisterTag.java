package com.dandobyns.plcsim.core.tags;

public class RegisterTag extends Tag {
  private int value;
  public RegisterTag(long id, String name, String address, String description, int initial) {
    super(id, name, TagType.REGISTER, address, description);
    this.value = initial;
  }
  public void write(int v){ value=v; }
  public int read(){ return value; }
  public String valueAsString(){ return Integer.toString(value); }
}
