package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2016.ReportNervesOfSteel;

@ReportMessageType(ReportId.NERVES_OF_STEEL)
@RulesCollection(Rules.BB2016)
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
