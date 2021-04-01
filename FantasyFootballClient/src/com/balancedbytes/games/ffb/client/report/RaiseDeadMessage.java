package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportRaiseDead;

@ReportMessageType(ReportId.RAISE_DEAD)
@RulesCollection(Rules.COMMON)
public class RaiseDeadMessage extends ReportMessageBase<ReportRaiseDead> {

    public RaiseDeadMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportRaiseDead report) {
  		Player<?> raisedPlayer = game.getPlayerById(report.getPlayerId());
  		print(getIndent(), false, raisedPlayer);
  		if (report.isNurglesRot()) {
  			print(getIndent(), " has been infected with Nurgle's Rot and will join team ");
  		} else {
  			print(getIndent(), " is raised from the dead to join team ");
  		}
  		if (game.getTeamHome().hasPlayer(raisedPlayer)) {
  			print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
  		} else {
  			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
  		}
  		if (report.isNurglesRot()) {
  			println(getIndent(), TextStyle.NONE, " as a Rotter in the next game.");
  		} else {
  			println(getIndent(), TextStyle.NONE, " as a Zombie.");
  		}
    }
}
