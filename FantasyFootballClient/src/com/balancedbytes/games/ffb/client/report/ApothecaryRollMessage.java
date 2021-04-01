package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportApothecaryRoll;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.ArrayTool;

@ReportMessageType(ReportId.APOTHECARY_ROLL)
@RulesCollection(Rules.COMMON)
public class ApothecaryRollMessage extends ReportMessageBase<ReportApothecaryRoll> {

    public ApothecaryRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportApothecaryRoll report) {
  		int[] casualtyRoll = report.getCasualtyRoll();
  		if (ArrayTool.isProvided(casualtyRoll)) {
  			println(getIndent(), TextStyle.BOLD, "Apothecary used.");
  			Player<?> player = game.getPlayerById(report.getPlayerId());
  			StringBuilder status = new StringBuilder();
  			status.append("Casualty Roll [ ").append(casualtyRoll[0]).append(" ][ ").append(casualtyRoll[1]).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			PlayerState injury = report.getPlayerState();
  			print(getIndent() + 1, false, player);
  			status = new StringBuilder();
  			status.append(" ").append(injury.getDescription()).append(".");
  			println(getIndent() + 1, status.toString());
  			SeriousInjury seriousInjury = report.getSeriousInjury();
  			if (seriousInjury != null) {
  				print(getIndent() + 1, false, player);
  				status = new StringBuilder();
  				status.append(" ").append(seriousInjury.getDescription()).append(".");
  				println(getIndent() + 1, status.toString());
  			}
  		}
    }
}
