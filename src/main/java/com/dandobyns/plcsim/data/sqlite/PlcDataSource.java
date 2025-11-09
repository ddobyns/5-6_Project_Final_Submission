/*******************************************************************
 * Name: Dan Dobyns
 * Date: 11/9/2025
 * Assignment: SDC330 Week 3 – Course Project
 * Description: Java application that simulates a simple PLC system.
 * plcdatasource.java
 *******************************************************************/
package com.dandobyns.plcsim.data.sqlite;

import java.util.List;
import java.util.Optional;

import com.dandobyns.plcsim.data.PlcSample;

public interface PlcDataSource {
  void connect(String sqlitePath);
  List<PlcSample> readAll();
  Optional<PlcSample> findByUid(int uid);
  void close();
}
