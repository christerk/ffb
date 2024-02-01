package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.PushbackMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPushback;

@ReportMessageType(ReportId.PUSHBACK)
@RulesCollection(Rules.COMMON)
public class PushbackMessage extends ReportMessageBase<ReportPushback> {

    @Override
    protected void render(ReportPushback report) {
  		int indent = getIndent() + 1;
  		StringBuilder status = new StringBuilder();
  		Player<?> defender = game.getPlayerById(report.getDefenderId());
  		if (report.getPushbackMode() == PushbackMode.SIDE_STEP) {
  			print(indent, false, defender);
  			status.append(" uses Side Step to avoid being pushed.");
  			println(indent, status.toString());
  		}
  		if (report.getPushbackMode() == PushbackMode.GRAB) {
  			ActingPlayer actingPlayer = game.getActingPlayer();
  			print(indent, false, actingPlayer.getPlayer());
  			status.append(" uses Grab to place ").append(actingPlayer.getPlayer().getPlayerGender().getGenitive())
  				.append(" opponent.");
  			println(indent, status.toString());
  		}
    }
}
