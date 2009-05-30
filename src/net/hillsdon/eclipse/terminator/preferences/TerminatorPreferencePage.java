package net.hillsdon.eclipse.terminator.preferences;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import terminator.TerminatorPreferences;

public class TerminatorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public TerminatorPreferencePage() {
    super(GRID);
    setPreferenceStore(TerminatorPlugin.getInstance().getPreferenceStore());
  }

  public void createFieldEditors() {
    final Composite parent = getFieldEditorParent();
    addField(new BooleanFieldEditor(TerminatorPreferences.ANTI_ALIAS, "Anti-alias text", parent));
    addField(new BooleanFieldEditor(TerminatorPreferences.BLINK_CURSOR, "Blink cursor", parent));
    addField(new BooleanFieldEditor(TerminatorPreferences.BLOCK_CURSOR, "Block cursor", parent));
    addField(new BooleanFieldEditor(TerminatorPreferences.VISUAL_BELL, "Visual bell (as opposed to no bell)", parent));
    addField(new BooleanFieldEditor(TerminatorPreferences.HIDE_MOUSE_WHEN_TYPING, "Hide mouse when typing", parent));
    addField(new BooleanFieldEditor(TerminatorPreferences.SCROLL_ON_KEY_PRESS, "Scroll to bottom on key press", parent));
    addField(new BooleanFieldEditor(TerminatorPreferences.SCROLL_ON_TTY_OUTPUT, "Scroll to bottom on output", parent));
  }

  public void init(final IWorkbench workbench) {
  }

}