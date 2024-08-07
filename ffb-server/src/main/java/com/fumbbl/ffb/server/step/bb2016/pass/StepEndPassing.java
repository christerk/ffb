package com.fumbbl.ffb.server.step.bb2016.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Pass;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Bomb;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Final step of the pass sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter CATCHER_ID to be set by a preceding step. Expects
 * stepParameter END_PLAYER_ACTION to be set by a preceding step. Expects
 * stepParameter END_TURN to be set by a preceding step. Expects stepParameter
 * HAIL_MARY_PASS to be set by a preceding step. Expects stepParameter
 * INTERCEPTOR_ID to be set by a preceding step. Expects stepParameter
 * PASS_ACCURATE to be set by a preceding step. Expects stepParameter
 * PASS_FUMBLE to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepEndPassing extends AbstractStep {

	private String fInterceptorId;
	private String fCatcherId;
	private boolean fPassAccurate;
	private boolean fPassFumble;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean fBombOutOfBounds;
	private boolean dontDropFumble;

	public StepEndPassing(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_PASSING;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case CATCHER_ID:
				fCatcherId = (String) parameter.getValue();
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
			case INTERCEPTOR_ID:
				fInterceptorId = (String) parameter.getValue();
				consume(parameter);
				return true;
			case PASS_ACCURATE:
				fPassAccurate = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case PASS_FUMBLE:
				fPassFumble = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case DONT_DROP_FUMBLE:
				dontDropFumble = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case BOMB_OUT_OF_BOUNDS:
				fBombOutOfBounds = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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

	// TODO: what happens here in the case of dump-off interception?

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		game.getFieldModel().setRangeRuler(null);
		game.getFieldModel().setOutOfBounds(false);
		ActingPlayer actingPlayer = game.getActingPlayer();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endGenerator = ((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()));
		Bomb bombGenerator = ((Bomb) factory.forName(SequenceGenerator.Type.Bomb.name()));
		// failed confusion roll on throw bomb -> end player action
		if (fEndPlayerAction && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)
				|| (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		// throw bomb mode -> start bomb sequence
		if (game.getTurnMode().isBombTurn()) {
			if (StringTool.isProvided(fInterceptorId)) {
				bombGenerator.pushSequence(new Bomb.SequenceParams(getGameState(), fInterceptorId, fPassFumble, false, false));
			} else {
				bombGenerator.pushSequence(new Bomb.SequenceParams(getGameState(), fCatcherId, fPassFumble, false, false));
			}
			if (fBombOutOfBounds) {
				publishParameter(new StepParameter(StepParameterKey.BOMB_OUT_OF_BOUNDS, true));
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		// failed animosity may try to choose a new target
		if (actingPlayer.isSufferingAnimosity() && !fEndPlayerAction && (game.getPassCoordinate() == null)) {
			((Pass)factory.forName(SequenceGenerator.Type.Pass.name())).pushSequence(new com.fumbbl.ffb.server.step.generator.Pass.SequenceParams(getGameState()));
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		Player<?> catcher = game.getPlayerById(fCatcherId);
		// completions and passing statistic
		if ((game.getThrower() != null) && UtilPlayer.hasBall(game, catcher)
			&& game.getThrower().getTeam().hasPlayer(catcher)
			&& game.getFieldModel().getPlayerCoordinate(catcher).equals(game.getPassCoordinate())
		) {
			PlayerResult throwerResult = game.getGameResult().getPlayerResult(game.getThrower());
			if (fPassAccurate) {
				throwerResult.setCompletions(throwerResult.getCompletions() + 1);
			}
			FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
			FieldCoordinate endCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
			int deltaX;
			if (game.isHomePlaying()) {
				deltaX = endCoordinate.getX() - startCoordinate.getX();
			} else {
				deltaX = startCoordinate.getX() - endCoordinate.getX();
			}
			throwerResult.setPassing(throwerResult.getPassing() + deltaX);
		}
		if (fEndTurn || fEndPlayerAction || ((game.getThrower() == actingPlayer.getPlayer())
				&& actingPlayer.isSufferingBloodLust() && !actingPlayer.hasFed())) {
			fEndTurn |= (UtilServerSteps.checkTouchdown(getGameState())
					|| ((catcher == null) && !actingPlayer.isSufferingAnimosity())
					|| UtilPlayer.findOtherTeam(game, game.getThrower()).hasPlayer(catcher) || fPassFumble);
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, fEndPlayerAction, fEndTurn));
		} else {
			if (StringTool.isProvided(fInterceptorId)) {
				catcher = game.getPlayerById(fInterceptorId);
				GameResult gameResult = game.getGameResult();
				PlayerResult catcherResult = gameResult.getPlayerResult(catcher);
				catcherResult.setInterceptions(catcherResult.getInterceptions() + 1);
				FieldCoordinate interceptorCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
				game.getFieldModel().setBallCoordinate(interceptorCoordinate);
				game.getFieldModel().setBallMoving(false);
			} else {
				catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
			}
			if (game.getThrower() == actingPlayer.getPlayer()) {
				fEndTurn |= (UtilServerSteps.checkTouchdown(getGameState())
						|| (catcher == null)
						|| UtilPlayer.findOtherTeam(game, game.getThrower()).hasPlayer(catcher)
						|| (fPassFumble && !dontDropFumble));
				endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
			} else {
				game.setDefenderAction(null); // reset dump-off action
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		IServerJsonOption.PASS_ACCURATE.addTo(jsonObject, fPassAccurate);
		IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, fPassFumble);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.DONT_DROP_FUMBLE.addTo(jsonObject, dontDropFumble);
		return jsonObject;
	}

	@Override
	public StepEndPassing initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInterceptorId = IServerJsonOption.INTERCEPTOR_ID.getFrom(source, jsonObject);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		fPassAccurate = IServerJsonOption.PASS_ACCURATE.getFrom(source, jsonObject);
		fPassFumble = IServerJsonOption.PASS_FUMBLE.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		dontDropFumble = IServerJsonOption.DONT_DROP_FUMBLE.getFrom(source, jsonObject);
		return this;
	}

}
