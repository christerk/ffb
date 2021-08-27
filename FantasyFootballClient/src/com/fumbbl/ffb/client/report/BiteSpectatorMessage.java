package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportBiteSpectator;
import com.fumbbl.ffb.report.ReportId;

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
