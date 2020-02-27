package datadog.homework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CommandLine Interface to start the monitoring engine
 */
public class CLI {
  
  public static final String DEFAULT_FILE_PATH = "/tmp/access.log";
  public static final String DEFAULT_THRESHOLD = "10";
  
  public static void main(final String[] args) {
    final CommandLine cmd = CLI.parseArgs(args);
    if (cmd == null) {
      System.exit(1);
    }
    final String filePath = cmd.getOptionValue("f", CLI.DEFAULT_FILE_PATH);
    final int threshold = Integer.parseInt(cmd.getOptionValue("t", CLI.DEFAULT_THRESHOLD));
    // start engine with parameters
  }
  
  public static CommandLine parseArgs(final String[] args) {
    final Options options = new Options();
    options.addOption(Option.builder("f").longOpt("file").desc("the log file")
        .hasArg(true).optionalArg(true).build());
    options.addOption(Option.builder("t").longOpt("threshold").desc("requests per second alert threshold")
        .hasArg(true).optionalArg(true).build());
    try {
      return new DefaultParser().parse(options, args);
    } catch (final ParseException e) {
      System.err.println(e.getMessage());
      new HelpFormatter().printHelp("homework", options);
      return null;
    }
  }
}
