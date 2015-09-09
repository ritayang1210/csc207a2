package fileSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exceptions.JShellFileExistsException;
import exceptions.JShellFileNotFoundException;

/**
 * Directory objects are the places to store other Dir's or Files
 * 
 * This Directory class will extends File. It reuses all methods of
 * File, except method setContent, since, unlike File, Directory
 * objects' contents will be a mash map contains Keys:Name of the
 * file, and Values:File Object (including Directory).
 * 
 *
 */
public class Directory extends File {

  private static final String NAME_ROOTDIR = "";
  public static final String PATH_PARENT_DIR = "..";
  public static final String PATH_SELF_DIR = ".";

  /* Contents of the directory */
  private Map<String, File> contents;
  /* Boolean flag indicating whether this directory is root */
  private Boolean isRootDir;

  /**
   * Constructor of the Directory Object.
   * 
   * Only one of Directory will be created as a rootDir
   * 
   * @param parentDir The parent directory
   * @param Name The name that user desire.
   * @param rootDir Whether this directory is a root directory.
   */
  private Directory(Directory parentDir, String Name,
      Boolean isRootDir) {
    setName(Name);
    this.isRootDir = isRootDir;
    contents = new HashMap<String, File>();
    contents.put(PATH_SELF_DIR, this);
    // If the directory is not set to be root, it will be added to
    // parent
    // dir's contents by calling setParentDir().
    if (!isRootDir) {
      setParentDir(parentDir);
      // If the directory is set to be root, setParent() will not be
      // called,
      // since setParent() will add the File to its parent's contents.
    } else {
      contents.put(PATH_PARENT_DIR, this);
      this.parentDir = this;
    }
  }

  /**
   * This method is used to construct root directory.
   * 
   * Root directory's parent will be set to itself.
   */
  public static Directory createRootDir() {
    return new Directory(null, NAME_ROOTDIR, Boolean.TRUE);
  }

  /**
   * To construct regular directory by given name and its parent
   * directory.
   * 
   * @param parentDir The parent directory which user desire
   * @param name The name of the directory which user desire.
   * @return A directory that has user desired parent directory and
   *         name.
   * @throws JShellFileExistsException when the file exists
   */
  public static Directory createRegularDir(Directory parentDir,
      String name, Boolean replace) throws JShellFileExistsException {
    if (!replace && parentDir.contains(name)) {
      throw new JShellFileExistsException(name);
    }
    return new Directory(parentDir, name, Boolean.FALSE);
  }

  /**
   * This method will override and reuse File's setParentDir method.
   * 
   * The reason is that to set Directory's parent directory, there
   * will be one more step, which is to add the parentDir to
   * Directory's hash map.
   * 
   * @param newParentDir The parent directory which user desire.
   */
  @Override
  public void setParentDir(Directory parentDir) {
    super.setParentDir(parentDir);
    this.contents.put(PATH_PARENT_DIR, parentDir);
  }

  /**
   * To check whether this directory is a root directory.
   * 
   * @return true or false
   */
  public Boolean isRootDir() {
    return isRootDir;
  }

  /**
   * The method addContents appends File Object(including Directory
   * Object) to the directory. This method is protected. To add a File
   * to a Directory, use setParentDir().
   * 
   * @param file File Object which need to be added.
   */
  public void addContents(File fileOrDirectory) {
    // The Key of the map is set to the file's Name strictly.
    String Name = fileOrDirectory.getName();
    this.contents.put(Name, fileOrDirectory);
  }

  /**
   * The method findFile will check if this Directory contains the
   * desired File or Directory. If the File exists, this method will
   * return the File
   * 
   * @param fileName Name of the file which needed
   * @return
   * @return File Object with fileName if existed, Or throw a
   *         FileNotFoundException.
   * @throws JShellFileNotFoundException when fileName not found in
   *         dir
   */
  public File findFile(String fileName)
      throws JShellFileNotFoundException {
    if (!this.contents.containsKey(fileName)) {
      throw new JShellFileNotFoundException(fileName);
    } else {
      return this.contents.get(fileName);
    }
  }

  /**
   * To remove File Object name fileName
   * 
   * @param fileName Name of the File
   */
  public void removeFile(String fileName) {
    this.contents.remove(fileName);
  }

  /**
   * To return a List contains all the names of the File Objects in
   * the directory including rootDir and itself.
   * 
   * @return List<Names of all File Objects>
   */
  public List<String> getContentList() {
    // Get the Set<all names of all File Object>.
    Set<String> nameSet = this.contents.keySet();
    List<String> nameList = new ArrayList<String>(nameSet);
    return nameList;
  }

  /**
   * This method will return the contents of the directory, except
   * root and itself as a List<String>.
   * 
   * @return
   */
  public List<String> getContentsNoRtSf() {
    List<String> nameSet = this.getContentList();
    nameSet.remove(".");
    nameSet.remove("..");
    return nameSet;
  }

  /**
   * Get content list in File object
   * 
   * @return a list of File which is the contents
   */
  public List<File> getFileList() {
    Map<String, File> fileMap = new HashMap<String, File>(contents);
    fileMap.remove(PATH_PARENT_DIR);
    fileMap.remove(PATH_SELF_DIR);
    return new ArrayList<File>(fileMap.values());
  }

  /**
   * Copy the directory(including its contents) to desired target
   * directory.
   * 
   * @param newName
   * @throws JShellFileExistsException
   */
  @Override
  public void makeCopyToDirectoryWithName(Directory newParentDir,
      String newName) throws JShellFileExistsException {
    Directory newDir;
    List<String> contents;
    newDir =
        Directory.createRegularDir(newParentDir, newName,
            Boolean.TRUE);

    contents = this.getContentsNoRtSf();
    // Recursively copy the contents of all File in this Directory.
    for (String name : contents) {
      this.contents.get(name).makeCopyToDirectoryWithName(newDir,
          name);
    }
  }

  /**
   * Check if file/directory exist
   * 
   * @param fileName is the name of the file/directory to be checked
   * @return true if file/directory exist. Otherwise, false
   */
  public boolean contains(String fileName) {
    return this.contents.containsKey(fileName);
  }
}
