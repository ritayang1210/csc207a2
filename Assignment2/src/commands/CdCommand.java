package commands;

import java.util.List;

import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class for cd command.
 *
 */
public class CdCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "cd";
  public static final String USAGE = "cd Dir";

  public CdCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  /**
   * Runs the cd command.
   * 
   * @param args is the list of valid arguments that the user provides
   * @param fileSystem is the singleton filesystem that cd will act on
   * @param cwDir is the current working directory
   * @throws JShellFileNotFoundException if a file or directory cannot
   *         be found given a path
   * @throws JShellPathInvalidException if the given path does not
   *         exist
   * @throws JShellIsNotDir if the target file is not a Directory
   */
  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellIsNotDirException {
    /* cd only takes one argument so extract the string from args */
    String newCwDirPath = args.get(0);
    /* find the target file using the path */
    File newCwDir = fileSystem.getFileGivenPath(newCwDirPath, cwDir);

    /*
     * If given path specifies a directory, notify JShell to update
     * cwDir
     */
    if (newCwDir instanceof Directory) {
      setChanged();
      notifyObservers(newCwDir);
    } else {
      /* if given path specifies a file, throw the exception */
      throw new JShellIsNotDirException(newCwDirPath);
    }
    return null;
  }
}
