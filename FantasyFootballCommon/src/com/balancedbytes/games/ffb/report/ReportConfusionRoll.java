package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportConfusionRoll extends ReportSkillRoll {

	private Skill fConfusionSkill;

	public ReportConfusionRoll() {
		super(ReportId.CONFUSION_ROLL);
	}

	public ReportConfusionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			Skill pConfusionSkill) {
		super(ReportId.CONFUSION_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled);
		fConfusionSkill = pConfusionSkill;
	}

	public ReportId getId() {
		return ReportId.CONFUSION_ROLL;
	}

	public Skill getConfusionSkill() {
		return fConfusionSkill;
	}

	// transformation

	public IReport transform() {
		return new ReportConfusionRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getConfusionSkill());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.CONFUSION_SKILL.addTo(jsonObject, fConfusionSkill);
		return jsonObject;
	}

	@Override
	public ReportConfusionRoll initFrom(Game game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fConfusionSkill = (Skill) IJsonOption.CONFUSION_SKILL.getFrom(game, jsonObject);
		return this;
	}

}
