package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import commands.CatCommand;
import commands.CdCommand;
import commands.CpCommand;
import commands.EchoCommand;
import commands.ExitCommand;
import commands.GetCommand;
import commands.GrepCommand;
import commands.ManCommand;
import commands.MkdirCommand;
import commands.MvCommand;
import commands.PopdCommand;
import commands.PushdCommand;
import commands.PwdCommand;

import driver.JShell;
import exceptions.JShellFileNotFoundException;
import exceptions.JShellPathInvalidException;
import fileSystem.Directory;
import fileSystem.FileSystem;

/**
 * Integration test for JShell
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JShellTest {

  private static final String PREFIX_INVALID_ARGUMENTS_USAGE =
      "Invalid arguments.\nusage: ";
  private JShell jShell;
  private FileSystem fileSystem = FileSystem.getFileSystem();
  private Directory rootDir = fileSystem.getRootDir();
  private final ByteArrayOutputStream outContent =
      new ByteArrayOutputStream();

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(null);
    System.setIn(System.in);
  }

  @AfterClass
  public static void afterClass() {
    FileSystem.destroySingletonObj();
  }

  @Test
  public void testJShellIllegalOptionLs() {
    runJShellWithUserInput("ls -a\n");
    assertPrintMessageEquals("ls: illegal option -- a");

    runJShellWithUserInput("ls -A\n");
    assertPrintMessageEquals("ls: illegal option -- A");
  }

  @Test
  public void testJShellInvalidInputMkdir() {
    runJShellWithUserInput("mkdir\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + MkdirCommand.USAGE);
  }

  @Test
  public void testJShellInvalidCommand() {
    runJShellWithUserInput("a\n");
    assertPrintMessageEquals("a: Command not found.");

    runJShellWithUserInput("        \n");
    assertPrintMessageEquals("");
  }

  @Test
  public void testJShellInvalidInputEcho() {
    /* Test invalid print */
    testEchoInvalidArgsPrint();

    /* Test invalid overwrite/append */
    testEchoInvalidArgsOverwriteAppend();
  }

  private void testEchoInvalidArgsPrint() {
    runJShellWithUserInput("echo\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo \"a\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo a\"\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo a\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);
  }

  private void testEchoInvalidArgsOverwriteAppend() {
    runJShellWithUserInput("echo a > file1\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo \"a > file1\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo a\" > file1\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo \"a\" invalidOperand file1\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);

    runJShellWithUserInput("echo \"a\" >>\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + EchoCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputCd() {
    runJShellWithUserInput("cd\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + CdCommand.USAGE);

    runJShellWithUserInput("cd a b c\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + CdCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputPwd() {
    runJShellWithUserInput("pwd a\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + PwdCommand.USAGE);

    runJShellWithUserInput("pwd a b c\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + PwdCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputCat() {
    runJShellWithUserInput("cat\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + CatCommand.USAGE);

    runJShellWithUserInput("cat a b\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + CatCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputMan() {
    runJShellWithUserInput("man\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + ManCommand.USAGE);

    runJShellWithUserInput("man a b\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + ManCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputExit() {
    runJShellWithUserInput("exit a\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + ExitCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputPushd() {
    runJShellWithUserInput("pushd\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + PushdCommand.USAGE);

    runJShellWithUserInput("pushd a b\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + PushdCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputPopd() {
    runJShellWithUserInput("popd a\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + PopdCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputGet() {
    runJShellWithUserInput("get\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GetCommand.USAGE);

    runJShellWithUserInput("get a b\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GetCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputGrep() {
    runJShellWithUserInput("grep\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GrepCommand.USAGE);

    runJShellWithUserInput("grep \"a\"\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GrepCommand.USAGE);

    runJShellWithUserInput("grep a\" b\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GrepCommand.USAGE);

    runJShellWithUserInput("grep a\" b\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GrepCommand.USAGE);

    runJShellWithUserInput("grep -r \"a\"\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + GrepCommand.USAGE);
  }

  @Test
  public void testJShellIllegalOptionGrep() {
    runJShellWithUserInput("grep -a \"a\" b\n");
    assertPrintMessageEquals("grep: illegal option -- a");

    runJShellWithUserInput("grep -Z \"a\" b\n");
    assertPrintMessageEquals("grep: illegal option -- Z");
  }

  @Test
  public void testJShellInvalidInputMv() {
    runJShellWithUserInput("mv\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + MvCommand.USAGE);

    runJShellWithUserInput("mv a b c\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + MvCommand.USAGE);
  }

  @Test
  public void testJShellInvalidInputCp() {
    runJShellWithUserInput("cp\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + CpCommand.USAGE);

    runJShellWithUserInput("cp a b c\n");
    assertPrintMessageEquals(PREFIX_INVALID_ARGUMENTS_USAGE
        + CpCommand.USAGE);
  }

  @Test
  public void testJShellRunAMkdir()
      throws JShellFileNotFoundException {
    runJShellWithUserInput("mkdir dir1 dir2 dir1/dir3"
        + " dir1/dir3/dir4 > wrongFile\n");

    assertFalse(rootDir.contains("wrongFile"));
    assertTrue(rootDir.contains("dir1"));
    assertTrue(rootDir.contains("dir2"));
    Directory dir1 = (Directory) rootDir.findFile("dir1");
    assertTrue(dir1.contains("dir3"));
    Directory dir3 = (Directory) dir1.findFile("dir3");
    assertTrue(dir3.contains("dir4"));

    /* File already exist */
    runJShellWithUserInput("mkdir dir1\n");
    assertPrintMessageEquals("dir1: File exists.");

    /* File not found */
    runJShellWithUserInput("mkdir dir2/dir3/dir4\n");
    assertPrintMessageEquals("dir3: No such file or directory.");

    /* Test redirect nothing */
  }

  @Test
  public void testJShellRunBEchoMkdir()
      throws JShellFileNotFoundException {
    runJShellWithUserInput("echo \"this is file 1\" > dir1/file1\n");
    runJShellWithUserInput("echo \"this is file 2\" >> /dir2/file2\n");
    runJShellWithUserInput("echo \"this is file 3\" > ./dir1/dir3/file3\n");
    runJShellWithUserInput("echo \"this is file 4\" "
        + ">> ././../dir1/dir3/dir4/file4\n");

    Directory dir1 = (Directory) rootDir.findFile("dir1");
    assertTrue(dir1.contains("file1"));
    assertEquals("this is file 1", dir1.findFile("file1")
        .getContents());

    Directory dir2 = (Directory) rootDir.findFile("dir2");
    assertTrue(dir2.contains("file2"));
    assertEquals("this is file 2", dir2.findFile("file2")
        .getContents());

    Directory dir3 = (Directory) dir1.findFile("dir3");
    assertTrue(dir3.contains("file3"));
    assertEquals("this is file 3", dir3.findFile("file3")
        .getContents());

    Directory dir4 = (Directory) dir3.findFile("dir4");
    assertTrue(dir4.contains("file4"));
    assertEquals("this is file 4", dir4.findFile("file4")
        .getContents());

    testEchoCmdEdgeCases(dir1, dir4);
  }

  private void testEchoCmdEdgeCases(Directory dir1, Directory dir4)
      throws JShellFileNotFoundException {
    /* Overwrite */
    runJShellWithUserInput("echo \"this is file 1\" > dir1/file1\n");
    assertEquals("this is file 1", dir1.findFile("file1")
        .getContents());

    /* Append */
    runJShellWithUserInput("echo \"this is file 4\""
        + " >> ././../dir1/dir3/dir4/file4\n");
    assertEquals("this is file 4\nthis is file 4",
        dir4.findFile("file4").getContents());

    /* Simply print */
    runJShellWithUserInput("echo \"this is just a print\"\n");
    assertPrintMessageEquals("this is just a print");

    /* Echo on dir */
    runJShellWithUserInput("echo \"echo on dir\" > dir1\n");
    assertPrintMessageEquals("dir1: Is not a file.");

    /* Path invalid */
    runJShellWithUserInput("mkdir dir1/file1/dir5\n");
    assertPrintMessageEquals("Path dir1/file1/dir5 is not valid.");

    /* Path invalid */
    runJShellWithUserInput("echo \"invalid path\" >> dir1/file1/file5\n");
    assertPrintMessageEquals("Path dir1/file1/file5 is not valid.");

    /* File not found */
    runJShellWithUserInput("echo \"file not found\" >> ./dir/file\n");
    assertPrintMessageEquals("dir: No such file or directory.");
  }

  @Test
  public void testJShellRunCGrep() {
    /* Non-recursive */
    runJShellWithUserInput("grep \"file\" dir1/file1 dir2/file2\n");
    assertPrintMessageEquals("this is file 1\nthis is file 2");

    runJShellWithUserInput("grep \"1\" dir1/file1 dir2/file2");
    assertPrintMessageEquals("this is file 1");

    runJShellWithUserInput("grep \"file\" dir1");
    assertPrintMessageEquals("dir1: Is not a file.");

    /* Recursive */
    runJShellWithUserInput("grep -R \"file\" .");
    assertPrintMessageEquals("/dir1/file1:this is file 1\n"
        + "/dir1/dir3/file3:this is file 3\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir2/file2:this is file 2");

    runJShellWithUserInput("grep -r \"4\" dir1");
    assertPrintMessageEquals("/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4");

    runJShellWithUserInput("grep -R \"file\" dir1 dir2/file2\n");
    assertPrintMessageEquals("/dir1/file1:this is file 1\n"
        + "/dir1/dir3/file3:this is file 3\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir2/file2:this is file 2");

    /* Test for regex */
    runJShellWithUserInput("grep -R \".*fi.*\" dir1 dir2/file2\n");
    assertPrintMessageEquals("/dir1/file1:this is file 1\n"
        + "/dir1/dir3/file3:this is file 3\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir2/file2:this is file 2");

    runJShellWithUserInput("grep -R \".*\" dir1 dir2/file2\n");
    assertPrintMessageEquals("/dir1/file1:this is file 1\n"
        + "/dir1/dir3/file3:this is file 3\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir2/file2:this is file 2");

    runJShellWithUserInput("grep -R \"[a-zA-Z0-9]+\" dir1 dir2/file2\n");
    assertPrintMessageEquals("/dir1/file1:this is file 1\n"
        + "/dir1/dir3/file3:this is file 3\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir2/file2:this is file 2");

    runJShellWithUserInput("grep -R \"this\\sis"
        + "\\sfile\\s\\d\" dir1 dir2/file2\n");
    assertPrintMessageEquals("/dir1/file1:this is file 1\n"
        + "/dir1/dir3/file3:this is file 3\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir1/dir3/dir4/file4:this is file 4\n"
        + "/dir2/file2:this is file 2");
  }

  @Test
  public void testJShellRunDGetCommand()
      throws JShellFileNotFoundException {
    /* Get file */
    runJShellWithUserInput("get http://www.cs.cmu.edu/"
        + "~spok/grimmtmp/073.txt > wrongFile\n");
    assertFalse(rootDir.contains("wrongFile"));
    assertTrue(rootDir.contains("073.txt"));
    assertTrue(rootDir.findFile("073.txt").getContents()
        .startsWith("There was once a king who had an illness"));

    /* Get file replace existing file */
    runJShellWithUserInput("echo \"this is fake\" > archive.asc\n");
    assertTrue(rootDir.contains("archive.asc"));
    assertTrue(rootDir.findFile("archive.asc").getContents()
        .equals("this is fake"));

    runJShellWithUserInput("get http://mozilla.debian.net/archive.asc\n");
    assertTrue(rootDir.contains("archive.asc"));
    assertTrue(rootDir.findFile("archive.asc").getContents()
        .startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----"));

    /* Get file replace existing dir */
    runJShellWithUserInput("mkdir wget_7.html\n");
    assertTrue(rootDir.contains("wget_7.html"));
    assertTrue(rootDir.findFile("wget_7.html") instanceof Directory);

    runJShellWithUserInput("get http://www.editcorp.com/personal/"
        + "lars_appel/wget/v1/wget_7.html\n");
    assertTrue(rootDir.contains("wget_7.html"));
    assertTrue(rootDir.findFile("wget_7.html").getContents()
        .startsWith("<HTML>"));

    /* Invalid URL */
    runJShellWithUserInput("get a\n");
    assertPrintMessageEquals("a: Is not a valid URL.");
  }

  @Test
  public void testJShellRunECdPwd() {
    runJShellWithUserInput("cd / >> wrongFile\npwd \n");
    assertFalse(rootDir.contains("wrongFile"));
    assertPrintMessageEquals("/");

    runJShellWithUserInput("cd .\npwd\n");
    assertPrintMessageEquals("/");

    runJShellWithUserInput("cd ..\npwd\n");
    assertPrintMessageEquals("/");

    runJShellWithUserInput("cd dir1\npwd\n");
    assertPrintMessageEquals("/dir1/\n/dir1");

    runJShellWithUserInput("cd ./dir1/dir3\npwd\n");
    assertPrintMessageEquals("/dir1/dir3/\n/dir1/dir3");

    runJShellWithUserInput("cd /dir1/dir3/dir4\npwd\n");
    assertPrintMessageEquals("/dir1/dir3/dir4/\n/dir1/dir3/dir4");

    runJShellWithUserInput("cd dir1/dir3/dir4/../../../dir2/.\npwd\n");
    assertPrintMessageEquals("/dir2/\n/dir2");
  }

  @Test
  public void testJShellRunFPushdPopd() {
    runJShellWithUserInput("pushd dir1\npopd >> wrongFile\n");
    assertFalse(rootDir.contains("wrongFile"));
    assertPrintMessageMatches(".*/dir1/# /#.*");

    runJShellWithUserInput("pushd /dir1/dir3/dir4\npopd\n");
    assertPrintMessageMatches(".*/dir1/dir3/dir4/# /#.*");

    runJShellWithUserInput("cd dir1/dir3/dir4\npushd /\npopd\n");
    assertPrintMessageMatches(".*/# /dir1/dir3/dir4/#.*");

    runJShellWithUserInput("popd\n");
    assertPrintMessageEquals("popd: Direcotry stack empty.");
  }

  @Test
  public void testJShellRunGLs() {
    /* Non-recursive */
    runJShellWithUserInput("ls\n");
    assertPrintMessageEquals("073.txt archive.asc dir1 dir2 wget_7.html");

    runJShellWithUserInput("ls dir1 dir2 dir1/dir3 dir1/dir3/dir4\n");
    assertPrintMessageEquals("dir1:\ndir3 file1\n\ndir2:\nfile2\n\n"
        + "dir1/dir3:\ndir4 file3\n\ndir1/dir3/dir4:\nfile4");

    runJShellWithUserInput("ls dir1/file1\n");
    assertPrintMessageEquals("dir1/file1");

    runJShellWithUserInput("mkdir dir5\nls dir5\n");
    assertPrintMessageEquals("dir5:");

    /* Recursive */
    runJShellWithUserInput("ls -r\n");
    assertPrintMessageEquals("073.txt archive.asc "
        + "dir1 dir2 dir5 wget_7.html\n\n" + "./dir1:\ndir3 file1"
        + "\n\n./dir1/dir3:\ndir4 file3\n\n."
        + "/dir1/dir3/dir4:\nfile4\n\n./dir2:\nfile2\n\n./dir5:");

    runJShellWithUserInput("ls -R .\n");
    assertPrintMessageEquals(".:\n073.txt archive.asc"
        + " dir1 dir2 dir5 wget_7.html\n\n" + "./dir1:\ndir3 file1"
        + "\n\n./dir1/dir3:\ndir4 file3\n\n./dir1/"
        + "dir3/dir4:\nfile4\n\n./dir2:\nfile2\n\n./dir5:");
  }

  @Test
  public void testJShellRunHCat() {
    runJShellWithUserInput("cat dir1/file1\n");
    assertPrintMessageEquals("this is file 1");

    runJShellWithUserInput("cat /dir2/file2\n");
    assertPrintMessageEquals("this is file 2");

    runJShellWithUserInput("cat ./dir1/dir3/file3\n");
    assertPrintMessageEquals("this is file 3");

    runJShellWithUserInput("cat dir1/dir3/dir4/file4\n");
    assertPrintMessageEquals("this is file 4\nthis is file 4");

    /* Cat on dir */
    runJShellWithUserInput("cat dir1\n");
    assertPrintMessageEquals("dir1: Is not a file.");
  }

  @Test
  public void testJShellRunIGMan() {
    runJShellWithUserInput("man man\n");
    assertPrintMessageStartWith("man CMD");

    runJShellWithUserInput("man cd\n");
    assertPrintMessageStartWith("cd DIR");

    /* Cat on dir */
    runJShellWithUserInput("man nonExistCommand\n");
    assertPrintMessageEquals("No manual entry for nonExistCommand");
  }

  @Test
  public void testJShellRunJExit() {
    runJShellWithUserInput("exit\n");
  }

  @Test
  public void testJShellRunKMvCat()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    runJShellWithUserInput("mv dir2 dir1 >> wrongFile\n");
    assertFalse(rootDir.contains("wrongFile"));
    assertFalse(rootDir.contains("dir2"));
    assertTrue(rootDir.contains("dir1"));
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1",
        rootDir)).contains("dir2"));
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1/dir2",
        rootDir)).contains("file2"));

    runJShellWithUserInput("mv /dir1/dir2/file2 /\n");
    assertFalse(((Directory) fileSystem.getFileGivenPath(
        "/dir1/dir2", rootDir)).contains("file2"));
    assertTrue(rootDir.contains("file2"));

    testMvCmdEdgeCases();
  }

  private void testMvCmdEdgeCases()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    /* Rename file */
    runJShellWithUserInput("mv /file2 dir1/../newFile2\n");
    assertTrue(rootDir.contains("newFile2"));
    runJShellWithUserInput("cat newFile2\n");
    assertPrintMessageEquals("this is file 2");

    /* Rename dir */
    runJShellWithUserInput("mv dir1/dir2 dir1/../newDir2\n");
    assertTrue(rootDir.contains("newDir2"));
    assertFalse(((Directory) fileSystem.getFileGivenPath("newDir2",
        rootDir)).contains("file2"));
    assertFalse(((Directory) fileSystem.getFileGivenPath("newDir2",
        rootDir)).contains("newFile2"));

    /* Move dir to file */
    runJShellWithUserInput("mv newDir2 newFile2\n");
    assertPrintMessageEquals("newFile2: Is not a directory.");

    /* Move dir to its child */
    runJShellWithUserInput("mv dir1 /dir1/dir3/dir4\n");
    assertPrintMessageStartWith("Cannot move/copy dir1 to dir4");

    runJShellWithUserInput("mkdir dir2\n");
    runJShellWithUserInput("mkdir dir2/newFile2\n");
    runJShellWithUserInput("echo \"this is a not a file\" >>"
        + " dir2/newDir2\n");

    /* Try replacing file with dir */
    runJShellWithUserInput("mv newFile2 dir2\n");
    assertPrintMessageEquals("newFile2: Is not a directory.");

    /* Try replacing dir with file */
    runJShellWithUserInput("mv newDir2 dir2\n");
    assertPrintMessageEquals("dir2/newDir2: Is not a directory.");
  }

  @Test
  public void testJShellRunLCpCat()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    runJShellWithUserInput("cp dir1/dir3/file3 /dir1 > wrongFile\n");
    assertFalse(rootDir.contains("wrongFile"));
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1/dir3",
        rootDir)).contains("file3"));
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1",
        rootDir)).contains("file3"));
    runJShellWithUserInput("cat dir1/dir3/file3\n");
    assertPrintMessageEquals("this is file 3");
    runJShellWithUserInput("cat /dir1/../dir1/./file3\n");
    assertPrintMessageEquals("this is file 3");

    runJShellWithUserInput("cp dir2 dir1\n");
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1",
        rootDir)).contains("dir2"));
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1/dir2",
        rootDir)).contains("newFile2"));
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1/dir2",
        rootDir)).contains("newDir2"));
    assertTrue(rootDir.contains("dir2"));

    testCpCmdEdgeCases();
  }

  private void testCpCmdEdgeCases()
      throws JShellPathInvalidException, JShellFileNotFoundException {
    /* Move dir to its child */
    runJShellWithUserInput("cp dir1 /dir1/dir3/dir4\n");
    assertPrintMessageStartWith("Cannot move/copy dir1 to dir4");

    /* Rename dir */
    runJShellWithUserInput("cp dir1 newDir1\n");
    assertTrue(rootDir.contains("dir1"));
    assertTrue(rootDir.contains("newDir1"));

    /* Rename dir */
    runJShellWithUserInput("cp dir1/file1 newFile1\n");
    assertTrue(((Directory) fileSystem.getFileGivenPath("/dir1/",
        rootDir)).contains("file1"));
    assertTrue(rootDir.contains("newFile1"));
    assertEquals(fileSystem.getFileGivenPath("dir1/file1", rootDir)
        .getContents(), rootDir.findFile("newFile1").getContents());

    /* Try replacing file with dir */
    runJShellWithUserInput("cp newFile2 dir2\n");
    assertPrintMessageEquals("newFile2: Is not a directory.");

    /* Try replacing dir with file */
    runJShellWithUserInput("cp newDir2 dir2\n");
    assertPrintMessageEquals("dir2/newDir2: Is not a directory.");

    /* Copy dir to file */
    runJShellWithUserInput("cp newDir1 newFile1\n");
    assertPrintMessageEquals("newFile1: Is not a directory.");
  }

  @Test
  public void testJShellRunMHistory() {
    runJShellWithUserInput("cd dir1\ncommand 1\nls\nhistory\n");
    assertPrintMessageStartWith("1 cd dir1\n"
        + "2 command 1\n3 ls\n4 history\n");

    runJShellWithUserInput("cd dir1\ncommand 1\nls\nhistory 2\n");
    assertPrintMessageStartWith("3 ls\n4 history 2\n");

    runJShellWithUserInput("cd dir1\ncommand 1\nls\nhistory 100\n");
    assertPrintMessageStartWith("1 cd dir1\n2 "
        + "command 1\n3 ls\n4 history 100\n");

    runJShellWithUserInput("cd dir1\ncommand 1\nls\nhistory 0\n");
    assertPrintMessageEquals("/dir1");
  }

  @Test
  public void testJShellRunNRecall() {
    runJShellWithUserInput("cd dir1\ncommand 1\nls\ncd /\n!1\n");
    assertPrintMessageEquals("cd dir1\n/dir1");

    runJShellWithUserInput("cd dir1\ncommand 1\nls\ncd /\n!3\n");
    assertPrintMessageEquals("ls\n073.txt archive.asc"
        + " dir1 dir2 dir5 newDir1 "
        + "newDir2 newFile1 newFile2 wget_7.html");

    runJShellWithUserInput("cd dir1\ncommand \t\t1\nls\ncd /\n!2\n");
    assertPrintMessageEquals("command \t\t1\ncommand: Command not found.");

    runJShellWithUserInput("cd dir1\ncommand 1\nls\ncd /\n!5\n");
    assertPrintMessageEquals("!5: event not found");

    runJShellWithUserInput("cd dir1\ncommand 1\nls\ncd /\n!a\n");
    assertPrintMessageEquals("!a: event not found");
  }

  @Test
  public void testJShellRunORedirection()
      throws JShellFileNotFoundException {
    /* Redirect cat command */
    runJShellWithUserInput("cat newFile1 > catFile\n");
    assertTrue(rootDir.contains("catFile"));
    assertEquals(rootDir.findFile("newFile1").getContents(), rootDir
        .findFile("catFile").getContents());

    runJShellWithUserInput("cat newFile1 >> catFile\n");
    assertTrue(rootDir.contains("catFile"));
    assertEquals(rootDir.findFile("newFile1").getContents() + "\n"
        + rootDir.findFile("newFile1").getContents(), rootDir
        .findFile("catFile").getContents());

    /* Redirect grep command */
    runJShellWithUserInput("echo \"another line\" >> "
        + "newFile1\ngrep \"another\" newFile1 > grepFile\n");
    assertEquals("another line", rootDir.findFile("grepFile")
        .getContents());

    /* Redirect history command */
    runJShellWithUserInput("hahahaCommand\nhistory 2 >> historyFile");
    assertEquals("1 hahahaCommand\n2 history 2 >> historyFile",
        rootDir.findFile("historyFile").getContents());

    /* Redirect ls command */
    runJShellWithUserInput("ls dir1 > lsFile");
    assertEquals("dir1:\ndir2 dir3 file1 file3",
        rootDir.findFile("lsFile").getContents());

    /* Redirect man command */
    runJShellWithUserInput("man man >> manFile");
    assertEquals("man CMD\n\nPrint documentation for CMD.", rootDir
        .findFile("manFile").getContents());

    /* Redirect pwd command */
    runJShellWithUserInput("pwd > pwdFile");
    assertEquals("/", rootDir.findFile("pwdFile").getContents());

    /* Redirect recall command */
    runJShellWithUserInput("ls dir1\n!1 > recallFile");
    assertEquals("dir1:\ndir2 dir3 file1 file3",
        rootDir.findFile("recallFile").getContents());
  }

  /**
   * Assert print message equals to
   * 
   * @param printMsg is the expected print message
   */
  private void assertPrintMessageEquals(String printMsg) {
    String[] lines = outContent.toString().split("/#");
    String secondLastLine = lines[lines.length - 2].trim();
    assertEquals(printMsg, secondLastLine);
  }

  /**
   * Assert print message matches pattern
   * 
   * @param printMsgRegex is the given regex pattern
   */
  private void assertPrintMessageMatches(String printMsgRegex) {
    assertTrue(outContent.toString().matches(printMsgRegex));
  }

  /**
   * Assert print message starts with
   * 
   * @param printMsg is the expected prefix
   */
  private void assertPrintMessageStartWith(String printMsg) {
    String[] lines = outContent.toString().split("/#");
    String secondLastLine = lines[lines.length - 2].trim();
    assertTrue(secondLastLine.startsWith(printMsg));
  }

  /**
   * Run JShell with user input
   * 
   * @param userInput is the simulated user input
   */
  private void runJShellWithUserInput(String userInput) {
    if (!userInput.endsWith("\n")) {
      userInput = userInput + "\n";
    }
    userInput = userInput + ExitCommand.CMD_NAME + "\n";
    readMockUserInput(userInput);
    jShell = new JShell();
    jShell.run();
  }

  /**
   * Read the simulated user input as System in
   * 
   * @param userInput is the simulated user input
   */
  private void readMockUserInput(String userInput) {
    ByteArrayInputStream in =
        new ByteArrayInputStream(userInput.getBytes());
    System.setIn(in);
  }
}
