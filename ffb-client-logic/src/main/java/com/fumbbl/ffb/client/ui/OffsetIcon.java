package com.fumbbl.ffb.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * ImageIcon with a vertical offset applied when painting.
 * 
 * @author Garcangel
 */
public class OffsetIcon extends ImageIcon {
  private final int yOffset;

  public OffsetIcon(Image image, int yOffset) {
    super(image);
    this.yOffset = yOffset;
  }

  @Override
  public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
    super.paintIcon(c, g, x, y + yOffset);
  }
}
