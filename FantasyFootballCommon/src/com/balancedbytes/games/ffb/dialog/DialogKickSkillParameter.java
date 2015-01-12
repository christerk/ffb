package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogKickSkillParameter implements IDialogParameter {
  
  private String fPlayerId;
  private FieldCoordinate fBallCoordinate;
  private FieldCoordinate fBallCoordinateWithKick;

  public DialogKickSkillParameter() {
    super();
  }
  
  public DialogKickSkillParameter(String pPlayerId, FieldCoordinate pBallCoordinate, FieldCoordinate pBallCoordinateWithKick) {
    fPlayerId = pPlayerId;
    fBallCoordinate = pBallCoordinate;
    fBallCoordinateWithKick = pBallCoordinateWithKick;
  }
  
  public DialogId getId() {
    return DialogId.KICK_SKILL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public FieldCoordinate getBallCoordinate() {
    return fBallCoordinate;
  }

  public FieldCoordinate getBallCoordinateWithKick() {
    return fBallCoordinateWithKick;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogKickSkillParameter(getPlayerId(), FieldCoordinate.transform(getBallCoordinate()), FieldCoordinate.transform(getBallCoordinateWithKick()));
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.BALL_COORDINATE.addTo(jsonObject, fBallCoordinate);
    IJsonOption.BALL_COORDINATE_WITH_KICK.addTo(jsonObject, fBallCoordinateWithKick);
    return jsonObject;
  }
  
  public DialogKickSkillParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(jsonObject);
    fBallCoordinateWithKick = IJsonOption.BALL_COORDINATE_WITH_KICK.getFrom(jsonObject);
    return this;
  }

}
