package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * This report is no longer used, it remains for compatibility with older
 * versions. Will be removed in a later version.
 * 
 * @author Georg Seipler
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportGameOptions implements IReport {

	private boolean fOvertime;
	private int fTurntime;
	// Sneaky Git works as it does now and like Guard for fouling assists
	private boolean fSneakyGitAsFoulGuard;
	// +1 to the AV for a foul if the fouler is not in an opposing tackle zone
	private boolean fFoulBonusOutsideTacklezone;
	// Right Stuff prevents Tackle from negating Dodge for Pow!
	private boolean fRightStuffCancelsTackle;
	// A player cannot use his skills that modify the Armour or Injury roll when
	// using a Piling On re-roll
	private boolean fPilingOnWithoutModifier;

	public ReportGameOptions() {
		super();
	}

	public void init(ReportGameOptions pReportGameOptions) {
		if (pReportGameOptions != null) {
			fOvertime = pReportGameOptions.isOvertime();
			fTurntime = pReportGameOptions.getTurntime();
			fSneakyGitAsFoulGuard = pReportGameOptions.isSneakyGitAsFoulGuard();
			fFoulBonusOutsideTacklezone = pReportGameOptions.isFoulBonusOutsideTacklezone();
			fRightStuffCancelsTackle = pReportGameOptions.isRightStuffCancelsTackle();
			fPilingOnWithoutModifier = pReportGameOptions.isPilingOnWithoutModifier();
		}
	}

	public ReportId getId() {
		return ReportId.GAME_OPTIONS;
	}

	public boolean isOvertime() {
		return fOvertime;
	}

	public int getTurntime() {
		return fTurntime;
	}

	public boolean isSneakyGitAsFoulGuard() {
		return fSneakyGitAsFoulGuard;
	}

	public boolean isFoulBonusOutsideTacklezone() {
		return fFoulBonusOutsideTacklezone;
	}

	public boolean isRightStuffCancelsTackle() {
		return fRightStuffCancelsTackle;
	}

	public boolean isPilingOnWithoutModifier() {
		return fPilingOnWithoutModifier;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		ReportGameOptions transformedReport = new ReportGameOptions();
		transformedReport.init(this);
		return transformedReport;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		// incomplete because it is no longer necessary
		return jsonObject;
	}

	public ReportGameOptions initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		// incomplete because it is no longer necessary
		return this;
	}

}
