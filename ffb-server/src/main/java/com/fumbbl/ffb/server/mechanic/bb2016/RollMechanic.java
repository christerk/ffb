package com.fumbbl.ffb.server.mechanic.bb2016;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.bb2016.SeriousInjury;
import com.fumbbl.ffb.dialog.DialogReRollParameter;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportReRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.HasIdForSingleUseReRoll;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.*;

@RulesCollection(RulesCollection.Rules.BB2016)
public class RollMechanic extends com.fumbbl.ffb.server.mechanic.RollMechanic {
	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.PASS_BLOCK);
		add(TurnMode.DUMP_OFF);
	}};

	@Override
	public int[] rollCasualty(DiceRoller diceRoller) {
		return diceRoller.rollCasualtyRenamed();
	}

	@Override
	public PlayerState interpretInjuryRoll(Game game, InjuryContext pInjuryContext) {
		PlayerState playerState = null;
		if ((game != null) && (pInjuryContext != null)) {
			int[] injuryRoll = pInjuryContext.getInjuryRoll();
			Player<?> defender = game.getPlayerById(pInjuryContext.getDefenderId());
			if (defender instanceof ZappedPlayer) {
				return new PlayerState(PlayerState.BADLY_HURT);
			}

			if ((defender != null) && defender.hasSkillProperty(NamedProperties.preventDamagingInjuryModifications)) {
				pInjuryContext.clearInjuryModifiers();
			}
			if (injuryRoll == null) {
				// This is a forced injury, for example triggered by the player being eaten
				// We expect an injury being available in the injury context
				playerState = pInjuryContext.getInjury();
			} else {
				boolean isStunty = Arrays.stream(pInjuryContext.getInjuryModifiers())
					.anyMatch(injuryModifier -> injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.isHurtMoreEasily));
				int total = injuryRoll[0] + injuryRoll[1] + pInjuryContext.getInjuryModifierTotal(game);
				if ((total == 8) && (defender != null)
					&& defender.hasSkillProperty(NamedProperties.convertKOToStunOn8)) {
					playerState = new PlayerState(PlayerState.STUNNED);
					defender.getSkillWithProperty(NamedProperties.convertKOToStunOn8).getInjuryModifiers()
						.forEach(pInjuryContext::addInjuryModifier);
				} else if ((total == 7) && isStunty) {
					playerState = new PlayerState(PlayerState.KNOCKED_OUT);
				} else if ((total == 9) && (defender != null) && isStunty) {
					playerState = new PlayerState(PlayerState.BADLY_HURT);
				} else if (total > 9) {
					//noinspection DataFlowIssue
					playerState = null;
				} else if (total > 7) {
					playerState = new PlayerState(PlayerState.KNOCKED_OUT);
				} else {
					playerState = new PlayerState(PlayerState.STUNNED);
				}
			}
		}
		return playerState;
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int[] roll) {

		// 11-38 Badly Hurt No long term effect
		// 41 Broken Ribs Miss next game
		// 42 Groin Strain Miss next game
		// 43 Gouged Eye Miss next game
		// 44 Broken Jaw Miss next game
		// 45 Fractured Arm Miss next game
		// 46 Fractured Leg Miss next game
		// 47 Smashed Hand Miss next game
		// 48 Pinched Nerve Miss next game
		// 51 Damaged Back Niggling Injury
		// 52 Smashed Knee Niggling Injury
		// 53 Smashed Hip -1 MA
		// 54 Smashed Ankle -1 MA
		// 55 Serious Concussion -1 AV
		// 56 Fractured Skull -1 AV
		// 57 Broken Neck -1 AG
		// 58 Smashed Collar Bone -1 ST
		// 61-68 DEAD Dead!

		SeriousInjury seriousInjury = null;
		switch (roll[0]) {
			case 4:
				switch (roll[1]) {
					case 1:
						seriousInjury = SeriousInjury.BROKEN_RIBS;
						break;
					case 2:
						seriousInjury = SeriousInjury.GROIN_STRAIN;
						break;
					case 3:
						seriousInjury = SeriousInjury.GOUGED_EYE;
						break;
					case 4:
						seriousInjury = SeriousInjury.BROKEN_JAW;
						break;
					case 5:
						seriousInjury = SeriousInjury.FRACTURED_ARM;
						break;
					case 6:
						seriousInjury = SeriousInjury.FRACTURED_LEG;
						break;
					case 7:
						seriousInjury = SeriousInjury.SMASHED_HAND;
						break;
					case 8:
						seriousInjury = SeriousInjury.PINCHED_NERVE;
						break;
				}
				break;
			case 5:
				switch (roll[1]) {
					case 1:
						seriousInjury = SeriousInjury.DAMAGED_BACK;
						break;
					case 2:
						seriousInjury = SeriousInjury.SMASHED_KNEE;
						break;
					case 3:
						seriousInjury = SeriousInjury.SMASHED_HIP;
						break;
					case 4:
						seriousInjury = SeriousInjury.SMASHED_ANKLE;
						break;
					case 5:
						seriousInjury = SeriousInjury.SERIOUS_CONCUSSION;
						break;
					case 6:
						seriousInjury = SeriousInjury.FRACTURED_SKULL;
						break;
					case 7:
						seriousInjury = SeriousInjury.BROKEN_NECK;
						break;
					case 8:
						seriousInjury = SeriousInjury.SMASHED_COLLAR_BONE;
						break;
				}
				break;
		}
		return seriousInjury;
	}

	@Override
	public int multiBlockAttackerModifier() {
		return 0;
	}

	@Override
	public int multiBlockDefenderModifier() {
		return 2;
	}

	@Override
	public PlayerState interpretCasualtyRollAndAddModifiers(Game game, InjuryContext injuryContext, Player<?> player,
		boolean useDecayRoll) {
		int[] roll = useDecayRoll ? injuryContext.getCasualtyRollDecay() : injuryContext.getCasualtyRoll();
		if (ArrayTool.isProvided(roll)) {
			switch (roll[0]) {
				case 6:
					return new PlayerState(PlayerState.RIP);
				case 5:
				case 4:
					return new PlayerState(PlayerState.SERIOUS_INJURY);
				default: // 1 - 3
					return new PlayerState(PlayerState.BADLY_HURT);
			}
		} else {
			return null;
		}
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext) {
		return interpretSeriousInjuryRoll(game, injuryContext, false);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, boolean useDecay) {
		if (useDecay) {
			return interpretSeriousInjuryRoll(game, injuryContext, injuryContext.getCasualtyRollDecay());
		} else {
			return interpretSeriousInjuryRoll(game, injuryContext, injuryContext.getCasualtyRoll());
		}
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return 4;
	}

	@Override
	public int minimumProRoll() {
		return 4;
	}

	@Override
	public boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
		int minimumRoll, boolean fumble, Skill modificationSkill, Skill reRollSkill,
		CommonProperty menuProperty, String defaultValueKey, List<String> messages) {
		boolean dialogShown = false;
		Game game = gameState.getGame();
		if (minimumRoll >= 0) {
			boolean teamReRollOption = isTeamReRollAvailable(gameState, player);
			boolean singleUseReRollOption = isSingleUseReRollAvailable(gameState, player);
			boolean proOption = isProReRollAvailable(player, game, gameState.getPassState());
			if (reRollSkill == null) {
				Optional<Skill> reRollOnce =
					UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canRerollSingleDieOncePerPeriod);
				if (reRollOnce.isPresent()) {
					reRollSkill = reRollOnce.get();
				}
			}

			dialogShown =
				(teamReRollOption || proOption || singleUseReRollOption || reRollSkill != null || modificationSkill != null);
			if (dialogShown) {
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				String playerId = player.getId();
				UtilServerDialog.showDialog(gameState,
					new DialogReRollParameter(playerId, reRolledAction, minimumRoll, teamReRollOption, proOption, fumble,
						reRollSkill, singleUseReRollOption ? ReRollSources.LORD_OF_CHAOS : null, modificationSkill, menuProperty,
						defaultValueKey, messages),
					!actingTeam.hasPlayer(player));
			}
		}
		return dialogShown;
	}

	public boolean useReRoll(IStep pStep, ReRollSource pReRollSource, Player<?> pPlayer) {
		if (pPlayer == null) {
			throw new IllegalArgumentException("Parameter player must not be null.");
		}
		boolean successful = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		StepResult stepResult = pStep.getResult();
		if (pReRollSource != null) {
			boolean teamReRoll = ReRollSources.TEAM_RE_ROLL == pReRollSource;
			boolean lordOfChaos = ReRollSources.LORD_OF_CHAOS == pReRollSource && pStep instanceof HasIdForSingleUseReRoll;
			Skill reRollSourceSkill = pReRollSource.getSkill(game);
			if (teamReRoll || lordOfChaos) {
				TurnData turnData = game.getTurnData();
				ReRollSource usedAdditionalReRollSource = null;

				if (teamReRoll) {
					usedAdditionalReRollSource = updateTurnDataAfterReRollUsage(turnData);
				}

				if (teamReRoll && usedAdditionalReRollSource != null) {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), usedAdditionalReRollSource, successful, 0));
				} else if (teamReRoll && LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LEADER, successful, 0));
					turnData.setLeaderState(LeaderState.USED);
				} else {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), pReRollSource, successful, 0));
					if (lordOfChaos) {
						game.getPlayerById(((HasIdForSingleUseReRoll) pStep).idForSingleUseReRoll())
							.markUsed(reRollSourceSkill, game);
						UtilServerGame.updateSingleUseReRolls(turnData, pPlayer.getTeam(), game.getFieldModel());
					}
				}

				if (pPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
					int roll = gameState.getDiceRoller().rollSkill();
					int minimumRoll = minimumLonerRoll(pPlayer);
					successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LONER, successful, roll));
				} else {
					successful = true;
				}

			}
			if (!teamReRoll && !lordOfChaos && reRollSourceSkill != null) {
				if (ReRollSources.PRO == pReRollSource) {
					PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
					successful = (pPlayer.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
						&& !playerState.hasUsedPro());
					if (successful) {
						game.getFieldModel().setPlayerState(pPlayer, playerState.changeUsedPro(true));
						int roll = gameState.getDiceRoller().rollSkill();
						successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumProRoll());
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, roll));
					}
				} else {
					if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
						successful = !pPlayer.isUsed(reRollSourceSkill);
					} else {
						successful = UtilCards.hasSkill(pPlayer, reRollSourceSkill);
					}
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), pReRollSource, successful, 0));
				}
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (actingPlayer.getPlayer() == pPlayer) {
					actingPlayer.markSkillUsed(reRollSourceSkill);
				} else if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
					pPlayer.markUsed(reRollSourceSkill, game);
				}
			}
		}
		return successful;
	}

	@Override
	public ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRollUsed(true);
		turnData.setReRolls(turnData.getReRolls() - 1);
		return null;
	}

	@Override
	public boolean allowsTeamReRoll(TurnMode turnMode) {
		return !modesProhibitingReRolls.contains(turnMode);
	}

}
