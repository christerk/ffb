package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogConfirmEndActionParameter implements IDialogParameter {

	private String fTeamId;
	private PlayerAction playerAction;

	public DialogConfirmEndActionParameter() {
	}

	public DialogConfirmEndActionParameter(String teamId, PlayerAction playerAction) {
		this();
		setTeamId(teamId);
		this.playerAction = playerAction;
	}

	public DialogId getId() {
		return DialogId.CONFIRM_END_ACTION;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public void setTeamId(String teamId) {
		fTeamId = teamId;
	}

	public PlayerAction getPlayerAction() {
		return playerAction;
	}

	public void setPlayerAction(PlayerAction playerAction) {
		this.playerAction = playerAction;
	}
// transformation

	public IDialogParameter transform() {
		return new DialogConfirmEndActionParameter(getTeamId(), playerAction);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, getTeamId());
		IJsonOption.PLAYER_ACTION.addTo(jsonObject, playerAction);
		return jsonObject;
	}

	public DialogConfirmEndActionParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		setTeamId(IJsonOption.TEAM_ID.getFrom(game, jsonObject));
		playerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(game, jsonObject);
		return this;
	}

}
