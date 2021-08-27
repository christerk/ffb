package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPlayerAction;
import com.fumbbl.ffb.util.StringTool;

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
