package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportKickoffThrowARock implements IReport {

	private int fRollHome;
	private int fRollAway;
	private final List<String> fPlayersHit;

	public ReportKickoffThrowARock() {
		fPlayersHit = new ArrayList<>();
	}

	public ReportKickoffThrowARock(int pRollHome, int pRollAway, String[] pPlayersHit) {
		this();
		fRollHome = pRollHome;
		fRollAway = pRollAway;
		addPlayerIds(pPlayersHit);
	}

	public ReportId getId() {
		return ReportId.KICKOFF_THROW_A_ROCK;
	}

	public int getRollHome() {
		return fRollHome;
	}

	public int getRollAway() {
		return fRollAway;
	}

	public String[] getPlayersHit() {
		return fPlayersHit.toArray(new String[fPlayersHit.size()]);
	}

	private void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayersHit.add(pPlayerId);
		}
	}

	private void addPlayerIds(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerId(playerId);
			}
		}
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportKickoffThrowARock(getRollAway(), getRollHome(), getPlayersHit());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL_HOME.addTo(jsonObject, fRollHome);
		IJsonOption.ROLL_AWAY.addTo(jsonObject, fRollAway);
		IJsonOption.PLAYER_IDS_HIT.addTo(jsonObject, fPlayersHit);
		return jsonObject;
	}

	public ReportKickoffThrowARock initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fRollHome = IJsonOption.ROLL_HOME.getFrom(source, jsonObject);
		fRollAway = IJsonOption.ROLL_AWAY.getFrom(source, jsonObject);
		fPlayersHit.clear();
		addPlayerIds(IJsonOption.PLAYER_IDS_HIT.getFrom(source, jsonObject));
		return this;
	}

}
