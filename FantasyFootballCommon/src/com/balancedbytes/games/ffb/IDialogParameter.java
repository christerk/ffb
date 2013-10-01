package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;

/**
 * 
 * @author Kalimar
 */
public interface IDialogParameter extends IXmlWriteable, IByteArraySerializable {
  
  public static final String XML_TAG = "dialogParameter";

  public static final String XML_ATTRIBUTE_ID = "id";

  public DialogId getId();
  
  public IDialogParameter transform();
  
}
