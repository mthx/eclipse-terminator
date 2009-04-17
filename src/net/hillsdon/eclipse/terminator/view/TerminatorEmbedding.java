package net.hillsdon.eclipse.terminator.view;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import terminator.view.JTerminalPane;
import terminator.view.TerminalPaneHost;

/**
 * A JTerminal pane that can be installed in a SWT composite.
 * 
 * @author mth
 */
public class TerminatorEmbedding {

  /**
   * This is the only way I can make Page-Up/Down navigation work. 
   */
  private final class RepostNavigationEventsWithSwtControlFocused implements Listener {
    @Override
    public void handleEvent(final Event e) {
      if (isEclipseHandledNavigationEvent(e) && !_swtFocusControl.isFocusControl()) {
        if (_swtFocusControl.forceFocus()) {
          Display.getCurrent().post(e);
        }
      }
    }
  }

  private final JTerminalPane _terminalPane;
  private final TerminalPaneHost _host;
  private final EventThreads _eventThreads;
  private Composite _composite;
  private Button _swtFocusControl;

  public TerminatorEmbedding(final JTerminalPane terminal, final TerminalPaneHost host, final EventThreads eventThreads) {
    _terminalPane = terminal;
    _host = host;
    _eventThreads = eventThreads;
  }
  
  public void install(final Composite parent) {
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    parent.setLayout(layout);
    
    // We need SWT-land to take the focus sometimes so we have a 0x0 (but not invisible) SWT control.
    _swtFocusControl = createInvisibleSwtFocusControl(parent);
    _composite = new Composite(parent, SWT.NO_BACKGROUND | SWT.EMBEDDED);
    _composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    _composite.addListener(SWT.KeyDown, new RepostNavigationEventsWithSwtControlFocused());
    
    final Frame frame = SWT_AWT.new_Frame(_composite);
    // This is probably too strong but <tab> can't be our focus traversal key else tab completion in the shell loses the focus.
    frame.setFocusTraversalKeysEnabled(false);
    frame.add(_terminalPane);
    _terminalPane.start(_host);

    _composite.addFocusListener(new FocusAdapter() {
      public void focusGained(final FocusEvent e) {
        _eventThreads.runSwingFromSWT(new Runnable() {
          public void run() {
            _terminalPane.requestFocus();
          }
        });
      }
    });
  }
  
  private boolean isEclipseHandledNavigationEvent(Event keyboardEvent) {
    return (keyboardEvent.keyCode == SWT.PAGE_UP || keyboardEvent.keyCode == SWT.PAGE_DOWN) && (keyboardEvent.stateMask & SWT.CONTROL) != 0;
  }

  private Button createInvisibleSwtFocusControl(final Composite parent) {
    Button swtFocusControl = new Button(parent, SWT.NONE);
    GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false);
    data.heightHint = 0;
    data.widthHint = 0;
    swtFocusControl.setLayoutData(data);
    return swtFocusControl;
  }

  /**
   * This implementation uses the /proc filesystem and will only work on Linux.
   */
  public String getCwdOfTerminalIfPossible() {
    return resolvePidSymlink(("/proc/" + getPtyProcessPid() + "/cwd"));
  }

  private long getPtyProcessPid() {
    return _terminalPane.getTerminalView().getTerminalControl().getPtyProcess().getPid();
  }

  private String resolvePidSymlink(final String pidDir) {
    if (pidDir == null) {
      return null;
    }
    File cwdLink = new File(pidDir);
    if (cwdLink.exists()) {
      try {
        return cwdLink.getCanonicalFile().toString();
      }
      catch (IOException nevermind) {
      }
    }
    return null;
  }
  
  public void dispose() {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        _terminalPane.doCloseAction();
      }
    });
  }

  public void setFocus() {
    _composite.setFocus();
  }
  
}
