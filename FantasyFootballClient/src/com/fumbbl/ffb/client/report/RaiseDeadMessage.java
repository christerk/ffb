package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportRaiseDead;

@ReportMessageType(ReportId.RAISE_DEAD)
@RulesCollection(Rules.COMMON)
public class RaiseDeadMessage extends ReportMessageBase<ReportRaiseDead> {

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