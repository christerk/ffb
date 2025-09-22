package com.fumbbl.ffb.client.ui.swing;

import javax.swing.text.*;

/**
 * Editor kit that forces line wrapping in JTextPane.
 *
 * Replaces the default LabelView with WrapLabelView so long 
 * words break instead of extending beyond the viewport.
 *
 * @author Garcangel
 */
public class WrappingEditorKit extends StyledEditorKit {

  @Override
  public ViewFactory getViewFactory() {
    return elem -> {
      if (AbstractDocument.ContentElementName.equals(elem.getName())) {
        return new WrapLabelView(elem);
      }
      return super.getViewFactory().create(elem);
    };
  }

  private static final class WrapLabelView extends LabelView {
    WrapLabelView(Element e) { super(e); }

    @Override
    public float getMinimumSpan(int axis) {
      return axis == View.X_AXIS ? 0f : super.getMinimumSpan(axis);
    }
  }
}
