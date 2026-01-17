package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2025.BlockLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateBlock extends AbstractClientStateBlock<BlockLogicModule> {

  public ClientStateBlock(FantasyFootballClientAwt pClient) {
    super(pClient, new BlockLogicModule(pClient));
  }

  @Override
  protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
    return new HashMap<Integer, ClientAction>() {{
      put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
      put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
      put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
      put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
      put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
      put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
      put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
      put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
      put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
      put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, ClientAction.THEN_I_STARTED_BLASTIN);
      put(IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT, ClientAction.AUTO_GAZE_ZOAT);
    }};
  }

  public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Player<?> player = actingPlayer.getPlayer();
    boolean actionHandled = true;
    if (logicModule.isSufferingBloodLust(actingPlayer)) {
      switch (pActionKey) {
        case PLAYER_SELECT:
          clickOnPlayer(player);
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
          actionHandled = handleResize(pActionKey);
          break;
      }
      return actionHandled;
    } else {
      switch (pActionKey) {
        case PLAYER_ACTION_BLOCK:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLOCK);
          break;
        case PLAYER_ACTION_TREACHEROUS:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
          break;
        case PLAYER_ACTION_WISDOM:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
          break;
        case PLAYER_ACTION_RAIDING_PARTY:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
          break;
        case PLAYER_ACTION_BALEFUL_HEX:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
          break;
        case PLAYER_ACTION_GORED:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
          return true;
        case PLAYER_ACTION_BLACK_INK:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
          return true;
        case PLAYER_ACTION_THEN_I_STARTED_BLASTIN:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
          return true;
        case PLAYER_ACTION_AUTO_GAZE_ZOAT:
          menuItemSelected(player, IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT);
          return true;
        default:
          if (handleResize(pActionKey)) {
            return true;
          }
          FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(player);
          FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(playerPosition,
            pActionKey);
          Player<?> defender = game.getFieldModel().getPlayer(moveCoordinate);
          clickOnPlayer(defender);
          return true;
      }
      return false;
    }
  }

  @Override
  protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
    LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

    itemConfigs.put(ClientAction.MOVE, new MenuItemConfig("Move", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_MOVE));
    itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
    itemConfigs.put(ClientAction.TREACHEROUS, new MenuItemConfig("Treacherous", IIconProperty.ACTION_STAB, IPlayerPopupMenuKeys.KEY_TREACHEROUS));
    itemConfigs.put(ClientAction.WISDOM, new MenuItemConfig("Wisdom of the White Dwarf", IIconProperty.ACTION_WISDOM, IPlayerPopupMenuKeys.KEY_WISDOM));
    itemConfigs.put(ClientAction.RAIDING_PARTY, new MenuItemConfig("Raiding Party", IIconProperty.ACTION_RAIDING_PARTY, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY));
    itemConfigs.put(ClientAction.LOOK_INTO_MY_EYES, new MenuItemConfig("Look Into My Eyes", IIconProperty.ACTION_LOOK_INTO_MY_EYES, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES));
    itemConfigs.put(ClientAction.BALEFUL_HEX, new MenuItemConfig("Baleful Hex", IIconProperty.ACTION_BALEFUL_HEX, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX));
    itemConfigs.put(ClientAction.BLACK_INK, new MenuItemConfig("Black Ink", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_BLACK_INK));
    itemConfigs.put(ClientAction.CATCH_OF_THE_DAY, new MenuItemConfig("Catch of the Day", IIconProperty.ACTION_CATCH_OF_THE_DAY, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY));
    itemConfigs.put(ClientAction.THEN_I_STARTED_BLASTIN, new MenuItemConfig("\"Then I Started Blastin'!\"", IIconProperty.ACTION_STARTED_BLASTIN, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN));
    itemConfigs.put(ClientAction.BLOCK, new MenuItemConfig("Block Opponent", IIconProperty.ACTION_BLOCK, IPlayerPopupMenuKeys.KEY_BLOCK));
    itemConfigs.put(ClientAction.AUTO_GAZE_ZOAT, new MenuItemConfig("\"Excuse Me, Are You a Zoat?\"", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT));

    return itemConfigs;

  }
}
