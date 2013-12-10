package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportFoul;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in foul sequence to handle the actual foul.
 * 
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepFoul extends AbstractStep {

	public StepFoul(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.FOUL;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    getResult().addReport(new ReportFoul(game.getDefenderId()));
    if (!UtilCards.hasSkill(game, actingPlayer, Skill.CHAINSAW)) {
      getResult().setSound(Sound.FOUL);
    }
    UtilGame.syncGameModel(this);
    FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
    InjuryResult injuryResultDefender = UtilInjury.handleInjury(this, InjuryType.FOUL, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
    publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
	// ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    return super.toJsonValue();
  }
  
  @Override
  public StepFoul initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    return this;
  }

}
