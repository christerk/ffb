package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportTentaclesShadowingRoll implements IReport {

	private Skill fSkill;
	private String fDefenderId;
	private int[] fRoll;
	private boolean fSuccessful;
	private int fMinimumRoll;
	private boolean fReRolled;

	public ReportTentaclesShadowingRoll() {
		super();
	}

	public ReportTentaclesShadowingRoll(Skill pSkill, String pDefenderId, int[] pRoll, boolean pSuccessful,
			int pMinimumRoll, boolean pReRolled) {
		fSkill = pSkill;
		fDefenderId = pDefenderId;
		fRoll = pRoll;
		fSuccessful = pSuccessful;
		fMinimumRoll = pMinimumRoll;
		fReRolled = pReRolled;
	}

	public ReportId getId() {
		return ReportId.TENTACLES_SHADOWING_ROLL;
	}

	public Skill getSkill() {
		return fSkill;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public int[] getRoll() {
		return fRoll;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int getMinimumRoll() {
		return fMinimumRoll;
	}

	public boolean isReRolled() {
		return fReRolled;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportTentaclesShadowingRoll(getSkill(), getDefenderId(), getRoll(), isSuccessful(), getMinimumRoll(),
				isReRolled());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.TENTACLE_ROLL.addTo(jsonObject, fRoll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
		return jsonObject;
	}

	public ReportTentaclesShadowingRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fRoll = IJsonOption.TENTACLE_ROLL.getFrom(source, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		fReRolled = IJsonOption.RE_ROLLED.getFrom(source, jsonObject);
		return this;
	}

}
