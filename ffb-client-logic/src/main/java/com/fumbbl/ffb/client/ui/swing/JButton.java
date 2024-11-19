package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

import javax.swing.*;

public class JButton extends javax.swing.JButton {
  public JButton(DimensionProvider dimensionProvider, String text, RenderContext renderContext) {
    super(text);
    dimensionProvider.scaleFont(this, renderContext);
  }

  public JButton(DimensionProvider dimensionProvider, RenderContext renderContext) {
    super();
    dimensionProvider.scaleFont(this, renderContext);
  }

  public JButton(DimensionProvider dimensionProvider, Icon icon, RenderContext renderContext) {
    super(icon);
    dimensionProvider.scaleFont(this, renderContext);
  }

  public JButton(DimensionProvider dimensionProvider, String text, int mnemonic, RenderContext renderContext) {
    super(text + " ( " + (char) mnemonic + " )");
    dimensionProvider.scaleFont(this, renderContext);
  }

}
