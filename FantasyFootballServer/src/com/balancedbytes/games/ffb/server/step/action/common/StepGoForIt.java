package com.balancedbytes.games.ffb.server.step.action.common;

import java.util.Set;

import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle go for it on blitz.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter INJURY_TYPE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepGoForIt extends AbstractStepWithReRoll {
	
	private boolean fSecondGoForIt;
	private String fGotoLabelOnFailure;

	public StepGoForIt(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.GO_FOR_IT;
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
    ActingPlayer actingPlayer = game.getActingPlayer();
    if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && (getReRolledAction() == null)) {
      game.getTurnData().setBlitzUsed(true);
    	actingPlayer.setCurrentMove(actingPlayer.getCurrentMove() + 1);
      actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game));
    }
    if (actingPlayer.isGoingForIt() && (actingPlayer.getCurrentMove() > actingPlayer.getPlayer().getMovement())) {
      if (ReRolledAction.GO_FOR_IT == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          failGfi();
          return;
        }
      }
      switch (goForIt()) {
        case SUCCESS:
        	succeedGfi();
          return;
        case FAILURE:
        	failGfi();
        	return;
      	default:
        	getResult().setNextAction(StepAction.CONTINUE);
        	return;
      }
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  private void succeedGfi() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
  	if (actingPlayer.isLeaping() && (actingPlayer.getCurrentMove() > actingPlayer.getPlayer().getMovement() + 1) && !fSecondGoForIt) {
  		fSecondGoForIt = true;
  		setReRolledAction(null);
  		getGameState().pushCurrentStepOnStack();
  	}
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  private void failGfi() {
    publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
    publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.DROP_GFI));
    getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
  }
  
  private ActionStatus goForIt() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Set<GoForItModifier> goForItModifiers = GoForItModifier.findGoForItModifiers(game);
    int minimumRoll = DiceInterpreter.getInstance().minimumRollGoingForIt(goForItModifiers);
    int roll = getGameState().getDiceRoller().rollGoingForIt();
    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
    GoForItModifier[] goForItModifierArray = GoForItModifier.toArray(goForItModifiers);
    boolean reRolled = ((getReRolledAction() == ReRolledAction.GO_FOR_IT) && (getReRollSource() != null));
    getResult().addReport(new ReportSkillRoll(ReportId.GO_FOR_IT_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, goForItModifierArray));
    if (successful) {
      return ActionStatus.SUCCESS;
    } else {
      if (getReRolledAction() != ReRolledAction.GO_FOR_IT) {
        setReRolledAction(ReRolledAction.GO_FOR_IT);
        if (UtilCards.hasUnusedSkill(game, actingPlayer, Skill.SURE_FEET)) {
          setReRollSource(ReRollSource.SURE_FEET);
          UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
          return goForIt();
        } else {
          if (UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.GO_FOR_IT, minimumRoll, false)) {
            return ActionStatus.WAITING_FOR_RE_ROLL;
          } else {
            return ActionStatus.FAILURE;
          }
        }
      } else {
        return ActionStatus.FAILURE;
      }
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
  	pByteList.addBoolean(fSecondGoForIt);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	fSecondGoForIt = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.SECOND_GO_FOR_IT.addTo(jsonObject, fSecondGoForIt);
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepGoForIt initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fSecondGoForIt = IServerJsonOption.SECOND_GO_FOR_IT.getFrom(jsonObject);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }

}
