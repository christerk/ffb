package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPlayCard;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.PLAY_CARD)
@RulesCollection(Rules.COMMON)
public class PlayCardMessage extends ReportMessageBase<ReportPlayCard> {

    public PlayCardMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportPlayCard report) {
  		StringBuilder status = new StringBuilder();
  		status.append("Card ").append(report.getCard().getName());
  		if (StringTool.isProvided(report.getPlayerId())) {
  			status.append(" is played on ");
  		} else {
  			status.append(" is played.");
  		}
  		print(getIndent(), TextStyle.BOLD, status.toString());
  		if (StringTool.isProvided(report.getPlayerId())) {
  			Player<?> player = game.getPlayerById(report.getPlayerId());
  			print(getIndent(), true, player);
  			println(getIndent(), TextStyle.BOLD, ".");
  		} else {
  			println();
  		}
    }
}
