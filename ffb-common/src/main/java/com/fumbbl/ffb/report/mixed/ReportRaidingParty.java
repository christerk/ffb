package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportRaidingParty extends NoDiceReport {

	private String playerId, otherPlayerId;
	private Direction direction;

	public ReportRaidingParty() {
		super();
	}

	public ReportRaidingParty(String playerId, String otherPlayerId, Direction direction) {
		this.playerId = playerId;
		this.otherPlayerId = otherPlayerId;
		this.direction = direction;
	}

	public ReportId getId() {
		return ReportId.RAIDING_PARTY;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Direction getDirection() {
		return direction;
	}

	public String getOtherPlayerId() {
		return otherPlayerId;
	}

// transformation

	public IReport transform(IFactorySource source) {
		return new ReportRaidingParty(playerId, otherPlayerId, direction != null ? direction.transform() : null);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.PLAYER_ID_OTHER_PLAYER.addTo(jsonObject, otherPlayerId);
		IJsonOption.DIRECTION.addTo(jsonObject, direction);
		return jsonObject;
	}

	public ReportRaidingParty initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		otherPlayerId = IJsonOption.PLAYER_ID_OTHER_PLAYER.getFrom(source, jsonObject);
		direction = (Direction) IJsonOption.DIRECTION.getFrom(source, jsonObject);
		return this;
	}

}
