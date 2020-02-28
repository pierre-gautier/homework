package datadog.homework;

import java.io.File;

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
  public static final String DEFAULT_DISPLAY = "10";
  private static final String DEFAULT_TOP = "3";
  public static final String DEFAULT_THRESHOLD = "10";
  public static final String DEFAULT_WINDOW = "120";
  
  public static void main(final String[] args) {
    final CommandLine cmd = CLI.parseArgs(args);
    if (cmd == null) {
      System.exit(1);
    }
    final String filePath = cmd.getOptionValue("f", CLI.DEFAULT_FILE_PATH);
    final int display = Integer.parseInt(cmd.getOptionValue("d", CLI.DEFAULT_DISPLAY));
    final int top = Integer.parseInt(cmd.getOptionValue("p", CLI.DEFAULT_TOP));
    final int threshold = Integer.parseInt(cmd.getOptionValue("t", CLI.DEFAULT_THRESHOLD));
    final int window = Integer.parseInt(cmd.getOptionValue("w", CLI.DEFAULT_WINDOW));
    final boolean generator = cmd.hasOption("g");
    // start engine with parameters
    new Engine(new File(filePath), display, top, threshold, window, generator).start();
  }
  
  public static CommandLine parseArgs(final String[] args) {
    final Options options = new Options();
    options.addOption(Option.builder("f").longOpt("file")
        .desc("the log file path")
        .hasArg(true).optionalArg(true).build());
    options.addOption(Option.builder("d").longOpt("display")
        .desc("time in seconds between displays")
        .hasArg(true).optionalArg(true).build());
    options.addOption(Option.builder("p").longOpt("top")
        .desc("size of top sections displayed")
        .hasArg(true).optionalArg(true).build());
    options.addOption(Option.builder("t").longOpt("threshold")
        .desc("requests per second alert threshold")
        .hasArg(true).optionalArg(true).build());
    options.addOption(Option.builder("w").longOpt("window")
        .desc("consecutive time in seconds of threshold exceeding before alerting")
        .hasArg(true).optionalArg(true).build());
    options.addOption(Option.builder("g").longOpt("generator")
        .desc("start engine in generator mode")
        .hasArg(false).optionalArg(true).build());
    try {
      return new DefaultParser().parse(options, args);
    } catch (final ParseException e) {
      System.err.println(e.getMessage());
      new HelpFormatter().printHelp("homework", options);
      return null;
    }
  }
}
