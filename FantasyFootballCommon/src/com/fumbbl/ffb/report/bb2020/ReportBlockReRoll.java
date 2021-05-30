package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportBlockReRoll implements IReport {

	private int[] blockRoll;
	private String playerId;
	private ReRollSource reRollSource;

	public ReportBlockReRoll() {
		super();
	}

	public ReportBlockReRoll(int[] blockRoll, String playerId, ReRollSource reRollSource) {
		this.blockRoll = blockRoll;
		this.playerId = playerId;
		this.reRollSource = reRollSource;
	}

	public ReportId getId() {
		return ReportId.BLOCK_RE_ROLL;
	}

	public int[] getBlockRoll() {
		return blockRoll;
	}

	public String getPlayerId() {
		return playerId;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}
	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBlockReRoll(blockRoll, playerId, reRollSource);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.BLOCK_ROLL.addTo(jsonObject, blockRoll);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
		return jsonObject;
	}

	public ReportBlockReRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		blockRoll = IJsonOption.BLOCK_ROLL.getFrom(game, jsonObject);
		reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		return this;
	}

}
