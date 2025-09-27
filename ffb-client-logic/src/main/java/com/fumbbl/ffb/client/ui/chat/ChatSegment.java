package com.fumbbl.ffb.client.ui.chat;

import javax.swing.ImageIcon;

import com.fumbbl.ffb.client.TextStyle;

/**
 * Represents a single chat segment: styled text, an icon (with offset),
 * or a newline marker.
 * 
 * @author Garcangel
 */
public class ChatSegment {
  public final String text;
  public final TextStyle style;
  public final ImageIcon icon;
  public final int offset;

  public ChatSegment(TextStyle style, String text) {
    this.style = (style == null ? TextStyle.NONE : style);
    this.text = text;
    this.icon = null;
    this.offset = 0;
  }

  public ChatSegment(ImageIcon icon, int offset) {
    this.style = TextStyle.NONE;
    this.text = null;
    this.icon = icon;
    this.offset = offset;
  }

  public static ChatSegment newline() {
    return new ChatSegment(TextStyle.NONE, null);
  }
}