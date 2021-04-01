package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportFoul;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.FOUL)
@RulesCollection(Rules.COMMON)
public class FoulMessage extends ReportMessageBase<ReportFoul> {

    public FoulMessage(StatusReport statusReport) {
        super(statusReport);
    }

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
