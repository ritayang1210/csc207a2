package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.CdCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for CdCommand
 * 
 */
/* Cd uses notifyObserver so this test needs to implement Observer */
public class CdCommandTest implements Observer {

  private FileSystem fileSystem;
  private CdCommand cdCmd;
  private Directory curDir;

  /**
   * Helper method that creates mock arguments to test cat.
   * 
   * @param args is the mock argument for cat
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
   * Override update to change the value of curDir to what cd notifies
   * 
   * @param o is the observable object, cdCmd in this case
   * @param arg is the change that o notifies, the new curDir in this
   *        case
   */
  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof CdCommand && arg instanceof Directory) {
      curDir = (Directory) arg;
    }
  }

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    cdCmd = new CdCommand();
    curDir = fileSystem.getRootDir();
    /* Adds test as an observer of cdCmd */
    cdCmd.addObserver(this);
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  /* Test that cd can only take one argument */
  @Test
  public void testValidateArgument()
      throws JShellArgsInvalidException {
    List<String> arg = createArgs("file1");
    cdCmd.validateArgs(arg);
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgumentWithNoArgs()
      throws JShellArgsInvalidException {
    cdCmd.validateArgs(new ArrayList<String>());
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgumentWithInvalidArgs()
      throws JShellArgsInvalidException {
    List<String> args = createArgs("file1");
    args.add(1, "file2");
    cdCmd.validateArgs(args);
  }

  /* Test the output of cd in different situations */
  @Test
  public void testChangeFromRootToDirInRoot()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    List<String> arg = createArgs("dir1");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir1", curDir.getName());
  }

  @Test
  public void testChangeFromRootToDirUsingPath()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    Directory.createRegularDir(dir2, "dir3", Boolean.FALSE);
    List<String> arg = createArgs("dir1/dir2/dir3");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir3", curDir.getName());
  }

  @Test
  public void testChangeFromNonRootDirUsingRelativePath()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    Directory dir3 =
        Directory.createRegularDir(dir2, "dir3", Boolean.FALSE);
    Directory.createRegularDir(dir3, "dir4", Boolean.FALSE);
    curDir = dir2;
    List<String> arg = createArgs("dir3/dir4");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir4", curDir.getName());
  }

  @Test
  public void testChangeFromNonRootDirUsingRelativAbsolutePath()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    Directory dir3 =
        Directory.createRegularDir(dir2, "dir3", Boolean.FALSE);
    Directory.createRegularDir(dir3, "dir4", Boolean.FALSE);
    curDir = dir2;
    List<String> arg = createArgs("/dir1/dir2/dir3/dir4");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir4", curDir.getName());
  }

  /*
   * Test that commonly used .. and . works as previous directory and
   * current directory
   */
  @Test
  public void testChangeFromNonRootDirToPreviousDir()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    curDir = dir2;
    List<String> arg = createArgs("..");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir1", curDir.getName());
  }

  @Test
  public void testChangeFromNonRootDirToCurrentDir()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    curDir = dir1;
    List<String> arg = createArgs(".");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir1", curDir.getName());
  }

  /*
   * Test that cd works correctly when given path is made of directory
   * names and . and ..
   */
  @Test
  public void testChangeDirWithMixedPath()
      throws JShellFileExistsException, JShellIsNotDirException,
      JShellFileNotFoundException, JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    Directory dir3 =
        Directory.createRegularDir(dir2, "dir3", Boolean.FALSE);
    Directory.createRegularDir(dir3, "dir4", Boolean.FALSE);
    curDir = dir2;
    List<String> arg =
        createArgs("../../dir1/dir2/./dir3/../dir3/dir4/..");
    cdCmd.run(arg, fileSystem, curDir);
    /* Verify that curDir has been changed correctly */
    assertEquals("dir3", curDir.getName());
  }

  /* Test that cd throws exceptions in the right situations */
  @Test(expected = JShellIsNotDirException.class)
  public void testChangeToFile() throws JShellFileExistsException,
      JShellIsNotDirException, JShellFileNotFoundException,
      JShellPathInvalidException {
    Directory dir1 =
        Directory.createRegularDir(curDir, "dir1", Boolean.FALSE);
    new File(dir1, "file1");
    List<String> arg = createArgs("dir1/file1");
    cdCmd.run(arg, fileSystem, curDir);
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
    cdCmd.run(argument, fileSystem, curDir);
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testRunNonExistingFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellIsNotDirException {
    new File(curDir, "file1");
    List<String> argument = createArgs("file2");
    cdCmd.run(argument, fileSystem, curDir);
  }
}
