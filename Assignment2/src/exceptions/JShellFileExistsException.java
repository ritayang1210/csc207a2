package exceptions;

/**
 * Exception gets thrown when file/directory already exist
 *
 */
public class JShellFileExistsException extends JShellException {

  private static final String ERROR_MSG_EXISTED_FILE =
      "%s: File exists.";

  /* Name of the already existing file */
  private String existingFileName;

  public JShellFileExistsException(String fileName) {
    this.existingFileName = fileName;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_EXISTED_FILE,
        this.existingFileName));
  }
}
