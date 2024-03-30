package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportPassRoll extends ReportSkillRoll {

	private PassingDistance fPassingDistance;
	private boolean fHailMaryPass;
	private boolean fBomb;
	private PassResult result;
	private StatBasedRollModifier statBasedRollModifier;

	public ReportPassRoll() {
	}

	public ReportPassRoll(String pPlayerId, int pRoll, boolean pReRolled, boolean pBomb, PassResult result) {
		this(pPlayerId, pRoll, 2, pReRolled, null, null, pBomb, result, true);
	}

	public ReportPassRoll(String pPlayerId, int pRoll, int pMinimumRoll, boolean pReRolled,
												PassModifier[] pRollModifiers, PassingDistance pPassingDistance,
												boolean pBomb, PassResult result) {
		this(pPlayerId, pRoll, pMinimumRoll, pReRolled, pRollModifiers, pPassingDistance, pBomb, result, false);
	}

	public ReportPassRoll(String pPlayerId, int pRoll, int pMinimumRoll, boolean pReRolled,
												PassModifier[] pRollModifiers, PassingDistance pPassingDistance,
												boolean pBomb, PassResult result, boolean hailMaryPass) {
		this(pPlayerId, pRoll, pMinimumRoll, pReRolled, pRollModifiers, pPassingDistance, pBomb, result, hailMaryPass, null);
	}

	public ReportPassRoll(String pPlayerId, int pRoll, int pMinimumRoll, boolean pReRolled,
												PassModifier[] pRollModifiers, PassingDistance pPassingDistance,
												boolean pBomb, PassResult result, boolean hailMaryPass, StatBasedRollModifier statBasedRollModifier) {
		super(pPlayerId, PassResult.ACCURATE == result || (hailMaryPass && PassResult.INACCURATE == result), pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.result = result;
		fPassingDistance = pPassingDistance;
		fBomb = pBomb;
		fHailMaryPass = hailMaryPass;
		this.statBasedRollModifier = statBasedRollModifier;
	}

	public ReportId getId() {
		return ReportId.PASS_ROLL;
	}

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	public PassModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new PassModifier[0]);
	}

	public PassingDistance getPassingDistance() {
		return fPassingDistance;
	}

	public PassResult getResult() {
		return result;
	}

	public boolean isHailMaryPass() {
		return fHailMaryPass;
	}

	public boolean isBomb() {
		return fBomb;
	}

	public StatBasedRollModifier getStatBasedRollModifier() {
		return statBasedRollModifier;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPassRoll(getPlayerId(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), getPassingDistance(), isBomb(), result, isHailMaryPass(), statBasedRollModifier);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.PASSING_DISTANCE.addTo(jsonObject, fPassingDistance);
		IJsonOption.PASS_RESULT.addTo(jsonObject, result);
		IJsonOption.HAIL_MARY_PASS.addTo(jsonObject, fHailMaryPass);
		IJsonOption.BOMB.addTo(jsonObject, fBomb);
		if (statBasedRollModifier != null) {
			IJsonOption.STAT_BASED_ROLL_MODIFIER.addTo(jsonObject, statBasedRollModifier.toJsonValue().asObject());
		}
		return jsonObject;
	}

	@Override
	public ReportPassRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPassingDistance = (PassingDistance) IJsonOption.PASSING_DISTANCE.getFrom(source, jsonObject);
		result = (PassResult) IJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		if (result == null) {
			boolean fumble = IJsonOption.FUMBLE.getFrom(source, jsonObject);
			boolean safeThrowHold = IJsonOption.SAFE_THROW_HOLD.getFrom(source, jsonObject);
			if (safeThrowHold) {
				result = PassResult.SAVED_FUMBLE;
			} else if (fumble) {
				result = PassResult.FUMBLE;
			} else if (isSuccessful()) {
				result = PassResult.ACCURATE;
			} else {
				result = PassResult.INACCURATE;
			}
		}
		fHailMaryPass = IJsonOption.HAIL_MARY_PASS.getFrom(source, jsonObject);
		fBomb = IJsonOption.BOMB.getFrom(source, jsonObject);
		if (IJsonOption.STAT_BASED_ROLL_MODIFIER.isDefinedIn(jsonObject)) {
			statBasedRollModifier = new StatBasedRollModifier()
				.initFrom(source, IJsonOption.STAT_BASED_ROLL_MODIFIER.getFrom(source, jsonObject));
		}
		return this;
	}

}
