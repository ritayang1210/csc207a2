package exceptions;

/**
 * Abstract super class exception for all JShell exceptions
 *
 */
public abstract class JShellException extends Exception {

  /**
   * Print the error message.
   * 
   */
  public abstract void printJShellErrMsg();
}
