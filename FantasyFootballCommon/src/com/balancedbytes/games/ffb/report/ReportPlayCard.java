package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportPlayCard implements IReport {
  
  private String fTeamId;
  private Card fCard;
  private String fPlayerId;
  
  public ReportPlayCard() {
    super();
  }

  public ReportPlayCard(String pTeamId, Card pCard) {
  	fTeamId = pTeamId;
  	fCard = pCard;
  }
  
  public ReportPlayCard(String pTeamId, Card pCard, String pCatcherId) {
  	this(pTeamId, pCard);
    fPlayerId = pCatcherId;
  }
  
  public ReportId getId() {
    return ReportId.PLAY_CARD;
  }
  
  public String getTeamId() {
	  return fTeamId;
  }
  
  public Card getCard() {
	  return fCard;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  // transformation
  
  public IReport transform() {
    return new ReportPlayCard(getTeamId(), getCard(), getPlayerId());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fCard = new CardFactory().forId(pByteArray.getSmallInt());
    fPlayerId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.CARD.addTo(jsonObject, fCard);
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    return jsonObject;
  }
  
  public ReportPlayCard initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fCard = (Card) IJsonOption.CARD.getFrom(jsonObject);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    return this;
  }    
    
}
