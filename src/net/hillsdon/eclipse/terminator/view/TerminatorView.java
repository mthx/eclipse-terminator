package net.hillsdon.eclipse.terminator.view;

import java.util.UUID;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import terminator.view.JTerminalPane;

/**
 * A view hosting a single terminator pane.
 * 
 * @author mth
 */
public class TerminatorView extends ViewPart {

  public static final String ID = "net.hillsdon.eclipse.terminator.view.TerminatorView";
  
  private TerminatorEmbedding _embedding;

  public String getCwdOfTerminalIfPossible() {
    return _embedding.getCwdOfTerminalIfPossible();
  }

  /**
   * Overriden to increase visibility.
   */
  @Override
  public void setPartName(String partName) {
    super.setPartName(partName);
  }
   
  public void createPartControl(final Composite parent) {
    String secondaryId = getViewSite().getSecondaryId();
    final JTerminalPane terminalPane = JTerminalPane.newShellWithName(null, getWorkingDirectoryFromViewId(secondaryId));
    final EventThreads eventThreads = new EventThreads(parent, terminalPane);
    final ViewTerminatorHost host = new ViewTerminatorHost(this, eventThreads);
    _embedding = new TerminatorEmbedding(terminalPane, host, eventThreads);
    _embedding.install(parent);
  }

  @Override
  public void dispose() {
    _embedding.dispose();
  }

  public void setFocus() {
    _embedding.setFocus();
  }
 
  public static String getWorkingDirectoryFromViewId(final String id) {
    if (id != null) {
      String[] parts = id.split("\\|");
      if (parts.length == 2 && !parts[1].isEmpty()) {
        return parts[1];
      }
    }
    return null;
  }

  public static String createSecondaryId(final String workingDirectory) {
    return UUID.randomUUID().toString() + "|" + (workingDirectory == null ? "" : workingDirectory);
  }
 
}