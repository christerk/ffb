package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPushback;

@ReportMessageType(ReportId.PUSHBACK)
@RulesCollection(Rules.COMMON)
public class PushbackMessage extends ReportMessageBase<ReportPushback> {

    public PushbackMessage(StatusReport statusReport) {
        super(statusReport);
    }

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
