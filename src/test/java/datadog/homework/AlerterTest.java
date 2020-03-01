package datadog.homework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import datadog.homework.worker.Alerter;

public class AlerterTest {
  
  @Test
  public void testAlerter() throws IOException, InterruptedException {
    
    final File temp = File.createTempFile("test", "alert");
    System.setErr(new PrintStream(temp));
    
    new Alerter(this.createSegments(10), 10, 1).run();
    this.assertOutputEmpty(temp);
    
    new Alerter(this.createSegments(11), 10, 1).run();
    this.assertOutputContains(temp, "High traffic generated", "11", "11.0");
    
    new Alerter(this.createSegments(11), 10, 2).run();
    this.assertOutputEmpty(temp);
    
    final Alerter alerter = new Alerter(this.createSegments(30), 10, 2);
    alerter.run();
    this.assertOutputContains(temp, "High traffic generated", "30", "15.0");
    alerter.run();
    this.assertOutputEmpty(temp); // still in alert, no more message
    Thread.sleep(1000);
    alerter.run();
    this.assertOutputEmpty(temp); // still
    Thread.sleep(1000);
    alerter.run();
    this.assertOutputContains(temp, "Traffic has recovered");
    
  }
  
  private void assertOutputEmpty(final File file) throws IOException {
    try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
      while (reader.readLine() != null) {
        Assert.fail("Output must be empty");
      }
    }
  }
  
  private void assertOutputContains(final File file, final String... expected) throws IOException {
    try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
      int lineNumber = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        for (final String element : expected) {
          Assert.assertTrue("Line does not contain " + element, line.contains(element));
        }
        lineNumber++;
      }
      Assert.assertEquals("output must be a single line", 1, lineNumber);
    }
    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(),
        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
    }
  }
  
  private List<LogSegment> createSegments(final int number) {
    final List<LogSegment> segments = new ArrayList<>(number);
    for (int i = 0; i < number; i++) {
      final LogEntry entry = LogEntry.of(String.format("- - - [%s] \"GET /report HTTP/1.0\" 200 123",
          ZonedDateTime.now().format(LogEntry.FORMATTER)));
      if (segments.isEmpty()) {
        segments.add(new LogSegment(100, entry));
      } else {
        segments.get(segments.size() - 1).add(entry);
      }
    }
    return segments;
  }
  
}
