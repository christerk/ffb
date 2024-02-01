package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPrayerRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PRAYER_ROLL)
public class PrayerRollMessage extends ReportMessageBase<ReportPrayerRoll> {

	@Override
	protected void render(ReportPrayerRoll report) {
		Prayer prayer = game.<PrayerFactory>getFactory(FactoryType.Factory.PRAYER).forRoll(report.getRoll());
		println(getIndent(), TextStyle.ROLL, "Prayer Roll [ " + report.getRoll() + " ]");
		println(getIndent() + 1, TextStyle.BOLD, prayer.getName());
		println(getIndent() + 2, TextStyle.EXPLANATION, prayer.getDuration().getDescription() + ": " + prayer.getDescription());
	}
}
