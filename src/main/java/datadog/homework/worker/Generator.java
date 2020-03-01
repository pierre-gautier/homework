package datadog.homework.worker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.Random;

import datadog.homework.LogEntry;

/**
 * Generate and append random log lines to the monitored file
 */
public class Generator implements Runnable {
  
  private final File file;
  private final int number;
  
  public Generator(final File file, final int number) {
    this.file = file;
    this.number = number;
  }
  
  private static final String[] USERS = { "admin", "foo", "127.0.0.1", "", "-" };
  private static final String[] METHODS = { "POST", "GET", "OPTIONS", "DEL", "HEAD" };
  private static final String[] REQUESTS = { "/administrator", "/api/", "/api/users", "/api/users/3",
      "/api-v2/", "/newApi/", "/users/3?create=toto" };
  private static final String[] STATUS = { "200", "308", "404", "501" };
  
  @Override
  public void run() {
    final long t = System.currentTimeMillis();
    final StringBuilder builder = new StringBuilder();
    final int size = this.random(this.number);
    System.out.println("generating " + size + " lines");
    for (int i = 0; i < size; i++) {
      builder.append(this.random(USERS)).append(" ");
      builder.append(this.random(USERS)).append(" ");
      builder.append(this.random(USERS)).append(" ");
      builder.append("[").append(ZonedDateTime.now().format(LogEntry.FORMATTER)).append("] ");
      builder.append("\"").append(this.random(METHODS)).append(" ").append(this.random(REQUESTS))
          .append(" HTTP/1.1\" ");
      builder.append(this.random(STATUS)).append(" ").append(this.random(50000));
      builder.append(System.lineSeparator());
    }
    
    // some kind of hardcore rotation ^^
    if (this.file.length() > 100 * 1024 * 1024) {
      try (BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(),
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
        System.out.println("rotated logs");
      } catch (final IOException ioe) {
        ioe.printStackTrace();
      }
    }
    
    try (BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(),
        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write(builder.toString());
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }
    final long spent = System.currentTimeMillis() - t;
    if (spent > 800)
      System.err.println("generation took " + spent + " ms");
  }
  
  private int random(final int max) {
    return new Random().nextInt(max);
  }
  
  private String random(final String[] source) {
    return source[this.random(source.length - 1)];
  }
  
}
