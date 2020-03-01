package datadog.homework;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import datadog.homework.worker.Alerter;
import datadog.homework.worker.Cleaner;
import datadog.homework.worker.Displayer;
import datadog.homework.worker.Generator;

public class Engine extends TailerListenerAdapter {
  
  private final File file;
  private final int display;
  private final int top;
  private final int threshold;
  private final int window;
  private final int size;
  private final boolean generator;
  
  private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
  
  private final List<LogSegment> segments = new CopyOnWriteArrayList<>();
  
  private LogSegment segment;
  
  public Engine(final File file, final int display, final int top, final int threshold,
      final int window, final int size, final boolean generator) {
    super();
    this.file = file;
    this.display = display;
    this.top = top;
    this.threshold = threshold;
    this.window = window;
    this.size = size;
    this.generator = generator;
  }
  
  public void start() {
    if (!this.generator) {
      // start tailer
      final Tailer tailer = new Tailer(this.file, this, 100, true, true);
      final Thread tailerThread = new Thread(tailer);
      tailerThread.setName("trailer");
      tailerThread.start();
      // start scheduled runnables
      // displayer
      this.executor.scheduleAtFixedRate(new Displayer(this.segments, this.display, this.top),
          this.display, this.display, TimeUnit.SECONDS);
      // alerter
      this.executor.scheduleAtFixedRate(new Alerter(this.segments, this.threshold, this.window),
          1, 1, TimeUnit.SECONDS);
      // cleaner
      final int timeBeforeRemoval = Math.max(this.display, this.window);
      this.executor.scheduleAtFixedRate(new Cleaner(this.segments, timeBeforeRemoval),
          1, 1, TimeUnit.SECONDS);
    } else {
      this.executor.scheduleWithFixedDelay(new Generator(this.file), 1, 1, TimeUnit.SECONDS);
    }
  }
  
  @Override
  public void handle(final String line) {
    final LogEntry entry = LogEntry.of(line);
    if (entry != null) {
      if (this.segment == null || this.segment.isFull()) {
        this.segment = new LogSegment(this.size, entry);
        this.segments.add(this.segment);
      }
      this.segment.add(entry);
    }
  }
  
}
