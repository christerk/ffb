package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportNervesOfSteel;
import com.fumbbl.ffb.report.bb2020.ReportThrowTeamMateRoll;

@ReportMessageType(ReportId.THROW_TEAM_MATE_ROLL)
@RulesCollection(Rules.BB2020)
public class ThrowTeamMateRollMessage extends ReportMessageBase<ReportThrowTeamMateRoll> {

    @Override
    protected void render(ReportThrowTeamMateRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> thrower = game.getActingPlayer().getPlayer();
  		Player<?> thrownPlayer = game.getPlayerById(report.getThrownPlayerId());
  		boolean canThrow =  thrower.getPassing() > 0;
  		if (!report.isReRolled()) {
  			print(getIndent(), true, thrower);
  			print(getIndent(), TextStyle.BOLD, " tries to throw ");
  			print(getIndent(), true, thrownPlayer);
  			println(getIndent(), TextStyle.BOLD, ":");
  		}
  		
  		status.append("Throw Team-Mate Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent() + 1, TextStyle.ROLL, status.toString());
  		print(getIndent() + 2, false, thrower);
  		if (report.isSuccessful()) {
			  print(getIndent() + 2, " throws ");
				print(getIndent() + 2, false, thrownPlayer);
			  status = new StringBuilder(" ");
  			if (report.getPassResult() == PassResult.ACCURATE) {
				  status.append("superbly");
			  } else {
				  status.append("successfully");
			  }
			  status.append(".");
  			println(getIndent() + 2, status.toString());
  		} else if (report.getPassResult() == PassResult.WILDLY_INACCURATE) {
			  print(getIndent() + 2, " lets ");
			  print(getIndent() + 2, false, thrownPlayer);
			  println(getIndent() + 2, " deviate.");
		  } else {
  			print(getIndent() + 2, " fumbles ");
			  print(getIndent() + 2, false, thrownPlayer);
			  println(getIndent() + 2, ".");
  		}
  		if (report.isSuccessful() && !report.isReRolled() && canThrow) {
  			neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  				.append("+ to avoid a fumble or terrible throw");
  		}
  		if (!report.isSuccessful() && !report.isReRolled() && canThrow) {
  			neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to have at least a successful throw");
  		}
  		if (neededRoll != null && canThrow) {
  			neededRoll.append(" (Roll ");
  			PassingDistance passingDistance = report.getPassingDistance();
 				neededRoll.append(" - ");
  			neededRoll.append(passingDistance.getModifier2020()).append(" ").append(passingDistance.getName());
  			neededRoll.append(statusReport.formatRollModifiers(report.getRollModifiers())).append(" > 1).");
  			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
  		setIndent(getIndent() + 1);
    }
}
