package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportCheeringFans;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.KICKOFF_CHEERING_FANS)
@RulesCollection(RulesCollection.Rules.BB2025)
public class CheeringFansMessage extends ReportMessageBase<ReportCheeringFans> {

	@Override
	protected void render(ReportCheeringFans report) {
		int homeTemps = game.getTurnDataHome().getInducementSet().value(Usage.ADD_CHEERLEADER);
		int awayTemps = game.getTurnDataAway().getInducementSet().value(Usage.ADD_CHEERLEADER);

		StringBuilder status = new StringBuilder();

		status.append("Cheering Fans Roll Home Team [ ").append(report.getRollHome()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalHome = report.getRollHome()
			+ game.getTeamHome().getCheerleaders() + homeTemps;
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollHome());
		status.append(" + ").append(game.getTeamHome().getCheerleaders()).append(" Cheerleaders");
		if (homeTemps > 0) {
			status.append(" + ").append(homeTemps).append(" Temp Agency Cheerleaders");
		}
		status.append(" = ").append(totalHome).append(".");
		println(getIndent() + 1, status.toString());
		status = new StringBuilder();
		status.append("Cheering Fans Roll Away Team [ ").append(report.getRollAway()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalAway = report.getRollAway()
			+ game.getTeamAway().getCheerleaders() + awayTemps;
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollAway());
		status.append(" + ").append(game.getTeamAway().getCheerleaders()).append(" Cheerleaders");
		if (awayTemps > 0) {
			status.append(" + ").append(awayTemps).append(" Temp Agency Cheerleaders");
		}
		status.append(" = ").append(totalAway).append(".");
		println(getIndent() + 1, status.toString());

		if (report.getTeamId() == null) {
			println(getIndent(), "Neither team gains a Prayer to Nuffle.");
		} else {
			if (report.getTeamId().equals(game.getTeamHome().getId())) {
				print(getIndent(), "Team ");
				print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
			} else {
				print(getIndent(), "Team ");
				print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
			}
			if (report.isPrayerAvailable()) {
				println(getIndent(), " gains a Prayer to Nuffle.");
			} else {
				println(getIndent(), " would gain a Prayer to Nuffle but all are in effect.");
			}
		}
	}
}
