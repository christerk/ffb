package com.balancedbytes.games.ffb.server.step.action.move;

import java.util.Set;

import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.DodgeModifiers;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.factory.DodgeModifierFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeDropDodge;
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
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to handle skill DODGE.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 *
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter COORDINATE_TO to be set by a preceding step. Expects
 * stepParameter USING_BREAK_TACKLE to be set by a preceding step. Expects
 * stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 *
 * StepParameter RE_ROLL_USED may be set by a preceding step. StepParameter
 * DODGE_ROLL may be set by a preceding step.
 *
 * Sets stepParameter RE_ROLL_USED for all steps on the stack. Sets
 * stepParameter DODGE_ROLL for all steps on the stack. Sets stepParameter
 * INJURY_TYPE for all steps on the stack. Sets stepParameter USING_BREAK_TACKLE
 * for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepMoveDodge extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private int fDodgeRoll;
	private Boolean fUsingDivingTackle;
	private boolean fUsingBreakTackle;
	private boolean fReRollUsed;

	public StepMoveDodge(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MOVE_DODGE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					fGotoLabelOnFailure = (String) parameter.getValue();
				}
			}
		}
		if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case COORDINATE_FROM:
				fCoordinateFrom = (FieldCoordinate) parameter.getValue();
				return true;
			case COORDINATE_TO:
				fCoordinateTo = (FieldCoordinate) parameter.getValue();
				return true;
			case DODGE_ROLL:
				fDodgeRoll = (Integer) parameter.getValue();
				return true;
			case USING_BREAK_TACKLE:
				fUsingBreakTackle = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				return true;
			case USING_DIVING_TACKLE:
				fUsingDivingTackle = (Boolean) parameter.getValue();
				return true;
			case RE_ROLL_USED:
				fReRollUsed = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				return true;
			default:
				break;
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.isDodging()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		if (ReRolledActions.DODGE == getReRolledAction()) {
			if ((getReRollSource() == null)
					|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
				failDodge();
				return;
			}
		}
		boolean reRolledAction = (getReRolledAction() == ReRolledActions.DODGE) && (getReRollSource() != null);
		boolean doRoll = reRolledAction || (fUsingDivingTackle == null);
		switch (dodge(doRoll)) {
		case SUCCESS:
			reRolledAction = (getReRolledAction() == ReRolledActions.DODGE) && (getReRollSource() != null);
			publishParameter(new StepParameter(StepParameterKey.RE_ROLL_USED, fReRollUsed || reRolledAction));
			getResult().setNextAction(StepAction.NEXT_STEP);
			break;
		case FAILURE:
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.STAND_FIRM_NO_DROP_ON_FAILED_DODGE)) {
				publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				failDodge();
			}
			break;
		default:
			break;
		}
	}

	private void failDodge() {
		publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropDodge()));
		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
	}

	private ActionStatus dodge(boolean pDoRoll) {

		ActionStatus status;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (pDoRoll) {
			publishParameter(new StepParameter(StepParameterKey.DODGE_ROLL, getGameState().getDiceRoller().rollSkill()));
		}
		DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
		Set<DodgeModifier> dodgeModifiers = modifierFactory.findDodgeModifiers(game, fCoordinateFrom, fCoordinateTo, 0);
		if (fUsingBreakTackle) {
			dodgeModifiers.add(DodgeModifiers.BREAK_TACKLE);
		}
		if ((fUsingDivingTackle != null) && fUsingDivingTackle) {
			dodgeModifiers.add(DodgeModifiers.DIVING_TACKLE);
		}

		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());

		int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll);

		if (successful) {
			if (dodgeModifiers.remove(DodgeModifiers.BREAK_TACKLE)) {
				int minimumRollWithoutBreakTackle = mechanic.minimumRollDodge(game,
						actingPlayer.getPlayer(), dodgeModifiers);
				if (!DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithoutBreakTackle)) {
					dodgeModifiers.add(DodgeModifiers.BREAK_TACKLE);
				} else {
					minimumRoll = minimumRollWithoutBreakTackle;
				}
			}
		} else {
			if (pDoRoll && dodgeModifiers.remove(DodgeModifiers.BREAK_TACKLE)) {
				minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
				if (!fUsingBreakTackle) {
					getResult().addReport(new ReportSkillUse(null, SkillConstants.BREAK_TACKLE, false, SkillUse.WOULD_NOT_HELP));
				}
			}
		}

		DodgeModifier[] dodgeModifierArray = modifierFactory.toArray(dodgeModifiers);
		boolean reRolled = ((getReRolledAction() == ReRolledActions.DODGE) && (getReRollSource() != null));
		getResult().addReport(new ReportSkillRoll(ReportId.DODGE_ROLL, actingPlayer.getPlayerId(), successful,
				(pDoRoll ? fDodgeRoll : 0), minimumRoll, reRolled, dodgeModifierArray));

		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (!fReRollUsed && (getReRolledAction() != ReRolledActions.DODGE)) {
				setReRolledAction(ReRolledActions.DODGE);
				ReRollSource skillRerollSource = UtilCards.getUnusedRerollSource(game.getActingPlayer(), ReRolledActions.DODGE);
				if (skillRerollSource != null) {
					Team otherTeam = UtilPlayer.findOtherTeam(game, actingPlayer.getPlayer());
					Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, fCoordinateFrom, false);
					for (Player<?> opponent : opponents) {
						if (UtilCards.cancelsSkill(opponent, skillRerollSource.getSkill(game))) {
							skillRerollSource = null;
							break;
						}
					}
				}
				if (skillRerollSource != null) {
					setReRollSource(skillRerollSource);
					UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
					status = dodge(true);
				} else {
					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledActions.DODGE,
							minimumRoll, false)) {
						status = ActionStatus.WAITING_FOR_RE_ROLL;
					}
				}
			}
		}

		if (dodgeModifiers.contains(DodgeModifiers.BREAK_TACKLE) && ((status == ActionStatus.SUCCESS))) {
			fUsingBreakTackle = true;
			actingPlayer.markSkillUsed(SkillConstants.BREAK_TACKLE);
			publishParameter(new StepParameter(StepParameterKey.USING_BREAK_TACKLE, fUsingBreakTackle));
		}

		return status;

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		IServerJsonOption.DODGE_ROLL.addTo(jsonObject, fDodgeRoll);
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, fUsingDivingTackle);
		IServerJsonOption.USING_BREAK_TACKLE.addTo(jsonObject, fUsingBreakTackle);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, fReRollUsed);
		return jsonObject;
	}

	@Override
	public StepMoveDodge initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(game, jsonObject);
		fDodgeRoll = IServerJsonOption.DODGE_ROLL.getFrom(game, jsonObject);
		fUsingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(game, jsonObject);
		fUsingBreakTackle = IServerJsonOption.USING_BREAK_TACKLE.getFrom(game, jsonObject);
		Boolean reRollUsed = IServerJsonOption.RE_ROLL_USED.getFrom(game, jsonObject);
		fReRollUsed = (reRollUsed != null) ? reRollUsed : false;
		return this;
	}

}
