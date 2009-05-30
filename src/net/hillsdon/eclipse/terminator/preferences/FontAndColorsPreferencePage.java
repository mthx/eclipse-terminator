package net.hillsdon.eclipse.terminator.preferences;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import terminator.TerminatorPreferences;

public class FontAndColorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public static final String ID = "net.hillsdon.eclipse.terminator.preferences.ColorsPreferencePage";

  public FontAndColorsPreferencePage() {
    super(GRID); 
    setPreferenceStore(TerminatorPlugin.getInstance().getPreferenceStore());
  }

  public void createFieldEditors() {
    final Composite parent = getFieldEditorParent();
    addField(new FontFieldEditor(TerminatorPreferences.FONT, "Terminal Font:", parent));
    // The Terminator preference's dialog is much better here, with previewed presets.
    addField(new ColorFieldEditor(TerminatorPreferences.FOREGROUND_COLOR, "Foreground color:", parent));
    addField(new ColorFieldEditor(TerminatorPreferences.BACKGROUND_COLOR, "Background color:", parent));
    addField(new ColorFieldEditor(TerminatorPreferences.CURSOR_COLOR, "Cursor color:", parent));
    addField(new ColorFieldEditor(TerminatorPreferences.SELECTION_COLOR, "Selection color:", parent));
  }

  public void init(final IWorkbench workbench) {
  }

}