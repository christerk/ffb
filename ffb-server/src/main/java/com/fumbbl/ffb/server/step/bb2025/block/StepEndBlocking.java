package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogPileDriverParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPileDriver;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.generator.*;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Last step in block sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter DEFENDER_PUSHED to be set by a preceding step. Expects
 * stepParameter END_PLAYER_ACTION to be set by a preceding step. Expects
 * stepParameter END_TURN to be set by a preceding step. Expects stepParameter
 * OLD_DEFENDER_STATE to be set by a preceding step. Expects stepParameter
 * USING_STAB to be set by a preceding step.
 * <p>
 * May push a new sequence on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepEndBlocking extends AbstractStep {

	private final List<String> targetsRegularMultibLock = new ArrayList<>(); // will be set for  multi blocks excl. special actions
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean fDefenderPushed;
	private PlayerAction bloodlustAction;
	private boolean fUsingStab, usingChainsaw, allowSecondBlockAction, usingVomit, addBlockDieHandled, usingBreatheFire;
	private Boolean usePileDriver, useHitAndRun, usePutridRegurgitation;
	private List<String> knockedDownPlayers = new ArrayList<>(); // will be set for all kinds of blocks
	private String targetPlayerId;
	private PlayerState oldDefenderState; // will be set for non-multi blocks incl. special actions

	public StepEndBlocking(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_BLOCKING;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), receivedCommand)) {
			switch (receivedCommand.getId()) {
				case CLIENT_PILE_DRIVER:
					ClientCommandPileDriver commandPileDriver = (ClientCommandPileDriver) receivedCommand.getCommand();
					targetPlayerId = commandPileDriver.getPlayerId();
					usePileDriver = StringTool.isProvided(targetPlayerId);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) receivedCommand.getCommand();
					if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canMoveAfterBlock)) {
						useHitAndRun = commandUseSkill.isSkillUsed();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					} else if (
						commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canUseVomitAfterBlock)) {
						usePutridRegurgitation = commandUseSkill.isSkillUsed();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					} else if (
						commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canAddBlockDie)) {
						Game game = getGameState().getGame();
						ActingPlayer actingPlayer = game.getActingPlayer();
						TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
						if (targetSelectionState != null && UtilCards.hasUnusedSkill(actingPlayer, commandUseSkill.getSkill())) {
							FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetSelectionState.getSelectedPlayerId()));
							FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
							DiceDecoration diceDecoration = game.getFieldModel().getDiceDecoration(targetCoordinate);
							Player<?> defender = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
							boolean opponentCanMove = UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.canMoveBeforeBeingBlocked);
							if (diceDecoration != null && (diceDecoration.getNrOfDice() == 1 || diceDecoration.getNrOfDice() == 2 || (diceDecoration.getNrOfDice() == 3 && opponentCanMove)) && targetCoordinate.isAdjacent(playerCoordinate)) {
								targetSelectionState.addUsedSkill(commandUseSkill.getSkill());
								getResult().addReport(new ReportSkillUse(commandUseSkill.getSkill(), true, SkillUse.ADD_BLOCK_DIE));
								ServerUtilBlock.updateDiceDecorations(getGameState());
							}
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}


	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case DEFENDER_PUSHED:
					fDefenderPushed = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case END_PLAYER_ACTION:
					fEndPlayerAction = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case END_TURN:
					fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case USING_STAB:
					fUsingStab = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case USING_CHAINSAW:
					usingChainsaw = toPrimitive((Boolean) parameter.getValue());
					consume(parameter);
					return true;
				case INJURY_RESULT:
					InjuryResult injuryResult = (InjuryResult) parameter.getValue();
					if (injuryResult != null) {
						String defenderId = injuryResult.injuryContext().getDefenderId();
						knockedDownPlayers.add(defenderId);
					}
					consume(parameter);
					return true;
				case ALLOW_SECOND_BLOCK_ACTION:
					allowSecondBlockAction = (boolean) parameter.getValue();
					break;
				case OLD_DEFENDER_STATE:
					oldDefenderState = (PlayerState) parameter.getValue();
					break;
				case BLOOD_LUST_ACTION:
					bloodlustAction = (PlayerAction) parameter.getValue();
					break;
				case USING_VOMIT:
					usingVomit = (boolean) parameter.getValue();
					consume(parameter);
					break;
				case USING_BREATHE_FIRE:
					usingBreatheFire = (boolean) parameter.getValue();
					consume(parameter);
					break;
				case TARGET_PLAYER_ID:
					targetsRegularMultibLock.add((String) parameter.getValue());
					consume(parameter);
					break;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilServerDialog.hideDialog(getGameState());
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
		Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
		Block blockGenerator = (Block) factory.forName(SequenceGenerator.Type.Block.name());
		BlitzBlock blitzBlockGenerator = (BlitzBlock) factory.forName(SequenceGenerator.Type.BlitzBlock.name());
		PileDriver pileDriver = (PileDriver) factory.forName(SequenceGenerator.Type.PileDriver.name());

		getResult().setNextAction(StepAction.NEXT_STEP);

		fieldModel.clearMultiBlockTargets();

		boolean regularBlock = !fUsingStab && !usingChainsaw && !usingVomit && !usingBreatheFire;
		if (regularBlock) {
			UtilCards.getUnusedSkillWithProperty(game.getDefender(), NamedProperties.ignoresDefenderStumblesResultForFirstBlock)
				.ifPresent(skill -> game.getDefender().markUsed(skill, game));
		}

		if (actingPlayer.getPlayerAction() == PlayerAction.VICIOUS_VINES) {
			actingPlayer.markSkillUsed(NamedProperties.canBlockOverDistance);
		}

		if (fEndTurn || fEndPlayerAction) {
			if (actingPlayer.getPlayerAction().isKickingDowned()) {
				// special case where we need to reset player state base. Otherwise, when changing the acting player to null
				// all 'BLOCKED' players will become 'STANDING' which must not happen for 'Kick 'em...' as that can only
				// be used on prone or stunned players
				ServerUtilBlock.removePlayerBlockStates(game, oldDefenderState);
			}
			game.setDefenderId(null); // clear defender for next multi block
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
		} else if (actingPlayer.isSufferingBloodLust() && bloodlustAction != null) {
			if (oldDefenderState != null) {
				game.getFieldModel().setPlayerState(game.getDefender(), oldDefenderState);
			}
			game.setDefenderId(null);
			ServerUtilBlock.updateDiceDecorations(getGameState());
			UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), bloodlustAction, false);
			moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
		} else {
			// Revert back strength gained from HORNS and DAUNTLESS to avoid interaction
			// with tentacles.
			Player<?> activePlayer = actingPlayer.getPlayer();
			Skill skillHorns = activePlayer.getSkillWithProperty(NamedProperties.addStrengthOnBlitz);
			Skill skillDauntless = activePlayer.getSkillWithProperty(NamedProperties.canRollToMatchOpponentsStrength);
			boolean usedHorns = (skillHorns != null) && actingPlayer.isSkillUsed(skillHorns);
			boolean usedDauntless = (skillDauntless != null) && actingPlayer.isSkillUsed(skillDauntless);

			if (usedHorns || usedDauntless) {
				actingPlayer.setStrength(activePlayer.getStrengthWithModifiers());
			}

			FieldCoordinate defenderPosition = fieldModel.getPlayerCoordinate(game.getDefender());
			FieldCoordinate attackerPosition = fieldModel.getPlayerCoordinate(activePlayer);
			PlayerState attackerState = fieldModel.getPlayerState(activePlayer);
			PlayerState defenderState = fieldModel.getPlayerState(game.getDefender());

			Skill unusedPlayerMustMakeSecondBlockSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer,
				NamedProperties.forceSecondBlock);

			if (activePlayer.hasSkillProperty(NamedProperties.forceSecondBlock)) {
				actingPlayer.setGoingForIt(true);
			}

			String defenderId = game.getDefenderId();
			boolean isBlitz = PlayerAction.BLITZ == actingPlayer.getPlayerAction();
			if ((unusedPlayerMustMakeSecondBlockSkill != null) && (defenderState != null)
				&& defenderState.canBeBlocked() && attackerPosition.isAdjacent(defenderPosition)
				&& attackerState.hasTacklezones() && fDefenderPushed
				&& (actingPlayer.getPlayerAction() != PlayerAction.MULTIPLE_BLOCK)
				&& UtilPlayer.hasMoveLeft(game, false)) {

				TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
				Skill addBlockDieSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie);

				if (!addBlockDieHandled
					&& regularBlock
					&& addBlockDieSkill != null
					&& PlayerAction.BLITZ == actingPlayer.getPlayerAction()
					&& targetSelectionState != null
					&& !targetSelectionState.getUsedSkills().contains(addBlockDieSkill)) {
					ServerUtilBlock.updateDiceDecorations(getGameState(), true);
				}

				actingPlayer.setGoingForIt(true);
				actingPlayer.markSkillUsed(unusedPlayerMustMakeSecondBlockSkill);

				boolean askForBlockKind = UtilCards.hasUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.providesBlockAlternative) ||
					(UtilCards.hasUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.providesBlockAlternativeDuringBlitz) && isBlitz);

				if (isBlitz) {
					blitzBlockGenerator.pushSequence(new BlitzBlock.SequenceParams(getGameState(), defenderId, fUsingStab, true, null, askForBlockKind));
				} else {
					blockGenerator.pushSequence(new Block.Builder(getGameState()).withDefenderId(defenderId).useStab(fUsingStab).askForBlockKind(askForBlockKind).build());
					publishParameter(StepParameter.from(StepParameterKey.ALLOW_SECOND_BLOCK_ACTION, allowSecondBlockAction));
				}
			} else {
				if (oldDefenderState != null) {
					ServerUtilBlock.removePlayerBlockStates(game, oldDefenderState);
				}
				fieldModel.clearDiceDecorations();
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
				FieldCoordinate attackerCoordinate = fieldModel.getPlayerCoordinate(activePlayer);
				knockedDownPlayers = knockedDownPlayers.stream().filter(playerId -> {
						Player<?> player = game.getPlayerById(playerId);
						PlayerState playerState = fieldModel.getPlayerState(player);

						return !game.getActingTeam().hasPlayer(player) && fieldModel.getPlayerCoordinate(player).isAdjacent(attackerCoordinate)
							&& (playerState.getBase() == PlayerState.PRONE || playerState.getBase() == PlayerState.STUNNED)
							&& (oldDefenderState != null || targetsRegularMultibLock.contains(playerId));
					}
				).collect(Collectors.toList());

				PlayerState playerState = fieldModel.getPlayerState(activePlayer);

				boolean canFoulAfterBlock = playerState.getBase() == PlayerState.MOVING
					&& activePlayer.hasSkillProperty(NamedProperties.canFoulAfterBlock);

				if (oldDefenderState != null) {
					canFoulAfterBlock &= regularBlock && !oldDefenderState.isProneOrStunned();
				}

				if (!canFoulAfterBlock || knockedDownPlayers.isEmpty() || game.getTurnMode() == TurnMode.BLITZ) {
					usePileDriver = false;
				}

				boolean canMoveAfterBlock = playerState.getBase() == PlayerState.MOVING && activePlayer.hasSkillProperty(NamedProperties.canMoveAfterBlock)
					&& (regularBlock || fUsingStab) && !playerState.isRooted();

				Set<FieldCoordinate> availableSquares = Arrays.stream(game.getFieldModel().findAdjacentCoordinates(fieldModel.getPlayerCoordinate(activePlayer), FieldCoordinateBounds.FIELD, 1, false))
					.filter(fieldCoordinate -> game.getFieldModel().getPlayers(fieldCoordinate) == null)
					.filter(fieldCoordinate -> !ArrayTool.isProvided(UtilPlayer
						.findAdjacentPlayers(game, game.getOtherTeam(game.getActingTeam()), fieldCoordinate)))
					.collect(Collectors.toSet());

				if (!canMoveAfterBlock || availableSquares.isEmpty()) {
					useHitAndRun = false;
				}

				boolean canUsePutridRegurgitation = actingPlayer.getPlayerAction().isBlockAction()
					&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)
					&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(activePlayer.getTeam()), game.getFieldModel().getPlayerCoordinate(activePlayer)))
					&& regularBlock;

				if (!canUsePutridRegurgitation) {
					usePutridRegurgitation = false;
				}

				if (usePutridRegurgitation == null) {
					UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(activePlayer.getId(), activePlayer.getSkillWithProperty(NamedProperties.canUseVomitAfterBlock), 0), false);
					getResult().setNextAction(StepAction.CONTINUE);
					return;
				} else if (usePutridRegurgitation) {
					blockGenerator.pushSequence(new Block.Builder(getGameState()).publishDefender(true).build());
					UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.PUTRID_REGURGITATION_BLOCK, actingPlayer.isJumping());
					ServerUtilBlock.updateDiceDecorations(getGameState());
					// always set the selection state to null in case the player action got changed from blitz_move to blitz in case of e.g. take root
					// otherwise logic for target selection will not trigger on players other than the original target
					// there is another line like this in StepInitMoving#handleCommand
					fieldModel.setTargetSelectionState(null);
					return;
				}
				if (useHitAndRun == null) {
					UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(activePlayer.getId(), activePlayer.getSkillWithProperty(NamedProperties.canMoveAfterBlock), 0), false);
					getResult().setNextAction(StepAction.CONTINUE);
				} else if (useHitAndRun) {
					useHitAndRun = false;
					getGameState().pushCurrentStepOnStack();
					IStep step = getGameState().getStepFactory().create(StepId.HIT_AND_RUN, null, null);
					getGameState().getStepStack().push(step);
				} else if (usePileDriver == null) {
					UtilServerDialog.showDialog(getGameState(), new DialogPileDriverParameter(game.getActingTeam().getId(), knockedDownPlayers), false);
					getResult().setNextAction(StepAction.CONTINUE);
				} else if (usePileDriver) {
					String actingPlayerId = activePlayer.getId();
					UtilServerGame.changeActingPlayer(this, actingPlayerId, PlayerAction.FOUL, actingPlayer.isJumping());
					ServerUtilBlock.updateDiceDecorations(getGameState());
					pileDriver.pushSequence(new PileDriver.SequenceParams(getGameState(), targetPlayerId));
					PlayerResult playerResult = game.getGameResult().getPlayerResult(activePlayer);
					playerResult.setFouls(playerResult.getFouls() + 1);

					// go-for-it
				} else {

					boolean flashesBlade = actingPlayer.getPlayerAction() == PlayerAction.THE_FLASHING_BLADE;
					if (flashesBlade) {
						actingPlayer.markSkillUsed(NamedProperties.canStabAndMoveAfterwards);
					}
					boolean canMoveOn = !fUsingStab && !usingChainsaw && !usingBreatheFire;
					if (((isBlitz && canMoveOn) || flashesBlade)
						&& attackerState.hasTacklezones() && UtilPlayer.isNextMovePossible(game, false)) {
						String actingPlayerId = activePlayer.getId();
						PlayerAction newAction;
						if (flashesBlade) {
							newAction = PlayerAction.MOVE;
						} else if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)) {
							newAction = PlayerAction.PUTRID_REGURGITATION_MOVE;
						} else {
							newAction = PlayerAction.BLITZ_MOVE;
						}
						UtilServerGame.changeActingPlayer(this, actingPlayerId, newAction, actingPlayer.isJumping());
						UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
						ServerUtilBlock.updateDiceDecorations(getGameState());
						moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
						// this may happen for ball and chain
					} else if ((actingPlayer.getPlayerAction() == PlayerAction.MOVE)
						&& UtilPlayer.isNextMovePossible(game, false)) {
						UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
						ServerUtilBlock.updateDiceDecorations(getGameState());
						moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
					} else {
						boolean blitzWithMoveLeft = isBlitz && UtilPlayer.isNextMovePossible(game, false);
						Player<?>[] opponents = null;
						if (game.getDefender() != null) {
							opponents = UtilPlayer.findAdjacentBlockablePlayers(game, game.getDefender().getTeam(), game.getFieldModel().getPlayerCoordinate(activePlayer));
						}

						boolean hasValidOpponent = ArrayTool.isProvided(opponents);
						boolean hasValidOtherOpponent = ArrayTool.isProvided(opponents) && (opponents.length > 1 || opponents[0] != game.getDefender());

						game.setDefenderId(null);
						if (attackerState.hasTacklezones() && allowSecondBlockAction && hasValidOpponent) {
							allowSecondBlockAction = false;
							actingPlayer.setHasBlocked(false);
							actingPlayer.markSkillUnused(NamedProperties.forceSecondBlock);
							blockGenerator.pushSequence(new Block.Builder(getGameState()).useChainsaw(usingChainsaw).publishDefender(true).build());
							ServerUtilBlock.updateDiceDecorations(getGameState());
						} else if (
							usingChainsaw && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canPerformSecondChainsawAttack)
								&& attackerState.hasTacklezones() && hasValidOtherOpponent &&
								(blitzWithMoveLeft || actingPlayer.getPlayerAction() == PlayerAction.BLOCK || (actingPlayer.getPlayerAction() == PlayerAction.BLITZ && playerState.isRooted()))
						) {
							game.setLastDefenderId(defenderId);
							UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.MAXIMUM_CARNAGE, false);
							if (isBlitz) {
								blitzBlockGenerator.pushSequence(new BlitzBlock.SequenceParams(getGameState(), true, true));
							} else {
								blockGenerator.pushSequence(new Block.Builder(getGameState()).useChainsaw(true).publishDefender(true).build());
							}

						} else {

							game.setLastDefenderId(null);
							endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, false));
						}
					}
				}
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.DEFENDER_PUSHED.addTo(jsonObject, fDefenderPushed);
		IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, knockedDownPlayers);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, targetPlayerId);
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		IServerJsonOption.ALLOW_SECOND_BLOCK_ACTION.addTo(jsonObject, allowSecondBlockAction);
		IServerJsonOption.USING_PILE_DRIVER.addTo(jsonObject, usePileDriver);
		IServerJsonOption.USING_HIT_AND_RUN.addTo(jsonObject, useHitAndRun);
		IServerJsonOption.USING_VOMIT.addTo(jsonObject, usingVomit);
		IServerJsonOption.USING_PUTRID_REGURGITATION.addTo(jsonObject, usePutridRegurgitation);
		IServerJsonOption.ADD_BLOCK_DIE_HANDLED.addTo(jsonObject, addBlockDieHandled);
		IServerJsonOption.PLAYER_ACTION.addTo(jsonObject, bloodlustAction);
		IServerJsonOption.USING_BREATHE_FIRE.addTo(jsonObject, usingBreatheFire);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, oldDefenderState);
		IServerJsonOption.PLAYER_IDS_HIT.addTo(jsonObject, targetsRegularMultibLock);
		return jsonObject;
	}

	@Override
	public StepEndBlocking initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		fDefenderPushed = IServerJsonOption.DEFENDER_PUSHED.getFrom(source, jsonObject);
		fUsingStab = IServerJsonOption.USING_STAB.getFrom(source, jsonObject);
		usingVomit = toPrimitive(IServerJsonOption.USING_VOMIT.getFrom(source, jsonObject));
		knockedDownPlayers = Arrays.stream(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)).collect(Collectors.toList());
		targetPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		usingChainsaw = toPrimitive(IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject));
		allowSecondBlockAction = toPrimitive(IServerJsonOption.ALLOW_SECOND_BLOCK_ACTION.getFrom(source, jsonObject));
		useHitAndRun = IServerJsonOption.USING_HIT_AND_RUN.getFrom(source, jsonObject);
		usePileDriver = IServerJsonOption.USING_PILE_DRIVER.getFrom(source, jsonObject);
		usePutridRegurgitation = IServerJsonOption.USING_PUTRID_REGURGITATION.getFrom(source, jsonObject);
		addBlockDieHandled = toPrimitive(IServerJsonOption.ADD_BLOCK_DIE_HANDLED.getFrom(source, jsonObject));
		bloodlustAction = (PlayerAction) IServerJsonOption.PLAYER_ACTION.getFrom(source, jsonObject);
		usingBreatheFire = toPrimitive(IServerJsonOption.USING_BREATHE_FIRE.getFrom(source, jsonObject));
		oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		if (IServerJsonOption.PLAYER_IDS_HIT.isDefinedIn(jsonObject)) {
			targetsRegularMultibLock.addAll(Arrays.stream(IServerJsonOption.PLAYER_IDS_HIT.getFrom(source, jsonObject)).collect(Collectors.toSet()));
		}
		return this;
	}

}
