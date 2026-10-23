package com.fumbbl.ffb.server.step.bb2025.ttm;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.RightStuffModifierFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.mechanics.SppMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.RightStuffContext;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportRightStuffRoll;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeTTMLanding;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeFumbledKtmApoKo;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.ReRollRequest;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.ReRollModifierChoiceDialogParameterFactory;
import com.fumbbl.ffb.server.util.bb2025.RightStuffModifierSelectionService;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Step in ttm sequence to handle skill RIGHT_STUFF (landing roll).
 * <p>
 * Expects stepParameter DROP_THROWN_PLAYER to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepRightStuff extends AbstractStepWithReRoll {

	private Boolean fThrownPlayerHasBall;
	private String fThrownPlayerId;
	private boolean fDropThrownPlayer, kickedPlayer, usingSwoop;
	private PassResult passResult;
	private String goToOnSuccess;
	private PlayerState oldPlayerState;
	private int landingRoll;
	private boolean reRollUsed, awaitingRescue;
	private final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	private final RightStuffModifierSelectionService selectionService = new RightStuffModifierSelectionService();

	public StepRightStuff(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.RIGHT_STUFF;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).forEach(parameter -> {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_SUCCESS:
						goToOnSuccess = (String) parameter.getValue();
						break;
					case IS_KICKED_PLAYER:
						kickedPlayer = parameter.getValue() != null && (boolean) parameter.getValue();
						break;
					default:
						break;
				}
			});
		}
		super.init(parameterSet);
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (Boolean) parameter.getValue();
					return true;
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) parameter.getValue();
					return true;
				case DROP_THROWN_PLAYER:
					fDropThrownPlayer = parameter.getValue() != null && (Boolean) parameter.getValue();
					return true;
				case PASS_RESULT:
					passResult = (PassResult) parameter.getValue();
					return true;
				case OLD_DEFENDER_STATE:
					oldPlayerState = (PlayerState) parameter.getValue();
					return true;
				case USING_SWOOP:
					usingSwoop = parameter.getValue() != null && (Boolean) parameter.getValue();
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

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
				ClientCommandUseSkill useSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				if (useSkill.isSkillUsed() && useSkill.getSkill().hasSkillProperty(NamedProperties.ttmScattersInSingleDirection)) {
					setReRolledAction(ReRolledActions.RIGHT_STUFF);
					setReRollSource(ReRollSources.SWOOP);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			} else if (pReceivedCommand.getId() == NetCommandId.CLIENT_RE_ROLL_MODIFIER_CHOICE
				&& handleModifierChoice((ClientCommandReRollModifierChoice) pReceivedCommand.getCommand())) {
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public void repeat() {
		super.repeat();
		executeStep();
	}

	private boolean handleModifierChoice(ClientCommandReRollModifierChoice command) {
		Game game = getGameState().getGame();
		Player<?> thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if (!awaitingRescue || thrownPlayer == null || command.getReRolledAction() != ReRolledActions.RIGHT_STUFF
			|| !thrownPlayer.getId().equals(command.getPlayerId())) {
			return false;
		}
		Set<Skill> skills = new LinkedHashSet<>(command.getSkills());
		if (selectionService.findOptions(game, thrownPlayer, passResult, landingRoll).stream()
			.noneMatch(option -> new LinkedHashSet<>(option.getSkills()).equals(skills))) {
			return false;
		}
		for (Skill skill : skills) {
			selectedModifierSkills.add(skill);
			if (thrownPlayer == game.getActingPlayer().getPlayer()) {
				game.getActingPlayer().markSkillUsed(skill);
			} else {
				thrownPlayer.markUsed(skill, game);
			}
			getResult().addReport(new ReportSkillUse(thrownPlayer.getId(), skill, true, SkillUse.ADD_AGILITY_MODIFIER));
		}
		awaitingRescue = false;
		return true;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> thrownPlayer = game.getPlayerById(fThrownPlayerId);
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(thrownPlayer);
		// skip right stuff step when player has been thrown out of bounds or fell down a trapdoor
		if ((thrownPlayer != null) && (game.getFieldModel().getPlayerState(thrownPlayer).getBase() == PlayerState.FALLING || playerCoordinate.isBoxCoordinate())) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		game.getFieldModel().setPlayerState(thrownPlayer, oldPlayerState);
		publishParameter(StepParameter.from(StepParameterKey.THROWN_PLAYER_STATE, oldPlayerState));
		if (fThrownPlayerHasBall) {
			game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(thrownPlayer));
		}
		boolean fumbledKtm = PassResult.FUMBLE == passResult && kickedPlayer;
		boolean autoFailLanding = oldPlayerState != null && (oldPlayerState.isProneOrStunned() || oldPlayerState.isDistracted());

		boolean doRoll = !fDropThrownPlayer && !fumbledKtm && !autoFailLanding;
		if (doRoll && awaitingRescue) {
			awaitingRescue = false;
			if (reRollUsed || (getReRollSource() == null)
				|| !UtilServerReRoll.useReRoll(this, getReRollSource(), thrownPlayer)) {
				doRoll = false;
			} else {
				reRollUsed = true;
				landingRoll = 0;
			}
		}
		if (doRoll) {
			RightStuffModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.RIGHT_STUFF_MODIFIER);
			Set<RightStuffModifier> rightStuffModifiers = modifierFactory
				.findModifiers(new RightStuffContext(game, thrownPlayer, passResult, selectedModifierSkills));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			int minimumRoll = mechanic.minimumRollRightStuff(thrownPlayer, rightStuffModifiers);
			boolean rollDice = landingRoll == 0;
			if (rollDice) {
				landingRoll = getGameState().getDiceRoller().rollSkill();
			}
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(landingRoll, minimumRoll);

			if (PassResult.FUMBLE == passResult && game.getThrower() != null && game.getThrower().hasSkillProperty(NamedProperties.fumbledPlayerLandsSafely)) {
				successful = true;
				if (rollDice) {
					getResult().addReport(new ReportSkillUse(game.getThrowerId(),
						game.getThrower().getSkillWithProperty(NamedProperties.fumbledPlayerLandsSafely),
						true, SkillUse.FUMBLED_PLAYER_LANDS_SAFELY));
				}
			} else if (rollDice) {
				getResult().addReport(new ReportRightStuffRoll(fThrownPlayerId, successful, landingRoll,
					minimumRoll, reRollUsed, rightStuffModifiers.toArray(new RightStuffModifier[0])));
			}
			if (successful) {

				SppMechanic spp = (SppMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SPP.name());
				spp.addLanding(game.getGameResult().getPlayerResult(thrownPlayer));

				if (passResult == PassResult.ACCURATE) {
					GameResult gameResult = getGameState().getGame().getGameResult();
					TeamResult teamResult = game.getActingTeam() == game.getTeamHome() ? gameResult.getTeamResultHome() : gameResult.getTeamResultAway();
					if (game.getThrower() != null) {
						spp.addCompletion(getGameState().getPrayerState().getAdditionalCompletionSppTeams(), teamResult.getPlayerResult(game.getThrower()));

					}
				}
				if (fThrownPlayerHasBall) {
					if (UtilServerSteps.checkTouchdown(getGameState())) {
						publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					}
				} else {
					if (game.getFieldModel().getPlayerCoordinate(thrownPlayer).equals(game.getFieldModel().getBallCoordinate())) {
						game.getFieldModel().setBallMoving(true);
						publishParameter(
							new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
					}
				}
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
				getResult().setNextAction(StepAction.GOTO_LABEL, goToOnSuccess);
			} else {
				doRoll = offerRescue(thrownPlayer, minimumRoll);
			}
		}
		if (!doRoll) {
			InjuryResult injuryResultThrownPlayer = UtilServerInjury.handleInjury(this, fumbledKtm ? new InjuryTypeFumbledKtmApoKo() : new InjuryTypeTTMLanding(),
				game.getActingPlayer().getPlayer(), thrownPlayer, playerCoordinate, null, null, ApothecaryMode.THROWN_PLAYER);
			DropPlayerContext dropPlayerContext = new DropPlayerContext(injuryResultThrownPlayer, fThrownPlayerHasBall, false,
				null, thrownPlayer.getId(), ApothecaryMode.THROWN_PLAYER, false, false, null, fThrownPlayerHasBall, false,
				null);
			publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT, new SteadyFootingContext(dropPlayerContext)));
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	/**
	 * Offers the optional modifiers of the thrown player together with the available re-rolls. A lone Swoop re-roll is
	 * used right away, there is nothing to choose between.
	 *
	 * @return whether the step waits for the coach or for a re-roll instead of resolving the failed landing
	 */
	private boolean offerRescue(Player<?> thrownPlayer, int minimumRoll) {
		Game game = getGameState().getGame();
		setReRolledAction(ReRolledActions.RIGHT_STUFF);
		List<ModifierChoiceOption> options = selectionService.findOptions(game, thrownPlayer, passResult, landingRoll);
		ReRollSource swoopReRoll = (!reRollUsed && usingSwoop
			&& thrownPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection)) ? ReRollSources.SWOOP : null;
		if (options.isEmpty() && swoopReRoll != null) {
			setReRollSource(swoopReRoll);
			awaitingRescue = true;
			getResult().setNextAction(StepAction.REPEAT);
			return true;
		}
		List<ModifierChoiceOption> combinations = selectionService.findCombinations(game, thrownPlayer, passResult);
		awaitingRescue = getGameState().getReRollService().askForReRollIfAvailable(
			ReRollRequest.forPlayer(getGameState(), thrownPlayer, ReRolledActions.RIGHT_STUFF, minimumRoll)
				.reRollSkill(swoopReRoll == null ? null : swoopReRoll.getSkill(game))
				.dialogParameter(new ReRollModifierChoiceDialogParameterFactory(
					landingRoll, options, combinations, !reRollUsed))
				.build());
		return awaitingRescue;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.DROP_THROWN_PLAYER.addTo(jsonObject, fDropThrownPlayer);
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, passResult);
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, goToOnSuccess);
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, kickedPlayer);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, oldPlayerState);
		IServerJsonOption.USING_SWOOP.addTo(jsonObject, usingSwoop);
		IServerJsonOption.RIGHT_STUFF_ROLL.addTo(jsonObject, landingRoll);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, reRollUsed);
		IServerJsonOption.AWAITING_RESCUE.addTo(jsonObject, awaitingRescue);
		JsonArray skills = new JsonArray();
		selectedModifierSkills.forEach(skill -> skills.add(skill.getName()));
		IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, skills);
		return jsonObject;
	}

	@Override
	public StepRightStuff initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		passResult = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		goToOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		kickedPlayer = IServerJsonOption.IS_KICKED_PLAYER.getFrom(source, jsonObject);
		fDropThrownPlayer = IServerJsonOption.DROP_THROWN_PLAYER.getFrom(source, jsonObject);
		oldPlayerState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		usingSwoop = IServerJsonOption.USING_SWOOP.getFrom(source, jsonObject);
		landingRoll = IServerJsonOption.RIGHT_STUFF_ROLL.isDefinedIn(jsonObject)
			? IServerJsonOption.RIGHT_STUFF_ROLL.getFrom(source, jsonObject) : 0;
		reRollUsed = toPrimitive(IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject));
		awaitingRescue = IServerJsonOption.AWAITING_RESCUE.isDefinedIn(jsonObject)
			? toPrimitive(IServerJsonOption.AWAITING_RESCUE.getFrom(source, jsonObject))
			: getReRolledAction() == ReRolledActions.RIGHT_STUFF;
		selectedModifierSkills.clear();
		JsonArray skills = IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(source, jsonObject);
		if (skills != null) {
			SkillFactory factory = source.getFactory(FactoryType.Factory.SKILL);
			for (JsonValue skill : skills) {
				selectedModifierSkills.add(factory.forName(skill.asString()));
			}
		}
		return this;
	}

}
