package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Integration test for FileSystem
 * 
 */
public class FileSystemTest {

  private FileSystem fileSystem;

  @Before
  public void setUp() throws JShellFileExistsException {
    fileSystem = FileSystem.getFileSystem();
    buildFileSystem();
  }

  private void buildFileSystem() throws JShellFileExistsException {
    Directory rootDir = fileSystem.getRootDir();

    Directory dir1 =
        Directory.createRegularDir(rootDir, "dir1", Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);

    new File(dir1, "file1");
    new File(dir2, "file2");
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  @Test
  public void testFileSystemRootDir()
      throws JShellFileNotFoundException {
    Directory rootDir = fileSystem.getRootDir();

    assertNotNull(rootDir);
    assertTrue(rootDir.isRootDir());
    assertEquals(3, rootDir.getContentList().size());
    assertEquals(rootDir.getName(), rootDir.findFile("..").getName());
    assertEquals(rootDir.getName(), rootDir.findFile(".").getName());
  }

  @Test
  public void testGetPathGivenDir()
      throws JShellFileNotFoundException {
    Directory rootDir = fileSystem.getRootDir();
    Directory dir1 = (Directory) rootDir.findFile("dir1");
    Directory dir2 = (Directory) dir1.findFile("dir2");

    assertEquals("/", fileSystem.getPathGivenDir(rootDir));
    assertEquals("/dir1/", fileSystem.getPathGivenDir(dir1));
    assertEquals("/dir1/dir2/", fileSystem.getPathGivenDir(dir2));

  }

  @Test
  public void testGetFileGivenPath()
      throws JShellFileNotFoundException, JShellPathInvalidException {
    Directory rootDir = fileSystem.getRootDir();
    Directory dir1 = (Directory) rootDir.findFile("dir1");
    Directory dir2 = (Directory) dir1.findFile("dir2");
    File file2 = dir2.findFile("file2");

    /* Test absolute path */
    assertSame(rootDir, fileSystem.getFileGivenPath("/", rootDir));
    assertSame(dir1, fileSystem.getFileGivenPath("/dir1/", rootDir));
    assertSame(dir2,
        fileSystem.getFileGivenPath("/dir1/dir2", rootDir));
    assertSame(file2,
        fileSystem.getFileGivenPath("/dir1/dir2/file2", rootDir));

    assertSame(rootDir, fileSystem.getFileGivenPath("/./", rootDir));
    assertSame(dir1, fileSystem.getFileGivenPath("/./dir1/", rootDir));
    assertSame(dir2,
        fileSystem.getFileGivenPath("/../dir1/dir2", rootDir));
    assertSame(file2,
        fileSystem.getFileGivenPath("/dir1/./dir2/file2", rootDir));

    /* Test relative path */
    assertSame(dir1, fileSystem.getFileGivenPath("../dir1/", rootDir));
    assertSame(dir2,
        fileSystem.getFileGivenPath("./dir1/dir2", rootDir));
    assertSame(file2,
        fileSystem.getFileGivenPath("dir1/dir2/file2", rootDir));

    assertSame(dir2, fileSystem.getFileGivenPath("./dir2", dir1));
    assertSame(file2, fileSystem.getFileGivenPath("dir2/file2", dir1));
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testGetFileGivenPathThrowJShellFileNotFoundException1()
      throws JShellFileNotFoundException, JShellPathInvalidException {
    Directory rootDir = fileSystem.getRootDir();

    /* Absolute path */
    fileSystem.getFileGivenPath("/dir3", rootDir);
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testGetFileGivenPathThrowJShellFileNotFoundException2()
      throws JShellFileNotFoundException, JShellPathInvalidException {
    Directory rootDir = fileSystem.getRootDir();

    /* Relative path */
    fileSystem.getFileGivenPath("dir1/dir2/file1", rootDir);
  }

  @Test(expected = JShellPathInvalidException.class)
  public void testGetFileGivenPathThrowJShellPathInvalidException1()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    Directory rootDir = fileSystem.getRootDir();

    /* Absolute path */
    fileSystem.getFileGivenPath("/dir1/file1/file1", rootDir);
  }

  @Test(expected = JShellPathInvalidException.class)
  public void testGetFileGivenPathThrowJShellPathInvalidException2()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    Directory rootDir = fileSystem.getRootDir();

    /* Relative path */
    fileSystem.getFileGivenPath("dir1/dir2/file2/", rootDir);
  }

  @Test
  public void testIsSubDir() throws JShellFileNotFoundException {
    Directory rootDir = fileSystem.getRootDir();
    Directory dir1 = (Directory) rootDir.findFile("dir1");
    Directory dir2 = (Directory) dir1.findFile("dir2");

    assertTrue(fileSystem.isSubDir(rootDir, dir1));
    assertTrue(fileSystem.isSubDir(rootDir, dir2));
    assertTrue(fileSystem.isSubDir(dir1, dir2));

    assertFalse(fileSystem.isSubDir(dir1, rootDir));
    assertFalse(fileSystem.isSubDir(dir2, rootDir));
    assertFalse(fileSystem.isSubDir(dir2, dir1));
  }

  @Test
  public void testGetParentPathAndName()
      throws JShellPathInvalidException {
    assertEquals("./a/b/c", fileSystem
        .getParentPathAndName("a/b/c/d").get(0));
    assertEquals("d",
        fileSystem.getParentPathAndName("a/b/c/d").get(1));

    assertEquals("/.", fileSystem.getParentPathAndName("/a").get(0));
    assertEquals("a", fileSystem.getParentPathAndName("/a").get(1));

    assertEquals("./././././a",
        fileSystem.getParentPathAndName("././././a/b").get(0));
    assertEquals("b", fileSystem.getParentPathAndName("b").get(1));
  }

  @Test(expected = JShellPathInvalidException.class)
  public void testGetParentPathAndNameInvalid()
      throws JShellPathInvalidException {
    fileSystem.getParentPathAndName("/");
  }
}
