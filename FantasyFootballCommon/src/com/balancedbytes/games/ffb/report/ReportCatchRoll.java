package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportCatchRoll extends ReportSkillRoll {

	private boolean fBomb;

	public ReportCatchRoll() {
		super(ReportId.CATCH_ROLL);
	}

	public ReportCatchRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			CatchModifier[] pRollModifiers, boolean pBomb) {
		super(ReportId.CATCH_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		fBomb = pBomb;
	}

	public boolean isBomb() {
		return fBomb;
	}

	@Override
	public CatchModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new CatchModifier[getRollModifierList().size()]);
	}

	// transformation

	public IReport transform(Game game) {
		return new ReportCatchRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getRollModifiers(), isBomb());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.BOMB.addTo(jsonObject, fBomb);
		return jsonObject;
	}

	@Override
	public ReportCatchRoll initFrom(Game game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fBomb = IJsonOption.BOMB.getFrom(game, jsonObject);
		return this;
	}

}
