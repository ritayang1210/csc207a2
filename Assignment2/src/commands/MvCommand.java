package commands;

import java.util.List;

import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellMoveParentToChildException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class for mv command.
 *
 */
public class MvCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 2;
  public static final String CMD_NAME = "mv";
  public static final String USAGE = "mv OLDPATH NEWPATH";

  public MvCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellMoveParentToChildException,
      JShellIsNotDirException {
    String oldPath = args.get(0);
    String newPath = args.get(1);

    File oldFile = fileSystem.getFileGivenPath(oldPath, cwDir);
    try {
      File newFile = fileSystem.getFileGivenPath(newPath, cwDir);

      if (newFile instanceof Directory) {
        Directory newParentDir = (Directory) newFile;
        /* If target path is a directory, move source in */
        assertNotMoveParentToChild(fileSystem, oldFile, newParentDir);
        if (newParentDir.contains(oldFile.getName())) {
          /* When target contains file/directory with same name */
          assertSourceTargetTypeMatches(oldPath, newPath, oldFile,
              newParentDir);
        }

        oldFile.setParentDir(newParentDir);
      } else {
        if (oldFile instanceof Directory) {
          /* If moving a directory into a file */
          throw new JShellIsNotDirException(newPath);
        }
        /*
         * If both source and target are files, replace target with
         * source
         */
        moveFileToPath(fileSystem, cwDir, newPath, oldFile);
      }
    } catch (JShellFileNotFoundException e) {
      /* If target doesn't exist, move source and rename */
      moveFileToPath(fileSystem, cwDir, newPath, oldFile);
    }
    return null;
  }

  private void assertSourceTargetTypeMatches(String oldPath,
      String newPath, File oldFile, Directory newParentDir)
      throws JShellFileNotFoundException, JShellIsNotDirException {
    if (!newParentDir.findFile(oldFile.getName()).getClass()
        .equals(oldFile.getClass())) {
      /*
       * When target contains file/directory with same name but
       * different type
       */
      throw new JShellIsNotDirException(
          oldFile instanceof Directory ? newPath
              + FileSystem.DIRECTORY_SEPARTOR + oldFile.getName()
              : oldPath);
    }
  }

  /**
   * Move a file/directory to a given path
   * 
   * @param fileSystem is the file system we are operating upon
   * @param cwDir is the current working directory
   * @param newPath is the new path for the file to move
   * @param oldFile is the file to move
   * @throws JShellPathInvalidException when newPath is invalid
   * @throws JShellFileNotFoundException when newPath cannot be found
   * @throws JShellMoveParentToChildException when moving a directory
   *         into its child directory
   */
  private void moveFileToPath(FileSystem fileSystem, Directory cwDir,
      String newPath, File oldFile)
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellMoveParentToChildException {
    List<String> pathAndName =
        fileSystem.getParentPathAndName(newPath);
    String parentPath = pathAndName.get(0);
    String newName = pathAndName.get(1);

    File parentDir = fileSystem.getFileGivenPath(parentPath, cwDir);
    if (parentDir instanceof Directory) {
      /* Check if moving a directory into its child */
      assertNotMoveParentToChild(fileSystem, oldFile, parentDir);

      /* Move the original file */
      moveFileToDirWithName(oldFile, (Directory) parentDir, newName);
    } else {
      /* When new path is invalid */
      throw new JShellPathInvalidException(newPath);
    }
  }

  /**
   * Move a file/directory into another directory with a given name.
   * 
   * @param oldFile is the original file
   * @param parentDir is the which directory we want to move item into
   * @param newName is the new name for the original file
   */
  private void moveFileToDirWithName(File oldFile,
      Directory parentDir, String newName) {
    /* Remove from old parent */
    oldFile.getParentDir().removeFile(oldFile.getName());
    /* Rename */
    oldFile.setName(newName);
    /* Update to new parent */
    oldFile.setParentDir(parentDir);
  }

  /**
   * Assert not moving a directory into its child
   * 
   * @param fileSystem is file system that we are operating upon
   * @param source is the directory to be moved
   * @param target is the directory to move into
   * @throws JShellMoveParentToChildException when source is parent
   *         directory of target
   */
  private void assertNotMoveParentToChild(FileSystem fileSystem,
      File source, File target)
      throws JShellMoveParentToChildException {
    if (source instanceof Directory && target instanceof Directory) {
      /* Only check when both source and target are directories */
      if (fileSystem.isSubDir((Directory) source, (Directory) target)) {
        throw new JShellMoveParentToChildException(source.getName(),
            target.getName());
      }
    }
  }
}
