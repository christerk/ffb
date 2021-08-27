package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportChainsawRoll;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.CHAINSAW_ROLL)
@RulesCollection(Rules.COMMON)
public class ChainsawRollMessage extends ReportMessageBase<ReportChainsawRoll> {

    @Override
    protected void render(ReportChainsawRoll report) {
  		Player<?> player = game.getActingPlayer().getPlayer();
  		StringBuilder status = new StringBuilder();
  		status.append("Chainsaw Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		status = new StringBuilder();
  		if (report.isSuccessful()) {
  			status.append(" uses ").append(player.getPlayerGender().getGenitive()).append(" Chainsaw");
  		} else {
  			status.append("'s Chainsaw kicks back to hurt ").append(player.getPlayerGender().getDative());
  		}
  		print(getIndent() + 1, status.toString());

  		if (StringTool.isProvided(report.getDefenderId())) {
  			print(getIndent() + 1, " against ");
			  Player<?> defender = game.getPlayerById(report.getDefenderId());
			  if (defender != null) {
			  	print(getIndent() + 1 , false, defender);
			  }
		  }

  		println(getIndent() + 1, ".");
    }
}
