package com.fumbbl.ffb.server.step.bb2025.special;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilCards;


@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepRecheckExplodeSkill extends AbstractStep {

	private String catcherId;
	private Boolean explodeSkillUsed;
	private boolean skip = true;

	public StepRecheckExplodeSkill(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.RECHECK_EXPLODE_SKILL;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case SKIP:
					skip = parameter.getValue() != null && (Boolean) parameter.getValue();
					return true;
				case CATCHER_ID:
					catcherId = (String) parameter.getValue();
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
				ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				if (clientCommandUseSkill.getSkill().hasSkillProperty(NamedProperties.canForceBombExplosion)) {
					explodeSkillUsed = clientCommandUseSkill.isSkillUsed();
					getResult().addReport(
						new ReportSkillUse(clientCommandUseSkill.getPlayerId(), clientCommandUseSkill.getSkill(),
							clientCommandUseSkill.isSkillUsed(), SkillUse.FORCE_BOMB_EXPLOSION));
					if (explodeSkillUsed) {
						getGameState().getGame().getActingPlayer().markSkillUsed(clientCommandUseSkill.getSkill());
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		game.getTurnData().setBombUsed(true);

		if (!skip && UtilGameOption.isOptionEnabled(game, GameOptionId.BOMB_BOUNCES_ON_EMPTY_SQUARES) &&
			catcherId != null) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			Skill explodeSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canForceBombExplosion);
			if (explodeSkill != null) {
				if (explodeSkillUsed == null) {
					UtilServerDialog.showDialog(getGameState(),
						new DialogSkillUseParameter(actingPlayer.getPlayerId(), explodeSkill, 0), false);
					return;
				}
			} else if (explodeSkillUsed == null) {
				explodeSkillUsed = false;
			}
			if (explodeSkillUsed) {
				catcherId = null;
			}
		}
		leaveStep();
	}

	private void leaveStep() {
		publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, catcherId));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IServerJsonOption.SKILL_USED.addTo(jsonObject, explodeSkillUsed);
		IServerJsonOption.SKIP.addTo(jsonObject, skip);
		return jsonObject;
	}

	@Override
	public StepRecheckExplodeSkill initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		catcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		explodeSkillUsed = IServerJsonOption.SKILL_USED.getFrom(source, jsonObject);
		skip = IServerJsonOption.SKIP.getFrom(source, jsonObject);
		return this;
	}

}
