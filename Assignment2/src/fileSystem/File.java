package fileSystem;

import exceptions.JShellFileExistsException;

/**
 * File objects have strings as contents. It keeps track on its own
 * name and parent.
 * 
 * This class will have Directory as a subclass. Objects of this class
 * will be the objects used by all the commands input by users through
 * Jshell. This class will be the skeleton and the basis of our mock
 * file system.
 * 
 *
 */
public class File {

  /* Name of the file */
  private String name;
  /*
   * Parent directory of the file. parentDir is set to protected for
   * Directory to create a root directory
   */
  protected Directory parentDir;
  /* Contents of the file */
  private String contents;

  public File() {}

  /**
   * Constructor of the File Object.
   * 
   * @param parentDir Parent directory of the File Object
   * @param fileName Name of the File which user desire
   * @throws JShellFileExistsException
   */
  public File(Directory parentDir, String fileName) {
    setName(fileName);
    setParentDir(parentDir);
    contents = "";
  }

  /**
   * Method used to set the name of the File Object
   * 
   * @param fileName Name of the File which user desire.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method used to get the name of the File
   * 
   * @return Name of the File
   */
  public String getName() {
    return name;
  }

  /**
   * Method used to set parent directory of File Object. Whenever the
   * parent directory is set, the parent directory will be notified
   * that it has a new content. Old parent will remove the File.
   * 
   * @param parentDir
   */
  public void setParentDir(Directory parentDir) {
    // If the File has a parent, it will be removed from the parent's
    // contents.
    if (this.parentDir != null) {
      this.parentDir.removeFile(this.getName());
    }
    this.parentDir = parentDir;
    // To notify parent directory that it has a new child.
    parentDir.addContents(this);
  }

  /**
   * To get the parent directory of the File
   * 
   * @return parent directory
   */
  public Directory getParentDir() {
    return parentDir;
  }

  /**
   * To set the contents of the File Object. The contents of a File
   * Object will be String.
   * 
   * @param newContents String input by user.
   */
  public void setContents(String newContents) {
    this.contents = newContents;
  }

  /**
   * To get the contents of the File Object. The return type is set to
   * Object for Directory to override this method.
   * 
   * @return String contents
   */
  public String getContents() {
    return contents;
  }

  /**
   * To copy the File Object to a new parent directory. A new File
   * Object with the same name and contents will be created under the
   * new parent directory.
   * 
   * @param newParentDir Directory where user wants the File to be
   *        stored.
   * @throws JShellFileExistsException
   */
  public void makeCopyToDirectoryWithName(Directory newParentDir,
      String newName) throws JShellFileExistsException {
    File newFile;
    newFile = new File(newParentDir, newName);
    newFile.setContents(this.contents);
  }

  /**
   * To add String after the original contents instead of overwriting
   * them.
   * 
   * @param newContents String that want to be appended.
   */
  public void appendContents(String newContents) {
    // Get its original contents and make a new String with
    // newContents,
    // and then reset Contents.
    this.setContents(this.contents + newContents);
  }
}
