package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import fileSystem.Directory;
import fileSystem.File;

/**
 * Integration test for File
 * 
 */
public class FileTest {
  Directory rootDir;
  Directory regularDir;

  File file_1;
  File file_2;

  @Before
  public void setUp() throws JShellFileExistsException {
    buildSystem();
  }

  // setParentDir() is more than a setter, so test is necessary.
  @Test
  public void testSetParentDir() throws JShellFileNotFoundException {
    assertTrue(rootDir.contains("file_1"));
    file_1.setParentDir(regularDir);

    /* Assert the results */
    assertFalse(rootDir.contains("file_1"));
    assertTrue(regularDir.contains("file_1"));
    assertSame(regularDir, file_1.getParentDir());
    assertSame(file_1, regularDir.findFile("file_1"));
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testSetParentDirWillRemoveFileFromOldParent()
      throws JShellFileNotFoundException {
    file_1.setParentDir(regularDir);
    /* Old file removed */
    rootDir.findFile("file_1");
  }

  @Test
  public void testAppendContetns() {
    /* Append contents */
    file_1.appendContents("\nThis is the line want to be appended");
    assertEquals(
        "This is file 1\nThis is the line want to be appended",
        file_1.getContents());
  }

  @Test
  public void testMakeCopyWithNameToDirectoryToDiffParent()
      throws JShellFileNotFoundException, JShellFileExistsException {
    /* Copy and rename the file */
    file_1.makeCopyToDirectoryWithName(regularDir, "file_1_copy");
    assertIsCopyOf(file_1, regularDir.findFile("file_1_copy"));
  }

  @Test
  public void testMakeCopyWithNameToDirectoryToSameParent()
      throws JShellFileNotFoundException, JShellFileExistsException {
    /* Copy and rename the file */
    file_1.makeCopyToDirectoryWithName(rootDir, "file_1_copy");
    assertIsCopyOf(file_1, rootDir.findFile("file_1_copy"));
  }

  @Test
  public void testMakeCopyWithNameReplaceExistingFileWithSameName()
      throws JShellFileNotFoundException, JShellFileExistsException {
    File file_1_copy = new File(rootDir, "file_1_copy");
    file_1_copy.setContents("This is not a copy of file1");

    file_1.makeCopyToDirectoryWithName(rootDir, "file_1_copy");

    /* Old file removed */
    assertIsCopyOf(file_1, rootDir.findFile("file_1_copy"));
  }

  private void buildSystem() throws JShellFileExistsException {
    rootDir = Directory.createRootDir();
    regularDir =
        Directory.createRegularDir(rootDir, "regularDir",
            Boolean.FALSE);

    file_1 = new File(rootDir, "file_1");

    file_1.setContents("This is file 1");
  }

  /**
   * Helper function to assert that two files are equal
   * 
   * @param origin is original file
   * @param copy is the new copy
   */
  private void assertIsCopyOf(File origin, File copy) {
    assertEquals(origin.getContents(), copy.getContents());
  }
}
