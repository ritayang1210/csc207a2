package exceptions;

/**
 * Exception gets thrown when dirStack is empty not found.
 *
 */
public class JShellDirStackEmptyException extends JShellException {

  private static final String ERROR_MSG_INVALID_COMMAND =
      "popd: Direcotry stack empty.";

  @Override
  public void printJShellErrMsg() {
    System.out.println(ERROR_MSG_INVALID_COMMAND);
  }
}
