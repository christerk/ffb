package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public class ClientStateTouchback extends ClientState {
	
	private boolean fTouchbackToAnyField;
    
  protected ClientStateTouchback(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.TOUCHBACK;
  }
    
  public void enterState() {
    super.enterState();
    setSelectable(true);
    // check if there are players on the field to give the ball to
    Game game = getClient().getGame();
    fTouchbackToAnyField = true;
    for (Player player : game.getTeamHome().getPlayers()) {
    	if (isPlayerSelectable(player)) {
    		fTouchbackToAnyField = false;
    		break;
    	}
    }
  }
  
  @Override
  protected boolean mouseOverPlayer(Player pPlayer) {
    super.mouseOverPlayer(pPlayer);
    if (isClickable() && (fTouchbackToAnyField || isPlayerSelectable(pPlayer))) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
    } else {
      UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
    }
    return true;
  }
  
  @Override
  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    super.mouseOverField(pCoordinate);
    if (isClickable() && fTouchbackToAnyField) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
    } else {
      UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
    }
    return true;
  }
  
  @Override
  protected void clickOnPlayer(Player pPlayer) {
  	Game game = getClient().getGame();
    if (isClickable() && (fTouchbackToAnyField || isPlayerSelectable(pPlayer))) {
    	FieldCoordinate touchBackCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
      getClient().getCommunication().sendTouchback(touchBackCoordinate);
    }
  }
  
  @Override
  protected void clickOnField(FieldCoordinate pCoordinate) {
    if (isClickable() && fTouchbackToAnyField) {
      getClient().getCommunication().sendTouchback(pCoordinate);
    }
  }
  
  private boolean isPlayerSelectable(Player pPlayer) {
    boolean selectable = false;
    if (pPlayer != null) {
      Game game = getClient().getGame();
      PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
      selectable = ((playerState != null) && playerState.hasTacklezones() && game.getTeamHome().hasPlayer(pPlayer) && !UtilCards.hasSkill(game, pPlayer, Skill.NO_HANDS));
    }
    return selectable;
  }
        
}
