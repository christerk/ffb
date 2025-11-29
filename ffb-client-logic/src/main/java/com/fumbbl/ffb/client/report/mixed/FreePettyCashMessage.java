package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportFreePettyCash;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.FREE_PETTY_CASH)
@RulesCollection(Rules.COMMON)
@RulesCollection(Rules.BB2025)
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
