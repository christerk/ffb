package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogInformationOkayParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeTTMHitPlayer;
import com.fumbbl.ffb.server.model.CarriedPlayer;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.bb2025.command.DropPlayerCommand;
import com.fumbbl.ffb.server.step.bb2025.command.HitPlayerTurnOverCommand;
import com.fumbbl.ffb.server.step.generator.CarriedPlayerScatter;
import com.fumbbl.ffb.server.step.generator.ScatterPlayer;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPlaceCarriedPlayer extends AbstractStep {

	private final List<FieldCoordinate> eligibleSquares = new ArrayList<>();
	private FieldCoordinate selectedCoordinate;
	private TurnMode savedTurnMode;

	public StepPlaceCarriedPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PLACE_CARRIED_PLAYER;
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
				case CLIENT_FIELD_COORDINATE:
					ClientCommandFieldCoordinate command = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
					FieldCoordinate coordinate;
					if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
						coordinate = command.getFieldCoordinate();
					} else {
						coordinate = command.getFieldCoordinate().transform();
					}
					selectedCoordinate = eligibleSquares.contains(coordinate) ? coordinate : null;
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

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		CarriedPlayer carriedPlayerState = getGameState().getCarriedPlayer();

		if (carriedPlayerState == null || !StringTool.isProvided(carriedPlayerState.getPlayerId())) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		Player<?> carrier = actingPlayer.getPlayer();
		Player<?> carriedPlayer = game.getPlayerById(carriedPlayerState.getPlayerId());
		FieldCoordinate carrierCoordinate = game.getFieldModel().getPlayerCoordinate(carrier);

		boolean bouncesFromCarrier =
			UtilGameOption.isOptionEnabled(game, GameOptionId.CARRIED_PLAYER_BOUNCES_FROM_CARRIER);

		if (eligibleSquares.isEmpty()) {
			List<FieldCoordinate> emptySquares = findEmptySquares(game, carrierCoordinate);

			PlayerState carrierState = game.getFieldModel().getPlayerState(carrier);
			if (bouncesFromCarrier && (!carrierState.isStanding() || emptySquares.isEmpty())) {
				scatterCarriedPlayer(game, carriedPlayer, carrierCoordinate);
				leave(game, carrier);
				return;
			}

			if (!emptySquares.isEmpty()) {
				eligibleSquares.addAll(emptySquares);
			} else if (FieldCoordinateBounds.ENDZONE_HOME.isInBounds(carrierCoordinate)
				|| FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(carrierCoordinate)
				|| FieldCoordinateBounds.SIDELINE_UPPER.isInBounds(carrierCoordinate)
				|| FieldCoordinateBounds.SIDELINE_LOWER.isInBounds(carrierCoordinate)) {
				pushCarriedPlayerIntoCrowd(game, carriedPlayer, carrierCoordinate);
				leave(game, carrier);
				return;
			} else {
				eligibleSquares.addAll(Arrays.asList(
					game.getFieldModel().findAdjacentCoordinates(carrierCoordinate, FieldCoordinateBounds.FIELD, 1, false)));
			}
		}

		if (eligibleSquares.size() == 1) {
			placePlayerOnEmptySquare(game, carrier, carriedPlayer, eligibleSquares.get(0));
			return;
		}

		if (selectedCoordinate != null) {
			Player<?> playerLandedUpon = game.getFieldModel().getPlayer(selectedCoordinate);
			if (playerLandedUpon != null && !playerLandedUpon.getId().equals(carriedPlayer.getId())) {
				placePlayerOnOccupiedSquare(game, carrier, carriedPlayer, playerLandedUpon, selectedCoordinate);
			} else {
				placePlayerOnEmptySquare(game, carrier, carriedPlayer, selectedCoordinate);
			}			
			return;
		}

		prepareClientData(game);
	}

	private List<FieldCoordinate> findEmptySquares(Game game, FieldCoordinate carrierCoordinate) {
		FieldModel fieldModel = game.getFieldModel();
		return Arrays.stream(fieldModel.findAdjacentCoordinates(carrierCoordinate, FieldCoordinateBounds.FIELD, 1, false))
			.filter(coordinate -> fieldModel.getPlayer(coordinate) == null)
			.collect(Collectors.toList());
	}

	private void prepareClientData(Game game) {
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.clearMoveSquares();
		eligibleSquares.stream().map(square -> new MoveSquare(square, 0, 0)).forEach(fieldModel::add);

		if (game.getTurnMode() != TurnMode.PLACE_CARRIED_PLAYER) {
			savedTurnMode = game.getTurnMode();
			game.setTurnMode(TurnMode.PLACE_CARRIED_PLAYER);
		}

		UtilServerDialog.showDialog(getGameState(),
				new DialogInformationOkayParameter("I'll Carry You", "Select a square to place carried player.", false), false);

		getResult().setNextAction(StepAction.CONTINUE);
	}

	private void placePlayerOnEmptySquare(Game game, Player<?> carrier, Player<?> carriedPlayer, FieldCoordinate coordinate) {
		PlayerState oldState = getGameState().getCarriedPlayer().getOldState();
		game.getFieldModel().setPlayerCoordinate(carriedPlayer, coordinate);
		game.getFieldModel().setPlayerState(carriedPlayer, oldState);

		if (getGameState().getCarriedPlayer().hasBall()) {
			game.getFieldModel().setBallCoordinate(coordinate);
			game.getFieldModel().setBallMoving(false);
		} else if (game.getFieldModel().isBallMoving() && coordinate.equals(game.getFieldModel().getBallCoordinate())) {
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
		}

		leave(game, carrier);
	}

	private void placePlayerOnOccupiedSquare(Game game, Player<?> carrier, Player<?> carriedPlayer, Player<?> playerLandedUpon,
		FieldCoordinate coordinate) {

		InjuryResult injuryResultHitPlayer = UtilServerInjury.handleInjury(this, new InjuryTypeTTMHitPlayer(), null,
			playerLandedUpon, coordinate, null, null, ApothecaryMode.HIT_PLAYER);

		List<DeferredCommand> commands = new ArrayList<>();
		GameOptionBoolean alwaysTurnOver = (GameOptionBoolean) game.getOptions()
			.getOptionWithDefault(GameOptionId.END_TURN_WHEN_HITTING_ANY_PLAYER_WITH_TTM);
		if (alwaysTurnOver.isEnabled() || ((game.isHomePlaying() && game.getTeamHome().hasPlayer(playerLandedUpon))
			|| (!game.isHomePlaying() && game.getTeamAway().hasPlayer(playerLandedUpon)))) {
			commands.add(new HitPlayerTurnOverCommand());
		}
		commands.add(new DropPlayerCommand(playerLandedUpon.getId(), ApothecaryMode.HIT_PLAYER, true));

		scatterCarriedPlayer(game, carriedPlayer, coordinate);

		getResult().addReport(new ReportPlayerEvent(playerLandedUpon.getId(), "was hit"));
		publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
			new SteadyFootingContext(injuryResultHitPlayer, commands)));

		leave(game, carrier);
	}

	private void pushCarriedPlayerIntoCrowd(Game game, Player<?> carriedPlayer, FieldCoordinate carrierCoordinate) {
		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
				UtilServerInjury.handleInjury(this, new InjuryTypeCrowdPush(), null, carriedPlayer, carrierCoordinate,
						null, null, ApothecaryMode.CROWD_PUSH)));

		if (getGameState().getCarriedPlayer().hasBall()) {
			game.getFieldModel().setBallCoordinate(null);
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
			publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, carrierCoordinate));
		}

		publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
	}

	private void scatterCarriedPlayer(Game game, Player<?> carriedPlayer, FieldCoordinate coordinate) {
		CarriedPlayer carriedPlayerState = getGameState().getCarriedPlayer();
		PlayerState oldState = carriedPlayerState.getOldState();
		PlayerState thrownPlayerState = oldState.changeBase(PlayerState.PICKED_UP);
		boolean carriedPlayerHasBall = carriedPlayerState.hasBall();

		game.getFieldModel().setPlayerCoordinate(carriedPlayer, coordinate);
		game.getFieldModel().setPlayerState(carriedPlayer, oldState);

		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		((CarriedPlayerScatter) factory.forName(SequenceGenerator.Type.CarriedPlayerScatter.name()))
			.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));

		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, coordinate));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, carriedPlayer.getId()));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, thrownPlayerState));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, carriedPlayerHasBall));

		((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
			.pushSequence(new ScatterPlayer.SequenceParams(getGameState(), carriedPlayer.getId(), thrownPlayerState,
				carriedPlayerHasBall, coordinate, false, false, false, false));

		publishParameter(new StepParameter(StepParameterKey.OLD_DEFENDER_STATE, oldState));
		publishParameter(new StepParameter(StepParameterKey.IS_CARRIED_PLAYER, true));
	}

	private void leave(Game game, Player<?> carrier) {
		UtilServerDialog.hideDialog(getGameState());
		game.getFieldModel().clearMoveSquares();

		if (game.getTurnMode() == TurnMode.PLACE_CARRIED_PLAYER && savedTurnMode != null) {
			game.setTurnMode(savedTurnMode);
		}

		Skill skill = carrier.getSkillWithProperty(NamedProperties.canCarryPartner);
		if (skill != null) {
			game.getFieldModel().removeSkillEnhancements(carrier, skill);
		}

		getGameState().clearCarriedPlayer();
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (selectedCoordinate != null) {
			IServerJsonOption.FIELD_COORDINATE.addTo(jsonObject, selectedCoordinate.toJsonValue());
		}
		JsonArray jsonArray = new JsonArray();
		eligibleSquares.stream().map(FieldCoordinate::toJsonValue).forEach(jsonArray::add);
		IServerJsonOption.FIELD_COORDINATES.addTo(jsonObject, jsonArray);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, savedTurnMode);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		JsonObject fieldCoordinate = IServerJsonOption.FIELD_COORDINATE.getFrom(source, jsonObject);
		if (fieldCoordinate != null) {
			selectedCoordinate = new FieldCoordinate().initFrom(source, fieldCoordinate);
		}

		JsonArray jsonArray = IServerJsonOption.FIELD_COORDINATES.getFrom(source, jsonObject);
		if (jsonArray != null) {
			eligibleSquares.clear();
			jsonArray.values().stream()
				.map(value -> new FieldCoordinate().initFrom(source, value))
				.forEach(eligibleSquares::add);
		}

		savedTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(source, jsonObject);

		return this;
	}
}
