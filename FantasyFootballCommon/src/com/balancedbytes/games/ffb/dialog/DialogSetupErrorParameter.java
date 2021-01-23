package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogSetupErrorParameter implements IDialogParameter {

	private String fTeamId;
	private List<String> fSetupErrors;

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

	public DialogSetupErrorParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		add(IJsonOption.SETUP_ERRORS.getFrom(game, jsonObject));
		return this;
	}

}
