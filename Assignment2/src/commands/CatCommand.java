package commands;

import java.util.List;

import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class for cat command.
 *
 */
public class CatCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "cat";
  public static final String USAGE = "cat FILE";

  // public CatCommand() {
  // numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
  // cmdName = CMD_NAME;
  // cmdUsage = PREFIX_USAGE + USAGE;
  // }


  /**
   * Runs the cat command.
   * 
   * @param args is the list of valid arguments that the user provides
   * @param fileSystem is the singleton filesystem that cat will act
   *        on
   * @param cwDir is the current working directory
   * @throws JShellFileNotFoundException if a file or directory cannot
   *         be found given a path
   * @throws JShellPathInvalidException if the given path does not
   *         exist
   * @throws JShellIsNotFile if the target file is a Directory
   * @return the output of cat command
   */
  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) throws JShellFileNotFoundException,
      JShellPathInvalidException, JShellIsNotFileException {
    /* cat only takes one argument so extract the string from args */
    String filepath = args.get(0);
    /* find the target file using the path */
    File targetfile = fileSystem.getFileGivenPath(filepath, cwDir);
    /* check that that the file is not a directory, but a file */
    if (!(targetfile instanceof Directory)) {
      /* print the contents of the file */
      return targetfile.getContents();
    } else {
      /* if the target file is a directory, throw the exception */
      throw new JShellIsNotFileException(filepath);
    }
  }
}
