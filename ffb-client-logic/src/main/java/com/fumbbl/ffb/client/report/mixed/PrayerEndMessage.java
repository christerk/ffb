package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportPrayerEnd;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PRAYER_END)
@RulesCollection(RulesCollection.Rules.BB2025)
public class PrayerEndMessage extends ReportMessageBase<ReportPrayerEnd> {

	@Override
	protected void render(ReportPrayerEnd report) {
		println(getIndent(), TextStyle.ROLL, "Prayer effect ended: " + report.getPrayer().getName());
		println(getIndent() + 2, TextStyle.EXPLANATION, "Effect was: " + report.getPrayer().getDescription());
	}
}
