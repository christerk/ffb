package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.List;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportSpecialEffectRoll implements IReport {

	private SpecialEffect fSpecialEffect;
	private String fPlayerId;
	private int fRoll;
	private boolean fSuccessful;

	public ReportSpecialEffectRoll() {
		super();
	}

	public ReportSpecialEffectRoll(SpecialEffect pSpecialEffect, String pPlayerId, int pRoll, boolean pSuccessful) {
		fSpecialEffect = pSpecialEffect;
		fPlayerId = pPlayerId;
		fRoll = pRoll;
		fSuccessful = pSuccessful;
	}

	public ReportId getId() {
		return ReportId.SPELL_EFFECT_ROLL;
	}

	public SpecialEffect getSpecialEffect() {
		return fSpecialEffect;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public int getRoll() {
		return fRoll;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSpecialEffectRoll(getSpecialEffect(), getPlayerId(), getRoll(), isSuccessful());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SPECIAL_EFFECT.addTo(jsonObject, fSpecialEffect);
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		return jsonObject;
	}

	public ReportSpecialEffectRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fSpecialEffect = (SpecialEffect) IJsonOption.SPECIAL_EFFECT.getFrom(source, jsonObject);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		if (fRoll > 0) {
			int minimumRoll = fSpecialEffect == SpecialEffect.LIGHTNING ? 2 : 4;
			diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.OPPONENT_TEAM_FOR_PLAYER, fPlayerId, fRoll, minimumRoll, getId(), fSuccessful));
		}
	}
}
