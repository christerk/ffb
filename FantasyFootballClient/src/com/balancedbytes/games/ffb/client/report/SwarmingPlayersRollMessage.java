package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSwarmingRoll;

@ReportMessageType(ReportId.SWARMING_PLAYERS_ROLL)
@RulesCollection(Rules.COMMON)
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
