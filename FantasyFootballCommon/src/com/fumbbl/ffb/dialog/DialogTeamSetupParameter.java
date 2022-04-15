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
public class DialogTeamSetupParameter implements IDialogParameter {

	private boolean fLoadDialog;
	private final List<String> fSetupNames;

	public DialogTeamSetupParameter() {
		fSetupNames = new ArrayList<>();
	}

	public DialogTeamSetupParameter(boolean pLoadDialog, String[] pSetupNames) {
		this();
		fLoadDialog = pLoadDialog;
		add(pSetupNames);
	}

	public DialogId getId() {
		return DialogId.TEAM_SETUP;
	}

	public boolean isLoadDialog() {
		return fLoadDialog;
	}

	public String[] getSetupNames() {
		return fSetupNames.toArray(new String[fSetupNames.size()]);
	}

	private void add(String pSetupName) {
		if (StringTool.isProvided(pSetupName)) {
			fSetupNames.add(pSetupName);
		}
	}

	private void add(String[] pSetupNames) {
		if (ArrayTool.isProvided(pSetupNames)) {
			for (String setupError : pSetupNames) {
				add(setupError);
			}
		}
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogTeamSetupParameter(isLoadDialog(), getSetupNames());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.LOAD_DIALOG.addTo(jsonObject, fLoadDialog);
		IJsonOption.SETUP_NAMES.addTo(jsonObject, fSetupNames);
		return jsonObject;
	}

	public DialogTeamSetupParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fLoadDialog = IJsonOption.LOAD_DIALOG.getFrom(source, jsonObject);
		add(IJsonOption.SETUP_NAMES.getFrom(source, jsonObject));
		return this;
	}

}
