package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.JumpModifierFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.JumpMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.JumpContext;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeDropJump;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Set;

/**
 * Step in move sequence to handle jumps.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter INJURY_TYPE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepJump extends AbstractStepWithReRoll {

	public class StepState {
		public String goToLabelOnFailure;
	}

	private StepState state;

	public StepJump(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.JUMP;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					state.goToLabelOnFailure = (String) parameter.getValue();
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		boolean doLeap = (actingPlayer.isJumping() && mechanic.canStillJump(game, actingPlayer));
		if (doLeap) {
			if (ReRolledActions.JUMP == getReRolledAction()) {
				if ((getReRollSource() == null)
					|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropJump()));
					getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
					doLeap = false;
				}
			}
			if (doLeap) {
				switch (leap()) {
					case SUCCESS:
						actingPlayer.setJumping(false);
						actingPlayer.setHasJumped(true);
						actingPlayer.markSkillUsed(NamedProperties.canLeap);
						getResult().setNextAction(StepAction.NEXT_STEP);
						break;
					case FAILURE:
						actingPlayer.setJumping(false);
						actingPlayer.setHasJumped(true);
						actingPlayer.markSkillUsed(NamedProperties.canLeap);
						publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropJump()));
						getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
						break;
					default:
						break;
				}
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}


	private ActionStatus leap() {
		ActionStatus status = null;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		JumpModifierFactory modifierFactory = new JumpModifierFactory();
		Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(new JumpContext(game, actingPlayer.getPlayer()));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);
		int roll = getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.JUMP) && (getReRollSource() != null));
		getResult().addReport(new ReportSkillRoll(ReportId.JUMP_ROLL, actingPlayer.getPlayerId(), successful, roll,
			minimumRoll, reRolled, jumpModifiers.toArray(new JumpModifier[0])));
		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (getReRolledAction() != ReRolledActions.JUMP) {
				setReRolledAction(ReRolledActions.JUMP);
				if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
					ReRolledActions.JUMP, minimumRoll, false)) {
					status = ActionStatus.WAITING_FOR_RE_ROLL;
				}
			}
		}
		return status;
	}
	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		return jsonObject;
	}

	@Override
	public StepJump initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		return this;
	}

}
