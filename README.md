# Phase #1
# PLC SQL Week 3 (Java, Maven)
Build/run:
  mvn -q clean compile
  mvn -q exec:java -Dexec.mainClass=com.dandobyns.plcsim.App
Menu:
  9 = list first 10 SQL rows  |  A = lookup by UID
