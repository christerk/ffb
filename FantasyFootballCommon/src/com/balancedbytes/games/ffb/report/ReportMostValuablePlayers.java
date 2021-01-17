package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportMostValuablePlayers implements IReport {

	private List<String> fPlayerIdsHome;
	private List<String> fPlayerIdsAway;

	public ReportMostValuablePlayers() {
		fPlayerIdsHome = new ArrayList<>();
		fPlayerIdsAway = new ArrayList<>();
	}

	public ReportId getId() {
		return ReportId.MOST_VALUABLE_PLAYERS;
	}

	public void addPlayerIdHome(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIdsHome.add(pPlayerId);
		}
	}

	private void addPlayerIdsHome(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerIdHome(playerId);
			}
		}
	}

	public String[] getPlayerIdsHome() {
		return fPlayerIdsHome.toArray(new String[fPlayerIdsHome.size()]);
	}

	public void addPlayerIdAway(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIdsAway.add(pPlayerId);
		}
	}

	private void addPlayerIdsAway(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerIdAway(playerId);
			}
		}
	}

	public String[] getPlayerIdsAway() {
		return fPlayerIdsAway.toArray(new String[fPlayerIdsAway.size()]);
	}

	// transformation

	public IReport transform(IFactorySource source) {
		ReportMostValuablePlayers transformedReport = new ReportMostValuablePlayers();
		transformedReport.addPlayerIdsAway(getPlayerIdsHome());
		transformedReport.addPlayerIdsHome(getPlayerIdsAway());
		return transformedReport;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS_HOME.addTo(jsonObject, getPlayerIdsHome());
		IJsonOption.PLAYER_IDS_AWAY.addTo(jsonObject, getPlayerIdsAway());
		return jsonObject;
	}

	public ReportMostValuablePlayers initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		addPlayerIdsHome(IJsonOption.PLAYER_IDS_HOME.getFrom(game, jsonObject));
		addPlayerIdsAway(IJsonOption.PLAYER_IDS_AWAY.getFrom(game, jsonObject));
		return this;
	}

}
