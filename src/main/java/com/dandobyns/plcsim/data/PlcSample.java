/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * plcsample.java
 *******************************************************************/
package com.dandobyns.plcsim.data;

public class PlcSample {
  private final int uid;
  private final String productType;
  private final double humidity;
  private final double temperature;
  private final int age;
  private final int quantity;
  private final int mttf;

  public PlcSample(int uid, String productType, double humidity, double temperature, int age, int quantity, int mttf) {
    this.uid = uid; this.productType = productType; this.humidity = humidity;
    this.temperature = temperature; this.age = age; this.quantity = quantity; this.mttf = mttf;
  }

  public int getUid() { return uid; }
  public String getProductType() { return productType; }
  public double getHumidity() { return humidity; }
  public double getTemperature() { return temperature; }
  public int getAge() { return age; }
  public int getQuantity() { return quantity; }
  public int getMttf() { return mttf; }

  @Override public String toString() {
    return uid + " | " + productType + " | H=" + humidity + " T=" + temperature +
           " | AGE=" + age + " QTY=" + quantity + " | MTTF=" + mttf;
  }
}
