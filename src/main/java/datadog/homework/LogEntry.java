package datadog.homework;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A LogEntry object parsed from w3c common logfile format.
 * <p/>
 * The class is immutable.
 *
 * @see <a href="https://www.w3.org/Daemon/User/Config/Logging.html#common-logfile-format">w3c format</a>
 */
public class LogEntry {
  
  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
  
  public static final String REGEX = "^(?<remotehost>\\S*?) (?<rfc931>\\S*?) (?<authuser>\\S*?)"
      + " \\[(?<date>.*?)\\] \\\"(?<method>\\S*?) (?<section>\\/[^\\/\\s]*)(?<request>\\S*?) (?<protocol>HTTP\\/[0-9.]+)\\\" (?<status>\\d*?) (?<size>\\d*?)( .*$|$)";
  
  public static final Pattern PATTERN = Pattern.compile(LogEntry.REGEX);
  
  /**
   * Creates a LogEntry instance from an input string.
   *
   * @param input
   *          the string to build the LogEntry instance
   * @return a new LogEntry instance or null
   */
  public static LogEntry of(final String input) {
    final Matcher matcher = LogEntry.PATTERN.matcher(input);
    if (matcher.matches()) {
      try {
        return new LogEntry(matcher.group("remotehost"), matcher.group("rfc931"), matcher.group("authuser"),
            ZonedDateTime.parse(matcher.group("date"), LogEntry.FORMATTER),
            matcher.group("method"), matcher.group("section"), matcher.group("request"), matcher.group("protocol"),
            Integer.parseInt(matcher.group("status")), Integer.parseInt(matcher.group("size")));
      } catch (final Exception e) {
        System.err.println(e.getMessage() + ": " + input);
        return null;
      }
    }
    System.err.println("Input does not match defined pattern: " + input);
    return null;
  }
  
  private final String remotehost;
  private final String rfc931;
  private final String authuser;
  private final ZonedDateTime date;
  private final String method;
  private final String section;
  private final String request;
  private final String protocol;
  private final int status;
  private final int size;
  
  public LogEntry(final String remotehost, final String rfc931, final String authuser, final ZonedDateTime date,
      final String method, final String section, final String request, final String protocol, final int status,
      final int size) {
    super();
    this.remotehost = remotehost;
    this.rfc931 = rfc931;
    this.authuser = authuser;
    this.date = date;
    this.method = method;
    this.section = section;
    this.request = request;
    this.protocol = protocol;
    this.status = status;
    this.size = size;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (this.getClass() != obj.getClass())
      return false;
    final LogEntry other = (LogEntry) obj;
    if (this.authuser == null) {
      if (other.authuser != null)
        return false;
    } else if (!this.authuser.equals(other.authuser))
      return false;
    if (this.date == null) {
      if (other.date != null)
        return false;
    } else if (!this.date.equals(other.date))
      return false;
    return true;
  }
  
  public String getAuthuser() {
    return this.authuser;
  }
  
  public ZonedDateTime getDate() {
    return this.date;
  }
  
  public String getMethod() {
    return this.method;
  }
  
  public String getProtocol() {
    return this.protocol;
  }
  
  public String getRemotehost() {
    return this.remotehost;
  }
  
  public String getSection() {
    return this.section;
  }
  
  public String getRequest() {
    return this.request;
  }
  
  public String getRfc931() {
    return this.rfc931;
  }
  
  public int getSize() {
    return this.size;
  }
  
  public int getStatus() {
    return this.status;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.authuser == null ? 0 : this.authuser.hashCode());
    result = prime * result + (this.date == null ? 0 : this.date.hashCode());
    return result;
  }
  
  /**
   * To String method that complies to the w3c format
   */
  @Override
  public String toString() {
    return String.format("%s %s %s [%s] \"%s %s%s %s\" %d %d",
        this.remotehost, this.rfc931, this.authuser,
        this.date.format(LogEntry.FORMATTER),
        this.method, this.section, this.request, this.protocol,
        this.status, this.size);
  }
  
}
