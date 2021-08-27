package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.bb2016.ReportFanFactorRollPostMatch;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.ArrayTool;

@ReportMessageType(ReportId.FAN_FACTOR_ROLL_POST_MATCH)
@RulesCollection(Rules.BB2016)
public class FanFactorRollPostMatchMessage extends ReportMessageBase<ReportFanFactorRollPostMatch> {

    @Override
    protected void render(ReportFanFactorRollPostMatch report) {
  		StringBuilder status = new StringBuilder();
  		if (ArrayTool.isProvided(report.getFanFactorRollHome())) {
  			status.append("Fan Factor Roll Home Team ");
  			int[] fanFactorRollHome = report.getFanFactorRollHome();
  			for (int i = 0; i < fanFactorRollHome.length; i++) {
  				status.append("[ ").append(fanFactorRollHome[i]).append(" ]");
  			}
  		} else {
  			status.append("Fan Factor: Concession of Home Team");
  		}
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		status.append("FanFactor ").append(game.getTeamHome().getFanFactor());
  		if (report.getFanFactorModifierHome() < 0) {
  			status.append(" - ").append(Math.abs(report.getFanFactorModifierHome()));
  		} else {
  			status.append(" + ").append(report.getFanFactorModifierHome());
  		}
  		status.append(" = ").append(game.getTeamHome().getFanFactor() + report.getFanFactorModifierHome());
  		println(getIndent() + 1, TextStyle.NONE, status.toString());
  		print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  		if (report.getFanFactorModifierHome() > 0) {
  			println(getIndent() + 1, TextStyle.NONE, " win some new fans.");
  		} else if (report.getFanFactorModifierHome() < 0) {
  			println(getIndent() + 1, TextStyle.NONE, " lose some fans.");
  		} else {
  			println(getIndent() + 1, TextStyle.NONE, " keep their fans.");
  		}

  		status = new StringBuilder();
  		if (ArrayTool.isProvided(report.getFanFactorRollAway())) {
  			status.append("Fan Factor Roll Away Team ");
  			int[] fanFactorRollAway = report.getFanFactorRollAway();
  			for (int i = 0; i < fanFactorRollAway.length; i++) {
  				status.append("[ ").append(fanFactorRollAway[i]).append(" ]");
  			}
  		} else {
  			status.append("Fan Factor: Concession of Away Team");
  		}
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		status.append("FanFactor ").append(game.getTeamAway().getFanFactor());
  		if (report.getFanFactorModifierAway() < 0) {
  			status.append(" - ").append(Math.abs(report.getFanFactorModifierAway()));
  		} else {
  			status.append(" + ").append(report.getFanFactorModifierAway());
  		}
  		status.append(" = ").append(game.getTeamAway().getFanFactor() + report.getFanFactorModifierAway());
  		println(getIndent() + 1, TextStyle.NONE, status.toString());
  		print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  		if (report.getFanFactorModifierAway() > 0) {
  			println(getIndent() + 1, TextStyle.NONE, " win some new fans.");
  		} else if (report.getFanFactorModifierAway() < 0) {
  			println(getIndent() + 1, TextStyle.NONE, " lose some fans.");
  		} else {
  			println(getIndent() + 1, TextStyle.NONE, " keep their fans.");
  		}
    }
}
