package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPickupRoll extends com.fumbbl.ffb.report.ReportPickupRoll {

	private boolean secureTheBall;

	@SuppressWarnings("unused")
	public ReportPickupRoll() {
	}

	public ReportPickupRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
													boolean pReRolled, RollModifier<?>[] pRollModifiers, boolean secureTheBall) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.secureTheBall = secureTheBall;
	}

	public boolean isSecureTheBall() {
		return secureTheBall;
	}

	@Override
	public ReportPickupRoll transform(IFactorySource source) {
		return new ReportPickupRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), secureTheBall);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.SECURE_THE_BALL_USED.addTo(jsonObject, secureTheBall);
		return jsonObject;
	}

	@Override
	public ReportSkillRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		secureTheBall = IJsonOption.SECURE_THE_BALL_USED.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
