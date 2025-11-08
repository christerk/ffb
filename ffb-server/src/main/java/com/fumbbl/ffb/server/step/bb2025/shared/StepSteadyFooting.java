package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2025.ReportSteadyFootingRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
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
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepSteadyFooting extends AbstractStepWithReRoll {

	private static final int MINMUM_ROLL = 6;
	private Boolean useSkill;
	private String goToLabelOnFailure, goToLabelOnSuccess;
	private ApothecaryMode apothecaryMode;
	private SteadyFootingContext context;
	private PlayerState oldDefenderState;
	private boolean skip;
	private String playerId;
	private boolean removeCatchMode = true;

	public StepSteadyFooting(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.STEADY_FOOTING;
	}


	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						goToLabelOnFailure = (String) parameter.getValue();
						break;
					case GOTO_LABEL_ON_SUCCESS:
						goToLabelOnSuccess = (String) parameter.getValue();
						break;
					case APOTHECARY_MODE:
						apothecaryMode = (ApothecaryMode) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {

		switch (parameter.getKey()) {
			case STEADY_FOOTING_CONTEXT:
				SteadyFootingContext steadyFootingContext = (SteadyFootingContext) parameter.getValue();
				if (apothecaryMode == null || apothecaryMode == steadyFootingContext.getApothecaryMode()) {
					context = steadyFootingContext;

					DropPlayerContext dropPlayerContext = steadyFootingContext.getDropPlayerContext();
					InjuryResult injuryResult = steadyFootingContext.getInjuryResult();
					InjuryTypeServer<?> injuryType = steadyFootingContext.getInjuryType();

					if (dropPlayerContext != null) {
						playerId = dropPlayerContext.getPlayerId();
					} else if (injuryResult != null) {
						playerId = injuryResult.injuryContext().getDefenderId();
					} else if (injuryType != null) {
						playerId = getGameState().getGame().getActingPlayer().getPlayerId();
					}

					consume(parameter);
					return true;
				}
				return false;
			case OLD_DEFENDER_STATE:
				if (this.apothecaryMode == ApothecaryMode.DEFENDER) {
					this.oldDefenderState = (PlayerState) parameter.getValue();
					return true;
				}
				break;
			case ATTACKER_ALREADY_DOWN:
				if (this.apothecaryMode == ApothecaryMode.ATTACKER) {
					skip = (Boolean) parameter.getValue();
					return true;
				}
				break;
			case BALL_KNOCKED_LOSE:
				removeCatchMode = !toPrimitive((Boolean) parameter.getValue());
				consume(parameter);
				return true;
			default:
				break;
		}

		return super.setParameter(parameter);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (useSkillCommand.getSkill().hasSkillProperty(NamedProperties.canAvoidFallingDown)) {
						useSkill = useSkillCommand.isSkillUsed();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				default:
					break;
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();

		if (context == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		Player<?> player = game.getPlayerById(playerId);

		Optional<Skill> skill = UtilCards.getSkillWithProperty(player, NamedProperties.canAvoidFallingDown);

		if (skip || !skill.isPresent()) {
			fail();
			return;
		}

		if (useSkill == null) {
			UtilServerDialog.showDialog(getGameState(),
				new DialogSkillUseParameter(player.getId(), player.getSkillWithProperty(NamedProperties.canAvoidFallingDown),
					MINMUM_ROLL), !game.getActingTeam().hasPlayer(player));
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		boolean reRolled = ((getReRolledAction() == ReRolledActions.STEADY_FOOTING) && (getReRollSource() != null));

		if (!reRolled) {
			getResult().addReport(new ReportSkillUse(player.getId(), skill.get(), useSkill, SkillUse.AVOID_FALLING));
		}

		if (!useSkill) {
			fail();
			return;
		}

		if (ReRolledActions.STEADY_FOOTING == getReRolledAction()) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
				fail();
				return;
			}
		}

		int roll = getGameState().getDiceRoller().rollSkill();

		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, MINMUM_ROLL);

		getResult().addReport(new ReportSteadyFootingRoll(player.getId(), successful, roll, MINMUM_ROLL, reRolled));

		if (successful) {
			publishParameter(StepParameter.from(StepParameterKey.END_TURN, false));
			publishParameter(StepParameter.from(StepParameterKey.END_PLAYER_ACTION, false));
			if (removeCatchMode) {
				publishParameter(StepParameter.from(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, null));
			}
			if (oldDefenderState != null) {
				game.getFieldModel().setPlayerState(player, oldDefenderState);
			}
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (apothecaryMode == ApothecaryMode.ATTACKER && playerState.getBase() == PlayerState.FALLING) {
				game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.MOVING));
			}
			if (StringTool.isProvided(goToLabelOnSuccess)) {
				getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnSuccess);
			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
			return;
		}

		if (!reRolled &&
			UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, ReRolledActions.STEADY_FOOTING, MINMUM_ROLL,
				false)) {
			setReRolledAction(ReRolledActions.STEADY_FOOTING);
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		fail();
	}

	private void fail() {
		if (StringTool.isProvided(goToLabelOnFailure)) {
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
		if (context.getDropPlayerContext() != null) {
			publishParameter(StepParameter.from(StepParameterKey.DROP_PLAYER_CONTEXT, context.getDropPlayerContext()));
		}
		if (context.getInjuryResult() != null) {
			publishParameter(StepParameter.from(StepParameterKey.INJURY_RESULT, context.getInjuryResult()));
		}
		if (context.getInjuryType() != null) {
			publishParameter(StepParameter.from(StepParameterKey.INJURY_TYPE, context.getInjuryType()));
		}
		if (apothecaryMode == ApothecaryMode.ATTACKER) {
			publishParameter(StepParameter.from(StepParameterKey.ATTACKER_ALREADY_DOWN, true));
		}

		context.getDeferredCommands().forEach(command -> command.execute(this));
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		//TODO
		return jsonObject;
	}

	@Override
	public StepSteadyFooting initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		//TODO
		return this;
	}
}
