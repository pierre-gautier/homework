package datadog.homework;

import org.junit.Assert;
import org.junit.Test;

public class LogEntryTest {

  private static final String[] LOG_LINES = {
      "127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123",
      "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234",
      "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34",
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 503 12",
  };
  
  private static final String[] FAULTY_LOG_LINES = {
      "127.0.0.1 james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123", // miss rfc
      "127.0.0.1 - jill [09/07/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234", // month as MM instead of MMM
      "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200", // miss size
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] POST /api/user HTTP/1.0 503 12", // miss "" around request
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] POST /api/user HTTP/1.0 notfound 12", // status as string
  };

  @Test
  public void failOfAndToString() {
    for (final String line : LogEntryTest.FAULTY_LOG_LINES) {
      final LogEntry logEntry = LogEntry.of(line);
      Assert.assertNull(logEntry);
    }
  }

  @Test
  public void sucessOfAndToString() {
    for (final String line : LogEntryTest.LOG_LINES) {
      final LogEntry logEntry = LogEntry.of(line);
      Assert.assertNotNull(logEntry);
      Assert.assertEquals(line, logEntry.toString());
    }
  }

}
