package datadog.homework.worker;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import datadog.homework.LogSegment;

/**
 * Remove outdated LogSegments from the main collection to avoid to get OOM.
 */
public class Cleaner implements Runnable {
  
  private final List<LogSegment> segments;
  private final int timeBeforeRemoval;
  
  public Cleaner(final List<LogSegment> segments, final int timeBeforeRemoval) {
    this.segments = segments;
    this.timeBeforeRemoval = timeBeforeRemoval;
  }
  
  @Override
  public void run() {
    final ZonedDateTime removalLimit = ZonedDateTime.now().minus(this.timeBeforeRemoval, ChronoUnit.SECONDS);
    // HashSet has a good .contains() that's used in .removeAll()
    final Collection<LogSegment> toRemove = new HashSet<>();
    // Using an iterator here allows some implementations to use a snapshot to iterate over the collection
    final Iterator<LogSegment> iterator = this.segments.iterator();
    while (iterator.hasNext()) {
      final LogSegment segment = iterator.next();
      // check the segment is old enough, and not the last one
      if (segment.getFirstDate().isBefore(removalLimit) && iterator.hasNext()) {
        toRemove.add(segment);
      } else {
        // since segments are ordered by date when we meet the first too young we can stop
        break;
      }
    }
    this.segments.removeAll(toRemove);
  }
  
}
