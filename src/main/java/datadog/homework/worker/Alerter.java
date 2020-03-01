package datadog.homework.worker;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ListIterator;

import datadog.homework.LogEntry;
import datadog.homework.LogSegment;

public class Alerter implements Runnable {
  
  private static final String ALERT = "High traffic generated an alert - hits = %d, avg = %f, triggered at %s";
  private static final String RECOVER = "Traffic has recovered - hits = %d, avg = %f, triggered at %s";
  
  private final List<LogSegment> segments;
  private final int threshold;
  private final int window;
  
  private boolean alert;
  
  public Alerter(final List<LogSegment> segments, final int threshold, final int window) {
    this.segments = segments;
    this.window = window;
    this.threshold = threshold;
  }
  
  @Override
  public void run() {
    
    int counter = 0;
    final ZonedDateTime now = ZonedDateTime.now();
    final ZonedDateTime windowLowerBound = now.minus(this.window, ChronoUnit.SECONDS);
    
    final ListIterator<LogSegment> iterator = this.segments.listIterator(this.segments.size());
    
    loop:
    while (iterator.hasPrevious()) {
      final LogSegment segment = iterator.previous();
      for (int l = segment.getLast(); l > 0; l--) {
        final LogEntry entry = segment.get(l);
        if (entry.getDate().isAfter(windowLowerBound)) {
          counter++;
        } else {
          break loop;
        }
      }
    }
    
    final double average = (double) counter / this.window;
    if (!this.alert && average > this.threshold) {
      System.err.println(String.format(ALERT, counter, average, now.toString()));
      this.alert = true;
    } else if (this.alert && average <= this.threshold) {
      System.err.println(String.format(RECOVER, counter, average, now.toString()));
      this.alert = false;
    }
  }
  
}
