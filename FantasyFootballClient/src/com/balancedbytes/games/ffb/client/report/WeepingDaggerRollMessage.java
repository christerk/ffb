package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;

@ReportMessageType(ReportId.WEEPING_DAGGER_ROLL)
@RulesCollection(Rules.COMMON)
public class WeepingDaggerRollMessage extends ReportMessageBase<ReportSkillRoll> {

    @Override
    protected void render(ReportSkillRoll report) {
  		String playerId = report.getPlayerId();

  		Player<?> player = game.getPlayerById(playerId);

  		StringBuilder status = new StringBuilder();
  		status.append("Weeping Dagger Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			println(getIndent() + 1, " poisons " + player.getPlayerGender().getGenitive() + " opponent.");
  		} else {
  			println(getIndent() + 1, " fails to poison " + player.getPlayerGender().getGenitive() + " opponent.");
  		}
    }
}
