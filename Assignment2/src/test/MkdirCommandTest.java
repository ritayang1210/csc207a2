package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.MkdirCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for MkdirCommand
 * 
 */
public class MkdirCommandTest {

  private final ByteArrayOutputStream outContent =
      new ByteArrayOutputStream();

  private FileSystem fileSystem;
  private MkdirCommand mkdirCmd;

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    mkdirCmd = new MkdirCommand();
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
    System.setOut(null);
  }

  /* helper function to create Array list of arguments */
  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    List<String> args = createArgs("dir1");
    /* Valid arg */
    mkdirCmd.validateArgs(args);
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsThrowJShellArgsInvalidException()
      throws JShellArgsInvalidException {
    List<String> args = createArgs();
    /* Invalid arg */
    mkdirCmd.validateArgs(args);
  }

  @Test
  public void testRunSingleArg() throws JShellPathInvalidException {
    List<String> args = createArgs("dir1");
    Directory rootDir = fileSystem.getRootDir();
    /* make directory dir1 in root directory */
    mkdirCmd.run(args, fileSystem, rootDir);

    /* rootDir should contains dir1 */
    assertTrue(rootDir.contains("dir1"));
  }

  @Test
  public void testRunSingleArgFileNotFound()
      throws JShellPathInvalidException {
    List<String> args = createArgs("/a/b/c");
    Directory rootDir = fileSystem.getRootDir();
    mkdirCmd.run(args, fileSystem, rootDir);

    assertFalse(rootDir.contains("a"));
    /* Verify the printed message */
    assertEquals("a: No such file or directory.\n",
        outContent.toString());
  }

  @Test
  public void testRunSingleArgPathInvalid()
      throws JShellFileNotFoundException, JShellPathInvalidException {
    List<String> args = createArgs("dir1");
    Directory rootDir = fileSystem.getRootDir();
    mkdirCmd.run(args, fileSystem, rootDir);
    Directory dir1 = (Directory) rootDir.findFile("dir1");
    /* Create new file 'file1' in dir1 */
    new File(dir1, "file1");
    List<String> arguments = createArgs("/dir1/file1/dir2");
    /*
     * run mkdir command to make directory dir2 in file1 which should
     * be invalid
     */
    mkdirCmd.run(arguments, fileSystem, rootDir);

    assertFalse(dir1.contains("dir2"));
    /*
     * Verify the printed message which shows the input path is
     * invalid
     */
    assertEquals("Path /dir1/file1/dir2 is not valid.\n",
        outContent.toString());
  }

  @Test
  public void testRunMultiArgs() throws JShellPathInvalidException {
    Directory rootDir = fileSystem.getRootDir();
    mkdirCmd.run(createArgs("dir1", "dir2", "dir3"), fileSystem,
        rootDir);

    /* rootDir should contains dir1 dir2 and dir3 */
    assertTrue(rootDir.contains("dir1"));
    assertTrue(rootDir.contains("dir2"));
    assertTrue(rootDir.contains("dir3"));
  }

  @Test
  public void testRunMultiArgsFileNotFound()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    List<String> args = createArgs("/dir1", "dir2/dir3", "./dir2");
    Directory rootDir = fileSystem.getRootDir();
    mkdirCmd.run(args, fileSystem, rootDir);

    /* rootDir should contains dir1 and dir2 */
    assertTrue(rootDir.contains("dir1"));
    assertTrue(rootDir.contains("dir2"));
    /* dir2 should not contains dir3 */
    assertFalse(((Directory) rootDir.findFile("dir2"))
        .contains("dir3"));
    /* Verify the printed message */
    assertEquals("dir2: No such file or directory.\n",
        outContent.toString());
  }

  @Test
  public void testRunMultiArgsPathInvalid()
      throws JShellFileNotFoundException, JShellPathInvalidException {
    List<String> args = createArgs("dir1");
    Directory rootDir = fileSystem.getRootDir();
    mkdirCmd.run(args, fileSystem, rootDir);
    Directory dir1 = (Directory) rootDir.findFile("dir1");
    /* create new File 'file1' in dir1 */
    new File(dir1, "file1");
    List<String> arguments =
        createArgs("dir3", "/dir1/file1/dir2", "dir4");
    mkdirCmd.run(arguments, fileSystem, rootDir);

    assertTrue(rootDir.contains("dir3"));
    assertTrue(rootDir.contains("dir4"));
    assertFalse(dir1.contains("dir2"));
    /*
     * Verify the printed message, which should shows the second
     * argument is an invalid path- file exists in the middle of path
     */
    assertEquals("Path /dir1/file1/dir2 is not valid.\n",
        outContent.toString());
  }
}
