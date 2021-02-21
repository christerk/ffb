package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * @author Kalimar
 */
public class ReportPassRoll extends ReportSkillRoll {

	private PassingDistance fPassingDistance;
	private boolean fHailMaryPass;
	private boolean fBomb;
	private PassResult result;

	public ReportPassRoll() {
		super(ReportId.PASS_ROLL);
	}

	public ReportPassRoll(String pPlayerId, int pRoll, boolean pReRolled, boolean pBomb, PassResult result) {
		this(pPlayerId, pRoll, 2, pReRolled, null, null, pBomb, result);
		fHailMaryPass = true;
	}

	public ReportPassRoll(String pPlayerId, int pRoll, int pMinimumRoll, boolean pReRolled,
	                      PassModifier[] pRollModifiers, PassingDistance pPassingDistance,
	                      boolean pBomb, PassResult result) {
		super(ReportId.PASS_ROLL, pPlayerId, PassResult.ACCURATE == result, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.result = result;
		fPassingDistance = pPassingDistance;
		fBomb = pBomb;
		fHailMaryPass = false;
	}

	public ReportId getId() {
		return ReportId.PASS_ROLL;
	}

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

	// transformation

	public IReport transform(IFactorySource source) {
		if (isHailMaryPass()) {
			return new ReportPassRoll(getPlayerId(), getRoll(), isReRolled(), isBomb(), result);
		} else {
			return new ReportPassRoll(getPlayerId(), getRoll(), getMinimumRoll(), isReRolled(),
				getRollModifiers(), getPassingDistance(), isBomb(), result);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.PASSING_DISTANCE.addTo(jsonObject, fPassingDistance);
		IJsonOption.PASS_RESULT.addTo(jsonObject, result);
		IJsonOption.HAIL_MARY_PASS.addTo(jsonObject, fHailMaryPass);
		IJsonOption.BOMB.addTo(jsonObject, fBomb);
		return jsonObject;
	}

	@Override
	public ReportPassRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fPassingDistance = (PassingDistance) IJsonOption.PASSING_DISTANCE.getFrom(game, jsonObject);
		result = (PassResult) IJsonOption.PASS_RESULT.getFrom(game, jsonObject);
		if (result == null) {
			boolean fumble = IJsonOption.FUMBLE.getFrom(game, jsonObject);
			boolean safeThrowHold = IJsonOption.SAFE_THROW_HOLD.getFrom(game, jsonObject);
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
		fHailMaryPass = IJsonOption.HAIL_MARY_PASS.getFrom(game, jsonObject);
		fBomb = IJsonOption.BOMB.getFrom(game, jsonObject);
		return this;
	}

}
