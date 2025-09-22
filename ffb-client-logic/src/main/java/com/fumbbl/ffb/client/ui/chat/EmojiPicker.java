package com.fumbbl.ffb.client.ui.chat;
import com.fumbbl.ffb.client.Component;

import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.util.UtilClientChat;

import javax.swing.*;
import java.awt.*;

/**
 * Emoji picker window for chat input.
 *
 * Displays a scrollable grid of emoji buttons. Clicking a button inserts
 * the emoji shortcode at the current caret position in the chat input,
 * then closes the picker.
 *
 * @author Garcangel
 */
public class EmojiPicker extends JInternalFrame {

  private final int pad;
  private final int emojiSize;
  private final UiDimensionProvider dimensionProvider;
  private final IconCache iconCache;
  private static final int COLUMNS = 8;
  private static final int VISIBLE_ROWS = 5;

  public EmojiPicker(UserInterface ui, JTextField chatInput) {
    super("Pick Emoji", false, true, false, false);

    this.dimensionProvider = ui.getUiDimensionProvider();
    this.iconCache = ui.getIconCache();
    this.pad = dimensionProvider.scale(1);
    this.emojiSize = dimensionProvider.dimension(Component.EMOJI_PICKER_ICON).width;

    setLayout(new BorderLayout(dimensionProvider.scale(4), dimensionProvider.scale(4)));

    JPanel gridPanel = new JPanel(new GridLayout(0, COLUMNS, pad, pad));

    for (String shortcode : EmojiLookup.getShortcodes()) {
      String path = EmojiLookup.getPath(shortcode);
      JButton btn = createEmojiButton(shortcode, path, chatInput);
      gridPanel.add(btn);
    }

    JScrollPane center = buildScrollPane(gridPanel);
    add(center, BorderLayout.CENTER);

    pack();
    setResizable(false);

  }

  private JButton createEmojiButton(String shortcode, String path, JTextField chatInput) {

    ImageIcon icon = iconCache.getEmojiIcon(path, Component.EMOJI_PICKER_ICON, dimensionProvider);

    JButton btn = new JButton(icon);
    btn.setMargin(new Insets(pad, pad, pad, pad));
    btn.setToolTipText(shortcode);
    btn.addActionListener(e -> {
      insertEmoji(chatInput, shortcode);
      chatInput.requestFocusInWindow();
      setVisible(false);
    });
    return btn;
  }

  private void insertEmoji(JTextField chatInput, String shortcode) {
    int caret = chatInput.getCaretPosition();
    UtilClientChat.applyInsertion(chatInput, caret, caret, shortcode + " ");
  }

  private JScrollPane buildScrollPane(JPanel gridPanel) {
    int rowHeight = emojiSize + (2 * pad) + pad;
    int viewportHeight = VISIBLE_ROWS * rowHeight - pad;
    int gridWidth = gridPanel.getPreferredSize().width;

    JScrollPane scroll = new JScrollPane(gridPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    int page = rowHeight * VISIBLE_ROWS;

    scroll.setWheelScrollingEnabled(true);
    scroll.getVerticalScrollBar().setUnitIncrement(rowHeight / 2);
    scroll.getVerticalScrollBar().setBlockIncrement(page);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getViewport().setPreferredSize(new Dimension(gridWidth, viewportHeight));

    return scroll;
  }
  
}
