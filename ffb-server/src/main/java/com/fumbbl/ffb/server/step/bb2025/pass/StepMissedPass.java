package com.fumbbl.ffb.server.step.bb2025.pass;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.DirectionFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportScatterBall;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.mixed.ReportEvent;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Step of the pass sequence to handle a missed pass.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter THROWIN_COORDINATE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepMissedPass extends AbstractStep {

	private final List<Integer> rollList = new ArrayList<>();
	private final List<Direction> directionList = new ArrayList<>();

	private FieldCoordinate coordinateStart, coordinateEnd, lastValidCoordinate;
	private Direction direction;

	private int roll;

	private boolean doRoll, reRolling;

	public StepMissedPass(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MISSED_PASS;
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
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
				ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				if (clientCommandUseSkill.getSkill().hasSkillProperty(NamedProperties.canReRollHmpScatter)) {
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					if (rollList.size() < 3) {
						Game game = getGameState().getGame();
						doRoll = clientCommandUseSkill.isSkillUsed();
						if (doRoll) {
							game.getActingPlayer().markSkillUsed(clientCommandUseSkill.getSkill());
						}
					}
					getResult().addReport(new ReportSkillUse(clientCommandUseSkill.getPlayerId(), clientCommandUseSkill.getSkill(), doRoll, SkillUse.RE_ROLL_DIRECTION));
					if (clientCommandUseSkill.isSkillUsed()) {
						getGameState().getPassState().setUsingBlastIt(true);
					} else if (clientCommandUseSkill.isNeverUse()) {
						getGameState().getPassState().setUsingBlastIt(false);
					}

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
		PassState state = getGameState().getPassState();

		if (coordinateStart == null) {
			doRoll = true;
			coordinateStart = game.getPassCoordinate();
		}
		while (FieldCoordinateBounds.FIELD.isInBounds(coordinateStart) && (rollList.size() < 3)) {
			if (doRoll) {
				roll = getGameState().getDiceRoller().rollScatterDirection();
				direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
				coordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(coordinateStart, direction, 1);
				lastValidCoordinate = FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd) ? coordinateEnd : coordinateStart;
			}

			if (reRolling) {
				game.getFieldModel().clearMoveSquares();
				game.getFieldModel().setBallCoordinate(coordinateEnd);
				game.getFieldModel().setBallMoving(true);
			}

			boolean hasBlastIt = UtilCards.hasSkillWithProperty(game.getActingPlayer().getPlayer(), NamedProperties.canReRollHmpScatter);
			boolean hasUnusedBlastIt = UtilCards.hasUnusedSkillWithProperty(game.getActingPlayer(), NamedProperties.canReRollHmpScatter);

			if (game.getActingPlayer().getPlayerAction() == PlayerAction.HAIL_MARY_PASS
				&& ((hasUnusedBlastIt && state.getUsingBlastIt() == null) || (hasBlastIt && toPrimitive(state.getUsingBlastIt())))
				&& !reRolling) {

				reportDirectionRoll();

				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(game.getThrowerId(), game.getThrower().getSkillWithProperty(NamedProperties.canReRollHmpScatter),
						0, state.getUsingBlastIt() == null), false);

				reRolling = true;
				game.getFieldModel().clearMoveSquares();
				game.getFieldModel().add(new MoveSquare(coordinateEnd, 0, 0));
				return;
			}

			if (reRolling && doRoll) {
				reportDirectionRoll();
			}

			rollList.add(roll);
			directionList.add(direction);

			doRoll = true;
			reRolling = false;

			coordinateStart = coordinateEnd;
		}
		int[] rolls = new int[rollList.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = rollList.get(i);
		}

		Direction[] directions = directionList.toArray(new Direction[0]);
		getResult().addReport(new ReportScatterBall(directions, rolls, false));

		game.setPassCoordinate(lastValidCoordinate);
		game.getFieldModel().setOutOfBounds(lastValidCoordinate != coordinateEnd);
		RangeRuler rangeRuler = new RangeRuler(game.getThrowerId(), lastValidCoordinate, -1, false);

		game.getFieldModel().setRangeRuler(rangeRuler);
		if (PlayerAction.THROW_BOMB == game.getThrowerAction() || PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
			game.getFieldModel().setBombCoordinate(lastValidCoordinate);
			game.getFieldModel().setBombMoving(true);

		} else {
			game.getFieldModel().setBallCoordinate(lastValidCoordinate);
			game.getFieldModel().setBallMoving(true);
		}
		game.getFieldModel().clearMoveSquares();
		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private void reportDirectionRoll() {
		getResult().addReport(new ReportScatterBall(new Direction[]{direction}, new int[]{roll}, false));
		if (!FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd)) {
			getResult().addReport(new ReportEvent("The ball would land out of bounds!"));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.ROLLS.addTo(jsonObject, rollList);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, coordinateStart);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, coordinateEnd);
		IServerJsonOption.COORDINATE.addTo(jsonObject, lastValidCoordinate);
		IServerJsonOption.DIRECTION.addTo(jsonObject, direction);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.DO_ROLL.addTo(jsonObject, doRoll);
		IServerJsonOption.RE_ROLLING.addTo(jsonObject, reRolling);
		JsonArray directionArray = new JsonArray();
		directionList.stream().map(UtilJson::toJsonValue).forEach(directionArray::add);
		IServerJsonOption.DIRECTION_ARRAY.addTo(jsonObject, directionArray);
		return jsonObject;
	}

	@Override
	public StepMissedPass initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		int[] rolls = IServerJsonOption.ROLLS.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(rolls)) {
			Arrays.stream(rolls).forEach(rollList::add);
		}
		coordinateStart = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		coordinateEnd = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		lastValidCoordinate = IServerJsonOption.COORDINATE.getFrom(source, jsonObject);
		direction = (Direction) IServerJsonOption.DIRECTION.getFrom(source, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		doRoll = toPrimitive(IServerJsonOption.DO_ROLL.getFrom(source, jsonObject));
		reRolling = toPrimitive(IServerJsonOption.RE_ROLLING.getFrom(source, jsonObject));
		if (IServerJsonOption.DIRECTION_ARRAY.isDefinedIn(jsonObject)) {
			IServerJsonOption.DIRECTION_ARRAY.getFrom(source, jsonObject).values().stream()
				.map(direction -> (Direction) UtilJson.toEnumWithName(source.<DirectionFactory>getFactory(FactoryType.Factory.DIRECTION), direction))
				.forEach(directionList::add);
		}
		return this;
	}
}
