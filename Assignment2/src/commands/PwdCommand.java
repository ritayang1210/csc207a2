package commands;

import java.util.List;

import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * The class for pwd command.
 *
 */
public class PwdCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 0;
  public static final String CMD_NAME = "pwd";
  public static final String USAGE = "pwd";

  public PwdCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }


  /**
   * The method is to print the path of current working directory.
   */
  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) {
    /* Find current path using method getPathGivenDir in fileSystem */
    String currentPath = fileSystem.getPathGivenDir(cwDir);

    return currentPath;
  }
}
