package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.EchoCommand;
import exceptions.JShellArgsInvalidException;
import fileSystem.FileSystem;

/**
 * Test class for EchoCommand
 *
 */
public class EchoCommandTest {

  private FileSystem fileSystem;
  private EchoCommand echoCmd;

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    echoCmd = new EchoCommand();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    /* Valid args */
    echoCmd.validateArgs(createArgs("\"blabla\""));
    echoCmd.validateArgs(createArgs("\"b", "lab", "la\""));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsNoArgs()
      throws JShellArgsInvalidException {
    /* Invalid arg */
    echoCmd.validateArgs(createArgs());
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsSingleArgNoDoubleQuotation()
      throws JShellArgsInvalidException {
    /* Invalid arg */
    echoCmd.validateArgs(createArgs("blabla"));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsSingleArgOnlyLeftDoubleQuotation()
      throws JShellArgsInvalidException {
    /* Invalid arg */
    echoCmd.validateArgs(createArgs("\"blabla"));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsSingleArgOnlyRightDoubleQuotation()
      throws JShellArgsInvalidException {
    /* Invalid arg */
    echoCmd.validateArgs(createArgs("blabla\""));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateMultiArgsNoOperand()
      throws JShellArgsInvalidException {
    /* Invalid arg */
    echoCmd.validateArgs(createArgs("blabla", "ha", "file1"));
  }

  @Test
  public void testRunSingleArg() {
    /* Just print */
    assertEquals("This is a single argument", echoCmd.run(
        createArgs("\"This", "is", "a", "single", "argument\""),
        fileSystem, fileSystem.getRootDir()));
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }

    return result;
  }
}
