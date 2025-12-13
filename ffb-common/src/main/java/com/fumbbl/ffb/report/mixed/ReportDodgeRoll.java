package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportDodgeRoll extends ReportSkillRoll {

	private StatBasedRollModifier statBasedRollModifier;

	public ReportDodgeRoll() {
	}

	public ReportDodgeRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
												 boolean pReRolled, RollModifier<?>[] pRollModifiers, StatBasedRollModifier statBasedRollModifier) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.statBasedRollModifier = statBasedRollModifier;
	}

	@Override
	public ReportId getId() {
		return ReportId.DODGE_ROLL;
	}

	public StatBasedRollModifier getStatBasedRollModifier() {
		return statBasedRollModifier;
	}

	@Override
	public ReportDodgeRoll transform(IFactorySource source) {
		return new ReportDodgeRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), statBasedRollModifier);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (statBasedRollModifier != null) {
			IJsonOption.STAT_BASED_ROLL_MODIFIER.addTo(jsonObject, statBasedRollModifier.toJsonValue().asObject());
		}

		return jsonObject;
	}

	@Override
	public ReportSkillRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		if (IJsonOption.STAT_BASED_ROLL_MODIFIER.isDefinedIn(jsonObject)) {
			statBasedRollModifier = new StatBasedRollModifier()
				.initFrom(source, IJsonOption.STAT_BASED_ROLL_MODIFIER.getFrom(source, jsonObject));
		}
		return this;
	}
}
