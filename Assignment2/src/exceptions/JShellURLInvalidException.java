package exceptions;

/**
 * Exception gets thrown when given URL is invalid
 * 
 */
public class JShellURLInvalidException extends JShellException {

  private static final String ERROR_MSG_URL_INVALID =
      "%s: Is not a valid URL.";

  /* The supposed URL */
  private String invalidURL;

  public JShellURLInvalidException(String isNotValidURL) {
    this.invalidURL = isNotValidURL;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_URL_INVALID,
        invalidURL));
  }

}
