package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportSecretWeaponBan extends NoDiceReport {

	private final List<String> fPlayerIds;
	private final List<Integer> fRolls;
	private final List<Boolean> fBans;

	public ReportSecretWeaponBan() {
		fPlayerIds = new ArrayList<>();
		fRolls = new ArrayList<>();
		fBans = new ArrayList<>();
	}

	public ReportId getId() {
		return ReportId.SECRET_WEAPON_BAN;
	}

	public String[] getPlayerIds() {
		return fPlayerIds.toArray(new String[0]);
	}

	public int[] getRolls() {
		int[] result = new int[fRolls.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fRolls.get(i);
		}
		return result;
	}

	public boolean[] getBans() {
		boolean[] result = new boolean[fBans.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fBans.get(i);
		}
		return result;
	}

	public void add(String pPlayerId, int pRoll, boolean pBanned) {
		fPlayerIds.add(pPlayerId);
		fRolls.add(pRoll);
		fBans.add(pBanned);
	}

	private void addPlayerIds(String[] pPlayerIds) {
		if (pPlayerIds != null) {
			Collections.addAll(fPlayerIds, pPlayerIds);
		}
	}

	private void addRolls(int[] pRolls) {
		if (pRolls != null) {
			for (int roll : pRolls) {
				fRolls.add(roll);
			}
		}
	}

	private void addBans(boolean[] pBans) {
		if (pBans != null) {
			for (boolean ban : pBans) {
				fBans.add(ban);
			}
		}
	}

	// transformation

	public ReportSecretWeaponBan transform(IFactorySource source) {
		ReportSecretWeaponBan transformed = new ReportSecretWeaponBan();
		String[] playerIds = getPlayerIds();
		int[] rolls = getRolls();
		boolean[] banned = getBans();
		for (int i = 0; i < playerIds.length; i++) {
			transformed.add(playerIds[i], rolls[i], banned[i]);
		}
		return transformed;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
		IJsonOption.ROLLS.addTo(jsonObject, fRolls);
		IJsonOption.BAN_ARRAY.addTo(jsonObject, fBans);
		return jsonObject;
	}

	public ReportSecretWeaponBan initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerIds.clear();
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		fRolls.clear();
		addRolls(IJsonOption.ROLLS.getFrom(source, jsonObject));
		fBans.clear();
		addBans(IJsonOption.BAN_ARRAY.getFrom(source, jsonObject));
		return this;
	}

}
