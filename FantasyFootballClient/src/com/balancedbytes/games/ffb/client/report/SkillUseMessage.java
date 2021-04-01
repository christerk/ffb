package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.SKILL_USE)
@RulesCollection(Rules.COMMON)
public class SkillUseMessage extends ReportMessageBase<ReportSkillUse> {

    public SkillUseMessage(StatusReport statusReport) {
        super(statusReport);
    }

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
