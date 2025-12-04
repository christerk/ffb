package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportKickoffOfficiousRef extends NoDiceReport {

	private int rollHome;
	private int rollAway;
	private final List<String> playerIds = new ArrayList<>();

	public ReportKickoffOfficiousRef() {
	}

	public ReportKickoffOfficiousRef(int rollHome, int rollAway, List<String> playerIds) {
		this.rollHome = rollHome;
		this.rollAway = rollAway;
		this.playerIds.addAll(playerIds);
	}

	public ReportId getId() {
		return ReportId.KICKOFF_OFFICIOUS_REF;
	}

	public int getRollHome() {
		return rollHome;
	}

	public int getRollAway() {
		return rollAway;
	}

	public List<String> getPlayerIds() {
		return playerIds;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportKickoffOfficiousRef(getRollAway(), getRollHome(), getPlayerIds());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL_HOME.addTo(jsonObject, rollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, rollAway);
		IJsonOption.PLAYER_IDS_HIT.addTo(jsonObject, playerIds);
		return jsonObject;
	}

	public ReportKickoffOfficiousRef initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		rollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		rollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		playerIds.clear();
		String[] playerIdsArray = IJsonOption.PLAYER_IDS_HIT.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(playerIdsArray)) {
			playerIds.addAll(Arrays.stream(playerIdsArray).collect(Collectors.toList()));
		}
		return this;
	}

}
