package datadog.homework;

import org.apache.commons.cli.CommandLine;
import org.junit.Assert;
import org.junit.Test;

public class CLITest {
  
  public void failParse() {
    Assert.assertNull(CLI.parseArgs(new String[] { "f", "b", "foo", "", "200", "-bar", "--foobar", }));
  }

  @Test
  public void sucessParse() {
    Assert.assertNotNull(CLI.parseArgs(null));
    Assert.assertNotNull(CLI.parseArgs(new String[0]));
    final CommandLine defaultCmd = CLI.parseArgs(new String[] { "f", "b", "foo", "", "200" });
    Assert.assertNotNull(defaultCmd);
    Assert.assertNull(defaultCmd.getOptionValue("t"));
    Assert.assertNull(defaultCmd.getOptionValue("f"));
    final CommandLine definedCmd = CLI.parseArgs(new String[] { "-f", "/tmp/other.log", "-t", "200" });
    Assert.assertNotNull(definedCmd);
    Assert.assertEquals("/tmp/other.log", definedCmd.getOptionValue("f"));
    Assert.assertEquals("200", definedCmd.getOptionValue("t"));
  }
}
