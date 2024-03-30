package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportNervesOfSteel;

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
