package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.bb2020.ReportFanFactor;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.FAN_FACTOR)
@RulesCollection(Rules.COMMON)
public class FanFactorMessage extends ReportMessageBase<ReportFanFactor> {

    @Override
    protected void render(ReportFanFactor report) {
  		println(getIndent(), TextStyle.ROLL, "Fan Factor Roll [" + report.getRoll() + "]");

  		print(getIndent() + 1, "Team ");
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  		} else {
  			print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  		}

  		String status = " has " +
  				report.getResult() +
  				"k fans behind them (" +
  				report.getDedicatedFans() +
  				"k Dedicated Fans and " +
  				report.getRoll() +
  				"k fair-weather fans)";

  		println(getIndent() + 1, status);
    }
}
