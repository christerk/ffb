package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.InterceptionModifier;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportInterceptionRoll extends ReportSkillRoll {

	private boolean fBomb, ignoreAgility;

	public ReportInterceptionRoll() {
	}

	public ReportInterceptionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
																InterceptionModifier[] pModifiers, boolean pBomb, boolean ignoreAgility) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pModifiers);
		fBomb = pBomb;
		this.ignoreAgility = ignoreAgility;
	}

	public ReportId getId() {
		return ReportId.INTERCEPTION_ROLL;
	}

	@Override
	public InterceptionModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new InterceptionModifier[0]);
	}

	public boolean isBomb() {
		return fBomb;
	}

	public boolean isIgnoreAgility() {
		return ignoreAgility;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportInterceptionRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), isBomb(), ignoreAgility);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.BOMB.addTo(jsonObject, fBomb);
		IJsonOption.IGNORE_AGILITY.addTo(jsonObject, ignoreAgility);
		return jsonObject;
	}

	@Override
	public ReportInterceptionRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBomb = IJsonOption.BOMB.getFrom(source, jsonObject);
		if (IJsonOption.IGNORE_AGILITY.isDefinedIn(jsonObject)) {
			ignoreAgility = IJsonOption.IGNORE_AGILITY.getFrom(source, jsonObject);
		}
		return this;
	}

}
