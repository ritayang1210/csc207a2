package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import driver.JShellRedirector;
import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

public class JShellRedirectorTest {

  private JShellRedirector redirector;
  private FileSystem fileSystem;

  @Before
  public void setUp() {
    redirector = new JShellRedirector();
    fileSystem = FileSystem.getFileSystem();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  @Test(expected = JShellPathInvalidException.class)
  public void testRedirectToInvalidPath()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException {
    new File(fileSystem.getRootDir(), "file");
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "test", ">", "file/file");
  }

  @Test(expected = JShellFileNotFoundException.class)
  public void testRedirectToFileNotFound()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException {
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "test", ">", "dir/file");
  }

  @Test(expected = JShellIsNotFileException.class)
  public void testRedirectToIsNotFile()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException, JShellFileExistsException {
    Directory.createRegularDir(fileSystem.getRootDir(), "dir",
        Boolean.FALSE);
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "test", ">", "dir");
  }

  @Test
  public void testRedirectToCreateFile()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException {
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "this is file1", ">", "file1");
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "this is file2", ">>", "file2");

    assertTrue(fileSystem.getRootDir().contains("file1"));
    assertTrue(fileSystem.getRootDir().contains("file2"));

    assertEquals("this is file1",
        fileSystem.getRootDir().findFile("file1").getContents());
    assertEquals("this is file2",
        fileSystem.getRootDir().findFile("file2").getContents());
  }

  @Test
  public void testRedirectToAppend()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException {
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "this is file1", ">>", "file1");
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "this is file1", ">>", "file1");

    assertTrue(fileSystem.getRootDir().contains("file1"));

    assertEquals("this is file1\nthis is file1", fileSystem
        .getRootDir().findFile("file1").getContents());
  }

  @Test
  public void testRedirectToOverwrite()
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException {
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "this is file1", ">", "file1");
    redirector.redirectTo(fileSystem, fileSystem.getRootDir(),
        "this is file1", ">", "file1");

    assertTrue(fileSystem.getRootDir().contains("file1"));

    assertEquals("this is file1",
        fileSystem.getRootDir().findFile("file1").getContents());
  }
}
