package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportMasterChefRoll;

@ReportMessageType(ReportId.MASTER_CHEF_ROLL)
@RulesCollection(Rules.COMMON)
public class MasterChefRollMessage extends ReportMessageBase<ReportMasterChefRoll> {

    @Override
    protected void render(ReportMasterChefRoll report) {
  		StringBuilder status = new StringBuilder();
  		int[] roll = report.getMasterChefRoll();
  		status.append("Master Chef Roll [ ").append(roll[0]).append(" ][ ").append(roll[1]).append(" ][ ").append(roll[2])
  			.append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		printTeamName(false, report.getTeamId());
  		status.append(" steal ");
  		if (report.getReRollsStolen() == 0) {
  			status.append(" no re-rolls from ");
  		} else if (report.getReRollsStolen() == 1) {
  			status.append(report.getReRollsStolen()).append(" re-roll from ");
  		} else {
  			status.append(report.getReRollsStolen()).append(" re-rolls from ");
  		}
  		print(getIndent() + 1, status.toString());
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			printTeamName(false, game.getTeamAway().getId());
  		} else {
  			printTeamName(false, game.getTeamHome().getId());
  		}
  		println(getIndent() + 1, ".");
    }
}
