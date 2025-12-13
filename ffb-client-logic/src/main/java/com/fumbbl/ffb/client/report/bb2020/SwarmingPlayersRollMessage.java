package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportSwarmingRoll;

@ReportMessageType(ReportId.SWARMING_PLAYERS_ROLL)
@RulesCollection(Rules.BB2020)
public class SwarmingPlayersRollMessage extends ReportMessageBase<ReportSwarmingRoll> {

    @Override
    protected void render(ReportSwarmingRoll report) {
		if (report.getLimit() < 0) {
			renderLegacy(report);
		} else {
			Team team = game.getTeamById(report.getTeamId());
			TextStyle style = game.getTeamHome() == team ? TextStyle.HOME_BOLD : TextStyle.AWAY_BOLD;
			println(0, TextStyle.ROLL, "Swarming Roll [" + report.getRoll() + "]");
			print(1, style, team.getName());
			println(1, TextStyle.NONE, " have " + report.getLimit() + " swarming players on the pitch.");
			if (report.getAmount() == 0) {
				println(1, TextStyle.NONE, "They are not allowed to place any swarming players.");
			} else {
				println(1, TextStyle.NONE, "They are allowed to place " + report.getAmount() + " swarming players.");
			}
		}
	}

	private void renderLegacy(ReportSwarmingRoll report) {
		Team team = game.getTeamById(report.getTeamId());
		TextStyle style = game.getTeamHome() == team ? TextStyle.HOME_BOLD : TextStyle.AWAY_BOLD;
		println(0, TextStyle.ROLL, "Swarming Roll [" + report.getAmount() + "]");
		print(1, style, team.getName());
		println(1, TextStyle.NONE, " are allowed to place " + report.getAmount() + " swarming players.");
	}
}
