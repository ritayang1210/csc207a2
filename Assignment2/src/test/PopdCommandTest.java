package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.PopdCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellDirStackEmptyException;
import exceptions.JShellFileExistsException;
import fileSystem.Directory;
import fileSystem.FileSystem;

public class PopdCommandTest implements Observer {

  private FileSystem fileSystem;
  private PopdCommand popdCmd;
  private Directory curDir;
  private Stack<Directory> dirStack;

  /**
   * Helper method that creates mock arguments to test popd.
   * 
   * @param args is the mock argument for popd
   * @return returns a List that holds args
   */
  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();
    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof PopdCommand) {
      curDir = dirStack.pop();
    }
  }

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    popdCmd = new PopdCommand();
    curDir = fileSystem.getRootDir();
    dirStack = new Stack<Directory>();
    /* Adds test as an observer of popdCmd */
    popdCmd.addObserver(this);
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  /* Test that popd takes no argument */
  @Test
  public void testValidateArg() throws JShellArgsInvalidException {
    popdCmd.validateArgs(new ArrayList<String>());
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgInvalidArgument()
      throws JShellArgsInvalidException {
    List<String> args = createArgs("pop");
    popdCmd.validateArgs(args);
  }

  @Test
  public void testRunDirStackOneEntry()
      throws JShellFileExistsException, JShellDirStackEmptyException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    dirStack.push(dir1);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir has been changed correctly */
    assertEquals("dir1", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.isEmpty());
  }

  @Test
  public void testRunDirStackMultiEntry()
      throws JShellFileExistsException, JShellDirStackEmptyException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(curDir, "dir2", Boolean.FALSE);
    dirStack.push(dir1);
    dirStack.push(dir2);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir has been changed correctly */
    assertEquals("dir2", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.size() == 1);
  }

  @Test
  public void testDirStackEntriesParentNotRoot()
      throws JShellFileExistsException, JShellDirStackEmptyException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    dirStack.push(dir2);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir has been changed correctly */
    assertEquals("dir2", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.isEmpty());
  }

  @Test
  public void testRunMultipleTimes()
      throws JShellFileExistsException, JShellDirStackEmptyException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    Directory dir3 =
        Directory.createRegularDir(dir2, "dir3", Boolean.FALSE);
    dirStack.push(dir3);
    dirStack.push(dir1);
    dirStack.push(dir2);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir has been changed correctly */
    assertEquals("dir2", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.size() == 2);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir has been changed correctly */
    assertEquals("dir1", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.size() == 1);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir has been changed correctly */
    assertEquals("dir3", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.isEmpty());
  }

  @Test
  public void testPopSelf() throws JShellDirStackEmptyException {
    dirStack.push(curDir);
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
    /* Check that curDir is the same */
    assertEquals("", curDir.getName());
    /* Check that size of dirStack is correct */
    assertTrue(dirStack.isEmpty());
  }

  @Test(expected = JShellDirStackEmptyException.class)
  public void testPopEmptyDir() throws JShellDirStackEmptyException {
    popdCmd.run(new ArrayList<String>(), fileSystem, curDir);
  }
}
