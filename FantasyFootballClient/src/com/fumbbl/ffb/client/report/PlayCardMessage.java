package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPlayCard;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.PLAY_CARD)
@RulesCollection(Rules.COMMON)
public class PlayCardMessage extends ReportMessageBase<ReportPlayCard> {

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
