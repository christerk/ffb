package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.report.ReportLeader;
import com.fumbbl.ffb.report.ReportStartHalf;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StateMechanic extends com.fumbbl.ffb.server.mechanic.StateMechanic {
	@Override
	public void updateLeaderReRollsForTeam(TurnData turnData, Team team, FieldModel fieldModel, IStep step) {
		if (step.getGameState().getGame().getHalf() > 2) {
			return;
		}
		if (!LeaderState.USED.equals(turnData.getLeaderState())) {
			if (teamHasLeaderOnField(team, fieldModel)) {
				if (teamHasUnusedLeaderOnField(team, fieldModel) &&
						LeaderState.NONE.equals(turnData.getLeaderState())) {
					turnData.setLeaderState(LeaderState.AVAILABLE);
					turnData.setReRolls(turnData.getReRolls() + 1);
					step.getResult().addReport(new ReportLeader(team.getId(), turnData.getLeaderState()));
				}
			} else {
				if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
					turnData.setLeaderState(LeaderState.NONE);
					turnData.setReRolls(Math.max(turnData.getReRolls() - 1, 0));
					step.getResult().addReport(new ReportLeader(team.getId(), turnData.getLeaderState()));
				}
			}
			markUsed(team, step.getGameState().getGame());
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
			Optional<Skill> skill =
					UtilCards.getUnusedSkillWithProperty(player, NamedProperties.grantsTeamReRollWhenOnPitch);
			skill.ifPresent(value -> player.markUsed(value, game));
		}
	}

	public void startHalf(IStep pStep, int pHalf) {
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		game.setHalf(pHalf);
		game.getTurnDataHome().setTurnNr(0);
		game.getTurnDataAway().setTurnNr(0);
		if (game.isHomeFirstOffense()) {
			game.setHomePlaying(game.getHalf() % 2 == 0);
		} else {
			game.setHomePlaying(game.getHalf() % 2 > 0);
		}
		game.getFieldModel().setBallCoordinate(null);
		game.getFieldModel().setBallInPlay(false);
		pStep.getResult().addReport(new ReportStartHalf(game.getHalf()));
		// handle Apothecaries + Wandering Apothecaries
		if (game.getHalf() <= 1) {
			addApothecaries(pStep, true);
			addApothecaries(pStep, false);
		}
		// handle ReRolls + Extra Team Training
		if (game.getHalf() <= 2) {
			addReRolls(pStep, true);
			addReRolls(pStep, false);
		}

		resetLeaderState(game);
		resetSpecialSkillsAtHalfTime(game);
		resetInducements(game);
	}

	private void resetInducements(Game game) {
		if (game.getHalf() <= 2) {
			resetInducements(game.getTurnDataHome().getInducementSet());
			resetInducements(game.getTurnDataAway().getInducementSet());
		}
	}

	private void resetInducements(InducementSet inducementSet) {
		resetInducement(inducementSet, inducementSet.forUsage(Usage.CONDITIONAL_REROLL));
	}

	private void resetInducement(InducementSet inducementSet, InducementType inducementType) {
		Inducement inducement = inducementSet.get(inducementType);
		if (inducement != null) {
			inducement.setUses(0);
			inducementSet.addInducement(inducement);
		}
	}

	private void resetSpecialSkillsAtHalfTime(Game game) {
		if (game.getHalf() <= 2) {
			for (Player<?> player : game.getPlayers()) {
				player.resetUsedSkills(SkillUsageType.ONCE_PER_HALF, game);
			}
		}
		resetSpecialSkillAtEndOfDrive(game);
	}
}
