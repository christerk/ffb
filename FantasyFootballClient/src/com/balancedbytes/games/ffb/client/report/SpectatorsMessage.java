package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSpectators;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.SPECTATORS)
@RulesCollection(Rules.COMMON)
public class SpectatorsMessage extends ReportMessageBase<ReportSpectators> {

    @Override
    protected void render(ReportSpectators report) {
  		setIndent(0);
  		StringBuilder status = new StringBuilder();
  		int[] fanRollHome = report.getSpectatorRollHome();
  		status.append("Spectator Roll Home Team [ ").append(fanRollHome[0]).append(" ][ ").append(fanRollHome[1])
  			.append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		int rolledTotalHome = fanRollHome[0] + fanRollHome[1];
  		status.append("Rolled Total of ").append(rolledTotalHome);
  		int fanFactorHome = game.getTeamHome().getFanFactor();
  		status.append(" + ").append(fanFactorHome).append(" Fan Factor");
  		status.append(" = ").append(rolledTotalHome + fanFactorHome);
  		println(getIndent() + 1, status.toString());
  		status = new StringBuilder();
  		status.append(StringTool.formatThousands(report.getSpectatorsHome())).append(" fans have come to support ");
  		print(getIndent() + 1, status.toString());
  		print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  		println(getIndent() + 1, ".");
  		status = new StringBuilder();
  		int[] fanRollAway = report.getSpectatorRollAway();
  		status.append("Spectator Roll Away Team [ ").append(fanRollAway[0]).append(" ][ ").append(fanRollAway[1])
  			.append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		int rolledTotalAway = fanRollAway[0] + fanRollAway[1];
  		status.append("Rolled Total of ").append(rolledTotalAway);
  		int fanFactorAway = game.getTeamAway().getFanFactor();
  		status.append(" + ").append(fanFactorAway).append(" Fan Factor");
  		status.append(" = ").append(rolledTotalAway + fanFactorAway);
  		println(getIndent() + 1, status.toString());
  		status = new StringBuilder();
  		status.append(StringTool.formatThousands(report.getSpectatorsAway())).append(" fans have come to support ");
  		print(getIndent() + 1, status.toString());
  		print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  		println(getIndent() + 1, ".");
  		status = new StringBuilder();
  		if (report.getFameHome() > report.getFameAway()) {
  			status.append("Team ").append(game.getTeamHome().getName());
  			if (report.getFameHome() - report.getFameAway() > 1) {
  				status.append(" have the whole audience with them (FAME +2)!");
  			} else {
  				status.append(" have a fan advantage (FAME +1) for the game.");
  			}
  			println(getIndent(), TextStyle.HOME_BOLD, status.toString());
  		} else if (report.getFameAway() > report.getFameHome()) {
  			status.append("Team ").append(game.getTeamAway().getName());
  			if (report.getFameAway() - report.getFameHome() > 1) {
  				status.append(" have the whole audience with them (FAME +2)!");
  			} else {
  				status.append(" have a fan advantage (FAME +1) for the game.");
  			}
  			println(getIndent(), TextStyle.AWAY_BOLD, status.toString());
  		} else {
  			println(getIndent(), TextStyle.BOLD, "Both teams have equal fan support (FAME 0).");
  		}
		}
}
