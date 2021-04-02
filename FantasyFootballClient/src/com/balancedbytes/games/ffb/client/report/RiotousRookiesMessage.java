package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportRiotousRookies;

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
