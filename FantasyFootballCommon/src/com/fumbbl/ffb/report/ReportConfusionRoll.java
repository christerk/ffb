package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportConfusionRoll extends ReportSkillRoll {

	private Skill fConfusionSkill;

	public ReportConfusionRoll() {

	}

	public ReportConfusionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			Skill pConfusionSkill) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
		fConfusionSkill = pConfusionSkill;
	}

	public ReportId getId() {
		return ReportId.CONFUSION_ROLL;
	}

	public Skill getConfusionSkill() {
		return fConfusionSkill;
	}

	// transformation

	public IReport transform(IFactorySource source) {
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
	public ReportConfusionRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fConfusionSkill = (Skill) IJsonOption.CONFUSION_SKILL.getFrom(source, jsonObject);
		return this;
	}

}
