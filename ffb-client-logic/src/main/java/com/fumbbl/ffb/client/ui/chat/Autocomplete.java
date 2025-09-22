package com.fumbbl.ffb.client.ui.chat;

import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Autocomplete popup for chat input.
 *
 * Listens to changes in the input field and shows a popup with matching
 * emojis, mentions, or commands. A mouse click or keyboard commit
 * replaces the current token in the input with the selected suggestion.
 *
 * @author Garcangel
 */
public class Autocomplete {

  private final DimensionProvider dimensionProvider;
  private final JTextComponent input;
  private final JPopupMenu popup;
  private final JList<String> list;
  private final DefaultListModel<String> model;
  private final JScrollPane scrollPane;
  private final AutocompleteGenerator generator;

  private static final int MAX_ROWS_VISIBLE = 8;

  private int padding;
  private int popupWidth;
  private int rowHeight;

  public Autocomplete(JTextComponent input, FantasyFootballClient client, DimensionProvider dimensionProvider, IconCache iconCache) {
    this.input = input;
    this.dimensionProvider = dimensionProvider;
    this.generator = new AutocompleteGenerator(client);

    model = new DefaultListModel<>();
    list = new JList<>(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setVisibleRowCount(MAX_ROWS_VISIBLE);
    list.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setIcon(iconCache.getEmojiIcon(EmojiLookup.getPath((String) value), Component.AUTOCOMPLETE_ICON, dimensionProvider));
        setText((String) value);
        return this;
      }
    });

    popup = new JPopupMenu();
    scrollPane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    popup.add(scrollPane);

    layout();

    list.setFocusable(false);
    list.setRequestFocusEnabled(false);
    scrollPane.setFocusable(false);
    scrollPane.setRequestFocusEnabled(false);
    popup.setFocusable(false);
    popup.setRequestFocusEnabled(false);

    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          commit();
        }
      }
    });

    input.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) { scheduleUpdate(); }
      @Override
      public void removeUpdate(DocumentEvent e) { scheduleUpdate(); }
      @Override
      public void changedUpdate(DocumentEvent e) {}
    });

    input.addCaretListener(e -> scheduleUpdate());
  }

  public boolean isVisible() {
    return popup.isVisible();
  }

  public void hide() {
    popup.setVisible(false);
  }

  public void update() {
    try {
      List<String> matches = generator.getSuggestions(input.getText(), input.getCaretPosition());
      if (matches.isEmpty()) {
        hide();
        return;
      }

      model.clear();
      matches.forEach(model::addElement);
      list.setSelectedIndex(0);

      int rows = Math.min(model.getSize(), MAX_ROWS_VISIBLE);
      int height = rows * rowHeight + padding;
      Dimension size = new Dimension(popupWidth, height);
      popup.setPopupSize(size);

      Rectangle caretRect = input.modelToView(input.getCaretPosition());
      Point position = new Point(caretRect.x, caretRect.y - size.height - padding);
      popup.show(input, position.x, position.y);

      SwingUtilities.invokeLater(() -> input.requestFocusInWindow());
    } catch (BadLocationException ignored) {
      hide();
    }
  }

  public void moveSelection(int delta) {
    int size = model.getSize();
    int currentIndex = list.getSelectedIndex();
    int nextIndex = (currentIndex + delta + size) % size;

    list.setSelectedIndex(nextIndex);
    list.ensureIndexIsVisible(nextIndex);
  }

  public void commit() {
    if (!isVisible()) return;

    String selected = list.getSelectedValue();
    if (selected != null) {
      generator.applySelection(input, selected);
    }
    hide();
  }

  public void refresh() {
    layout();
  }

  private void scheduleUpdate() {
    SwingUtilities.invokeLater(this::update);
  }

  private void layout() {
    padding = dimensionProvider.scale(2);
    popupWidth = dimensionProvider.dimension(Component.AUTOCOMPLETE_POPUP).width;
    rowHeight = dimensionProvider.dimension(Component.AUTOCOMPLETE_ICON).width + padding;

    list.setFont(input.getFont());
    list.setFixedCellHeight(rowHeight);
    popup.setBorder(BorderFactory.createLineBorder(input.getForeground()));
  }
}
