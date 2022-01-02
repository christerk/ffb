package com.fumbbl.ffb.server.skillbehaviour.bb2020;

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
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlitzState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.report.bb2020.ReportAnimalSavagery;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBlock;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.StepAnimalSavagery;
import com.fumbbl.ffb.server.step.bb2020.StepAnimalSavagery.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.AnimalSavagery;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(Rules.BB2020)
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
			public boolean handleExecuteStepHook(StepAnimalSavagery step,
					StepState state) {

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
				if (UtilCards.hasSkill(actingPlayer, skill)) {
					boolean doRoll = true;
					ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(game, skill);
					if ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							status = ActionStatus.FAILURE;
						}
					} else {
						doRoll = UtilCards.hasUnusedSkill(actingPlayer, skill);
					}
					if (doRoll) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean goodConditions = ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
								|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
								|| (actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
								|| (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)
								|| (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ));
						int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(goodConditions);
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						actingPlayer.markSkillUsed(skill);
						if (successful) {
							PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer()).recoverTacklezones();
							game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState);

						} else {
							status = ActionStatus.FAILURE;
							if (((reRolledAction == null) || (reRolledAction != step.getReRolledAction()))
									&& UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
											reRolledAction, minimumRoll, false)) {
								status = ActionStatus.WAITING_FOR_RE_ROLL;
							}
						}
						boolean reRolled = ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())
								&& (step.getReRollSource() != null));
						step.getResult().addReport(
								new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, skill));
					}
				}
				if (status == ActionStatus.SUCCESS) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					if (status == ActionStatus.FAILURE) {
						Set<Player<?>> players = Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getActingTeam(), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()))).collect(Collectors.toSet());

						if (StringTool.isProvided(state.thrownPlayerId)) {
							players.add(game.getPlayerById(state.thrownPlayerId));
						}

						if (!players.isEmpty()) {
							if (players.size() == 1) {
								lashOut(game, step, players.stream().findFirst().get(), state);
							} else {
								state.playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());
								UtilServerDialog.showDialog(step.getGameState(),
									new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.ANIMAL_SAVAGERY, state.playerIds.toArray(new String[0]),
										null, 1, 1),
									false);
							}
						} else {

							cancelPlayerAction(step);

							BlitzState blitzState = game.getFieldModel().getBlitzState();
							if (blitzState != null) {
								blitzState.failed();
							}

							step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							step.getResult().addReport(new ReportAnimalSavagery(actingPlayer.getPlayerId()));
						}
					}
				}

				return false;
			}
		});
	}

	private void lashOut(Game game, StepAnimalSavagery step, Player<?> player, StepState state) {
		if (StringTool.isProvided(game.getDefenderId())) {
			step.publishParameter(StepParameter.from(StepParameterKey.GAZE_VICTIM_ID, game.getDefenderId()));
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer()).recoverTacklezones();
		game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState);

		game.setDefenderId(player.getId());
		step.getResult().addReport(new ReportAnimalSavagery(actingPlayer.getPlayerId(), player.getId()));
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		InjuryTypeBlock.Mode mode = actingPlayer.isStandingUp() ? InjuryTypeBlock.Mode.DO_NOT_USE_MODIFIERS : InjuryTypeBlock.Mode.USE_MODIFIERS_AGAINST_TEAM_MATES;
		InjuryResult injuryResult = UtilServerInjury.handleInjury(step, new InjuryTypeBlock(mode, false),
			actingPlayer.getPlayer(), game.getDefender(), playerCoordinate, null, null, ApothecaryMode.ANIMAL_SAVAGERY);
		step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResult));
		step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.ANIMAL_SAVAGERY, true));

		if (player.getId().equals(state.thrownPlayerId)) {
			PlayerAction action = actingPlayer.getPlayerAction();
			if (state.endTurn || (action == PlayerAction.KICK_TEAM_MATE || action == PlayerAction.KICK_TEAM_MATE_MOVE) || ((action == PlayerAction.THROW_TEAM_MATE || action == PlayerAction.THROW_TEAM_MATE_MOVE) && (injuryResult.injuryContext().isKnockedOut() || injuryResult.injuryContext().isCasualty()))) {
				actingPlayer.setStandingUp(false);
				cancelPlayerAction(step);
				step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
				step.publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
				step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
				step.getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}
		}

		step.getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void cancelPlayerAction(StepAnimalSavagery step) {
		Game game = step.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (actingPlayer.getPlayerAction()) {
			case BLITZ:
			case BLITZ_MOVE:
				game.getTurnData().setBlitzUsed(true);
				break;
			case KICK_TEAM_MATE:
			case KICK_TEAM_MATE_MOVE:
				game.getTurnData().setKtmUsed(true);
				break;
			case PASS:
			case PASS_MOVE:
			case THROW_TEAM_MATE:
			case THROW_TEAM_MATE_MOVE:
				game.getTurnData().setPassUsed(true);
				break;
		case HAND_OVER:
		case HAND_OVER_MOVE:
			game.getTurnData().setHandOverUsed(true);
			break;
		case FOUL:
		case FOUL_MOVE:
			game.getTurnData().setFoulUsed(true);
			break;
		default:
			break;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if (actingPlayer.isStandingUp()) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
					playerState.changeBase(PlayerState.PRONE).changeActive(false));
		} else {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
					playerState.changeBase(PlayerState.STANDING).changeActive(false).changeConfused(true));
		}
		game.setPassCoordinate(null);
		step.getResult().setSound(SoundId.ROAR);
	}

}