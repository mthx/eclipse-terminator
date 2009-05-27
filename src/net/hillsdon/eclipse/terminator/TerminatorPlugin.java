package net.hillsdon.eclipse.terminator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.hillsdon.eclipse.terminator.preferences.PreferenceInitializer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jessies.os.OS;
import org.osgi.framework.BundleContext;

import terminator.Terminator;
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
  
  static {
    if (OS.isWindows()) {
      // The terminator native libraries depend on this.  Because we're 
      // using Bundle-Native code we need to be sure to load it first.
      System.loadLibrary("mingwm10");
    }
  }
  
  public static final String ID = "net.hillsdon.eclipse.terminator";

  private static final String HOME_DIR = System.getProperty("user.home");

  private static TerminatorPlugin _instance;

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    _instance = this;
    
    initializeLogging();
    initializeSignalMap();
    initializeTermInfo();
    initializePreferences();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    _instance = null;
    super.stop(context);
  }

  public static TerminatorPlugin getInstance() {
    return _instance;
  }
  
  private void initializeLogging() {
    // Ideally we'd redirect this logging to the Eclipse error log - need to 
    // give the logging system in salma-hayek pluggable backends.
    System.setProperty("e.util.Log.filename", OS.isWindows() ? "nul" : "/dev/null");
    // This logs all terminal output.  I'm not happy enabling this without UI
    // that makes it really obvious what's going on, so disable for now.
    System.setProperty("org.jessies.terminator.logDirectory", "");
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

  private void initializePreferences() {
    // We'll fail to read from it but that doesn't matter.
    System.setProperty("org.jessies.terminator.optionsFile", "");
    final IPreferenceStore store = getPreferenceStore();
    store.addPropertyChangeListener(new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        PreferenceInitializer.copyFromEclipseToTerminator(store, Terminator.getPreferences());
      }
    });
    PreferenceInitializer.copyFromEclipseToTerminator(store, Terminator.getPreferences());
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
