package fileSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.JShellFileNotFoundException;
import exceptions.JShellPathInvalidException;

/**
 * This class initiates and maintains a constant root directory and
 * process files and directories in the system
 * 
 * @author yangran
 *
 */
public class FileSystem {

  private static final String PATTERN_PATH = "^(.+)/(.+)$";
  public static final String DIRECTORY_SEPARTOR = "/";
  /* Singleton object */
  private static FileSystem fileSystem;

  /* Root directory */
  private final Directory rootDir;

  /**
   * Constructor of the fileSystem Object. Only one of Directory will
   * be created as a rootDir。
   * 
   */
  private FileSystem() {
    /* initialize rootDir */
    rootDir = Directory.createRootDir();
  }

  /**
   * singleton getter for FileSystem
   * 
   * @return the singleton object fileSystem
   */
  public static FileSystem getFileSystem() {
    if (fileSystem == null) {
      fileSystem = new FileSystem();
    }
    return fileSystem;
  }

  public static void destroySingletonObj() {
    fileSystem = null;
  }

  /**
   * getter for rootDir
   * 
   * @return rootDir
   */
  public Directory getRootDir() {
    return rootDir;
  }

  /**
   * get the path for a given directory
   * 
   * @param dir is the directory pass in to find the path
   * @return path for dir
   */
  public String getPathGivenDir(Directory dir) {
    /* base case for root directory */
    if (dir.isRootDir()) {
      return DIRECTORY_SEPARTOR;
    } else {
      Directory parentDir = dir.getParentDir();
      return getPathGivenDir(parentDir) + dir.getName()
          + DIRECTORY_SEPARTOR;
    }
  }

  /**
   * get file or directory with the given path
   * 
   * @param path given to find the file or directory
   * @param curWorkingDir current working directory
   * @return the file or directory object with given path
   * @throws JShellPathInvalidException when the given path is invalid
   * @throws JShellFileNotFoundException when files or directories
   *         shown in the given path can not be found
   */
  public File getFileGivenPath(String path, Directory curWorkingDir)
      throws JShellPathInvalidException, JShellFileNotFoundException {
    /* Process the path and put it into an ArrayList */
    String[] pathArray = path.split(DIRECTORY_SEPARTOR);
    List<String> pathList = new ArrayList<String>();
    for (int i = 0; i < pathArray.length; i++) {
      if (!pathArray[i].isEmpty()) {
        pathList.add(pathArray[i]);
      }
    }
    Directory curDir = null;
    /* Identify if the path is absolute or relative */
    if (path.startsWith(DIRECTORY_SEPARTOR)) {
      curDir = rootDir;
    } else {
      curDir = curWorkingDir;
    }
    File result = curDir;
    for (int i = 0; i < pathList.size(); i++) {
      File nextFileOrDir = curDir.findFile(pathList.get(i));

      if (i == pathList.size() - 1) {
        result = nextFileOrDir;
      } else {
        if (!(nextFileOrDir instanceof Directory)) {
          /* A file appears in the middle of the path */
          throw new JShellPathInvalidException(path);
        }
        curDir = (Directory) nextFileOrDir;
      }
    }
    /* If path ends with '/', then it must return a directory */
    if (path.endsWith(DIRECTORY_SEPARTOR)
        && !(result instanceof Directory)) {
      throw new JShellPathInvalidException(path);
    }
    return result;
  }

  /**
   * Identify if a directory is a sub-directory of another directory
   * 
   * @param dir1 is the given directory supposed to be the parent
   * @param dir2 is the given directory supposed to be the
   *        sub-directory
   * @return true if dir2 is a sub-directory of dir1. Otherwise,
   *         false.
   */
  public Boolean isSubDir(Directory dir1, Directory dir2) {
    String path1 = getPathGivenDir(dir1);
    String path2 = getPathGivenDir(dir2);
    return path2.startsWith(path1);
  }

  /**
   * Get parent path and child name from a path
   * 
   * @param path is the input path
   * @return the parent path and child name in a List
   * @throws JShellPathInvalidException when given path is invalid
   */
  public List<String> getParentPathAndName(String path)
      throws JShellPathInvalidException {
    if (path.startsWith(DIRECTORY_SEPARTOR)) {
      path = DIRECTORY_SEPARTOR + Directory.PATH_SELF_DIR + path;
    } else {
      path = Directory.PATH_SELF_DIR + DIRECTORY_SEPARTOR + path;
    }

    Pattern pattern = Pattern.compile(PATTERN_PATH);
    Matcher matcher = pattern.matcher(path);
    if (matcher.matches()) {
      return Arrays.asList(matcher.group(1), matcher.group(2));
    } else {
      throw new JShellPathInvalidException(path);
    }
  }
}
