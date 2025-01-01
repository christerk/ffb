package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.state.logic.BlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public abstract class AbstractClientStateBlitz<T extends BlitzLogicModule> extends AbstractClientStateMove<T> {

	protected final ClientStateBlockExtension extension = new ClientStateBlockExtension();

	protected AbstractClientStateBlitz(FantasyFootballClientAwt client, T logicModule) {
		super(client, logicModule);
	}

	public void initUI() {
		super.initUI();
	}

	protected void evaluateClick(InteractionResult result, Player<?> player) {
		switch (result.getKind()) {
			case SHOW_ACTION_ALTERNATIVES:

				List<JMenuItem> menuItemList = new ArrayList<>();
				if (logicModule.isGoredAvailable(getClient().getGame())) {
					menuItemList.add(createGoredItem());
				}

				ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
				extension.createAndShowBlockOptionsPopupMenu(this, actingPlayer.getPlayer(), player, false, menuItemList);

			default:
				super.evaluateClick(result, player);
				break;
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
				break;
			case RESET:
				UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
				break;
			default:
				break;
		}
		return true;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		return extension.actionKeyPressed(this, pActionKey) || super.actionKeyPressed(pActionKey);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, ClientAction.FUMBLEROOSKIE);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
			put(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, ClientAction.GORED_BY_THE_BULL);
			putAll(genericBlockMapping());
		}};
	}

	@Override
	protected void postPerform(int menuKey) {
		getClient().getUserInterface().getFieldComponent().refresh();
	}


	private JMenuItem createGoredItem() {
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Gored By The Bull",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ, dimensionProvider())));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, 0));
		return menuItem;
	}
}
