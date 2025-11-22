package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifierFactory;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportApothecaryRoll extends NoDiceReport {

	private String fPlayerId;
	private int[] fCasualtyRoll;
	private PlayerState fPlayerState;
	private SeriousInjury fSeriousInjury, originalInjury;

	private final Set<CasualtyModifier> casualtyModifiers = new HashSet<>();

	public ReportApothecaryRoll() {
		super();
	}

	public ReportApothecaryRoll(String pPlayerId, int[] pCasualtyRoll, PlayerState pPlayerState,
	                            SeriousInjury pSeriousInjury, SeriousInjury originalInjury, Set<CasualtyModifier> casualtyModifiers) {
		fPlayerId = pPlayerId;
		fCasualtyRoll = pCasualtyRoll;
		fPlayerState = pPlayerState;
		fSeriousInjury = pSeriousInjury;
		this.originalInjury = originalInjury;
		this.casualtyModifiers.addAll(casualtyModifiers);
	}

	public ReportId getId() {
		return ReportId.APOTHECARY_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public int[] getCasualtyRoll() {
		return fCasualtyRoll;
	}

	public PlayerState getPlayerState() {
		return fPlayerState;
	}

	public SeriousInjury getSeriousInjury() {
		return fSeriousInjury;
	}

	public SeriousInjury getOriginalInjury() {
		return originalInjury;
	}

	public Set<CasualtyModifier> getCasualtyModifiers() {
		return casualtyModifiers;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportApothecaryRoll(getPlayerId(), getCasualtyRoll(), getPlayerState(), getSeriousInjury(), originalInjury, casualtyModifiers);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.CASUALTY_ROLL.addTo(jsonObject, fCasualtyRoll);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		IJsonOption.SERIOUS_INJURY_OLD.addTo(jsonObject, originalInjury);
		JsonArray casualtyModifiers = new JsonArray();
		this.casualtyModifiers.forEach(modifier -> casualtyModifiers.add(UtilJson.toJsonValue(modifier)));
		IJsonOption.CASUALTY_MODIFIERS.addTo(jsonObject, casualtyModifiers);
		return jsonObject;
	}

	public ReportApothecaryRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fCasualtyRoll = IJsonOption.CASUALTY_ROLL.getFrom(source, jsonObject);
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		originalInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY_OLD.getFrom(source, jsonObject);

		casualtyModifiers.clear();
		if (IJsonOption.CASUALTY_MODIFIERS.isDefinedIn(jsonObject)) {
			CasualtyModifierFactory casualtyModifierFactory = source.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
			JsonArray casualtyModifiers = IJsonOption.CASUALTY_MODIFIERS.getFrom(source, jsonObject);
			casualtyModifiers.values().forEach(value ->
				this.casualtyModifiers.add((CasualtyModifier) UtilJson.toEnumWithName(casualtyModifierFactory, value))
			);
		}
		return this;
	}

}
