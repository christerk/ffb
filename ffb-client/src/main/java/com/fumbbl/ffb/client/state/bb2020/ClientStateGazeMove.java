package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.logic.bb2020.GazeMoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;

public class ClientStateGazeMove extends AbstractClientStateMove<GazeMoveLogicModule> {
  public ClientStateGazeMove(FantasyFootballClientAwt client) {
    super(client, new GazeMoveLogicModule((client)));
  }

  @Override
  public ClientStateId getId() {
    return ClientStateId.GAZE_MOVE;
  }

  @Override
  protected boolean mouseOverPlayer(Player<?> player) {
    boolean result = super.mouseOverPlayer(player);

    if (logicModule.playerPeek(player).getKind() == InteractionResult.Kind.PERFORM) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GAZE);
    }

    return result;
  }

  @Override
  protected void clickOnPlayer(Player<?> player) {

    logicModule.playerInteraction(player);

    super.clickOnPlayer(player);
  }
}
