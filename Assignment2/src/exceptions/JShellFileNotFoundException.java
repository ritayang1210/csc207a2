package exceptions;

/**
 * Exception gets thrown when file or directory not found.
 * 
 */
public class JShellFileNotFoundException extends JShellException {

  private static final String ERROR_MSG_INVALID_FILE =
      "%s: No such file or directory.";

  /* Name of the file not found */
  private String invalidFileName;

  public JShellFileNotFoundException(String fileName) {
    this.invalidFileName = fileName;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_INVALID_FILE,
        this.invalidFileName));
  }
}
