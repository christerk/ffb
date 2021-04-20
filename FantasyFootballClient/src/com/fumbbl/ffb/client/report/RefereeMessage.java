package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportReferee;

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
