package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPlayerAction;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.PLAYER_ACTION)
@RulesCollection(Rules.COMMON)
public class PlayerActionMessage extends ReportMessageBase<ReportPlayerAction> {

    @Override
    protected void render(ReportPlayerAction report) {
  		setIndent(0);
  		Player<?> player = game.getPlayerById(report.getActingPlayerId());
  		PlayerAction playerAction = report.getPlayerAction();
  		String actionDescription = (playerAction != null) ? playerAction.getDescription() : null;
  		if ((player != null) && StringTool.isProvided(actionDescription)) {
  			print(getIndent(), true, player);
  			println(getIndent(), TextStyle.BOLD, " " + actionDescription + ".");
  		}
  		setIndent(getIndent() + 1);
    }
}
