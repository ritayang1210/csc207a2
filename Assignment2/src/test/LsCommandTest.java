package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.LsCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileExistsException;
import exceptions.JShellIllegalOptionException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for LsCommand
 * 
 */
public class LsCommandTest {

  private FileSystem fileSystem;
  private LsCommand lsCmd;
  private Directory rtDir;
  private final ByteArrayOutputStream outContent =
      new ByteArrayOutputStream();

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    lsCmd = new LsCommand();
    rtDir = fileSystem.getRootDir();
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
    System.setOut(null);
  }

  /* Test that ls can take single, multiple, or no arguments */
  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    lsCmd.validateArgs(createArgs("dir1", "dir2", "dir3"));
  }

  @Test
  public void testValidateArgsNoArgument()
      throws JShellArgsInvalidException {
    lsCmd.validateArgs(new ArrayList<String>());
  }

  @Test
  public void testValidateArgsOneArgument()
      throws JShellArgsInvalidException {
    List<String> argument = createArgs("a/b/c");
    lsCmd.validateArgs(argument);
  }

  @Test
  public void testValidateArgsMultiArgument()
      throws JShellArgsInvalidException {
    List<String> argument = createArgs("a/b/c", "/d/e", "/f/g/h/i");
    lsCmd.validateArgs(argument);
  }

  /* Test the output of ls in different situations */
  @Test
  public void testRootDirContainsNothing() {
    /* Verify output of ls */
    assertEquals(
        "",
        lsCmd.run(new ArrayList<String>(), fileSystem,
            fileSystem.getRootDir()));
  }

  @Test
  public void testRootDirContainsFileAndDir()
      throws JShellFileExistsException {
    Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory.createRegularDir(rtDir, "dir2", Boolean.FALSE);
    new File(rtDir, "file1");
    /*
     * The output of ls can be on multiple lines so split outContent
     * at newline
     */
    String output =
        lsCmd.run(new ArrayList<String>(), fileSystem, rtDir);
    /*
     * Check that the size of output is as expected and each content
     * is as expected
     */
    assertEquals("dir1 dir2 file1", output);
  }

  @Test
  public void testPathSpecifiesDirectoryContainsNothing()
      throws JShellFileExistsException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    List<String> argument = createArgs("dir1/dir2");
    /* Verify output of ls */
    assertEquals("dir1/dir2:", lsCmd.run(argument, fileSystem, rtDir)
        .trim());
  }

  @Test
  public void testPathSpecifiesDirectoryContainsFileAndDir()
      throws JShellFileExistsException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    new File(dir1, "file1");
    List<String> argument = createArgs("dir1");
    /*
     * The output of ls can be on multiple lines so split outContent
     * at newline
     */
    String output = lsCmd.run(argument, fileSystem, rtDir);
    /*
     * Check that the size of output is as expected and each content
     * is as expected
     */
    assertEquals("dir1:\ndir2 file1", output);
  }

  @Test
  public void testPathSpecifiesFile()
      throws JShellFileExistsException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    new File(dir1, "file1");
    List<String> argument = createArgs("dir1/file1");
    /* Verify output of ls */
    assertEquals("dir1/file1", lsCmd.run(argument, fileSystem, rtDir)
        .trim());
  }

  @Test
  public void testMultiPaths() throws JShellFileExistsException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    new File(dir2, "file1");
    List<String> argument =
        createArgs("dir1/dir2/file1", "dir1/dir2", "dir1");
    /*
     * The output of ls can be on multiple lines so split outContent
     * at newline
     */
    String output = lsCmd.run(argument, fileSystem, rtDir);
    /*
     * Check that the size of output is as expected and each content
     * is as expected
     */

    assertEquals(
        "dir1/dir2/file1\n\ndir1/dir2:\nfile1\n\ndir1:\ndir2", output);
  }

  @Test
  public void testRunOnInvalidPath() throws JShellFileExistsException {
    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    new File(dir1, "file1");
    List<String> argument = createArgs("dir1/file1/dir2", "/");

    assertEquals("/:\ndir1", lsCmd.run(argument, fileSystem, rtDir));
    assertEquals("Path dir1/file1/dir2 is not valid.\n",
        outContent.toString());
  }

  @Test
  public void testRunNonExistingFile()
      throws JShellFileExistsException {
    Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    List<String> argument = createArgs("dir2", "dir1");

    assertEquals("dir1:", lsCmd.run(argument, fileSystem, rtDir));
    assertEquals("dir2: No such file or directory.\n",
        outContent.toString());
  }

  @Test(expected = JShellIllegalOptionException.class)
  public void testProcessOptionsIllegalOption()
      throws JShellIllegalOptionException {
    lsCmd.processOptions(createArgs("-A"));
  }

  @Test
  public void testRunRecursivelyOneArg()
      throws JShellIllegalOptionException, JShellFileExistsException {
    List<String> args = createArgs("-r", ".");

    lsCmd.processOptions(args);

    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    new File(dir2, "file1");

    assertEquals(".:\ndir1\n\n./dir1:\ndir2\n\n./dir1/dir2:\nfile1",
        lsCmd.run(args, fileSystem, rtDir));
  }

  @Test
  public void testRunRecursivelyMultiArg()
      throws JShellIllegalOptionException, JShellFileExistsException {
    List<String> args =
        createArgs("-R", "dir1/dir2/file1", "dir1/dir2", "dir1");

    lsCmd.processOptions(args);

    Directory dir1 =
        Directory.createRegularDir(rtDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    new File(dir2, "file1");

    assertEquals("dir1/dir2/file1\n\ndir1/dir2:\nfile1\n\n"
        + "dir1:\ndir2\n\ndir1/dir2:\nfile1",
        lsCmd.run(args, fileSystem, rtDir));
  }

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
}
