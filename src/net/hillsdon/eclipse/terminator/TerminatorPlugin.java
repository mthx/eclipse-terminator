package net.hillsdon.eclipse.terminator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The plugin initializes various static terminator settings.
 * 
 * @author mth
 */
public class TerminatorPlugin extends AbstractUIPlugin {

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    // These are all rather dubious...
    System.setProperty("org.jessies.libraryDirectories", "/home/mth/devel/terminator/terminator/.generated/i386_Linux/lib/");
    System.setProperty("org.jessies.terminator.logDirectory", "/home/local/mth/tmp");
    System.setProperty("org.jessies.terminator.optionsFile", "");
    System.setProperty("org.jessies.terminator.signalMap", "29:POLL30:PWR7:BUS11:SEGV9:KILL31:SYS15:TERM6:IOT1:HUP19:STOP5:TRAP2:INT27:PROF24:XCPU17:CLD13:PIPE8:FPE26:VTALRM29:IO28:WINCH21:TTIN20:TSTP6:ABRT22:TTOU3:QUIT10:USR117:CHLD18:CONT4:ILL12:USR225:XFSZ23:URG14:ALRM0:EXIT");
  }

}
