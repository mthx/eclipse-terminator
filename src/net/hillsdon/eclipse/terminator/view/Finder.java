package net.hillsdon.eclipse.terminator.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JLabel;

import terminator.view.JTerminalPane;
import terminator.view.TerminalView;
import terminator.view.highlight.FindHighlighter;
import e.util.PatternUtilities;

/**
 * 
 * @author mth
 */
public class Finder {

  private final EventThreads _eventThreads;
  private final JTerminalPane _terminalPane;
  private String _currentRegularExpression = "";

  public Finder(final EventThreads eventThreads, final JTerminalPane terminalPane) {
    _eventThreads = eventThreads;
    _terminalPane = terminalPane;
  }
  
  public void find(String regularExpression, final FindCallback callback) throws PatternSyntaxException {
    if (regularExpression == null) {
      regularExpression = "";
    }
    if (_currentRegularExpression.equals(regularExpression)) {
      return;
    }
    _currentRegularExpression = regularExpression;
    final Pattern pattern = createPattern(regularExpression);
    if (pattern == null) {
      callback.statusChanged("");
    }
    doFind(regularExpression, callback, pattern);
  }

  private void doFind(final String regularExpression, final FindCallback callback, final Pattern pattern) {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        final TerminalView terminalView = _terminalPane.getTerminalView();
        final FindHighlighter findHighlighter = terminalView.getHighlighterOfClass(FindHighlighter.class);
        final JLabel hack = new JLabel();
        hack.addPropertyChangeListener("text", new PropertyChangeListener() {
          public void propertyChange(final PropertyChangeEvent evt) {
            _eventThreads.runSWTFromSwing(new Runnable() {
              public void run() {
                callback.statusChanged(((String) evt.getNewValue()));
              }
            });
          }
        });
        findHighlighter.setPattern(terminalView, regularExpression, pattern, hack);
      }
    });
  }

  private Pattern createPattern(final String regularExpression) {
    if (regularExpression == null) {
      return null;
    }
    final String trimmed = regularExpression.trim();
    if (trimmed.length() == 0) {
      return null;
    }
    return PatternUtilities.smartCaseCompile(trimmed);
  }
  
  public void findPrevious() {
    _terminalPane.getTerminalView().findPrevious(FindHighlighter.class);
  }

  public void findNext() {
    _terminalPane.getTerminalView().findNext(FindHighlighter.class);
  }

}
