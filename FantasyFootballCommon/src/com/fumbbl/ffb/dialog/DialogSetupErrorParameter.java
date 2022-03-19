package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class DialogSetupErrorParameter implements IDialogParameter {

	private String fTeamId;
	private final List<String> fSetupErrors;

	public DialogSetupErrorParameter() {
		fSetupErrors = new ArrayList<>();
	}

	public DialogSetupErrorParameter(String pTeamId, String[] pSetupErrors) {
		this();
		fTeamId = pTeamId;
		add(pSetupErrors);
	}

	public DialogId getId() {
		return DialogId.SETUP_ERROR;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public String[] getSetupErrors() {
		return fSetupErrors.toArray(new String[fSetupErrors.size()]);
	}

	private void add(String pSetupError) {
		if (StringTool.isProvided(pSetupError)) {
			fSetupErrors.add(pSetupError);
		}
	}

	private void add(String[] pSetupErrors) {
		if (ArrayTool.isProvided(pSetupErrors)) {
			for (String setupError : pSetupErrors) {
				add(setupError);
			}
		}
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogSetupErrorParameter(getTeamId(), getSetupErrors());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.SETUP_ERRORS.addTo(jsonObject, fSetupErrors);
		return jsonObject;
	}

	public DialogSetupErrorParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		add(IJsonOption.SETUP_ERRORS.getFrom(source, jsonObject));
		return this;
	}

}
