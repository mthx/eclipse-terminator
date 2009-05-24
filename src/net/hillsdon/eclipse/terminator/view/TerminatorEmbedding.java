package net.hillsdon.eclipse.terminator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

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
import org.jessies.os.Posix;

import terminator.TerminatorPreferences;
import terminator.view.JTerminalPane;
import terminator.view.TerminalPaneHost;
import e.util.Preferences;
import e.util.ProcessUtilities;

/**
 * A JTerminal pane that can be installed in a SWT composite.
 * 
 * Note that methods are expected to be called from the SWT thread
 * but will be run on the Swing thread.  To reduce the chance of
 * deadlock this is done asyncronously so clients cannot rely on
 * the timing of these actions wrt SWT events.
 * 
 * @author mth
 */
public class TerminatorEmbedding {

  /**
   * This is the only way I can make Page-Up/Down navigation work. 
   */
  private final class RepostNavigationEventsWithSwtControlFocused implements Listener {
    public void handleEvent(final Event e) {
      if (isEclipseHandledNavigationEvent(e) && !_swtFocusControl.isFocusControl()) {
        if (_swtFocusControl.forceFocus()) {
          Display.getCurrent().post(e);
        }
      }
    }
  }

  private final EventThreads _eventThreads;
  private final TerminalPaneHost _host;
  private final JTerminalPane _terminalPane;
  private final Set<PasteEnabledListener> _pasteEnabledListeners = new LinkedHashSet<PasteEnabledListener>();
  private final TerminatorPreferences _preferences;
  private final Preferences.Listener _preferencesListener = new Preferences.Listener() {
    public void preferencesChanged() {
      System.err.println(_preferences.getFont("font"));
      _terminalPane.optionsDidChange();
    }
  };
  private Composite _composite;
  private Button _swtFocusControl;
  

  public TerminatorEmbedding(final JTerminalPane terminal, final TerminalPaneHost host, final TerminatorPreferences preferences, final EventThreads eventThreads) {
    _terminalPane = terminal;
    _host = host;
    _preferences = preferences;
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
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout());
    frame.add(panel, BorderLayout.CENTER);
    panel.add(_terminalPane);
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
    
    _preferences.addPreferencesListener(_preferencesListener);
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

  public String getCwdOfTerminalIfPossible() {
    // As in JTerminalPane.newShellHere().
    int fd = _terminalPane.getControl().getPtyProcess().getFd();
    int foregroundPid = Posix.tcgetpgrp(fd);
    if (foregroundPid < 0) {
      return null;
    }
    String cwd = ProcessUtilities.findCurrentWorkingDirectory(foregroundPid);
    // The impl. doesn't canonicalise, which doesn't help when we restore... 
    if (cwd.startsWith("/proc/")) {
      try {
        return new File(cwd).getCanonicalPath();
      }
      catch (IOException ex) {
        return null;
      }
    }
    return cwd;
  }

  public void copy() {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        _terminalPane.doCopyAction();
      }
    });
  }
  
  public void paste() {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        _terminalPane.doPasteAction();
      }
    });
  }
  
  public void clearScrollback() {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        _terminalPane.getTerminalView().getModel().clearScrollBuffer();
      }
    });
  }

  private Clipboard getSwingClipboard() {
    return Toolkit.getDefaultToolkit().getSystemClipboard();
  }
  
  public void addPasteEnabledListener(final PasteEnabledListener listener) {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        getSwingClipboard().addFlavorListener(new FlavorListener() {
          public void flavorsChanged(final FlavorEvent event) {
            firePasteEnabledChangedSwing();
          }
        });
        firePasteEnabledChangedSwing();
      }
    });
    _pasteEnabledListeners.add(listener);
  }
  
  private void firePasteEnabledChangedSwing() {
    final boolean newEnablement = getSwingClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
    _eventThreads.runSWTFromSwing(new Runnable() {
      public void run() {
        for (PasteEnabledListener listener : _pasteEnabledListeners) {
          listener.pasteEnablementChanged(newEnablement);
        }
      }
    });
  }
  
  public void dispose() {
    _preferences.removePreferencesListener(_preferencesListener);
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
