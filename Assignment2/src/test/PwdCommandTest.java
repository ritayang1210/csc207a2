package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.PwdCommand;
import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileExistsException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * Integration test for PwdCommand
 * 
 */
public class PwdCommandTest {

  private FileSystem fileSystem;
  private PwdCommand pwdCommand;

  @Before
  public void setUp() {
    fileSystem = FileSystem.getFileSystem();
    pwdCommand = new PwdCommand();
  }

  @After
  public void tearDown() {
    FileSystem.destroySingletonObj();
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

  @Test
  public void testValidateArgs() throws JShellArgsInvalidException {
    List<String> args = createArgs();
    /* Test when no arguments */
    pwdCommand.validateArgs(args);
  }

  @Test(expected = JShellArgsInvalidException.class)
  public void testValidateArgsThrowJShellArgsInvalidException()
      throws JShellArgsInvalidException {
    List<String> args = createArgs("dsd");
    /* Test if arguments exist */
    pwdCommand.validateArgs(args);
  }

  @Test
  public void testRunRootDir() {
    /* Verify the printed message */
    assertEquals("/", pwdCommand.run(new ArrayList<String>(),
        fileSystem, fileSystem.getRootDir()));
  }

  @Test
  public void testRunNonRootDir() throws JShellFileExistsException {
    /* Create three directories under different directories */
    Directory dir1 =
        Directory.createRegularDir(fileSystem.getRootDir(), "dir1",
            Boolean.FALSE);
    Directory dir2 =
        Directory.createRegularDir(dir1, "dir2", Boolean.FALSE);
    Directory dir3 =
        Directory.createRegularDir(dir2, "dir3", Boolean.FALSE);

    /* Verify the printed message */
    assertEquals("/dir1/dir2/dir3/",
        pwdCommand.run(new ArrayList<String>(), fileSystem, dir3));
  }
}
