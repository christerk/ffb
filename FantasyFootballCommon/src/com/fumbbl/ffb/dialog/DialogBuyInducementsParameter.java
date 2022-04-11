package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogBuyInducementsParameter implements IDialogParameter {

	private String fTeamId;
	private int fAvailableGold;

	public DialogBuyInducementsParameter() {
		super();
	}

	public DialogBuyInducementsParameter(String pTeamId, int pAvailableGold) {
		fTeamId = pTeamId;
		fAvailableGold = pAvailableGold;
	}

	public DialogId getId() {
		return DialogId.BUY_INDUCEMENTS;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getAvailableGold() {
		return fAvailableGold;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogBuyInducementsParameter(getTeamId(), getAvailableGold());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.AVAILABLE_GOLD.addTo(jsonObject, fAvailableGold);
		return jsonObject;
	}

	public DialogBuyInducementsParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fAvailableGold = IJsonOption.AVAILABLE_GOLD.getFrom(source, jsonObject);
		return this;
	}

}
