package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportBlock;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.BLOCK)
@RulesCollection(Rules.COMMON)
public class BlockMessage extends ReportMessageBase<ReportBlock> {

    @Override
    protected void render(ReportBlock report) {
    	setIndent(1);
  		ActingPlayer actingPlayer = game.getActingPlayer();
  		Player<?> attacker = actingPlayer.getPlayer();
  		Player<?> defender = game.getPlayerById(report.getDefenderId());

  		print(getIndent(), true, attacker);
  		if (actingPlayer.getPlayerAction() == PlayerAction.BLITZ) {
  			print(getIndent(), TextStyle.BOLD, " blitzes ");
  		} else {
  			print(getIndent(), TextStyle.BOLD, " blocks ");
  		}
  		print(getIndent(), true, defender);
  		println(getIndent(), TextStyle.BOLD, ":");
    }
}
