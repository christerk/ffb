package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DialogReRollRegenerationMultipleParameter implements IDialogParameter {

	private final List<String> playerIds = new ArrayList<>();
	private InducementType inducementType;
	private final List<Inducement> reRollOptions = new ArrayList<>();

	public DialogReRollRegenerationMultipleParameter() {
		super();
	}

	public DialogReRollRegenerationMultipleParameter(List<String> playerIds, InducementType inducementType) {
		this(playerIds, inducementType, null);
	}

	public DialogReRollRegenerationMultipleParameter(List<String> playerIds, List<Inducement> reRollOptions) {
		this(playerIds, null, reRollOptions);
	}

	private DialogReRollRegenerationMultipleParameter(List<String> playerIds, InducementType inducementType,
		List<Inducement> reRollOptions) {
		if (playerIds != null) {
			this.playerIds.addAll(playerIds);
		}
		this.inducementType = inducementType;
		if (reRollOptions != null) {
			this.reRollOptions.addAll(reRollOptions);
		}
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

	public List<Inducement> getReRollOptions() {
		return reRollOptions;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollRegenerationMultipleParameter(playerIds, inducementType, reRollOptions);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		if (inducementType != null) {
			IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, inducementType);
		}
		JsonArray reRollOptionsArray = new JsonArray();
		reRollOptions.stream().map(Inducement::toJsonValue).forEach(reRollOptionsArray::add);
		IJsonOption.RE_ROLL_OPTIONS.addTo(jsonObject, reRollOptionsArray);
		return jsonObject;
	}

	public DialogReRollRegenerationMultipleParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		playerIds.addAll(Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject)));
		inducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		JsonArray reRollOptionsArray = IJsonOption.RE_ROLL_OPTIONS.getFrom(source, jsonObject);
		if (reRollOptionsArray != null) {
			reRollOptionsArray.values().stream().map(value -> new Inducement().initFrom(source, value))
				.forEach(reRollOptions::add);
		}
		return this;
	}

}
