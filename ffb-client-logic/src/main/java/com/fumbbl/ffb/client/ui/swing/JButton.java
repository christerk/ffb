package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.*;

public class JButton extends javax.swing.JButton {
  public JButton(DimensionProvider dimensionProvider, String text) {
    super(text);
    dimensionProvider.scaleFont(this);
  }

  public JButton(DimensionProvider dimensionProvider) {
    super();
    dimensionProvider.scaleFont(this);
  }

  public JButton(DimensionProvider dimensionProvider, Icon icon) {
    super(icon);
    dimensionProvider.scaleFont(this);
  }

  public JButton(DimensionProvider dimensionProvider, String text, int mnemonic) {
    super(text + " ( " + (char) mnemonic + " )");
    dimensionProvider.scaleFont(this);
  }

}
