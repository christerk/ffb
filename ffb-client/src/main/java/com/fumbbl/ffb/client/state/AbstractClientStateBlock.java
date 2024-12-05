package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.BlockLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public abstract class AbstractClientStateBlock<T extends BlockLogicModule> extends ClientStateAwt<T> {

  protected final ClientStateBlockExtension extension = new ClientStateBlockExtension();

  protected AbstractClientStateBlock(FantasyFootballClientAwt pClient, T logicModule) {
    super(pClient, logicModule);
  }

  protected void clickOnPlayer(Player<?> pPlayer) {
    InteractionResult result = logicModule.playerInteraction(pPlayer);
    switch (result.getKind()) {
      case SHOW_ACTIONS:
        createAndShowPopupMenuForBlockingPlayer();
        break;
      case SHOW_ACTION_ALTERNATIVES:
        List<JMenuItem> menuItemList = new ArrayList<>();
        ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
        extension.createAndShowBlockOptionsPopupMenu(this, actingPlayer.getPlayer(), pPlayer, false, menuItemList);
      default:
        break;
    }
  }

  protected boolean mouseOverPlayer(Player<?> player) {
    super.mouseOverPlayer(player);
    InteractionResult result = logicModule.playerPeek(player);
    switch (result.getKind()) {
      case PERFORM:
        UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
        return true;
      case RESET:
        UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
        return true;
      default:
        break;
    }
    return true;
  }

  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    super.mouseOverField(pCoordinate);
    UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
    return true;
  }

  public boolean actionKeyPressed(ActionKey pActionKey) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Player<?> player = actingPlayer.getPlayer();
    boolean actionHandled = true;
    if (logicModule.isSufferingBloodLust(actingPlayer)) {
      switch (pActionKey) {
        case PLAYER_SELECT:
          createAndShowPopupMenuForBlockingPlayer();
          break;
        case PLAYER_ACTION_MOVE:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_MOVE);
          break;
        case PLAYER_ACTION_END_MOVE:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
          break;
        case PLAYER_ACTION_LOOK_INTO_MY_EYES:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
          break;
        default:
          actionHandled = false;
          break;
      }
      return actionHandled;
    } else {
      return extension.actionKeyPressed(this, pActionKey);
    }
  }

  @Override
  protected Map<Integer, ClientAction> actionMapping() {
    return new HashMap<Integer, ClientAction>() {{
      put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
      put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
      putAll(genericBlockMapping());
    }};
  }

  private void createAndShowPopupMenuForBlockingPlayer() {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    List<JMenuItem> menuItemList = new ArrayList<>();
    UserInterface userInterface = getClient().getUserInterface();
    IconCache iconCache = userInterface.getIconCache();
    userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
    if (logicModule.isSufferingBloodLust(actingPlayer)) {
      JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE, dimensionProvider())));
      moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
      moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
      menuItemList.add(moveAction);
    }
    addEndActionLabel(iconCache, menuItemList);

    if (logicModule.isTreacherousAvailable(actingPlayer)) {
      menuItemList.add(createTreacherousItem(iconCache));
    }
    if (logicModule.isWisdomAvailable(actingPlayer)) {
      menuItemList.add(createWisdomItem(iconCache));
    }
    if (logicModule.isRaidingPartyAvailable(actingPlayer)) {
      menuItemList.add(createRaidingPartyItem(iconCache));
    }
    if (logicModule.isLookIntoMyEyesAvailable(actingPlayer)) {
      menuItemList.add(createLookIntoMyEyesItem(iconCache));
    }
    if (logicModule.isBalefulHexAvailable(actingPlayer)) {
      menuItemList.add(createBalefulHexItem(iconCache));
    }
    if (logicModule.isBlackInkAvailable(actingPlayer)) {
      menuItemList.add(createBlackInkItem(iconCache));
    }
    if (logicModule.isCatchOfTheDayAvailable(actingPlayer)) {
      menuItemList.add(createCatchOfTheDayItem(iconCache));
    }
    if (logicModule.isThenIStartedBlastinAvailable(actingPlayer)) {
      menuItemList.add(createThenIStartedBlastinItem(iconCache));
    }
    createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
    showPopupMenuForPlayer(actingPlayer.getPlayer());
  }

}
