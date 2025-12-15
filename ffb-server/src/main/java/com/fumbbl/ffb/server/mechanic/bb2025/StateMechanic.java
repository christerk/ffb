package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportLeader;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StateMechanic extends com.fumbbl.ffb.server.mechanic.StateMechanic {
	@Override
	public void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel, IStep pStep) {
		if (!LeaderState.USED.equals(pTurnData.getLeaderState())) {
			if (teamHasLeaderOnField(pTeam, pFieldModel)) {
				if (teamHasUnusedLeaderOnField(pTeam, pFieldModel) && LeaderState.NONE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.AVAILABLE);
					pTurnData.setReRolls(pTurnData.getReRolls() + 1);
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
					markUsed(pTeam, pStep.getGameState().getGame());
				}
			} else {
				if (LeaderState.AVAILABLE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.NONE);
					pTurnData.setReRolls(Math.max(pTurnData.getReRolls() - 1, 0));
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			}
		}
	}

	protected boolean teamHasUnusedLeaderOnField(Team pTeam, FieldModel pFieldModel) {
		for (Player<?> player : pTeam.getPlayers()) {
			if (playerOnField(player, pFieldModel)
				&& player.hasUnusedSkillProperty(NamedProperties.grantsTeamReRollWhenOnPitch)) {
				return true;
			}
		}
		return false;
	}

	protected void markUsed(Team team, Game game) {
		for (Player<?> player : team.getPlayers()) {
			Optional<Skill> skill = UtilCards.getUnusedSkillWithProperty(player, NamedProperties.grantsTeamReRollWhenOnPitch);
			skill.ifPresent(value -> player.markUsed(value, game));
		}
	}

}
