package net.hillsdon.eclipse.terminator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

import e.util.LogWriter;

/**
 * Adapts the salma-hayek log to the Eclipse ILog interface.
 * 
 * @author mth
 */
public class PluginLogWriter implements LogWriter {

  private final ILog _log;

  public PluginLogWriter() {
    this(TerminatorPlugin.getInstance().getLog());
  }
  
  PluginLogWriter(final ILog log) {
    _log = log;
  }

  public void log(final String message, final Throwable throwable) {
    // This is just a heuristic, conceivably there are bad things that have no
    // throwable, but there are definitely run-of-the-mill things that don't.
    final int status = throwable == null ? Status.OK : Status.ERROR;
    _log.log(new Status(status, TerminatorPlugin.ID, message, throwable));
  }

}
