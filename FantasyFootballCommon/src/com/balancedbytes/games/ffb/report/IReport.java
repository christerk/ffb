package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public interface IReport extends IByteArrayReadable, IJsonSerializable {
  
  public static final String XML_TAG = "report";
  
  public static final String XML_ATTRIBUTE_ID = "id";

  public ReportId getId();

  public IReport transform();
  
}
