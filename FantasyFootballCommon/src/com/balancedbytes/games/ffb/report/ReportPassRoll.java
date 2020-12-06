package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportPassRoll extends ReportSkillRoll {

	private PassingDistance fPassingDistance;
	private boolean fFumble;
	private boolean fSafeThrowHold;
	private boolean fHailMaryPass;
	private boolean fBomb;

	public ReportPassRoll() {
		super(ReportId.PASS_ROLL);
	}

	public ReportPassRoll(String pPlayerId, boolean pFumble, int pRoll, boolean pReRolled, boolean pBomb) {
		this(pPlayerId, !pFumble, pRoll, 2, pReRolled, null, null, pFumble, false, pBomb);
		fHailMaryPass = true;
	}

	public ReportPassRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			PassModifier[] pRollModifiers, PassingDistance pPassingDistance, boolean pFumble, boolean pSafeThrowHold,
			boolean pBomb) {
		super(ReportId.PASS_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		fFumble = pFumble;
		fPassingDistance = pPassingDistance;
		fFumble = pFumble;
		fSafeThrowHold = pSafeThrowHold;
		fBomb = pBomb;
		fHailMaryPass = false;
	}

	public ReportId getId() {
		return ReportId.PASS_ROLL;
	}

	@Override
	public PassModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new PassModifier[getRollModifierList().size()]);
	}

	public PassingDistance getPassingDistance() {
		return fPassingDistance;
	}

	public boolean isFumble() {
		return fFumble;
	}

	public boolean isHeldBySafeThrow() {
		return fSafeThrowHold;
	}

	public boolean isHailMaryPass() {
		return fHailMaryPass;
	}

	public boolean isBomb() {
		return fBomb;
	}

	// transformation

	public IReport transform() {
		if (isHailMaryPass()) {
			return new ReportPassRoll(getPlayerId(), isFumble(), getRoll(), isReRolled(), isBomb());
		} else {
			return new ReportPassRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
					getRollModifiers(), getPassingDistance(), isFumble(), isHeldBySafeThrow(), isBomb());
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.PASSING_DISTANCE.addTo(jsonObject, fPassingDistance);
		IJsonOption.FUMBLE.addTo(jsonObject, fFumble);
		IJsonOption.SAFE_THROW_HOLD.addTo(jsonObject, fSafeThrowHold);
		IJsonOption.HAIL_MARY_PASS.addTo(jsonObject, fHailMaryPass);
		IJsonOption.BOMB.addTo(jsonObject, fBomb);
		return jsonObject;
	}

	@Override
	public ReportPassRoll initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fPassingDistance = (PassingDistance) IJsonOption.PASSING_DISTANCE.getFrom(jsonObject);
		fFumble = IJsonOption.FUMBLE.getFrom(jsonObject);
		fSafeThrowHold = IJsonOption.SAFE_THROW_HOLD.getFrom(jsonObject);
		fHailMaryPass = IJsonOption.HAIL_MARY_PASS.getFrom(jsonObject);
		fBomb = IJsonOption.BOMB.getFrom(jsonObject);
		return this;
	}

}
