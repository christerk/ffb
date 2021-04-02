package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPettyCash;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.PETTY_CASH)
@RulesCollection(Rules.COMMON)
public class PettyCashMessage extends ReportMessageBase<ReportPettyCash> {

    @Override
    protected void render(ReportPettyCash report) {
  		if (!statusReport.fPettyCashReportReceived) {
  			statusReport.fPettyCashReportReceived = true;
  			println(getIndent(), TextStyle.BOLD, "Transfer Petty Cash");
  		}
  		print(getIndent() + 1, "Team ");
  		Team team = null;
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  			team = game.getTeamHome();
  		} else {
  			print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  			team = game.getTeamAway();
  		}
  		StringBuilder status = new StringBuilder();
  		status.append(" transfers ");
  		if (report.getGold() > 0) {
  			status.append(StringTool.formatThousands(report.getGold()));
  			status.append(" gold");
  		} else {
  			status.append("nothing");
  		}
  		status.append(" from the Treasury into Petty Cash.");
  		println(getIndent() + 1, status.toString());
  		if (report.getGold() > team.getTreasury()) {
  			status = new StringBuilder();
  			status.append("They received an extra ");
  			status.append(StringTool.formatThousands(report.getGold() - team.getTreasury()));
  			status.append(" gold for being the underdog.");
  			println(getIndent() + 1, status.toString());
  		}
    }
}
