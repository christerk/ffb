package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.report.ReportBlockChoice;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.UtilCards;

@ReportMessageType(ReportId.BLOCK_CHOICE)
@RulesCollection(Rules.COMMON)
public class BlockChoiceMessage extends ReportMessageBase<ReportBlockChoice> {

    @Override
    protected void render(ReportBlockChoice report) {
	    Player<?> defender = game.getPlayerById(report.getDefenderId());
	    StringBuilder status = new StringBuilder();
	    status.append("Block Result");
	    if (report.isShowNameInReport()) {
			  status.append(" against ");
			  status.append(defender.getName());
		  }
	    status.append(" [ ").append(report.getBlockResult().getName()).append(" ]");
	    println(getIndent(), TextStyle.ROLL, status.toString());
	    Player<?> attacker = game.getActingPlayer().getPlayer();
  		switch (report.getBlockResult()) {
  			case BOTH_DOWN:
  				if (attacker.hasSkillProperty(NamedProperties.preventFallOnBothDown)) {
  					print(getIndent() + 1, false, attacker);
  					status = new StringBuilder();
  					status
  						.append(" has been saved by ")
  						.append(attacker.getPlayerGender().getGenitive())
  						.append(" ")
  						.append(attacker.getSkillWithProperty(NamedProperties.preventFallOnBothDown))
  						.append(" skill.");
  					println(getIndent() + 1, status.toString());
  				}
  				if (defender.hasSkillProperty(NamedProperties.preventFallOnBothDown)) {
  					print(getIndent() + 1, false, defender);
  					status = new StringBuilder();
  					status
  						.append(" has been saved by ")
  						.append(defender.getPlayerGender().getGenitive())
  						.append(" ")
  						.append(defender.getSkillWithProperty(NamedProperties.preventFallOnBothDown))
  						.append(" skill.");
  					println(getIndent() + 1, status.toString());
  				}
  				break;
  			case POW_PUSHBACK:
  				if (UtilCards.hasSkillWithProperty(defender, NamedProperties.ignoreDefenderStumblesResult)
  					&& UtilCards.hasSkillToCancelProperty(attacker, NamedProperties.ignoreDefenderStumblesResult)) {
  					print(getIndent() + 1, false, attacker);
  					println(getIndent() + 1, " uses Tackle to bring opponent down.");
  				}
  				break;
  			default:
  				break;
  		}
    }
}
