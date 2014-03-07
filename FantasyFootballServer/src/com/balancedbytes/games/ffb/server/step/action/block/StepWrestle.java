package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
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
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill WRESTLE.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepWrestle extends AbstractStep {
	
	private Boolean fUsingWrestleAttacker;
	private Boolean fUsingWrestleDefender;
	
	public StepWrestle(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.WRESTLE;
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
          if (Skill.WRESTLE == useSkillCommand.getSkill()) {
            if (fUsingWrestleAttacker == null) {
              fUsingWrestleAttacker = useSkillCommand.isSkillUsed();
            } else {
              fUsingWrestleDefender = useSkillCommand.isSkillUsed();
            }
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
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
    if (fUsingWrestleAttacker == null) {
      if (UtilCards.hasSkill(game, actingPlayer, Skill.WRESTLE) && !attackerState.isRooted() && !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)) {
        UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayer().getId(), Skill.WRESTLE, 0));
      } else {
      	fUsingWrestleAttacker = false;
      }
    }
    if ((fUsingWrestleAttacker != null) && (fUsingWrestleDefender == null)) {
      if (!fUsingWrestleAttacker && UtilCards.hasSkill(game, game.getDefender(), Skill.WRESTLE) && !defenderState.isRooted()
          && !(actingPlayer.getPlayerAction() == PlayerAction.BLITZ && UtilCards.hasSkill(game, actingPlayer, Skill.JUGGERNAUT))) {
        UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), Skill.WRESTLE, 0));
      } else {
      	fUsingWrestleDefender = false;
      }
    }
    if (fUsingWrestleDefender != null) {
      if (fUsingWrestleAttacker) {
        getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), Skill.WRESTLE, true, SkillUse.BRING_DOWN_OPPONENT));
      } else if (fUsingWrestleDefender) {
        getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.WRESTLE, true, SkillUse.BRING_DOWN_OPPONENT));
      } else {
        if (UtilCards.hasSkill(game, actingPlayer, Skill.WRESTLE) || UtilCards.hasSkill(game, game.getDefender(), Skill.WRESTLE)) {
          getResult().addReport(new ReportSkillUse(null, Skill.WRESTLE, false, null));
        }
      }
      if (fUsingWrestleAttacker || fUsingWrestleDefender) {
        publishParameters(UtilServerInjury.dropPlayer(this, actingPlayer.getPlayer()));
        publishParameters(UtilServerInjury.dropPlayer(this, game.getDefender()));
        if (UtilCards.hasSkill(game, game.getDefender(), Skill.BALL_AND_CHAIN)) {
        	FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
          publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
          	UtilServerInjury.handleInjury(this, InjuryType.BALL_AND_CHAIN, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER)));
        }
      }
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fUsingWrestleAttacker = pByteArray.getBoolean();
  	fUsingWrestleDefender = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.USING_WRESTLE_ATTACKER.addTo(jsonObject, fUsingWrestleAttacker);
    IServerJsonOption.USING_WRESTLE_DEFENDER.addTo(jsonObject, fUsingWrestleDefender);
    return jsonObject;
  }
  
  @Override
  public StepWrestle initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fUsingWrestleAttacker = IServerJsonOption.USING_WRESTLE_ATTACKER.getFrom(jsonObject);
    fUsingWrestleDefender = IServerJsonOption.USING_WRESTLE_DEFENDER.getFrom(jsonObject);
    return this;
  }

}
