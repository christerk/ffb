package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.InjuryModificationOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Asks a coach which of several ways to modify an armour or injury roll should be used, if any.
 * The options are sorted by the server and must be shown in that order.
 */
public class DialogInjuryModificationChoiceParameter implements IDialogParameter {

	private String teamId;
	private String defenderId;
	private String message;
	private final List<InjuryModificationOption> options = new ArrayList<>();

	public DialogInjuryModificationChoiceParameter() {
		super();
	}

	public DialogInjuryModificationChoiceParameter(String teamId, String defenderId, String message,
																								 List<InjuryModificationOption> options) {
		this.teamId = teamId;
		this.defenderId = defenderId;
		this.message = message;
		if (options != null) {
			this.options.addAll(options);
		}
	}

	public DialogId getId() {
		return DialogId.INJURY_MODIFICATION_CHOICE;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getDefenderId() {
		return defenderId;
	}

	public String getMessage() {
		return message;
	}

	public List<InjuryModificationOption> getOptions() {
		return options;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogInjuryModificationChoiceParameter(teamId, defenderId, message, options);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		IJsonOption.MESSAGE.addTo(jsonObject, message);
		JsonArray optionArray = new JsonArray();
		options.stream().map(InjuryModificationOption::toJsonValue).forEach(optionArray::add);
		IJsonOption.MODIFIER_OPTIONS.addTo(jsonObject, optionArray);
		return jsonObject;
	}

	public DialogInjuryModificationChoiceParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		message = IJsonOption.MESSAGE.getFrom(source, jsonObject);
		options.clear();
		JsonArray optionArray = IJsonOption.MODIFIER_OPTIONS.getFrom(source, jsonObject);
		if (optionArray != null) {
			for (int i = 0; i < optionArray.size(); i++) {
				options.add(new InjuryModificationOption().initFrom(source, optionArray.get(i)));
			}
		}
		return this;
	}
}
