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
public class ClientCommandPasswordChallenge extends ClientCommand {

  private String fCoach;
  
  public ClientCommandPasswordChallenge() {
    super();
  }

  public ClientCommandPasswordChallenge(String pChallenge) {
    fCoach = pChallenge;
  }
 
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PASSWORD_CHALLENGE;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    return jsonObject;
  }
  
  public ClientCommandPasswordChallenge initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    return this;
  }
    
}
