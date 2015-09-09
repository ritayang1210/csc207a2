package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import commands.CatCommand;
import commands.CdCommand;
import commands.CpCommand;
import commands.EchoCommand;
import commands.ExitCommand;
import commands.GetCommand;
import commands.GrepCommand;
import commands.HistoryCommand;
import commands.JShellCommand;
import commands.LsCommand;
import commands.ManCommand;
import commands.MkdirCommand;
import commands.MvCommand;
import commands.PopdCommand;
import commands.PushdCommand;
import commands.PwdCommand;
import driver.CmdManager;
import exceptions.JShellCommandNotFoundException;

/**
 * Integration test for CmdManager
 * 
 */
public class CmdManagerTest {

  private CmdManager cmdManager;

  @Before
  public void setup() {
    cmdManager = new CmdManager();
  }

  /*
   * Access the cmdMap of cmdManager to check that is contains the
   * right amount of commands
   */
  @Test
  public void testCommandMapSize() throws NoSuchFieldException,
      IllegalAccessException {
    Field cmdManagerMap =
        cmdManager.getClass().getDeclaredField("cmdMap");
    cmdManagerMap.setAccessible(true);
    Map<String, JShellCommand> cmdMap =
        (Map<String, JShellCommand>) cmdManagerMap.get(cmdManager);
    assertEquals(15, cmdMap.size());
  }

  /*
   * Test that cmdManager returns the correct instance of a command
   * corresponding to the given input
   */
  @Test
  public void testgetCommand() throws JShellCommandNotFoundException {
    assertTrue(cmdManager.getCommand("exit") instanceof ExitCommand);
    assertTrue(cmdManager.getCommand("mkdir") instanceof MkdirCommand);
    assertTrue(cmdManager.getCommand("cd") instanceof CdCommand);
    assertTrue(cmdManager.getCommand("ls") instanceof LsCommand);
    assertTrue(cmdManager.getCommand("pwd") instanceof PwdCommand);
    assertTrue(cmdManager.getCommand("cp") instanceof CpCommand);
    assertTrue(cmdManager.getCommand("man") instanceof ManCommand);
    assertTrue(cmdManager.getCommand("cat") instanceof CatCommand);
    assertTrue(cmdManager.getCommand("mv") instanceof MvCommand);
    assertTrue(cmdManager.getCommand("echo") instanceof EchoCommand);
    assertTrue(cmdManager.getCommand("pushd") instanceof PushdCommand);
    assertTrue(cmdManager.getCommand("popd") instanceof PopdCommand);
    assertTrue(cmdManager.getCommand("history") instanceof HistoryCommand);
    assertTrue(cmdManager.getCommand("grep") instanceof GrepCommand);
    assertTrue(cmdManager.getCommand("get") instanceof GetCommand);
  }

  /* Test that when an invalid command is given, exception is thrown */
  @Test(expected = JShellCommandNotFoundException.class)
  public void testGetCommandWithNonExistingCommand()
      throws JShellCommandNotFoundException {
    cmdManager.getCommand("quit");
  }
}
