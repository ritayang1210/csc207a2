package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.CatCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for CatCommand
 * 
 */
public class CatCommandTest {

  private FileSystem fileSystem;
  private CatCommand catCmd;
  private Directory rtDir;

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

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    catCmd = new CatCommand();
    rtDir = fileSystem.getRootDir();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  /* Test that cat can only take one argument */
  @Test
  public void testValidateArgument()
      throws JShellArgsInvalidException {
    List<String> arg = createArgs("file1");
    catCmd.validateArgs(arg);
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgumentWithNoArgs()
      throws JShellArgsInvalidException {
    catCmd.validateArgs(new ArrayList<String>());
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgumentWithInvalidArgs()
      throws JShellArgsInvalidException {
    List<String> args = createArgs("file1");
    args.add(1, "file2");
    catCmd.validateArgs(args);
  }

  /* Test the output of cat in different situations */
  @Test
  public void testRunOnEmptyFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellIsNotFileException {
    new File(rtDir, "file1");
    List<String> arg = createArgs("file1");
    assertEquals("", catCmd.run(arg, fileSystem, rtDir).trim());
  }

  @Test
  public void testRunOnNonEmptyFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellIsNotFileException {
    File file1 = new File(rtDir, "file1");
    file1.setContents("content");
    List<String> arg = createArgs("file1");

    /* Verify output of cat */
    assertEquals("content", catCmd.run(arg, fileSystem, rtDir).trim());
  }

  @Test
  public void testRunPathSpecifiesEmptyFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellIsNotFileException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    new File(dir2, "file1");
    List<String> arg = createArgs("dir1/dir2/file1");
    /* Verify output of cat */
    assertEquals("", catCmd.run(arg, fileSystem, rtDir).trim());
  }

  @Test
  public void testRunPathSpecifiesNonEmptyFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellIsNotFileException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    File file1 = new File(dir2, "file1");
    file1.setContents("content");
    List<String> arg = createArgs("dir1/dir2/file1");
    /* Verify output of cat */
    assertEquals("content", catCmd.run(arg, fileSystem, rtDir).trim());
  }

  /* Test that ls throws exceptions in the right situations */
  @Test(expected = JShellPathInvalidException.class)
  public void testRunOnInvalidPath()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellIsNotFileException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    new File(dir1, "file1");
    new File(dir1, "file2");
    List<String> argument = createArgs("dir1/file1/file2");
    catCmd.run(argument, fileSystem, rtDir);
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testRunNonExistingFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellIsNotFileException {
    new File(rtDir, "file1");
    List<String> argument = createArgs("file2");
    catCmd.run(argument, fileSystem, rtDir);
  }

  @Test(expected = JShellIsNotFileException.class)
  public void testRunPathSpecifiesDirectory()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellIsNotFileException {
    Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    List<String> argument = createArgs("dir1");
    catCmd.run(argument, fileSystem, rtDir);
  }
}
