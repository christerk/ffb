package com.fumbbl.ffb.server.step.bb2020.pass;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerGame;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepResolvePass extends AbstractStep {
	public StepResolvePass(GameState pGameState) {
		super(pGameState, StepAction.NEXT_STEP);
	}

	@Override
	public StepId getId() {
		return StepId.RESOLVE_PASS;
	}

	@Override
	public void start() {
		super.start();
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		AnimationType animationType = getAnimationType(game.getThrowerAction());
		FieldCoordinate interceptorCoordinate = interceptorCoordinate(state, game);
		getResult().setAnimation(new Animation(animationType, state.getThrowerCoordinate(), game.getPassCoordinate(), interceptorCoordinate));
		UtilServerGame.syncGameModel(this);

		if (state.isDeflectionSuccessful()) {
			if (!state.isInterceptionSuccessful()) {
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombCoordinate(interceptorCoordinate);
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
						CatchScatterThrowInMode.DEFLECTED_BOMB));
				} else {
					game.getFieldModel().setBallCoordinate(interceptorCoordinate);
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
						CatchScatterThrowInMode.DEFLECTED));
				}
			} else {
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombCoordinate(interceptorCoordinate);
				}
			}
		} else if (state.getResult() == PassResult.ACCURATE) {
			Player<?> catcher = game.getPlayerById(state.getCatcherId());
			PlayerState catcherState = game.getFieldModel().getPlayerState(catcher);
			if ((catcher == null) || (catcherState == null) || !catcherState.hasTacklezones()) {
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
						state.getCatcherId() == null ? CatchScatterThrowInMode.CATCH_ACCURATE_BOMB_EMPTY_SQUARE
							: CatchScatterThrowInMode.CATCH_BOMB));
				} else {
					game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
						state.getCatcherId() == null ? CatchScatterThrowInMode.CATCH_ACCURATE_PASS_EMPTY_SQUARE
							: CatchScatterThrowInMode.CATCH_MISSED_PASS));
				}
			} else {
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
						CatchScatterThrowInMode.CATCH_ACCURATE_BOMB));
				} else {
					publishParameter(new StepParameter(StepParameterKey.PASS_ACCURATE, true));
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
						CatchScatterThrowInMode.CATCH_ACCURATE_PASS));
				}
			}
		} else  {
			if (state.isLandingOutOfBounds()) {
				if ((PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())
					|| (PlayerAction.THROW_BOMB == game.getThrowerAction())) {
					game.getFieldModel().setBombCoordinate(null);
					publishParameter(new StepParameter(StepParameterKey.BOMB_OUT_OF_BOUNDS, true));
				} else {
					publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
					publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, game.getPassCoordinate()));
					game.getFieldModel().setBallMoving(true);
				}
			} else {
				if ((PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())
					|| (PlayerAction.THROW_BOMB == game.getThrowerAction())) {
					publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_BOMB));
					game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
					game.getFieldModel().setBombMoving(true);
				} else {
					publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_MISSED_PASS));
					game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
					game.getFieldModel().setBallMoving(true);
				}
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private AnimationType getAnimationType(PlayerAction throwerAction) {
		if (PlayerAction.HAIL_MARY_PASS.equals(throwerAction)) {
			return AnimationType.HAIL_MARY_PASS;
		} else if (PlayerAction.HAIL_MARY_BOMB.equals(throwerAction)) {
			return AnimationType.HAIL_MARY_BOMB;
		} else if (PlayerAction.THROW_BOMB.equals(throwerAction)) {
			return AnimationType.THROW_BOMB;
		} else {
			return AnimationType.PASS;
		}
	}

	private FieldCoordinate interceptorCoordinate(PassState state, Game game){
		if (state.isDeflectionSuccessful()) {
			Player<?> interceptor = game.getPlayerById(state.getInterceptorId());
			if (interceptor != null) {
				return game.getFieldModel().getPlayerCoordinate(interceptor);
			}
		}
		return null;
	}
}
