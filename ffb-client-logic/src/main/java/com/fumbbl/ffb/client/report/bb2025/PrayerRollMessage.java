package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.factory.mixed.PrayerFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportPrayerRoll;

@ReportMessageType(ReportId.PRAYER_ROLL)
@RulesCollection(RulesCollection.Rules.BB2025)
public class PrayerRollMessage extends ReportMessageBase<ReportPrayerRoll> {

	@Override
	protected void render(ReportPrayerRoll report) {
		Prayer prayer = game.<PrayerFactory>getFactory(FactoryType.Factory.PRAYER).forRoll(report.getRoll());
		print(getIndent(), TextStyle.ROLL, "Prayer Roll [ " + report.getRoll() + " ] for ");
		println(getIndent(), report.isHomeTeam() ? TextStyle.HOME_BOLD : TextStyle.AWAY_BOLD, report.getTeamName());
		println(getIndent() + 1, TextStyle.BOLD, prayer.getName());
		println(getIndent() + 2, TextStyle.EXPLANATION, prayer.getDuration().getDescription() + ": " + prayer.getDescription());
	}
}
