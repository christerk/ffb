package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPilingOn;

@ReportMessageType(ReportId.PILING_ON)
@RulesCollection(Rules.COMMON)
public class PilingOnMessage extends ReportMessageBase<ReportPilingOn> {

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
