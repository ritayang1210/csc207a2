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

import commands.PushdCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for PushdCommand
 * 
 */
/* pushd uses notifyObserver so the test needs to implement Observer */
public class PushdCommandTest implements Observer {

  private FileSystem fileSystem;
  private PushdCommand pushdCmd;
  private Directory curDir;
  private Stack<Directory> dirStack;

  /**
   * Helper method that creates mock arguments to test pushd.
   * 
   * @param args is the mock argument for pushdt
   * @return returns a List that holds args
   */
  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();
    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

  /**
   * Override update to change the value of curDir to what pushd
   * notifies and push the current directory into the dirStack
   * 
   * @param o is the observable object, pushdCmd in this case
   * @param arg is the change that o notifies, the new curDir in this
   *        case
   */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof PushdCommand && arg instanceof Directory) {
      dirStack.push(curDir);
      curDir = (Directory) arg;
    }
  }

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    pushdCmd = new PushdCommand();
    curDir = fileSystem.getRootDir();
    dirStack = new Stack<Directory>();
    /* Adds test as an observer of pushdCmd */
    pushdCmd.addObserver(this);
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  /* Test that pushd can only take one argument */
  @Test
  public void testValidateArgument()
      throws JShellArgsInvalidException {
    List<String> arg = createArgs("dir1");
    pushdCmd.validateArgs(arg);
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgumentWithNoArgs()
      throws JShellArgsInvalidException {
    pushdCmd.validateArgs(new ArrayList<String>());
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgumentWithInvalidArgs()
      throws JShellArgsInvalidException {
    List<String> args = createArgs("dir1");
    args.add(1, "dir2");
    pushdCmd.validateArgs(args);
  }

  /* Test the effect of pushd in different situations */
  @Test
  public void testPushdOntoEmptyStack()
      throws JShellFileExistsException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellIsNotDirException {
    Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    List<String> arg = createArgs("dir1");
    pushdCmd.run(arg, fileSystem, curDir);
    /* Check that curDir was changed correctly */
    assertEquals("dir1", curDir.getName());
    /* Check that dirStack is the right size */
    assertTrue(1 == dirStack.size());
    /* Check that dirStack contains the right directory */
    assertEquals("", dirStack.peek().getName());
  }

  @Test
  public void testPushdOntoNonEmptyStack()
      throws JShellFileExistsException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellIsNotDirException {
    Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    List<String> arg = createArgs("dir1");
    dirStack.push(Directory.createRegularDir(curDir, "dir2",
        Boolean.FALSE));
    pushdCmd.run(arg, fileSystem, curDir);
    /* Check that curDir was changed correctly */
    assertEquals("dir1", curDir.getName());
    /* Check that dirStack is the right size */
    assertTrue(2 == dirStack.size());
    /* Check that the top entry in dirStack is correct */
    assertEquals("", dirStack.peek().getName());
  }

  @Test
  public void testPushdRunsMultipleTimes()
      throws JShellFileExistsException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellIsNotDirException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    List<String> arg1 = createArgs("dir1");
    pushdCmd.run(arg1, fileSystem, curDir);
    /* Include testing that a full path works */
    List<String> arg2 = createArgs("/dir1/dir2");
    pushdCmd.run(arg2, fileSystem, curDir);
    /* Include testing that .. and . works */
    List<String> arg3 = createArgs(".././../dir1");
    pushdCmd.run(arg3, fileSystem, curDir);
    /* Check that curDir was changed correctly */
    assertEquals("dir1", curDir.getName());
    /* Check that dirStack is the right size */
    assertTrue(3 == dirStack.size());
    /* Check that the entries in dirStack are in the correct order */
    assertEquals("dir2", dirStack.peek().getName());
    assertEquals("dir1", dirStack.get(1).getName());
    assertEquals("", dirStack.get(0).getName());
  }

  @Test
  public void testPushSelfAndNoChange()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotDirException {
    List<String> arg3 = createArgs(".");
    pushdCmd.run(arg3, fileSystem, curDir);
    /* Check that curDir is the same */
    assertEquals("", curDir.getName());
    /* Check that dirStack is the right size */
    assertTrue(1 == dirStack.size());
    /* Check that entry in dirStack is the root directory */
    assertEquals("", dirStack.peek().getName());
  }

  /* Test that pushd throws exceptions in the right situations */
  @Test(expected = JShellIsNotDirException.class)
  public void testChangeToFile() throws JShellFileExistsException,
      JShellIsNotDirException, JShellFileNotFoundException,
      JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    new File(dir1, "file1");
    List<String> arg = createArgs("dir1/file1");
    pushdCmd.run(arg, fileSystem, curDir);
  }

  @Test(expected = JShellPathInvalidException.class)
  public void testRunOnInvalidPath()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellIsNotDirException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    new File(dir1, "file1");
    new File(dir1, "file2");
    List<String> argument = createArgs("dir1/file1/file2");
    pushdCmd.run(argument, fileSystem, curDir);
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testRunNonExistingFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellIsNotDirException {
    new File(curDir, "file1");
    List<String> argument = createArgs("file2");
    pushdCmd.run(argument, fileSystem, curDir);
  }
}
