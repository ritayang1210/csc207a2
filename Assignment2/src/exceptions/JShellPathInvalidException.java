package exceptions;

/**
 * Exception gets thrown when path is not valid.
 * 
 */

public class JShellPathInvalidException extends JShellException {

  private static final String ERROR_MSG_INVALID_PATH =
      "Path %s is not valid.";

  /* The path that is invalid */
  private String invalidPath;

  public JShellPathInvalidException(String invalidPath) {
    this.invalidPath = invalidPath;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_INVALID_PATH,
        invalidPath));
  }
}
