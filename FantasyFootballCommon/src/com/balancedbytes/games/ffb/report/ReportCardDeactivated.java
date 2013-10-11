package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportCardDeactivated implements IReport {
  
  private Card fCard;
  
  public ReportCardDeactivated() {
    super();
  }

  public ReportCardDeactivated(Card pCard) {
  	fCard = pCard;
  }
  
  public ReportId getId() {
    return ReportId.CARD_DEACTIVATED;
  }
  
  public Card getCard() {
	  return fCard;
  }

  // transformation
  
  public IReport transform() {
    return new ReportCardDeactivated(getCard());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt((getCard() != null) ? getCard().getId() : 0);
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCard = new CardFactory().forId(pByteArray.getSmallInt());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.CARD.addTo(jsonObject, fCard);
    return jsonObject;
  }
  
  public ReportCardDeactivated initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fCard = (Card) IJsonOption.CARD.getFrom(jsonObject);
    return this;
  }
    
}
