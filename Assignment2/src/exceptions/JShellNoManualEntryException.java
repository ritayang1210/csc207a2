package exceptions;

/**
 * Exception gets thrown when no manual entry exist
 *
 */
public class JShellNoManualEntryException extends JShellException {

  private static final String ERROR_MSG_NO_MAN_ENTRY =
      "No manual entry for %s";

  /* Name of the command not found */
  private String commandWithNoManual;

  public JShellNoManualEntryException(String commandWithNoManual) {
    this.commandWithNoManual = commandWithNoManual;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_NO_MAN_ENTRY,
        commandWithNoManual));
  }

}
