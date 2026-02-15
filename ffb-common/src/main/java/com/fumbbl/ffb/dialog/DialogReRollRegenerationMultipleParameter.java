package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.Arrays;
import java.util.List;


public class DialogReRollRegenerationMultipleParameter implements IDialogParameter {

	private List<String> playerIds;
	private InducementType inducementType;

	public DialogReRollRegenerationMultipleParameter() {
		super();
	}

	public DialogReRollRegenerationMultipleParameter(List<String> playerIds, InducementType inducementType) {
		this.playerIds = playerIds;
		this.inducementType = inducementType;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_REGENERATION_MULTIPLE;
	}

	public InducementType getInducementType() {
		return inducementType;
	}

	public List<String> getPlayerIds() {
		return playerIds;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollRegenerationMultipleParameter(playerIds, inducementType);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, inducementType);
		return jsonObject;
	}

	public DialogReRollRegenerationMultipleParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		playerIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		inducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		return this;
	}

}
