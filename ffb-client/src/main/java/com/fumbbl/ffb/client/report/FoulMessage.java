package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportFoul;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.FOUL)
@RulesCollection(Rules.COMMON)
public class FoulMessage extends ReportMessageBase<ReportFoul> {

    @Override
    protected void render(ReportFoul report) {
  		Player<?> attacker = game.getActingPlayer().getPlayer();
  		Player<?> defender = game.getPlayerById(report.getDefenderId());
  		print(getIndent(), true, attacker);
  		print(getIndent(), TextStyle.BOLD, " fouls ");
  		print(getIndent(), true, defender);
  		println(getIndent(), ":");
  		setIndent(getIndent() + 1);
    }
}
