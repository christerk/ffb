package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportFanFactor;

@ReportMessageType(ReportId.FAN_FACTOR)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
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
