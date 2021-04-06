package com.balancedbytes.games.ffb.server.step.bb2020.multiblock;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.dialog.DialogReRollForTargetsParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.balancedbytes.games.ffb.report.ReportFoulAppearanceRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepFoulAppearanceMultiple extends AbstractStep {

	public static class StepState {
		public String goToLabelOnFailure;
		public List<BlockTarget> blockTargets = new ArrayList<>();
		public boolean firstRun = true, teamReRollAvailable, proReRollAvailable;
		public ReRollSource reRollSource;
		public String reRollTarget;
	}

	private final StepState state;

	public StepFoulAppearanceMultiple(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.FOUL_APPEARANCE_MULTIPLE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						state.goToLabelOnFailure = (String) parameter.getValue();
						break;
					case BLOCK_TARGETS:
						//noinspection unchecked
						state.blockTargets.addAll((List<BlockTarget>) parameter.getValue());
						break;
					default:
						break;
				}
			}
		}
		if (state.goToLabelOnFailure == null) {
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_RE_ROLL_FOR_TARGET) {
				ClientCommandUseReRollForTarget command = (ClientCommandUseReRollForTarget) pReceivedCommand.getCommand();
				if (command.getReRolledAction() == ReRolledActions.FOUL_APPEARANCE) {
					state.reRollSource = command.getReRollSource();
					state.reRollTarget = command.getTargetId();
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

		if (state.firstRun) {
			state.firstRun = false;
			for (BlockTarget target : state.blockTargets) {
				Player<?> player = game.getPlayerById(target.getPlayerId());
				if (UtilCards.hasSkillWithProperty(player, NamedProperties.forceRollBeforeBeingBlocked)
					&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
					roll(actingPlayer, state.blockTargets, target.getPlayerId(), false);
				}
			}
			decideNextStep(game, state);

		} else {
			if (!StringTool.isProvided(state.reRollTarget) || state.reRollSource == null || !UtilServerReRoll.useReRoll(this, state.reRollSource, actingPlayer.getPlayer())) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				roll(actingPlayer, state.blockTargets, state.reRollTarget, true);
				decideNextStep(game, state);
			}
		}
	}

	private void decideNextStep(Game game, StepState state) {
		if (state.blockTargets.isEmpty()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			state.teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(getGameState(), game.getActingPlayer().getPlayer());
			state.proReRollAvailable = UtilServerReRoll.isProReRollAvailable(game.getActingPlayer().getPlayer(), game);
			if (!state.teamReRollAvailable && !state.proReRollAvailable) {
				if (state.blockTargets.size() == 1) {
					publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_TO_REMOVE, state.blockTargets.get(0)));
				} else {
					getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				}
			} else {
				UtilServerDialog.showDialog(getGameState(), createDialogParameter(game.getActingPlayer().getPlayer(), state), false);
			}
		}
	}

	private void roll(ActingPlayer actingPlayer, List<BlockTarget> targets, String currentTargetId, boolean reRolling) {
		int foulAppearanceRoll = getGameState().getDiceRoller().rollSkill();
		int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
		boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
		getResult().addReport(new ReportFoulAppearanceRoll(actingPlayer.getPlayerId(),
			mayBlock, foulAppearanceRoll, minimumRoll, reRolling, null));
		if (mayBlock) {
			targets.stream().filter(target -> target.getPlayerId().equals(currentTargetId))
				.findFirst().ifPresent(targets::remove);
		} else if (!reRolling) {
			getResult().setSound(SoundId.EW);
		}
	}

	private DialogReRollForTargetsParameter createDialogParameter(Player<?> player, StepState state) {
		return new DialogReRollForTargetsParameter(player.getId(), state.blockTargets.stream().map(BlockTarget::getPlayerId).collect(Collectors.toList()),
			ReRolledActions.FOUL_APPEARANCE, state.blockTargets.stream().map(t -> 2).collect(Collectors.toList()),
			state.teamReRollAvailable, state.proReRollAvailable);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		JsonArray jsonArray = new JsonArray();
		state.blockTargets.stream().map(BlockTarget::toJsonValue).forEach(jsonArray::add);
		IJsonOption.SELECTED_BLOCK_TARGETS.addTo(jsonObject, jsonArray);
		IJsonOption.PLAYER_ID.addTo(jsonObject, state.reRollTarget);
		IJsonOption.FIRST_RUN.addTo(jsonObject, state.firstRun);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, state.proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, state.teamReRollAvailable);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, state.reRollSource);
		return jsonObject;
	}

	@Override
	public StepFoulAppearanceMultiple initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		JsonArray jsonArray = IJsonOption.SELECTED_BLOCK_TARGETS.getFrom(game, jsonObject);
		jsonArray.values().stream()
			.map(value -> new BlockTarget().initFrom(game, value))
			.limit(2)
			.forEach(value -> state.blockTargets.add(value));
		state.reRollTarget = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		state.firstRun = IJsonOption.FIRST_RUN.getFrom(game, jsonObject);
		state.proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		state.teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		state.reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		return this;
	}

}
