package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientStateFuriousOutburst extends ClientState {
	public ClientStateFuriousOutburst(FantasyFootballClient pClient) {
		super(pClient);
	}


	@Override
	public ClientStateId getId() {
		return ClientStateId.FURIOUS_OUTBURST;
	}

	@Override
	public void enterState() {
		super.enterState();
	}

	@Override
	public void leaveState() {
		super.leaveState();
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (isEligible(pCoordinate)) {
			getClient().getCommunication().sendFieldCoordinate(pCoordinate);
		}
	}

	@Override
	protected void clickOnPlayer(Player<?> player) {
		ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			createAndShowPopupMenuForPlayer(player);
		}
		super.clickOnPlayer(player);
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return super.mouseOverPlayer(pPlayer);
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (isEligible(pCoordinate)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_FURIOUS);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_FURIOUS);
		}
		return super.mouseOverField(pCoordinate);
	}

	private boolean isEligible(FieldCoordinate coordinate) {
		return getClient().getGame().getFieldModel().getMoveSquare(coordinate) != null;
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();

		addEndActionLabel(iconCache, menuItemList);

		if (!menuItemList.isEmpty()) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					getClient().getCommunication().sendActingPlayer(null, null, false);
					break;
				default:
					break;
			}
		}
	}
}
