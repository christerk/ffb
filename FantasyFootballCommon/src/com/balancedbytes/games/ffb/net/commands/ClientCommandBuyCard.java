package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ClientCommandBuyCard extends NetCommand {
  
  private CardType fCardType;
  
  public ClientCommandBuyCard() {
    super();
  }
  
  public ClientCommandBuyCard(CardType pCardType) {
    fCardType = pCardType;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_BUY_CARD;
  }
  
  public CardType getCardType() {
	  return fCardType;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CARD_TYPE.addTo(jsonObject, fCardType);
    return jsonObject;
  }
  
  public ClientCommandBuyCard initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fCardType = (CardType) IJsonOption.CARD_TYPE.getFrom(jsonObject);
    return this;
  }

}
