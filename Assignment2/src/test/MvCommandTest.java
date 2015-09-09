package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.MvCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellMoveParentToChildException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for MvCommand
 *
 */
public class MvCommandTest {

  private FileSystem fileSystem;
  private MvCommand mvCmd;

  @Before
  public void setUp() throws JShellFileExistsException {
    fileSystem = FileSystem.getFileSystem();
    mvCmd = new MvCommand();

    buildFileSystem();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    List<String> args = createArgs("dir1", "dir2");
    /* Valid arg */
    mvCmd.validateArgs(args);
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsThrowJShellArgsInvalidException()
      throws JShellArgsInvalidException {
    List<String> args = createArgs();
    /* Invalid arg */
    mvCmd.validateArgs(args);
  }

  @Test
  public void testRunMoveDir() throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    /* Move normally */
    assertTrue(fileSystem.getRootDir().contains("dir1"));
    assertTrue(fileSystem.getRootDir().contains("dir2"));

    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    Directory dir2 =
        (Directory) fileSystem.getRootDir().findFile("dir2");
    mvCmd.run(createArgs("dir1", "dir2/"), fileSystem,
        fileSystem.getRootDir());

    /* Assert mv is behaving correctly */
    assertTrue(fileSystem.getRootDir().contains("dir2"));
    assertFalse(fileSystem.getRootDir().contains("dir1"));
    assertTrue(dir2.contains("dir1"));
    assertSame(dir1, dir2.findFile("dir1"));
  }

  public void testRunMoveFile() throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory rootDir = fileSystem.getRootDir();
    Directory dir1 =
        (Directory) fileSystem.getFileGivenPath("/dir1", rootDir);
    Directory dir2 =
        (Directory) fileSystem.getFileGivenPath("dir2", rootDir);
    assertTrue(dir1.contains("file1"));
    assertTrue(dir2.contains("file2"));

    File file1 = fileSystem.getFileGivenPath("/dir1/file1", rootDir);
    mvCmd.run(createArgs("./dir1/file1", "/dir2"), fileSystem,
        rootDir);

    /* Assert mv is behaving correctly */
    assertFalse(!dir1.contains("file1"));
    assertTrue(dir2.contains("file1"));
    assertSame(file1, dir2.findFile("file1"));
  }

  @Test(expected = JShellIsNotDirException.class)
  public void testRunMoveDirToFile()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException, JShellIsNotDirException,
      JShellFileExistsException {
    Directory.createRegularDir(fileSystem.getRootDir(), "dir5",
        Boolean.FALSE);
    new File((Directory) fileSystem.getFileGivenPath("/dir1",
        fileSystem.getRootDir()), "dir5");

    /* Move into a file */
    mvCmd.run(createArgs("dir5", "/dir1/./dir5"), fileSystem,
        fileSystem.getRootDir());
  }

  @Test(expected = JShellIsNotDirException.class)
  public void testRunMoveDirToDirContainsFileWithSameName()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    new File((Directory) fileSystem.getFileGivenPath("dir2",
        fileSystem.getRootDir()), "dir1");

    /* Try replacing a file with dir */
    mvCmd.run(createArgs("dir1", "dir2"), fileSystem,
        fileSystem.getRootDir());
  }

  @Test(expected = JShellIsNotDirException.class)
  public void testRunMoveFileToDirContainsDirWithSameName()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    new File(fileSystem.getRootDir(), "dir3");

    /* Try replacing a dir with file */
    mvCmd.run(createArgs("dir3", "dir1"), fileSystem,
        fileSystem.getRootDir());
  }

  @Test
  public void testRunDirReplaceDir()
      throws JShellFileExistsException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory newDir3 =
        Directory.createRegularDir(fileSystem.getRootDir(), "dir3",
            Boolean.FALSE);
    Directory orgDir3 =
        (Directory) fileSystem.getFileGivenPath("dir1/dir3",
            fileSystem.getRootDir());

    mvCmd.run(createArgs("dir3", "/dir1"), fileSystem,
        fileSystem.getRootDir());

    /* Assert replace correctly */
    assertSame(
        newDir3,
        fileSystem.getFileGivenPath("dir1/dir3",
            fileSystem.getRootDir()));
    assertNotSame(
        orgDir3,
        fileSystem.getFileGivenPath("dir1/dir3",
            fileSystem.getRootDir()));
  }

  @Test
  public void testRunFileReplaceFile()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    File newFile1 = new File(fileSystem.getRootDir(), "file1");
    newFile1.setContents("file1");
    File orgFile1 =
        fileSystem.getFileGivenPath("../.././dir1/file1",
            fileSystem.getRootDir());

    mvCmd.run(createArgs("file1", "/dir1"), fileSystem,
        fileSystem.getRootDir());

    /* Assert replace correctly */
    assertSame(
        newFile1,
        fileSystem.getFileGivenPath("dir1/file1",
            fileSystem.getRootDir()));
    assertNotSame(
        orgFile1,
        fileSystem.getFileGivenPath("dir1/file1",
            fileSystem.getRootDir()));

    File newFile = new File(fileSystem.getRootDir(), "newFile");
    mvCmd.run(createArgs("newFile", "/dir1/file1"), fileSystem,
        fileSystem.getRootDir());

    assertSame(
        newFile,
        fileSystem.getFileGivenPath("dir1/file1",
            fileSystem.getRootDir()));
  }

  @Test
  public void testRunRenameDir() throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getFileGivenPath("/dir1",
            fileSystem.getRootDir());
    mvCmd.run(createArgs("dir1", "newDir1"), fileSystem,
        fileSystem.getRootDir());

    /* Assert rename correctly */
    assertFalse(fileSystem.getRootDir().contains("dir1"));
    assertTrue(fileSystem.getRootDir().contains("newDir1"));
    assertSame(
        dir1,
        fileSystem.getFileGivenPath("/newDir1/",
            fileSystem.getRootDir()));
  }

  @Test
  public void testRunRenameFile() throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getFileGivenPath("/dir1",
            fileSystem.getRootDir());
    File file1 =
        fileSystem.getFileGivenPath("/dir1/file1",
            fileSystem.getRootDir());
    mvCmd.run(createArgs("/dir1/file1", "/newFile1"), fileSystem,
        fileSystem.getRootDir());

    /* Assert rename correctly */
    assertFalse(dir1.contains("file1"));
    assertTrue(fileSystem.getRootDir().contains("newFile1"));
    assertSame(
        file1,
        fileSystem.getFileGivenPath("./newFile1",
            fileSystem.getRootDir()));
  }

  @Test(expected = JShellPathInvalidException.class)
  public void testRunMoveToInvalidPath()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    /* Path invalid */
    mvCmd.run(createArgs("dir1", "dir2/file2/blabla"), fileSystem,
        fileSystem.getRootDir());
  }

  @Test(expected = JShellMoveParentToChildException.class)
  public void testRunMoveDirToItsChild()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    /* Move a dir to its child */
    mvCmd.run(createArgs("dir2", "/dir2/dir3"), fileSystem,
        fileSystem.getRootDir());
  }

  private void buildFileSystem() throws JShellFileExistsException {
    Directory dir1 =
        Directory.createRegularDir(fileSystem.getRootDir(), "dir1",
            Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(fileSystem.getRootDir(), "dir2",
            Boolean.FALSE);
    Directory dir3 =
        Directory.createRegularDir(dir1, "dir3", Boolean.FALSE);
    Directory dir4 =
        Directory.createRegularDir(dir3, "dir4", Boolean.FALSE);

    new File(dir1, "file1").setContents("file1");
    new File(dir2, "file2").setContents("file2");
    new File(dir3, "file3").setContents("file3");
    new File(dir4, "file4").setContents("file4");
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }

    return result;
  }
}
