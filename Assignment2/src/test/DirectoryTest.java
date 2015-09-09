package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import fileSystem.Directory;
import fileSystem.File;

/**
 * Integration test for Directory
 * 
 */
public class DirectoryTest {
  private static final String PATH_PARENT_DIR = "..";
  private static final String PATH_SELF_DIR = ".";

  private static Directory rootDir;
  private Directory regularDir_1;
  private Directory regularDir_2;

  @Before
  public void setUp() throws JShellFileExistsException {
    rootDir = Directory.createRootDir();
    regularDir_1 =
        Directory.createRegularDir(rootDir, "regularDir_1",
            Boolean.FALSE);
    regularDir_2 =
        Directory.createRegularDir(rootDir, "regularDir_2",
            Boolean.FALSE);
  }

  @Test
  public void testIsRootDir() {
    assertTrue(rootDir.isRootDir());
    assertFalse(regularDir_1.isRootDir());
  }

  @Test
  public void testContains() {
    /* test contains */
    assertTrue(rootDir.contains("."));
    assertTrue(rootDir.contains(".."));
    assertTrue(rootDir.contains("regularDir_1"));
    assertTrue(rootDir.contains("regularDir_2"));
    assertFalse(rootDir.contains("NotExistFile"));
  }

  // setParentDir() is more than a setter, so test is necessary/
  @Test
  public void testSetParentDir() throws JShellFileNotFoundException {
    regularDir_1.setParentDir(regularDir_2);
    assertEquals(regularDir_2, regularDir_1.getParentDir());
    assertEquals(regularDir_2, regularDir_1.findFile(".."));
    assertEquals(regularDir_1, regularDir_2.findFile("regularDir_1"));
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testSetParentDirWillRemoveFileFromOldParent()
      throws JShellFileNotFoundException {
    regularDir_1.setParentDir(regularDir_2);
    /* Old dire removed */
    rootDir.findFile("regularDir_1");
  }

  @Test
  public void testAddContents() throws JShellFileNotFoundException {
    File file1 = new File(regularDir_1, "file1");
    regularDir_1.addContents(file1);

    /* Check new contents */
    assertTrue(regularDir_1.contains("file1"));
    assertSame(file1, regularDir_1.findFile("file1"));
  }

  @Test
  public void testFindFile() throws JShellFileNotFoundException {
    assertEquals(regularDir_1, rootDir.findFile("regularDir_1"));
  }

  @Test
  public void testFindFileReturnTheRightType()
      throws JShellFileNotFoundException {
    assertTrue(rootDir.findFile("regularDir_1") instanceof Directory);
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testFindFileThrowsJShellFileNotFoundException()
      throws JShellFileNotFoundException {
    /* File does not exist */
    rootDir.findFile("NonExistDir");
  }

  @Test
  public void testRemoveFile() {
    rootDir.removeFile("regularDir_1");
    /* Assert file doesn't exist */
    assertFalse(rootDir.contains("regularDir_1"));
  }

  @Test
  public void testGetContentList() {
    List<String> contents = rootDir.getContentList();
    String[] expContents =
        new String[] {".", "..", "regularDir_1", "regularDir_1"};
    assertEquals(4, contents.size());
    for (String expDir : expContents) {
      /* Check for each content */
      assertTrue(contents.contains(expDir));
    }
  }

  @Test
  public void testMakeCopyToDirectoryWithNameToDiffParent()
      throws JShellFileNotFoundException, JShellFileExistsException {
    regularDir_1.makeCopyToDirectoryWithName(regularDir_2,
        "regularDir_1_copy");
    assertIsCopyOf(regularDir_1,
        regularDir_2.findFile("regularDir_1_copy"));
  }

  @Test
  public void testMakeCopyToDirectoryWithNameToSameParent()
      throws JShellFileNotFoundException, JShellFileExistsException {
    new File(regularDir_1, "file1");
    Directory.createRegularDir(regularDir_1, "dir1", Boolean.FALSE);
    regularDir_1.makeCopyToDirectoryWithName(rootDir,
        "regularDir_1_copy");

    assertIsCopyOf(regularDir_1,
        rootDir.findFile("regularDir_1_copy"));
  }

  @Test(expected = AssertionError.class)
  public void testMakeCopyToDirectoryWithNameCopyNotModifiedWhenOrgChanged()
      throws JShellFileNotFoundException, JShellFileExistsException {
    regularDir_1.makeCopyToDirectoryWithName(rootDir,
        "regularDir_1_copy");
    new File(regularDir_1, "file2");
    /* Original dir was changed */
    assertIsCopyOf(regularDir_1,
        rootDir.findFile("regularDir_1_copy"));
  }

  @Test
  public void testMakeCopyToDirectoryWithNameReplaceExisitingFile()
      throws JShellFileNotFoundException, JShellFileExistsException {
    new File(regularDir_1, "file1");
    Directory.createRegularDir(regularDir_1, "dir1", Boolean.FALSE);

    regularDir_1.makeCopyToDirectoryWithName(rootDir, "regularDir_1");

    /* Old dir replaced */
    assertIsCopyOf(regularDir_1, rootDir.findFile("regularDir_1"));
    assertNotSame(regularDir_1, rootDir.findFile("regularDir_1"));
  }

  @Test
  public void testCreateRegularDir() throws JShellFileExistsException {
    Directory
        .createRegularDir(rootDir, "regularDir_3", Boolean.FALSE);
    assertTrue(rootDir.contains("regularDir_3"));
  }

  @Test(expected = JShellFileExistsException.class)
  public void testCreateRegularDirThrowsJShellFileExistsException()
      throws JShellFileExistsException {
    Directory
        .createRegularDir(rootDir, "regularDir_3", Boolean.FALSE);
    /*
     * Fail to create a regular dir if a dir with same name exists
     * already
     */
    Directory
        .createRegularDir(rootDir, "regularDir_3", Boolean.FALSE);
  }

  @Test
  public void testCreateRegularDirReplaceExistingDir()
      throws JShellFileExistsException {
    Directory.createRegularDir(rootDir, "regularDir_3", Boolean.TRUE);
    // To replace the original Dir while "@param replace" is set to
    // true.
    Directory.createRegularDir(rootDir, "regularDir_3", Boolean.TRUE);
  }

  /**
   * Helper function to assert that two directories/files are equal
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
