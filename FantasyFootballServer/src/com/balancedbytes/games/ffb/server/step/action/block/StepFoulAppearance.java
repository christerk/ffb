package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill FOUL_APPEARANCE.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * @author Kalimar
 */
public class StepFoulAppearance extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;

	public StepFoulAppearance(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.FOUL_APPEARANCE;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
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
    if ((game.getDefender() != null) && UtilCards.hasSkill(game, game.getDefender(), Skill.FOUL_APPEARANCE) && !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)) {
      boolean doRoll = true;
      if (ReRolledAction.FOUL_APPEARANCE == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doRoll = false;
          actingPlayer.setHasBlocked(true);
        	game.getTurnData().setTurnStarted(true);
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        }
      }
      if (doRoll) {
        int foulAppearanceRoll = getGameState().getDiceRoller().rollSkill();
        int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
        boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
        boolean reRolled = ((getReRolledAction() == ReRolledAction.FOUL_APPEARANCE) && (getReRollSource() != null));
        getResult().addReport(new ReportSkillRoll(ReportId.FOUL_APPEARANCE_ROLL, actingPlayer.getPlayerId(), mayBlock, foulAppearanceRoll, minimumRoll, reRolled));
        if (mayBlock) {
        	getResult().setNextAction(StepAction.NEXT_STEP);
        } else {
          if (!UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.FOUL_APPEARANCE, minimumRoll, false)) {
            actingPlayer.setHasBlocked(true);
          	game.getTurnData().setTurnStarted(true);
          	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
          }
        }
        if (!mayBlock && !reRolled) {
          getResult().setSound(Sound.EW);
        }
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	return byteArraySerializationVersion;
  }

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepFoulAppearance initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    return this;
  }

}
