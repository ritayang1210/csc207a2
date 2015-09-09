package exceptions;

/**
 * Exception gets thrown when command not found.
 *
 */
public class JShellCommandNotFoundException extends JShellException {

  private static final String ERROR_MSG_INVALID_COMMAND =
      "%s: Command not found.";

  /* Name of the command not found */
  private String invalidCommand;

  public JShellCommandNotFoundException(String invalidCommand) {
    this.invalidCommand = invalidCommand;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_INVALID_COMMAND,
        invalidCommand));
  }
}
