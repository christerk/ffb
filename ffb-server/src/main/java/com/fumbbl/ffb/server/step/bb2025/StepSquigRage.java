package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.report.bb2025.ReportSquigRage;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBlock;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.DropPlayerContextBuilder;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;
import com.fumbbl.ffb.util.bb2025.SquigRageService;

import java.util.Arrays;

/**
 * Rolls for Squig Rage when the player is activated, before an action is declared.
 * <p>
 * Expects stepParameter GOTO_LABEL_ON_FAILURE to be set by a preceding step.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepSquigRage extends AbstractStepWithReRoll {

	private static final int MINIMUM_ROLL = 3;

	private final SquigRageService squigRageService = new SquigRageService();
	private String goToLabelOnFailure;
	private String[] teamMateIds;

	public StepSquigRage(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.SQUIG_RAGE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					goToLabelOnFailure = (String) parameter.getValue();
				}
			});
		}
		if (goToLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND
			&& pReceivedCommand.getCommand() instanceof ClientCommandPlayerChoice
			&& teamMateIds != null
			&& UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
			if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.SQUIG_RAGE) {
				String chosenId = playerChoiceCommand.getPlayerId();
				if (!StringTool.isProvided(chosenId) || Arrays.asList(teamMateIds).contains(chosenId)) {
					teamMateIds = null;
					UtilServerDialog.hideDialog(getGameState());
					if (StringTool.isProvided(chosenId)) {
						knockDownTeamMate(getGameState().getGame().getPlayerById(chosenId));
					} else {
						becomeDistracted();
					}
					return StepCommandStatus.SKIP_STEP;
				}
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		Skill skill = squigRageService.squigRageSkill(player);
		if (skill == null || actingPlayer.getPlayerAction() != PlayerAction.SQUIG_RAGE) {
			return;
		}

		ReRolledAction reRolledAction = ReRolledActions.SQUIG_RAGE;
		boolean reRolling = reRolledAction == getReRolledAction();
		if (reRolling) {
			if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
				rage();
				return;
			}
		} else if (actingPlayer.isSkillUsed(skill)) {
			return;
		}

		int roll = getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, MINIMUM_ROLL);
		actingPlayer.markSkillUsed(skill);
		getResult().addReport(new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, MINIMUM_ROLL,
			reRolling, skill));

		if (successful) {
			return;
		}

		if (!reRolling && UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, reRolledAction,
			MINIMUM_ROLL, false)) {
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		rage();
	}

	private void rage() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();

		if (ArrayTool.isProvided(squigRageService.blockTargets(game, player))) {
			getResult().addReport(new ReportSquigRage(player.getId(), ReportSquigRage.Outcome.MUST_BLOCK, null));
			UtilServerSteps.changePlayerAction(this, player.getId(), PlayerAction.BLOCK, false);
			actingPlayer.setMustCompleteAction(true);
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		Player<?>[] teamMates = squigRageService.teamMateTargets(game, player);
		if (ArrayTool.isProvided(teamMates)) {
			teamMateIds = Arrays.stream(teamMates).map(Player::getId).toArray(String[]::new);
			UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getActingTeam().getId(),
				PlayerChoiceMode.SQUIG_RAGE, teamMateIds, null, 1, 0), false);
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		becomeDistracted();
	}

	private void knockDownTeamMate(Player<?> teamMate) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		getResult().addReport(new ReportSquigRage(actingPlayer.getPlayerId(),
			ReportSquigRage.Outcome.KNOCKS_DOWN_TEAM_MATE, teamMate.getId()));
		FieldCoordinate teamMateCoordinate = game.getFieldModel().getPlayerCoordinate(teamMate);
		InjuryResult injuryResult = UtilServerInjury.handleInjury(this,
			new InjuryTypeBlock(InjuryTypeBlock.Mode.DO_NOT_USE_MODIFIERS, false), actingPlayer.getPlayer(), teamMate,
			teamMateCoordinate, null, null, ApothecaryMode.SQUIG_RAGE);
		DropPlayerContext dropPlayerContext = DropPlayerContextBuilder.builder().injuryResult(injuryResult)
			.endTurn(UtilPlayer.hasBall(game, teamMate)).eligibleForSafePairOfHands(true).playerId(teamMate.getId())
			.apothecaryMode(ApothecaryMode.SQUIG_RAGE).build();
		publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
			new SteadyFootingContext(dropPlayerContext)));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void becomeDistracted() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		getResult().addReport(new ReportSquigRage(player.getId(), ReportSquigRage.Outcome.DISTRACTED, null));
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		game.getFieldModel().setPlayerState(player, playerState.changeConfused(true).changeActive(false));
		getResult().setSound(SoundId.DUH);
		publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
		getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, teamMateIds);
		return jsonObject;
	}

	@Override
	public StepSquigRage initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		teamMateIds = IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject);
		return this;
	}
}
