package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;

import java.awt.Rectangle;

/**
 *
 * @author Kalimar
 */
public class BoxSlot {

	private PlayerState fType;
	private final Rectangle fLocation;
	private Player<?> fPlayer;

	public BoxSlot(Rectangle pLocation, PlayerState pType) {
		fLocation = pLocation;
	}

	public PlayerState getType() {
		return fType;
	}

	public void setType(PlayerState pType) {
		fType = pType;
	}

	public Rectangle getLocation() {
		return fLocation;
	}

	public Player<?> getPlayer() {
		return fPlayer;
	}

	public void setPlayer(Player<?> pPlayer) {
		fPlayer = pPlayer;
	}

	public String getToolTip(Game pGame) {
		if ((pGame != null) && (getPlayer() != null)) {
			StringBuilder toolTip = new StringBuilder();
			toolTip.append("<html>");
			toolTip.append(getPlayer().getName());
			PlayerResult playerResult = pGame.getGameResult().getPlayerResult(getPlayer());
			if (playerResult.getSeriousInjury() != null) {
				toolTip.append(" ").append(playerResult.getSeriousInjury().getDescription()).append(".");
				if (playerResult.getSeriousInjuryDecay() != null) {
					toolTip.append("<br>").append(getPlayer().getName()).append(" ")
							.append(playerResult.getSeriousInjuryDecay().getDescription()).append(".");
				}
			} else {
				PlayerState playerState = pGame.getFieldModel().getPlayerState(getPlayer());
				if (playerState != null) {
					toolTip.append(" ").append(playerState.getDescription()).append(".");
				}
			}
			if (playerResult.getSendToBoxReason() != null) {
				toolTip.append("<br>Player ").append(playerResult.getSendToBoxReason().getReason());
				if (playerResult.getSendToBoxHalf() > 0) {
					toolTip.append(" in turn ").append(playerResult.getSendToBoxTurn());
					if (playerResult.getSendToBoxHalf() > 2) {
						toolTip.append(" of Overtime");
					} else if (playerResult.getSendToBoxHalf() > 1) {
						toolTip.append(" of 2nd half");
					} else {
						toolTip.append(" of 1st half");
					}
				}
				Player<?> attacker = pGame.getPlayerById(playerResult.getSendToBoxByPlayerId());
				if (attacker != null) {
					toolTip.append(" by ").append(attacker.getName());
				}
				toolTip.append(".");
			}
			toolTip.append("</html>");
			return toolTip.toString();
		} else {
			return null;
		}
	}

}
