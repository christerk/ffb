package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportChainsawRoll extends ReportSkillRoll {
	private String defenderId;

	public ReportChainsawRoll() {
	}

	public ReportChainsawRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                          boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		this(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers, null);
	}

	public ReportChainsawRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                          boolean pReRolled, RollModifier<?>[] pRollModifiers, String defenderId) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.defenderId = defenderId;
	}

	@Override
	public ReportId getId() {
		return ReportId.CHAINSAW_ROLL;
	}

	@Override
	public ReportChainsawRoll transform(IFactorySource source) {
		return new ReportChainsawRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), defenderId);
	}

	public String getDefenderId() {
		return defenderId;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public ReportChainsawRoll initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
