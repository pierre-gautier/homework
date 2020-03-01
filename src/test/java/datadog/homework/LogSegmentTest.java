package datadog.homework;

import org.junit.Assert;
import org.junit.Test;

public class LogSegmentTest {
  
  @Test
  public void test() {
    final LogEntry entry = LogEntry.of(LogEntryTest.LOG_LINES[0]);
    final LogSegment segment = new LogSegment(3, entry);
    Assert.assertEquals(0, segment.getLast());
    segment.add(entry);
    Assert.assertFalse(segment.isFull());
    segment.add(entry);
    Assert.assertTrue(segment.isFull());
    Assert.assertEquals(2, segment.getLast());
    segment.add(entry);
    Assert.assertEquals(2, segment.getLast());
  }
}
