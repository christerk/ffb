package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.step.generator.common.Block;
import com.balancedbytes.games.ffb.server.step.generator.common.EndPlayerAction;
import com.balancedbytes.games.ffb.server.step.generator.common.Move;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Last step in block sequence. Consumes all expected stepParameters.
 * 
 * Expects stepParameter DEFENDER_PUSHED to be set by a preceding step. Expects
 * stepParameter END_PLAYER_ACTION to be set by a preceding step. Expects
 * stepParameter END_TURN to be set by a preceding step. Expects stepParameter
 * OLD_DEFENDER_STATE to be set by a preceding step. Expects stepParameter
 * USING_STAB to be set by a preceding step.
 * 
 * May push a new sequence on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepEndBlocking extends AbstractStep {

	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean fDefenderPushed;
	private boolean fUsingStab;
	private PlayerState fOldDefenderState;

	public StepEndBlocking(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_BLOCKING;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case DEFENDER_PUSHED:
				fDefenderPushed = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case END_PLAYER_ACTION:
				fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case END_TURN:
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case OLD_DEFENDER_STATE:
				fOldDefenderState = (PlayerState) pParameter.getValue();
				consume(pParameter);
				return true;
			case USING_STAB:
				fUsingStab = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilServerDialog.hideDialog(getGameState());
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
		Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
		Block blockGenerator = (Block) factory.forName(SequenceGenerator.Type.Block.name());
		if (fEndTurn || fEndPlayerAction) {
			game.setDefenderId(null); // clear defender for next multi block
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
		} else {
			// Revert back strength gained from HORNS and DAUNTLESS to avoid interaction
			// with tentacles.
			Skill skillHorns = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.addStrengthOnBlitz);
			Skill skillDauntless = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRollToMatchOpponentsStrength);
			boolean usedHorns = (skillHorns != null) && actingPlayer.isSkillUsed(skillHorns);
			boolean usedDauntless = (skillDauntless != null) && actingPlayer.isSkillUsed(skillDauntless);

			if (usedHorns || usedDauntless) {
				actingPlayer.setStrength(UtilCards.getPlayerStrength(game, actingPlayer.getPlayer()));
			}

			FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getDefender());
			FieldCoordinate attackerPositon = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
			PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());

			Skill unusedPlayerMustMakeSecondBlockSkill = UtilCards.getUnusedSkillWithProperty(game, actingPlayer,
					NamedProperties.forceSecondBlock);

			if (actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.forceSecondBlock)) {
				actingPlayer.setGoingForIt(true);
			}
			Skill canBlockMultipleTimesSkill = UtilCards.getUnusedSkillWithProperty(game, actingPlayer,
					NamedProperties.canBlockMoreThanOnce);
			if ((actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK) && canBlockMultipleTimesSkill != null
					&& attackerState.hasTacklezones()
					&& !actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.blocksLikeChainsaw)
					&& !attackerState.isConfused() && actingPlayer.hasBlocked()) {
				actingPlayer.markSkillUsed(canBlockMultipleTimesSkill);
				actingPlayer.setHasBlocked(false);
				ServerUtilBlock.updateDiceDecorations(game);
				blockGenerator.pushSequence(new Block.SequenceParams(getGameState(), null, false, game.getDefenderId()));
				game.setDefenderId(null);
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else if ((unusedPlayerMustMakeSecondBlockSkill != null) && (defenderState != null)
					&& defenderState.canBeBlocked() && attackerPositon.isAdjacent(defenderPosition)
					&& attackerState.hasTacklezones() && fDefenderPushed
					&& (actingPlayer.getPlayerAction() != PlayerAction.MULTIPLE_BLOCK)
					&& UtilPlayer.isNextMovePossible(game, false)) {
				actingPlayer.setGoingForIt(true);
				actingPlayer.markSkillUsed(unusedPlayerMustMakeSecondBlockSkill);
				blockGenerator.pushSequence(new Block.SequenceParams(getGameState(), game.getDefenderId(), fUsingStab, null));
			} else {
				ServerUtilBlock.removePlayerBlockStates(game);
				game.getFieldModel().clearDiceDecorations();
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
				// go-for-it
				if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) && !fUsingStab
						&& !actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.blocksLikeChainsaw)
						&& attackerState.hasTacklezones() && UtilPlayer.isNextMovePossible(game, false)) {
					String actingPlayerId = actingPlayer.getPlayer().getId();
					UtilServerGame.changeActingPlayer(this, actingPlayerId, PlayerAction.BLITZ_MOVE, actingPlayer.isLeaping());
					UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
					ServerUtilBlock.updateDiceDecorations(game);
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
					// this may happen for ball and chain
				} else if ((actingPlayer.getPlayerAction() == PlayerAction.MOVE)
						&& UtilPlayer.isNextMovePossible(game, false)) {
					UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
					ServerUtilBlock.updateDiceDecorations(game);
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
					// this may happen on a failed bloodlust roll
				} else if (actingPlayer.isSufferingBloodLust() && !actingPlayer.hasBlocked()) {
					game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
					game.setDefenderId(null);
					ServerUtilBlock.updateDiceDecorations(game);
					blockGenerator.pushSequence(new Block.SequenceParams(getGameState()));
				} else {
					game.setDefenderId(null); // clear defender for next multi block
					endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, false));
				}
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.DEFENDER_PUSHED.addTo(jsonObject, fDefenderPushed);
		IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
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
		fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(game, jsonObject);
		return this;
	}

}
