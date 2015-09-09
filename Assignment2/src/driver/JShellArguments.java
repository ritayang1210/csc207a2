package driver;

import java.util.List;

public class JShellArguments {

  /* Name of the command that user types in */
  private String cmdName;
  /* Arguments of the command that user types in */
  private List<String> cmdArgs;
  /* The redirection operation */
  private String redirectOp;
  /* The redirection output file */
  private String outputFile;
  /* Flag indicate whether a redirection is required */
  private Boolean redirectFlag;

  public JShellArguments(String cmdName, List<String> cmdArgs,
      String redirectOp, String outputFile, Boolean redirectFlag) {
    this.cmdName = cmdName;
    this.cmdArgs = cmdArgs;
    this.redirectOp = redirectOp;
    this.outputFile = outputFile;
    this.redirectFlag = redirectFlag;
  }

  /**
   * Getter for redirect operation
   * 
   * @return redirectOp
   */
  public String getRedirectOp() {
    return redirectOp;
  }

  /**
   * Getter for the output file
   * 
   * @return outputFile
   */
  public String getOutputFile() {
    return outputFile;
  }

  /**
   * Indicate whether or not a redirection is required
   * 
   * @return redirectFlag
   */
  public Boolean ifRedirect() {
    return redirectFlag;
  }

  /**
   * Getter for command name.
   * 
   * @return cmdName.
   */
  public String getCommandName() {
    return cmdName;
  };

  /**
   * Getter for command arguments.
   * 
   * @return cmdArgs.
   */
  public List<String> getArguments() {
    return cmdArgs;
  };
}
