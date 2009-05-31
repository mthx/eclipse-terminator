package net.hillsdon.eclipse.terminator.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import net.hillsdon.eclipse.terminator.view.actions.ClearScrollbackAction;
import net.hillsdon.eclipse.terminator.view.actions.CopyAction;
import net.hillsdon.eclipse.terminator.view.actions.PasteAction;
import net.hillsdon.eclipse.terminator.view.actions.ShowPreferencesAction;
import net.hillsdon.eclipse.terminator.view.actions.ToggleFindBarAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import terminator.Terminator;
import terminator.TerminatorPreferences;
import terminator.view.JTerminalPane;

/**
 * A view hosting a single terminator pane.
 * 
 * @author mth
 */
public class TerminatorView extends ViewPart {

  public static final String ID = "net.hillsdon.eclipse.terminator.view.TerminatorView";

  private static final String MEMENTO_WORKING_DIRECTORY_KEY = "working-directory";
  
  private TerminatorEmbedding _embedding;
  private String _initialWorkingDirectory;

  /**
   * Overriden to increase visibility.
   */
  @Override
  public void setPartName(String partName) {
    super.setPartName(partName);
  }
   
  public void createPartControl(final Composite parent) {
    final IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
    final TerminatorPreferences preferences = Terminator.getPreferences();
    final JTerminalPane terminalPane = JTerminalPane.newShellWithName(null, _initialWorkingDirectory);
    final EventThreads eventThreads = new EventThreads(parent, terminalPane);
    final ViewTerminatorHost host = new ViewTerminatorHost(this, eventThreads);
    final FindBar findBar = new FindBar(new Finder(eventThreads, terminalPane));
    _embedding = new TerminatorEmbedding(terminalPane, host, preferences, eventThreads, findBar);
    _embedding.install(parent);
    
    addAction(new CopyAction(window, _embedding));
    addAction(new PasteAction(window, _embedding));
    addAction(new ClearScrollbackAction(window, _embedding));
    addAction(new ToggleFindBarAction(window, _embedding));
    addSeparator();
    addAction(new ShowPreferencesAction(window));
  }
  
  @Override
  public void init(final IViewSite site, final IMemento memento) throws PartInitException {
    super.init(site, memento);
    _initialWorkingDirectory = getInitialWorkingDirectory(memento);
  }
  
  @Override
  public void saveState(final IMemento memento) {
    super.saveState(memento);
    memento.putString(MEMENTO_WORKING_DIRECTORY_KEY, getCwdOfTerminalIfPossible());
  }

  private String getInitialWorkingDirectory(final IMemento memento) {
    // First try restore, then new view.
    String directory = memento == null ? null : memento.getString(MEMENTO_WORKING_DIRECTORY_KEY);
    if (directory == null) {
      String secondaryId = getViewSite().getSecondaryId();
      directory = getWorkingDirectoryFromViewId(secondaryId);
    }
    return directory != null && new File(directory).exists() ? directory : null;
  }

  public String getCwdOfTerminalIfPossible() {
    return _embedding.getCwdOfTerminalIfPossible();
  }
  
  /**
   * We want a context menu rather than the view menu but I can't figure out how
   * to get it to display over the Swing area on Linux (apparently it works on Windows). 
   */
  private void addAction(final IAction action) {
    getMenuManager().add(action);
    IHandlerService service = (IHandlerService) getViewSite().getService(IHandlerService.class);
    service.activateHandler(action.getActionDefinitionId(), new ActionHandler(action));
  }

  private void addSeparator() {
    getMenuManager().add(new Separator());
  }
  
  private IMenuManager getMenuManager() {
    return getViewSite().getActionBars().getMenuManager();
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
      if (parts.length == 2) {
        return decodeWorkingDirectory(parts[1]);
      }
    }
    return null;
  }

  public static String createSecondaryId(final String workingDirectory) {
    return UUID.randomUUID().toString() + "|" + encodeWorkingDirectory(workingDirectory);
  }

  private static String encodeWorkingDirectory(final String workingDirectory) {
    // Although it doesn't say on the Javadoc colons in view ids aren't allowed.
    if (workingDirectory == null) {
      return "";
    }
    try {
      return URLEncoder.encode(workingDirectory, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static String decodeWorkingDirectory(final String encodedWorkingDirectory) {
    if (encodedWorkingDirectory.length() == 0) {
      return null;
    }
    try {
      return URLDecoder.decode(encodedWorkingDirectory, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
 
}