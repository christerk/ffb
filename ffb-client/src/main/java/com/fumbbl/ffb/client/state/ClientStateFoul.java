package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.FoulLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kalimar
 */
public class ClientStateFoul extends AbstractClientStateMove<FoulLogicModule> {

  protected ClientStateFoul(FantasyFootballClientAwt client) {
    super(client, new FoulLogicModule(client));
  }

  public void clickOnPlayer(Player<?> player) {
    InteractionResult result = logicModule.playerInteraction(player);
    switch (result.getKind()) {

      case SHOW_ACTION_ALTERNATIVES:
        createAndShowBlockOptionsPopupMenu(logicModule.getActingPlayer().getPlayer(), player);
        break;
      case SHOW_BLOODLUST_ACTIONS:
        createAndShowPopupMenuForBloodLustPlayer();
        break;
      case SHOW_ACTIONS:
        createAndShowPopupMenuForActingPlayer();
        break;
      default:
        super.evaluateClick(result, player);
        break;

    }
  }

  public boolean actionKeyPressed(ActionKey pActionKey) {
    boolean actionHandled;
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();

    FieldCoordinate playerPosition = logicModule.getCoordinate(actingPlayer.getPlayer());
    FieldCoordinate defenderPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition,
      pActionKey);
    Optional<Player<?>> defender = logicModule.getPlayer(defenderPosition);
    if (defender.isPresent()) {
      InteractionResult result = logicModule.playerSelected(defender.get());

      switch (result.getKind()) {
        case HANDLED:
          actionHandled = true;
          break;
        case SHOW_ACTION_ALTERNATIVES:
          createAndShowBlockOptionsPopupMenu(actingPlayer.getPlayer(), defender.get());
          actionHandled = true;
          break;
        case IGNORE:
        default:
          actionHandled = false;
          break;
      }
    } else {
      actionHandled = super.actionKeyPressed(pActionKey);
    }

    return actionHandled;
  }

  protected boolean mouseOverPlayer(Player<?> player) {
    super.mouseOverPlayer(player);
    InteractionResult result = logicModule.playerPeek(player);
    switch (result.getKind()) {
      case PERFORM:
        UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_FOUL);
        break;
      default:
        break;
    }
    return true;
  }

  @Override
  protected Map<Integer, ClientAction> actionMapping() {
    return new HashMap<Integer, ClientAction>() {{
      put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
      put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
      put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
      put(IPlayerPopupMenuKeys.KEY_FOUL, ClientAction.FOUL);
      put(IPlayerPopupMenuKeys.KEY_CHAINSAW, ClientAction.CHAINSAW);
      put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
      put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
      put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
      put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
      put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
      put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
      put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, ClientAction.CATCH_OF_THE_DAY);
      put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
    }};
  }

  private void createAndShowBlockOptionsPopupMenu(Player<?> attacker, Player<?> defender) {
    IconCache iconCache = getClient().getUserInterface().getIconCache();
    List<JMenuItem> menuItemList = new ArrayList<>();
    if (attacker.hasSkillProperty(NamedProperties.providesChainsawFoulingAlternative)) {
      JMenuItem chainsawAction = new JMenuItem(dimensionProvider(), "Chainsaw",
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_CHAINSAW, dimensionProvider())));
      chainsawAction.setMnemonic(IPlayerPopupMenuKeys.KEY_CHAINSAW);
      chainsawAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CHAINSAW, 0));
      menuItemList.add(chainsawAction);
    }
    JMenuItem foulAction = new JMenuItem(dimensionProvider(), "Foul Opponent",
      new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_FOUL, dimensionProvider())));
    foulAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FOUL);
    foulAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FOUL, 0));
    menuItemList.add(foulAction);
    createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
    showPopupMenuForPlayer(defender);
  }

  protected void createAndShowPopupMenuForBloodLustPlayer() {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (actingPlayer.isSufferingBloodLust()) {
      UserInterface userInterface = getClient().getUserInterface();
      IconCache iconCache = userInterface.getIconCache();
      userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
      List<JMenuItem> menuItemList = new ArrayList<>();
      JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE, dimensionProvider())));
      moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
      moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
      menuItemList.add(moveAction);
      JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), "End Move",
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE, dimensionProvider())));
      endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
      endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
      menuItemList.add(endMoveAction);
      createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
      showPopupMenuForPlayer(actingPlayer.getPlayer());
    }
  }

}
