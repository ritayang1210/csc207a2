package commands;

import java.util.List;

import exceptions.JShellArgsInvalidException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * The class for echo command
 *
 */
public class EchoCommand extends JShellCommand {

  private static final String DOUBLE_QUOTATION_MARK = "\"";
  private static final int MIN_NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "echo";
  public static final String USAGE = "echo \"STRING\" [>/>> OUTFILE]";

  public EchoCommand() {
    numOfRequiredArgs = MIN_NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  @Override
  public void validateArgs(List<String> args)
      throws JShellArgsInvalidException {
    int size = args.size();

    if (args.isEmpty()
        || !args.get(0).startsWith(DOUBLE_QUOTATION_MARK)
        || !args.get(size - 1).endsWith(DOUBLE_QUOTATION_MARK)) {
      throw new JShellArgsInvalidException(cmdUsage);
    }
  }

  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) {
    return buildContents(args);
  }

  /**
   * Build the contents to be added to file from the arguments
   * 
   * @param args that user types in
   * @return the contents
   */
  private String buildContents(List<String> args) {
    StringBuilder strBuilder = new StringBuilder("");

    for (int i = 0; i < args.size(); i++) {
      String toAppend = args.get(i);
      if (i == 0) {
        /* Remove double quotation */
        toAppend = toAppend.substring(1);
      }
      if (i == args.size() - 1) {
        /* Remove double quotation */
        toAppend = toAppend.substring(0, toAppend.length() - 1);
      }

      strBuilder.append(toAppend + (i == args.size() - 1 ? "" : " "));
    }

    return strBuilder.toString();
  }
}
