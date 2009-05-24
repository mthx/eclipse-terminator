/*******************************************************************************
 * Copyright (c) 2008 Matthew Hillsdon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hillsdon <matt@hillsdon.net>
 *******************************************************************************/
package net.hillsdon.eclipse.terminator.preferences;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import net.hillsdon.eclipse.terminator.TerminatorPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import terminator.TerminatorPreferences;
import e.util.GuiUtilities;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

  // Copied from TerminatorPrefererences.  Perhaps we should model 'ColorScheme' explicitly.
  private static final Color LIGHT_BLUE = new Color(0xb3d4ff);
  private static final Color NEAR_BLACK = new Color(0x181818);

  private static final Map<String, Boolean> BOOLEAN_PREFERENCE_DEFAULTS = new LinkedHashMap<String, Boolean>();
  static {
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.ANTI_ALIAS, true);
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.BLINK_CURSOR, false);
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.BLOCK_CURSOR, true);
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.VISUAL_BELL, true);
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.HIDE_MOUSE_WHEN_TYPING, true);
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.SCROLL_ON_KEY_PRESS, true);
    BOOLEAN_PREFERENCE_DEFAULTS.put(TerminatorPreferences.SCROLL_ON_TTY_OUTPUT, false);
  }
  
  private static RGB toRGB(final Color color) {
    return new RGB(color.getRed(), color.getGreen(), color.getBlue());
  }
  
  private static final Map<String, RGB> COLOR_PREFERENCE_DEFAULTS = new LinkedHashMap<String, RGB>();
  static {
    COLOR_PREFERENCE_DEFAULTS.put(TerminatorPreferences.FOREGROUND_COLOR, toRGB(NEAR_BLACK));
    COLOR_PREFERENCE_DEFAULTS.put(TerminatorPreferences.BACKGROUND_COLOR, toRGB(Color.WHITE));
    COLOR_PREFERENCE_DEFAULTS.put(TerminatorPreferences.CURSOR_COLOR, toRGB(Color.BLACK));
    COLOR_PREFERENCE_DEFAULTS.put(TerminatorPreferences.SELECTION_COLOR, toRGB(LIGHT_BLUE));
  }

	public void initializeDefaultPreferences() {
		IPreferenceStore store = TerminatorPlugin.getInstance().getPreferenceStore();
		for (Map.Entry<String, Boolean> preference : BOOLEAN_PREFERENCE_DEFAULTS.entrySet()) {
	    store.setDefault(preference.getKey(), preference.getValue());
		}
		PreferenceConverter.setDefault(store, TerminatorPreferences.FONT, getDefaultFont());
		for (Map.Entry<String, RGB> preference : COLOR_PREFERENCE_DEFAULTS.entrySet()) {
      PreferenceConverter.setDefault(store, preference.getKey(), preference.getValue());
    }
	}

  public static void copyFromEclipseToTerminator(final IPreferenceStore eclipsePreferences, final TerminatorPreferences terminatorPreferences) {
    // Editing the preferences fires an event so make sure it happens on the Swing thread. 
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        for (String preference : BOOLEAN_PREFERENCE_DEFAULTS.keySet()) {
          terminatorPreferences.put(preference, eclipsePreferences.getBoolean(preference));
        }
        terminatorPreferences.put(TerminatorPreferences.FONT, fontDataToFont(PreferenceConverter.getFontData(eclipsePreferences, TerminatorPreferences.FONT)));
        for (String preference : COLOR_PREFERENCE_DEFAULTS.keySet()) {
          terminatorPreferences.put(preference, rgbToColor(PreferenceConverter.getColor(eclipsePreferences, preference)));
        }
      }
    });
  }

  private FontData getDefaultFont() {
    return new FontData(GuiUtilities.getMonospacedFontName(), 12, SWT.NORMAL);
  }

  private static Color rgbToColor(final RGB color) {
    return new Color(color.red, color.green, color.blue);
  }

  private static Font fontDataToFont(final FontData fontData) {
    return new Font(fontData.getName(), Font.PLAIN, fontData.getHeight());
  }

}
