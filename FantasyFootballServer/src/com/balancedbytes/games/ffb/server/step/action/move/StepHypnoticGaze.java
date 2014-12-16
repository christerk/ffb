package com.balancedbytes.games.ffb.server.step.action.move;

import java.util.Set;

import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.GazeModifierFactory;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
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
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to handle skill HYPNOTIC_GAZE.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 *  
 * @author Kalimar
 */
public class StepHypnoticGaze extends AbstractStepWithReRoll {
	
  private String fGotoLabelOnEnd;
	
	public StepHypnoticGaze(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.HYPNOTIC_GAZE;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						fGotoLabelOnEnd = (String) parameter.getValue();
						break;
					default:
						break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
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
    boolean doGaze = ((actingPlayer.getPlayerAction() == PlayerAction.GAZE) && (game.getDefender() != null));
    if (!doGaze) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
    boolean gotoEndLabel = true;
    if (ReRolledAction.HYPNOTIC_GAZE == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
        doGaze = false;
      }
    } else {
      doGaze = UtilCards.hasUnusedSkill(game, actingPlayer, Skill.HYPNOTIC_GAZE) && !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN);
    }
    if (doGaze) {
      actingPlayer.markSkillUsed(Skill.HYPNOTIC_GAZE);
      int roll = getGameState().getDiceRoller().rollSkill();
      GazeModifierFactory modifierFactory = new GazeModifierFactory();
      Set<GazeModifier> gazeModifiers = modifierFactory.findGazeModifiers(game);
      int minimumRoll = DiceInterpreter.getInstance().minimumRollHypnoticGaze(actingPlayer.getPlayer(), gazeModifiers);
      boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
      boolean reRolled = ((getReRolledAction() == ReRolledAction.HYPNOTIC_GAZE) && (getReRollSource() != null));
      if (!reRolled) {
        getResult().setSound(SoundId.HYPNO);
      }
      getResult().addReport(new ReportSkillRoll(ReportId.HYPNOTIC_GAZE_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, modifierFactory.toArray(gazeModifiers)));
      if (successful) {
        PlayerState oldVictimState = game.getFieldModel().getPlayerState(game.getDefender());
        if (!oldVictimState.isConfused() && !oldVictimState.isHypnotized()) {
          game.getFieldModel().setPlayerState(game.getDefender(), oldVictimState.changeHypnotized(true));
        }
      } else {
        if ((getReRolledAction() != ReRolledAction.HYPNOTIC_GAZE) && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.HYPNOTIC_GAZE, minimumRoll, false)) {
          gotoEndLabel = false;
        }
      }
    }
    if (gotoEndLabel) {
    	publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    }
  }
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    return jsonObject;
  }
  
  @Override
  public StepHypnoticGaze initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    return this;
  }
  	
}
