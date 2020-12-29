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

	public DialogGameStatisticsParameter initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		return this;
	}

}
