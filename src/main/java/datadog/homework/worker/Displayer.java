package datadog.homework.worker;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import datadog.homework.LogEntry;
import datadog.homework.LogSegment;

/**
 * Display stats.
 * <p/>
 * Display stats every delay seconds about the traffic: the sections of the web site with the most hits,
 * as well as interesting summary statistics on the traffic as a whole.
 */
public class Displayer implements Runnable {
  
  private static final String STATS = "Traffic during last %d seconds: %d access of which %d errors for %s, top sections: ";
  
  private final List<LogSegment> segments;
  private final int delay;
  private final int topSize;
  
  private LogEntry last;
  
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
      for (int l = segment.getLast(); l >= 0; l--) {
        final LogEntry entry = segment.get(l);
        if (lastEntry == null) {
          lastEntry = entry;
        }
        // stop when we reach the last entry seen during the last run
        if (entry == this.last) {
          break loop;
        }
        // several simple counters
        totalAccess++;
        totalTransfered += entry.getSize();
        if (entry.getStatus() >= 400) {
          totalError++;
        }
        // counter by section
        sectionCounter.merge(entry.getSection(), 1, Integer::sum);
      }
    }
    
    // save last log entry seen for the next run
    this.last = lastEntry;
    
    if (totalAccess > 0) {
      final StringBuilder out = new StringBuilder();
      out.append(String.format(STATS, this.delay, totalAccess, totalError,
          FileUtils.byteCountToDisplaySize(totalTransfered)));
      sectionCounter.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue())
          .limit(this.topSize).forEach(e -> out.append(String.format(" %s %d hits", e.getKey(), e.getValue())));
      System.out.println(out);
    }
  }
}
