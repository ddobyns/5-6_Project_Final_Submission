package com.dandobyns.plcsim.core.tags;

public class CounterTag extends Tag {
  private int preset;
  private int accum;
  private boolean done;

  public CounterTag(long id, String name, String address, String description, int preset) {
    super(id, name, TagType.COUNTER, address, description);
    this.preset = preset;
  }

  public void increment(){ accum++; }
  public void reset(){ accum=0; done=false; }
  public void setPreset(int p){ preset=p; }
  @Override public void evaluate(){ done = accum >= preset; }
  public String valueAsString(){ return "ACC=" + accum + " / PRE=" + preset + " DONE=" + done; }
}
