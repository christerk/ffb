package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.report.bb2020.ReportAnimalSavagery;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBlock;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.DropPlayerContextBuilder;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.StepAnimalSavagery;
import com.fumbbl.ffb.server.step.bb2020.StepAnimalSavagery.StepState;
import com.fumbbl.ffb.server.step.bb2025.command.AnimalSavageryEndTurnCommand;
import com.fumbbl.ffb.server.step.bb2025.command.StandingUpCommand;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.AnimalSavagery;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(Rules.BB2025)
public class AnimalSavageryBehaviour extends SkillBehaviour<AnimalSavagery> {
	public AnimalSavageryBehaviour() {
		super();

		registerModifier(new StepModifier<StepAnimalSavagery, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepAnimalSavagery step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepAnimalSavagery step, StepState state) {

				ActionStatus status = ActionStatus.SUCCESS;
				Game game = step.getGameState().getGame();

				if (StringTool.isProvided(state.playerId)) {
					lashOut(game, step, game.getPlayerById(state.playerId), state);
					return false;
				}

				if (!game.getTurnMode().checkNegatraits()) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
					return false;
				}
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (UtilCards.hasSkill(actingPlayer, skill) && state.attackOpponent == null) {
					boolean doRoll = true;
					ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(game, skill);
					if ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())) {
						if ((step.getReRollSource() == null) ||
							!UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							status = ActionStatus.FAILURE;
						}
					} else {
						doRoll = UtilCards.hasUnusedSkill(actingPlayer, skill);
					}
					if (doRoll) {
						step.commitTargetSelection();
						int roll = step.getGameState().getDiceRoller().rollSkill();
						PlayerAction playerAction = actingPlayer.getPlayerAction();
						boolean goodConditions =
							((playerAction == PlayerAction.BLITZ_MOVE) || (playerAction != null && playerAction.isKickingDowned()) ||
								(playerAction == PlayerAction.BLITZ) || (playerAction != null && playerAction.isBlockAction()) ||
								(playerAction == PlayerAction.MULTIPLE_BLOCK) || (playerAction == PlayerAction.STAND_UP_BLITZ));
						int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(goodConditions);
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						actingPlayer.markSkillUsed(skill);
						if (!successful) {
							status = ActionStatus.FAILURE;
							if (((reRolledAction == null) || (reRolledAction != step.getReRolledAction())) &&
								UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(), reRolledAction,
									minimumRoll, false)) {
								status = ActionStatus.WAITING_FOR_RE_ROLL;
							}
						}
						boolean reRolled = ((reRolledAction != null) && (reRolledAction == step.getReRolledAction()) &&
							(step.getReRollSource() != null));
						step.getResult().addReport(
							new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, skill));
					}
				} else if (state.attackOpponent != null) {
					status = ActionStatus.FAILURE;
				}

				if (status == ActionStatus.SUCCESS) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					if (status == ActionStatus.FAILURE) {

						Team team = game.getActingTeam();

						Team opponentTeam = game.getOtherTeam(team);
						FieldModel fieldModel = game.getFieldModel();
						FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
						if (state.attackOpponent == null) {
							if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLashOutAgainstOpponents)) {
								Set<Player<?>> adjacentOpponents = adjacentTargets(game, opponentTeam, fieldModel, playerCoordinate);
								if (!adjacentOpponents.isEmpty()) {
									UtilServerDialog.showDialog(step.getGameState(),
										new DialogSkillUseParameter(actingPlayer.getPlayerId(),
											actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canLashOutAgainstOpponents), 0),
										false);
									return false;
								}
							}
							state.attackOpponent = false;
						}

						if (state.attackOpponent) {
							team = opponentTeam;
						}

						Set<Player<?>> players = adjacentTargets(game, team, fieldModel, playerCoordinate);

						if (StringTool.isProvided(state.thrownPlayerId)) {
							players.add(game.getPlayerById(state.thrownPlayerId));
						}

						if (players.isEmpty()) {

							new AnimalSavageryEndTurnCommand(game).execute();

							PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
							if (actingPlayer.isStandingUp()) {
								game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
									playerState.changeBase(PlayerState.PRONE).changeActive(false));
							} else {
								game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
									playerState.changeBase(PlayerState.STANDING).changeActive(false).changeConfused(true));
							}
							step.getResult().setSound(SoundId.ROAR);

							TargetSelectionState targetSelectionState = fieldModel.getTargetSelectionState();
							if (targetSelectionState != null) {
								targetSelectionState.failed();
							}

							step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							step.getResult().addReport(new ReportAnimalSavagery(actingPlayer.getPlayerId()));
						} else {
							if (players.size() == 1) {
								lashOut(game, step, players.stream().findFirst().get(), state);
							} else {
								state.playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());
								UtilServerDialog.showDialog(step.getGameState(),
									new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.ANIMAL_SAVAGERY,
										state.playerIds.toArray(new String[0]), null, 1, 1), false);
							}
						}
					}
				}

				return false;
			}

		});
	}

	private Set<Player<?>> adjacentTargets(Game game, Team team, FieldModel fieldModel,
																				 FieldCoordinate playerCoordinate) {
		Set<Player<?>> adjacentOpponents =
			Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, team, playerCoordinate)).collect(Collectors.toSet());
		FieldCoordinate defenderCoordinate = fieldModel.getPlayerCoordinate(game.getDefender());
		if (team.hasPlayer(game.getDefender()) && playerCoordinate.isAdjacent(defenderCoordinate)) {
			adjacentOpponents.add(game.getDefender());
		}
		return adjacentOpponents;
	}

	private void lashOut(Game game, StepAnimalSavagery step, Player<?> player, StepState state) {
		step.getResult().setNextAction(StepAction.NEXT_STEP);
		if (StringTool.isProvided(game.getDefenderId())) {
			step.publishParameter(StepParameter.from(StepParameterKey.GAZE_VICTIM_ID, game.getDefenderId()));
		}
		ActingPlayer actingPlayer = game.getActingPlayer();

		game.setDefenderId(player.getId());
		step.getResult().addReport(new ReportAnimalSavagery(actingPlayer.getPlayerId(), player.getId()));
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		InjuryTypeBlock.Mode mode =
			(actingPlayer.isStandingUp() || actingPlayer.getPlayer().getTeam() != game.getDefender().getTeam()) ?
				InjuryTypeBlock.Mode.DO_NOT_USE_MODIFIERS : InjuryTypeBlock.Mode.USE_MODIFIERS_AGAINST_TEAM_MATES;
		InjuryResult injuryResult =
			UtilServerInjury.handleInjury(step, new InjuryTypeBlock(mode, false), actingPlayer.getPlayer(),
				game.getDefender(), playerCoordinate, null, null, ApothecaryMode.ANIMAL_SAVAGERY);

		boolean lashedOutAgainstOpponent = actingPlayer.getPlayer().getTeam() != game.getDefender().getTeam();

		boolean endTurn = UtilPlayer.hasBall(game, game.getDefender()) && !lashedOutAgainstOpponent;
		StepParameterKey playerStateKey = null;
		String label = null;
		StepParameterKey[] additionalStateKeys = null;

		List<DeferredCommand> deferredCommands = new ArrayList<>();
		List<StepParameter> endTurnParameters = new ArrayList<>();
		if (endTurn) {
			deferredCommands.add(new StandingUpCommand(game));
			deferredCommands.add(new AnimalSavageryEndTurnCommand(game));
			endTurnParameters.add(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
			endTurnParameters.add(
				new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
		} else {

			TurnData turnData = game.isHomePlaying() ? game.getTurnDataHome() : game.getTurnDataAway();
			boolean hitTargetTeamMate =
				player.getId().equals(state.thrownPlayerId) || player.getId().equals(state.catcherId);
			PlayerAction action =
				fallbackAction(step, actingPlayer.getPlayerAction(), injuryResult.injuryContext(), turnData, game,
					state.blockDefenderId, hitTargetTeamMate);
			if (action != null && action.isBlockAction() && !lashedOutAgainstOpponent) {
				label = state.goToLabelOnFailure;
			} else {
				if (action != null && hitTargetTeamMate) {
					step.publishParameter(StepParameter.from(StepParameterKey.RESET_PLAYER_ACTION, action));
					step.publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
					step.publishParameter(
						new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
				} else if (action == null && player.getId().equals(state.thrownPlayerId)) {
					playerStateKey = StepParameterKey.THROWN_PLAYER_STATE;
					additionalStateKeys = new StepParameterKey[]{StepParameterKey.OLD_DEFENDER_STATE};
				} else if (lashedOutAgainstOpponent && !(actingPlayer.getPlayerAction().isBlockAction() && action == null)) {
					UtilServerPlayerMove.updateMoveSquares(step.getGameState(), false);
					step.publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, null));
					step.publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
				}
			}
		}

		DropPlayerContext dropPlayerContext =
			DropPlayerContextBuilder.builder().injuryResult(injuryResult).endTurn(endTurn).eligibleForSafePairOfHands(true)
				.label(label).playerId(game.getDefenderId()).apothecaryMode(ApothecaryMode.ANIMAL_SAVAGERY)
				.victimStateKey(playerStateKey).additionalVictimStateKeys(additionalStateKeys).stepParameters(endTurnParameters)
				.deferredCommands(deferredCommands).build();
		step.publishParameter(
			new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT, new SteadyFootingContext(dropPlayerContext)));

	}

	private PlayerAction fallbackAction(StepAnimalSavagery step, PlayerAction playerAction, InjuryContext injuryContext,
																			TurnData turnData, Game game, String oldDefenderId, boolean hitTargetTeamMate) {
		boolean playerRemoved = injuryContext.isCasualty() || injuryContext.isKnockedOut();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> defender = game.getDefender();
		switch (playerAction) {
			case KICK_TEAM_MATE:
			case KICK_TEAM_MATE_MOVE:
				if (hitTargetTeamMate) {
					turnData.setKtmUsed(true);
					game.setPassCoordinate(null);
					fieldModel.setRangeRuler(null);
					return PlayerAction.KICK_TEAM_MATE_MOVE;
				}
				return null;
			case HAND_OVER:
			case HAND_OVER_MOVE:
				if (hitTargetTeamMate) {
					return PlayerAction.HAND_OVER_MOVE;
				}
				return null;
			case PASS:
			case PASS_MOVE:
				if (hitTargetTeamMate) {
					game.setPassCoordinate(null);
					game.getActingPlayer().setHasPassed(false);
					fieldModel.setRangeRuler(null);
					return PlayerAction.PASS;
				}
				return null;
			case THROW_TEAM_MATE:
			case THROW_TEAM_MATE_MOVE:
				if (playerRemoved && hitTargetTeamMate) {
					turnData.setPassUsed(true);
					game.setPassCoordinate(null);
					fieldModel.setRangeRuler(null);
					return PlayerAction.THROW_TEAM_MATE_MOVE;
				}
				return null;
			case BLITZ:
			case BLITZ_MOVE:
				fieldModel.setPlayerState(defender, fieldModel.getPlayerState(defender).removeAllTargetSelections());
				return null;
			case BLOCK:
			case VICIOUS_VINES:
				if (game.getDefenderId().equals(oldDefenderId)) {
					return playerAction;
				}
				return null;
			case MULTIPLE_BLOCK:
				step.publishParameter(StepParameter.from(StepParameterKey.PLAYER_ID_TO_REMOVE, defender.getId()));
				fieldModel.setPlayerState(defender, fieldModel.getPlayerState(defender).changeSelectedBlockTarget(false));
				return null;
			default:
				return null;
		}
	}
}