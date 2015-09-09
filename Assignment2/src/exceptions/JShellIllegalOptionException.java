package exceptions;

/**
 * Exception gets thrown when given option is illegal
 * 
 */
public class JShellIllegalOptionException extends JShellException {

  private static final String ERROR_MSG_ILLEGAL_OPTION =
      "%s: illegal option -- %s";

  /* Command that doesn't this option */
  private String cmdName;
  /* The illegal option */
  private String illegalOp;

  public JShellIllegalOptionException(String cmdName, String illegalOp) {
    this.cmdName = cmdName;
    this.illegalOp = illegalOp;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_ILLEGAL_OPTION,
        cmdName, illegalOp));
  }

}
