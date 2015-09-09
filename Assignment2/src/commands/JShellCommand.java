package commands;

import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellException;
import exceptions.JShellIllegalOptionException;
import exceptions.JShellNoManualEntryException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * Abstract super class for all commands.
 *
 */
public abstract class JShellCommand extends Observable {

  protected static final String PREFIX_USAGE = "usage: ";
  private static final String PATH_MANUAL = "commandManual/";
  private static final String SUFFIX_MANUAL = "_manual.txt";
  private static final String PREFIX_OPTION = "-";

  /* Number of required arguments */
  protected int numOfRequiredArgs;
  /* Name of the command */
  protected String cmdName;
  /* Usage message of the command */
  protected String cmdUsage;
  /* Map that indicate which options are activated */
  private Map<String, Boolean> optionMap =
      new HashMap<String, Boolean>();

  /**
   * Run the command using the given arguments on a given file or
   * directory.
   * 
   * @param args is the arguments from user input
   * @param file is the file or directory that the command will run
   *        on.
   * @throws JShellException when any error happens
   * @return the output of the command
   */
  public abstract String run(List<String> args,
      final FileSystem fileSystem, final Directory cwDir)
      throws JShellException;

  /**
   * Validate the arguments.
   * 
   * @param args is the arguments from user input.
   * @throws JShellInputException when arguments invalid.
   */
  public void validateArgs(List<String> args)
      throws JShellArgsInvalidException {
    if (args.size() != numOfRequiredArgs) {
      throw new JShellArgsInvalidException(cmdUsage);
    }
  }

  /**
   * Function used to process the option in user input
   * 
   * @param args is the user input
   * @throws JShellIllegalOptionException when the option is invalid
   */
  public void processOptions(List<String> args)
      throws JShellIllegalOptionException {
    Iterator<String> argsIterator = args.iterator();
    while (argsIterator.hasNext()) {
      /* Go through all arguments to find options */
      String arg = argsIterator.next();
      if (arg.startsWith(PREFIX_OPTION) && !arg.isEmpty()) {
        /* When find an option */
        String option = arg.substring(1);
        /* Activate this option for this option */
        activateOptionIfAvailable(option);
        /* Remove option from user input */
        argsIterator.remove();
        break;
      }
    }
  }

  /**
   * Find the manual for this command.
   * 
   * @return the manual as String
   * @throws JShellNoManualEntryException if not manual entry exists
   *         for this command
   */
  public final String getManual() throws JShellNoManualEntryException {
    try {
      URL url =
          ClassLoader.getSystemClassLoader().getResource(
              PATH_MANUAL + cmdName + SUFFIX_MANUAL);
      FileInputStream manual = new FileInputStream(url.getPath());
      return new Scanner(manual, "UTF-8").useDelimiter("\\A").next();
    } catch (Exception e) {
      /* If no entry found */
      throw new JShellNoManualEntryException(cmdName);
    }
  }

  /**
   * Getter for cmdUsage
   * 
   * @return cmdUsage
   */
  public String getCmdUsage() {
    return cmdUsage;
  }


  /**
   * Add an option to this command
   * 
   * @param option is option to be added
   */
  protected final void addOption(String option) {
    /* Add the command and initially inactive */
    optionMap.put(option, Boolean.FALSE);
  }

  /**
   * Activate the given option if available in this command
   * 
   * @param option is the option to be activated
   * @throws JShellIllegalOptionException when option illegal
   */
  protected final void activateOptionIfAvailable(String option)
      throws JShellIllegalOptionException {
    if (hasOption(option)) {
      /* Update optionMap to set the value to be true */
      optionMap.put(option.toUpperCase(), Boolean.TRUE);
    } else {
      throw new JShellIllegalOptionException(cmdName, option);
    }
  }

  /**
   * Check if a given option is activated
   * 
   * @param option is the option to be checked
   * @return true if the given option is activated. Otherwise, false
   */
  protected final Boolean optionActivated(String option) {
    return hasOption(option) && optionMap.get(option.toUpperCase());
  }

  private final Boolean hasOption(String option) {
    return optionMap.containsKey(option.toUpperCase());
  }
}
