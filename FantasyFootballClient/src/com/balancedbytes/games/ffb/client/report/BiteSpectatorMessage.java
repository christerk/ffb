package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportBiteSpectator;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.BITE_SPECTATOR)
@RulesCollection(Rules.COMMON)
public class BiteSpectatorMessage extends ReportMessageBase<ReportBiteSpectator> {

    @Override
    protected void render(ReportBiteSpectator report) {
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		if (player != null) {
  			print(getIndent(), true, player);
  			println(getIndent(), TextStyle.BOLD, " heads off to the spectator ranks to bite some beautiful maiden.");
  		}
    }
}
