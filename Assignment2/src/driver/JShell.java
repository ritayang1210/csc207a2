// **********************************************************
// Assignment2:
// Student1:
// UTOR user_name: hetom
// UT Student #: 1000270547
// Author: Tom He
//
// Student2:
// UTOR user_name:wangruol
// UT Student #:1000298563
// Author:Ruolan Wang
//
// Student3:
// UTOR user_name: yangran
// UT Student #: 998510856
// Author: Ran Yang
//
// Student4:
// UTOR user_name:zhaoji25
// UT Student #:999484213
// Author: Jiawei Zhao
//
//
// Honor Code: I pledge that this program represents my own
// program code and that I have coded on my own. I received
// help from no one in designing and debugging my program.
// I have also read the plagiarism section in the course info
// sheet of CSC 207 and understand the consequences.
// *********************************************************
package driver;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.Stack;

import commands.CdCommand;
import commands.HistoryCommand;
import commands.JShellCommand;
import commands.ManCommand;
import commands.PopdCommand;
import commands.PushdCommand;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellEventNotFoundException;
import exceptions.JShellException;
import exceptions.JShellExpectedExitExceptiopn;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellIsNotFileException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

/**
 * Main class of the program.
 * 
 */
public class JShell implements Observer {

  private static final String CHAR_PROMPT = "# ";

  private Scanner userInputScanner;

  /* Command manager to manage all commands */
  private CmdManager cmdMng;

  /* Command reader to process the user input */
  private CmdProcessor cmdProcessor;

  /* File system to keep track of all files and directories */
  private FileSystem fileSystem;

  /* Directory that is the current working directory */
  private Directory cwDir;

  /* Stack that holds directories from pushd and popd commands. */
  private Stack<String> dirStack;

  /* The redirector that performs redirection */
  JShellRedirector redirector;

  /* The history recorder that stores the history */
  JShellHistoryRecorder historyRecorder;

  /* The recaller used to retrieve the command from history */
  CmdRecaller cmdRecaller;

  public JShell() {
    userInputScanner = new Scanner(System.in);
    cmdMng = new CmdManager();
    cmdProcessor = new CmdProcessor();
    fileSystem = FileSystem.getFileSystem();
    cwDir = fileSystem.getRootDir();
    dirStack = new Stack<String>();
    redirector = new JShellRedirector();
    historyRecorder = new JShellHistoryRecorder();
    cmdRecaller = new CmdRecaller(historyRecorder);

    try {
      cmdMng.getCommand(CdCommand.CMD_NAME).addObserver(this);
    } catch (Exception e) {
    }

    try {
      cmdMng.getCommand(PushdCommand.CMD_NAME).addObserver(this);
    } catch (Exception e) {
    }

    try {
      cmdMng.getCommand(PopdCommand.CMD_NAME).addObserver(this);
    } catch (Exception e) {
    }

    try {
      ((HistoryCommand) cmdMng.getCommand(HistoryCommand.CMD_NAME))
          .setHistoryRecorder(historyRecorder);
    } catch (Exception e) {
    }

    try {
      ((ManCommand) cmdMng.getCommand(ManCommand.CMD_NAME))
          .setCmdManager(cmdMng);
    } catch (Exception e) {
    }
  }

  /**
   * Run the JShell program.
   *
   */
  public void run() {
    /* Repeatedly read the user input */
    while (Boolean.TRUE) {
      /* Processing the user input to split into command and arguments */
      System.out.print(fileSystem.getPathGivenDir(cwDir)
          + CHAR_PROMPT);
      try {
        String input = userInputScanner.nextLine();
        input = preProcessInput(input);

        /* Process the user input */
        JShellArguments jShellArgs = cmdProcessor.processInput(input);
        if (jShellArgs == null) {
          continue;
        }

        String command = jShellArgs.getCommandName();

        /* Find the right command to run */
        JShellCommand jsc = cmdMng.getCommand(command);
        /* Validate the arguments and run the command */
        jsc.validateArgs(jShellArgs.getArguments());
        jsc.processOptions(jShellArgs.getArguments());
        String output =
            jsc.run(jShellArgs.getArguments(), fileSystem, cwDir);
        processOutput(jShellArgs, jsc, output);
      } catch (JShellExpectedExitExceptiopn e) {
        /* Exit the program */
        break;
      } catch (JShellException e) {
        /* Standard error */
        e.printJShellErrMsg();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    userInputScanner.close();
  }

  private String preProcessInput(String input)
      throws JShellEventNotFoundException {
    input = cmdRecaller.recallIfRequired(input);
    if (!input.trim().isEmpty()) {
      historyRecorder.recordCommand(input);
    }
    return input;
  }

  private void processOutput(JShellArguments jShellArgs,
      JShellCommand jsc, String output)
      throws JShellArgsInvalidException, JShellPathInvalidException,
      JShellFileNotFoundException, JShellIsNotFileException {
    if (output != null) {
      if (jShellArgs.ifRedirect()) {
        if (jShellArgs.getOutputFile() == null
            || jShellArgs.getOutputFile().isEmpty()) {
          throw new JShellArgsInvalidException(jsc.getCmdUsage());
        }
        /* Redirection */
        redirector.redirectTo(fileSystem, cwDir, output,
            jShellArgs.getRedirectOp(), jShellArgs.getOutputFile());
      } else {
        /* Standard out */
        System.out.println(output);
      }
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    /* If the Observable is cd, update the current working directory */
    if (o instanceof CdCommand && arg instanceof Directory) {
      cwDir = (Directory) arg;
    } else if (o instanceof PushdCommand && arg instanceof Directory) {
      /*
       * If the Observable is pushd, push cwDir into dirStack and
       * change cwDir to the notified directory
       */
      dirStack.push(fileSystem.getPathGivenDir(cwDir));
      cwDir = (Directory) arg;
    } else if (o instanceof PopdCommand) {
      /*
       * If the Observable is popd, pop the top directory in dirStack
       * and change cwDir to it
       */
      try {
        File tempDir =
            fileSystem.getFileGivenPath(dirStack.pop(), cwDir);
        cwDir = (Directory) tempDir;
      } catch (JShellException e) {
        e.printJShellErrMsg();
      }

    }
  }

  public static void main(String[] args) {
    JShell jShell = new JShell();
    jShell.run();
  }
}
