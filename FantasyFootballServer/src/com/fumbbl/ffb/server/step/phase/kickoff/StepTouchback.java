package com.fumbbl.ffb.server.step.phase.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogTouchbackParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandTouchback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Step in kickoff sequence to handle touchback.
 *
 * Expects stepParameter TOUCHBACK to be set by a preceding step.
 *
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepTouchback extends AbstractStep {

	private boolean fTouchback;
	private FieldCoordinate fTouchbackCoordinate;

	public StepTouchback(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.TOUCHBACK;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_TOUCHBACK:
				ClientCommandTouchback touchbackCommand = (ClientCommandTouchback) pReceivedCommand.getCommand();
				if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
					fTouchbackCoordinate = touchbackCommand.getBallCoordinate();
				} else {
					fTouchbackCoordinate = touchbackCommand.getBallCoordinate().transform();
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
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
			case TOUCHBACK:
				fTouchback = (Boolean) parameter.getValue();
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private void executeStep() {

		boolean doNextStep = true;
		Game game = getGameState().getGame();

		if (fTouchback) {

			if (fTouchbackCoordinate == null) {
				game.getFieldModel().setBallCoordinate(null);
				game.setTurnMode(TurnMode.TOUCHBACK);
				game.setDialogParameter(new DialogTouchbackParameter());
				doNextStep = false;
			} else {
				UtilServerDialog.hideDialog(getGameState());
				game.getFieldModel().setBallCoordinate(fTouchbackCoordinate);
				Player<?> player = game.getFieldModel().getPlayer(fTouchbackCoordinate);
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				if ((player != null) && !player.hasSkillProperty(NamedProperties.preventHoldBall)
						&& playerState.hasTacklezones()) {
					game.getFieldModel().setBallMoving(false);
					getResult().setSound(SoundId.CATCH);
				} else {
					publishParameter(
							new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_KICKOFF));
				}
				game.setTurnMode(TurnMode.REGULAR);
			}

		}

		if (doNextStep) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TOUCHBACK.addTo(jsonObject, fTouchback);
		IServerJsonOption.TOUCHBACK_COORDINATE.addTo(jsonObject, fTouchbackCoordinate);
		return jsonObject;
	}

	@Override
	public StepTouchback initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fTouchback = IServerJsonOption.TOUCHBACK.getFrom(game, jsonObject);
		fTouchbackCoordinate = IServerJsonOption.TOUCHBACK_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
