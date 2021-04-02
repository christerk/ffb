package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportDefectingPlayers implements IReport {

	private List<String> fPlayerIds;
	private List<Integer> fRolls;
	private List<Boolean> fDefectings;

	public ReportDefectingPlayers() {
		fPlayerIds = new ArrayList<>();
		fRolls = new ArrayList<>();
		fDefectings = new ArrayList<>();
	}

	public ReportDefectingPlayers(String[] pPlayerIds, int[] pRolls, boolean[] pDefecting) {
		this();
		addPlayerIds(pPlayerIds);
		addRolls(pRolls);
		addDefectings(pDefecting);
	}

	public ReportId getId() {
		return ReportId.DEFECTING_PLAYERS;
	}

	public int[] getRolls() {
		int[] rolls = new int[fRolls.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = fRolls.get(i);
		}
		return rolls;
	}

	private void addRoll(int pRoll) {
		fRolls.add(pRoll);
	}

	private void addRolls(int[] pRolls) {
		if (ArrayTool.isProvided(pRolls)) {
			for (int roll : pRolls) {
				addRoll(roll);
			}
		}
	}

	public boolean[] getDefectings() {
		boolean[] defecting = new boolean[fDefectings.size()];
		for (int i = 0; i < defecting.length; i++) {
			defecting[i] = fDefectings.get(i);
		}
		return defecting;
	}

	private void addDefecting(boolean pDefecting) {
		fDefectings.add(pDefecting);
	}

	private void addDefectings(boolean[] pDefectings) {
		if (ArrayTool.isProvided(pDefectings)) {
			for (boolean defecting : pDefectings) {
				addDefecting(defecting);
			}
		}
	}

	public String[] getPlayerIds() {
		return fPlayerIds.toArray(new String[fPlayerIds.size()]);
	}

	private void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIds.add(pPlayerId);
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

	public ReportDefectingPlayers transform(IFactorySource source) {
		return new ReportDefectingPlayers(getPlayerIds(), getRolls(), getDefectings());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
		IJsonOption.ROLLS.addTo(jsonObject, fRolls);
		IJsonOption.DEFECTING_ARRAY.addTo(jsonObject, fDefectings);
		return jsonObject;
	}

	public ReportDefectingPlayers initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fPlayerIds.clear();
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		fRolls.clear();
		addRolls(IJsonOption.ROLLS.getFrom(game, jsonObject));
		fDefectings.clear();
		addDefectings(IJsonOption.DEFECTING_ARRAY.getFrom(game, jsonObject));
		return this;
	}

}
