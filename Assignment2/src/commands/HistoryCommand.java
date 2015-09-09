package commands;


import java.util.List;

import driver.JShellHistoryRecorder;
import exceptions.JShellArgsInvalidException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * The class for history command.
 */

public class HistoryCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "history";
  public static final String USAGE = "history NUMBER";

  private JShellHistoryRecorder historyRecorder;

  public HistoryCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  /*
   * Set up HistoryRecorder from driver package
   */
  public void setHistoryRecorder(JShellHistoryRecorder historyRecorder) {
    this.historyRecorder = historyRecorder;
  }

  @Override
  public void validateArgs(List<String> args)
      throws JShellArgsInvalidException {
    if (!args.isEmpty()) {
      try {
        Integer.parseInt(args.get(0));
      } catch (Exception e) {
        /* When argument is not number */
        throw new JShellArgsInvalidException(cmdUsage);
      }
    }
    if (args.size() > numOfRequiredArgs) {
      throw new JShellArgsInvalidException(cmdUsage);
    }
  }

  /**
   * Runs the history command.
   * 
   * @param args is the list of valid arguments that the user provides
   * @param fileSystem is the file system that history will act on
   * @param cwDir is the current working directory
   */
  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) {
    if (args.isEmpty()) {
      /* Call HistoryRecaorder */
      return historyRecorder.getRecentHistory();
    } else {
      return historyRecorder.getRecentHistory(Integer.parseInt(args
          .get(0)));
    }
  }
}
