package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportWizardUse implements IReport {

	private String fTeamId;
	private SpecialEffect fWizardSpell;

	public ReportWizardUse() {
		super();
	}

	public ReportWizardUse(String pTeamId, SpecialEffect pWizardSpell) {
		fTeamId = pTeamId;
		fWizardSpell = pWizardSpell;
	}

	public ReportId getId() {
		return ReportId.WIZARD_USE;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public SpecialEffect getWizardSpell() {
		return fWizardSpell;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportWizardUse(getTeamId(), getWizardSpell());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.WIZARD_SPELL.addTo(jsonObject, fWizardSpell);
		return jsonObject;
	}

	public ReportWizardUse initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fWizardSpell = (SpecialEffect) IJsonOption.WIZARD_SPELL.getFrom(game, jsonObject);
		return this;
	}

}
