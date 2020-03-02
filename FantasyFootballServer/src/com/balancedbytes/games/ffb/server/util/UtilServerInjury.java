package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.ArmorModifierFactory;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportRaiseDead;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ApothecaryStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class UtilServerInjury {

	public static InjuryResult handleInjury(
			IStep pStep,
			InjuryType pInjuryType,
			Player pAttacker,
			Player pDefender,
			FieldCoordinate pDefenderCoordinate,
			InjuryResult pOldInjuryResult,
			ApothecaryMode pApothecaryMode
			) {

		if (pDefender == null) {
			throw new IllegalArgumentException("Parameter defender must not be null.");
		}
		if (pInjuryType == null) {
			throw new IllegalArgumentException("Parameter injuryType must not be null.");
		}

		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		DiceRoller diceRoller = gameState.getDiceRoller();
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		InjuryResult injuryResult = new InjuryResult();
		injuryResult.setInjuryType(pInjuryType);
		injuryResult.setDefenderId(pDefender.getId());
		injuryResult.setAttackerId((pAttacker != null) ? pAttacker.getId() : null);
		injuryResult.setDefenderCoordinate(pDefenderCoordinate);
		injuryResult.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
		injuryResult.setApothecaryMode(pApothecaryMode);

		if ((pInjuryType == InjuryType.PILING_ON_INJURY) && (pOldInjuryResult != null)) {
			for (ArmorModifier armorModifier : pOldInjuryResult.getArmorModifiers()) {
				injuryResult.addArmorModifier(armorModifier);
			}
		}

		// ball and chain always breaks armor on being knocked down
		if ((pInjuryType == InjuryType.BALL_AND_CHAIN) || (UtilCards.hasSkill(game, pDefender, Skill.BALL_AND_CHAIN)
				&& (pInjuryType != InjuryType.STAB) && (pInjuryType != InjuryType.CHAINSAW))) {
			injuryResult.setArmorBroken(true);
		}

		// Blatant Foul breaks armor without roll
		if ((pInjuryType == InjuryType.FOUL) && UtilCards.isCardActive(game, Card.BLATANT_FOUL)) {
			injuryResult.setArmorBroken(true);
		}

		if (!injuryResult.isArmorBroken()) {

			switch (pInjuryType) {
			case KTM_CROWD:
			case KTM_INJURY:
			case CROWDPUSH:
			case THROW_A_ROCK:
			case EAT_PLAYER:
			case PILING_ON_INJURY:
			case BITTEN:
			case PILING_ON_KNOCKED_OUT:
				injuryResult.setArmorBroken(true);
				break;
			case STAB:
				Team otherTeam = game.getTeamHome().hasPlayer(pDefender) ? game.getTeamHome() : game.getTeamAway();
				if ((pAttacker != null) && UtilCards.hasSkill(game, pAttacker, Skill.STAKES)
						&& (otherTeam.getRoster().isUndead() || ((pDefender != null) && pDefender.getPosition().isUndead()))) {
					injuryResult.addArmorModifier(ArmorModifier.STAKES);
				}
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				break;
			case DROP_DODGE:
			case DROP_GFI:
			case DROP_LEAP:
			case TTM_LANDING:
			case TTM_HIT_PLAYER:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				if (UtilCards.hasSkill(game, pDefender, Skill.CHAINSAW)) {
					injuryResult.addArmorModifier(ArmorModifier.CHAINSAW);
				}
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				break;
			case CHAINSAW:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				injuryResult.addArmorModifier(ArmorModifier.CHAINSAW);
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				break;
			case BLOCK_STUNNED:
			case BLOCK_PRONE:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				break;
			case BLOCK:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				if (UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW) || UtilCards.hasSkill(game, pDefender, Skill.CHAINSAW)) {
					injuryResult.addArmorModifier(ArmorModifier.CHAINSAW);
				}
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				// do not use armorModifiers on blocking own team-mate with b&c
				if (!UtilCards.hasSkill(game, pAttacker, Skill.BALL_AND_CHAIN) || (pAttacker.getTeam() != pDefender.getTeam())) {
					if (UtilCards.hasSkill(game, pAttacker, Skill.CLAW) && (pDefender.getArmour() > 7) && !UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW)) {
						injuryResult.addArmorModifier(ArmorModifier.CLAWS);
					}
					injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
					if (!injuryResult.isArmorBroken() && UtilCards.hasSkill(game, pAttacker, Skill.MIGHTY_BLOW)
							&& !UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW)
							&& !(UtilCards.hasSkill(game, pAttacker, Skill.CLAW) && UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK))) {
						injuryResult.addArmorModifier(ArmorModifier.MIGHTY_BLOW);
						injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
					}
				}
				break;
			case PILING_ON_ARMOR:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
					if (UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW) || UtilCards.hasSkill(game, pDefender, Skill.CHAINSAW)) {
						injuryResult.addArmorModifier(ArmorModifier.CHAINSAW);
					}
					if (UtilCards.hasSkill(game, pAttacker, Skill.CLAW) && (pDefender.getArmour() > 7) && !UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW)) {
						injuryResult.addArmorModifier(ArmorModifier.CLAWS);
					}
					injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
					if (!injuryResult.isArmorBroken() && UtilCards.hasSkill(game, pAttacker, Skill.MIGHTY_BLOW)
							&& !UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW)
							&& !(UtilCards.hasSkill(game, pAttacker, Skill.CLAW) && UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK))) {
						injuryResult.addArmorModifier(ArmorModifier.MIGHTY_BLOW);
						injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
					}
				}
				break;
			case LIGHTNING:
			case FIREBALL:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				if (!injuryResult.isArmorBroken()) {
					injuryResult.addArmorModifier(ArmorModifier.MIGHTY_BLOW);
					injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				}
				break;
			case BOMB:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				if (UtilCards.hasSkill(game, pDefender, Skill.CHAINSAW)) {
					injuryResult.addArmorModifier(ArmorModifier.CHAINSAW);
				}
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				break;
			case FOUL:
				injuryResult.setArmorRoll(diceRoller.rollArmour());
				if (UtilCards.hasSkill(game, pAttacker, Skill.CHAINSAW)) {
					injuryResult.addArmorModifier(ArmorModifier.CHAINSAW);
				}
				if (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS)
						|| (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE) && (UtilPlayer.findTacklezones(game, pAttacker) < 1))) {
					injuryResult.addArmorModifier(ArmorModifier.FOUL);
				}
				int foulAssists = UtilPlayer.findFoulAssists(game, pAttacker, pDefender);
				if (foulAssists != 0) {
					ArmorModifier assistModifier = new ArmorModifierFactory().getFoulAssist(foulAssists);
					injuryResult.addArmorModifier(assistModifier);
				}
				injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				if (!injuryResult.isArmorBroken() && UtilCards.hasSkill(game, pAttacker, Skill.DIRTY_PLAYER)) {
					injuryResult.addArmorModifier(ArmorModifier.DIRTY_PLAYER);
					injuryResult.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryResult));
				}
				break;
			default:
				break;
			}

		}

		if (injuryResult.isArmorBroken()) {
			if (pInjuryType == InjuryType.EAT_PLAYER) {
				injuryResult.setInjury(new PlayerState(PlayerState.RIP));
			} else if (pInjuryType == InjuryType.PILING_ON_KNOCKED_OUT) {
				injuryResult.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
			} else if (pInjuryType == InjuryType.KTM_CROWD) {
				injuryResult.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
			} else if (UtilCards.hasCard(game, pDefender, Card.LUCKY_CHARM) && (injuryResult.getArmorRoll() != null)) {
				injuryResult.setArmorBroken(false);
				injuryResult.setInjury(new PlayerState(PlayerState.PRONE));
				UtilServerCards.deactivateCard(pStep, Card.LUCKY_CHARM);
			} else {

				injuryResult.setInjuryRoll(diceRoller.rollInjury());
				if (pInjuryType != InjuryType.STAB) { // stab does not have any
					// modifiers at all
					injuryResult.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
				}
				switch (pInjuryType) {
					case BLOCK:
						// do not use injuryModifiers on blocking own team-mate with b&c
						if (!UtilCards.hasSkill(game, pAttacker, Skill.BALL_AND_CHAIN) || (pAttacker.getTeam() != pDefender.getTeam())) {
							if (UtilCards.hasSkill(game, pAttacker, Skill.MIGHTY_BLOW) && !injuryResult.hasArmorModifier(ArmorModifier.MIGHTY_BLOW)) {
								injuryResult.addInjuryModifier(InjuryModifier.MIGHTY_BLOW);
							}
						}
						break;
					case PILING_ON_ARMOR:
					case PILING_ON_INJURY:
						if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
							if (UtilCards.hasSkill(game, pAttacker, Skill.MIGHTY_BLOW) && !injuryResult.hasArmorModifier(ArmorModifier.MIGHTY_BLOW)) {
								injuryResult.addInjuryModifier(InjuryModifier.MIGHTY_BLOW);
							}
						}
						break;
					case FIREBALL:
					case LIGHTNING:
						if (!injuryResult.hasArmorModifier(ArmorModifier.MIGHTY_BLOW)) {
							injuryResult.addInjuryModifier(InjuryModifier.MIGHTY_BLOW);
						}
						break;
					case FOUL:
						if (UtilCards.hasSkill(game, pAttacker, Skill.DIRTY_PLAYER) && !injuryResult.hasArmorModifier(ArmorModifier.DIRTY_PLAYER)) {
							injuryResult.addInjuryModifier(InjuryModifier.DIRTY_PLAYER);
						}
						break;
					default:
						break;
				}
				injuryResult.setInjury(interpretInjury(diceInterpreter, gameState, injuryResult, pDefender instanceof ZappedPlayer));

				if (injuryResult.getPlayerState() == null) {
					if (pInjuryType == InjuryType.BITTEN) {
						injuryResult.setInjury(new PlayerState(PlayerState.BADLY_HURT));
					} else {
						injuryResult.setCasualtyRoll(diceRoller.rollCasualty());
						injuryResult.setInjury(diceInterpreter.interpretRollCasualty(injuryResult.getCasualtyRoll()));
						if (UtilCards.hasSkill(game, pDefender, Skill.DECAY)) {
							injuryResult.setCasualtyRollDecay(diceRoller.rollCasualty());
							injuryResult.setInjuryDecay(diceInterpreter.interpretRollCasualty(injuryResult.getCasualtyRollDecay()));
						}
					}
				}
			}
		} else {

			switch (pInjuryType) {
				case STAB:
				case CHAINSAW:
					injuryResult.setInjury(null);
					break;
				case BLOCK_STUNNED:
					injuryResult.setInjury(new PlayerState(PlayerState.STUNNED));
					break;
				default:
					injuryResult.setInjury(new PlayerState(PlayerState.PRONE));
			}
		}

		if (injuryResult.isSeriousInjury()) {
			injuryResult.setSeriousInjury(DiceInterpreter.getInstance().interpretRollSeriousInjury(injuryResult.getCasualtyRoll()));
			if (UtilCards.hasSkill(game, pDefender, Skill.DECAY)) {
				injuryResult.setSeriousInjuryDecay(DiceInterpreter.getInstance().interpretRollSeriousInjury(injuryResult.getCasualtyRollDecay()));
			}
		}

		// ball and chain is never stunned, but always knocked out instead
		if (UtilCards.hasSkill(game, pDefender, Skill.BALL_AND_CHAIN) && (injuryResult.getInjury() != null)
				&& (injuryResult.getInjury().getBase() == PlayerState.STUNNED)) {
			injuryResult.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}

		// Kick Team-Mate injuries get KO'd instead of Stunned
		if ((pInjuryType == InjuryType.KTM_INJURY) 
		    && (injuryResult.getInjury() != null) 
		    && injuryResult.getInjury().getBase() == PlayerState.STUNNED) {
		  injuryResult.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}
		
		// crowdpush to reserve
		if ((pInjuryType == InjuryType.CROWDPUSH) && !injuryResult.isCasualty() && !injuryResult.isKnockedOut()) {
			injuryResult.setInjury(new PlayerState(PlayerState.RESERVE));
		}

		if (injuryResult.getPlayerState() != null) {
			if (injuryResult.isCasualty() || injuryResult.isKnockedOut()) {
				injuryResult.setSufferedInjury(injuryResult.getPlayerState());
				if ((pInjuryType == InjuryType.EAT_PLAYER) || (pInjuryType == InjuryType.PILING_ON_KNOCKED_OUT)
						|| (injuryResult.isKnockedOut() && UtilCards.hasSkill(game, pDefender, Skill.BALL_AND_CHAIN))) {
					injuryResult.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				} else if ((game.getTeamHome().hasPlayer(pDefender) && (game.getTurnDataHome().getApothecaries() > 0) && pDefender.getPlayerType() != PlayerType.STAR)
						|| (game.getTeamAway().hasPlayer(pDefender) && (game.getTurnDataAway().getApothecaries() > 0) && pDefender.getPlayerType() != PlayerType.STAR)) {
					injuryResult.setApothecaryStatus(ApothecaryStatus.DO_REQUEST);
				} else {
					injuryResult.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				}
			}
		}

		if (injuryResult.isCasualty() || injuryResult.isKnockedOut() || injuryResult.isReserve()) {
			injuryResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
			injuryResult.setSendToBoxHalf(game.getHalf());
			switch (pInjuryType) {
			case DROP_DODGE:
				injuryResult.setSendToBoxReason(SendToBoxReason.DODGE_FAIL);
				break;
			case DROP_GFI:
				injuryResult.setSendToBoxReason(SendToBoxReason.GFI_FAIL);
				break;
			case DROP_LEAP:
				injuryResult.setSendToBoxReason(SendToBoxReason.LEAP_FAIL);
				break;
			case BLOCK:
			case BLOCK_PRONE:
			case BLOCK_STUNNED:
				injuryResult.setSendToBoxReason(SendToBoxReason.BLOCKED);
				break;
			case FOUL:
				injuryResult.setSendToBoxReason(SendToBoxReason.FOULED);
				break;
			case CROWDPUSH:
				injuryResult.setSendToBoxReason(SendToBoxReason.CROWD_PUSHED);
				break;
			case THROW_A_ROCK:
				injuryResult.setSendToBoxReason(SendToBoxReason.HIT_BY_ROCK);
				break;
			case EAT_PLAYER:
				injuryResult.setSendToBoxReason(SendToBoxReason.EATEN);
				break;
			case STAB:
				injuryResult.setSendToBoxReason(SendToBoxReason.STABBED);
				break;
			case TTM_LANDING:
				injuryResult.setSendToBoxReason(SendToBoxReason.LANDING_FAIL);
				break;
			case TTM_HIT_PLAYER:
				injuryResult.setSendToBoxReason(SendToBoxReason.HIT_BY_THROWN_PLAYER);
				break;
			case PILING_ON_ARMOR:
			case PILING_ON_INJURY:
				injuryResult.setSendToBoxReason(SendToBoxReason.PILED_ON);
				break;
			case PILING_ON_KNOCKED_OUT:
				injuryResult.setSendToBoxReason(SendToBoxReason.KO_ON_PILING_ON);
				break;
			case CHAINSAW:
				injuryResult.setSendToBoxReason(SendToBoxReason.CHAINSAW);
				break;
			case BITTEN:
				injuryResult.setSendToBoxReason(SendToBoxReason.BITTEN);
				break;
			case FIREBALL:
				injuryResult.setSendToBoxReason(SendToBoxReason.FIREBALL);
				break;
			case LIGHTNING:
				injuryResult.setSendToBoxReason(SendToBoxReason.LIGHTNING);
				break;
			case BOMB:
				injuryResult.setSendToBoxReason(SendToBoxReason.BOMB);
				break;
			case BALL_AND_CHAIN:
				injuryResult.setSendToBoxReason(SendToBoxReason.BALL_AND_CHAIN);
				break;
			default:
				break;
			}
		}

		// UtilPlayerMove.updateMoveSquares(gameState, false);

		if ((injuryResult != null) && (injuryResult.getPlayerState() != null)) {
			switch (injuryResult.getPlayerState().getBase()) {
			case PlayerState.RIP:
				injuryResult.setSound(SoundId.RIP);
				break;
			case PlayerState.SERIOUS_INJURY:
			case PlayerState.BADLY_HURT:
				injuryResult.setSound(SoundId.INJURY);
				break;
			case PlayerState.KNOCKED_OUT:
				injuryResult.setSound(SoundId.KO);
				break;
			default:
				if (injuryResult.getInjuryType() != InjuryType.FOUL) {
					injuryResult.setSound(SoundId.FALL);
				}
				break;
			}
		}

		return injuryResult;

	}

	private static PlayerState interpretInjury(DiceInterpreter diceInterpreter, GameState gameState, InjuryResult injuryResult, boolean isZapped) {
		if (isZapped) {
			return new PlayerState(PlayerState.BADLY_HURT);
		}
		return diceInterpreter.interpretRollInjury(gameState, injuryResult);
	}

	public static boolean handleRegeneration(IStep pStep, Player pPlayer) {
		boolean successful = false;
		if (pPlayer != null) {
			GameState gameState = pStep.getGameState();
			Game game = gameState.getGame();
			PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
			if ((playerState != null) && playerState.isCasualty() && UtilCards.hasSkill(game, pPlayer, Skill.REGENERATION)) {
				DiceRoller diceRoller = gameState.getDiceRoller();
				DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
				int roll = diceRoller.rollSkill();
				successful = diceInterpreter.isRegenerationSuccessful(roll);
				if (successful) {
					game.getFieldModel().setPlayerState(pPlayer, playerState.changeBase(PlayerState.RESERVE));
					GameResult gameResult = game.getGameResult();
					PlayerResult playerResult = gameResult.getPlayerResult(pPlayer);
					playerResult.setSeriousInjury(null);
					playerResult.setSeriousInjuryDecay(null);
					UtilBox.putPlayerIntoBox(game, pPlayer);
					UtilBox.refreshBoxes(game);
					UtilServerGame.updateLeaderReRolls(pStep);
				}
				pStep.getResult().addReport(new ReportSkillRoll(ReportId.REGENERATION_ROLL, pPlayer.getId(), successful, roll, 4, false));
			}
		}
		return successful;
	}

	public static boolean handleRaiseDead(IStep pStep, InjuryResult pInjuryResult) {

		RosterPlayer raisedPlayer = null;
		boolean nurglesRot = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		Player deadPlayer = game.getPlayerById(pInjuryResult.getDefenderId());
		Team necroTeam = UtilPlayer.findOtherTeam(game, deadPlayer);
		TeamResult necroTeamResult = (game.getTeamHome() == necroTeam) ? game.getGameResult().getTeamResultHome() : game.getGameResult().getTeamResultAway();

		if ((pInjuryResult != null) && (pInjuryResult.getPlayerState() != null) && (PlayerState.RIP == pInjuryResult.getPlayerState().getBase())) {
			if (necroTeam.getRoster().hasNecromancer() && (necroTeamResult.getRaisedDead() == 0) && (deadPlayer.getStrength() <= 4)
					&& !UtilCards.hasSkill(game, deadPlayer, Skill.STUNTY) && !UtilCards.hasSkill(game, deadPlayer, Skill.REGENERATION)) {
				raisedPlayer = raisePlayer(game, necroTeam, necroTeamResult, deadPlayer.getName(), nurglesRot, deadPlayer.getId());
			} else {
				Player attacker = game.getPlayerById(pInjuryResult.getAttackerId());
				if ((attacker != null) && UtilCards.hasSkill(game, attacker, Skill.NURGLES_ROT) && (deadPlayer.getStrength() <= 4)
						&& !UtilCards.hasSkill(game, deadPlayer, Skill.STUNTY) && !UtilCards.hasSkill(game, deadPlayer, Skill.REGENERATION)
						&& !UtilCards.hasSkill(game, deadPlayer, Skill.DECAY)) {
					RosterPosition zombiePosition = necroTeam.getRoster().getRaisedRosterPosition();
					if (zombiePosition != null) {
						nurglesRot = true;
						raisedPlayer = raisePlayer(game, necroTeam, necroTeamResult, deadPlayer.getName(), nurglesRot, deadPlayer.getId());
					}
				}
			}
		}

		if (raisedPlayer != null) {

			// communicate raised player to clients
			gameState.getServer().getCommunication().sendAddPlayer(
					gameState, necroTeam.getId(), raisedPlayer, game.getFieldModel().getPlayerState(raisedPlayer), game.getGameResult().getPlayerResult(raisedPlayer)
					);
			pStep.getResult().addReport(new ReportRaiseDead(raisedPlayer.getId(), nurglesRot));
			pStep.getResult().setSound(SoundId.ORGAN);

			return true;

		} else {
			return false;
		}

	}

	private static RosterPlayer raisePlayer(Game pGame, Team pNecroTeam, TeamResult pNecroTeamResult, String pPlayerName, boolean pNurglesRot, String killedId) {
		RosterPlayer raisedPlayer = null;
		RosterPosition zombiePosition = pNecroTeam.getRoster().getRaisedRosterPosition();
		if (zombiePosition != null) {
			pNecroTeamResult.setRaisedDead(pNecroTeamResult.getRaisedDead() + 1);
			raisedPlayer = new RosterPlayer();
			StringBuilder raisedPlayerId = new StringBuilder().append(killedId).append("R").append(pNecroTeamResult.getRaisedDead());
			raisedPlayer.setId(raisedPlayerId.toString());
			raisedPlayer.updatePosition(zombiePosition);
			raisedPlayer.setName(pPlayerName);
			raisedPlayer.setNr(pNecroTeam.getMaxPlayerNr() + 1);
			raisedPlayer.setType(PlayerType.RAISED_FROM_DEAD);
			pNecroTeam.addPlayer(raisedPlayer);
			PlayerResult playerResult = pGame.getGameResult().getPlayerResult(raisedPlayer);
			playerResult.setSendToBoxHalf(pGame.getHalf());
			playerResult.setSendToBoxTurn(pGame.getTurnData().getTurnNr());
			if (pNurglesRot) {
				pGame.getFieldModel().setPlayerState(raisedPlayer, new PlayerState(PlayerState.MISSING));
				playerResult.setSendToBoxReason(SendToBoxReason.NURGLES_ROT);
			} else {
				pGame.getFieldModel().setPlayerState(raisedPlayer, new PlayerState(PlayerState.RESERVE));
				playerResult.setSendToBoxReason(SendToBoxReason.RAISED);
			}
			UtilBox.putPlayerIntoBox(pGame, raisedPlayer);
		}
		return raisedPlayer;
	}

	public static StepParameterSet dropPlayer(IStep pStep, Player pPlayer, ApothecaryMode pApothecaryMode) {
		return dropPlayer(pStep, pPlayer, PlayerState.PRONE, pApothecaryMode);
	}

	public static StepParameterSet stunPlayer(IStep pStep, Player pPlayer, ApothecaryMode pApothecaryMode) {
		return dropPlayer(pStep, pPlayer, PlayerState.STUNNED, pApothecaryMode);
	}

	// drops the given player
	// sets stepParameter END_TURN if player is on acting team and drops the ball
	// sets stepParameter INJURY_RESULT if player has skill Ball&Chain
	private static StepParameterSet dropPlayer(IStep pStep, Player pPlayer, int pPlayerBase, ApothecaryMode pApothecaryMode) {
		StepParameterSet stepParameters = new StepParameterSet();
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerCoordinate != null) && (playerState != null)) {
			if (UtilCards.hasSkill(game, pPlayer, Skill.BALL_AND_CHAIN)) {
				pStep.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
						UtilServerInjury.handleInjury(pStep, InjuryType.BALL_AND_CHAIN, null, pPlayer, playerCoordinate, null, pApothecaryMode))
						);
			} else {
				if ((playerState.getBase() != PlayerState.PRONE) && (playerState.getBase() != PlayerState.STUNNED)) {
					playerState = playerState.changeBase(pPlayerBase);
					if ((pPlayer == game.getActingPlayer().getPlayer()) || (PlayerState.STUNNED == pPlayerBase)) {
						playerState = playerState.changeActive(false);
					}
				}
				playerState = playerState.changeRooted(false);
				game.getFieldModel().setPlayerState(pPlayer, playerState);
			}
			if (playerCoordinate.equals(game.getFieldModel().getBallCoordinate()) && game.getTurnMode() != TurnMode.BLITZ) {
				game.getFieldModel().setBallMoving(true);
				stepParameters.add(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
				// check for turnover
				boolean endTurn;
				switch (game.getTurnMode()) {
				case BOMB_HOME:
				case BOMB_HOME_BLITZ:
					endTurn = game.getTeamHome().hasPlayer(pPlayer);
					break;
				case BOMB_AWAY:
				case BOMB_AWAY_BLITZ:
					endTurn = game.getTeamAway().hasPlayer(pPlayer);
					break;
				case PASS_BLOCK:
					endTurn = false;
					break;
				default:
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					endTurn = actingTeam.hasPlayer(pPlayer);
					break;
				}
				if (endTurn) {
					stepParameters.add(new StepParameter(StepParameterKey.END_TURN, true));
				}
			}
		}
		UtilServerPlayerMove.updateMoveSquares(gameState, false);
		return stepParameters;
	}

}
