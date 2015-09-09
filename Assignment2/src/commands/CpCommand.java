package commands;


import java.util.List;

import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotDirException;
import exceptions.JShellMoveParentToChildException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * This class will run command cp.
 * 
 * The command cp will recursively copy the given File or Directory to
 * the target Dir. When the target directory dosen's exists, the
 * program will create the directory. When the File/Dir is copied to
 * its original parent, the original File/Dir will be replaced.
 * 
 * This is written by Rita
 *
 */
public class CpCommand extends JShellCommand {
  private static final int NUM_OF_REQUIRED_ARGS = 2;
  public static final String CMD_NAME = "cp";
  public static final String USAGE = "cp File Dir";

  public CpCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  /**
   * To copy a File to target Directory. The File could be either File
   * or Directory.
   * 
   */
  @Override
  public String run(List<String> args, final FileSystem fileSystem,
      final Directory cwDir) throws JShellPathInvalidException,
      JShellFileNotFoundException, JShellFileExistsException,
      JShellMoveParentToChildException, JShellIsNotDirException {
    String originFilePath = args.get(0);
    String targetFilePath = args.get(1);
    File originFile =
        fileSystem.getFileGivenPath(originFilePath, cwDir);

    try {
      // Try to get target file and identify whether it is a File or
      // Directory
      File targetFile =
          fileSystem.getFileGivenPath(targetFilePath, cwDir);

      if (targetFile instanceof Directory) {
        // To assert original file is not the parent of the target
        // file.
        assertNotMoveParentToChild(fileSystem, originFile, targetFile);
        Directory newParentDir = (Directory) targetFile;

        // When target contains file/directory with same name
        if (newParentDir.contains(originFile.getName())) {
          /*
           * When target contains file/directory with same name but
           * different type
           */
          assertSourceTargetTypeMatches(originFilePath,
              targetFilePath, originFile, newParentDir);
        }

        originFile.makeCopyToDirectoryWithName(
            (Directory) targetFile, originFile.getName());
      }
      // The case of target file is a File.
      else {
        // If moving a directory into a file
        if (originFile instanceof Directory) {
          throw new JShellIsNotDirException(targetFilePath);
        }
        // Replace target file when both are files
        copyFileToPath(fileSystem, cwDir, targetFilePath, originFile);
      }
    } catch (JShellFileNotFoundException e) {
      // If targetFile doesn't exist, create a new one as copy
      copyFileToPath(fileSystem, cwDir, targetFilePath, originFile);
    }
    return null;
  }

  private void assertSourceTargetTypeMatches(String originFilePath,
      String targetFilePath, File originFile, Directory newParentDir)
      throws JShellFileNotFoundException, JShellIsNotDirException {
    if (!newParentDir.findFile(originFile.getName()).getClass()
        .equals(originFile.getClass())) {
      /*
       * If source is dir but target is file or source is file and
       * target is dir
       */
      throw new JShellIsNotDirException(
          originFile instanceof Directory ? targetFilePath
              + FileSystem.DIRECTORY_SEPARTOR + originFile.getName()
              : originFilePath);
    }
  }

  /**
   * Copy a given file/directory to a path
   * 
   * @param fileSystem is the file system we are operating upon
   * @param cwDir is the current working directory
   * @param targetFilePath is the path we want to copy to
   * @param originFile is the given file/directory
   * @throws JShellPathInvalidException when targetFilePath is invalid
   * @throws JShellFileNotFoundException when parentDir not found
   * @throws JShellFileExistsException
   * @throws JShellMoveParentToChildException when copy a directory
   *         into its child
   */
  private void copyFileToPath(final FileSystem fileSystem,
      final Directory cwDir, String targetFilePath, File originFile)
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellFileExistsException, JShellMoveParentToChildException {
    List<String> parentPathAndName =
        fileSystem.getParentPathAndName(targetFilePath);
    /*
     * Figure out the path for the parent dir and name for new
     * dir/file
     */
    String parentPath = parentPathAndName.get(0);
    String name = parentPathAndName.get(1);

    /* Find the parent dir */
    File parentDir = fileSystem.getFileGivenPath(parentPath, cwDir);
    if (!(parentDir instanceof Directory)) {
      /* If parent is not dir, path invalid */
      throw new JShellPathInvalidException(targetFilePath);
    }
    /* Check not moving a dir to its child */
    assertNotMoveParentToChild(fileSystem, originFile, parentDir);

    /* Copy the file/dir */
    originFile.makeCopyToDirectoryWithName((Directory) parentDir,
        name);
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
      if (fileSystem.isSubDir((Directory) source, (Directory) target)) {
        throw new JShellMoveParentToChildException(source.getName(),
            target.getName());
      }
    }
  }
}
