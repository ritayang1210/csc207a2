package driver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commands.GrepCommand;

/**
 * The class used to process user input.
 *
 */
public class CmdProcessor {

  private static final String CHAR_DELIMITER = "\\s";

  /**
   * Read the user input and store the command name and arguments.
   * 
   * @return true if the input is not empty. Otherwise, false.
   */
  public JShellArguments processInput(String input) {
    input = input.trim();

    if (input.isEmpty()) {
      return null;
    }

    String[] inputArray = null;
    String containedFirstStr = null;

    // in case of grep, take out the regex which is quoted.
    if (input.startsWith(GrepCommand.CMD_NAME)) {
      containedFirstStr = takeOutStr(input);
      if (!(containedFirstStr == null))
        input = input.replace(containedFirstStr, "");
    }
    inputArray = input.split(CHAR_DELIMITER);
    int length = inputArray.length;

    String cmdName = inputArray[0];
    List<String> cmdArgs = new ArrayList<String>();
    String redirectOp = null;
    String outputFile = null;
    Boolean redirectFlag = Boolean.FALSE;

    // if case of grep, add the part quoted to args.
    if (cmdName.equals(GrepCommand.CMD_NAME)
        && !(containedFirstStr == null))
      cmdArgs.add(containedFirstStr);

    for (int i = 1; i < length; i++) {
      String arg = inputArray[i];
      if (!arg.isEmpty()) {
        if (redirectFlag) {
          outputFile = arg;
          break;
        } else if (JShellRedirector.OPR_APPEND.equals(arg)
            || JShellRedirector.OPR_OVERWRITE.equals(arg)) {
          redirectOp = arg;
          redirectFlag = Boolean.TRUE;
        } else {
          cmdArgs.add(arg);
        }
      }
    }

    return new JShellArguments(cmdName, cmdArgs, redirectOp,
        outputFile, redirectFlag);
  }

  /**
   * The helper method to handle "grep" command. To take string from
   * 
   * @param args input arguments from user
   * @return the contents inside of input arguments that been quoted
   */
  private String takeOutStr(String args) {
    String containedStr = null;
    Pattern regex = Pattern.compile(".*?(\\\".*\\\").*?");
    Matcher toBeMatched = regex.matcher(args);

    // to find the first part which is quoted.
    if (toBeMatched.matches()) {
      containedStr = toBeMatched.group(1);
    }

    return containedStr;
  }
}
