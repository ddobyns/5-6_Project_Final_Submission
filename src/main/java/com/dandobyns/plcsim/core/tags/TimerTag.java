package com.dandobyns.plcsim.core.tags;

public class TimerTag extends Tag {
  private long presetMs;
  private long accumMs;
  private boolean enabled;
  private boolean done;

  public TimerTag(long id, String name, String address, String description, long presetMs) {
    super(id, name, TagType.TIMER, address, description);
    this.presetMs = presetMs;
  }

  public void enable(boolean e){ enabled=e; }
  public void reset(){ accumMs=0; done=false; }
  public void setPresetMs(long ms){ presetMs=ms; }
  public void tick(long deltaMs){ if(!enabled || done) return; accumMs += deltaMs; }
  @Override public void evaluate(){ done = accumMs >= presetMs; }
  public String valueAsString(){ return "EN=" + enabled + " ACC=" + accumMs + "ms / PRE=" + presetMs + "ms DONE=" + done; }
}
