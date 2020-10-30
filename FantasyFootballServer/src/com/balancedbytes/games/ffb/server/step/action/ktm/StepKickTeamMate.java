package com.balancedbytes.games.ffb.server.step.action.ktm;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportKickTeamMateRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in ttm sequence to actual throw the team mate.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * 
 * Pushes new scatterPlayerSequence on the stack.
 * 
 * @author Kalimar
 */
public final class StepKickTeamMate extends AbstractStepWithReRoll {
	
  private String fGotoLabelOnFailure;
  private String fKickedPlayerId;
  private PlayerState fKickedPlayerState;
  private boolean fKickedPlayerHasBall;
  private int fNumDice;
  private int fDistance;
  private int[] fRolls;
	
	public StepKickTeamMate(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.KICK_TEAM_MATE;
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
  	if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
  }

  @Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case KICKED_PLAYER_ID:
					fKickedPlayerId = (String) pParameter.getValue();
					return true;
				case KICKED_PLAYER_STATE:
					fKickedPlayerState = (PlayerState) pParameter.getValue();
					return true;
				case KICKED_PLAYER_HAS_BALL:
					fKickedPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					return true;
				case NR_OF_DICE:
				  fNumDice = (pParameter.getValue() != null) ? Math.max(0, Math.min((Integer) pParameter.getValue(), 2)) : 0;
				  break;
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
    actingPlayer.setHasBlocked(true);
    game.setConcessionPossible(false);
    game.getTurnData().setBlitzUsed(true);
    UtilServerDialog.hideDialog(getGameState());
    Player kicker = game.getActingPlayer().getPlayer();
    boolean doRoll = true;
    if (ReRolledAction.KICK_TEAM_MATE == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), kicker)) {
        FieldCoordinate kickerCoordinate = game.getFieldModel().getPlayerCoordinate(kicker);
        Player kickedPlayer = game.getPlayerById(fKickedPlayerId);
        boolean successful = fNumDice == 1 || fRolls[0] != fRolls[1];
        
        executeKick(kickedPlayer, kickerCoordinate, successful);
        
        doRoll = false;
      }
    }
    if (doRoll) {
      Player kickedPlayer = game.getPlayerById(fKickedPlayerId);
      FieldCoordinate kickerCoordinate = game.getFieldModel().getPlayerCoordinate(kicker);
      FieldCoordinate kickedPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(kickedPlayer);
 
      Direction d = FieldCoordinate.GetDirection(kickerCoordinate, kickedPlayerCoordinate);
      
      fRolls = new int[fNumDice];
      for (int i=0; i<fNumDice; i++) {
        fRolls[i] = getGameState().getDiceRoller().rollSkill();
      }
      
      boolean successful = fNumDice == 1 || fRolls[0] != fRolls[1];
      fDistance = fRolls[0] + (fNumDice > 1 ? fRolls[1] : 0);

      FieldCoordinate targetCoordinate = kickedPlayerCoordinate;
      targetCoordinate = targetCoordinate.move(d, fDistance);
      game.setPassCoordinate(targetCoordinate);
      
      boolean reRolled = ((getReRolledAction() == ReRolledAction.KICK_TEAM_MATE) && (getReRollSource() != null));
      getResult().addReport(new ReportKickTeamMateRoll(kicker.getId(), kickedPlayer.getId(), successful, fRolls, reRolled, fDistance));

      boolean act = false;
      
      boolean allowKtmReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_KTM_REROLL);
      
      if (allowKtmReroll && getReRolledAction() != ReRolledAction.KICK_TEAM_MATE) {
        setReRolledAction(ReRolledAction.KICK_TEAM_MATE);
        if (!UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.KICK_TEAM_MATE, 0, false)) {
          act = true;
        }
      } else {
        act = true;
      }

      if (act) {
        executeKick(kickedPlayer, kickerCoordinate, successful);
      }
    }
  }

  private void executeKick(Player kickedPlayer, FieldCoordinate kickerCoordinate, boolean successful) {
    if (successful) {
      Game game = getGameState().getGame();
      boolean hasSwoop = kickedPlayer != null && kickedPlayer.hasSkill(ServerSkill.SWOOP);
      game.getFieldModel().setPlayerState(game.getDefender(), fKickedPlayerState.changeBase(PlayerState.PICKED_UP));
      SequenceGenerator.getInstance().pushScatterPlayerSequence(getGameState(), fKickedPlayerId, fKickedPlayerState, fKickedPlayerHasBall, kickerCoordinate, hasSwoop, true);
      publishParameter(new StepParameter(StepParameterKey.IS_KICKED_PLAYER, true));
      if (fDistance >= 9) {
        publishParameter(new StepParameter(StepParameterKey.KTM_MODIFIER, -2));
      } else if (fDistance >= 6) {
        publishParameter(new StepParameter(StepParameterKey.KTM_MODIFIER, -1));
      }
      getResult().setNextAction(StepAction.NEXT_STEP);
    } else {
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
    }
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    IServerJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
    IServerJsonOption.KICKED_PLAYER_STATE.addTo(jsonObject, fKickedPlayerState);
    IServerJsonOption.KICKED_PLAYER_HAS_BALL.addTo(jsonObject, fKickedPlayerHasBall);
    return jsonObject;
  }
  
  @Override
  public StepKickTeamMate initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    fKickedPlayerId = IServerJsonOption.KICKED_PLAYER_ID.getFrom(jsonObject);
    fKickedPlayerState = IServerJsonOption.KICKED_PLAYER_STATE.getFrom(jsonObject);
    fKickedPlayerHasBall = IServerJsonOption.KICKED_PLAYER_HAS_BALL.getFrom(jsonObject);
    return this;
  }
}
