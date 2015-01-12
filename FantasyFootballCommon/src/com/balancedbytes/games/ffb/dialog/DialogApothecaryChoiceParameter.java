package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogApothecaryChoiceParameter implements IDialogParameter {

  private String fPlayerId;
  private PlayerState fPlayerStateOld;
  private SeriousInjury fSeriousInjuryOld;
  private PlayerState fPlayerStateNew;
  private SeriousInjury fSeriousInjuryNew;

  public DialogApothecaryChoiceParameter() {
    super();
  }

  public DialogApothecaryChoiceParameter(
    String pPlayerId,
    PlayerState pPlayerStateOld,
    SeriousInjury pSeriousInjuryOld,
    PlayerState pPlayerStateNew,
    SeriousInjury pSeriousInjuryNew
  ) {
    fPlayerId = pPlayerId;
    fPlayerStateOld = pPlayerStateOld;
    fSeriousInjuryOld = pSeriousInjuryOld;
    fPlayerStateNew = pPlayerStateNew;
    fSeriousInjuryNew = pSeriousInjuryNew;
  }

  public DialogId getId() {
    return DialogId.APOTHECARY_CHOICE;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public PlayerState getPlayerStateOld() {
    return fPlayerStateOld;
  }

  public SeriousInjury getSeriousInjuryOld() {
    return fSeriousInjuryOld;
  }

  public PlayerState getPlayerStateNew() {
    return fPlayerStateNew;
  }

  public SeriousInjury getSeriousInjuryNew() {
    return fSeriousInjuryNew;
  }

  // transformation

  public IDialogParameter transform() {
    return new DialogApothecaryChoiceParameter(getPlayerId(), getPlayerStateOld(), getSeriousInjuryOld(), getPlayerStateNew(), getSeriousInjuryNew());
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, fPlayerStateOld);
    IJsonOption.SERIOUS_INJURY_OLD.addTo(jsonObject, fSeriousInjuryOld);
    IJsonOption.PLAYER_STATE_NEW.addTo(jsonObject, fPlayerStateNew);
    IJsonOption.SERIOUS_INJURY_NEW.addTo(jsonObject, fSeriousInjuryNew);
    return jsonObject;
  }
  
  public DialogApothecaryChoiceParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerStateOld = IJsonOption.PLAYER_STATE_OLD.getFrom(jsonObject);
    fSeriousInjuryOld = (SeriousInjury) IJsonOption.SERIOUS_INJURY_OLD.getFrom(jsonObject);
    fPlayerStateNew = IJsonOption.PLAYER_STATE_NEW.getFrom(jsonObject);
    fSeriousInjuryNew = (SeriousInjury) IJsonOption.SERIOUS_INJURY_NEW.getFrom(jsonObject);
    return this;
  }

}
