package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class KnockoutRecovery implements IJsonSerializable {
  
  private String fPlayerId;
  private boolean fRecovering;
  private int fRoll;
  private int fBloodweiserBabes;
  
  public KnockoutRecovery() {
    super();
  }

  public KnockoutRecovery(String pPlayerId, boolean pRecovering, int pRoll, int pBloodweiserBabes) {
    fPlayerId = pPlayerId;
    fRecovering = pRecovering;
    fRoll = pRoll;
    fBloodweiserBabes = pBloodweiserBabes;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isRecovering() {
    return fRecovering;
  }

  public int getRoll() {
    return fRoll;
  }
  
  public int getBloodweiserBabes() {
    return fBloodweiserBabes;
  }
    
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.RECOVERING.addTo(jsonObject, fRecovering);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.BLOODWEISER_BABES.addTo(jsonObject, fBloodweiserBabes);
    return jsonObject;
  }
  
  public KnockoutRecovery initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fRecovering = IJsonOption.RECOVERING.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fBloodweiserBabes = IJsonOption.BLOODWEISER_BABES.getFrom(jsonObject);
    return this;
  }

}
