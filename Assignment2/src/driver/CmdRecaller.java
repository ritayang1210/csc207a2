package driver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.JShellEventNotFoundException;

/**
 * The class used to recall previously used command in history
 *
 */
public class CmdRecaller {

  private static final String REGEX_RECALL_CMD = "^\\s*(\\!\\w+).*$";
  private static final String REGEX_RECALL = "\\!\\w+";

  /* The history recorder */
  private JShellHistoryRecorder historyRecorder;

  public CmdRecaller(JShellHistoryRecorder historyRecorder) {
    this.historyRecorder = historyRecorder;
  }

  /**
   * Retrieve the required command from history recorder.
   * 
   * @param input is the user input
   * @return the command retrieved from history recorder if required
   *         by user
   * @throws JShellEventNotFoundException if command number not exist
   *         in the history recorder
   */
  public String recallIfRequired(String input)
      throws JShellEventNotFoundException {
    Pattern pattern = Pattern.compile(REGEX_RECALL_CMD);
    Matcher matcher = pattern.matcher(input);

    if (matcher.matches()) {
      /* If matches his recall command format */
      String historyStr = matcher.group(1).substring(1);
      try {
        int historyNum = Integer.parseInt(historyStr);
        String oldCommand =
            historyRecorder.getHistoryWithNumber(historyNum);

        /* Replace with the command retrieved from history recorder */
        String newCommand =
            input.replaceFirst(REGEX_RECALL, oldCommand);
        System.out.println(newCommand);
        return newCommand;
      } catch (Exception e) {
        /* If command cannot be found */
        throw new JShellEventNotFoundException(historyStr);
      }
    }
    return input;
  }
}
