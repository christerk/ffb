package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportWinningsRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.WINNINGS_ROLL)
@RulesCollection(Rules.COMMON)
public class WinningsRollMessage extends ReportMessageBase<ReportWinningsRoll> {

    public WinningsRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportWinningsRoll report) {

  		if ((report.getWinningsRollAway() == 0) && (report.getWinningsRollHome() > 0)) {
  			print(getIndent(), TextStyle.NONE, "Coach ");
  			print(getIndent(), TextStyle.HOME, game.getTeamHome().getCoach());
  			println(getIndent(), TextStyle.NONE, " re-rolls winnings.");
  		}
  		if ((report.getWinningsRollHome() == 0) && (report.getWinningsRollAway() > 0)) {
  			print(getIndent(), TextStyle.NONE, "Coach ");
  			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getCoach());
  			println(getIndent(), TextStyle.NONE, " re-rolls winnings.");
  		}

  		if (report.getWinningsRollHome() > 0) {
  			StringBuilder status = new StringBuilder();
  			status.append("Winnings Roll Home Team [ ").append(report.getWinningsRollHome()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  			status = new StringBuilder();
  			status.append(" earn ").append(StringTool.formatThousands(report.getWinningsHome())).append(" goldcoins.");
  			println(getIndent() + 1, TextStyle.NONE, status.toString());
  		}

  		if (report.getWinningsRollAway() > 0) {
  			StringBuilder status = new StringBuilder();
  			status.append("Winnings Roll Away Team [ ").append(report.getWinningsRollAway()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  			status = new StringBuilder();
  			status.append(" earn ").append(StringTool.formatThousands(report.getWinningsAway())).append(" in gold.");
  			println(getIndent() + 1, TextStyle.NONE, status.toString());
  		}

  		if ((report.getWinningsRollHome() == 0) && (report.getWinningsRollAway() == 0)) {
  			if (report.getWinningsHome() > 0) {
  				println(getIndent(), TextStyle.BOLD, "Winnings: Concession of Away Team");
  				print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  				print(getIndent() + 1, TextStyle.NONE, " win ");
  				print(getIndent() + 1, TextStyle.NONE, Integer.toString(report.getWinningsHome()));
  				println(getIndent() + 1, TextStyle.NONE, " in gold.");
  				print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  				println(getIndent() + 1, TextStyle.NONE, " get nothing.");
  			}
  			if (report.getWinningsAway() > 0) {
  				println(getIndent(), TextStyle.BOLD, "Winnings: Concession of Home Team");
  				print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  				print(getIndent() + 1, TextStyle.NONE, " win ");
  				print(getIndent() + 1, TextStyle.NONE, Integer.toString(report.getWinningsAway()));
  				println(getIndent() + 1, TextStyle.NONE, " in gold.");
  				print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  				println(getIndent() + 1, TextStyle.NONE, " get nothing.");
  			}
  		}
    }
}
