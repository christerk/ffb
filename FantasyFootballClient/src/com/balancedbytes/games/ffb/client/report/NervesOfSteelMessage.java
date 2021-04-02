package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportNervesOfSteel;

@ReportMessageType(ReportId.NERVES_OF_STEEL)
@RulesCollection(Rules.COMMON)
public class NervesOfSteelMessage extends ReportMessageBase<ReportNervesOfSteel> {

    @Override
    protected void render(ReportNervesOfSteel report) {
    	Player player = game.getPlayerById(report.getPlayerId());
    	String ballAction = report.getBallAction();
    	
  		if (player != null) {
  			print(getIndent(), false, player);
  			println(getIndent(), " is using Nerves of Steel to " + ballAction + " the ball.");
  		}
    }
}
