package com.balancedbytes.games.ffb.report.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.UtilReport;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportTentaclesShadowingRoll implements IReport {

	private Skill fSkill;
	private String fDefenderId;
	private int fRoll;
	private boolean fSuccessful;
	private int fMinimumRoll;
	private boolean fReRolled;

	public ReportTentaclesShadowingRoll() {
		super();
	}

	public ReportTentaclesShadowingRoll(Skill pSkill, String pDefenderId, int pRoll, boolean pSuccessful,
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

	public int getRoll() {
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
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
		return jsonObject;
	}

	public ReportTentaclesShadowingRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fSkill = (Skill) IJsonOption.SKILL.getFrom(game, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(game, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(game, jsonObject);
		fReRolled = IJsonOption.RE_ROLLED.getFrom(game, jsonObject);
		return this;
	}

}
