package com.fumbbl.ffb.server.step.bb2025;

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
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepHandleDropPlayerContext extends AbstractStepWithReRoll {

	private DropPlayerContext dropPlayerContext;
	private String playerId;
	private Skill skill;

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
						getGameState().getGame().getPlayerById(playerId).markUsed(skill, getGameState().getGame());
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

					if (skill.getSkillBehaviour().getInjuryContextModification().requiresConditionalReRollSkill()) {
						getGameState().pushCurrentStepOnStack();
						Sequence sequence = new Sequence(getGameState());
						sequence.add(StepId.PRO, StepParameter.from(StepParameterKey.PLAYER_ID, clientCommandUseSkill.getPlayerId()));
						getGameState().getStepStack().push(sequence.getSequence());
						commandStatus = StepCommandStatus.SKIP_STEP;
						getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						getGameState().getGame().getPlayerById(clientCommandUseSkill.getPlayerId()).markUsed(skill, getGameState().getGame());
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
		dropPlayerContext.setEndTurn(dropPlayerContext.isModifiedInjuryEndsTurn());
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
				playerId = game.getActingPlayer().getPlayerId();
				skill = injuryContext.getUsedSkill();
				UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(playerId, skill, 0), true);
				getResult().setNextAction(StepAction.CONTINUE);
			} else {

				if (!dropPlayerContext.isAlreadyDropped() && (!dropPlayerContext.isRequiresArmourBreak() || injuryResult.injuryContext().isArmorBroken())) {
					publishParameters(UtilServerInjury.dropPlayer(this, game.getPlayerById(dropPlayerContext.getPlayerId()),
						dropPlayerContext.getApothecaryMode(), dropPlayerContext.isEligibleForSafePairOfHands(),
						StepParameterKey.INJURY_RESULT_FROM_ACTUAL_DROP));
					if (dropPlayerContext.isEndTurn()) {
						publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					}
					if (dropPlayerContext.getVictimStateKey() != null) {
						publishParameter(new StepParameter(dropPlayerContext.getVictimStateKey(), game.getFieldModel().getPlayerState(game.getDefender())));
					}
					if (ArrayTool.isProvided(dropPlayerContext.getAdditionalVictimStateKeys())) {
						for (StepParameterKey additionalVictimStateKey : dropPlayerContext.getAdditionalVictimStateKeys()) {
							publishParameter(new StepParameter(additionalVictimStateKey, game.getFieldModel().getPlayerState(game.getDefender())));
						}
					}
				} else if (!dropPlayerContext.isAlreadyDropped()
					&& dropPlayerContext.isEndTurnWithoutKnockdown() && dropPlayerContext.isEndTurn()) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}

				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT_FROM_ACTUAL_DROP, injuryResult));
				getGameState().getSteadyFootingState().clear();
				if (StringTool.isProvided(dropPlayerContext.getLabel())) {
					getResult().setNextAction(StepAction.GOTO_LABEL, dropPlayerContext.getLabel());
				}
				UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
			}
		}
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (dropPlayerContext != null) {
			IServerJsonOption.DROP_PLAYER_CONTEXT.addTo(jsonObject, dropPlayerContext.toJsonValue());
		}
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.SKILL.addTo(jsonObject, skill);
		return jsonObject;
	}

	@Override
	public StepHandleDropPlayerContext initFrom(IFactorySource source, JsonValue jsonValue) {
		StepHandleDropPlayerContext step = (StepHandleDropPlayerContext) super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		if (IServerJsonOption.DROP_PLAYER_CONTEXT.isDefinedIn(jsonObject)) {
			dropPlayerContext = new DropPlayerContext().initFrom(source, IServerJsonOption.DROP_PLAYER_CONTEXT.getFrom(source, jsonObject));
		}
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		return step;
	}
}
