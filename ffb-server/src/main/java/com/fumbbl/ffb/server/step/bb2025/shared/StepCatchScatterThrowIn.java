package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonArray;
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
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.ThrowInMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
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
import com.fumbbl.ffb.server.step.generator.QuickBite;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.SpikedBallApo;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.ReRollRequest;
import com.fumbbl.ffb.server.util.UtilServerCards;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.CatchModifierSelectionService;
import com.fumbbl.ffb.server.util.bb2025.ReRollModifierChoiceDialogParameterFactory;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
@RulesCollection(RulesCollection.Rules.BB2025)
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
	private DivingCatchPhase phase = DivingCatchPhase.ASK_ACTIVE;
	private List<String> divingCatchers;
	private int roll;
	private boolean reRollUsed, awaitingRescue, modifierChosen;
	private final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	private final CatchModifierSelectionService selectionService = new CatchModifierSelectionService();
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
						case ASK_ACTIVE:
							if (selected.isEmpty()) {
								phase = DivingCatchPhase.ASK_PASSIVE;
							} else {
								fCatcherId = selected.get(0);
								phase = DivingCatchPhase.PROCESS;
							}
							break;
						case ASK_PASSIVE:
							if (!selected.isEmpty()) {
								fCatcherId = selected.get(0);
							}
							phase = DivingCatchPhase.PROCESS;
							break;
						default:
							break;
					}
					if (StringTool.isProvided(fCatcherId)) {
						FieldModel fieldModel = getGameState().getGame().getFieldModel();
						FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(game.getPlayerById(fCatcherId));
						if (fCatchScatterThrowInMode.isBomb()) {
							fieldModel.setBombCoordinate(playerCoordinate);
						} else {
							fieldModel.setBallCoordinate(playerCoordinate);
						}
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
							reports.add(new ReportSkillUse(actingPlayer.getPlayerId(), commandUseSkill.getSkill(),
								commandUseSkill.isSkillUsed(), SkillUse.GRANT_CATCH_BONUS));
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					} else if (commandUseSkill.getSkill().getRerollSource(getReRolledAction()) != null) {
						if (commandUseSkill.isSkillUsed()) {
							setReRollSource(commandUseSkill.getSkill().getRerollSource(getReRolledAction()));
						} else if (passState != null) {
							setReRollSource(null);
							passState.setUsingBlastIt(false);
							reports.add(new ReportSkillUse(fCatcherId, commandUseSkill.getSkill(), commandUseSkill.isSkillUsed(),
								SkillUse.RE_ROLL_CATCH));
							reports.add(new ReportSkillUse(actingPlayer.getPlayerId(),
								UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.grantsCatchBonusToReceiver),
								commandUseSkill.isSkillUsed(), SkillUse.GRANT_CATCH_BONUS));
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_RE_ROLL_MODIFIER_CHOICE:
					ClientCommandReRollModifierChoice modifierChoiceCommand =
						(ClientCommandReRollModifierChoice) pReceivedCommand.getCommand();
					if (handleModifierChoice(modifierChoiceCommand)) {
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

	private boolean handleModifierChoice(ClientCommandReRollModifierChoice command) {
		Game game = getGameState().getGame();
		Player<?> catcher = game.getPlayerById(fCatcherId);
		if (!awaitingRescue || catcher == null || command.getReRolledAction() != ReRolledActions.CATCH
			|| !catcher.getId().equals(command.getPlayerId())) {
			return false;
		}
		PassState passState = getGameState().getPassState();
		Set<Skill> skills = new LinkedHashSet<>(command.getSkills());
		if (selectionService.findOptions(game, catcher, fCatchScatterThrowInMode,
			passState != null ? passState.getUsingBlastIt() : null, roll).stream()
			.noneMatch(option -> new LinkedHashSet<>(option.getSkills()).equals(skills))) {
			return false;
		}
		for (Skill skill : skills) {
			selectedModifierSkills.add(skill);
			if (catcher == game.getActingPlayer().getPlayer()) {
				game.getActingPlayer().markSkillUsed(skill);
			} else {
				catcher.markUsed(skill, game);
			}
			reports.add(new ReportSkillUse(catcher.getId(), skill, true, SkillUse.ADD_AGILITY_MODIFIER));
		}
		awaitingRescue = false;
		modifierChosen = true;
		return true;
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
		FieldModel fieldModel = game.getFieldModel();

		switch (fCatchScatterThrowInMode) {
			case CATCH_BOMB:
			case CATCH_ACCURATE_BOMB_EMPTY_SQUARE:
			case CATCH_ACCURATE_BOMB:
				if (askForDivingCatch(fieldModel, game)) {
					return;
				}
				fBombMode = true;
				if (!StringTool.isProvided(fCatcherId)) {
					Player<?> directCatcher = directCatcher(fieldModel);
					fCatcherId = (directCatcher != null) ? directCatcher.getId() : null;
				}
				if (StringTool.isProvided(fCatcherId)) {
					PlayerState catcherState = fieldModel.getPlayerState(game.getPlayerById(fCatcherId));
					if ((catcherState != null) && catcherState.hasTacklezones() && fieldModel.isBombMoving()) {
						fCatchScatterThrowInMode = catchBall();
					} else {
						fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
					}
				} else {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
				}
				if ((fCatchScatterThrowInMode == CatchScatterThrowInMode.FAILED_CATCH)
					|| (fCatchScatterThrowInMode == CatchScatterThrowInMode.SCATTER_BALL)) {
					fieldModel.setBombMoving(true);
					fCatchScatterThrowInMode = null;
				}
				break;
			case CATCH_KICKOFF:
			case CATCH_ACCURATE_PASS:
				if (askForDivingCatch(fieldModel, game)) {
					return;
				}
				// fall through
			case CATCH_HAND_OFF:
			case CATCH_SCATTER:
			case CATCH_PUNT:
				fBombMode = false;
				if (!StringTool.isProvided(fCatcherId)) {
					Player<?> directCatcher = directCatcher(fieldModel);
					fCatcherId = (directCatcher != null) ? directCatcher.getId() : null;
				}
				if (StringTool.isProvided(fCatcherId)) {
					PlayerState catcherState = fieldModel.getPlayerState(game.getPlayerById(fCatcherId));
					if ((catcherState != null) && fieldModel.isBallInPlay() && fieldModel.isBallMoving()) {
						if (catcherState.hasTacklezones()) {
							fCatchScatterThrowInMode = catchBall();
						} else {
							fCatchScatterThrowInMode = CatchScatterThrowInMode.FAILED_CATCH;
						}
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
			case CATCH_THROW_IN:
			case CATCH_ACCURATE_PASS_EMPTY_SQUARE:
			case CATCH_MISSED_PASS:
				if (askForDivingCatch(fieldModel, game)) {
					return;
				}
				fBombMode = false;
				if (directCatcher(fieldModel) != null) {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.CATCH_SCATTER;
				} else {
					fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
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
				Player<?> playerUnderBall = directCatcher(fieldModel);
				if ((playerUnderBall != null) && fieldModel.isBallInPlay()
					&& (UtilGameOption.isOptionEnabled(game, GameOptionId.SPIKED_BALL)
					|| game.isActive(NamedProperties.droppedBallCausesArmourRoll))) {
					InjuryResult injuryResultCatcher = UtilServerInjury.handleInjury(this, new InjuryTypeStab(true), null,
						playerUnderBall, catchCoordinate(fieldModel), null, null, ApothecaryMode.CATCHER);
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
				if (fieldModel.isBallInPlay()) {
					fCatchScatterThrowInMode = bounceBall();
				} else {
					fCatchScatterThrowInMode = null;
				}
				break;
			case THREE_SQUARE_SCATTER:
				fBombMode = false;
				if (fieldModel.isBallInPlay()) {
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
					catcher = !fieldModel.isBombMoving()
						? fieldModel.getPlayer(fieldModel.getBombCoordinate())
						: null;
				} else {
					catcher = fieldModel.getPlayer(fieldModel.getBallCoordinate());
					if (catcher != null) {
						Player<?>[] opponents = UtilPlayer.findAdjacentOpposingPlayersWithProperty(game, catcher,
							fieldModel.getBallCoordinate(), NamedProperties.canAttackOpponentForBallAfterCatch, false, true);
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
					&& !fScatterBounds.isInBounds(fieldModel.getBallCoordinate())) {
					publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, true));
				}

			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private boolean askForDivingCatch(FieldModel fieldModel, Game game) {
		if (divingCatchers == null) {
			divingCatchers = divingCatchers(fieldModel, catchCoordinate(fieldModel));
		}

		Player<?> directCatcher = directCatcher(fieldModel);
		if (divingCatchers.isEmpty()) {
			if (directCatcher != null) {
				fCatcherId = directCatcher.getId();
			}
			return false;
		} else {
			List<Player<?>> potentialCatchers = new ArrayList<>();
			while (phase != DivingCatchPhase.PROCESS) {
				List<String> descriptions = new ArrayList<>();
				Team team;
				boolean stopTimer = false;
				switch (phase) {
					case ASK_ACTIVE:
						team = game.getActingTeam();
						break;
					case ASK_PASSIVE:
						team = game.getOtherTeam(game.getActingTeam());
						stopTimer = true;
						break;
					default:
						return false;
				}

				if (team != null) {
					potentialCatchers.addAll(divingCatchers.stream().map(game::getPlayerById)
						.filter(team::hasPlayer).collect(Collectors.toList()));
					for (Player<?> ignored : potentialCatchers) {
						descriptions.add(" ");
					}
					if (team.hasPlayer(directCatcher)) {
						potentialCatchers.add(0, directCatcher);
						descriptions.add(0, "Will be forced to catch if neither team uses Diving Catch");
					}
					if (!potentialCatchers.isEmpty()) {

						UtilServerDialog.showDialog(getGameState(),
							new DialogPlayerChoiceParameter(team.getId(), PlayerChoiceMode.CATCH,
								potentialCatchers.toArray(new Player[0]),
								descriptions.toArray(new String[0]), 1), stopTimer);
						return true;
					}
				}

				switch (phase) {
					case ASK_ACTIVE:
						phase = DivingCatchPhase.ASK_PASSIVE;
						break;
					case ASK_PASSIVE:
						phase = DivingCatchPhase.PROCESS;
						break;
					default:
						break;
				}
			}
		}
		return false;
	}

	private FieldCoordinate catchCoordinate(FieldModel fieldModel) {
		return fCatchScatterThrowInMode.isBomb() ? fieldModel.getBombCoordinate() : fieldModel.getBallCoordinate();
	}

	private Player<?> directCatcher(FieldModel fieldModel) {
		return fieldModel.getPlayer(catchCoordinate(fieldModel));
	}

	private List<String> divingCatchers(FieldModel fieldModel, FieldCoordinate coordinate) {
		return Arrays.stream(fieldModel.findAdjacentCoordinates(coordinate, fScatterBounds, 1, false))
			.map(fieldModel::getPlayer).filter(Objects::nonNull)
			.filter(player -> player.hasUsableSkillProperty(NamedProperties.canAttemptCatchInAdjacentSquares,
				fieldModel.getPlayerState(player))).map(Player::getId)
			.collect(Collectors.toList());
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

	private CatchScatterThrowInMode catchBall() {

		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "catchBall()");

		Player<?> catcher = game.getPlayerById(fCatcherId);
		state.catcher = catcher;
		if ((state.catcher == null) || state.catcher.hasSkillProperty(NamedProperties.preventCatch)) {
			return CatchScatterThrowInMode.SCATTER_BALL;
		}
		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(state.catcher);

		boolean modifierSelected = modifierChosen;
		modifierChosen = false;
		boolean doRoll = true;
		if (modifierSelected) {
			// the coach rescued the die with an optional modifier, it is evaluated again instead of being re-rolled
			doRoll = false;
		} else if (ReRolledActions.CATCH == getReRolledAction()) {
			if (reRollUsed || (getReRollSource() == null)
				|| !UtilServerReRoll.useReRoll(this, getReRollSource(), state.catcher)) {
				doRoll = false;
			} else {
				reRollUsed = true;
			}
		}

		if (doRoll || evaluate || modifierSelected) {
			awaitingRescue = false;
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			CatchModifierFactory modifierFactory = game.getFactory(Factory.CATCH_MODIFIER);
			PassState passState = getGameState().getPassState();
			Boolean usingBlastIt = passState != null ? passState.getUsingBlastIt() : null;
			Set<CatchModifier> catchModifiers = modifierFactory.findModifiers(
				new CatchContext(game, state.catcher, fCatchScatterThrowInMode, usingBlastIt, selectedModifierSkills));
			int minimumRoll = mechanic.minimumRollCatch(state.catcher, catchModifiers);
			if (doRoll) {
				roll = getGameState().getDiceRoller().rollSkill();
			}
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			getResult().addReport(
				new ReportCatchRoll(state.catcher.getId(), successful, roll, minimumRoll, reRollUsed || evaluate,
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
					|| (fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_ACCURATE_PASS)
					|| (fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_PUNT))
					&& (game.getTurnMode() != TurnMode.DUMP_OFF)
					&& ((game.isHomePlaying() && game.getTeamAway().hasPlayer(state.catcher))
					|| (!game.isHomePlaying() && game.getTeamHome().hasPlayer(state.catcher)))) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}
				return null;

			} else {
				boolean successfulWithBlastIt = false;

				if (getGameState().getPassState() != null &&
					game.getActingPlayer().getPlayerAction() == PlayerAction.HAIL_MARY_PASS) {
					Boolean usingBlastItSkill = getGameState().getPassState().getUsingBlastIt();

					if (
						UtilCards.hasUnusedSkillWithProperty(game.getActingPlayer(), NamedProperties.grantsCatchBonusToReceiver) &&
							usingBlastItSkill == null) {
						Set<CatchModifier> catchModifiersWithBlastIt = modifierFactory.findModifiers(
							new CatchContext(game, state.catcher, fCatchScatterThrowInMode, true, selectedModifierSkills));
						successfulWithBlastIt = DiceInterpreter.getInstance()
							.isSkillRollSuccessful(roll, mechanic.minimumRollCatch(state.catcher, catchModifiersWithBlastIt));
					}
				}
				Optional<Skill> catchSkill = catcher.getSkillsIncludingTemporaryOnes().stream()
					.filter(skill -> skill.getRerollSource(ReRolledActions.CATCH) != null).findFirst();

				boolean firstAttempt = getReRolledAction() != ReRolledActions.CATCH;
				boolean catchReRollAvailable = false;
				boolean skillReRollHandled = false;

				if (firstAttempt) {

					skillReRollHandled = getGameState().executeStepHooks(this, state);
					GameOptionBoolean catchForBombs = (GameOptionBoolean) game.getOptions()
						.getOptionWithDefault(GameOptionId.CATCH_WORKS_FOR_BOMBS);
					catchReRollAvailable =
						state.rerollCatch && (!fCatchScatterThrowInMode.isBomb() || catchForBombs.isEnabled());

					// Blast It! changes the roll that is needed, so it keeps its own prompts
					if (successfulWithBlastIt) {
						if (catchReRollAvailable) {
							UtilServerDialog.showDialog(getGameState(),
								new DialogSkillUseParameter(fCatcherId, catchSkill.orElse(null),
									minimumRoll, game.getThrower().getSkillWithProperty(NamedProperties.grantsCatchBonusToReceiver)),
								false);
							return fCatchScatterThrowInMode;
						}
						if (!skillReRollHandled && UtilServerReRoll.askForReRollIfAvailable(getGameState(), catcher,
							ReRolledActions.CATCH, 0, false,
							game.getThrower().getSkillWithProperty(NamedProperties.grantsCatchBonusToReceiver), null)) {
							setReRolledAction(ReRolledActions.CATCH);
							return fCatchScatterThrowInMode;
						}
					}
				} else if (successfulWithBlastIt) {
					UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getThrowerId(),
						game.getThrower().getSkillWithProperty(NamedProperties.grantsCatchBonusToReceiver),
						0), false);
					return fCatchScatterThrowInMode;
				}

				List<ModifierChoiceOption> options =
					selectionService.findOptions(game, state.catcher, fCatchScatterThrowInMode, usingBlastIt, roll);

				if (catchReRollAvailable && options.isEmpty() && mayUseSkillReRollAutomatically()) {
					return catchBall();
				}

				boolean reRollAllowed = firstAttempt && !reRollUsed && (!skillReRollHandled || catchReRollAvailable);
				if ((!options.isEmpty() || reRollAllowed)
					&& offerRescue(minimumRoll, options, reRollAllowed,
					catchReRollAvailable ? catchSkill.orElse(null) : null)) {
					setReRolledAction(ReRolledActions.CATCH);
					return fCatchScatterThrowInMode;
				}
			}

		}

		awaitingRescue = false;
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

	/**
	 * A free skill re-roll is used without asking, unless the coach may want to fail the catch on purpose.
	 * <p>
	 * On a loose ball the coach may prefer not to hold the ball in that square, so the re-roll is always a choice.
	 * For a deliberate delivery it is only a choice when failing would get a rock thrown at a team mate.
	 */
	private boolean mayUseSkillReRollAutomatically() {
		if (fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_SCATTER) {
			return false;
		}
		Game game = getGameState().getGame();
		return !new StallingExtension().wouldEndOfTurnTriggerStallingRoll(game, game.getActingPlayer().getPlayer());
	}

	private boolean offerRescue(int minimumRoll, List<ModifierChoiceOption> options, boolean reRollAllowed,
	                            Skill reRollSkill) {
		Game game = getGameState().getGame();
		PassState passState = getGameState().getPassState();
		List<ModifierChoiceOption> combinations = selectionService.findCombinations(game, state.catcher,
			fCatchScatterThrowInMode, passState != null ? passState.getUsingBlastIt() : null);
		awaitingRescue = getGameState().getReRollService().askForReRollIfAvailable(
			ReRollRequest.forPlayer(getGameState(), state.catcher, ReRolledActions.CATCH, minimumRoll)
				.reRollSkill(reRollSkill)
				.dialogParameter(new ReRollModifierChoiceDialogParameterFactory(roll, options, combinations, reRollAllowed))
				.build());
		return awaitingRescue;
	}

	/**
	 * Resets everything that belongs to a single catch attempt, the ball is on its way to another square.
	 */
	private void resetCatchAttempt() {
		state = new StepState();
		roll = 0;
		reRollUsed = false;
		awaitingRescue = false;
		modifierChosen = false;
		selectedModifierSkills.clear();
	}

	private CatchScatterThrowInMode scatterBall() {

		Game game = getGameState().getGame();
		getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), "scatterBall()");

		setReRolledAction(null);
		setReRollSource(null);
		resetCatchAttempt();

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

		getResult().addReport(
			new ReportScatterBall(directions.toArray(new Direction[0]), rolls.stream().mapToInt(i -> i).toArray(), false));

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
		resetCatchAttempt();

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
				publishParameter(new StepParameter(StepParameterKey.BALL_OUT_OF_BOUNDS, true));
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
		FieldCoordinate ballCoordinateStart = fThrowInCoordinate;
		fCatcherId = null;

		ThrowInMechanic mechanic = game.getMechanic(Mechanic.Type.THROW_IN);
		boolean cornerThrowIn = mechanic.isCornerThrowIn(ballCoordinateStart);
		int directionRoll = cornerThrowIn ? diceRoller.rollCornerThrowInDirection() : diceRoller.rollThrowInDirection();
		Direction direction = mechanic.interpretThrowInDirectionRoll(ballCoordinateStart, directionRoll);
		int[] distanceRoll = diceRoller.rollThrowInDistance();
		int distance = mechanic.distance(distanceRoll);
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
		if (divingCatchers != null) {
			IServerJsonOption.PLAYER_IDS.addTo(jsonObject, divingCatchers);
		}
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, reRollUsed);
		IServerJsonOption.AWAITING_RESCUE.addTo(jsonObject, awaitingRescue);
		JsonArray selectedSkills = new JsonArray();
		selectedModifierSkills.forEach(skill -> selectedSkills.add(skill.getName()));
		IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, selectedSkills);
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

		if (IServerJsonOption.PLAYER_IDS.isDefinedIn(jsonObject)) {
			divingCatchers = new ArrayList<>();
			divingCatchers.addAll(
				Arrays.stream(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)).collect(Collectors.toList()));
		}

		if (IServerJsonOption.ROLL.isDefinedIn(jsonObject)) {
			roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		}

		reRollUsed = toPrimitive(IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject));
		awaitingRescue = toPrimitive(IServerJsonOption.AWAITING_RESCUE.getFrom(source, jsonObject));
		modifierChosen = false;
		selectedModifierSkills.clear();
		JsonArray selectedSkills = IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(source, jsonObject);
		if (selectedSkills != null) {
			SkillFactory skillFactory = source.getFactory(Factory.SKILL);
			for (JsonValue skill : selectedSkills) {
				selectedModifierSkills.add(skillFactory.forName(skill.asString()));
			}
		}

		usingModifyingSkill = IServerJsonOption.USING_MODIFYING_SKILL.getFrom(source, jsonObject);
		evaluate = toPrimitive(IServerJsonOption.EVALUATE.getFrom(source, jsonObject));

		if (IServerJsonOption.REPORT_LIST.isDefinedIn(jsonObject)) {
			reports = new ReportList().initFrom(source, IServerJsonOption.REPORT_LIST.getFrom(source, jsonObject));
		}
		return this;
	}

	private enum DivingCatchPhase {
		ASK_ACTIVE, ASK_PASSIVE, PROCESS,
		@Deprecated
		ASK_HOME,
		@Deprecated
		ASK_AWAY // legacy, kept around to maintain compatibility with older replays
	}
}
