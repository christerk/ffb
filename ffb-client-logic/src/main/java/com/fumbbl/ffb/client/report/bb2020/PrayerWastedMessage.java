package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPrayerWasted;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PRAYER_WASTED)
public class PrayerWastedMessage extends ReportMessageBase<ReportPrayerWasted> {

	@Override
	protected void render(ReportPrayerWasted report) {
		println(getIndent() + 1, TextStyle.EXPLANATION, "Prayer " + report.getName() + " is wasted since there are no eligible players/skills.");
	}
}
