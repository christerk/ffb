package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.CHAINSAW_ROLL)
@RulesCollection(Rules.COMMON)
public class ChainsawRollMessage extends ReportMessageBase<ReportSkillRoll> {

    public ChainsawRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportSkillRoll report) {
  		Player<?> player = game.getActingPlayer().getPlayer();
  		StringBuilder status = new StringBuilder();
  		status.append("Chainsaw Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		status = new StringBuilder();
  		if (report.isSuccessful()) {
  			status.append(" uses ").append(player.getPlayerGender().getGenitive()).append(" Chainsaw.");
  		} else {
  			status.append("'s Chainsaw kicks back to hurt ").append(player.getPlayerGender().getDative()).append(".");
  		}
  		println(getIndent() + 1, status.toString());
    }
}
