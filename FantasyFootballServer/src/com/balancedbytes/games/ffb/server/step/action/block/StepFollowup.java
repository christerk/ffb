package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogFollowupChoiceParameter;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFollowupChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilPlayerMove;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle followup.
 * 
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * Expects stepParameter FOLLOWUP_CHOICE to be set by a preceding step.
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter COORDINATE_FROM for all steps on the stack.
 * Sets stepParameter FOLLOWUP_CHOICE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepFollowup extends AbstractStep {
	
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fDefenderPosition;
	private Boolean fUsingFend;
	private Boolean fFollowupChoice;
	private PlayerState fOldDefenderState;
	
	public StepFollowup(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.FOLLOWUP;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
		    case CLIENT_USE_SKILL:
		      ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pNetCommand;
		      if (Skill.FEND == useSkillCommand.getSkill()) {
		      	fUsingFend = useSkillCommand.isSkillUsed();
		        commandStatus = StepCommandStatus.EXECUTE_STEP;
		      }
		      break;
		    case CLIENT_FOLLOWUP_CHOICE:
		      ClientCommandFollowupChoice followupChoiceCommand = (ClientCommandFollowupChoice) pNetCommand;
		      publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, followupChoiceCommand.isChoiceFollowup()));
	        commandStatus = StepCommandStatus.EXECUTE_STEP;
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
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) pParameter.getValue();
					return true;
				case DEFENDER_POSITION:
					fDefenderPosition = (FieldCoordinate) pParameter.getValue();
					return true;
				case FOLLOWUP_CHOICE:
					fFollowupChoice = (Boolean) pParameter.getValue();
					return true;
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
    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    if (attackerState.isRooted()) {
  		publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
    }
    if (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK) {
  		publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
    }
    if (fFollowupChoice == null) {
    	PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
//    	FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
//      if (UtilCards.hasSkill(game, game.getDefender(), Skill.FEND) && FieldCoordinateBounds.FIELD.isInBounds(defenderCoordinate) && !defenderState.isProne() && !((fOldDefenderState != null) && fOldDefenderState.isProne())) {
      if (UtilCards.hasSkill(game, game.getDefender(), Skill.FEND) && !defenderState.isProne() && !((fOldDefenderState != null) && fOldDefenderState.isProne())) {
        if (fUsingFend == null) {
				  if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && UtilCards.hasSkill(game, actingPlayer, Skill.JUGGERNAUT)) {
				  	fUsingFend = false;
				    getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), Skill.JUGGERNAUT, true, SkillUse.CANCEL_FEND));
				  }
				}
        if (fUsingFend == null) {
          UtilDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), Skill.FEND, 0));
        } else {
          if (fUsingFend) {
          	publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
          }
          getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.FEND, fUsingFend, SkillUse.STAY_AWAY_FROM_OPPONENT));
        }
      } else {
      	fUsingFend = false;
      }
      if ((fUsingFend != null) && !fUsingFend && (UtilCards.hasSkill(game, actingPlayer, Skill.FRENZY) || UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN))) {
      	publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, true));
      }
      if ((fFollowupChoice == null) && (fUsingFend != null)) {
        UtilDialog.showDialog(getGameState(), new DialogFollowupChoiceParameter());
      }
    }
    if (fFollowupChoice != null) {
      TrackNumber trackNumber = null;
      FieldCoordinate followupCoordinate = null;
      if (fFollowupChoice) {
        followupCoordinate = fDefenderPosition;
        publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())));
        game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), followupCoordinate);
        UtilPlayerMove.updateMoveSquares(getGameState(), false);
        if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
          trackNumber = new TrackNumber(fCoordinateFrom, actingPlayer.getCurrentMove() - 1);
          game.getFieldModel().add(trackNumber);
        }
        getResult().setSound(Sound.STEP);
      } else {
      	publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, null));
      }
      publishParameter(new StepParameter(StepParameterKey.DEFENDER_POSITION, game.getFieldModel().getPlayerCoordinate(game.getDefender())));
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addFieldCoordinate(fCoordinateFrom);
  	pByteList.addFieldCoordinate(fDefenderPosition);
  	pByteList.addBoolean(fUsingFend);
  	pByteList.addBoolean(fFollowupChoice);
  	pByteList.addSmallInt((fOldDefenderState != null) ? fOldDefenderState.getId() : 0);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fCoordinateFrom = pByteArray.getFieldCoordinate();
  	fDefenderPosition = pByteArray.getFieldCoordinate();
  	fUsingFend = pByteArray.getBoolean();
  	fFollowupChoice = pByteArray.getBoolean();
  	int oldDefenderStateId = pByteArray.getSmallInt();
  	fOldDefenderState = (oldDefenderStateId > 0) ? new PlayerState(oldDefenderStateId) : null;
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = toJsonValueTemp();
    IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
    IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, fDefenderPosition);
    IServerJsonOption.USING_FEND.addTo(jsonObject, fUsingFend);
    IServerJsonOption.FOLLOWUP_CHOICE.addTo(jsonObject, fFollowupChoice);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
    return jsonObject;
  }
  
  public StepFollowup initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    fDefenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(jsonObject);
    fUsingFend = IServerJsonOption.USING_FEND.getFrom(jsonObject);
    fFollowupChoice = IServerJsonOption.FOLLOWUP_CHOICE.getFrom(jsonObject);
    fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    return this;
  }

}
