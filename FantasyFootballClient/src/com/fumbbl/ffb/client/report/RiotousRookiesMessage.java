package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportRiotousRookies;

@ReportMessageType(ReportId.RIOTOUS_ROOKIES)
@RulesCollection(Rules.COMMON)
public class RiotousRookiesMessage extends ReportMessageBase<ReportRiotousRookies> {

    @Override
    protected void render(ReportRiotousRookies report) {
  		println(0, TextStyle.ROLL,
  				"Riotous Rookies Roll [ " + report.getRoll()[0] + " ][ " + report.getRoll()[1] + " ] + 1");
  			if (game.getTeamHome().getId().equals(report.getTeamId())) {
  				print(1, TextStyle.HOME, game.getTeamHome().getName());
  			} else {
  				print(1, TextStyle.AWAY, game.getTeamAway().getName());
  			}
  			println(1, TextStyle.NONE, " hires " + report.getAmount() + " Riotous Rookies for this game");
    }
}
