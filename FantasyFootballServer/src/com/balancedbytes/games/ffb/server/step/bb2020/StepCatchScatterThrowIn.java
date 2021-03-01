package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.inducement.InducementDuration;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.factory.CatchModifierFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportCatchRoll;
import com.balancedbytes.games.ffb.report.ReportScatterBall;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.report.ReportThrowIn;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeStab;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.generator.common.SpikedBallApo;
import com.balancedbytes.games.ffb.server.util.UtilServerCards;
import com.balancedbytes.games.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
@RulesCollection(RulesCollection.Rules.BB2020)
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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case CATCH_SCATTER_THROW_IN_MODE:
				fCatchScatterThrowInMode = (CatchScatterThrowInMode) pParameter.getValue();
				consume(pParameter);
				return true;
			case THROW_IN_COORDINATE:
				fThrowInCoordinate = (FieldCoordinate) pParameter.getValue();
				consume(pParameter);
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
					if (fCatchScatterThrowInMode == null && getGameState().getPassState().isDeflectionSuccessful()) {
						getGameState().getPassState().setInterceptionSuccessful(true);
					}
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
							|| UtilCards.isCardActive(game, Card.SPIKED_BALL))) {
				InjuryResult injuryResultCatcher = UtilServerInjury.handleInjury(this, new InjuryTypeStab(), null,
						playerUnderBall, game.getFieldModel().getBallCoordinate(), null, ApothecaryMode.CATCHER);
				getGameState().pushCurrentStepOnStack();
				SequenceGeneratorFactory factory = game.getFactory(Factory.SEQUENCE_GENERATOR);
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
				getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "pushCurrentStepOnStack()");
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

		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "catchBall()");

		Game game = getGameState().getGame();
		state.catcher = game.getPlayerById(fCatcherId);
		if ((state.catcher == null) || state.catcher.hasSkillWithProperty(NamedProperties.preventCatch)) {
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
			Set<CatchModifier> catchModifiers = modifierFactory.findModifiers(new CatchContext(game, state.catcher, fCatchScatterThrowInMode));
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

		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "scatterBall()");

		Game game = getGameState().getGame();
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

		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "throwInBall()");

		Game game = getGameState().getGame();
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
		getResult().setAnimation(new Animation(AnimationType.PASS, ballCoordinateStart, lastValidCoordinate, null));

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
	public StepCatchScatterThrowIn initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		fScatterBounds = null;
		JsonObject scatterBoundsObject = IServerJsonOption.SCATTER_BOUNDS.getFrom(game, jsonObject);
		if (scatterBoundsObject != null) {
			fScatterBounds = new FieldCoordinateBounds().initFrom(game, scatterBoundsObject);
		}
		fCatchScatterThrowInMode = (CatchScatterThrowInMode) IServerJsonOption.CATCH_SCATTER_THROW_IN_MODE
				.getFrom(game, jsonObject);
		fThrowInCoordinate = IServerJsonOption.THROW_IN_COORDINATE.getFrom(game, jsonObject);
		fDivingCatchChoice = IServerJsonOption.DIVING_CATCH_CHOICE.getFrom(game, jsonObject);
		fBombMode = IServerJsonOption.BOMB_MODE.getFrom(game, jsonObject);
		return this;
	}

}
