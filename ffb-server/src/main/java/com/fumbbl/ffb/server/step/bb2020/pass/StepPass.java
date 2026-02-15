package com.fumbbl.ffb.server.step.bb2020.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.mixed.ReportModifiedPassResult;
import com.fumbbl.ffb.report.mixed.ReportPassRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;
import java.util.Set;

import static com.fumbbl.ffb.server.step.StepParameter.from;

/**
 * Step in the pass sequence to handle passing the ball.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_MISSED_PASS.
 * <p>
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * PASS_ACCURATE for all steps on the stack. Sets stepParameter PASS_FUMBLE for
 * all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPass extends AbstractStepWithReRoll {

	public StepPass(GameState pGameState) {
		super(pGameState);
		pGameState.setPassState(new PassState());
	}

	public StepId getId() {
		return StepId.PASS;
	}

	private String goToLabelOnEnd, goToLabelOnSavedFumble, goToLabelOnMissedPass;
	private Boolean usingModifyingSkill;
	private int roll, minimumRoll;

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						goToLabelOnEnd = (String) parameter.getValue();
						break;
					// mandatory
					case GOTO_LABEL_ON_MISSED_PASS:
						goToLabelOnMissedPass = (String) parameter.getValue();
						break;
					case GOTO_LABEL_ON_SAVED_FUMBLE:
						goToLabelOnSavedFumble = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(goToLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(goToLabelOnMissedPass)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_MISSED_PASS + " is not initialized.");
		}
		if (!StringTool.isProvided(goToLabelOnSavedFumble)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SAVED_FUMBLE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.CATCHER_ID) {
				getGameState().getPassState().setCatcherId((String) parameter.getValue());
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canAddStrengthToPass)) {
				usingModifyingSkill = commandUseSkill.isSkillUsed();
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			} else {
				commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), getGameState().getPassState());
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
			return;
		}
		if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
			game.getFieldModel().setBombMoving(true);
			if (!StringTool.isProvided(state.getOriginalBombardier())) {
				state.setOriginalBombardier(game.getThrowerId());
			}
		} else {
			game.getFieldModel().setBallMoving(true);
		}
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());

		PassModifierFactory factory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
		PassMechanic mechanic = (PassMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(),
			false);

		Set<PassModifier> passModifiers = factory.findModifiers(new PassContext(game, game.getThrower(),
			passingDistance, false));

		boolean isBomb = PlayerAction.THROW_BOMB == game.getThrowerAction() || PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction();

		if (ReRolledActions.PASS == getReRolledAction()) {
			if (usingModifyingSkill == null || !usingModifyingSkill) {
				if (getReRollSource() == null) {
					handleFailedPass(throwerCoordinate);
					return;
				} else if (!UtilServerReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
					if (usingModifyingSkill != null || !showUseModifyingSkillDialog(mechanic, passingDistance, passModifiers, isBomb)) {
						handleFailedPass(throwerCoordinate);
					}
					return;
				}
			}
		}

		StatBasedRollModifier statBasedRollModifier = null;

		if (usingModifyingSkill != null && usingModifyingSkill) {
			if (minimumRoll == 0) {
				Optional<Integer> minimumRollO = mechanic.minimumRoll(game.getThrower(), passingDistance, passModifiers);
				minimumRoll = minimumRollO.orElse(0);
			}
			if (roll == 0) {
				roll = minimumRoll > 0 ? getGameState().getDiceRoller().rollSkill() : 0;
			}
			Skill modifyingSkill = game.getActingPlayer().getPlayer().getSkillWithProperty(NamedProperties.canAddStrengthToPass);
			getResult().addReport(new ReportSkillUse(game.getThrowerId(), modifyingSkill, true, SkillUse.ADD_STRENGTH_TO_ROLL));
			statBasedRollModifier = game.getActingPlayer().statBasedModifier(NamedProperties.canAddStrengthToPass);
			state.setResult(mechanic.evaluatePass(game.getThrower(), roll, passingDistance, passModifiers, isBomb, statBasedRollModifier));
			game.getActingPlayer().markSkillUsed(modifyingSkill);
		} else {
			state.setThrowerCoordinate(throwerCoordinate);
			publishParameter(from(StepParameterKey.PASSING_DISTANCE, passingDistance));
			Optional<Integer> minimumRollO = mechanic.minimumRoll(game.getThrower(), passingDistance, passModifiers);
			minimumRoll = minimumRollO.orElse(0);
			roll = minimumRollO.isPresent() ? getGameState().getDiceRoller().rollSkill() : 0;
			state.setResult(mechanic.evaluatePass(game.getThrower(), roll, passingDistance, passModifiers, isBomb));
		}

		if (PassResult.FUMBLE == state.getResult()) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, false));
		} else if (PassResult.SAVED_FUMBLE == state.getResult()) {
			publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, true));
		}
		boolean reRolled = ((getReRolledAction() == ReRolledActions.PASS) && (getReRollSource() != null));
		getResult().addReport(new ReportPassRoll(game.getThrowerId(), roll, minimumRoll, reRolled,
			passModifiers.toArray(new PassModifier[0]), passingDistance, isBomb, state.getResult(), false, statBasedRollModifier));
		if (PassResult.ACCURATE == state.getResult()) {
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
			} else {
				game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
			}
		} else {
			boolean doNextStep = true;
			if (mechanic.eligibleToReRoll(getReRolledAction(), game.getThrower())) {
				setReRolledAction(ReRolledActions.PASS);
				Skill modificationSkill = getModifyingSkill(mechanic, passingDistance, passModifiers, isBomb);

				ReRollSource passingReroll = UtilCards.getRerollSource(game.getThrower(), ReRolledActions.PASS);
				if (passingReroll != null && !state.isPassSkillUsed()) {
					doNextStep = false;
					state.setPassSkillUsed(true);
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					UtilServerDialog.showDialog(getGameState(),
						new DialogSkillUseParameter(game.getThrowerId(), passingReroll.getSkill(game), minimumRoll, modificationSkill),
						actingTeam.hasPlayer(game.getThrower()));
				} else {
					if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledActions.PASS,
						minimumRoll, PassResult.FUMBLE == state.getResult(), modificationSkill, null)) {
						doNextStep = false;
					}
				}
			} else if (usingModifyingSkill == null && showUseModifyingSkillDialog(mechanic, passingDistance, passModifiers, isBomb)) {
				doNextStep = false;
			}
			if (doNextStep) {
				handleFailedPass(throwerCoordinate);
			}
		}
	}

	private boolean showUseModifyingSkillDialog(PassMechanic mechanic, PassingDistance passingDistance, Set<PassModifier> passModifiers, boolean isBomb) {
		if (usingModifyingSkill == null) {
			Skill modifyingSkill = getModifyingSkill(mechanic, passingDistance, passModifiers, isBomb);
			if (modifyingSkill != null) {
				UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(getGameState().getGame().getThrowerId(), modifyingSkill, 0), false);
				return true;
			}
		}
		return false;
	}

	private Skill getModifyingSkill(PassMechanic mechanic, PassingDistance passingDistance, Set<PassModifier> passModifiers, boolean isBomb) {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill modifying = null;
		if (game.getThrowerId().equals(actingPlayer.getPlayerId())) {
			PassResult modifiedResult = mechanic.evaluatePass(game.getThrower(), roll, passingDistance, passModifiers, isBomb, actingPlayer.statBasedModifier(NamedProperties.canAddStrengthToPass));
			if (state.getResult() != modifiedResult) {
				modifying = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canAddStrengthToPass);
				getResult().addReport(new ReportModifiedPassResult(modifying, modifiedResult));
			}
		}
		return modifying;
	}

	private void handleFailedPass(FieldCoordinate throwerCoordinate) {
		Game game = getGameState().getGame();
		PassState state = getGameState().getPassState();
		publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, PassResult.FUMBLE == state.getResult()));
		if (PassResult.SAVED_FUMBLE == state.getResult()) {
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(null);
				game.getFieldModel().setBombMoving(false);
				publishParameter(from(StepParameterKey.CATCHER_ID, null));
				publishParameter(from(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, null));
			} else {
				game.getFieldModel().setBallCoordinate(throwerCoordinate);
				game.getFieldModel().setBallMoving(false);
			}
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnSavedFumble);
		} else if (PassResult.FUMBLE == state.getResult()) {
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(throwerCoordinate);
			} else {
				game.getFieldModel().setBallCoordinate(throwerCoordinate);
				publishParameter(
					new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			}
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
				game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
			} else {
				game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
			}
			publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnMissedPass);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.addTo(jsonObject, goToLabelOnMissedPass);
		IServerJsonOption.GOTO_LABEL_ON_SAVED_FUMBLE.addTo(jsonObject, goToLabelOnSavedFumble);
		IServerJsonOption.USING_MODIFYING_SKILL.addTo(jsonObject, usingModifyingSkill);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.MINIMUM_ROLL.addTo(jsonObject, minimumRoll);
		return jsonObject;
	}

	@Override
	public StepPass initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		goToLabelOnMissedPass = IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.getFrom(source, jsonObject);
		goToLabelOnSavedFumble = IServerJsonOption.GOTO_LABEL_ON_SAVED_FUMBLE.getFrom(source, jsonObject);
		usingModifyingSkill = IServerJsonOption.USING_MODIFYING_SKILL.getFrom(source, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		minimumRoll = IServerJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		return this;
	}
}
