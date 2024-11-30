package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JTextField;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;

/**
 * This class is a <CODE>TextField</CODE> that only allows integer values to be
 * entered into it.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IntegerField extends JTextField {

	/**
	 * Ctor specifying the field width.
	 *
	 * @param cols Number of columns.
	 */
	public IntegerField(DimensionProvider dimensionProvider, int cols) {
		super(dimensionProvider, cols);
	}

	/**
	 * Retrieve the contents of this field as an <TT>int</TT>.
	 *
	 * @return the contents of this field as an <TT>int</TT>.
	 */
	public int getInt() {
		final String text = getText();
		if (text == null || text.length() == 0) {
			return 0;
		}
		try {
			return Integer.parseUnsignedInt(text);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * Set the contents of this field to the passed <TT>int</TT>.
	 *
	 * @param value The new value for this field.
	 */
	public void setInt(int value) {
		setText(String.valueOf(value));
	}

	/**
	 * Create a new document model for this control that only accepts integral
	 * values.
	 *
	 * @return The new document model.
	 */
	protected Document createDefaultModel() {
		return new IntegerDocument();
	}

	/**
	 * This document only allows integral values to be added to it.
	 */
	static class IntegerDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (str != null) {
				try {
					//noinspection ResultOfMethodCallIgnored
					Integer.decode(str);
					super.insertString(offs, str, a);
				} catch (NumberFormatException ex) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		}
	}

}
