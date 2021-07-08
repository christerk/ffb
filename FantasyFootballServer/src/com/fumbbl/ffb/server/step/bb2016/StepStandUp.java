package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportStandUpRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerConstant;
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
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step in select sequence to stand up a prone player.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepStandUp extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;

	public StepStandUp(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.STAND_UP;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnFailure)) {
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		game.getTurnData().setTurnStarted(true);
		ActingPlayer actingPlayer = game.getActingPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if ((actingPlayer.isStandingUp() && !actingPlayer.hasMoved())
				|| (ReRolledActions.STAND_UP == getReRolledAction())) {
			actingPlayer.setHasMoved(true);
			game.setConcessionPossible(false);
			boolean rollStandUp = (actingPlayer.getPlayer().getMovementWithModifiers() < IServerConstant.MINIMUM_MOVE_TO_STAND_UP);
			if (rollStandUp) {
				if (ReRolledActions.STAND_UP == getReRolledAction()) {
					if ((getReRollSource() == null)
							|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
						rollStandUp = false;
					}
				}
				if (rollStandUp) {
					int roll = getGameState().getDiceRoller().rollSkill();

					int modifier = 0;
					if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowStandUpAssists)) {
						modifier = UtilPlayer.findStandUpAssists(game, actingPlayer.getPlayer());
					}

					boolean successful = DiceInterpreter.getInstance().isStandUpSuccessful(roll, modifier);
					boolean reRolled = ((getReRolledAction() == ReRolledActions.STAND_UP) && (getReRollSource() != null));
					getResult()
							.addReport(new ReportStandUpRoll(actingPlayer.getPlayerId(), successful, roll, modifier, reRolled));
					if (successful) {
						actingPlayer.setStandingUp(false);
						if (playerState.isRooted()) {
							getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
						} else {
							getResult().setNextAction(StepAction.NEXT_STEP);
						}
					} else {
						if ((getReRolledAction() == ReRolledActions.STAND_UP)
								|| !UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
										ReRolledActions.STAND_UP, Math.max(2, 4 - modifier), false)) {
							rollStandUp = false;
							switch (actingPlayer.getPlayerAction()) {
							case BLITZ:
							case BLITZ_MOVE:
							case KICK_TEAM_MATE:
							case KICK_TEAM_MATE_MOVE:
								game.getTurnData().setBlitzUsed(true);
								break;
							case PASS:
							case PASS_MOVE:
							case THROW_TEAM_MATE:
							case THROW_TEAM_MATE_MOVE:
								game.getTurnData().setPassUsed(true);
								break;
							case HAND_OVER:
							case HAND_OVER_MOVE:
								game.getTurnData().setHandOverUsed(true);
								break;
							case FOUL:
							case FOUL_MOVE:
								game.getTurnData().setFoulUsed(true);
								break;
							default:
								break;
							}
						}
					}
				}
				if (!rollStandUp) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
							playerState.changeBase(PlayerState.PRONE).changeActive(false));
					publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				}
			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		return jsonObject;
	}

	@Override
	public StepStandUp initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		return this;
	}

}
