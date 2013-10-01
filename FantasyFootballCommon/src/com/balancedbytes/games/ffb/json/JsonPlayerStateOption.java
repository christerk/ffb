package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.PlayerState;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonPlayerStateOption extends JsonAbstractOption {
  
  public JsonPlayerStateOption(String pKey) {
    super(pKey);
  }
  
  public PlayerState getFrom(JsonObject pJsonObject) {
    return asPlayerState(getValueFrom(pJsonObject));
  }
  
  public PlayerState getFrom(JsonObject pJsonObject, PlayerState pDefault) {
    return asPlayerState(getValueFrom(pJsonObject, asJsonValue(pDefault)));
  }

  public void addTo(JsonObject pJsonObject, PlayerState pValue) {
    addValueTo(pJsonObject, asJsonValue(pValue));
  }
  
  private PlayerState asPlayerState(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    return new PlayerState(pJsonValue.asInt());
  }
  
  private JsonValue asJsonValue(PlayerState pPlayerState) {
    if (pPlayerState == null) {
      return JsonValue.NULL;
    }
    return JsonValue.valueOf(pPlayerState.getId());
  }

}
