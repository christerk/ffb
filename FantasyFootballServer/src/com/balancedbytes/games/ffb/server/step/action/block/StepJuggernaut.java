package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill JUGGERNAUT.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter BLOCK_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepJuggernaut extends AbstractStep {
	
	private Boolean fUsingJuggernaut;
	private PlayerState fOldDefenderState;
	private String fGotoLabelOnSuccess;
	
	public StepJuggernaut(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.JUGGERNAUT;
	}
	
	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_SUCCESS:
						fGotoLabelOnSuccess = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnSuccess)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
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
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_USE_SKILL:
          ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
          if (Skill.JUGGERNAUT == useSkillCommand.getSkill()) {
            fUsingJuggernaut = useSkillCommand.isSkillUsed();
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        default:
          break;
      }
    }
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case OLD_DEFENDER_STATE:
					fOldDefenderState = (PlayerState) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && UtilCards.hasSkill(game, actingPlayer, Skill.JUGGERNAUT) && !fOldDefenderState.isRooted()) {
      if (fUsingJuggernaut == null) {
        UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayer().getId(), Skill.JUGGERNAUT, 0));
      } else {
        if (fUsingJuggernaut) {
          getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), Skill.JUGGERNAUT, true, SkillUse.PUSH_BACK_OPPONENT));
          publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, BlockResult.PUSHBACK));
          game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
          publishParameters(UtilBlockSequence.initPushback(this));
          getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnSuccess);
        } else {
          getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), Skill.JUGGERNAUT, false, null));
          getResult().setNextAction(StepAction.NEXT_STEP);
        }
      }
    } else {
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fUsingJuggernaut = pByteArray.getBoolean();
  	int playerStateId = pByteArray.getSmallInt();
  	fOldDefenderState = (playerStateId > 0) ? new PlayerState(playerStateId) : null;
  	fGotoLabelOnSuccess = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.USING_JUGGERNAUT.addTo(jsonObject, fUsingJuggernaut);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
    IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
    return jsonObject;
  }
  
  @Override
  public StepJuggernaut initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fUsingJuggernaut = IServerJsonOption.USING_JUGGERNAUT.getFrom(jsonObject);
    fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(jsonObject);
    return this;
  }

}
