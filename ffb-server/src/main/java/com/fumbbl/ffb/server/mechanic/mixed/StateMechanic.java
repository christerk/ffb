package com.fumbbl.ffb.server.mechanic.mixed;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.ReportFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryModification;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportInjury;
import com.fumbbl.ffb.report.ReportLeader;
import com.fumbbl.ffb.report.ReportStartHalf;
import com.fumbbl.ffb.report.logcontrol.SkipInjuryParts;
import com.fumbbl.ffb.report.mixed.ReportPumpUpTheCrowdReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.UtilCards;

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

	public void reportInjury(IStep step, InjuryResult injuryResult) {
		InjuryContext injuryContext = injuryResult.injuryContext();

		SkipInjuryParts skip = SkipInjuryParts.NONE;
		boolean playSound = true;
		if (injuryContext instanceof ModifiedInjuryContext) {
			InjuryModification modification = ((ModifiedInjuryContext) injuryContext).getModification();
			if (modification == InjuryModification.INJURY) {
				skip = SkipInjuryParts.ARMOUR;
			}
		} else if (injuryContext.getModifiedInjuryContext() != null) {
			InjuryModification modification = injuryContext.getModifiedInjuryContext().getModification();
			if (injuryResult.isAlreadyReported()) {
				switch (modification) {
					case ARMOUR:
						skip = SkipInjuryParts.ARMOUR;
						break;
					case INJURY:
						skip = SkipInjuryParts.ARMOUR_AND_INJURY;
						break;
					default:
						break;
				}
				injuryResult.setAlreadyReported(false);
			} else {
				playSound = false;
				switch (modification) {
					case ARMOUR:
						skip = SkipInjuryParts.INJURY;
						break;
					case INJURY:
						skip = SkipInjuryParts.CAS;
						break;
					default:
						break;
				}
			}
		}

		if (injuryResult.isAlreadyReported()) {
			return;
		}

		ReportFactory factory = step.getGameState().getGame().getFactory(FactoryType.Factory.REPORT);
		ReportInjury reportInjury = (ReportInjury) factory.forId(ReportId.INJURY);
		step.getResult().addReport(reportInjury.init(injuryContext, skip));
		if (playSound) {
			step.getResult().setSound(injuryContext.getSound());
		}
		injuryResult.setAlreadyReported(true);
	}

	public boolean handlePumpUp(IStep pStep, InjuryResult pInjuryResult) {
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();

		Player<?> attacker = game.getPlayerById(pInjuryResult.injuryContext().getAttackerId());

		if (game.getActingTeam().hasPlayer(attacker) && !game.getFieldModel().getPlayerState(attacker).isProneOrStunned() &&
			pInjuryResult.injuryContext().isCasualty() &&
			UtilCards.hasUnusedSkillWithProperty(attacker, NamedProperties.grantsTeamReRollWhenCausingCas)) {
			TurnData turnData = game.getTurnData();
			turnData.setReRolls(turnData.getReRolls() + 1);
			turnData.setReRollsPumpUpTheCrowdOneDrive(turnData.getReRollsPumpUpTheCrowdOneDrive() + 1);
			attacker.markUsed(attacker.getSkillWithProperty(NamedProperties.grantsTeamReRollWhenCausingCas), game);
			pStep.getResult().addReport(new ReportPumpUpTheCrowdReRoll(attacker.getId()));
			pStep.getResult().setSound(SoundId.PUMP_CROWD);
			return true;
		}

		return false;
	}
}
