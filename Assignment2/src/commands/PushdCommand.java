package commands;

import java.util.List;

import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class for pushd command.
 *
 */
public class PushdCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "pushd";
  public static final String USAGE = "pushd DIR";

  public PushdCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }


  /**
   * Runs the pushd command.
   * 
   * @param args is the list of valid arguments that the user provides
   * @param fileSystem is the singleton filesystem that pushd will act
   *        on
   * @param cwDir is the current working directory
   */
  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellIsNotDirException {
    /* Extract the path from args */
    String path = args.get(0);
    /* Finds the target file using the path */
    File newDir = fileSystem.getFileGivenPath(path, cwDir);

    /* Check that the file is a directory */
    if (newDir instanceof Directory) {
      /* Notify JShell of the new directory */
      setChanged();
      notifyObservers(newDir);
    } else {
      /* Throw exception is the target file is not a directory */
      throw new JShellIsNotDirException(path);
    }
    return null;
  }
}
