package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogSwarmingPlayersParameter implements IDialogParameter {

	private int amount;
	private boolean restrictPlacement;

	@Override
	public DialogId getId() {
		return DialogId.SWARMING;
	}

	public DialogSwarmingPlayersParameter() {
	}

	public DialogSwarmingPlayersParameter(int amount) {
		this(amount, true);
	}

	public DialogSwarmingPlayersParameter(int amount, boolean restrictPlacement) {
		this.amount = amount;
		this.restrictPlacement = restrictPlacement;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSwarmingPlayersParameter(amount, restrictPlacement);
	}

	public int getAmount() {
		return amount;
	}

	public boolean isRestrictPlacement() {
		return restrictPlacement;
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		amount = IJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(source, jsonObject);
		if (IJsonOption.RESTRICT_PLACEMENT.isDefinedIn(jsonObject)) {
			restrictPlacement = IJsonOption.RESTRICT_PLACEMENT.getFrom(source, jsonObject);
		} else {
			restrictPlacement = true;
		}
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.SWARMING_PLAYER_AMOUNT.addTo(jsonObject, amount);
		IJsonOption.RESTRICT_PLACEMENT.addTo(jsonObject, restrictPlacement);
		return jsonObject;
	}
}
