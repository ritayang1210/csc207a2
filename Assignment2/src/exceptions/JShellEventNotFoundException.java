package exceptions;

/**
 * Exception gets thrown when a given event cannot be found in history
 *
 */
public class JShellEventNotFoundException extends JShellException {

  private static final String ERROR_MSG_EVENT_NOT_FOUND =
      "!%s: event not found";

  /* The wrong history number */
  String wrongHistoryNumber;

  public JShellEventNotFoundException(String worngHistoryNumber) {
    this.wrongHistoryNumber = worngHistoryNumber;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_EVENT_NOT_FOUND,
        wrongHistoryNumber));
  }
}
