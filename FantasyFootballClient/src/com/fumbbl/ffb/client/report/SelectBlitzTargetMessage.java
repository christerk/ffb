package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSelectBlitzTarget;

@ReportMessageType(ReportId.SELECT_BLITZ_TARGET)
@RulesCollection(Rules.COMMON)
public class SelectBlitzTargetMessage extends ReportMessageBase<ReportSelectBlitzTarget> {

    @Override
    protected void render(ReportSelectBlitzTarget report) {
  		Player<?> attacker = game.getPlayerById(report.getAttacker());
  		Player<?> defender = game.getPlayerById(report.getDefender());

  		print(getIndent() + 1, teamStyleForPlayer(attacker), attacker.getName());
  		print(getIndent() + 1, TextStyle.NONE, " targets ");
  		print(getIndent() + 1, teamStyleForPlayer(defender), defender.getName());
  		println(getIndent() + 1, TextStyle.NONE, ".");
    }
    
  	private TextStyle teamStyleForPlayer(Player<?> player) {
  		return game.getTeamHome().hasPlayer(player) ? TextStyle.HOME : TextStyle.AWAY;
  	}    
}
