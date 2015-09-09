package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.CpCommand;
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
 * Integration test for CpCommand
 * 
 */
public class CpCommandTest {
  private static final String PATH_PARENT_DIR = "..";
  private static final String PATH_SELF_DIR = ".";

  private FileSystem fileSystem;
  private CpCommand cpCmd;

  @Before
  public void setUp() throws Exception {
    fileSystem = FileSystem.getFileSystem();
    cpCmd = new CpCommand();

    buildFileSystem();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  /**
   * To test validArgs will verify the number of arguments be exactly
   * 2.
   */
  @Test
  public void testValidArgs() throws JShellArgsInvalidException {
    /* Valid args */
    cpCmd.validateArgs(createArgs("dir1", "dir2"));
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidArgsInputInvalidArgs()
      throws JShellArgsInvalidException {
    /* Invalid args */
    cpCmd.validateArgs(createArgs("dir1", "dir2", "dir3"));
  }

  @Test
  public void testRunCpDirtoDir() throws JShellFileNotFoundException,
      JShellPathInvalidException, JShellFileExistsException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    assertTrue(fileSystem.getRootDir().contains("dir1"));
    assertTrue(fileSystem.getRootDir().contains("dir2"));

    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    Directory dir2 =
        (Directory) fileSystem.getRootDir().findFile("dir2");
    // copy dir1 to dir2
    cpCmd.run(createArgs("dir1", "dir2/"), fileSystem,
        fileSystem.getRootDir());

    assertTrue(fileSystem.getRootDir().contains("dir2"));
    // dir1 still exists in root dir.
    assertTrue(fileSystem.getRootDir().contains("dir1"));
    assertTrue(dir2.contains("dir1"));
    // assert if dir1 in root dir is the same with the dir1 in dir2.
    // Notice that
    // dir1 is a nested dir.
    assertIsCopyOf(dir1, dir2.findFile("dir1"));
  }


  @Test
  public void testRunCpFiletoDir()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    Directory dir2 =
        (Directory) fileSystem.getRootDir().findFile("dir2");
    File file1 = dir1.findFile("file1");

    cpCmd.run(createArgs("dir1/file1", "dir2"), fileSystem,
        fileSystem.getRootDir());

    /*
     * Assert the File is copied to desired target Directory, and the
     * new File is copy of original File
     */
    assertTrue(dir1.contains("file1"));
    assertTrue(dir2.contains("file1"));
    assertIsCopyOf(file1, dir2.findFile("file1"));
  }

  @Test
  public void testRunCpDirtoRootDir()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");

    cpCmd.run(createArgs("dir1/dir3", "/"), fileSystem,
        fileSystem.getRootDir());

    /*
     * Assert the Directory could be copied to rootDir correctly, and
     * the Directory under rootDir is copy of original Directory.
     */
    assertTrue(dir1.contains("dir3"));
    assertTrue(fileSystem.getRootDir().contains("dir3"));
    assertIsCopyOf(dir1.findFile("dir3"), fileSystem.getRootDir()
        .findFile("dir3"));
  }

  @Test
  public void testRunCpFiletoRootDir()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    File file1 = dir1.findFile("file1");

    cpCmd.run(createArgs("dir1/file1", "/"), fileSystem,
        fileSystem.getRootDir());

    /*
     * Assert the File exists in both original place and rootDir, and
     * the File under rootDir is the copy of original File
     */
    assertTrue(dir1.contains("file1"));
    assertTrue(fileSystem.getRootDir().contains("file1"));
    assertIsCopyOf(file1, fileSystem.getRootDir().findFile("file1"));
  }

  /**
   * This test copy a file to an existing file. The contents of the
   * target file should be the same with that of the original file
   * after copying.
   */
  @Test
  public void testRunCpFiletoFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    Directory dir2 =
        (Directory) fileSystem.getRootDir().findFile("dir2");

    File file1 = dir1.findFile("file1");

    cpCmd.run(createArgs("dir1/file1", "dir2/file2"), fileSystem,
        fileSystem.getRootDir());

    File file2 = dir2.findFile("file2");

    /*
     * Assert the target File's contents is replaced correctly, and it
     * still keeps its original name
     */
    assertTrue(dir1.contains("file1"));
    assertTrue(dir2.contains("file2"));
    assertSame(file1.getContents(), file2.getContents());
  }

  /**
   * This test copy a file to a non-existing file. This should create
   * a new file that is identical to the original file.
   */
  @Test
  public void testRunCpFiletoNonExistFile()
      throws JShellFileNotFoundException, JShellPathInvalidException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    Directory dir2 =
        (Directory) fileSystem.getRootDir().findFile("dir2");


    cpCmd.run(createArgs("dir1/file1", "dir2/file3"), fileSystem,
        fileSystem.getRootDir());

    File file1 = dir1.findFile("file1");
    File file3 = dir2.findFile("file3");

    /*
     * Assert a File that is identical to original file but named as
     * users desired was created, when copied a File to non exist
     * Directory.
     */
    assertTrue(dir1.contains("file1"));
    assertTrue(dir2.contains("file3"));
    assertSame(file1.getContents(), file3.getContents());
  }

  @Test
  public void testRunCpDirtoNonExistDir()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    cpCmd.run(createArgs("dir1", "dir3"), fileSystem,
        fileSystem.getRootDir());

    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");
    Directory dir3 =
        (Directory) fileSystem.getRootDir().findFile("dir3");

    /*
     * Assert a Dir that with user desired name and is identical to
     * original Dir was created, when a Dir is copied to non exist
     * Dir.
     */
    assertTrue(fileSystem.getRootDir().contains("dir1"));
    assertTrue(fileSystem.getRootDir().contains("dir3"));
    assertIsCopyOf(dir1, dir3);
  }

  /**
   * Copy Dir to its original parent Dir will replace the original
   * Dir.
   * 
   */
  @Test
  public void testRunCpDirtoOriginalParent()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory dir1 =
        (Directory) fileSystem.getRootDir().findFile("dir1");

    cpCmd.run(createArgs("dir1", "/"), fileSystem,
        fileSystem.getRootDir());

    Directory dir1_copy =
        (Directory) fileSystem.getRootDir().findFile("dir1");

    /* Assert the original Dir is replaced by itself */
    assertTrue(fileSystem.getRootDir().contains("dir1"));
    assertIsCopyOf(dir1, dir1_copy);
  }

  /**
   * Copy File to its original parent File will replace the original
   * File.
   * 
   */
  @Test
  public void testRunCpFiletoOriginalParent()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    File file1 =
        ((Directory) fileSystem.getRootDir().findFile("dir1"))
            .findFile("file1");

    cpCmd.run(createArgs("dir1", "/"), fileSystem,
        fileSystem.getRootDir());

    File file1_copy =
        ((Directory) fileSystem.getRootDir().findFile("dir1"))
            .findFile("file1");

    /* Assert replace correctly */
    assertTrue(fileSystem.getRootDir().contains("dir1"));
    assertIsCopyOf(file1, file1_copy);
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testRunCpNoneExistOriginalFile()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    /* Copy a non exist File */
    cpCmd.run(createArgs("dir3", "/"), fileSystem,
        fileSystem.getRootDir());
  }


  @Test(expected = JShellIsNotDirException.class)
  public void testRunCpDirectoryToFile()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    /* Copy a dir to file */
    cpCmd.run(createArgs("dir1", "dir1/file1"), fileSystem,
        fileSystem.getRootDir());;
  }

  @Test(expected = JShellMoveParentToChildException.class)
  public void testRunCpDirectoryToSubDir()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    /* Copy a dir to its child */
    cpCmd.run(createArgs("dir1", "dir1/dir3"), fileSystem,
        fileSystem.getRootDir());
  }

  @Test(expected = JShellIsNotDirException.class)
  public void testRunCpDirToDirContainsFileWithSameName()
      throws JShellFileExistsException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory.createRegularDir(fileSystem.getRootDir(), "file1",
        Boolean.FALSE);

    /* Try replace dir with file */
    assertTrue(fileSystem.getRootDir().contains("file1"));
    cpCmd.run(createArgs("file1", "dir1"), fileSystem,
        fileSystem.getRootDir());
  }

  @Test(expected = JShellIsNotDirException.class)
  public void testRunCpDirToFileContainsDirWithSameName()
      throws JShellFileExistsException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    Directory.createRegularDir(fileSystem.getRootDir(), "file1",
        Boolean.FALSE);

    assertTrue(fileSystem.getRootDir().contains("file1"));

    /* Try replace file with dir */
    cpCmd.run(createArgs("dir1/file1", "/"), fileSystem,
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

  /**
   * This method help to create arguments for CpCommand to use as an
   * input, since CpCommand only take List<String> as an input It
   * operates like a mock CommandReader.
   * 
   * @param args
   * @return Arguments to be used as input
   */
  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }

    return result;
  }


  /**
   * Helper function to assert that two directories/files are equal
   * Due to the special case of CpCommand, this method will not assert
   * the sameness of the two Files. Note: CpCommand will copy Dir/File
   * to none existing Dir/File and named as users desire.
   * 
   * @param origin is original directory/file
   * @param copy is the new copy
   * @throws JShellFileNotFoundException if file/directory not found
   */
  private void assertIsCopyOf(File origin, File copy)
      throws JShellFileNotFoundException {
    if (origin instanceof Directory && copy instanceof Directory) {
      Directory originDir = (Directory) origin;
      Directory copyDir = (Directory) copy;

      assertTrue(originDir.getContentList().equals(
          copyDir.getContentList()));

      for (String key : originDir.getContentList()) {
        if (!PATH_PARENT_DIR.equals(key)
            && !PATH_SELF_DIR.equals(key)) {
          assertIsCopyOf(originDir.findFile(key),
              copyDir.findFile(key));
        }
      }
    } else {
      assertTrue(copy.getName().startsWith(origin.getName()));
      assertEquals(origin.getContents(), copy.getContents());
    }
  }

}
