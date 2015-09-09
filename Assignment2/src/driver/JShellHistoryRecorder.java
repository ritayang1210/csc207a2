package driver;

import java.util.ArrayList;
import java.util.List;

import exceptions.JShellEventNotFoundException;

/**
 * The class that stores command history
 *
 */
public class JShellHistoryRecorder {

  private static final String STRING_SPACE = " ";
  private static final String CHAR_NEWLINE = "\n";
  private static final String STRING_EMPTY = "";

  /* Array list stores the history */
  List<String> history;

  public JShellHistoryRecorder() {
    history = new ArrayList<String>();
  }

  /**
   * Add new record
   * 
   * @param command is the command to be added
   */
  public void recordCommand(String command) {
    history.add(command);
  }

  /**
   * Get complete history
   * 
   * @return the complete history in a string
   */
  public String getRecentHistory() {
    StringBuilder resultBuilder = new StringBuilder();

    for (int i = 0; i < history.size(); i++) {
      /* Add each record into result */
      resultBuilder.append((i + 1) + STRING_SPACE + history.get(i)
          + (i == history.size() - 1 ? STRING_EMPTY : CHAR_NEWLINE));
    }

    return resultBuilder.toString();
  }

  /**
   * Get most recent n history recorders
   * 
   * @param n is the number of recent history recorders asked by user
   * @return the most recent n history recorders
   */
  public String getRecentHistory(int n) {
    StringBuilder resultBuilder = new StringBuilder();

    for (int i = Math.max(0, history.size() - n); i < history.size(); i++) {
      /* Add each record into result */
      resultBuilder.append((i + 1) + STRING_SPACE + history.get(i)
          + (i == history.size() - 1 ? STRING_EMPTY : CHAR_NEWLINE));
    }

    return resultBuilder.toString();
  }

  /**
   * Get one specific history record with number
   * 
   * @param historyNum is ID of history record
   * @return the specified history record
   * @throws JShellEventNotFoundException when historyNum not valid
   */
  public String getHistoryWithNumber(int historyNum)
      throws JShellEventNotFoundException {
    try {
      return history.get(historyNum - 1);
    } catch (IndexOutOfBoundsException e) {
      /* When historyNum not valid */
      throw new JShellEventNotFoundException(
          String.valueOf(historyNum));
    }
  }
}
