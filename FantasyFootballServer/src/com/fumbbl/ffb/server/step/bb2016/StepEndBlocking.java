package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.BlitzBlock;
import com.fumbbl.ffb.server.step.generator.Block;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Move;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

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
@RulesCollection(RulesCollection.Rules.BB2016)
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
				case OLD_DEFENDER_STATE:
					fOldDefenderState = (PlayerState) parameter.getValue();
					consume(parameter);
					return true;
				case USING_STAB:
					fUsingStab = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilServerDialog.hideDialog(getGameState());
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
		Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
		BlitzBlock blitzBlockGenerator = (BlitzBlock) factory.forName(SequenceGenerator.Type.BlitzBlock.name());
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
				actingPlayer.setStrength(actingPlayer.getPlayer().getStrengthWithModifiers());
			}

			FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getDefender());
			FieldCoordinate attackerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
			PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());

			Skill unusedPlayerMustMakeSecondBlockSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer,
				NamedProperties.forceSecondBlock);

			if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.forceSecondBlock)) {
				actingPlayer.setGoingForIt(true);
			}
			Skill canBlockMultipleTimesSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer,
				NamedProperties.canBlockMoreThanOnce);
			if ((actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK) && canBlockMultipleTimesSkill != null
				&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.canBlockMoreThanOnce)
				&& attackerState.hasTacklezones()
				&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksLikeChainsaw)
				&& !attackerState.isConfused() && actingPlayer.hasBlocked()) {
				actingPlayer.markSkillUsed(canBlockMultipleTimesSkill);
				actingPlayer.setHasBlocked(false);
				ServerUtilBlock.updateDiceDecorations(game);
				if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
					blitzBlockGenerator.pushSequence(new BlitzBlock.SequenceParams(getGameState(),  game.getDefenderId(), fUsingStab, null));
				} else {
					blockGenerator.pushSequence(new Block.SequenceParams(getGameState(), null, false, game.getDefenderId()));
				}
				game.setDefenderId(null);
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else if ((unusedPlayerMustMakeSecondBlockSkill != null) && (defenderState != null)
				&& defenderState.canBeBlocked() && attackerPosition.isAdjacent(defenderPosition)
				&& attackerState.hasTacklezones() && fDefenderPushed
				&& (actingPlayer.getPlayerAction() != PlayerAction.MULTIPLE_BLOCK)
				&& UtilPlayer.isNextMovePossible(game, false)) {
				actingPlayer.setGoingForIt(true);
				actingPlayer.markSkillUsed(unusedPlayerMustMakeSecondBlockSkill);
				blockGenerator.pushSequence(new Block.SequenceParams(getGameState(), game.getDefenderId(), fUsingStab,  true,null));
			} else {
				ServerUtilBlock.removePlayerBlockStates(game);
				game.getFieldModel().clearDiceDecorations();
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
				// go-for-it
				if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) && !fUsingStab
					&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksLikeChainsaw)
					&& attackerState.hasTacklezones() && UtilPlayer.isNextMovePossible(game, false)) {
					String actingPlayerId = actingPlayer.getPlayer().getId();
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
