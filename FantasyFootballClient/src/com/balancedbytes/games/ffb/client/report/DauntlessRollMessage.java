package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportDauntlessRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.DAUNTLESS_ROLL)
@RulesCollection(Rules.COMMON)
public class DauntlessRollMessage extends ReportMessageBase<ReportDauntlessRoll> {

    @Override
    protected void render(ReportDauntlessRoll report) {
  		Player<?> player = game.getActingPlayer().getPlayer();
  		StringBuilder status = new StringBuilder();
  		status.append("Dauntless Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		status = new StringBuilder();
  		if (report.isSuccessful()) {
  			status.append(" uses Dauntless to push ").append(player.getPlayerGender().getSelf()).append(" to Strength ")
  				.append(report.getStrength()).append(".");
  		} else {
  			status.append(" fails to push ").append(player.getPlayerGender().getGenitive()).append(" strength.");
  		}
  		println(getIndent() + 1, status.toString());
    }

}
