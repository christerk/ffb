package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportWizardUse extends NoDiceReport {

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

	public ReportWizardUse initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fWizardSpell = (SpecialEffect) IJsonOption.WIZARD_SPELL.getFrom(source, jsonObject);
		return this;
	}

}
