package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPenaltyShootout;

@ReportMessageType(ReportId.PENALTY_SHOOTOUT)
@RulesCollection(Rules.COMMON)
public class PenaltyShootoutMessage extends ReportMessageBase<ReportPenaltyShootout> {

    @Override
    protected void render(ReportPenaltyShootout report) {
  		int penaltyScoreHome = report.getRollHome() + report.getReRollsLeftHome();
  		print(0, TextStyle.ROLL, "Penalty Shootout Roll Home [" + report.getRollHome() + "]");
  		print(0, TextStyle.ROLL, " + " + report.getReRollsLeftHome() + " ReRolls");
  		println(0, TextStyle.ROLL, " = " + penaltyScoreHome);
  		int penaltyScoreAway = report.getRollAway() + report.getReRollsLeftAway();
  		print(0, TextStyle.ROLL, "Penalty Shootout Roll Away [" + report.getRollAway() + "]");
  		print(0, TextStyle.ROLL, " + " + report.getReRollsLeftAway() + " ReRolls");
  		println(0, TextStyle.ROLL, " = " + penaltyScoreAway);
  		if (penaltyScoreHome > penaltyScoreAway) {
  			print(1, TextStyle.HOME, game.getTeamHome().getName());
  			println(1, TextStyle.NONE, " win the penalty shootout.");
  		} else {
  			print(1, TextStyle.AWAY, game.getTeamAway().getName());
  			println(1, TextStyle.NONE, " win the penalty shootout.");
  		}
    }
}
