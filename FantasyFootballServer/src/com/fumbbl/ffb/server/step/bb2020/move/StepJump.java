package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.report.ReportJumpRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeDropJump;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

import java.util.Set;

/**
 * Step in move sequence to handle jumps.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter INJURY_TYPE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepJump extends AbstractStepWithReRoll {

	private String goToLabelOnFailure;
	private FieldCoordinate moveStart;

	public StepJump(GameState pGameState) {
		super(pGameState);

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
						goToLabelOnFailure = (String) parameter.getValue();
						break;
					case MOVE_START:
						moveStart = (FieldCoordinate) parameter.getValue();
						break;
					default:
						break;
				}
			}
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.MOVE_START) {
			moveStart = (FieldCoordinate) parameter.getValue();
			return true;
		}
		return false;
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
					publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, moveStart));
					getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
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
						publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, moveStart));
						getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
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

		FieldCoordinate to = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		JumpModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.JUMP_MODIFIER);
		Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(new JumpContext(game, actingPlayer.getPlayer(), moveStart, to));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);
		int roll = getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.JUMP) && (getReRollSource() != null));
		getResult().addReport(new ReportJumpRoll(actingPlayer.getPlayerId(), successful, roll,
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
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.MOVE_START.addTo(jsonObject, moveStart);
		return jsonObject;
	}

	@Override
	public StepJump initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		moveStart = IServerJsonOption.MOVE_START.getFrom(game, jsonObject);
		return this;
	}

}
