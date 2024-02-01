package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportSelectBlitzTarget;

@ReportMessageType(ReportId.SELECT_BLITZ_TARGET)
@RulesCollection(Rules.BB2020)
public class SelectBlitzTargetMessage extends ReportMessageBase<ReportSelectBlitzTarget> {

    @Override
    protected void render(ReportSelectBlitzTarget report) {
  		Player<?> attacker = game.getPlayerById(report.getAttacker());
  		Player<?> defender = game.getPlayerById(report.getDefender());

  		print(getIndent(), teamStyleForPlayer(attacker), attacker.getName());
  		print(getIndent(), TextStyle.NONE, " targets ");
  		print(getIndent(), teamStyleForPlayer(defender), defender.getName());
  		println(getIndent(), TextStyle.NONE, ".");
    }
    
  	private TextStyle teamStyleForPlayer(Player<?> player) {
  		return game.getTeamHome().hasPlayer(player) ? TextStyle.HOME : TextStyle.AWAY;
  	}    
}
