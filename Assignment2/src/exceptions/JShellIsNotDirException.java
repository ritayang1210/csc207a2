package exceptions;

/**
 * Exception gets thrown when something supposed to be directory is
 * actually not
 * 
 */
public class JShellIsNotDirException extends JShellException {

  private static final String ERROR_MSG_IS_NOT_DIR =
      "%s: Is not a directory.";

  /* The supposed dir path */
  private String isNotDirPath;

  public JShellIsNotDirException(String isNotFilePath) {
    this.isNotDirPath = isNotFilePath;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_IS_NOT_DIR,
        isNotDirPath));
  }

}
