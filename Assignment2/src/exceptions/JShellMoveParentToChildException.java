package exceptions;

/**
 * Exception gets thrown when move/copy a directory to its child
 *
 */
public class JShellMoveParentToChildException extends JShellException {

  private static final String ERROR_MSG_MOVE_PARENT_TO_CHILD =
      "Cannot move/copy %s to %s as the former is parent folder of the latter";

  /* The parent directory */
  String parent;
  /* The child directory */
  String child;

  public JShellMoveParentToChildException(String parent, String child) {
    this.parent = parent;
    this.child = child;
  }

  @Override
  public void printJShellErrMsg() {
    System.out.println(String.format(ERROR_MSG_MOVE_PARENT_TO_CHILD,
        parent, child));

  }

}
