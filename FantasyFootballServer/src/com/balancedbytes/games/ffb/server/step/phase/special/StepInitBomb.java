package com.balancedbytes.games.ffb.server.step.phase.special;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportBombOutOfBounds;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * Initialization step of the pass sequence.
 * May push SpecialEffect sequences onto the stack.
 * 
 * Needs to be initialized with stepParameter CATCHER_ID.
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * May be initialized with stepParameter OLD_TURN_MODE.
 * Needs to be initialized with stepParameter PASS_FUMBLE.
 * 
 * Sets stepParameter CATCHER_ID for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter OLD_TURN_MODE for all steps on the stack.
 *
 * @author Kalimar
 */
public final class StepInitBomb extends AbstractStep {

	protected String fGotoLabelOnEnd;
	protected String fCatcherId;
	protected boolean fPassFumble;
	protected FieldCoordinate fBombCoordinate;
	
	public StepInitBomb(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_BOMB;
	}
	
	@Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // optional
  				case CATCHER_ID:
  					fCatcherId = (String) parameter.getValue();
  					break;
  			  // mandatory
  				case GOTO_LABEL_ON_END:
  					fGotoLabelOnEnd = (String) parameter.getValue();
  					break;
  			  // mandatory
  				case PASS_FUMBLE:
  					fPassFumble = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    game.getFieldModel().setRangeRuler(null);
    if (fPassFumble) {
    	fCatcherId = null;
    }
    if (fCatcherId == null) {
    	fBombCoordinate = game.getFieldModel().getBombCoordinate();
    	if (fBombCoordinate == null) {
    		getResult().addReport(new ReportBombOutOfBounds());
    	} else {
      	game.getFieldModel().setBombCoordinate(null);
  			getResult().setAnimation(new Animation(AnimationType.BOMB_EXLOSION, fBombCoordinate));
  			UtilGame.syncGameModel(this);
  			game.getFieldModel().add(new BloodSpot(fBombCoordinate, new PlayerState(PlayerState.HIT_BY_BOMB)));
  			List<Player> affectedPlayers = new ArrayList<Player>();
  			FieldCoordinate[] targetCoordinates = game.getFieldModel().findAdjacentCoordinates(fBombCoordinate, FieldCoordinateBounds.FIELD, 1, true);
    		for (int i = targetCoordinates.length - 1; i >= 0; i--) {
  				Player player = game.getFieldModel().getPlayer(targetCoordinates[i]);
  				if (player != null) {
  					affectedPlayers.add(player);
  				}
  			}
      	if (affectedPlayers.size() > 0) {
      		for (Player player : affectedPlayers) {
      			boolean rollForEffect = !fBombCoordinate.equals(game.getFieldModel().getPlayerCoordinate(player));
      			SequenceGenerator.getInstance().pushSpecialEffectSequence(getGameState(), SpecialEffect.BOMB, player.getId(), rollForEffect);
      		}
      	}
    	}
    	leaveStep(null);
    } else {
    	leaveStep(fGotoLabelOnEnd);
    }
	}
  
  private void leaveStep(String pGotoLabel) {
		publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, fCatcherId));
		if (StringTool.isProvided(pGotoLabel)) {
  		getResult().setNextAction(StepAction.GOTO_LABEL, pGotoLabel);
		} else {
	  	getResult().setNextAction(StepAction.NEXT_STEP);
		}
  }

  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addString(fCatcherId);
  	pByteList.addBoolean(fPassFumble);
  	pByteList.addFieldCoordinate(fBombCoordinate);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fCatcherId = pByteArray.getString();
  	fPassFumble = pByteArray.getBoolean();
  	fBombCoordinate = pByteArray.getFieldCoordinate();
  	return byteArraySerializationVersion;
  }

}
