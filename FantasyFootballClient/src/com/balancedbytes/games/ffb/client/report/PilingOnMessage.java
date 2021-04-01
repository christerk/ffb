package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPilingOn;

@ReportMessageType(ReportId.PILING_ON)
@RulesCollection(Rules.COMMON)
public class PilingOnMessage extends ReportMessageBase<ReportPilingOn> {

    public PilingOnMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportPilingOn report) {
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		if (player != null) {
  			Skill skill = player.getSkillWithProperty(NamedProperties.canPileOnOpponent);
  			if (skill != null) {
  				int indent = getIndent() + 1;
  				print(indent, false, player);
  				StringBuilder status = new StringBuilder();
  				if (!report.isUsed()) {
  					status.append(" does not use ").append(skill.getName()).append(".");
  				} else {
  					status.append(" uses ").append(skill.getName()).append(" to re-roll ");
  					status.append(report.isReRollInjury() ? "Injury" : "Armor").append(".");
  				}
  				println(indent, status.toString());
  			}
  		}
    }
}
