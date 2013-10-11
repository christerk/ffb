package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;

/**
 * 
 * @author Kalimar
 */
public interface IReport extends IByteArraySerializable {
  
  public static final String XML_TAG = "report";
  
  public static final String XML_ATTRIBUTE_ID = "id";

  public ReportId getId();

  public IReport transform();
  
}
