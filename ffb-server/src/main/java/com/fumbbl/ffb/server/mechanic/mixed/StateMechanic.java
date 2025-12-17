package com.fumbbl.ffb.server.mechanic.mixed;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.report.ReportLeader;
import com.fumbbl.ffb.report.ReportStartHalf;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerGame;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class StateMechanic extends com.fumbbl.ffb.server.mechanic.StateMechanic {
	@Override
	public void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel, IStep pStep) {
		if (!LeaderState.USED.equals(pTurnData.getLeaderState())) {
			if (teamHasLeaderOnField(pTeam, pFieldModel)) {
				if (LeaderState.NONE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.AVAILABLE);
					pTurnData.setReRolls(pTurnData.getReRolls() + 1);
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
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
		if (game.getHalf() < 2) {
			addApothecaries(pStep, true);
			addApothecaries(pStep, false);
		}
		// handle ReRolls + Extra Team Training
		if (game.getHalf() < 3) {
			addReRolls(pStep, true);
			addReRolls(pStep, false);

			GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
			if (mechanic.rollForChefAtStartOfHalf()) {
				UtilServerGame.handleChefRolls(pStep, game);
			}
		}
		resetLeaderState(game);
		UtilServerGame.updatePlayerStateDependentProperties(pStep);
		resetSpecialSkillsAtHalfTime(game);

	}

	private void resetSpecialSkillsAtHalfTime(Game game) {
		for (Player<?> player : game.getPlayers()) {
			player.resetUsedSkills(SkillUsageType.ONCE_PER_HALF, game);
		}
		resetSpecialSkillAtEndOfDrive(game);
	}
}
