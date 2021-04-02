package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportReferee;

@ReportMessageType(ReportId.REFEREE)
@RulesCollection(Rules.COMMON)
public class RefereeMessage extends ReportMessageBase<ReportReferee> {

    @Override
    protected void render(ReportReferee report) {
  		ActingPlayer actingPlayer = game.getActingPlayer();
  		if (report.isFoulingPlayerBanned()) {
  			print(getIndent(), "The referee spots the foul and bans ");
  			print(getIndent(), false, actingPlayer.getPlayer());
  			println(getIndent(), " from the game.");
  		} else {
  			println(getIndent(), "The referee didn't spot the foul.");
  		}
    }
}
