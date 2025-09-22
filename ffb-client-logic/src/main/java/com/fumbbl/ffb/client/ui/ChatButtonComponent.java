package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.ui.chat.EmojiLookup;
import com.fumbbl.ffb.client.ui.chat.EmojiPicker;
import com.fumbbl.ffb.client.ui.swing.JTextField;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;


/**
 * Chat input button for opening the emoji picker.
 *
 * Displays an emoji button with rollover/pressed icons. Clicking the button
 * toggles the EmojiPicker, positioned relative to the chat pane.
 *
 * @author Garcangel
 */
public class ChatButtonComponent extends JPanel {

  private final JButton button;
  private final DimensionProvider dimensionProvider;
  private final IconCache iconCache;
  private final JTextField chatInput;
  private final FantasyFootballClient fClient;

  private ChatLogTextPane chatTextPane;
  private EmojiPicker picker;

  public ChatButtonComponent(FantasyFootballClient client, JTextField chatInput, DimensionProvider dimensionProvider, IconCache iconCache) {
    this.fClient = client;
    this.chatInput = chatInput;
    this.dimensionProvider = dimensionProvider;
    this.iconCache = iconCache;

    setOpaque(false);
    setLayout(new BorderLayout());

    button = new JButton();
    button.setRolloverEnabled(true);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setOpaque(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setFocusable(false);

    add(button, BorderLayout.CENTER);

    button.addActionListener(e -> togglePicker());
  }

  public void initLayout(ChatLogTextPane chatTextPane) {
    this.chatTextPane = chatTextPane;
    refreshIcons();
    picker = null;
  }

  private void refreshIcons() {
    ImageIcon iconGrey = iconCache.getEmojiIcon(EmojiLookup.PICKER_GREY, Component.EMOJI_PICKER_BUTTON, dimensionProvider);
    ImageIcon iconColor = iconCache.getEmojiIcon(EmojiLookup.PICKER_COLOR, Component.EMOJI_PICKER_BUTTON, dimensionProvider);

    button.setIcon(iconGrey);
    button.setRolloverIcon(iconColor);
    button.setPressedIcon(iconColor);

    button.setPreferredSize(new Dimension(iconGrey.getIconWidth(), iconGrey.getIconHeight()));
  }

  private void togglePicker() {
    if (picker != null && picker.isVisible()) {
      hidePicker();
    } else {
      showPicker();
    }
  }

  private void showPicker() {
    if (picker == null || picker.isClosed()) {
      picker = new EmojiPicker(fClient.getUserInterface(), chatInput);
      fClient.getUserInterface().getDesktop().add(picker);
    }
    positionPicker();
    picker.setVisible(true);
    try {
      picker.setSelected(true);
    } catch (java.beans.PropertyVetoException ignored) {}
  }

  private void hidePicker() {
    if (picker != null) {
      picker.setVisible(false);
    }
  }

  private void positionPicker() {
    Rectangle r = chatTextPane.getVisibleRect();
    Point anchor = SwingUtilities.convertPoint(chatTextPane, r.x + r.width, r.y + r.height,
      fClient.getUserInterface().getDesktop());
    Dimension ps = picker.getPreferredSize();
    picker.setBounds(anchor.x - ps.width, anchor.y - ps.height, ps.width, ps.height);
  }

}
