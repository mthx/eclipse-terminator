package net.hillsdon.eclipse.terminator.view;

import terminator.view.JTerminalPane;
import terminator.view.TerminalView;
import terminator.view.highlight.FindHighlighter;
import terminator.view.highlight.FindStatusDisplay;

/**
 * 
 * @author mth
 */
public class Finder {

  private final EventThreads _eventThreads;
  private final JTerminalPane _terminalPane;

  public Finder(final EventThreads eventThreads, final JTerminalPane terminalPane) {
    _eventThreads = eventThreads;
    _terminalPane = terminalPane;
  }
  
  public void find(final String regularExpression, final FindStatusDisplay findStatusDisplay) {
    _eventThreads.runSwingFromSWT(new Runnable() {
      public void run() {
        final TerminalView terminalView = _terminalPane.getTerminalView();
        final FindHighlighter findHighlighter = terminalView.getHighlighterOfClass(FindHighlighter.class);
        findHighlighter.setPattern(terminalView, regularExpression, findStatusDisplay);
      }
    });
  }

  public void findPrevious() {
    _terminalPane.getTerminalView().findPrevious(FindHighlighter.class);
  }

  public void findNext() {
    _terminalPane.getTerminalView().findNext(FindHighlighter.class);
  }

}
