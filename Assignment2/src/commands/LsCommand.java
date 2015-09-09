package commands;

import java.util.Collections;
import java.util.List;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class for ls command.
 *
 */
public class LsCommand extends JShellCommand {

  private static final String OPTION_R = "R";
  private static final int MIN_NUM_OF_ARGS = 0;
  public static final String CMD_NAME = "ls";
  public static final String USAGE = "ls [PATH...]";
  public static int counter;
  public static List<String> history;

  public LsCommand() {
    numOfRequiredArgs = MIN_NUM_OF_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;

    addOption(OPTION_R);
  }

  /**
   * Overrides the validateArgs method inherited from the superclass.
   * 
   * ls has no set amount of arguments so the superclass validateArgs
   * is not appropriate
   * 
   * @param args is the List of args to be validated
   * @throws JShellArgsInvalidException if the args is not appropriate
   */
  @Override
  public void validateArgs(List<String> args)
      throws JShellArgsInvalidException {
    /*
     * check that the amount of arguments provided is at least the
     * minimum amount necessary
     */
    if (args.size() < numOfRequiredArgs) {
      throw new JShellArgsInvalidException(cmdUsage);
    }
  }

  /**
   * helper function that process recursively ls of Directory
   * 
   * @param toBeListed is the target Directory that contents need to
   *        be listed.
   * @param path is the path of Directory being processed
   * @return resultBuilder
   */
  private StringBuilder processOptionR(Directory toBeListed,
      String path) {
    List<File> fileList = toBeListed.getFileList();
    /* Build a new string builder */
    StringBuilder resultBuilder = new StringBuilder();
    /*
     * Loops through contents of target Directory and work on each
     * File or Directory under it
     */
    for (File ele : fileList) {
      /*
       * if ele is a directory and not empty, append its path and
       * contents to resultBuilder
       */
      if (ele instanceof Directory) {
        String newPath = path + "/" + ele.getName();
        resultBuilder.append(newPath + ":\n");

        if (!((Directory) ele).getFileList().isEmpty()) {
          addContents(resultBuilder,
              ((Directory) ele).getContentList());
          resultBuilder.append("\n\n");
        }
        /*
         * if ele is an empty directory, append its path and an blank
         * line to resultBuilder
         */
        else {
          resultBuilder.append("\n");
        }
        /* Recursively call the function to append the contents */
        resultBuilder
            .append(processOptionR((Directory) ele, newPath));
      }
    }
    return resultBuilder;
  }

  /**
   * Runs the ls command.
   * 
   * @param args is the list of valid arguments that the user provides
   * @param fileSystem is the singleton file system that cat will act
   *        on
   * @param cwDir is the current working directory
   */

  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) {
    StringBuilder resultBuilder = new StringBuilder();
    if (args.isEmpty()) {
      addContents(resultBuilder, cwDir.getContentList());
      resultBuilder.append("\n\n");
      if (optionActivated(OPTION_R)) {
        resultBuilder.append(processOptionR(cwDir, "."));
      }

    }
    /* Process ls command without option r/R */
    else {
      /* Loops through args and work on each path provided */
      for (int i = 0; i < args.size(); i++) {
        String path = args.get(i);
        File toBeListed = null;
        /* Throw error messages and jump unqualified args */
        try {
          toBeListed = fileSystem.getFileGivenPath(path, cwDir);
        } catch (JShellException e) {
          e.printJShellErrMsg();
        }
        /* If the path specifies a directory, print its contents */
        if (toBeListed instanceof Directory) {
          resultBuilder.append(path + ":\n");
          List<String> contentList =
              ((Directory) toBeListed).getContentList();
          addContents(resultBuilder, contentList);
          resultBuilder.append("\n\n");
          /* process ls command with option r/R */
          if (optionActivated(OPTION_R)) {
            resultBuilder.append(processOptionR(
                (Directory) toBeListed, path));
          }
        }

        /*
         * If the path specifies a file, print the path of the file
         * and a new line
         */
        else if (toBeListed != null) {
          resultBuilder.append(path + "\n\n");
        }
      }
    }
    return resultBuilder.toString().trim();
  }

  /**
   * Add the contents of cntList
   * 
   * @param builder is a string builder to add the contents
   * @param contentList is the List of contents that a directory holds
   */
  private void addContents(StringBuilder builder,
      List<String> contentList) {
    /* Sorts contentList to make printing in alphabetical order */
    Collections.sort(contentList);
    /*
     * Loops through cntList and prints everything except the
     * directory itself and its parent directory
     */
    for (int i = 0; i < contentList.size(); i++) {
      String content = contentList.get(i);
      if (!content.equals(Directory.PATH_PARENT_DIR)
          && !content.equals(Directory.PATH_SELF_DIR)) {
        builder.append(content
            + (i == contentList.size() - 1 ? "" : " "));
      }
    }
  }
}
