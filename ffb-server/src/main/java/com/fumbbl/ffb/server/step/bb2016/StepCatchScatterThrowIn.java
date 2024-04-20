package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.CatchModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportCatchRoll;
import com.fumbbl.ffb.report.ReportScatterBall;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.ReportThrowIn;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeStab;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.SpikedBallApo;
import com.fumbbl.ffb.server.util.UtilServerCards;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Set;

/**
 * Step in any sequence to handle scattering the ball and throw-ins. Consumes
 * all expected stepParameters.
 *
 * Expects stepParameter CATCH_SCATTER_THROWIN_MODE to be set by a preceding
 * step. Expects stepParameter THROW_IN_COORDINATE to be set by a preceding
 * step.
 *
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepCatchScatterThrowIn extends AbstractStepWithReRoll {

	public static class StepState {
		public boolean rerollCatch;
		public Player<?> catcher;
	}

	private StepState state;

	private String fCatcherId;
	private FieldCoordinateBounds fScatterBounds;
	private CatchScatterThrowInMode fCatchScatterThrowInMode;
	private FieldCoordinate fThrowInCoordinate;
	private Boolean fDivingCatchChoice;
	private boolean fBombMode;

	public StepCatchScatterThrowIn(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.CATCH_SCATTER_THROW_IN;
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
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (PlayerChoiceMode.DIVING_CATCH == playerChoiceCommand.getPlayerChoiceMode()) {
					fDivingCatchChoice = StringTool.isProvided(playerChoiceCommand.getPlayerId());
					fCatcherId = playerChoiceCommand.getPlayerId();
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
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
			case CATCH_SCATTER_THROW_IN_MODE:
				fCatchScatterThrowInMode = (CatchScatterThrowInMode) parameter.getValue();
				consume(parameter);
				return true;
			case THROW_IN_COORDINATE:
				fThrowInCoordinate = (FieldCoordinate) parameter.getValue();
				consume(parameter);
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private void executeStep() {
		getResult().reset();
		Game game = getGameState().getGame();
		UtilServerDialog.hideDialog(getGameState());

		if (fCatchScatterThrowInMode == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		if (game.getTurnMode() == TurnMode.KICKOFF) {
			if (game.isHomePlaying()) {
				fScatterBounds = FieldCoordinateBounds.HALF_AWAY;
			} else {
				fScatterBounds = FieldCoordinateBounds.HALF_HOME;
			}
		} else {
			fScatterBounds = FieldCoordinateBounds.FIELD;
		}
		Player<?> playerUnderBall = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
		switch (fCatchScatterThrowInMode) {
		case CATCH_BOMB:
		case CATCH_ACCURATE_BOMB_EMPTY_SQUARE:
		case CATCH_ACCURATE_BOMB:
		case DEFLECTED_BOMB:
			fBombMode = true;
			if (!StringTool.isProvided(fCatcherId)) {
				Player<?> playerUnderBomb = game.getFieldModel().getPlayer(game.getFieldModel().getBombCoordinate());
				fCatcherId = (playerUnderBomb != null) ? playerUnderBomb.getId() : null;
			}
			if (StringTool.isProvided(fCatcherId)) {
				PlayerState catcherState = game.getFieldModel().getPlayerState(game.getPlayerById(fCatcherId));
				if ((catcherState != null) && catcherState.hasTacklezones() && game.getFieldModel().isBombMoving()) {
					fCatchScatterThrowInMode = catchBall();
				} else {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
				}
			} else {
				if (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == fCatchScatterThrowInMode) {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.CATCH_BOMB;
				}
				fCatchScatterThrowInMode = divingCatch(game.getFieldModel().getBombCoordinate());
			}
			if ((fCatchScatterThrowInMode == CatchScatterThrowInMode.FAILED_CATCH)
					|| (fCatchScatterThrowInMode == CatchScatterThrowInMode.SCATTER_BALL)) {
				game.getFieldModel().setBombMoving(true);
				fCatchScatterThrowInMode = null;
			}
			break;
		case CATCH_ACCURATE_PASS:
		case CATCH_HAND_OFF:
		case CATCH_SCATTER:
		case DEFLECTED:
			fBombMode = false;
			if (!StringTool.isProvided(fCatcherId)) {
				fCatcherId = (playerUnderBall != null) ? playerUnderBall.getId() : null;
			}
			if (StringTool.isProvided(fCatcherId)) {
				PlayerState catcherState = game.getFieldModel().getPlayerState(game.getPlayerById(fCatcherId));
				if ((catcherState != null) && catcherState.hasTacklezones() && game.getFieldModel().isBallInPlay()
						&& game.getFieldModel().isBallMoving()) {
					fCatchScatterThrowInMode = catchBall();
				} else {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
				}
			} else {
				fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
			}
			break;
		case CATCH_KICKOFF:
		case CATCH_THROW_IN:
		case CATCH_ACCURATE_PASS_EMPTY_SQUARE:
		case CATCH_MISSED_PASS:
			fBombMode = false;
			if (playerUnderBall != null) {
				fCatchScatterThrowInMode = CatchScatterThrowInMode.CATCH_SCATTER;
			} else {
				fCatchScatterThrowInMode = divingCatch(game.getFieldModel().getBallCoordinate());
			}
			break;
		case THROW_IN:
			fBombMode = false;
			if (fThrowInCoordinate != null) {
				fCatchScatterThrowInMode = throwInBall();
			} else {
				fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
			}
			break;
		case FAILED_CATCH:
		case FAILED_PICK_UP:
			fBombMode = false;
			if ((playerUnderBall != null) && game.getFieldModel().isBallInPlay()
					&& (UtilGameOption.isOptionEnabled(game, GameOptionId.SPIKED_BALL)
							|| game.isActive(NamedProperties.droppedBallCausesArmourRoll))) {
				InjuryResult injuryResultCatcher = UtilServerInjury.handleInjury(this, new InjuryTypeStab(false), null,
					playerUnderBall, game.getFieldModel().getBallCoordinate(), null, null, ApothecaryMode.CATCHER);
				getGameState().pushCurrentStepOnStack();
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((SpikedBallApo) factory.forName(SequenceGenerator.Type.SpikedBallApo.name()))
					.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
				fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
				getResult().setNextAction(StepAction.NEXT_STEP);
				if (injuryResultCatcher.injuryContext().isArmorBroken()) {
					publishParameters(UtilServerInjury.dropPlayer(this, playerUnderBall, ApothecaryMode.CATCHER));
				}
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultCatcher));
				return;
			}
			// drop through to regular scatter
		case SCATTER_BALL:
			fBombMode = false;
			if (game.getFieldModel().isBallInPlay()) {
				fCatchScatterThrowInMode = scatterBall();
			} else {
				fCatchScatterThrowInMode = null;
			}
			break;
		default:
			break;
		}
		if ((getReRolledAction() != null) || (game.getDialogParameter() != null)) {
			getResult().setNextAction(StepAction.CONTINUE);
		} else {
			// repeat this step until it is finished
			if (fCatchScatterThrowInMode != null) {
				getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "pushCurrentStepOnStack()");
				fDivingCatchChoice = null;
				getGameState().pushCurrentStepOnStack();
			} else {
				Player<?> catcher;
				if (fBombMode) {
					catcher = !game.getFieldModel().isBombMoving()
							? game.getFieldModel().getPlayer(game.getFieldModel().getBombCoordinate())
							: null;
				} else {
					catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
				}
				publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, (catcher != null) ? catcher.getId() : null));
				deactivateCards();

				// Diving Catch during kickoff might take the ball out of bounds
				if (game.getTurnMode() == TurnMode.KICKOFF
						&& !fScatterBounds.isInBounds(game.getFieldModel().getBallCoordinate())) {
					publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, true));
				}

			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void deactivateCards() {
		Game game = getGameState().getGame();
		for (Player<?> player : game.getPlayers()) {
			for (Card card : game.getFieldModel().getCards(player)) {
				if ((InducementDuration.WHILE_HOLDING_THE_BALL == card.getDuration()) && !UtilPlayer.hasBall(game, player)) {
					UtilServerCards.deactivateCard(this, card);
				}
			}
		}
	}

	private CatchScatterThrowInMode divingCatch(FieldCoordinate pCoordinate) {
		Game game = getGameState().getGame();
		if (fDivingCatchChoice == null) {
			fCatcherId = null;
			Player<?>[] divingCatchersHome = UtilServerCatchScatterThrowIn.findDivingCatchers(getGameState(), game.getTeamHome(),
					pCoordinate);
			Player<?>[] divingCatchersAway = UtilServerCatchScatterThrowIn.findDivingCatchers(getGameState(), game.getTeamAway(),
					pCoordinate);
			if (ArrayTool.isProvided(divingCatchersHome) && ArrayTool.isProvided(divingCatchersAway)) {
				fDivingCatchChoice = false;
				Skill skill = divingCatchersHome[0].getSkillWithProperty(NamedProperties.canAttemptCatchInAdjacentSquares);
				getResult().addReport(new ReportSkillUse(skill, false, SkillUse.CANCEL_DIVING_CATCH));
			} else if (ArrayTool.isProvided(divingCatchersHome)) {
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getTeamHome().getId(),
						PlayerChoiceMode.DIVING_CATCH, divingCatchersHome, null, 1), !game.isHomePlaying());
			} else if (ArrayTool.isProvided(divingCatchersAway)) {
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getTeamAway().getId(),
						PlayerChoiceMode.DIVING_CATCH, divingCatchersAway, null, 1), game.isHomePlaying());
			} else {
				fDivingCatchChoice = false;
			}
		}
		if (fDivingCatchChoice != null) {
			if (fDivingCatchChoice) {
				Player<?> divingCatcher = game.getPlayerById(fCatcherId);
				if (getReRollSource() == null) {
					Skill skill = divingCatcher.getSkillWithProperty(NamedProperties.canAttemptCatchInAdjacentSquares);
					getResult().addReport(
							new ReportSkillUse(divingCatcher.getId(), skill, true, SkillUse.CATCH_BALL));
				}
				return catchBall();
			} else {
				return CatchScatterThrowInMode.SCATTER_BALL;
			}
		}
		return fCatchScatterThrowInMode;
	}

	private CatchScatterThrowInMode catchBall() {


		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "catchBall()");

		state.catcher = game.getPlayerById(fCatcherId);
		if ((state.catcher == null) || state.catcher.hasSkillProperty(NamedProperties.preventCatch)) {
			return CatchScatterThrowInMode.SCATTER_BALL;
		}
		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(state.catcher);

		boolean doRoll = true;
		if (ReRolledActions.CATCH == getReRolledAction()) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), state.catcher)) {
				doRoll = false;
			}
		}

		if (doRoll) {
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			CatchModifierFactory modifierFactory = game.getFactory(Factory.CATCH_MODIFIER);
			Set<CatchModifier> catchModifiers = modifierFactory.findModifiers(new CatchContext(game, state.catcher, fCatchScatterThrowInMode, null));
			int minimumRoll = mechanic.minimumRollCatch(state.catcher, catchModifiers);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.CATCH) && (getReRollSource() != null));
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			getResult().addReport(new ReportCatchRoll(state.catcher.getId(), successful, roll, minimumRoll, reRolled,
					catchModifiers.toArray(new CatchModifier[0]), fCatchScatterThrowInMode.isBomb()));

			if (successful) {

				if (fCatchScatterThrowInMode.isBomb()) {
					game.getFieldModel().setBombCoordinate(catcherCoordinate);
					game.getFieldModel().setBombMoving(false);
				} else {
					game.getFieldModel().setBallCoordinate(catcherCoordinate);
					game.getFieldModel().setBallMoving(false);
				}
				getResult().setSound(SoundId.CATCH);
				setReRolledAction(null);
				if (((fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_HAND_OFF)
						|| (fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_ACCURATE_PASS))
						&& (game.getTurnMode() != TurnMode.DUMP_OFF)
						&& ((game.isHomePlaying() && game.getTeamAway().hasPlayer(state.catcher))
								|| (!game.isHomePlaying() && game.getTeamHome().hasPlayer(state.catcher)))) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}
				return null;

			} else {
				if (getReRolledAction() != ReRolledActions.CATCH) {

					boolean stopProcessing = getGameState().executeStepHooks(this, state);
					if (state.rerollCatch) {
						return catchBall();
					}
					if (!stopProcessing) {
						if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), state.catcher, ReRolledActions.CATCH, minimumRoll,
								false)) {
							setReRolledAction(ReRolledActions.CATCH);
							return fCatchScatterThrowInMode;
						}
					}
				}
			}

		}

		setReRolledAction(null);
		if (catcherCoordinate != null) {
			if (fCatchScatterThrowInMode.isBomb()) {
				game.getFieldModel().setBombCoordinate(catcherCoordinate);
				game.getFieldModel().setBombMoving(true);
			} else {
				game.getFieldModel().setBallCoordinate(catcherCoordinate);
				game.getFieldModel().setBallMoving(true);
			}
		}
		return CatchScatterThrowInMode.FAILED_CATCH;

	}

	private CatchScatterThrowInMode scatterBall() {


		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "scatterBall()");
		setReRolledAction(null);
		setReRollSource(null);
		state = new StepState();

		int roll = getGameState().getDiceRoller().rollScatterDirection();
		Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
		FieldCoordinate ballCoordinateStart = game.getFieldModel().getBallCoordinate();
		FieldCoordinate ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(ballCoordinateStart,
				direction, 1);
		FieldCoordinate lastValidCoordinate = fScatterBounds.isInBounds(ballCoordinateEnd) ? ballCoordinateEnd
				: ballCoordinateStart;
		getResult().addReport(new ReportScatterBall(new Direction[] { direction }, new int[] { roll }, false));
		getResult().setSound(SoundId.BOUNCE);

		game.getFieldModel().setBallCoordinate(ballCoordinateEnd);
		game.getFieldModel().setBallMoving(true);

		if (fScatterBounds.isInBounds(ballCoordinateEnd)) {
			Player<?> player = game.getFieldModel().getPlayer(ballCoordinateEnd);
			if (player != null) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				if (playerState.hasTacklezones()) {
					fCatcherId = player.getId();
					return CatchScatterThrowInMode.CATCH_SCATTER;
				} else {
					return CatchScatterThrowInMode.SCATTER_BALL;
				}
			}
		} else {
			if (fScatterBounds.equals(FieldCoordinateBounds.FIELD)) {
				fThrowInCoordinate = lastValidCoordinate;
				return CatchScatterThrowInMode.THROW_IN;
			} else {
				publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, true));
			}
		}

		return null;

	}

	private CatchScatterThrowInMode throwInBall() {


		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "throwInBall()");
		DiceRoller diceRoller = getGameState().getDiceRoller();
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
		FieldCoordinate ballCoordinateStart = fThrowInCoordinate;
		fCatcherId = null;

		int directionRoll = diceRoller.rollThrowInDirection();
		Direction direction = diceInterpreter.interpretThrowInDirectionRoll(ballCoordinateStart, directionRoll);
		int[] distanceRoll = diceRoller.rollThrowInDistance();
		int distance = distanceRoll[0] + distanceRoll[1];
		FieldCoordinate ballCoordinateEnd = ballCoordinateStart;
		FieldCoordinate lastValidCoordinate = ballCoordinateEnd;
		for (int i = 0; i < distance; i++) {
			ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(ballCoordinateStart, direction, i);
			if (FieldCoordinateBounds.FIELD.isInBounds(ballCoordinateEnd)) {
				lastValidCoordinate = ballCoordinateEnd;
			}
		}

		getResult().addReport(new ReportThrowIn(direction, directionRoll, distanceRoll));
		getResult().setAnimation(new Animation(AnimationType.PASS, ballCoordinateStart, lastValidCoordinate));

		game.getFieldModel().setBallMoving(true);

		if (ballCoordinateEnd.equals(lastValidCoordinate)) {
			game.getFieldModel().setBallCoordinate(lastValidCoordinate);
			fThrowInCoordinate = null;
			return CatchScatterThrowInMode.CATCH_THROW_IN;
		} else {
			game.getFieldModel().setBallCoordinate(null);
			fThrowInCoordinate = lastValidCoordinate;
			return CatchScatterThrowInMode.THROW_IN;
		}

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		if (fScatterBounds != null) {
			IServerJsonOption.SCATTER_BOUNDS.addTo(jsonObject, fScatterBounds.toJsonValue());
		}
		IServerJsonOption.CATCH_SCATTER_THROW_IN_MODE.addTo(jsonObject, fCatchScatterThrowInMode);
		IServerJsonOption.THROW_IN_COORDINATE.addTo(jsonObject, fThrowInCoordinate);
		IServerJsonOption.DIVING_CATCH_CHOICE.addTo(jsonObject, fDivingCatchChoice);
		IServerJsonOption.BOMB_MODE.addTo(jsonObject, fBombMode);
		return jsonObject;
	}

	@Override
	public StepCatchScatterThrowIn initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		fScatterBounds = null;
		JsonObject scatterBoundsObject = IServerJsonOption.SCATTER_BOUNDS.getFrom(source, jsonObject);
		if (scatterBoundsObject != null) {
			fScatterBounds = new FieldCoordinateBounds().initFrom(source, scatterBoundsObject);
		}
		fCatchScatterThrowInMode = (CatchScatterThrowInMode) IServerJsonOption.CATCH_SCATTER_THROW_IN_MODE
				.getFrom(source, jsonObject);
		fThrowInCoordinate = IServerJsonOption.THROW_IN_COORDINATE.getFrom(source, jsonObject);
		fDivingCatchChoice = IServerJsonOption.DIVING_CATCH_CHOICE.getFrom(source, jsonObject);
		fBombMode = IServerJsonOption.BOMB_MODE.getFrom(source, jsonObject);
		return this;
	}

}
