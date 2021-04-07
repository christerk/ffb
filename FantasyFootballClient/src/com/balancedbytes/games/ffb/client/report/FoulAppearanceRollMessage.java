package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportFoulAppearanceRoll;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.FOUL_APPEARANCE_ROLL)
@RulesCollection(Rules.COMMON)
public class FoulAppearanceRollMessage extends ReportMessageBase<ReportFoulAppearanceRoll> {

    @Override
    protected void render(ReportFoulAppearanceRoll report) {
  		Player<?> player = game.getActingPlayer().getPlayer();
	    println(getIndent(), TextStyle.ROLL, "Foul Appearance Roll [ " + report.getRoll() + " ]");
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			print(getIndent() + 1, " resists the Foul Appearance");
  		} else {
  			print(getIndent() + 1, " cannot overcome the Foul Appearance");
  		}

  		if (StringTool.isProvided(report.getDefenderId())) {
  			Player<?> defender = game.getPlayerById(report.getDefenderId());
  			if (defender != null) {
  				print(getIndent() + 1, " of " );
				  print(getIndent() + 1, false, defender);
			  }
		  }

  		println(getIndent() + 1, ".");
    }
}
