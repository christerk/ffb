package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPileDriverParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPileDriver;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.BlitzBlock;
import com.fumbbl.ffb.server.step.generator.Block;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Move;
import com.fumbbl.ffb.server.step.generator.PileDriver;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndBlocking extends AbstractStep {

	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean fDefenderPushed;
	private boolean fUsingStab, usingChainsaw;
	private Boolean usePileDriver;
	private List<String> knockedDownPlayers = new ArrayList<>();
	private String targetPlayerId;

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
			if (receivedCommand.getId() == NetCommandId.CLIENT_PILE_DRIVER) {
				ClientCommandPileDriver commandPileDriver = (ClientCommandPileDriver) receivedCommand.getCommand();
				targetPlayerId = commandPileDriver.getPlayerId();
				usePileDriver = StringTool.isProvided(targetPlayerId);
				commandStatus = StepCommandStatus.EXECUTE_STEP;
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
		if (fEndTurn || fEndPlayerAction) {
			game.setDefenderId(null); // clear defender for next multi block
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
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

			if ((unusedPlayerMustMakeSecondBlockSkill != null) && (defenderState != null)
				&& defenderState.canBeBlocked() && attackerPosition.isAdjacent(defenderPosition)
				&& attackerState.hasTacklezones() && fDefenderPushed
				&& (actingPlayer.getPlayerAction() != PlayerAction.MULTIPLE_BLOCK)
				&& UtilPlayer.isNextMovePossible(game, false)) {
				actingPlayer.setGoingForIt(true);
				actingPlayer.markSkillUsed(unusedPlayerMustMakeSecondBlockSkill);
				if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
					blitzBlockGenerator.pushSequence(new BlitzBlock.SequenceParams(getGameState(),  game.getDefenderId(), fUsingStab, true, null));
				} else {
					blockGenerator.pushSequence(new Block.SequenceParams(getGameState(), game.getDefenderId(), fUsingStab, null));
				}
			} else {
				ServerUtilBlock.removePlayerBlockStates(game);
				fieldModel.clearDiceDecorations();
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
				FieldCoordinate attackerCoordinate = fieldModel.getPlayerCoordinate(activePlayer);
				knockedDownPlayers = knockedDownPlayers.stream().filter(playerId -> {
						Player<?> player = game.getPlayerById(playerId);
						PlayerState playerState = fieldModel.getPlayerState(player);

						return !game.getActingTeam().hasPlayer(player) && fieldModel.getPlayerCoordinate(player).isAdjacent(attackerCoordinate)
							&& (playerState.getBase() == PlayerState.PRONE || playerState.getBase() == PlayerState.STUNNED);
					}
				).collect(Collectors.toList());

				PlayerState playerState = fieldModel.getPlayerState(activePlayer);

				boolean canFoulAfterBlock = playerState.getBase() == PlayerState.MOVING && activePlayer.hasSkillProperty(NamedProperties.canFoulAfterBlock);

				if (!canFoulAfterBlock || knockedDownPlayers.isEmpty() || game.getTurnData().isFoulUsed()) {
					usePileDriver = false;
				}

				if (usePileDriver == null) {
					UtilServerDialog.showDialog(getGameState(), new DialogPileDriverParameter(game.getActingTeam().getId(), knockedDownPlayers), false);
					getResult().setNextAction(StepAction.CONTINUE);
				} else if (usePileDriver) {
					String actingPlayerId = activePlayer.getId();
					UtilServerGame.changeActingPlayer(this, actingPlayerId, PlayerAction.FOUL, actingPlayer.isJumping());
					ServerUtilBlock.updateDiceDecorations(game);
					pileDriver.pushSequence(new PileDriver.SequenceParams(getGameState(), targetPlayerId));
					PlayerResult playerResult = game.getGameResult().getPlayerResult(activePlayer);
					playerResult.setFouls(playerResult.getFouls() + 1);
					game.getTurnData().setFoulUsed(true);

					// go-for-it
				} else if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) && !fUsingStab
					&& !usingChainsaw
					&& attackerState.hasTacklezones() && UtilPlayer.isNextMovePossible(game, false)) {
					String actingPlayerId = activePlayer.getId();
					UtilServerGame.changeActingPlayer(this, actingPlayerId, PlayerAction.BLITZ_MOVE, actingPlayer.isJumping());
					UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
					ServerUtilBlock.updateDiceDecorations(game);
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
					// this may happen for ball and chain
				} else if ((actingPlayer.getPlayerAction() == PlayerAction.MOVE)
					&& UtilPlayer.isNextMovePossible(game, false)) {
					UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
					ServerUtilBlock.updateDiceDecorations(game);
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
				} else {
					game.setDefenderId(null);
					endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, false));
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
		return jsonObject;
	}

	@Override
	public StepEndBlocking initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(game, jsonObject);
		fDefenderPushed = IServerJsonOption.DEFENDER_PUSHED.getFrom(game, jsonObject);
		fUsingStab = IServerJsonOption.USING_STAB.getFrom(game, jsonObject);
		knockedDownPlayers = Arrays.stream(IServerJsonOption.PLAYER_IDS.getFrom(game, jsonObject)).collect(Collectors.toList());
		targetPlayerId = IServerJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		usingChainsaw = toPrimitive(IServerJsonOption.USING_CHAINSAW.getFrom(game, jsonObject));
		return this;
	}

}
