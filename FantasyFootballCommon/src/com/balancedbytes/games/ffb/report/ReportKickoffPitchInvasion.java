package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 * @author Kalimar
 */
public class ReportKickoffPitchInvasion implements IReport {

	private List<Integer> fRollsHome;
	private List<Boolean> fPlayersAffectedHome;
	private List<Integer> fRollsAway;
	private List<Boolean> fPlayersAffectedAway;

	public ReportKickoffPitchInvasion() {
		fRollsHome = new ArrayList<>();
		fPlayersAffectedHome = new ArrayList<>();
		fRollsAway = new ArrayList<>();
		fPlayersAffectedAway = new ArrayList<>();
	}

	public ReportKickoffPitchInvasion(int[] pRollsHome, boolean[] pPlayersAffectedHome, int[] pRollsAway,
			boolean[] pPlayersAffectedAway) {
		this();
		addRollsHome(pRollsHome);
		addPlayersAffectedHome(pPlayersAffectedHome);
		addRollsAway(pRollsAway);
		addPlayersAffectedAway(pPlayersAffectedAway);
	}

	public ReportId getId() {
		return ReportId.KICKOFF_PITCH_INVASION;
	}

	public int[] getRollsHome() {
		int[] rolls = new int[fRollsHome.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = fRollsHome.get(i);
		}
		return rolls;
	}

	private void addRollHome(int pRoll) {
		fRollsHome.add(pRoll);
	}

	private void addRollsHome(int[] pRolls) {
		if (ArrayTool.isProvided(pRolls)) {
			for (int roll : pRolls) {
				addRollHome(roll);
			}
		}
	}

	public boolean[] getPlayersAffectedHome() {
		boolean[] playersAffected = new boolean[fPlayersAffectedHome.size()];
		for (int i = 0; i < playersAffected.length; i++) {
			playersAffected[i] = fPlayersAffectedHome.get(i);
		}
		return playersAffected;
	}

	private void addPlayerAffectedHome(boolean pPlayerAffected) {
		fPlayersAffectedHome.add(pPlayerAffected);
	}

	private void addPlayersAffectedHome(boolean[] pPlayersAffected) {
		if (ArrayTool.isProvided(pPlayersAffected)) {
			for (boolean playerAffected : pPlayersAffected) {
				addPlayerAffectedHome(playerAffected);
			}
		}
	}

	public int[] getRollsAway() {
		int[] rolls = new int[fRollsAway.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = fRollsAway.get(i);
		}
		return rolls;
	}

	private void addRollAway(int pRoll) {
		fRollsAway.add(pRoll);
	}

	private void addRollsAway(int[] pRolls) {
		if (ArrayTool.isProvided(pRolls)) {
			for (int roll : pRolls) {
				addRollAway(roll);
			}
		}
	}

	public boolean[] getPlayersAffectedAway() {
		boolean[] playersAffected = new boolean[fPlayersAffectedAway.size()];
		for (int i = 0; i < playersAffected.length; i++) {
			playersAffected[i] = fPlayersAffectedAway.get(i);
		}
		return playersAffected;
	}

	private void addPlayerAffectedAway(boolean pPlayerAffected) {
		fPlayersAffectedAway.add(pPlayerAffected);
	}

	private void addPlayersAffectedAway(boolean[] pPlayersAffected) {
		if (ArrayTool.isProvided(pPlayersAffected)) {
			for (boolean playerAffected : pPlayersAffected) {
				addPlayerAffectedAway(playerAffected);
			}
		}
	}

	// transformation

	public ReportKickoffPitchInvasion transform(IFactorySource source) {
		return new ReportKickoffPitchInvasion(getRollsAway(), getPlayersAffectedAway(), getRollsHome(),
				getPlayersAffectedHome());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLLS_HOME.addTo(jsonObject, fRollsHome);
		IJsonOption.PLAYERS_AFFECTED_HOME.addTo(jsonObject, fPlayersAffectedHome);
		IJsonOption.ROLLS_AWAY.addTo(jsonObject, fRollsAway);
		IJsonOption.PLAYERS_AFFECTED_AWAY.addTo(jsonObject, fPlayersAffectedAway);
		return jsonObject;
	}

	public ReportKickoffPitchInvasion initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fRollsHome.clear();
		addRollsHome(IJsonOption.ROLLS_HOME.getFrom(game, jsonObject));
		fPlayersAffectedHome.clear();
		addPlayersAffectedHome(IJsonOption.PLAYERS_AFFECTED_HOME.getFrom(game, jsonObject));
		fRollsAway.clear();
		addRollsAway(IJsonOption.ROLLS_AWAY.getFrom(game, jsonObject));
		fPlayersAffectedAway.clear();
		addPlayersAffectedAway(IJsonOption.PLAYERS_AFFECTED_AWAY.getFrom(game, jsonObject));
		return this;
	}

}
