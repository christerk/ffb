package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportLeader;

@ReportMessageType(ReportId.LEADER)
@RulesCollection(Rules.COMMON)
public class LeaderMessage extends ReportMessageBase<ReportLeader> {

    @Override
    protected void render(ReportLeader report) {
  		StringBuilder status = new StringBuilder();
  		LeaderState leaderState = report.getLeaderState();
		setIndent(0);
  		if (LeaderState.AVAILABLE.equals(leaderState)) {
  			printTeamName(game, false, report.getTeamId());
  			status.append(" gain a Leader re-roll.");
  			print(getIndent() + 1, status.toString());
  		} else {
  			status.append("Leader re-roll removed from ");
  			print(getIndent() + 1, status.toString());
  			printTeamName(game, false, report.getTeamId());
  		}
  		println(getIndent() + 1, ".");
    }
}
