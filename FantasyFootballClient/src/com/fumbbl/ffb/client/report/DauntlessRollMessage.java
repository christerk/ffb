package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportDauntlessRoll;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.StringTool;

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
  			status.append(" uses Dauntless to push ").append(player.getPlayerGender().getSelf()).append(" to strength ")
  				.append(report.getStrength());
  		} else {
  			status.append(" fails to push ").append(player.getPlayerGender().getGenitive()).append(" strength");
  		}
  		print(getIndent() + 1, status.toString());
  		if (StringTool.isProvided(report.getDefenderId())) {
  			Player<?> defender = game.getPlayerById(report.getDefenderId());
  			if (defender != null) {
					print(getIndent() +1, " to match ");
					print(getIndent() + 1, false, defender);
			  }
		  }
  		println(getIndent() + 1, ".");
    }

}
