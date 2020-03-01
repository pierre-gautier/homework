package datadog.homework.worker;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import datadog.homework.LogEntry;
import datadog.homework.LogSegment;

public class Displayer implements Runnable {
  
  private final List<LogSegment> segments;
  private final int delay;
  private final int topSize;
  
  private LogEntry last = null;
  
  public Displayer(final List<LogSegment> segments, final int delay, final int topSize) {
    this.segments = segments;
    this.delay = delay;
    this.topSize = topSize;
  }
  
  @Override
  public void run() {
    
    final Map<String, Integer> sectionCounter = new HashMap<>();
    int totalAccess = 0;
    int totalError = 0;
    long totalTransfered = 0;
    
    LogEntry lastEntry = null;
    
    final ListIterator<LogSegment> iterator = this.segments.listIterator(this.segments.size());
    
    loop:
    while (iterator.hasPrevious()) {
      final LogSegment segment = iterator.previous();
      for (int l = segment.getLast(); l > 0; l--) {
        final LogEntry entry = segment.get(l);
        if (lastEntry == null) {
          lastEntry = entry;
        }
        if (entry == this.last) {
          break loop;
        }
        totalAccess++;
        totalTransfered += entry.getSize();
        if (entry.getStatus() >= 300) {
          totalError++;
        }
        final String section = entry.getSection();
        final Integer counter = sectionCounter.get(section);
        if (counter == null) {
          sectionCounter.put(section, 1);
        } else {
          sectionCounter.put(section, counter + 1);
        }
      }
    }
    
    // save last log entry seen
    this.last = lastEntry;
    
    if (totalAccess > 0) {
      System.out.println("---------------------------- since last " + this.delay + " seconds ");
      System.out.println(totalAccess + " access, of wich " + totalError + " errors, for "
          + FileUtils.byteCountToDisplaySize(totalTransfered));
      System.out.println("top sections ");
      sectionCounter.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue())
          .limit(this.topSize).forEach(e -> System.out.println(" " + e.getKey() + " " + e.getValue()));
    }
  }
}
