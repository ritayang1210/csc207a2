package commands;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.JShellArgsInvalidException;
import exceptions.JShellURLInvalidException;
import fileSystem.Directory;
import fileSystem.File;
import fileSystem.FileSystem;

public class GetCommand extends JShellCommand {
  private static final int NUM_OF_REQUIRED_ARGS = 1;
  public static final String CMD_NAME = "get";
  public static final String USAGE = "get URL";

  private final String URL_SEPARATOR = "/";
  private final String NEW_LINE = "\n";

  public GetCommand() {
    numOfRequiredArgs = NUM_OF_REQUIRED_ARGS;
    cmdName = CMD_NAME;
    cmdUsage = PREFIX_USAGE + USAGE;
  }

  @Override
  public String run(List<String> args, FileSystem fileSystem,
      Directory cwDir) throws JShellArgsInvalidException,
      JShellURLInvalidException {
    String url = args.get(0);


    String fileName = findName(url);
    String contents = getURLContents(url);

    // To create the file under cwDir.
    File newFile = new File(cwDir, fileName);
    newFile.setContents(contents);

    return null;
  }

  /**
   * To create a new File according to given URL
   * 
   * This method will retrieve the file from given URL if the URL ends
   * with txt.Or this method will create the file named the last
   * segment of the URL and the contents been set to its source code.
   * 
   * @param url URL given by user
   * @param cwDir current working directory
   * @return a File with its name and contents been set dynamically.
   * @throws JShellURLInvalidException
   * @throws Exception
   */
  private String getURLContents(String inputURL)
      throws JShellURLInvalidException {
    StringBuilder contents = new StringBuilder();
    URLConnection urlCon;
    InputStreamReader in;
    Scanner urlScanner;

    try {
      URL url = new URL(inputURL);
      urlCon = url.openConnection();

      // To check if the URL is valid, and has contents
      if (urlCon != null && urlCon.getInputStream() != null) {
        in = new InputStreamReader(urlCon.getInputStream());
        urlScanner = new Scanner(in);

        // Read from the contents of URL
        while (urlScanner.hasNext()) {
          if (!contents.toString().isEmpty())
            contents.append(NEW_LINE);
          contents.append(urlScanner.nextLine());
        }
        urlScanner.close();
      }
    } catch (Exception e) {
      throw new JShellURLInvalidException(inputURL);
    }

    return contents.toString();
  }

  /**
   * To find the file name from a given URL
   * 
   * @param url URL given by user
   */
  private String findName(String url) {
    String fileName = null;

    // To delete the last "/" if the URL ends with it.
    if (url.endsWith(URL_SEPARATOR))
      url = url.substring(0, url.length() - 1);

    String regex = ".*/(.*?)$";
    Pattern lastSegRegex = Pattern.compile(regex);
    Matcher m = lastSegRegex.matcher(url);


    // capture group 1 will be the last segment of the URL.
    if (m.matches() && !m.group(1).isEmpty()) {
      fileName = m.group(1);
    }

    return fileName;
  }
}
