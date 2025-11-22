package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportKickoffOfficiousRef;

@ReportMessageType(ReportId.KICKOFF_OFFICIOUS_REF)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class KickoffOfficiousRefMessage extends ReportMessageBase<ReportKickoffOfficiousRef> {

    @Override
    protected void render(ReportKickoffOfficiousRef report) {
  		GameResult gameResult = game.getGameResult();
  		StringBuilder status = new StringBuilder();
  		status.append("Officious Ref Roll Home Team [ ").append(report.getRollHome()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		int totalHome = report.getRollHome() + gameResult.getTeamResultHome().getFanFactor();
  		status = new StringBuilder();
  		status.append("Rolled ").append(report.getRollHome());
  		status.append(" + ").append(gameResult.getTeamResultHome().getFanFactor()).append(" Fan Factor");
  		status.append(" = ").append(totalHome).append(".");
  		println(getIndent() + 1, status.toString());
  		status = new StringBuilder();
  		status.append("Officious Ref Roll Away Team [ ").append(report.getRollAway()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		int totalAway = report.getRollAway() + gameResult.getTeamResultAway().getFanFactor();
  		status = new StringBuilder();
  		status.append("Rolled ").append(report.getRollAway());
  		status.append(" + ").append(gameResult.getTeamResultAway().getFanFactor()).append(" Fan Factor");
  		status.append(" = ").append(totalAway).append(".");
  		println(getIndent() + 1, status.toString());
  		for (String playerId : report.getPlayerIds()) {
  			Player<?> player = game.getPlayerById(playerId);
  			print(getIndent() + 1, false, player);
  			println(getIndent() + 1, " gets into an argument with the ref.");
  		}
    }
}
