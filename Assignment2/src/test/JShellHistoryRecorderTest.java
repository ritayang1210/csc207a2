package test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import driver.JShellHistoryRecorder;

public class JShellHistoryRecorderTest {

  private JShellHistoryRecorder histRecorder;

  @Before
  public void setUp() {
    histRecorder = new JShellHistoryRecorder();
  }

  @Test
  public void testRecordCommand() {
    String record1 = "command 1";
    String record2 = "command 2";
    String record3 = "command 3";

    histRecorder.recordCommand(record1);
    histRecorder.recordCommand(record2);
    histRecorder.recordCommand(record3);

    assertEquals("1 command 1\n2 command 2\n3 command 3",
        histRecorder.getRecentHistory());
  }

  @Test
  public void testGetRecentHistory() {
    String record1 = "command 1";
    String record2 = "command 2";
    String record3 = "command 3";

    histRecorder.recordCommand(record1);
    histRecorder.recordCommand(record2);
    histRecorder.recordCommand(record3);

    assertEquals("", histRecorder.getRecentHistory(-1));
    assertEquals("", histRecorder.getRecentHistory(0));
    assertEquals("3 command 3", histRecorder.getRecentHistory(1));
    assertEquals("2 command 2\n3 command 3",
        histRecorder.getRecentHistory(2));
    assertEquals("1 command 1\n2 command 2\n3 command 3",
        histRecorder.getRecentHistory(3));
    assertEquals("1 command 1\n2 command 2\n3 command 3",
        histRecorder.getRecentHistory(100));
  }
}
