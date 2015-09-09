package test;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.ExitCommand;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellExpectedExitExceptiopn;
import fileSystem.FileSystem;

/**
 * Integration test for ExitCommand
 * 
 */
public class ExitCommandTest {

  private FileSystem fileSystem;
  private ExitCommand exitCmd;

  @Before
  public void setUp() throws Exception {
    fileSystem = FileSystem.getFileSystem();
    exitCmd = new ExitCommand();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    /* Test whether no argument is valid */
    exitCmd.validateArgs(new ArrayList<String>());
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsInvalid()
      throws JShellArgsInvalidException {
    /* Test whether throw exception when arguments exist */
    exitCmd.validateArgs(Arrays.asList("lol"));
  }

  @Test(expected = JShellExpectedExitExceptiopn.class)
  public void testRun() throws JShellExpectedExitExceptiopn {
    /* Test whether throw exception when executing exit command */
    exitCmd.run(new ArrayList<String>(), fileSystem,
        fileSystem.getRootDir());
  }
}
