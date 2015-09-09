package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.JShellCommand;
import commands.ManCommand;
import driver.CmdManager;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellNoManualEntryException;
import fileSystem.FileSystem;

/**
 * Integration test for ManCommand
 * 
 */
public class ManCommandTest {

  private FileSystem fileSystem;
  private ManCommand manCmd;

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    manCmd = new ManCommand();
    manCmd.setCmdManager(new CmdManager());
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    /* Valid arg */
    manCmd.validateArgs(createArgs("cd"));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsThrowJShellArgsInvalidException()
      throws JShellArgsInvalidException {
    /* Invalid arg */
    manCmd.validateArgs(createArgs());
  }

  @Test
  public void testRunNormal() throws SecurityException,
      NoSuchFieldException, IllegalArgumentException,
      IllegalAccessException, JShellNoManualEntryException {
    CmdManager cmdManager = new CmdManager();
    Field cmdManagerMap =
        cmdManager.getClass().getDeclaredField("cmdMap");
    cmdManagerMap.setAccessible(true);
    Map<String, JShellCommand> cmdMap =
        (Map<String, JShellCommand>) cmdManagerMap.get(cmdManager);
    /* Check number of commands */
    assertEquals(15, cmdMap.keySet().size());

    /* Go through all commands and call man with */
    for (String command : cmdMap.keySet()) {
      /* Check if the manual looks good */
      assertTrue(manCmd.run(createArgs(command), fileSystem,
          fileSystem.getRootDir()).startsWith(command));
    }
  }

  @Test(expected = JShellNoManualEntryException.class)
  public void testRunNoManualEntry()
      throws JShellNoManualEntryException {
    /* No command exist */
    manCmd.run(createArgs("commandDoesNotExist"), fileSystem,
        fileSystem.getRootDir());
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }

    return result;
  }
}
