package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBlock;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBlockProne;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBlockStunned;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropGFI;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeSabotaged;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeSaboteur;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2025.command.DropPlayerCommand;
import com.fumbbl.ffb.server.util.UtilServerInjury;

import java.util.ArrayList;
import java.util.List;

/**
 * Step in block sequence to drop falling players and handle the skill
 * PILING_ON.
 * <p>
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack. Sets
 * stepParameter USING_PILING_ON for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepDropFallingPlayers extends AbstractStep {

	public static class StepState {
		public InjuryResult injuryResultDefender;
		public PlayerState oldDefenderState;
		public boolean saboteurTriggeredAttacker;
		public Boolean usingSaboteurAttacker;
		public boolean saboteurTriggeredDefender;
		public Boolean usingSaboteurDefender;
	}

	private final StepState state;

	public StepDropFallingPlayers(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.DROP_FALLING_PLAYERS;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.OLD_DEFENDER_STATE) {
				state.oldDefenderState = (PlayerState) parameter.getValue();
				return true;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND &&
			pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {

		if (getGameState().executeStepHooks(this, state)) {
			return; // dialog is up, wait for client response
		}
		
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if ((attackerState != null) && (attackerState.getBase() == PlayerState.FALLING) && attackerState.isRooted()) {
			attackerState = attackerState.changeRooted(false);
		}
		if ((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) && defenderState.isRooted()) {
			defenderState = defenderState.changeRooted(false);
		}
		if ((defenderState != null) && (defenderState.getBase() == PlayerState.HIT_ON_GROUND)) {
			defenderState = defenderState.changeBase(PlayerState.FALLING);
		}
		if (((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) && (defenderCoordinate != null))) {

			InjuryTypeServer<?> injuryType = new InjuryTypeBlock(InjuryTypeBlock.Mode.REGULAR, false);

			if (state.oldDefenderState != null) {
				if (state.oldDefenderState.isStunned()) {
					injuryType = new InjuryTypeBlockStunned();
				} else if (state.oldDefenderState.isProneOrStunned()) {
					injuryType = new InjuryTypeBlockProne();
				}
			}
			if (state.saboteurTriggeredDefender) {
				injuryType = new InjuryTypeSaboteur();
			} else if (state.saboteurTriggeredAttacker) {
				injuryType = new InjuryTypeSabotaged();
			}
			if (state.injuryResultDefender == null) {
				state.injuryResultDefender =
					UtilServerInjury.handleInjury(this, injuryType, actingPlayer.getPlayer(), game.getDefender(),
						defenderCoordinate, null, null, ApothecaryMode.DEFENDER);
			}

		}
		// end turn if dropping a player of your own team
		boolean droppedOwnTeam = ((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) &&
			(game.getDefender().getTeam() == actingPlayer.getPlayer().getTeam()) && (state.oldDefenderState != null) &&
			!state.oldDefenderState.isProneOrStunned());

		if (state.injuryResultDefender != null) {
			DropPlayerContext dropPlayerContext =
				new DropPlayerContext(state.injuryResultDefender, droppedOwnTeam, true, null, game.getDefenderId(),
					ApothecaryMode.DEFENDER, false);

			if (state.saboteurTriggeredDefender) {
				// bypass Steady Footing
				publishParameter(StepParameter.from(StepParameterKey.DROP_PLAYER_CONTEXT, dropPlayerContext));
				publishParameter(StepParameter.from(StepParameterKey.INJURY_RESULT, state.injuryResultDefender));
			} else {
				publishParameter(StepParameter.from(StepParameterKey.STEADY_FOOTING_CONTEXT, new SteadyFootingContext(dropPlayerContext)));
			}

		} else if (droppedOwnTeam && !state.saboteurTriggeredDefender && !state.saboteurTriggeredAttacker) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}

		List<DeferredCommand> deferredCommands = new ArrayList<>();

		if ((attackerState != null) && (attackerState.getBase() == PlayerState.FALLING) && (attackerCoordinate != null)) {
			if (!(state.saboteurTriggeredDefender || state.saboteurTriggeredAttacker)) {
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			}
			InjuryResult injuryResultAttacker;
			if (actingPlayer.isFellFromRush()) {
				injuryResultAttacker =
					UtilServerInjury.handleInjury(this, new InjuryTypeDropGFI(), game.getDefender(), actingPlayer.getPlayer(),
						attackerCoordinate, null, null, ApothecaryMode.ATTACKER);
			} else {
				deferredCommands.add(new DropPlayerCommand(actingPlayer.getPlayer().getId(), ApothecaryMode.ATTACKER, true));
				InjuryTypeServer<?> injuryType = new InjuryTypeBlock();
				if (state.saboteurTriggeredAttacker) {
					injuryType = new InjuryTypeSaboteur();
				} else if (state.saboteurTriggeredDefender) {
					injuryType = new InjuryTypeSabotaged();
				}
				injuryResultAttacker = UtilServerInjury.handleInjury(this, injuryType, game.getDefender(),
					actingPlayer.getPlayer(), attackerCoordinate, null, null, ApothecaryMode.ATTACKER);
			}

			if (state.saboteurTriggeredAttacker) {
				DropPlayerContext dropPlayerContext =
					new DropPlayerContext(injuryResultAttacker, false, false, null, actingPlayer.getPlayerId(),
						ApothecaryMode.ATTACKER, false);
				publishParameter(StepParameter.from(StepParameterKey.DROP_PLAYER_CONTEXT,dropPlayerContext));
				publishParameter(StepParameter.from(StepParameterKey.INJURY_RESULT, injuryResultAttacker));
			} else {
				publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
					new SteadyFootingContext(injuryResultAttacker, deferredCommands)));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (state.injuryResultDefender != null) {
			IServerJsonOption.INJURY_RESULT_DEFENDER.addTo(jsonObject, state.injuryResultDefender.toJsonValue());
		}
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
		IServerJsonOption.SABOTEUR_TRIGGERED_DEFENDER.addTo(jsonObject, state.saboteurTriggeredDefender);
		IServerJsonOption.USING_SABOTEUR_DEFENDER.addTo(jsonObject, state.usingSaboteurDefender);
		IServerJsonOption.SABOTEUR_TRIGGERED_ATTACKER.addTo(jsonObject, state.saboteurTriggeredAttacker);
		IServerJsonOption.USING_SABOTEUR_ATTACKER.addTo(jsonObject, state.usingSaboteurAttacker);
		return jsonObject;
	}

	@Override
	public StepDropFallingPlayers initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.injuryResultDefender = null;
		JsonObject injuryResultDefenderObject = IServerJsonOption.INJURY_RESULT_DEFENDER.getFrom(source, jsonObject);
		if (injuryResultDefenderObject != null) {
			state.injuryResultDefender = new InjuryResult().initFrom(source, injuryResultDefenderObject);
		}
		state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		state.saboteurTriggeredDefender = IServerJsonOption.SABOTEUR_TRIGGERED_DEFENDER.getFrom(source, jsonObject);
		state.usingSaboteurDefender = IServerJsonOption.USING_SABOTEUR_DEFENDER.getFrom(source, jsonObject);
		state.saboteurTriggeredAttacker = IServerJsonOption.SABOTEUR_TRIGGERED_ATTACKER.getFrom(source, jsonObject);
		state.usingSaboteurAttacker = IServerJsonOption.USING_SABOTEUR_ATTACKER.getFrom(source, jsonObject);
		return this;
	}

}
