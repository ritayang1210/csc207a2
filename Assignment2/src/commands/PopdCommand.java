package commands;

import java.util.EmptyStackException;
import java.util.List;

import exceptions.JShellDirStackEmptyException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * The class for popd command.
 *
 */
public class PopdCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 0;
  public static final String CMD_NAME = "popd";
  public static final String USAGE = "popd";

  public PopdCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }


  /**
   * Runs the popd command.
   * 
   * @param args is the list of valid arguments that the user provides
   * @param fileSystem is the singleton filesystem that popd will act
   *        on
   * @param cwDir is the current working directory
   */
  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) throws JShellDirStackEmptyException {
    try {
      /*
       * Notify JShell to pop and cd into the top directory in
       * dirStack
       */
      setChanged();
      notifyObservers();
    } catch (EmptyStackException e) {
      /* Throw exception if dirStack is empty */
      throw new JShellDirStackEmptyException();
    }

    return null;
  }
}
