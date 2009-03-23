package net.hillsdon.eclipse.terminator.view;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import terminator.view.JTerminalPane;
import terminator.view.TerminalPaneHost;

/**
 * A JTerminal pane that can be installed in a SWT composite.
 * 
 * @author mth
 */
public class TerminatorEmbedding {

  private final JTerminalPane _terminalPane;
  private final TerminalPaneHost _host;
  private final EventThreads _eventThreads;
  private Composite _composite;

  public TerminatorEmbedding(final JTerminalPane terminal, final TerminalPaneHost host, final EventThreads eventThreads) {
    _terminalPane = terminal;
    _host = host;
    _eventThreads = eventThreads;
  }
  
  public void install(final Composite parent) {
    parent.setLayout(new FillLayout());

    _composite = new Composite(parent, SWT.NO_BACKGROUND | SWT.EMBEDDED);
    final Frame frame = SWT_AWT.new_Frame(_composite);
    // This is probably too strong but <tab> can't be our focus traversal
    // key else tab completion in the shell loses the focus.
    frame.setFocusTraversalKeysEnabled(false);
    _terminalPane.setPopupMenuEnabled(false);
    _terminalPane.setHost(_host);
    frame.add(_terminalPane);
    _terminalPane.start();

    _composite.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        _eventThreads.runSwingFromSWT(new Runnable() {
          public void run() {
            _terminalPane.requestFocus();
          }
        });
      }
    });
  }

  public void dispose() {
    _eventThreads.runSwingFromSWT(new Runnable() {
      @Override
      public void run() {
        _terminalPane.doCloseAction();
      }
    });
  }

  public void setFocus() {
    _composite.setFocus();
  }
  
}
