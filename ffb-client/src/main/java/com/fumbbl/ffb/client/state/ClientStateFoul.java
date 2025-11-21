package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.FoulLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateFoul extends AbstractClientStateMove<FoulLogicModule> {

  private Player<?> target;

  public ClientStateFoul(FantasyFootballClientAwt client) {
    super(client, new FoulLogicModule(client));
  }

  @Override
  public void setUp() {
    target = null;
  }

  @Override
  protected void evaluateClick(InteractionResult result, Player<?> player) {
    super.evaluateClick(result, player);
    switch (result.getKind()) {
      case SELECT_ACTION:
        target = player;
        break;
      default:
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
        case SELECT_ACTION:
          super.evaluateClick(result, defender.get());
          actionHandled = true;
          break;
        default:
          actionHandled = false;
          break;
      }
    } else {
      actionHandled = super.actionKeyPressed(pActionKey);
    }

    return actionHandled;
  }

  public boolean mouseOverPlayer(Player<?> player) {
    super.mouseOverPlayer(player);
    InteractionResult result = logicModule.playerPeek(player);
    determineCursor(result);
    return true;
  }

  @Override
  protected String validCursor() {
    return IIconProperty.CURSOR_FOUL;
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
      put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, ClientAction.THEN_I_STARTED_BLASTIN);
    }};
  }

  @Override
  protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
    LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = super.itemConfigs(actionContext);
    itemConfigs.put(ClientAction.FOUL, new MenuItemConfig("Foul Opponent", IIconProperty.ACTION_FOUL, IPlayerPopupMenuKeys.KEY_FOUL));
    itemConfigs.put(ClientAction.CHAINSAW, new MenuItemConfig("Chainsaw", IIconProperty.ACTION_CHAINSAW, IPlayerPopupMenuKeys.KEY_CHAINSAW));
    return itemConfigs;
  }

  @Override
  public void actionPerformed(ActionEvent pActionEvent) {
    JMenuItem menuItem = (JMenuItem) (pActionEvent.getSource());
    menuItemSelected(target, menuItem.getMnemonic());
  }
}
