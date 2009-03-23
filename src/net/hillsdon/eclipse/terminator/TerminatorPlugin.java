package net.hillsdon.eclipse.terminator;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import terminator.Terminator;
import terminator.TerminatorPreferences;
import e.util.FileUtilities;

/**
 * The plugin initializes various files and static terminator settings.
 * 
 * Much of this corresponds to the ruby in Terminator's bin/terminator.
 * 
 * We don't try to support anything other than Linux at the moment.
 * 
 * @author mth
 */
public class TerminatorPlugin extends AbstractUIPlugin {
  
  public static final String ID = "net.hillsdon.eclipse.terminator";

  // Copied from TerminatorPrefererences.  Perhaps we should model 'ColorScheme' explicitly.
  private static final Color LIGHT_BLUE = new Color(0xb3d4ff);
  private static final Color NEAR_BLACK = new Color(0x181818);
  
  private static final String HOME_DIR = System.getProperty("user.home");
  private static final File DOT_DIR = new File(HOME_DIR, ".terminator");

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);

    DOT_DIR.mkdir();
    
    initializeNativeLibraries();
    initializeLogging();
    initializeSignalMap();
    initializeTermInfo();
    initializePreferences();
  }

  private void initializeSignalMap() {
    // This is not possible to get right from Java.  See the ruby in bin/terminator.
    // Only used to improve reporting of received signals, "" is fine (unset -> NPE).
    System.setProperty("org.jessies.terminator.signalMap", "");
  }

  /**
   * Issues:
   *  The "74" is not needed unless we support Mac.
   *  Perhaps it would be better to not overwrite?
   */
  private void initializeTermInfo() throws IOException {
    String termInfoDirName = System.getenv("TERMINFO");
    if (termInfoDirName == null) {
      termInfoDirName = HOME_DIR + File.separatorChar + ".terminfo";
    }
    final File termInfoDir = new File(termInfoDirName);
    installTermInfoIn(new File(termInfoDir, "t"));
    installTermInfoIn(new File(termInfoDir, "74"));
  }

  /**
   * Issues:
   *  Linux only.
   *  Make the library loading code more flexible so we can use OSGI's Bundle-NativeCode.
   */
  private void initializeNativeLibraries() throws IOException {
    final File libsDir = createDotSubDirectory("libs", true);
    System.setProperty("org.jessies.libraryDirectories", libsDir.toString());
    copyLibToLibsDir(libsDir, "libsalma-hayek.so");
    copyLibToLibsDir(libsDir, "libpty.so");
  }

  /**
   * Issues:
   *  We can't get at the pid from here so names don't match (do we care?)
   *  We should probably use the Eclipse error log for things that are likely bugs.
   *  Does terminator ever clean up these files?
   */
  private void initializeLogging() {
    try {
      final File logsDir = createDotSubDirectory("logs", true);
      System.setProperty("org.jessies.terminator.logDirectory", logsDir.toString());
      final File logFile = File.createTempFile("terminator-", ".log", logsDir);
      System.setProperty("e.util.Log.filename", logFile.toString());
    }
    catch (IOException ex) {
      // It'll end up on the console then.
    }
  }
  
  private File createDotSubDirectory(final String name, final boolean secure) throws IOException {
    final File dir = new File(DOT_DIR, name);
    if (!dir.exists()) {
      if (!dir.mkdir()) {
        throw new IOException("Failed to create " + dir);
      }
    }
    if (secure) {
      chmod700(dir);
    }
    return dir;
  }

  private void chmod700(final File file) {
    // Is this for real?  Note we don't check errors for now.
    file.setExecutable(false, false);
    file.setReadable(false, false);
    file.setWritable(false, false);
    file.setExecutable(true, true);
    file.setReadable(true, true);
    file.setWritable(true, true);
  }

  private void initializePreferences() {
    final File optionsFile = new File(DOT_DIR, "options");
    System.setProperty("org.jessies.terminator.optionsFile", optionsFile.toString());
    TerminatorPreferences preferences = Terminator.getPreferences();
    // Change the default color scheme to one that fits in better within the workspace.
    preferences.put(TerminatorPreferences.BACKGROUND_COLOR, Color.WHITE);
    preferences.put(TerminatorPreferences.FOREGROUND_COLOR, NEAR_BLACK);
    preferences.put(TerminatorPreferences.CURSOR_COLOR, Color.BLUE);
    preferences.put(TerminatorPreferences.SELECTION_COLOR, LIGHT_BLUE);
    preferences.readFromDisk();
    // Initialize the preferences.  Without this Ctrl-L/D etc don't work.
    Terminator.getSharedInstance().optionsDidChange();
  }
  
  private void installTermInfoIn(final File directory) throws IOException {
    if (directory.exists() || directory.mkdirs()) {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = getClass().getResourceAsStream("resources/terminfo");
        out = new FileOutputStream(new File(directory, "terminator"));
        copy(in, out);
      }
      finally {
        FileUtilities.close(out);
        FileUtilities.close(in);
      }
    }
  }

  private void copyLibToLibsDir(final File libDir, final String libName) throws IOException {
    FileOutputStream out = new FileOutputStream(new File(libDir, libName));
    InputStream in = getClass().getResourceAsStream("/" + libName);
    try {
      copy(in, out);
    }
    finally {
      FileUtilities.close(out);
      FileUtilities.close(in);
    }
  }

  /**
   * We don't depend on anything useful like commons-io.
   */
  private void copy(final InputStream in, final OutputStream out) throws IOException {
    byte[] buffer = new byte[2048];
    int read;
    while ((read = in.read(buffer)) != -1) {
     out.write(buffer, 0, read); 
    }
  }

}
