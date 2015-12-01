package com.balancedbytes.games.ffb.net.commands;

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
public class ClientCommandFollowupChoice extends NetCommand {
  
  private boolean fChoiceFollowup;
  
  public ClientCommandFollowupChoice() {
    super();
  }

  public ClientCommandFollowupChoice(boolean pChoiceReceive) {
    fChoiceFollowup = pChoiceReceive;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_FOLLOWUP_CHOICE;
  }
  
  public boolean isChoiceFollowup() {
    return fChoiceFollowup;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CHOICE_FOLLOWUP.addTo(jsonObject, fChoiceFollowup);
    return jsonObject;
  }
  
  public ClientCommandFollowupChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fChoiceFollowup = IJsonOption.CHOICE_FOLLOWUP.getFrom(jsonObject);
    return this;
  }
    
}
