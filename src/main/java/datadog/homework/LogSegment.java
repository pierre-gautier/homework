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

  public int getLast() {
    return this.last;
  }

  public void add(final LogEntry entry) {
    if (this.last < this.entries.length - 1)
      this.entries[++this.last] = entry;
    if (this.entries[this.last] == null) {
      System.err.println("aie");
    }
  }

  public LogEntry get(final int index) {
    return this.entries[index];
  }
  
  public boolean isFull() {
    return this.last == this.entries.length - 1;
  }

}
