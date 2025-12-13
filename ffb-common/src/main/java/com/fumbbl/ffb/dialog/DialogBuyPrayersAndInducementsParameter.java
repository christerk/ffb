package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
public class DialogBuyPrayersAndInducementsParameter implements IDialogParameter {

	private String fTeamId;
	private int availableGold, pettyCash, treasury;
	private boolean usesTreasury;

	public DialogBuyPrayersAndInducementsParameter() {
	}

	public DialogBuyPrayersAndInducementsParameter(String teamId, int availableGold, boolean usesTreasury, int pettyCash,
																								 int treasury) {
		this();
		fTeamId = teamId;
		this.availableGold = availableGold;
		this.usesTreasury = usesTreasury;
		this.pettyCash = pettyCash;
		this.treasury = treasury;
	}

	public DialogId getId() {
		return DialogId.BUY_PRAYERS_AND_INDUCEMENTS;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getAvailableGold() {
		return availableGold;
	}

	public boolean isUsesTreasury() {
		return usesTreasury;
	}

	public int getPettyCash() {
		return pettyCash;
	}

	public int getTreasury() {
		return treasury;
	}
// transformation

	public IDialogParameter transform() {
		return new DialogBuyPrayersAndInducementsParameter(getTeamId(), availableGold, usesTreasury, pettyCash, treasury);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.AVAILABLE_GOLD.addTo(jsonObject, availableGold);
		IJsonOption.USES_TREASURY.addTo(jsonObject, usesTreasury);
		IJsonOption.PETTY_CASH.addTo(jsonObject, pettyCash);
		IJsonOption.TREASURY.addTo(jsonObject, treasury);
		return jsonObject;
	}

	public DialogBuyPrayersAndInducementsParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		availableGold = IJsonOption.AVAILABLE_GOLD.getFrom(source, jsonObject);
		if (IJsonOption.USES_TREASURY.isDefinedIn(jsonObject)) {
			usesTreasury = IJsonOption.USES_TREASURY.getFrom(source, jsonObject);
		}
		pettyCash = IJsonOption.PETTY_CASH.getFrom(source, jsonObject);
		treasury = IJsonOption.TREASURY.getFrom(source, jsonObject);
		return this;
	}

}
