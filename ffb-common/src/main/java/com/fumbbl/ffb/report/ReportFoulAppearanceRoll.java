package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportFoulAppearanceRoll extends ReportSkillRoll {

	private String defenderId;

	public ReportFoulAppearanceRoll() {
	}

	public ReportFoulAppearanceRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                                boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		this(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers, null);
	}

	public ReportFoulAppearanceRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                                boolean pReRolled, RollModifier<?>[] pRollModifiers, String defenderId) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.defenderId = defenderId;
	}

	public String getDefenderId() {
		return defenderId;
	}

	@Override
	public ReportId getId() {
		return ReportId.FOUL_APPEARANCE_ROLL;
	}

	@Override
	public ReportFoulAppearanceRoll transform(IFactorySource source) {
		return new ReportFoulAppearanceRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), getDefenderId());
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public ReportFoulAppearanceRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		return this;
	}
}
