package datadog.homework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import datadog.homework.worker.Displayer;
import datadog.homework.worker.Generator;

public class Engine extends TailerListenerAdapter {

  private final File file;
  private final int display;
  private final int threshold;
  private final int window;
  private final int topSize;
  private final boolean generator;

  private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);

  private final List<LogEntry> entries = new ArrayList<>();

  public Engine(final File file, final int display, final int topSize, final int threshold, final int window,
      final boolean generator) {
    super();
    this.file = file;
    this.display = display;
    this.topSize = topSize;
    this.threshold = threshold;
    this.window = window;
    this.generator = generator;
  }

  public void start() {
    if (!this.generator) {
      // start tailer
      final Tailer tailer = new Tailer(this.file, this, 100);
      final Thread tailerThread = new Thread(tailer);
      tailerThread.setName("trailer");
      tailerThread.start();
      // start scheduled runnables
      this.executor.scheduleAtFixedRate(new Displayer(this.entries, this.display, this.topSize),
          this.display, this.display, TimeUnit.SECONDS);
    } else {
      this.executor.scheduleWithFixedDelay(new Generator(this.file), 1, 1, TimeUnit.SECONDS);
    }
  }

  @Override
  public void handle(final String line) {
    final LogEntry entry = LogEntry.of(line);
    if (entry != null)
      this.entries.add(entry);
  }

}
