package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.ReportScatterPlayer;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step in the move sequence to handle skill BALL_AND_CHAIN.
 * <p>
 * Needs to be initialized with stepParameter DISPATCH_TO_LABEL. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_END. May be initialized with
 * stepParameter GAZE_VICTIM_ID. May be initialized with stepParameter
 * MOVE_STACK.
 * <p>
 * Expects stepParameter MOVE_STACK to be set by a preceding step.
 * <p>
 * Sets stepParameter COORDINATE_FROM for all steps on the stack. Sets
 * stepParameter COORDINATE_TO for all steps on the stack. Sets stepParameter
 * DISPATCH_PLAYER_ACTION for all steps on the stack. Sets stepParameter
 * END_TURN for all steps on the stack. Sets stepParameter END_PLAYER_ACTION for
 * all steps on the stack. Sets stepParameter MOVE_STACK for all steps on the
 * stack.
 * <p>
 * May replace rest of move sequence with inducement sequence.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepMoveBallAndChain extends AbstractStepWithReRoll {

	private static final ReRolledAction RE_ROLLED_ACTION = ReRolledActions.DIRECTION;
	private String fGotoLabelOnEnd;
	private String fGotoLabelOnFallDown;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private Direction playerScatter;

	public StepMoveBallAndChain(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MOVE_BALL_AND_CHAIN;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						fGotoLabelOnEnd = (String) parameter.getValue();
						break;
					// mandatory
					case GOTO_LABEL_ON_FALL_DOWN:
						fGotoLabelOnFallDown = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(fGotoLabelOnFallDown)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FALL_DOWN + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) parameter.getValue();
					return true;
				case COORDINATE_TO:
					fCoordinateTo = (FieldCoordinate) parameter.getValue();
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

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
				ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				ReRollSource rerollSource = clientCommandUseSkill.getSkill().getRerollSource(RE_ROLLED_ACTION);
				if (rerollSource != null) {
					setReRolledAction(RE_ROLLED_ACTION);
					boolean skillUsed = clientCommandUseSkill.isSkillUsed();
					getResult().addReport(new ReportSkillUse(clientCommandUseSkill.getPlayerId(), clientCommandUseSkill.getSkill(), skillUsed, SkillUse.RE_ROLL_DIRECTION));

					if (skillUsed) {
						setReRollSource(rerollSource);
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.movesRandomly)) {
			boolean doRoll = playerScatter == null || (getReRollSource() != null && UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer()));
			if (doRoll) {
				int scatterRoll = getGameState().getDiceRoller().rollThrowInDirection();
				if (fCoordinateFrom.getX() < fCoordinateTo.getX()) {
					playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
				} else if (fCoordinateFrom.getX() > fCoordinateTo.getX()) {
					playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
				} else if (fCoordinateFrom.getY() < fCoordinateTo.getY()) {
					playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
				} else { // coordinateFrom.getY() > coordinateTo.getY()
					playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
				}
				fCoordinateTo = UtilServerCatchScatterThrowIn.findScatterCoordinate(fCoordinateFrom, playerScatter, 1);
				getResult().addReport(new ReportScatterPlayer(fCoordinateFrom, fCoordinateTo, new Direction[]{playerScatter},
					new int[]{scatterRoll}));
				game.getFieldModel().add(new MoveSquare(fCoordinateTo, 0, 0));
				if (getReRollSource() == null) {
					ReRollSource reRollSource = UtilCards.getUnusedRerollSource(actingPlayer, RE_ROLLED_ACTION);
					if (reRollSource != null) {
						UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayerId(), reRollSource.getSkill(game), 0), false);
						return;
					}
					boolean askForReRoll = ((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.ALLOW_BALL_AND_CHAIN_RE_ROLL)).isEnabled();

					if (askForReRoll && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer, RE_ROLLED_ACTION, 0, false)) {
						return;
					}
				}
			}
			if (!FieldCoordinateBounds.FIELD.isInBounds(fCoordinateTo)) {
				publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeCrowdPush()));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFallDown);
				return;
			}
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, fCoordinateTo));
			Player<?> blockDefender = game.getFieldModel().getPlayer(fCoordinateTo);
			if (blockDefender != null) {
				actingPlayer.setCurrentMove(actingPlayer.getCurrentMove() + 1);
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game));
				publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, blockDefender.getId()));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
				return;
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_FALL_DOWN.addTo(jsonObject, fGotoLabelOnFallDown);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		IServerJsonOption.DIRECTION.addTo(jsonObject, playerScatter);
		return jsonObject;
	}

	@Override
	public StepMoveBallAndChain initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fGotoLabelOnFallDown = IServerJsonOption.GOTO_LABEL_ON_FALL_DOWN.getFrom(source, jsonObject);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		if (IServerJsonOption.DIRECTION.isDefinedIn(jsonObject)) {
			playerScatter = (Direction) IServerJsonOption.DIRECTION.getFrom(source, jsonObject);
		}
		return this;
	}

}
