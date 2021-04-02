package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportPlayerAction implements IReport {

	private String fActingPlayerId;
	private PlayerAction fPlayerAction;

	public ReportPlayerAction() {
		super();
	}

	public ReportPlayerAction(String pActingPlayerId, PlayerAction pPlayerAction) {
		this();
		fActingPlayerId = pActingPlayerId;
		fPlayerAction = pPlayerAction;
	}

	public ReportId getId() {
		return ReportId.PLAYER_ACTION;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public PlayerAction getPlayerAction() {
		return fPlayerAction;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPlayerAction(getActingPlayerId(), getPlayerAction());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.PLAYER_ACTION.addTo(jsonObject, fPlayerAction);
		return jsonObject;
	}

	public ReportPlayerAction initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(game, jsonObject);
		return this;
	}

}
