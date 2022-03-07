package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepHandleDropPlayerContext extends AbstractStepWithReRoll {

	private DropPlayerContext dropPlayerContext;

	public StepHandleDropPlayerContext(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.HANDLE_DROP_PLAYER_CONTEXT;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case DROP_PLAYER_CONTEXT: {
					dropPlayerContext = (DropPlayerContext) parameter.getValue();
					consume(parameter);
					return true;
				}
				case SUCCESSFUL_PRO: {
					boolean successful = toPrimitive((Boolean) parameter.getValue());

					if (successful) {
						successfulSkillUse(dropPlayerContext.getInjuryResult());
					} else {
						dropPlayerContext.getInjuryResult().injuryContext().setModifiedInjuryContext(null);
					}
					consume(parameter);
					return true;
				}
				default:
					break;
			}
		}
		return super.setParameter(parameter);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getCommand().getId() == NetCommandId.CLIENT_USE_SKILL) {
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				Skill skill = clientCommandUseSkill.getSkill();
				InjuryResult injuryResult = dropPlayerContext.getInjuryResult();
				getResult().addReport(new ReportSkillUse(clientCommandUseSkill.getPlayerId(), skill, clientCommandUseSkill.isSkillUsed(), injuryResult.injuryContext().getModifiedInjuryContext().getSkillUse()));
				if (clientCommandUseSkill.isSkillUsed()) {

					getGameState().getGame().getPlayerById(clientCommandUseSkill.getPlayerId()).markUsed(skill, getGameState().getGame());

					if (skill.getSkillBehaviour().getInjuryContextModification().requiresConditionalReRollSkill()) {
						getGameState().pushCurrentStepOnStack();
						Sequence sequence = new Sequence(getGameState());
						sequence.add(StepId.PRO, StepParameter.from(StepParameterKey.PLAYER_ID, clientCommandUseSkill.getPlayerId()));
						getGameState().getStepStack().push(sequence.getSequence());
						commandStatus = StepCommandStatus.SKIP_STEP;
						getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						successfulSkillUse(injuryResult);
					}
				}
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void successfulSkillUse(InjuryResult injuryResult) {
		injuryResult.injuryContext().getModifiedInjuryContext().getReports().forEach(report -> getResult().addReport(report));
		injuryResult.swapToAlternateContext(this, getGameState().getGame());
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (dropPlayerContext != null && dropPlayerContext.getInjuryResult() != null) {
			Game game = getGameState().getGame();

			InjuryResult injuryResult = dropPlayerContext.getInjuryResult();
			if (injuryResult.injuryContext().getModifiedInjuryContext() != null && !injuryResult.isAlreadyReported()) {
				injuryResult.report(this);
				ModifiedInjuryContext injuryContext = injuryResult.injuryContext().getModifiedInjuryContext();
				UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getActingPlayer().getPlayerId(), injuryContext.getUsedSkill(), 0), true);
				getResult().setNextAction(StepAction.CONTINUE);
			} else {

				if (!dropPlayerContext.isAlreadyDropped() && (!dropPlayerContext.isRequiresArmourBreak() || injuryResult.injuryContext().isArmorBroken())) {
					publishParameters(UtilServerInjury.dropPlayer(this, game.getPlayerById(dropPlayerContext.getPlayerId()),
						dropPlayerContext.getApothecaryMode(), dropPlayerContext.isEligibleForSafePairOfHands()));
					if (dropPlayerContext.isEndTurn()) {
						publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					}
				}
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResult));
				if (StringTool.isProvided(dropPlayerContext.getLabel())) {
					getResult().setNextAction(StepAction.GOTO_LABEL, dropPlayerContext.getLabel());
				}
			}
		}
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (dropPlayerContext != null) {
			IServerJsonOption.DROP_PLAYER_CONTEXT.addTo(jsonObject, dropPlayerContext.toJsonValue());
		}
		return jsonObject;
	}

	@Override
	public StepHandleDropPlayerContext initFrom(IFactorySource source, JsonValue pJsonValue) {
		StepHandleDropPlayerContext step = (StepHandleDropPlayerContext) super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		if (IServerJsonOption.DROP_PLAYER_CONTEXT.isDefinedIn(jsonObject)) {
			dropPlayerContext = new DropPlayerContext().initFrom(source, IServerJsonOption.DROP_PLAYER_CONTEXT.getFrom(source, jsonObject));
		}
		return step;
	}
}
