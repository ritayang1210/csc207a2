package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import driver.CmdRecaller;
import driver.JShellHistoryRecorder;
import exceptions.JShellEventNotFoundException;

public class CmdRecallerTest {

  private CmdRecaller recaller;
  private JShellHistoryRecorder historyRecorder;
  private final ByteArrayOutputStream outContent =
      new ByteArrayOutputStream();

  @Before
  public void setUp() {
    historyRecorder = new JShellHistoryRecorder();
    historyRecorder.recordCommand("command 1");
    historyRecorder.recordCommand("command 2");
    historyRecorder.recordCommand("command 3");

    recaller = new CmdRecaller(historyRecorder);

    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(null);
  }

  @Test
  public void testRecallIfRequiredWrongFormat()
      throws JShellEventNotFoundException {
    String input = "mkdir dir1 dir2 dir3";
    assertEquals(input, recaller.recallIfRequired(input));

    input = "?1";
    assertEquals(input, recaller.recallIfRequired(input));

    input = "! 1";
    assertEquals(input, recaller.recallIfRequired(input));
  }

  @Test(expected = JShellEventNotFoundException.class)
  public void testRecallIfRequiredNoEventFoundIndexZero()
      throws JShellEventNotFoundException {
    String input = "!0";
    recaller.recallIfRequired(input);
  }

  @Test(expected = JShellEventNotFoundException.class)
  public void testRecallIfRequiredNoEventFoundIndexTooBig()
      throws JShellEventNotFoundException {
    String input = "!4";
    recaller.recallIfRequired(input);
  }

  @Test(expected = JShellEventNotFoundException.class)
  public void testRecallIfRequiredNoEventFoundIndexNotNum()
      throws JShellEventNotFoundException {
    String input = "!a";
    recaller.recallIfRequired(input);
  }

  @Test
  public void testRecallIfRequired()
      throws JShellEventNotFoundException {
    String input = "!1";
    assertEquals("command 1", recaller.recallIfRequired(input));

    input = "!2";
    assertEquals("command 2", recaller.recallIfRequired(input));

    input = "!3";
    assertEquals("command 3", recaller.recallIfRequired(input));
    assertEquals("command 1\ncommand 2\ncommand 3\n",
        outContent.toString());
  }
}
