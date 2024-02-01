package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportDefectingPlayers implements IReport {

	private final List<String> fPlayerIds;
	private final List<Integer> fRolls;
	private final List<Boolean> fDefectings;

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
		return fPlayerIds.toArray(new String[0]);
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

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		for (int i = 0; i < fPlayerIds.size(); i++) {
			diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, fPlayerIds.get(i), fRolls.get(i), 4, getId(), !fDefectings.get(i)));
		}
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

	public ReportDefectingPlayers initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerIds.clear();
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		fRolls.clear();
		addRolls(IJsonOption.ROLLS.getFrom(source, jsonObject));
		fDefectings.clear();
		addDefectings(IJsonOption.DEFECTING_ARRAY.getFrom(source, jsonObject));
		return this;
	}

}
