package net.hillsdon.eclipse.terminator.view.actions;

import net.hillsdon.eclipse.terminator.preferences.FontAndColorsPreferencePage;
import net.hillsdon.eclipse.terminator.preferences.TerminatorPreferencePage;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Shows just terminator's preferences.
 * 
 * @author mth
 */
public class ShowPreferencesAction extends Action {

  public static final String ID = "net.hillsdon.eclipse.terminator.showPreferences";
  
  private static final String[] ALL_PREFERENCE_PAGES = {
    TerminatorPreferencePage.ID,
    FontAndColorsPreferencePage.ID,
  };
  
  private final IWorkbenchWindow _window;
  
  public ShowPreferencesAction(final IWorkbenchWindow window) {
    _window = window;
    setText("Preferences");
    setActionDefinitionId(ID);
  }

  @Override
  public void run() {
    PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
        _window.getShell(), TerminatorPreferencePage.ID, ALL_PREFERENCE_PAGES, null);
    dialog.open();
  }

}
