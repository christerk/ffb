package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportHandOver;
import com.balancedbytes.games.ffb.report.ReportId;

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
