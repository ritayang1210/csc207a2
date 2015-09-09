package exceptions;

/**
 * Exception gets thrown when something supposed to be file is
 * actually not
 *
 */
public class JShellIsNotFileException extends JShellException {

  private static final String ERROR_MSG_IS_NOT_FILE =
      "%s: Is not a file.";

  /* The supposed file path */
  private String isNotFilePath;

  public JShellIsNotFileException(String isNotFilePath) {
    this.isNotFilePath = isNotFilePath;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_IS_NOT_FILE,
        isNotFilePath));
  }

}
