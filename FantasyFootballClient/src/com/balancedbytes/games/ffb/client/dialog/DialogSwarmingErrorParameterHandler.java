package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.dialog.DialogSwarmingErrorParameter;
import com.balancedbytes.games.ffb.model.Game;

public class DialogSwarmingErrorParameterHandler extends DialogHandler {

  public DialogSwarmingErrorParameterHandler(FantasyFootballClient pClient) {
    super(pClient);
  }

  @Override
  public void showDialog() {
    Game game = getClient().getGame();

    int allowed = ((DialogSwarmingErrorParameter)game.getDialogParameter()).getAllowed();
    int actual = ((DialogSwarmingErrorParameter)game.getDialogParameter()).getActual();

    if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
      SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
      if (!sideBarHome.isBoxOpen()) {
        sideBarHome.openBox(BoxType.RESERVES);
      }

      setDialog(new DialogInformation(getClient(), "Too many swarming players", new String[] { "You placed " +
        actual + " players rather than the allowed " + allowed + "." },
        DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
      getDialog().showDialog(this);

    } else {
      showStatus("Skill Use", "Waiting for coach to place swarming players.", StatusType.WAITING);
    }
  }

  @Override
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
  }
}
