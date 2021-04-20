package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportFoulAppearanceRoll;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.StringTool;

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
