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
public class DialogPettyCashParameter implements IDialogParameter {

	private String fTeamId;
	private int fTreasury;
	private int fTeamValue;
	private int fOpponentTeamValue;

	public DialogPettyCashParameter() {
		super();
	}

	public DialogPettyCashParameter(String pTeamId, int pTeamValue, int pTreasury, int pOpponentTeamValue) {
		this();
		fTeamId = pTeamId;
		fTeamValue = pTeamValue;
		fTreasury = pTreasury;
		fOpponentTeamValue = pOpponentTeamValue;
	}

	public DialogId getId() {
		return DialogId.PETTY_CASH;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getTeamValue() {
		return fTeamValue;
	}

	public int getTreasury() {
		return fTreasury;
	}

	public int getOpponentTeamValue() {
		return fOpponentTeamValue;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogPettyCashParameter(getTeamId(), getTeamValue(), getTreasury(), getOpponentTeamValue());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);
		IJsonOption.TREASURY.addTo(jsonObject, fTreasury);
		IJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, fOpponentTeamValue);
		return jsonObject;
	}

	public DialogPettyCashParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fTeamValue = IJsonOption.TEAM_VALUE.getFrom(source, jsonObject);
		fTreasury = IJsonOption.TREASURY.getFrom(source, jsonObject);
		fOpponentTeamValue = IJsonOption.OPPONENT_TEAM_VALUE.getFrom(source, jsonObject);
		return this;
	}

}
