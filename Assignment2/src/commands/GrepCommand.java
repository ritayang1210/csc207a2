package commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * GrepCommand will do global search for a regular expression give by
 * user.
 * 
 * GrepCommand is a subclass of JShellCommand class. It will be used
 * by the format "grep [-R] REGEX PATH...". [-R] is an optional
 * argument which indicates to search recursively. GrepCommand could
 * also be given multiple paths, so that it will search in the paths
 * given by user.
 * 
 * Logic: To import util.regex, and to use users input to form a
 * pattern, and then to create a matcher of contents of each file
 * under the directory. Need to implement a method to search a regex
 * in a file, a method to search regex under current(single) Dir, and
 * finally a method to search recursively.
 */
public class GrepCommand extends JShellCommand {
  private static final String PREFIX_OPTION = "-";
  private static final int NUM_OF_REQUIRED_ARGS = 2;
  public static final String CMD_NAME = "grep";
  public static final String USAGE = "grep [-R] REGEX PATH...";

  private static final String OPTION_R = "R";
  private static final String NEW_LINE = "\n";
  private static final String REGEX_ANY_CHAR = ".*";

  public GrepCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;

    addOption(OPTION_R);
  }

  @Override
  public void validateArgs(List<String> args)
      throws JShellArgsInvalidException {
    if (args.size() < numOfRequiredArgs) {
      throw new JShellArgsInvalidException(cmdUsage);
    }
    if (args.get(1).startsWith(PREFIX_OPTION)) {
      if (args.size() < numOfRequiredArgs + 1) {
        throw new JShellArgsInvalidException(cmdUsage);
      }
    }
  }

  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) throws JShellIsNotFileException,
      JShellPathInvalidException, JShellFileNotFoundException,
      JShellArgsInvalidException {
    StringBuilder toBeReturned = new StringBuilder();
    String regEx = args.get(0);

    // To remove regEx from the original input, so there are only
    // PATHs left.
    args.remove(0);

    if (regEx.startsWith("\"") && regEx.endsWith("\"")) {
      regEx = regEx.substring(1, regEx.length() - 1);
    } else {
      throw new JShellArgsInvalidException(cmdUsage);
    }

    // Loop through all PATHs given
    for (int i = 0; i < args.size(); i++) {
      String path = args.get(i);
      File toBeSearched = fileSystem.getFileGivenPath(path, cwDir);

      // The case of the PATH given is a File.
      if (!(toBeSearched instanceof Directory)) {
        toBeReturned.append(fileSearch(toBeSearched, regEx,
            optionActivated(OPTION_R), fileSystem));
      }
      // The case of the PATH given is a Dir.
      else {
        if (optionActivated(OPTION_R)) {
          toBeReturned.append(recursiveDirSearch(
              (Directory) toBeSearched, regEx, fileSystem));
        } else {
          throw new JShellIsNotFileException(path);
        }
      }
    }

    // To delete the extra new line character at the end.
    if (toBeReturned.toString().endsWith(NEW_LINE))
      toBeReturned.deleteCharAt(toBeReturned.length() - 1);

    // to reset the option
    addOption(OPTION_R);
    return toBeReturned.toString();
  }

  /**
   * This method only search the contents of File under single
   * Directory.
   * 
   * @param cwDir The Dir that will be searched
   * @param regEx The regEx given by user
   * @param recursive Whether this function is used for recursive
   *        purpose.
   * @return Any lines that contain regEx under the cwDir.
   * @throws JShellFileNotFoundException
   */
  private String singleDirSearch(Directory cwDir, String regEx,
      Boolean recursive, FileSystem fileSystem)
      throws JShellFileNotFoundException {
    StringBuilder toBeReturned = new StringBuilder();
    List<String> contentList = cwDir.getContentsNoRtSf();

    // A loop to iterate through cwDir
    for (String fileName : contentList) {
      // Only search File type
      if (!(cwDir.findFile(fileName) instanceof Directory)) {
        toBeReturned.append(fileSearch(cwDir.findFile(fileName),
            regEx, recursive, fileSystem));
      }
    }
    return toBeReturned.toString();
  }

  /**
   * To search for the lines corresponds to regEx recursively for a
   * given Directory.
   * 
   * @param cwDir Dir where begin to search
   * @param regEx The regular expression we are searching for
   * @param fileSystem singleton filSsystem passed for future use.
   * @return String of the path to the all files containing regEx
   *         (including the filename), then a colon, then the line
   *         that contained regEx.
   * @throws JShellFileNotFoundException
   */
  private String recursiveDirSearch(Directory cwDir, String regEx,
      FileSystem fileSystem) throws JShellFileNotFoundException {
    StringBuilder toBeReturned = new StringBuilder();
    List<String> contentList = cwDir.getContentsNoRtSf();

    // To search the current Dir first.
    toBeReturned.append(singleDirSearch(cwDir, regEx, Boolean.TRUE,
        fileSystem));

    // Search under the sub Dir.
    for (String fileName : contentList) {
      if (cwDir.findFile(fileName) instanceof Directory) {
        Directory nxtDir = (Directory) cwDir.findFile(fileName);
        toBeReturned.append(recursiveDirSearch(nxtDir, regEx,
            fileSystem));
      }
    }
    return toBeReturned.toString();
  }

  /**
   * To search for a given regular expression in a File type.
   * 
   * @param file
   * @param regEx regular expression given by user.
   * @param recursive whether this function is used for recursive
   *        purpose.
   * @return
   */
  private String fileSearch(File file, String regEx,
      Boolean recursive, FileSystem fileSystem) {
    String contents = file.getContents();
    Pattern target =
        Pattern.compile(REGEX_ANY_CHAR + regEx + REGEX_ANY_CHAR);
    Matcher toBeMatched = target.matcher(contents);
    StringBuilder toBeReturned = new StringBuilder();
    // path of current File
    String path =
        fileSystem.getPathGivenDir(file.getParentDir())
            + file.getName();

    while (toBeMatched.find()) {
      String toBeAppended =
          contents.substring(toBeMatched.start(), toBeMatched.end());
      if (toBeAppended.isEmpty())
        continue;

      // to check whether contents already exist in the StringBuilder
      // If there are contents already, add a new line character
      if (!toBeReturned.toString().isEmpty())
        toBeReturned.append(NEW_LINE);

      if (recursive)
        toBeReturned.append(path + ":");

      toBeReturned.append(toBeAppended);
    }
    if (!toBeReturned.toString().isEmpty())
      toBeReturned.append("\n");
    return toBeReturned.toString();
  }
}
