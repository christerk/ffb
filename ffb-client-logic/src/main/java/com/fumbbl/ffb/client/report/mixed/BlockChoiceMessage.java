package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportBlockChoice;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.UtilCards;

@ReportMessageType(ReportId.BLOCK_CHOICE)
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
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
					PlayerState playerState = game.getFieldModel().getPlayerState(defender);
					if (playerState.hasTacklezones()) {
						status
							.append(" has been saved by ")
							.append(defender.getPlayerGender().getGenitive())
							.append(" ")
							.append(defender.getSkillWithProperty(NamedProperties.preventFallOnBothDown))
							.append(" skill.");
					} else {
						status
							.append(" has not been saved by ")
							.append(defender.getPlayerGender().getGenitive())
							.append(" ")
							.append(defender.getSkillWithProperty(NamedProperties.preventFallOnBothDown))
							.append(" skill, due to having no tacklezones.");
					}
					println(getIndent() + 1, status.toString());
				}
				break;
			case POW_PUSHBACK:
				if (UtilCards.hasSkillToCancelProperty(attacker, NamedProperties.ignoreDefenderStumblesResult) &&
					(UtilCards.hasSkillWithProperty(defender, NamedProperties.ignoreDefenderStumblesResult)
						|| UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.ignoresDefenderStumblesResultForFirstBlock))) {
					print(getIndent() + 1, false, attacker);
					println(getIndent() + 1, " uses Tackle to bring opponent down.");
				}
				break;
			default:
				break;
		}
	}
}
