package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.bb2020.ReportFreePettyCash;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.FREE_PETTY_CASH)
@RulesCollection(Rules.COMMON)
public class FreePettyCashMessage extends ReportMessageBase<ReportFreePettyCash> {

    @Override
    protected void render(ReportFreePettyCash report) {
  		println(getIndent(), TextStyle.BOLD, "Assigning Petty Cash");

  		print(getIndent() + 1, "Team ");
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  		} else {
  			print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  		}
  		String status = " receives " +
  			StringTool.formatThousands(report.getGold()) +
  			" gold as petty cash from being the underdog before adding inducements.";
  		println(getIndent() + 1, status);
    }
}
