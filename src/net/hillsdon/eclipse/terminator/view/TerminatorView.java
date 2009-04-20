package net.hillsdon.eclipse.terminator.view;

import java.util.UUID;

import net.hillsdon.eclipse.terminator.view.actions.CopyAction;
import net.hillsdon.eclipse.terminator.view.actions.PasteAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.IHandlerService;
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
    final IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
    final JTerminalPane terminalPane = JTerminalPane.newShellWithName(null, getWorkingDirectoryFromViewId(secondaryId));
    final EventThreads eventThreads = new EventThreads(parent, terminalPane);
    final ViewTerminatorHost host = new ViewTerminatorHost(this, eventThreads);
    _embedding = new TerminatorEmbedding(terminalPane, host, eventThreads);
    _embedding.install(parent);
    
    addAction(new CopyAction(window, _embedding));
    addAction(new PasteAction(window, _embedding));
  }
  
  /**
   * We want a context menu rather than the view menu but I can't figure out how
   * to get it to display over the Swing arey on Linux (apparently it works on Windows). 
   */
  private void addAction(final IAction action) {
    final IHandlerService service = (IHandlerService) getViewSite().getService(IHandlerService.class);
    final IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
    menuManager.add(action);
    service.activateHandler(action.getActionDefinitionId(), new ActionHandler(action));
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