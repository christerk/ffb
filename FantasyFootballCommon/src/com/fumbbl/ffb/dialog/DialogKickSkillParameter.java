package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

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

	public DialogKickSkillParameter(String pPlayerId, FieldCoordinate pBallCoordinate,
			FieldCoordinate pBallCoordinateWithKick) {
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
		return new DialogKickSkillParameter(getPlayerId(), FieldCoordinate.transform(getBallCoordinate()),
				FieldCoordinate.transform(getBallCoordinateWithKick()));
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

	public DialogKickSkillParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(source, jsonObject);
		fBallCoordinateWithKick = IJsonOption.BALL_COORDINATE_WITH_KICK.getFrom(source, jsonObject);
		return this;
	}

}
