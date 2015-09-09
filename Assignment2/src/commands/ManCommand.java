package commands;

import java.util.List;

import driver.CmdManager;
import exceptions.JShellCommandNotFoundException;
import exceptions.JShellNoManualEntryException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * The class for man command.
 *
 */
public class ManCommand extends JShellCommand {

  private static final int NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "man";
  public static final String USAGE = "man CMD";

  private CmdManager cmdManager;

  public ManCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  public void setCmdManager(CmdManager cmdManager) {
    this.cmdManager = cmdManager;
  }

  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) throws JShellNoManualEntryException {
    String cmdName = args.get(0);
    try {
      JShellCommand commandToCheck = cmdManager.getCommand(cmdName);
      return commandToCheck.getManual();
    } catch (JShellCommandNotFoundException e) {
      /* If command not found */
      throw new JShellNoManualEntryException(cmdName);
    }
  }
}
