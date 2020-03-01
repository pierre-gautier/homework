package datadog.homework;

import java.time.ZonedDateTime;

public class LogSegment {
  
  private final LogEntry[] entries;
  private final ZonedDateTime firstDate;
  private int last = -1;
  
  public LogSegment(final int size, final LogEntry first) {
    this.entries = new LogEntry[size];
    this.firstDate = first.getDate();
    this.add(first);
  }
  
  public ZonedDateTime getFirstDate() {
    return this.firstDate;
  }
  
  public synchronized int getLast() {
    return this.last;
  }
  
  public synchronized void add(final LogEntry entry) {
    if (this.last < this.entries.length - 1)
      this.entries[++this.last] = entry;
  }
  
  public LogEntry get(final int index) {
    return this.entries[index];
  }
  
  public synchronized boolean isFull() {
    return this.last == this.entries.length - 1;
  }
  
}
