package exceptions;

/**
 * Exception gets thrown when arguments is invalid.
 * 
 * @author yangran
 */
public class JShellArgsInvalidException extends JShellException {

  private static final String ERROR_MSG_INVALID_ARGS =
      "Invalid arguments.";

  /* Usage message for the command */
  private String cmdUsage;

  public JShellArgsInvalidException(String cmdUsage) {
    this.cmdUsage = cmdUsage;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(ERROR_MSG_INVALID_ARGS);
    System.out.println(cmdUsage);
  }
}
