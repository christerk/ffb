package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportDauntlessRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill DAUNTLESS.
 * 
 * Expects stepParameter USING_STAB to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepDauntless extends AbstractStepWithReRoll {
	
	private Boolean fUsingStab;
	
	public StepDauntless(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.DAUNTLESS;
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
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case USING_STAB:
					fUsingStab = (Boolean) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
  private void executeStep() {
    boolean doNextStep = true;
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (UtilCards.hasSkill(game, actingPlayer, Skill.DAUNTLESS) && (actingPlayer.getStrength() < UtilCards.getPlayerStrength(game, game.getDefender())) && ((fUsingStab == null) || !fUsingStab) && !UtilCards.hasSkill(game, actingPlayer, Skill.CHAINSAW)) {
      boolean doDauntless = true;
      if (ReRolledAction.DAUNTLESS == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doDauntless = false;
        }
      }
      if (doDauntless) {
        int dauntlessRoll = getGameState().getDiceRoller().rollDauntless();
        int minimumRoll = DiceInterpreter.getInstance().minimumRollDauntless(actingPlayer.getStrength(), UtilCards.getPlayerStrength(game, game.getDefender()));
        boolean successful = (dauntlessRoll >= minimumRoll);
        boolean reRolled = ((getReRolledAction() == ReRolledAction.DAUNTLESS) && (getReRollSource() != null));
        getResult().addReport(new ReportDauntlessRoll(actingPlayer.getPlayerId(), successful, dauntlessRoll, minimumRoll, reRolled, UtilCards.getPlayerStrength(game, game.getDefender())));
        if (successful) {
          actingPlayer.setStrength(UtilCards.getPlayerStrength(game, game.getDefender()));
        } else {
          if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.DAUNTLESS, minimumRoll, false)) {
            doNextStep = false;
          }
        }
      }
    }
    if (doNextStep) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fUsingStab = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
    return jsonObject;
  }
  
  @Override
  public StepDauntless initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fUsingStab = IServerJsonOption.USING_STAB.getFrom(jsonObject);
    return this;
  }

}
