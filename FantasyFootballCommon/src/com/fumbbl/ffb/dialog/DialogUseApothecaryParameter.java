package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogUseApothecaryParameter implements IDialogParameter {

	private String fPlayerId;
	private PlayerState fPlayerState;
	private SeriousInjury fSeriousInjury;

	public DialogUseApothecaryParameter() {
		super();
	}

	public DialogUseApothecaryParameter(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
		fPlayerId = pPlayerId;
		fPlayerState = pPlayerState;
		fSeriousInjury = pSeriousInjury;
	}

	public DialogId getId() {
		return DialogId.USE_APOTHECARY;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public PlayerState getPlayerState() {
		return fPlayerState;
	}

	public SeriousInjury getSeriousInjury() {
		return fSeriousInjury;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogUseApothecaryParameter(getPlayerId(), getPlayerState(), getSeriousInjury());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		return jsonObject;
	}

	public DialogUseApothecaryParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(game, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(game, jsonObject);
		return this;
	}

}