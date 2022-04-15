package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

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

	public ReportPlayerAction initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(source, jsonObject);
		fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(source, jsonObject);
		return this;
	}

}
