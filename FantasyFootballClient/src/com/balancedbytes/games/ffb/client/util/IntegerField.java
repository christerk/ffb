package com.balancedbytes.games.ffb.client.util;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * This class is a <CODE>TextField</CODE> that only allows integer
 * values to be entered into it.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
@SuppressWarnings("serial")
public class IntegerField extends JTextField
{
  /**
   * Default ctor.
   */
  public IntegerField()
  {
    super();
  }

  /**
   * Ctor specifying the field width.
   *
   * @param cols  Number of columns.
   */
  public IntegerField(int cols)
  {
    super(cols);
  }

  /**
   * Retrieve the contents of this field as an <TT>int</TT>.
   *
   * @return  the contents of this field as an <TT>int</TT>.
   */
  public int getInt()
  {
    final String text = getText();
    if (text == null || text.length() == 0)
    {
      return 0;
    }
    return Integer.parseInt(text);
  }

  /**
   * Set the contents of this field to the passed <TT>int</TT>.
   *
   * @param value The new value for this field.
   */
  public void setInt(int value)
  {
    setText(String.valueOf(value));
  }

  /**
   * Create a new document model for this control that only accepts
   * integral values.
   *
   * @return  The new document model.
   */
  protected Document createDefaultModel()
  {
    return new IntegerDocument();
  }

  /**
   * This document only allows integral values to be added to it.
   */
  static class IntegerDocument extends PlainDocument
  {
    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException
    {
      if (str != null)
      {
        try
        {
          Integer.decode(str);
          super.insertString(offs, str, a);
        }
        catch (NumberFormatException ex)
        {
          Toolkit.getDefaultToolkit().beep();
        }
      }
    }
  }
  
}
