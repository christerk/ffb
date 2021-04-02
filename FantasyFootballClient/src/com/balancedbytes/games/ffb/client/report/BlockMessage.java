package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportBlock;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.BLOCK)
@RulesCollection(Rules.COMMON)
public class BlockMessage extends ReportMessageBase<ReportBlock> {

    @Override
    protected void render(ReportBlock report) {
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
  		setIndent(getIndent() + 1);
    }
}
