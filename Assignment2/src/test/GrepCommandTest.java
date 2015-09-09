package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.GrepCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellException;
import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

public class GrepCommandTest {

  private FileSystem fileSystem;
  private GrepCommand grepCmd;

  /*
   * To set up the fileSystem to test GrepCommand
   */
  @Before
  public void setUp() throws Exception {
    fileSystem = FileSystem.getFileSystem();
    grepCmd = new GrepCommand();

    buildFileSystem();
  }

  /*
   * To destroy the fileSystem
   */
  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  /*
   * Test the case when user input is valid
   */
  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    grepCmd.validateArgs(createArgs("-R", "[0-9]", "dir1"));
  }

  /*
   * Test the case when user input less than required.
   */
  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsInsuficientNumArgs()
      throws JShellArgsInvalidException {
    grepCmd.validateArgs(createArgs("-R"));
  }

  /*
   * The case where user input cannot be found.
   */
  @Test(expected = JShellFileNotFoundException.class)
  public void testRunNonExistsFile() throws JShellException {
    grepCmd.run(createArgs("\"regEx\"", "file1"), fileSystem,
        fileSystem.getRootDir());
  }

  /*
   * The case to search non exists regEx in a single File.
   */
  @Test
  public void testRunSingleFileNonExistRegEx() throws JShellException {
    assertEquals("", grepCmd.run(
        createArgs("\"regEx\"", "dir1/file1"), fileSystem,
        fileSystem.getRootDir()));
  }

  /*
   * The case to search simple exists regEx in a single File.
   */
  @Test
  public void testRunSingleFileExistRegEx() throws JShellException {
    assertEquals("file1:\nline2", grepCmd.run(
        createArgs("\"[0-9]\"", "dir1/file1"), fileSystem,
        fileSystem.getRootDir()));
  }

  /*
   * The case to search simple exists regEx that match exactly one
   * line in a single File.
   */
  @Test
  public void testRunSingleFileMatchOneLine() throws JShellException {
    assertEquals("line2", grepCmd.run(
        createArgs("\"2\"", "dir1/file1"), fileSystem,
        fileSystem.getRootDir()));
  }

  /*
   * The case to search simple exists regEx in multiple Files.
   */
  @Test
  public void testRunMultiFileSearch() throws JShellException {
    assertEquals("file1:\nline2\nfile2:\nline2", grepCmd.run(
        createArgs("\"[0-9]\"", "dir1/file1", "dir2/file2"),
        fileSystem, fileSystem.getRootDir()));
  }

  /*
   * The case to search simple exists regEx in given Dirs.
   */
  @Test(expected = JShellIsNotFileException.class)
  public void testRunOnlyGivenDir() throws JShellException {
    grepCmd.run(createArgs("\"[0-9]\"", "dir1", "dir2"), fileSystem,
        fileSystem.getRootDir());
  }

  /*
   * The case to search recursively.
   */
  @Test
  public void testRunRecursiveGivenDir() throws JShellException {
    String expectedResults =
        "/dir1/file1:file1:\n" + "/dir1/file1:line2\n"
            + "/dir1/dir3/file3:file3:\n"
            + "/dir1/dir3/file3:line2\n" + "/dir1/dir3/file3:line3";
    List<String> args = createArgs("-R", "\"[a-z]\"", "dir1");
    grepCmd.processOptions(args);
    assertEquals(expectedResults,
        grepCmd.run(args, fileSystem, fileSystem.getRootDir()));
  }

  /*
   * The case to search recursively.
   */
  @Test
  public void testRunRecursiveAnyChar() throws JShellException {
    String expectedResults =
        "/dir1/file1:file1:\n" + "/dir1/file1:line2\n"
            + "/dir1/dir3/file3:file3:\n"
            + "/dir1/dir3/file3:line2\n" + "/dir1/dir3/file3:line3";
    List<String> args = createArgs("-R", "\".*\"", "dir1");
    grepCmd.processOptions(args);
    assertEquals(expectedResults,
        grepCmd.run(args, fileSystem, fileSystem.getRootDir()));
  }


  private void buildFileSystem() throws JShellFileExistsException {
    // /dir1
    Directory dir1 =
        Directory.createRegularDir(fileSystem.getRootDir(), "dir1",
            Boolean.FALSE);
    // /dir2
    Directory dir2 =
        Directory.createRegularDir(fileSystem.getRootDir(), "dir2",
            Boolean.FALSE);
    // /dir1/dir3
    Directory dir3 =
        Directory.createRegularDir(dir1, "dir3", Boolean.FALSE);

    // /dir1/file1
    new File(dir1, "file1").setContents("file1:\nline2");
    // /dir2/file2
    new File(dir2, "file2").setContents("file2:\nline2");
    // /dir1/dir3/file3
    new File(dir3, "file3").setContents("file3:\nline2\nline3");
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }

    return result;
  }

}
