package datadog.homework.worker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import datadog.homework.LogEntry;

public class Displayer implements Runnable {

  private final List<LogEntry> entries;
  private final int delay;
  private final int topSize;

  private LogEntry last = null;

  public Displayer(final List<LogEntry> entries, final int delay, final int topSize) {
    this.entries = entries;
    this.delay = delay;
    this.topSize = topSize;
  }

  @Override
  public void run() {

    final int last = this.entries.size() - 1;

    int totalAccess = 0;
    int totalNonServedRequests = 0;
    long totalTransfered = 0;
    final Map<String, Integer> sectionCounter = new HashMap<>();

    for (int i = last; i > 0 && this.entries.get(i) != this.last; i--) {
      final LogEntry current = this.entries.get(i);
      totalAccess++;
      totalTransfered += current.getSize();
      if (current.getStatus() >= 300) {
        totalNonServedRequests++;
      }
      final String section = current.getSection();
      final Integer counter = sectionCounter.get(section);
      if (counter == null) {
        sectionCounter.put(section, 1);
      } else {
        sectionCounter.put(section, counter + 1);
      }
    }
    this.last = this.entries.get(last);

    if (totalAccess > 0) {
      System.out.println("---------------------------- since last " + this.delay + " seconds ");
      System.out.println(totalAccess + " access, of wich " + totalNonServedRequests + " non served, for "
          + FileUtils.byteCountToDisplaySize(totalTransfered));
      System.out.println("top sections ");
      sectionCounter.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue())
          .limit(this.topSize).forEach(e -> System.out.println(" " + e.getKey() + " " + e.getValue()));
    }

  }

}
