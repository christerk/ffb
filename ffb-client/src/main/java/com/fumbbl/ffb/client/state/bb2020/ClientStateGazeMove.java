package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.logic.bb2020.GazeMoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateGazeMove extends AbstractClientStateMove<GazeMoveLogicModule> {
  public ClientStateGazeMove(FantasyFootballClientAwt client) {
    super(client, new GazeMoveLogicModule((client)));
  }

  @Override
  public boolean mouseOverPlayer(Player<?> player) {
    boolean result = super.mouseOverPlayer(player);
    determineCursor(logicModule.playerPeek(player));
    return result;
  }

  @Override
  protected String validCursor() {
    return IIconProperty.CURSOR_GAZE;
  }

  @Override
  public void clickOnPlayer(Player<?> player) {
    InteractionResult result= logicModule.playerInteraction(player);
    evaluateClick(result, player);
  }
}
