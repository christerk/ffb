package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillUse;

@ReportMessageType(ReportId.SKILL_USE)
@RulesCollection(Rules.COMMON)
public class SkillUseMessage extends ReportMessageBase<ReportSkillUse> {

    @Override
    protected void render(ReportSkillUse report) {
  		if (report.getSkill() != null) {
  			Player<?> player = game.getPlayerById(report.getPlayerId());
  			int indent = getIndent();
  			StringBuilder status = new StringBuilder();
  			if (!report.isUsed()) {
  				if (player != null) {
  					print(indent, false, player);
  					status.append(" does not use ").append(report.getSkill().getName());
  				} else {
  					status.append(report.getSkill().getName()).append(" is not used");
  				}
  				if (report.getSkillUse() != null) {
  					status.append(" ").append(report.getSkillUse().getDescription(player));
  				}
  				status.append(".");
  				println(indent, status.toString());
  			} else {
  				if (player != null) {
  					print(indent, false, player);
  					status.append(" uses ").append(report.getSkill().getName());
  				} else {
  					status.append(report.getSkill().getName()).append(" used");
  				}
  				if (report.getSkillUse() != null) {
  					status.append(" ").append(report.getSkillUse().getDescription(player));
  				}
  				status.append(".");
  				println(indent, status.toString());
  			}
  		}
		}
}
