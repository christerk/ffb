package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.ReportJumpRoll;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropJump;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	private int roll;
	private Boolean usingDivingTackle;
	private boolean alreadyReported;
	private ActionStatus status;


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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.DIVING_TACKLE) {
					usingDivingTackle = StringTool.isProvided(playerChoiceCommand.getPlayerId());
					getGameState().getGame().setDefenderId(playerChoiceCommand.getPlayerId());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
		}
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
					handleFailure(game);
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
						handleFailure(game);
						break;
					default:
						break;
				}
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void handleFailure(Game game) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		actingPlayer.setJumping(false);
		actingPlayer.setHasJumped(true);
		actingPlayer.markSkillUsed(NamedProperties.canLeap);
		publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropJump(game.getDefender())));
		if (roll > 1) {
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, moveStart));
		} else {
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, null));
			game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), moveStart);
		}
		getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
	}

	private ActionStatus leap() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();


		boolean reRolled = ((getReRolledAction() == ReRolledActions.JUMP) && (getReRollSource() != null));
		FieldCoordinate to = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		JumpModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.JUMP_MODIFIER);
		JumpContext context = new JumpContext(game, actingPlayer.getPlayer(), moveStart, to);
		List<JumpModifier> divingTackleModifiers = new ArrayList<>();
		if (usingDivingTackle != null && usingDivingTackle) {
			Optional<Skill> skill = game.getDefender().getSkillsIncludingTemporaryOnes().stream().filter(s -> s.getSkillProperties().contains(NamedProperties.canAttemptToTackleJumpingPlayer))
				.findFirst();
			if (skill.isPresent()) {

				skill.get().getJumpModifiers().forEach(modifier -> {
					context.addModififerValue(modifier.getModifier());
					divingTackleModifiers.add(modifier);
				});
				if (!alreadyReported) {
					publishParameter(new StepParameter(StepParameterKey.USING_DIVING_TACKLE, true));
					alreadyReported = true;
					getResult()
						.addReport(new ReportSkillUse(game.getDefender().getId(), skill.get(), true, SkillUse.STOP_OPPONENT));
				}
			}
		}
		Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(context);
		jumpModifiers.addAll(divingTackleModifiers);
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);
		if (status == null || status == ActionStatus.WAITING_FOR_RE_ROLL) {
			roll = getGameState().getDiceRoller().rollSkill();
		}
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		getResult().addReport(new ReportJumpRoll(actingPlayer.getPlayerId(), successful, roll,
			minimumRoll, reRolled, jumpModifiers.toArray(new JumpModifier[0])));
		if (successful) {
			if (usingDivingTackle == null) {
				status = checkDivingTackle(game, new JumpContext(game, actingPlayer.getPlayer(), moveStart, to), modifierFactory, mechanic);
			} else {
				status = ActionStatus.SUCCESS;
			}
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

	private ActionStatus checkDivingTackle(Game game, JumpContext context, JumpModifierFactory modifierFactory, AgilityMechanic mechanic) {
		Player<?>[] divingTacklers = UtilPlayer.findAdjacentOpposingPlayersWithProperty(game, context.getFrom(),
			NamedProperties.canAttemptToTackleJumpingPlayer, true);
		divingTacklers = UtilPlayer.filterThrower(game, divingTacklers);
		if (game.getTurnMode() == TurnMode.DUMP_OFF) {
			divingTacklers = UtilPlayer.filterAttackerAndDefender(game, divingTacklers);
		}

		if (ArrayTool.isProvided(divingTacklers)) {
			Optional<Skill> skill = divingTacklers[0].getSkillsIncludingTemporaryOnes().stream().filter(s -> s.getSkillProperties().contains(NamedProperties.canAttemptToTackleJumpingPlayer))
				.findFirst();
			if (skill.isPresent()) {
				skill.get().getJumpModifiers().forEach(modifier -> context.addModififerValue(modifier.getModifier()));
				Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(context);
				jumpModifiers.addAll(skill.get().getJumpModifiers());
				int minimumRoll = mechanic.minimumRollJump(context.getPlayer(), jumpModifiers);

				if (DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll)) {
					getResult().addReport(new ReportSkillUse(null, skill.get(), false, SkillUse.WOULD_NOT_HELP));
				} else {

					String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
					UtilServerDialog.showDialog(getGameState(),
						new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.DIVING_TACKLE, divingTacklers, null, 1),
						true);
					usingDivingTackle = null;

					return ActionStatus.WAITING_FOR_SKILL_USE;
				}
			}
		}


		return ActionStatus.SUCCESS;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.MOVE_START.addTo(jsonObject, moveStart);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, usingDivingTackle);
		IServerJsonOption.ALREADY_REPORTED.addTo(jsonObject, alreadyReported);
		if (status != null) {
			IServerJsonOption.STATUS.addTo(jsonObject, status.name());
		}
		return jsonObject;
	}

	@Override
	public StepJump initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		moveStart = IServerJsonOption.MOVE_START.getFrom(game, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(game, jsonObject);
		usingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(game, jsonObject);
		alreadyReported = IServerJsonOption.ALREADY_REPORTED.getFrom(game, jsonObject);
		String statusString = IServerJsonOption.STATUS.getFrom(game, jsonObject);
		if (StringTool.isProvided(statusString)) {
			status = ActionStatus.valueOf(statusString);
		}
		return this;
	}

}
