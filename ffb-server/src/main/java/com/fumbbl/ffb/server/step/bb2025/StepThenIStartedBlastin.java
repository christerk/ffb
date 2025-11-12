package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandTargetSelected;
import com.fumbbl.ffb.report.bb2020.ReportThenIStartedBlastin;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeThenIStartedBlastin;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepThenIStartedBlastin extends AbstractStepWithReRoll {

	private String gotoLabelOnEnd;
	private int roll;
	private TurnMode oldTurnMode;

	public StepThenIStartedBlastin(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.THEN_I_STARTED_BLASTIN;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus stepCommandStatus = super.handleCommand(pReceivedCommand);

		if (stepCommandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getCommand().getId()) {
				case CLIENT_TARGET_SELECTED:
					ActingPlayer actingPlayer = game.getActingPlayer();
					ClientCommandTargetSelected targetSelected = (ClientCommandTargetSelected) pReceivedCommand.getCommand();
					game.setDefenderId(targetSelected.getTargetPlayerId());
					if (game.playingTeamHasActingPLayer()) {
						stepCommandStatus = StepCommandStatus.EXECUTE_STEP;
					} else {
						game.setHomePlaying(!game.isHomePlaying());
						FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
						FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
						getResult().setAnimation(new Animation(AnimationType.THROW_KEG,
							throwerCoordinate,
							targetCoordinate));
						hitPlayer(game.getDefender());
						stepCommandStatus = StepCommandStatus.SKIP_STEP;
						getResult().setNextAction(StepAction.NEXT_STEP);
						getResult().addReport(new ReportThenIStartedBlastin(actingPlayer.getPlayerId(), game.getDefenderId(), 0, true, false));
					}
					break;
				case CLIENT_END_TURN:
					restoreTurnModes(game);
					stepCommandStatus = StepCommandStatus.SKIP_STEP;
					getResult().setNextAction(StepAction.NEXT_STEP);
				default:
					break;
			}
		}

		if (stepCommandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return stepCommandStatus;
	}

	private void restoreTurnModes(Game game) {
		game.setTurnMode(game.getLastTurnMode());
		game.setLastTurnMode(oldTurnMode);
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
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canBlastRemotePlayer);
		if (skill != null || getReRolledAction() == ReRolledActions.THEN_I_STARTED_BLASTIN) {

			if (getReRolledAction() == ReRolledActions.THEN_I_STARTED_BLASTIN) {
				if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					fail();
					return;
				}
			}

			if (game.getTurnMode() != TurnMode.THEN_I_STARTED_BLASTIN) {
				oldTurnMode = game.getLastTurnMode();
				game.setTurnMode(TurnMode.THEN_I_STARTED_BLASTIN);
				getResult().setNextAction(StepAction.CONTINUE);
				getResult().addReport(new ReportThenIStartedBlastin(actingPlayer.getPlayerId(), null, 0, false, false));
				return;
			}


			actingPlayer.markSkillUsed(skill);
			switch (actingPlayer.getPlayerAction()) {
				case BLITZ_MOVE:
				case KICK_EM_BLITZ:
					game.getTurnData().setBlitzUsed(true);
					break;
				case FOUL_MOVE:
					if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
						game.getTurnData().setFoulUsed(true);
					}
					break;
				case HAND_OVER_MOVE:
					game.getTurnData().setHandOverUsed(true);
					break;
				case PASS_MOVE:
				case THROW_TEAM_MATE_MOVE:
					game.getTurnData().setPassUsed(true);
					break;
				case KICK_TEAM_MATE_MOVE:
					game.getTurnData().setKtmUsed(true);
					break;
				default:
					break;
			}

			roll = getGameState().getDiceRoller().rollSkill();

			boolean success = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, 3);
			getResult().addReport(new ReportThenIStartedBlastin(actingPlayer.getPlayerId(), game.getDefenderId(), roll, success, roll == 1));

			if (success) {
				hitPlayer(game.getDefender());
			} else {
				if (getReRolledAction() != ReRolledActions.THEN_I_STARTED_BLASTIN && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer, ReRolledActions.THEN_I_STARTED_BLASTIN, 3, false)) {
					setReRolledAction(ReRolledActions.THEN_I_STARTED_BLASTIN);
					getResult().setNextAction(StepAction.CONTINUE);
				} else {
					fail();
				}
			}

		}

		publishParameter(StepParameter.from(StepParameterKey.END_PLAYER_ACTION, Boolean.TRUE));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void fail() {
		Game game = getGameState().getGame();
		if (roll == 1) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			getResult().setAnimation(new Animation(AnimationType.FUMBLED_KEG, throwerCoordinate));
			Player<?> hitPlayer = actingPlayer.getPlayer();
			hitPlayer(hitPlayer);
		} else {
			game.setHomePlaying(!game.isHomePlaying());
			getResult().setNextAction(StepAction.CONTINUE);
			getResult().setSound(SoundId.QUESTION);
		}
	}

	private void hitPlayer(Player<?> hitPlayer) {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate startCoordinate = fieldModel.getPlayerCoordinate(game.getActingPlayer().getPlayer());
		FieldCoordinate targetCoordinate = fieldModel.getPlayerCoordinate(hitPlayer);
		InjuryResult injuryResult = UtilServerInjury.handleInjury(this, new InjuryTypeThenIStartedBlastin(), null, hitPlayer, targetCoordinate,
			null, null, ApothecaryMode.DEFENDER);
		boolean endTurn = UtilPlayer.hasBall(game, hitPlayer) && game.getActingTeam().hasPlayer(hitPlayer);
		publishParameter(StepParameter.from(StepParameterKey.DROP_PLAYER_CONTEXT,
			new DropPlayerContext(injuryResult, endTurn, true, null, hitPlayer.getId(), ApothecaryMode.DEFENDER, true)));
		getResult().setSound(SoundId.EXPLODE);
		getResult().setNextAction(StepAction.NEXT_STEP);
		restoreTurnModes(game);
		getResult().setAnimation(new Animation(AnimationType.THEN_I_STARTED_BLASTIN,
			startCoordinate,
			targetCoordinate));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, oldTurnMode);
		return jsonObject;
	}

	@Override
	public StepThenIStartedBlastin initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		oldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(source, jsonObject);
		return this;
	}
}
