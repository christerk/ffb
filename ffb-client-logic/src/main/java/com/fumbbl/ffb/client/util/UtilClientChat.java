package com.fumbbl.ffb.client.util;
import javax.swing.text.JTextComponent;

/**
 * Utility methods for chat components.
 *
 * Provides helpers for text insertion and mention checks used by
 * autocomplete and message parsing.
 *
 * @author Garcangel
 */
public final class UtilClientChat {

  private UtilClientChat() {}

  public static String replaceRange(String text, int start, int end, String insertion) {
    String prefix = text.substring(0, start);
    String suffix = text.substring(end);
    return prefix + insertion + suffix;
  }

  public static void applyInsertion(JTextComponent comp, int start, int end, String insertion) {
    String newText = replaceRange(comp.getText(), start, end, insertion);
    comp.setText(newText);
    comp.setCaretPosition(start + insertion.length());
  }
}
