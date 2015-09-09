package commands;

import java.util.List;

import exceptions.JShellExpectedExitExceptiopn;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * The class for exit command.
 *
 */
public class ExitCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 0;
  public static final String CMD_NAME = "exit";
  public static final String USAGE = "exit";

  public ExitCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  /**
   * The method is to exit program when catching
   * JShellExpectedExitException.
   * 
   * @throws JShellExpectedExitException
   */
  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) throws JShellExpectedExitExceptiopn {
    /* Exit the program */
    throw new JShellExpectedExitExceptiopn();
  }
}
