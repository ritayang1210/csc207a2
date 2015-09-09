package commands;

import java.util.List;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class for mkdir command.
 *
 */
public class MkdirCommand extends JShellCommand {
  private static final int MIN_NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "mkdir";
  public static final String USAGE = "mkdir Dir ...";

  public MkdirCommand() {
    numOfRequiredArgs = MIN_NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  @Override
  public void validateArgs(List<String> args)
      throws JShellArgsInvalidException {
    if (args.size() < numOfRequiredArgs) {
      throw new JShellArgsInvalidException(cmdUsage);
    }
  }

  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) throws JShellPathInvalidException {
    /* Create new dir for each argument */
    for (String arguments : args) {
      List<String> parentPathAndName =
          fileSystem.getParentPathAndName(arguments);
      /* Find the path for parent dir and name of new dir */
      String parentPath = parentPathAndName.get(0);
      String nameOfNewDir = parentPathAndName.get(1);
      try {
        File parentDir =
            fileSystem.getFileGivenPath(parentPath, cwDir);
        if (!(parentDir instanceof Directory)) {
          /* If parentDir is a file, then path is invalid */
          throw new JShellPathInvalidException(arguments);
        }
        /* Create a dir */
        Directory.createRegularDir((Directory) parentDir,
            nameOfNewDir, Boolean.FALSE);
      } catch (JShellException e) {
        /*
         * If anything wrongs happens, print error message and keep
         * processing the rest args
         */
        e.printJShellErrMsg();
      }
    }
    return null;
  }
}
