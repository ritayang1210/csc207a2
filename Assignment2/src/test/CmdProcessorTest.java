package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import driver.CmdProcessor;
import driver.JShellArguments;

/**
 * Integration test for CmdReader
 * 
 */
public class CmdProcessorTest {

  private CmdProcessor cmdProcessor;

  @Before
  public void setUp() {
    cmdProcessor = new CmdProcessor();
  }

  @Test
  public void testReadInputNormal() {
    JShellArguments jShellArgs =
        cmdProcessor.processInput("mkdir a b\n");
    /* Test whether the command name is 'mkdir' */
    assertEquals("mkdir", jShellArgs.getCommandName());
    /*
     * a, b\n are two arguments, test whether the size of arguments is
     * two
     */
    assertEquals(2, jShellArgs.getArguments().size());
    /* Test whether the first argument is 'a' */
    assertEquals("a", jShellArgs.getArguments().get(0));
    /* Test whether the second argument is 'b' */
    assertEquals("b", jShellArgs.getArguments().get(1));
    /* Assert redirect flag is off */
    assertFalse(jShellArgs.ifRedirect());
  }

  @Test
  public void testReadInputWithTabsAndSpaces() {
    JShellArguments jShellArgs =
        cmdProcessor
            .processInput("echo        121    >   \t\t\t\tb.txt\n");
    /* Test whether the command name is 'echo' */
    assertEquals("echo", jShellArgs.getCommandName());
    /* Test whether the size of arguments is 3 */
    assertEquals(1, jShellArgs.getArguments().size());
    /* Test whether the first argument is '121' */
    assertEquals("121", jShellArgs.getArguments().get(0));
    /* Test whether the redirect op is '>' */
    assertEquals(">", jShellArgs.getRedirectOp());
    /* Test whether the output file is 'b' */
    assertEquals("b.txt", jShellArgs.getOutputFile());
    /* Assert redirect flag is on */
    assertTrue(jShellArgs.ifRedirect());
  }

  @Test
  public void testReadInputWithTrailingSpaces() {
    JShellArguments jShellArgs =
        cmdProcessor
            .processInput("        cd    \t\t    root    \t\t\n");
    /* Test whether the command name id 'cd' */
    assertEquals("cd", jShellArgs.getCommandName());
    /*
     * 'root' is the argument, test whether the the size of argument
     * is 1
     */
    assertEquals(1, jShellArgs.getArguments().size());
    /* Test whether the first argument is 'root' */
    assertEquals("root", jShellArgs.getArguments().get(0));
  }

  @Test
  public void testReadInputWithEmptyInput() {
    cmdProcessor = new CmdProcessor();
    /* Test whether return False when the input is empty */
    assertNull(cmdProcessor.processInput("\n"));

    /*
     * Test whether return False if the input only contains tab, space
     * new line character
     */
    assertNull(cmdProcessor.processInput("\t\t\t      \t\t   \n"));
  }
}
