package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public DialogBuyInducementsParameter initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fAvailableGold = IJsonOption.AVAILABLE_GOLD.getFrom(game, jsonObject);
		return this;
	}

}
