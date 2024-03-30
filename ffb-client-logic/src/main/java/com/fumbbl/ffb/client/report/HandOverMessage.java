package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportHandOver;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.HAND_OVER)
@RulesCollection(Rules.COMMON)
public class HandOverMessage extends ReportMessageBase<ReportHandOver> {

    @Override
    protected void render(ReportHandOver report) {
  		Player<?> thrower = game.getActingPlayer().getPlayer();
  		Player<?> catcher = game.getPlayerById(report.getCatcherId());
  		print(getIndent(), true, thrower);
  		print(getIndent(), TextStyle.BOLD, " hands over the ball to ");
  		print(getIndent(), true, catcher);
  		println(getIndent(), TextStyle.BOLD, ":");
    }
}
