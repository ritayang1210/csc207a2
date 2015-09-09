package test;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.GetCommand;
import exceptions.JShellException;
import exceptions.JShellURLInvalidException;
import fileSystem.FileSystem;

public class GetCommandTest {
  private FileSystem fileSystem;
  private GetCommand getCmd;

  @Before
  public void setUp() throws Exception {
    fileSystem = FileSystem.getFileSystem();
    getCmd = new GetCommand();
  }

  @After
  public void tearDown() throws Exception {
    FileSystem.destroySingletonObj();
  }

  /**
   * The base case of test01.txt and has contents Hello World!
   * 
   * @throws JShellException
   */
  @Test
  public void testRunExampleURL1() throws JShellException {
    String url = "http://individual.utoronto.ca/mmina/a2b/test01.txt";
    String expName = "test01.txt";
    String expContents = "Hello World!";
    getCmd.run(createArgs(url), fileSystem, fileSystem.getRootDir());

    // assert if the file exists
    assertTrue(fileSystem.getRootDir().contains(expName));

    // assert if the contents of file matches our expectation
    assertEquals(expContents,
        fileSystem.getRootDir().findFile(expName).getContents());
  }

  /**
   * The base case 2
   * 
   * @throws JShellException
   */
  @Test
  public void testRunExampleURL2() throws JShellException {
    String url =
        "http://individual.utoronto.ca/mmina/a2b/test02.html";
    String expName = "test02.html";
    String expContents =
        "<!DOCTYPE html><HTML><HEAD><TITLE>HTML Hello</TITLE></HEAD><BODY><H1>"
            + "Hello World</H1></BODY></HTML>";
    getCmd.run(createArgs(url), fileSystem, fileSystem.getRootDir());

    // assert if the file exists
    assertTrue(fileSystem.getRootDir().contains(expName));

    // assert if the contents of file matches our expectation
    assertEquals(expContents,
        fileSystem.getRootDir().findFile(expName).getContents());
  }

  /**
   * The base case 3
   * 
   * @throws JShellException
   */
  @Test
  public void testRunExampleURL3() throws JShellException {
    String url = "http://individual.utoronto.ca/mmina/a2b/test03.txt";
    String expName = "test03.txt";
    String expContents = "1\n2\n3\n4\n5";
    getCmd.run(createArgs(url), fileSystem, fileSystem.getRootDir());

    // assert if the file exists
    assertTrue(fileSystem.getRootDir().contains(expName));

    // assert if the contents of file matches our expectation
    assertEquals(expContents,
        fileSystem.getRootDir().findFile(expName).getContents());
  }

  /**
   * The case that give URL is invalid
   * 
   * @throws JShellException
   */
  @Test(expected = JShellURLInvalidException.class)
  public void testRunInvalidURL() throws JShellException {
    String url = "non.exist.com";
    getCmd.run(createArgs(url), fileSystem, fileSystem.getRootDir());
  }

  private List<String> createArgs(String... args) {
    List<String> result = new ArrayList<String>();

    for (String arg : args) {
      result.add(arg);
    }

    return result;
  }
}
