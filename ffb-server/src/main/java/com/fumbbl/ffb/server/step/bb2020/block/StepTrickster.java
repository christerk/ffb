package com.fumbbl.ffb.server.step.bb2020.block;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.ActionStatus;
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
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepTrickster extends AbstractStep {

	private boolean fUsingStab, usingChainsaw, usingVomit;
	private TurnMode lastTurnMode;
	private final List<FieldCoordinate> eligibleSquares = new ArrayList<>();
	private Boolean usingTrickster;
	private FieldCoordinate toCoordinate;
	private ActionStatus actionStatus = ActionStatus.WAITING_FOR_SKILL_USE;

	public StepTrickster(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.TRICKSTER;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case USING_STAB:
					fUsingStab = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				case USING_CHAINSAW:
					usingChainsaw = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				case USING_VOMIT:
					usingVomit = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus;
		if (ActionStatus.SKILL_CHOICE_YES == actionStatus) {
			commandStatus = StepCommandStatus.EXECUTE_STEP;

		} else {
			commandStatus = super.handleCommand(pReceivedCommand);
			if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
				switch (pReceivedCommand.getId()) {
					case CLIENT_USE_SKILL:
						if (UtilServerSteps.checkCommandIsFromPassivePlayer(getGameState(), pReceivedCommand)) {
							ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
							if (useSkillCommand.getSkill().hasSkillProperty(NamedProperties.canMoveBeforeBeingBlocked)) {

								usingTrickster = useSkillCommand.isSkillUsed();
								commandStatus = StepCommandStatus.EXECUTE_STEP;
								UtilServerDialog.hideDialog(getGameState());
							}
						}
						break;
					case CLIENT_FIELD_COORDINATE:
						if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
							ClientCommandFieldCoordinate fieldCoordinateCommand = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
							FieldCoordinate fieldCoordinate = fieldCoordinateCommand.getFieldCoordinate();
							if (UtilServerSteps.checkCommandIsFromAwayPlayer(getGameState(), pReceivedCommand)) {
								fieldCoordinate = fieldCoordinate.transform();
							}
							if (eligibleSquares.contains(fieldCoordinate)) {
								toCoordinate = fieldCoordinate;
								commandStatus = StepCommandStatus.EXECUTE_STEP;
							}
						}
						break;
					case CLIENT_END_TURN:
						if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
							leave();
							commandStatus = StepCommandStatus.SKIP_STEP;
						}
						break;
					default:
						break;
				}
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> defender = game.getDefender();
		if (usingTrickster == null) {

			if (defender != null && defender.hasSkillProperty(NamedProperties.canMoveBeforeBeingBlocked)
				&& (usingChainsaw || usingVomit || fUsingStab || !UtilCards.cancelsSkill(actingPlayer.getPlayer(), defender.getSkillWithProperty(NamedProperties.canMoveBeforeBeingBlocked)))) {
				eligibleSquares.addAll(Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()), FieldCoordinateBounds.FIELD, 1, false))
					.filter(coord -> fieldModel.getPlayer(coord) == null && !fieldModel.isBlockedForTrickster(coord)).collect(Collectors.toList()));

				if (eligibleSquares.isEmpty()) {
					getResult().setNextAction(StepAction.NEXT_STEP);
					return;
				}

				UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(defender.getId(),
					defender.getSkillWithProperty(NamedProperties.canMoveBeforeBeingBlocked), 0), true);

			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else if (usingTrickster) {
			if (toCoordinate == null) {
				lastTurnMode = game.getTurnMode();
				game.setTurnMode(TurnMode.TRICKSTER);
				game.setHomePlaying(!game.isHomePlaying());
				fieldModel.clearMoveSquares();
				fieldModel.add(eligibleSquares.stream().map(coord -> new MoveSquare(coord, 0, 0))
					.toArray(MoveSquare[]::new));
			} else if (ActionStatus.WAITING_FOR_SKILL_USE == actionStatus) {
				FieldCoordinate defCoordinate = fieldModel.getPlayerCoordinate(defender);
				fieldModel.replaceMultiBlockTargetCoordinate(defCoordinate, toCoordinate);
				//	UtilServerGame.syncGameModel(this);
				getResult().setAnimation(new Animation(AnimationType.TRICKSTER, defCoordinate, toCoordinate, defender.getId()));
				//		UtilServerGame.syncGameModel(this);
				getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
				actionStatus = ActionStatus.SKILL_CHOICE_YES;
				getGameState().pushCurrentStepOnStack();
			} else {
				fieldModel.setPlayerCoordinate(defender, toCoordinate);
				publishParameter(new StepParameter(StepParameterKey.DEFENDER_POSITION, toCoordinate));
				ServerUtilBlock.updateDiceDecorations(game);
				UtilServerGame.syncGameModel(this);
				if (toCoordinate.equals(fieldModel.getBallCoordinate()) && fieldModel.isBallMoving()) {
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
				}
				leave();
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void leave() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.clearMoveSquares();
		game.setHomePlaying(!game.isHomePlaying());
		game.setTurnMode(lastTurnMode);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		IServerJsonOption.USING_VOMIT.addTo(jsonObject, usingVomit);
		IServerJsonOption.LAST_TURN_MODE.addTo(jsonObject, lastTurnMode);
		JsonArray jsonArray = new JsonArray();
		eligibleSquares.stream().map(FieldCoordinate::toJsonValue).forEach(jsonArray::add);
		IServerJsonOption.FIELD_COORDINATES.addTo(jsonObject, jsonArray);

		IServerJsonOption.USING_TRICKSTER.addTo(jsonObject, usingTrickster);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, toCoordinate);
		IServerJsonOption.STATUS.addTo(jsonObject, actionStatus.name());
		return jsonObject;
	}

	@Override
	public StepTrickster initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fUsingStab = IServerJsonOption.USING_STAB.getFrom(source, jsonObject);
		usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		usingVomit = IServerJsonOption.USING_VOMIT.getFrom(source, jsonObject);
		if (IServerJsonOption.LAST_TURN_MODE.isDefinedIn(jsonObject)) {
			lastTurnMode = (TurnMode) IServerJsonOption.LAST_TURN_MODE.getFrom(source, jsonObject);
		}

		JsonArray jsonArray = IServerJsonOption.FIELD_COORDINATES.getFrom(source, jsonObject);
		if (jsonArray != null) {
			eligibleSquares.clear();
			jsonArray.values().stream().map(value -> new FieldCoordinate().initFrom(source, value)).forEach(eligibleSquares::add);
		}

		usingTrickster = IServerJsonOption.USING_TRICKSTER.getFrom(source, jsonObject);
		toCoordinate = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		actionStatus = ActionStatus.valueOf(IServerJsonOption.STATUS.getFrom(source, jsonObject));
		return this;
	}

}
