package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.mixed.ReportThrownKeg;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeKegHit;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepThrowKeg extends AbstractStepWithReRoll {

	private String playerId;
	private int roll;

	public StepThrowKeg(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.THROW_KEG;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.TARGET_PLAYER_ID) {
					playerId = (String) parameter.getValue();
				}
			});
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus stepCommandStatus = super.handleCommand(pReceivedCommand);

		if (stepCommandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return stepCommandStatus;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canThrowKeg);
		if (skill != null || getReRolledAction() == ReRolledActions.THROW_KEG) {

			if (getReRolledAction() == ReRolledActions.THROW_KEG) {
				if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					fail();
					return;
				}
			} else {
				actingPlayer.markSkillUsed(skill);
			}

			roll = getGameState().getDiceRoller().rollSkill();

			boolean success = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, 3);
			getResult().addReport(new ReportThrownKeg(actingPlayer.getPlayerId(), playerId, roll, success, roll == 1));

			FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(playerId));
			if (success) {
				getResult().setAnimation(new Animation(AnimationType.THROW_KEG,
					throwerCoordinate,
					targetCoordinate));
				hitPlayer(game.getPlayerById(playerId), false);
			} else {
				if (getReRolledAction() != ReRolledActions.THROW_KEG && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer, ReRolledActions.THROW_KEG, 3, false)) {
					setReRolledAction(ReRolledActions.THROW_KEG);
					getResult().setNextAction(StepAction.CONTINUE);
				} else {
					fail();
				}
			}

		}
	}

	private void fail() {
		if (roll == 1) {
			Game game = getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			getResult().setAnimation(new Animation(AnimationType.FUMBLED_KEG, throwerCoordinate));
			Player<?> hitPlayer = actingPlayer.getPlayer();
			hitPlayer(hitPlayer, true);
		}
	}

	private void hitPlayer(Player<?> hitPlayer, boolean endTurn) {
		FieldCoordinate coordinate = getGameState().getGame().getFieldModel().getPlayerCoordinate(hitPlayer);
		InjuryResult injuryResult = UtilServerInjury.handleInjury(this, new InjuryTypeKegHit(), null, hitPlayer, coordinate,
			null, null, ApothecaryMode.DEFENDER);
		publishParameter(StepParameter.from(StepParameterKey.DROP_PLAYER_CONTEXT,
			new DropPlayerContext(injuryResult, endTurn, true, null, hitPlayer.getId(), ApothecaryMode.DEFENDER, false)));
		getResult().setSound(SoundId.EXPLODE);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TARGET_PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		return jsonObject;
	}

	@Override
	public StepThrowKeg initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.TARGET_PLAYER_ID.getFrom(source, jsonObject);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		return this;
	}
}
