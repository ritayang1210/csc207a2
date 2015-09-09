package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.HistoryCommand;
import driver.JShellHistoryRecorder;
import exceptions.JShellArgsInvalidException;
import fileSystem.FileSystem;

public class HistoryCommandTest {

  private HistoryCommand historyCommand;
  private FileSystem fileSystem;
  private JShellHistoryRecorder histRecorder;

  @Before
  public void setUp() {
    historyCommand = new HistoryCommand();
    fileSystem = FileSystem.getFileSystem();
    histRecorder = new JShellHistoryRecorder();
    historyCommand.setHistoryRecorder(histRecorder);

    histRecorder.recordCommand("command 1");
    histRecorder.recordCommand("command 2");
    histRecorder.recordCommand("command 3");
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    historyCommand.validateArgs(createArgs("10"));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsTooManyArgs()
      throws JShellArgsInvalidException {
    historyCommand.validateArgs(createArgs("1", "2"));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsTooNotNumber()
      throws JShellArgsInvalidException {
    historyCommand.validateArgs(createArgs("a"));
  }

  @Test
  public void testRunNoArg() {
    assertEquals(
        "1 command 1\n2 command 2\n3 command 3",
        historyCommand.run(createArgs(), fileSystem,
            fileSystem.getRootDir()));
  }

  @Test
  public void testRunWithArg() {
    assertEquals(
        "",
        historyCommand.run(createArgs("-1"), fileSystem,
            fileSystem.getRootDir()));
    assertEquals(
        "",
        historyCommand.run(createArgs("0"), fileSystem,
            fileSystem.getRootDir()));
    assertEquals(
        "3 command 3",
        historyCommand.run(createArgs("1"), fileSystem,
            fileSystem.getRootDir()));
    assertEquals(
        "2 command 2\n3 command 3",
        historyCommand.run(createArgs("2"), fileSystem,
            fileSystem.getRootDir()));
    assertEquals(
        "1 command 1\n2 command 2\n3 command 3",
        historyCommand.run(createArgs("3"), fileSystem,
            fileSystem.getRootDir()));
    assertEquals(
        "1 command 1\n2 command 2\n3 command 3",
        historyCommand.run(createArgs("100"), fileSystem,
            fileSystem.getRootDir()));
  }
}
