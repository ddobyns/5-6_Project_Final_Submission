package com.dandobyns.plcsim.core.tags;

public abstract class Tag {
  private long id;
  private final String name;
  private final TagType type;
  private final String address;
  private String description;

  protected Tag(long id, String name, TagType type, String address, String description) {
    this.id=id; this.name=name; this.type=type; this.address=address; this.description=description;
  }

  public long getId(){ return id; }
  public void setId(long id){ this.id=id; }
  public String getName(){ return name; }
  public TagType getType(){ return type; }
  public String getAddress(){ return address; }
  public String getDescription(){ return description; }
  public void setDescription(String d){ this.description=d; }

  /** Override in subclasses as needed. */
  public void evaluate() {}

  public abstract String valueAsString();

  @Override public String toString() {
    return name + " | " + type + " | " + address + " | " + valueAsString();
  }
}
