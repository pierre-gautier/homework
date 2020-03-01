package datadog.homework;

import org.junit.Assert;
import org.junit.Test;

public class LogEntryTest {
  
  public static final String[] LOG_LINES = {
      "127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123",
      "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234",
      "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34",
      "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34",
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 503 12",
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user/3?create=toto HTTP/1.0\" 503 12",
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST / HTTP/1.0\" 503 12",
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/ HTTP/1.0\" 503 12",
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user/3 HTTP/1.0\" 503 12",
      "176.114.206.26 - - [18/Feb/2016:10:29:18 +0100] \"POST /administrator/index.php HTTP/1.1\" 200 4494",
      "176.114.206.26 - - [18/Feb/2016:10:29:18 +0100] \"POST /administrator/index.php HTTP/1.1\" 200 4494 \"http://almhuette-raith.at/administrator/\" \"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36\" \"-\"",
  };
  
  public static final String[] FAULTY_LOG_LINES = {
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST HTTP/1.0\" 503 12", // miss request
      "127.0.0.1 james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123", // miss rfc
      "127.0.0.1 - jill [09/07/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234", // month as MM instead of MMM
      "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200", // miss size
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] POST /api/user HTTP/1.0 503 12", // miss "" around request
      "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" notfound 12", // status as string
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
      Assert.assertTrue(line.startsWith(logEntry.toString()));
    }
  }
  
}
