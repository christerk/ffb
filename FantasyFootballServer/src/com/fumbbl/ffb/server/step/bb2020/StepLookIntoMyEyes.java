package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportLookIntoMyEyesRoll;
import com.fumbbl.ffb.report.bb2020.ReportSkillWasted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepLookIntoMyEyes extends AbstractStepWithReRoll {

	private static final ReRolledAction RE_ROLLED_ACTION = ReRolledActions.LOOK_INTO_MY_EYES;
	private boolean endPlayerAction, endTurn;

	public StepLookIntoMyEyes(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.LOOK_INTO_MY_EYES;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus status = super.handleCommand(pReceivedCommand);

		if (status == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return status;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case END_TURN:
					endTurn = toPrimitive((Boolean) parameter.getValue());
					return true;
				case END_PLAYER_ACTION:
					endPlayerAction = toPrimitive((Boolean) parameter.getValue());
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canStealBallFromOpponent);
		if (skill != null) {

			if (endTurn || endPlayerAction) {
				getResult().addReport(new ReportSkillWasted(actingPlayer.getPlayerId(), skill));
				leave(actingPlayer, skill, endPlayerAction, endTurn);
			} else {
				if (getReRolledAction() == RE_ROLLED_ACTION) {
					if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
						leave(actingPlayer, skill, false, false);
						return;
					}
				}


				if (StringTool.isProvided(game.getDefenderId())) {
					FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
					getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.LOOK_INTO_MY_EYES));

					int roll = getGameState().getDiceRoller().rollSkill();
					boolean successful = roll > 1;

					getResult().addReport(new ReportLookIntoMyEyesRoll(actingPlayer.getPlayerId(), successful, roll, getReRolledAction() != null));
					if (successful) {
						game.getFieldModel().setBallCoordinate(playerCoordinate);
						getResult().setSound(SoundId.PICKUP);
						leave(actingPlayer, skill, true, false);
					} else if (getReRolledAction() != null || !UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, RE_ROLLED_ACTION, 2, false)) {
						getResult().setSound(SoundId.QUESTION);
						leave(actingPlayer, skill, false, false);
					}
				} else {
					leave(actingPlayer, skill, true, false);
				}
			}
		} else {
			leave(actingPlayer, skill, false, false);
		}
	}

	private void leave(ActingPlayer actingPlayer, Skill skill, boolean endPlayerAction, boolean endTurn) {
		actingPlayer.markSkillUsed(skill);
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (endPlayerAction || endTurn) {
			EndPlayerAction endActionGenerator = (EndPlayerAction) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR).forName(SequenceGenerator.Type.EndPlayerAction.name());
			endActionGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, endPlayerAction, endTurn));
		} else {

			Select generator = (Select) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR).forName(SequenceGenerator.Type.Select.name());
			generator.pushSequence(new Select.SequenceParams(getGameState(), true));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		return jsonObject;
	}

	@Override
	public StepLookIntoMyEyes initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		return this;
	}
}
