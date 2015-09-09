package driver;

import java.util.HashMap;
import java.util.Map;

import commands.CatCommand;
import commands.CdCommand;
import commands.CpCommand;
import commands.EchoCommand;
import commands.ExitCommand;
import commands.GetCommand;
import commands.GrepCommand;
import commands.JShellCommand;
import commands.LsCommand;
import commands.ManCommand;
import commands.MkdirCommand;
import commands.MvCommand;
import commands.PopdCommand;
import commands.PushdCommand;
import commands.PwdCommand;
import commands.HistoryCommand;
import exceptions.JShellCommandNotFoundException;

/**
 * Class that manage all commands and find the the correct one to run
 * according to user input.
 * 
 */
public class CmdManager {

  /* Map of all valid commands */
  private Map<String, JShellCommand> cmdMap;

  public CmdManager() {
    /* Initialize the map of all valid commands */
    cmdMap = new HashMap<String, JShellCommand>();

    /* Input all valid commands into map */
    cmdMap.put(ExitCommand.CMD_NAME, new ExitCommand());
    cmdMap.put(MkdirCommand.CMD_NAME, new MkdirCommand());
    cmdMap.put(LsCommand.CMD_NAME, new LsCommand());
    cmdMap.put(CdCommand.CMD_NAME, new CdCommand());
    cmdMap.put(PwdCommand.CMD_NAME, new PwdCommand());
    cmdMap.put(CpCommand.CMD_NAME, new CpCommand());
    cmdMap.put(ManCommand.CMD_NAME, new ManCommand());
    cmdMap.put(CatCommand.CMD_NAME, new CatCommand());
    cmdMap.put(MvCommand.CMD_NAME, new MvCommand());
    cmdMap.put(EchoCommand.CMD_NAME, new EchoCommand());
    cmdMap.put(PushdCommand.CMD_NAME, new PushdCommand());
    cmdMap.put(PopdCommand.CMD_NAME, new PopdCommand());
    cmdMap.put(HistoryCommand.CMD_NAME, new HistoryCommand());
    cmdMap.put(GrepCommand.CMD_NAME, new GrepCommand());
    cmdMap.put(GetCommand.CMD_NAME, new GetCommand());
  }

  /**
   * Find the right command according to user input.
   * 
   * @param cmd is the command name from user input.
   * @return the command according to user input.
   * @throws JShellCommandNotFoundException is thrown if cmd is not
   *         found in the map of valid commands
   */
  public JShellCommand getCommand(String cmd)
      throws JShellCommandNotFoundException {
    /*
     * Returns subclass of JShellCommand corresponding to cmd if cmd
     * is found in cmdMap
     */
    if (cmdMap.containsKey(cmd)) {
      return cmdMap.get(cmd);
    }

    /* Throw JShellCommandNotFoundException when command not found */
    throw new JShellCommandNotFoundException(cmd);
  }
}
