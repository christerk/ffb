package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSwarmingRoll;

@ReportMessageType(ReportId.SWARMING_PLAYERS_ROLL)
@RulesCollection(Rules.BB2016)
public class SwarmingPlayersRollMessage extends ReportMessageBase<ReportSwarmingRoll> {

    @Override
    protected void render(ReportSwarmingRoll report) {
  		Team team = game.getTeamById(report.getTeamId());
  		TextStyle style = game.getTeamHome() == team ? TextStyle.HOME_BOLD : TextStyle.AWAY_BOLD;
  		println(0, TextStyle.ROLL, "Swarming Roll [" + report.getAmount() + "]");
  		print(1, style, team.getName());
  		println(1, TextStyle.NONE, " are allowed to place " + report.getAmount() + " swarming players.");
    }
}
