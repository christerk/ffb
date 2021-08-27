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
public class DialogGameStatisticsParameter extends DialogWithoutParameter {

	public DialogGameStatisticsParameter() {
		super();
	}

	public DialogId getId() {
		return DialogId.GAME_STATISTICS;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogGameStatisticsParameter();
	}

	// JSON serialization

	public DialogGameStatisticsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		return this;
	}

}
