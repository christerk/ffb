package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportBribesRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.BRIBES_ROLL)
@RulesCollection(Rules.COMMON)
public class BribesRollMessage extends ReportMessageBase<ReportBribesRoll> {

    public BribesRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportBribesRoll report) {
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		StringBuilder status = new StringBuilder();
  		status.append("Bribes Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		if (report.isSuccessful()) {
  			print(getIndent() + 1, TextStyle.NONE, "The ref refrains from penalizing ");
  			print(getIndent() + 1, false, player);
  			status = new StringBuilder();
  			status.append(" and ").append(player.getPlayerGender().getNominative()).append(" remains in the game.");
  			println(getIndent() + 1, TextStyle.NONE, status.toString());
  		} else {
  			print(getIndent() + 1, TextStyle.NONE, "The ref appears to be unimpressed and ");
  			print(getIndent() + 1, false, player);
  			println(getIndent() + 1, TextStyle.NONE, " must leave the game.");
  		}
    }
}
