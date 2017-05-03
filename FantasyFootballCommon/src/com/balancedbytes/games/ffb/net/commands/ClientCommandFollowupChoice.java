package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandFollowupChoice extends ClientCommand {
  
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
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.CHOICE_FOLLOWUP.addTo(jsonObject, fChoiceFollowup);
    return jsonObject;
  }
  
  public ClientCommandFollowupChoice initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fChoiceFollowup = IJsonOption.CHOICE_FOLLOWUP.getFrom(jsonObject);
    return this;
  }
    
}
