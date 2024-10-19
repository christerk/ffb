package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.CatchModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportCatchRoll;
import com.fumbbl.ffb.report.ReportList;
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
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.step.generator.QuickBite;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.SpikedBallApo;
import com.fumbbl.ffb.server.util.UtilServerCards;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Step in any sequence to handle scattering the ball and throw-ins. Consumes
 * all expected stepParameters.
 * <p>
 * Expects stepParameter CATCH_SCATTER_THROWIN_MODE to be set by a preceding
 * step. Expects stepParameter THROW_IN_COORDINATE to be set by a preceding
 * step.
 * <p>
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
	private boolean fBombMode, evaluate;
	private DivingCatchPhase phase = DivingCatchPhase.ASK_HOME;
	private final List<String> divingCatchers = new ArrayList<>();
	private String divingCatchControlTeam;
	private int roll;
	private Boolean usingModifyingSkill;
	private ReportList reports = new ReportList();

	private transient boolean repeat;

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
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					List<String> selected = Arrays.stream(playerChoiceCommand.getPlayerIds()).collect(Collectors.toList());
					switch (phase) {
						case ASK_HOME:
							divingCatchers.addAll(selected);
							phase = DivingCatchPhase.ASK_AWAY;
							if (!selected.isEmpty()) {
								divingCatchControlTeam = game.getTeamHome().getId();
							}
							break;
						case ASK_AWAY:
							divingCatchers.addAll(selected);
							phase = DivingCatchPhase.PROCESS;
							if (!selected.isEmpty()) {
								divingCatchControlTeam = divingCatchControlTeam == null ? game.getTeamAway().getId() : game.getActingTeam().getId();
							}
							break;
						default:
							fCatcherId = selected.get(0);
							break;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					PassState passState = getGameState().getPassState();
					ActingPlayer actingPlayer = game.getActingPlayer();
					if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.grantsCatchBonusToReceiver)) {
						if (passState != null) {
							passState.setUsingBlastIt(commandUseSkill.isSkillUsed());
							if (commandUseSkill.isSkillUsed()) {
								evaluate = true;
								actingPlayer.markSkillUsed(commandUseSkill.getSkill());
							}
							setReRollSource(null);
							reports.add(new ReportSkillUse(actingPlayer.getPlayerId(), commandUseSkill.getSkill(), commandUseSkill.isSkillUsed(), SkillUse.GRANT_CATCH_BONUS));
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					} else if (commandUseSkill.getSkill().getRerollSource(getReRolledAction()) != null) {
						if (commandUseSkill.isSkillUsed()) {
							setReRollSource(commandUseSkill.getSkill().getRerollSource(getReRolledAction()));
						} else if (passState != null) {
							setReRollSource(null);
							passState.setUsingBlastIt(false);
							reports.add(new ReportSkillUse(fCatcherId, commandUseSkill.getSkill(), commandUseSkill.isSkillUsed(), SkillUse.RE_ROLL_CATCH));
							reports.add(new ReportSkillUse(actingPlayer.getPlayerId(), UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.grantsCatchBonusToReceiver), commandUseSkill.isSkillUsed(), SkillUse.GRANT_CATCH_BONUS));
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
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

	@Override
	public void repeat() {
		super.repeat();
		executeStep();
	}

	@SuppressWarnings("fallthrough")
	private void executeStep() {
		getResult().reset();
		Arrays.stream(reports.getReports()).forEach(report -> getResult().addReport(report));
		reports.clear();

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
		boolean deflectedBomb = false;
		boolean deflectedPass = false;
		switch (fCatchScatterThrowInMode) {
			case DEFLECTED_BOMB:
				deflectedBomb = true;
				// fall through
			case CATCH_BOMB:
			case CATCH_ACCURATE_BOMB_EMPTY_SQUARE:
			case CATCH_ACCURATE_BOMB:
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
					if (deflectedBomb) {
						fCatchScatterThrowInMode = scatterBomb();
					} else {
						game.getFieldModel().setBombMoving(true);
						fCatchScatterThrowInMode = null;
					}
				}
				break;
			case DEFLECTED:
				deflectedPass = true;
				// fall through
			case CATCH_ACCURATE_PASS:
			case CATCH_HAND_OFF:
			case CATCH_SCATTER:
				fBombMode = false;
				if (!StringTool.isProvided(fCatcherId)) {
					fCatcherId = (playerUnderBall != null) ? playerUnderBall.getId() : null;
				}
				if (StringTool.isProvided(fCatcherId)) {
					PlayerState catcherState = game.getFieldModel().getPlayerState(game.getPlayerById(fCatcherId));
					if ((catcherState != null) && game.getFieldModel().isBallInPlay() && game.getFieldModel().isBallMoving()) {
						if (catcherState.hasTacklezones()) {
							fCatchScatterThrowInMode = catchBall();
						} else {
							fCatchScatterThrowInMode = CatchScatterThrowInMode.FAILED_CATCH;
						}
						if (fCatchScatterThrowInMode == null && deflectedPass && getGameState().getPassState().isDeflectionSuccessful()) {
							getGameState().getPassState().setInterceptionSuccessful(true);
						} else if (fCatchScatterThrowInMode == CatchScatterThrowInMode.FAILED_CATCH && deflectedPass) {
							fCatchScatterThrowInMode = CatchScatterThrowInMode.FAILED_DEFLECTION_CONVERSION;
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
			case FAILED_DEFLECTION_CONVERSION:
				deflectedPass = true;
				// fall through
			case FAILED_CATCH:
			case FAILED_PICK_UP:
				fBombMode = false;
				if ((playerUnderBall != null) && game.getFieldModel().isBallInPlay()
					&& (UtilGameOption.isOptionEnabled(game, GameOptionId.SPIKED_BALL)
					|| game.isActive(NamedProperties.droppedBallCausesArmourRoll))) {
					InjuryResult injuryResultCatcher = UtilServerInjury.handleInjury(this, new InjuryTypeStab(true), null,
						playerUnderBall, game.getFieldModel().getBallCoordinate(), null, null, ApothecaryMode.CATCHER);
					getGameState().pushCurrentStepOnStack();
					SequenceGeneratorFactory factory = game.getFactory(Factory.SEQUENCE_GENERATOR);
					((SpikedBallApo) factory.forName(SequenceGenerator.Type.SpikedBallApo.name()))
						.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
					fCatchScatterThrowInMode = deflectedPass ? CatchScatterThrowInMode.THREE_SQUARE_SCATTER : CatchScatterThrowInMode.SCATTER_BALL;
					getResult().setNextAction(StepAction.NEXT_STEP);
					if (injuryResultCatcher.injuryContext().isArmorBroken()) {
						publishParameters(UtilServerInjury.dropPlayer(this, playerUnderBall, ApothecaryMode.CATCHER));
					}
					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultCatcher));
					return;
				} else if (deflectedPass) {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.THREE_SQUARE_SCATTER;
					break;
				}
				// drop through to regular scatter
			case SCATTER_BALL:
				fBombMode = false;
				if (game.getFieldModel().isBallInPlay()) {
					fCatchScatterThrowInMode = bounceBall();
				} else {
					fCatchScatterThrowInMode = null;
				}
				break;
			case THREE_SQUARE_SCATTER:
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
			if (repeat) {
				repeat = false;
				getResult().setNextAction(StepAction.REPEAT);
				return;
			}
			// repeat this step until it is finished
			if (fCatchScatterThrowInMode != null) {
				getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "pushCurrentStepOnStack()");
				getGameState().pushCurrentStepOnStack();
			} else {
				Player<?> catcher;
				if (fBombMode) {
					catcher = !game.getFieldModel().isBombMoving()
						? game.getFieldModel().getPlayer(game.getFieldModel().getBombCoordinate())
						: null;
				} else {
					catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
					if (catcher != null) {
						Player<?>[] opponents = UtilPlayer.findAdjacentOpposingPlayersWithProperty(game, catcher, game.getFieldModel().getBallCoordinate(),
							NamedProperties.canAttackOpponentForBallAfterCatch, false, true);
						if (ArrayTool.isProvided(opponents)) {
							SequenceGeneratorFactory factory = game.getFactory(Factory.SEQUENCE_GENERATOR);
							((QuickBite) factory.forName(SequenceGenerator.Type.QuickBite.name()))
								.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
						}
					}
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
		if (phase == DivingCatchPhase.ASK_HOME) {
			Player<?>[] divingCatchers = UtilServerCatchScatterThrowIn.findDivingCatchers(getGameState(), game.getTeamHome(),
				pCoordinate);
			if (ArrayTool.isProvided(divingCatchers)) {
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getTeamHome().getId(),
					PlayerChoiceMode.DECLARE_DIVING_CATCH, divingCatchers, null, divingCatchers.length), !game.isHomePlaying());
			} else {
				phase = DivingCatchPhase.ASK_AWAY;
			}
		}
		if (phase == DivingCatchPhase.ASK_AWAY) {
			Player<?>[] divingCatchers = UtilServerCatchScatterThrowIn.findDivingCatchers(getGameState(), game.getTeamAway(),
				pCoordinate);
			if (ArrayTool.isProvided(divingCatchers)) {
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getTeamAway().getId(),
					PlayerChoiceMode.DECLARE_DIVING_CATCH, divingCatchers, null, divingCatchers.length), game.isHomePlaying());
			} else {
				phase = DivingCatchPhase.PROCESS;
			}
		}

		if (phase == DivingCatchPhase.PROCESS) {

			if ((fCatcherId != null && (getReRollSource() != null || divingCatchers.contains(fCatcherId))) || evaluate) {
				Player<?> divingCatcher = game.getPlayerById(fCatcherId);
				divingCatchers.remove(fCatcherId);
				if (getReRollSource() == null && !evaluate) {
					setReRolledAction(null);
					Skill skill = divingCatcher.getSkillWithProperty(NamedProperties.canAttemptCatchInAdjacentSquares);
					getResult().addReport(
						new ReportSkillUse(divingCatcher.getId(), skill, true, SkillUse.CATCH_BALL));
				}
				CatchScatterThrowInMode mode = catchBall();

				if (mode == null || mode == fCatchScatterThrowInMode) {
					return mode;
				}

				setReRolledAction(null);
				setReRollSource(null);
			}
			setReRolledAction(null);
			if (divingCatchers.isEmpty()) {
				return CatchScatterThrowInMode.SCATTER_BALL;
			}
			UtilServerGame.syncGameModel(this);
			if (divingCatchers.size() == 1) {
				repeat = true;
				fCatcherId = divingCatchers.get(0);
			} else {
				UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(divingCatchControlTeam,
					PlayerChoiceMode.DIVING_CATCH, divingCatchers.toArray(new String[0]), null, 1, 1), !game.getActingTeam().getId().equals(divingCatchControlTeam));
			}

		}
		return fCatchScatterThrowInMode;
	}

	private CatchScatterThrowInMode catchBall() {

		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "catchBall()");

		Player<?> catcher = game.getPlayerById(fCatcherId);
		state.catcher = catcher;
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

		if (doRoll || evaluate) {
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			CatchModifierFactory modifierFactory = game.getFactory(Factory.CATCH_MODIFIER);
			PassState passState = getGameState().getPassState();
			Set<CatchModifier> catchModifiers = modifierFactory.findModifiers(new CatchContext(game, state.catcher, fCatchScatterThrowInMode, passState != null ? passState.getUsingBlastIt() : null));
			int minimumRoll = mechanic.minimumRollCatch(state.catcher, catchModifiers);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.CATCH) && (getReRollSource() != null));
			if (doRoll) {
				roll = getGameState().getDiceRoller().rollSkill();
			}
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			getResult().addReport(new ReportCatchRoll(state.catcher.getId(), successful, roll, minimumRoll, reRolled || evaluate,
				catchModifiers.toArray(new CatchModifier[0]), fCatchScatterThrowInMode.isBomb()));
			evaluate = false;

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
				boolean successfulWithBlastIt = false;

				if (getGameState().getPassState() != null && game.getActingPlayer().getPlayerAction() == PlayerAction.HAIL_MARY_PASS) {
					Boolean usingBlastIt = getGameState().getPassState().getUsingBlastIt();

					if (UtilCards.hasUnusedSkillWithProperty(game.getActingPlayer(), NamedProperties.grantsCatchBonusToReceiver) && usingBlastIt == null) {
						Set<CatchModifier> catchModifiersWithBlastIt = modifierFactory.findModifiers(new CatchContext(game, state.catcher, fCatchScatterThrowInMode, true));
						successfulWithBlastIt = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, mechanic.minimumRollCatch(state.catcher, catchModifiersWithBlastIt));
					}
				}
				Optional<Skill> catchSkill = catcher.getSkillsIncludingTemporaryOnes().stream().filter(skill -> skill.getRerollSource(ReRolledActions.CATCH) != null).findFirst();

				if (getReRolledAction() != ReRolledActions.CATCH) {

					boolean stopProcessing = getGameState().executeStepHooks(this, state);
					GameOptionBoolean catchForBombs = (GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.CATCH_WORKS_FOR_BOMBS);
					if (state.rerollCatch && (!fCatchScatterThrowInMode.isBomb() || catchForBombs.isEnabled())) {
						if (successfulWithBlastIt) {
							UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(fCatcherId, catchSkill.orElse(null),
								minimumRoll, game.getThrower().getSkillWithProperty(NamedProperties.grantsCatchBonusToReceiver)), false);
							return fCatchScatterThrowInMode;
						} else {
							return catchBall();
						}
					}
					if (!stopProcessing) {
						if ((successfulWithBlastIt
							&& UtilServerReRoll.askForReRollIfAvailable(getGameState(), catcher, ReRolledActions.CATCH, 0, false,
							game.getThrower().getSkillWithProperty(NamedProperties.grantsCatchBonusToReceiver), null))
							|| UtilServerReRoll.askForReRollIfAvailable(getGameState(), state.catcher, ReRolledActions.CATCH, minimumRoll,
							false)) {
							setReRolledAction(ReRolledActions.CATCH);
							return fCatchScatterThrowInMode;
						}
					}
				} else if (successfulWithBlastIt) {
					UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getThrowerId(), game.getThrower().getSkillWithProperty(NamedProperties.grantsCatchBonusToReceiver),
						0), false);
					return fCatchScatterThrowInMode;
				}
			}

		}

		setReRolledAction(null);
		if (catcherCoordinate != null && phase != DivingCatchPhase.PROCESS) {
			if (fCatchScatterThrowInMode.isBomb()) {
				game.getFieldModel().setBombCoordinate(catcherCoordinate);
				game.getFieldModel().setBombMoving(true);
			} else {
				game.getFieldModel().setBallCoordinate(catcherCoordinate);
				game.getFieldModel().setBallMoving(true);
				if (getGameState().getPassState() != null) {
					getGameState().getPassState().setUsingBlastIt(false);
				}
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

		List<FieldCoordinate> scatterCoordinates = new ArrayList<>();
		List<Integer> rolls = new ArrayList<>();
		List<Direction> directions = new ArrayList<>();

		boolean inBounds = true;

		FieldCoordinate lastValidCoordinate = game.getFieldModel().getBallCoordinate();

		while (inBounds && scatterCoordinates.size() < 3) {
			int roll = getGameState().getDiceRoller().rollScatterDirection();
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
			FieldCoordinate ballCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(lastValidCoordinate,
				direction, 1);
			if (fScatterBounds.isInBounds(ballCoordinateEnd)) {
				lastValidCoordinate = ballCoordinateEnd;
				scatterCoordinates.add(lastValidCoordinate);
				rolls.add(roll);
				directions.add(direction);
			} else {
				inBounds = false;
			}
		}

		getResult().addReport(new ReportScatterBall(directions.toArray(new Direction[0]), rolls.stream().mapToInt(i -> i).toArray(), false));

		game.getFieldModel().setBallCoordinate(lastValidCoordinate);
		game.getFieldModel().setBallMoving(true);

		if (inBounds) {
			Player<?> player = game.getFieldModel().getPlayer(lastValidCoordinate);
			if (player != null) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				if (playerState.hasTacklezones()) {
					fCatcherId = player.getId();
					return CatchScatterThrowInMode.CATCH_SCATTER;
				} else {
					return CatchScatterThrowInMode.FAILED_CATCH;
				}
			}
			return CatchScatterThrowInMode.SCATTER_BALL;
		} else {
			fThrowInCoordinate = lastValidCoordinate;
			return CatchScatterThrowInMode.THROW_IN;
		}
	}

	private CatchScatterThrowInMode bounceBall() {

		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "bounceBall()");

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
		getResult().addReport(new ReportScatterBall(new Direction[]{direction}, new int[]{roll}, false));
		getResult().setSound(SoundId.BOUNCE);

		game.getFieldModel().setBallCoordinate(ballCoordinateEnd);
		game.getFieldModel().setBallMoving(true);

		if (getGameState().getPassState() != null) {
			getGameState().getPassState().setUsingBlastIt(false);
		}

		if (fScatterBounds.isInBounds(ballCoordinateEnd)) {
			Player<?> player = game.getFieldModel().getPlayer(ballCoordinateEnd);
			if (player != null) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				if (playerState.hasTacklezones()) {
					fCatcherId = player.getId();
					return CatchScatterThrowInMode.CATCH_SCATTER;
				} else {
					return CatchScatterThrowInMode.FAILED_CATCH;
				}
			}
		} else {
			game.getFieldModel().setOutOfBounds(true);
			if (fScatterBounds.equals(FieldCoordinateBounds.FIELD)) {
				fThrowInCoordinate = lastValidCoordinate;
				return CatchScatterThrowInMode.THROW_IN;
			} else {
				publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, true));
			}
		}

		return null;

	}

	private CatchScatterThrowInMode scatterBomb() {

		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "scatterBomb()");

		setReRolledAction(null);
		setReRollSource(null);
		state = new StepState();

		List<FieldCoordinate> scatterCoordinates = new ArrayList<>();
		List<Integer> rolls = new ArrayList<>();
		List<Direction> directions = new ArrayList<>();

		boolean inBounds = true;

		FieldCoordinate lastValidCoordinate = game.getFieldModel().getBombCoordinate();

		while (inBounds && scatterCoordinates.size() < 3) {
			int roll = getGameState().getDiceRoller().rollScatterDirection();
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
			FieldCoordinate bombCoordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(lastValidCoordinate,
				direction, 1);
			if (fScatterBounds.isInBounds(bombCoordinateEnd)) {
				lastValidCoordinate = bombCoordinateEnd;
				scatterCoordinates.add(lastValidCoordinate);
				rolls.add(roll);
				directions.add(direction);
			} else {
				inBounds = false;
			}
		}

		getResult().addReport(new ReportScatterBall(directions.toArray(new Direction[0]), rolls.stream().mapToInt(i -> i).toArray(), false));

		game.getFieldModel().setBombCoordinate(lastValidCoordinate);
		game.getFieldModel().setBombMoving(true);

		if (inBounds) {
			Player<?> player = game.getFieldModel().getPlayer(lastValidCoordinate);
			if (player != null) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				if (playerState.hasTacklezones()) {
					fCatcherId = player.getId();
					return CatchScatterThrowInMode.CATCH_BOMB;
				}
			}
		} else {
			game.getFieldModel().setBombCoordinate(null);
			game.getFieldModel().setBombMoving(false);
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
		int distance = distanceRoll[0] + distanceRoll[1] + 1;
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
			game.getFieldModel().setOutOfBounds(false);
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
		IServerJsonOption.BOMB_MODE.addTo(jsonObject, fBombMode);
		IServerJsonOption.STEP_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.TEAM_ID.addTo(jsonObject, divingCatchControlTeam);
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, divingCatchers);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.USING_MODIFYING_SKILL.addTo(jsonObject, usingModifyingSkill);
		IServerJsonOption.EVALUATE.addTo(jsonObject, evaluate);
		IServerJsonOption.REPORT_LIST.addTo(jsonObject, reports.toJsonValue());
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
		fBombMode = IServerJsonOption.BOMB_MODE.getFrom(source, jsonObject);
		phase = DivingCatchPhase.valueOf(IServerJsonOption.STEP_PHASE.getFrom(source, jsonObject));
		divingCatchControlTeam = IServerJsonOption.TEAM_ID.getFrom(source, jsonObject);
		divingCatchers.addAll(Arrays.stream(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)).collect(Collectors.toList()));

		if (IServerJsonOption.ROLL.isDefinedIn(jsonObject)) {
			roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		}

		usingModifyingSkill = IServerJsonOption.USING_MODIFYING_SKILL.getFrom(source, jsonObject);
		evaluate = toPrimitive(IServerJsonOption.EVALUATE.getFrom(source, jsonObject));

		if (IServerJsonOption.REPORT_LIST.isDefinedIn(jsonObject)) {
			reports = new ReportList().initFrom(source, IServerJsonOption.REPORT_LIST.getFrom(source, jsonObject));
		}
		return this;
	}

	private enum DivingCatchPhase {
		ASK_HOME, ASK_AWAY, PROCESS
	}
}
