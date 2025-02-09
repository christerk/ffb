package com.fumbbl.ffb.server.step.bb2020.special;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Move;
import com.fumbbl.ffb.server.step.generator.Pass;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

/**
 * Final step of the bomb sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter CATCHER_ID to be set by a preceding step. Expects
 * stepParameter END_TURN to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepEndBomb extends AbstractStep {

	private String fCatcherId;
	private boolean fEndTurn;
	private boolean fBombExploded;
	private boolean allowMoveAfterPass;

	public StepEndBomb(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_BOMB;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case CATCHER_ID:
					fCatcherId = (String) parameter.getValue();
					consume(parameter);
					return true;
				case END_TURN:
					fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case BOMB_EXPLODED:
					fBombExploded = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		super.init(parameterSet);
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.ALLOW_MOVE_AFTER_PASS) {
					allowMoveAfterPass = (boolean) parameter.getValue();
				}
			}
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		boolean removePassCoordinate = true;
		if (fEndTurn || (fCatcherId == null) || fBombExploded) {
			if (game.getTurnMode().isBombTurn()) {
				game.setHomePlaying(
					(TurnMode.BOMB_HOME == game.getTurnMode()) || (TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()));
				if ((TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()) || (TurnMode.BOMB_AWAY_BLITZ == game.getTurnMode())) {
					game.setTurnMode(TurnMode.BLITZ);
				} else {
					game.setTurnMode(TurnMode.REGULAR);
				}
			}

			PassState state = getGameState().getPassState();
			Player<?> originalBomber = game.getPlayerById(state.getOriginalBombardier());
			Skill skill = originalBomber.getSkillWithProperty(NamedProperties.canUseThrowBombActionTwice);
			PlayerState playerState = game.getFieldModel().getPlayerState(originalBomber);
			boolean threwOnlyFirstBomb = toPrimitive(state.getThrowTwoBombs());

			if (originalBomber != actingPlayer.getPlayer()) {
				UtilServerSteps.changePlayerAction(this, originalBomber.getId(), PlayerAction.THROW_BOMB, false);
				if (playerState.isProneOrStunned()) {
					game.getFieldModel().setPlayerState(originalBomber, playerState.changeActive(false));
				}
			}

			if (!fEndTurn && threwOnlyFirstBomb && skill != null && originalBomber.hasUnused(skill) && playerState.hasTacklezones()) {
				originalBomber.markUsed(skill, game);
				actingPlayer.setMustCompleteAction(true);
				state.setThrowTwoBombs(false);
				((Pass) factory.forName(SequenceGenerator.Type.Pass.name())).pushSequence(new Pass.SequenceParams(getGameState()));
			} else if (state.getThrowTwoBombs() != null && skill != null) {

				if (state.getThrowTwoBombs() && originalBomber.hasUnused(skill)) {
					originalBomber.markUsed(skill, game);
				}

				if (!state.getThrowTwoBombs()) {
					state.setThrowTwoBombs(null);
					removePassCoordinate = false;
					getGameState().pushCurrentStepOnStack();
					getGameState().getStepStack().push(getGameState().getStepFactory().create(StepId.ALL_YOU_CAN_EAT, null, null));
				} else {
					((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
						.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, fEndTurn));
				}

			} else if (!fEndTurn && allowMoveAfterPass) {
				UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.MOVE, false);
				((Move) factory.forName(SequenceGenerator.Type.Move.name()))
					.pushSequence(new Move.SequenceParams(getGameState()));
			} else {
				((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
					.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, fEndTurn));
			}
		} else {
			game.setPassCoordinate(null);
			Player<?> catcher = game.getPlayerById(fCatcherId);
			game.setHomePlaying(game.getTeamHome().hasPlayer(catcher));
			UtilServerSteps.changePlayerAction(this, fCatcherId, PlayerAction.THROW_BOMB, false);
			((Pass) factory.forName(SequenceGenerator.Type.Pass.name()))
				.pushSequence(new Pass.SequenceParams(getGameState(), null));
		}
		// stop immediate re-throwing of the bomb
		if (removePassCoordinate) {
			game.setPassCoordinate(null);
		}
		game.setThrowerId(null);
		game.setThrowerAction(null);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.ALLOW_MOVE_AFTER_PASS.addTo(jsonObject, allowMoveAfterPass);
		return jsonObject;
	}

	@Override
	public StepEndBomb initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		allowMoveAfterPass = toPrimitive(IServerJsonOption.ALLOW_MOVE_AFTER_PASS.getFrom(source, jsonObject));
		return this;
	}

}
