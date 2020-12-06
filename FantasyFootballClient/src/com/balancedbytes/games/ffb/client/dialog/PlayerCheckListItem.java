package com.balancedbytes.games.ffb.client.dialog;

import javax.swing.Icon;

import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public final class PlayerCheckListItem {

	private Player fPlayer;
	private Icon fIcon;
	private String fText;
	private boolean fSelected = false;

	public PlayerCheckListItem(Player pPlayer, Icon pIcon, String pText) {
		fPlayer = pPlayer;
		fIcon = pIcon;
		fText = pText;
		setSelected(false);
	}

	public boolean isSelected() {
		return fSelected;
	}

	public void setSelected(boolean pSelected) {
		fSelected = pSelected;
	}

	public Player getPlayer() {
		return fPlayer;
	}

	public Icon getIcon() {
		return fIcon;
	}

	public String getText() {
		return fText;
	}

}
