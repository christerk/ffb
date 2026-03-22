package com.fumbbl.ffb.dialog;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class DialogReRollRegenerationMultipleParameter implements IDialogParameter {

	private List<String> playerIds;
	private InducementType inducementType;
	private List<Inducement> reRollOptions;

	public DialogReRollRegenerationMultipleParameter() {
		super();
	}

	public DialogReRollRegenerationMultipleParameter(List<String> playerIds, InducementType inducementType) {
		this(playerIds, inducementType, null);
	}

	public DialogReRollRegenerationMultipleParameter(List<String> playerIds, List<Inducement> reRollOptions) {
		this(playerIds, null, Objects.requireNonNull(reRollOptions, "Parameter reRollOptions must not be null."));
	}

	private DialogReRollRegenerationMultipleParameter(List<String> playerIds, InducementType inducementType,
		List<Inducement> reRollOptions) {
		validateReRollData(inducementType, reRollOptions);
		this.playerIds = Objects.requireNonNull(playerIds, "Parameter playerIds must not be null.");
		this.inducementType = inducementType;
		this.reRollOptions = reRollOptions;
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
		return inducementType != null
			? new DialogReRollRegenerationMultipleParameter(playerIds, inducementType)
			: new DialogReRollRegenerationMultipleParameter(playerIds, reRollOptions);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		if (inducementType != null) {
			IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, inducementType);
		}
		if (reRollOptions != null) {
			JsonArray reRollOptionsArray = new JsonArray();
			reRollOptions.stream().map(Inducement::toJsonValue).forEach(reRollOptionsArray::add);
			IJsonOption.RE_ROLL_OPTIONS.addTo(jsonObject, reRollOptionsArray);
		}
		return jsonObject;
	}

	public DialogReRollRegenerationMultipleParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		playerIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		inducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		reRollOptions = null;
		JsonArray reRollOptionsArray = IJsonOption.RE_ROLL_OPTIONS.getFrom(source, jsonObject);
		if (reRollOptionsArray != null) {
			reRollOptions = new ArrayList<>();
			reRollOptionsArray.values().stream().map(value -> new Inducement().initFrom(source, value))
				.forEach(reRollOptions::add);
		}
		validateReRollData(inducementType, reRollOptions);
		return this;
	}

	private void validateReRollData(InducementType inducementType, List<Inducement> reRollOptions) {
		if (inducementType != null && reRollOptions != null) {
			throw new IllegalArgumentException(
				"Cannot specify both inducementType and reRollOptions. Provide at most one; if none is provided, Team Re-Roll is used.");
		}
	}

}
