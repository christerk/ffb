package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.HeatExhaustion;
import com.fumbbl.ffb.KnockoutRecovery;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportTurnEnd implements IReport {

	private String fPlayerIdTouchdown;
	private final List<KnockoutRecovery> fKnockoutRecoveries;
	private final List<HeatExhaustion> fHeatExhaustions;
	private final List<Player<?>> unzappedPlayers;

	public ReportTurnEnd() {
		fKnockoutRecoveries = new ArrayList<>();
		fHeatExhaustions = new ArrayList<>();
		unzappedPlayers = new ArrayList<>();
	}

	public ReportTurnEnd(String pPlayerIdTouchdown, KnockoutRecovery[] pKnockoutRecoveries,
			HeatExhaustion[] pHeatExhaustions, List<Player<?>> unzappedPlayers) {
		this();
		fPlayerIdTouchdown = pPlayerIdTouchdown;
		add(pKnockoutRecoveries);
		add(pHeatExhaustions);
		this.unzappedPlayers.addAll(unzappedPlayers);
	}

	public ReportId getId() {
		return ReportId.TURN_END;
	}

	public String getPlayerIdTouchdown() {
		return fPlayerIdTouchdown;
	}

	public KnockoutRecovery[] getKnockoutRecoveries() {
		return fKnockoutRecoveries.toArray(new KnockoutRecovery[fKnockoutRecoveries.size()]);
	}

	private void add(KnockoutRecovery pKnockoutRecovery) {
		if (pKnockoutRecovery != null) {
			fKnockoutRecoveries.add(pKnockoutRecovery);
		}
	}

	private void add(KnockoutRecovery[] pKnockoutRecoveries) {
		if (ArrayTool.isProvided(pKnockoutRecoveries)) {
			for (KnockoutRecovery knockoutRecovery : pKnockoutRecoveries) {
				add(knockoutRecovery);
			}
		}
	}

	public List<Player<?>> getUnzappedPlayers() {
		return new ArrayList<>(unzappedPlayers);
	}

	public HeatExhaustion[] getHeatExhaustions() {
		return fHeatExhaustions.toArray(new HeatExhaustion[fHeatExhaustions.size()]);
	}

	private void add(HeatExhaustion pHeatExhaustion) {
		if (pHeatExhaustion != null) {
			fHeatExhaustions.add(pHeatExhaustion);
		}
	}

	private void add(HeatExhaustion[] pHeatExhaustions) {
		if (ArrayTool.isProvided(pHeatExhaustions)) {
			for (HeatExhaustion heatExhaustion : pHeatExhaustions) {
				add(heatExhaustion);
			}
		}
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportTurnEnd(getPlayerIdTouchdown(), getKnockoutRecoveries(), getHeatExhaustions(),
				getUnzappedPlayers());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID_TOUCHDOWN.addTo(jsonObject, fPlayerIdTouchdown);
		JsonArray knockoutRecoveryArray = new JsonArray();
		for (KnockoutRecovery knockoutRecovery : fKnockoutRecoveries) {
			knockoutRecoveryArray.add(knockoutRecovery.toJsonValue());
		}
		IJsonOption.KNOCKOUT_RECOVERY_ARRAY.addTo(jsonObject, knockoutRecoveryArray);
		JsonArray heatExhaustionArray = new JsonArray();
		for (HeatExhaustion heatExhaustion : fHeatExhaustions) {
			heatExhaustionArray.add(heatExhaustion.toJsonValue());
		}
		IJsonOption.HEAT_EXHAUSTION_ARRAY.addTo(jsonObject, heatExhaustionArray);
		JsonArray unzappedArray = new JsonArray();
		for (Player<?> unzappedPlayer : unzappedPlayers) {
			unzappedArray.add(unzappedPlayer.toJsonValue());
		}
		IJsonOption.UNZAP_ARRAY.addTo(jsonObject, unzappedArray);
		return jsonObject;
	}

	public ReportTurnEnd initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerIdTouchdown = IJsonOption.PLAYER_ID_TOUCHDOWN.getFrom(source, jsonObject);
		JsonArray knockoutRecoveryArray = IJsonOption.KNOCKOUT_RECOVERY_ARRAY.getFrom(source, jsonObject);
		if (knockoutRecoveryArray != null) {
			for (int i = 0; i < knockoutRecoveryArray.size(); i++) {
				add(new KnockoutRecovery().initFrom(source, knockoutRecoveryArray.get(i)));
			}
		}
		JsonArray heatExhaustionArray = IJsonOption.HEAT_EXHAUSTION_ARRAY.getFrom(source, jsonObject);
		if (heatExhaustionArray != null) {
			for (int i = 0; i < heatExhaustionArray.size(); i++) {
				add(new HeatExhaustion().initFrom(source, heatExhaustionArray.get(i)));
			}
		}

		JsonArray unzappedArray = IJsonOption.UNZAP_ARRAY.getFrom(source, jsonObject);
		if (unzappedArray != null) {
			for (int i = 0; i < unzappedArray.size(); i++) {
				unzappedPlayers.add(Player.getFrom(source, unzappedArray.get(i)));
			}
		}

		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		fHeatExhaustions.stream().map(ext ->
				new SingleDieStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, ext.getPlayerId(), ext.getRoll(), 2, getId(), !ext.isExhausted()))
			.forEach(diceStats::add);
		fKnockoutRecoveries.stream().map(rec ->
				new SingleDieStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, rec.getPlayerId(), rec.getRoll(), 4 - rec.getBloodweiserBabes(), getId(), rec.isRecovering()))
			.forEach(diceStats::add);
	}
}
