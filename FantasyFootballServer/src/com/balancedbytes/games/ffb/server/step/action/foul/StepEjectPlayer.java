package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in foul sequence to handle ejecting a spotted fouler.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * 
 * Expects stepParameter FOULER_HAS_BALL to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepEjectPlayer extends AbstractStep {
	
	private String fGotoLabelOnEnd;
	private Boolean fFoulerHasBall;
	
	public StepEjectPlayer(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.EJECT_PLAYER;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case FOULER_HAS_BALL:
					fFoulerHasBall = (Boolean) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
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
    GameResult gameResult = game.getGameResult();
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    PlayerResult attackerResult = gameResult.getPlayerResult(actingPlayer.getPlayer()); 
    if (UtilCards.hasSkill(game, actingPlayer, Skill.SNEAKY_GIT) && game.getOptions().getOptionValue(GameOption.SNEAKY_GIT_BAN_TO_KO).isEnabled()) {
    	game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.KNOCKED_OUT));
    } else {
    	game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.BANNED));
    }
    attackerResult.setSendToBoxReason(SendToBoxReason.FOUL_BAN);
    attackerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
    attackerResult.setSendToBoxHalf(game.getHalf());
    UtilBox.putPlayerIntoBox(game, actingPlayer.getPlayer());
    UtilBox.refreshBoxes(game);
    UtilGame.updateLeaderReRolls(this);
  	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
    if ((fFoulerHasBall != null) && fFoulerHasBall) {
    	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
    	getResult().setNextAction(StepAction.NEXT_STEP);
    } else  {
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    }
  }
	
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addBoolean(fFoulerHasBall);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fFoulerHasBall = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = toJsonValueTemp();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.FOULER_HAS_BALL.addTo(jsonObject, fFoulerHasBall);
    return jsonObject;
  }
  
  public StepEjectPlayer initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fFoulerHasBall = IServerJsonOption.FOULER_HAS_BALL.getFrom(jsonObject);
    return this;
  }

}
