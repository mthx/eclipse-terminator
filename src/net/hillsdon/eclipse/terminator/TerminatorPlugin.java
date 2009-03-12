package net.hillsdon.eclipse.terminator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.UIManager;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import e.util.FileUtilities;

/**
 * The plugin initializes various static terminator settings.
 * 
 * @author mth
 */
public class TerminatorPlugin extends AbstractUIPlugin {

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    
    // This seems to be unstable on Gtk.
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
    // These are all rather dubious...
    System.setProperty("org.jessies.libraryDirectories", "/home/mth/devel/terminator/terminator/.generated/i386_Linux/lib/");
    System.setProperty("org.jessies.terminator.logDirectory", "/home/local/mth/tmp");
    System.setProperty("org.jessies.terminator.optionsFile", "");
    System.setProperty("org.jessies.terminator.signalMap", "29:POLL30:PWR7:BUS11:SEGV9:KILL31:SYS15:TERM6:IOT1:HUP19:STOP5:TRAP2:INT27:PROF24:XCPU17:CLD13:PIPE8:FPE26:VTALRM29:IO28:WINCH21:TTIN20:TSTP6:ABRT22:TTOU3:QUIT10:USR117:CHLD18:CONT4:ILL12:USR225:XFSZ23:URG14:ALRM0:EXIT");
    
    // Install the terminfo.  See bin/terminator.
    String termInfoDirName = System.getenv("TERMINFO");
    if (termInfoDirName == null) {
      termInfoDirName = System.getProperty("user.home") + File.separatorChar + ".terminfo";
    }
    File termInfoDir = new File(termInfoDirName);
    installTermInfoIn(new File(termInfoDir, "t"));
    installTermInfoIn(new File(termInfoDir, "74"));
  }

  private void installTermInfoIn(File directory) throws IOException {
    if (directory.exists() || directory.mkdirs()) {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = getClass().getResourceAsStream("resources/terminfo");
        out = new FileOutputStream(new File(directory, "terminator"));
        // We don't depend on anything useful like commons-io.
        byte[] buffer = new byte[2048];
        int read;
        while ((read = in.read(buffer)) != -1) {
         out.write(buffer, 0, read); 
        }
      }
      finally {
        FileUtilities.close(out);
        FileUtilities.close(in);
      }
    }
  }

}
