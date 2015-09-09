package driver;

import java.util.List;

import exceptions.JShellException;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * The class that helps redirect the output of JShell commands
 *
 */
public class JShellRedirector {

  public static final String OPR_APPEND = ">>";
  public static final String OPR_OVERWRITE = ">";
  private static final String CHAR_NEW_LINE = "\n";

  /**
   * Redirect the source contents into target file with given path.
   * The target file will be created if not exist.
   * 
   * @param fileSystem is the file system that we are operating on
   * @param cwDir is the current working directory
   * @param source the source contents
   * @param operator indicates the type of the redirection
   * @param targetPath the path for target file
   * @throws JShellPathInvalidException when targetPath invalid
   * @throws JShellFileNotFoundException when targetPath cannot be
   *         found
   * @throws JShellIsNotFileException when target is not a file
   */
  public void redirectTo(FileSystem fileSystem, Directory cwDir,
      String source, String operator, String targetPath)
      throws JShellPathInvalidException, JShellFileNotFoundException,
      JShellIsNotFileException {
    File targetFile =
        findTargetFileOrCreateOneIfNotExist(fileSystem, cwDir,
            targetPath);
    if (targetFile instanceof Directory) {
      /* If target is directory, throw IsNotFileException */
      throw new JShellIsNotFileException(targetPath);
    }

    if (OPR_APPEND.equals(operator)) {
      /* Append new contents */
      targetFile.setContents((targetFile.getContents().isEmpty() ? ""
          : targetFile.getContents() + CHAR_NEW_LINE) + source);
    } else if (OPR_OVERWRITE.equals(operator)) {
      /* Overwrite with new contents */
      targetFile.setContents(source);
    }
  }

  /**
   * Find a file or create it given path
   * 
   * @param fileSystem is file system we are operating on
   * @param cwDir is the current working directory
   * @param targetPath is the path for target file
   * @return the target file
   * @throws JShellPathInvalidException when path is not valid
   * @throws JShellFileNotFoundException when file cannot be found
   */
  private File findTargetFileOrCreateOneIfNotExist(
      FileSystem fileSystem, Directory cwDir, String targetPath)
      throws JShellPathInvalidException, JShellFileNotFoundException {
    File targetFile;
    try {
      /* Try looking for the target file */
      targetFile = fileSystem.getFileGivenPath(targetPath, cwDir);
    } catch (JShellException e) {
      /* If targen not found, create a new file */
      List<String> parentPathAndName =
          fileSystem.getParentPathAndName(targetPath);
      String parentPath = parentPathAndName.get(0);
      String name = parentPathAndName.get(1);

      File parentDir = fileSystem.getFileGivenPath(parentPath, cwDir);
      if (!(parentDir instanceof Directory)) {
        /* If parent is a file, throw PathInvalidException */
        throw new JShellPathInvalidException(targetPath);
      }
      targetFile = new File((Directory) parentDir, name);
    }
    return targetFile;
  }
}
