package com.fumbbl.ffb.client.state.bb2016;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2016.KtmLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Christer
 */
public class ClientStateKickTeamMate extends AbstractClientStateMove<KtmLogicModule> {

	public ClientStateKickTeamMate(FantasyFootballClientAwt pClient) {
		super(pClient, new KtmLogicModule(pClient));
	}

	public void initUI() {
		super.initUI();
		markKickablePlayers();
	}

	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case PERFORM:
				IconCache iconCache = getClient().getUserInterface().getIconCache();
				List<JMenuItem> menuItemList = new ArrayList<>();

				JMenuItem shortKick = new JMenuItem(dimensionProvider(), "Short Kick",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ, dimensionProvider())));
				shortKick.setMnemonic(IPlayerPopupMenuKeys.KEY_SHORT);
				shortKick.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHORT, 0));
				menuItemList.add(shortKick);

				JMenuItem longKick = new JMenuItem(dimensionProvider(), "Long Kick",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ, dimensionProvider())));
				longKick.setMnemonic(IPlayerPopupMenuKeys.KEY_LONG);
				longKick.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LONG, 0));
				menuItemList.add(longKick);

				createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
				showPopupMenuForPlayer(player);
				break;
			default:
				super.evaluateClick(result, player);
				break;
		}
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		if (logicModule.fieldInteraction(pCoordinate).getKind() == InteractionResult.Kind.PERFORM) {
			super.clickOnField(pCoordinate);
			markKickablePlayers();
			userInterface.getFieldComponent().refresh();
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BLOCK);
				break;
			case RESET:
				UtilClientCursor.setDefaultCursor(userInterface);
				break;
			default:
				break;
		}
		userInterface.refreshSideBars();
		return true;
	}

	private void markKickablePlayers() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?>[] kickablePlayers = UtilPlayer.findKickableTeamMates(game, actingPlayer.getPlayer());
		if ((game.getDefender() == null) && ArrayTool.isProvided(kickablePlayers)) {
			userInterface.getFieldComponent().getLayerRangeRuler().markPlayers(kickablePlayers,
					FieldLayerRangeRuler.COLOR_THROWABLE_PLAYER);
		} else {
			userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		}
		userInterface.getFieldComponent().refresh();
	}

	@Override
	public void leaveState() {
		// clear marked players
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_SHORT, ClientAction.PASS_SHORT);
			put(IPlayerPopupMenuKeys.KEY_LONG, ClientAction.PASS_LONG);
		}};
	}
}
