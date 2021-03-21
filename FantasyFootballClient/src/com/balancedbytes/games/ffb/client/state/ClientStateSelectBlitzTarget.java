package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PathFinderWithPassBlockSupport;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldComponent;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 *
 * @author Kalimar
 */
public class ClientStateSelectBlitzTarget extends ClientStateMove {

	protected ClientStateSelectBlitzTarget(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_BLITZ_TARGET;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer.equals(actingPlayer.getPlayer()) || (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer))) {
			getClient().getCommunication().sendBlitzTargetSelected(pPlayer.getId());
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		if (moveSquare != null) {
			setCustomCursor(moveSquare);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
			String automoveProperty = getClient().getProperty(IClientProperty.SETTING_AUTOMOVE);
			if ((actingPlayer != null) && (actingPlayer.getPlayerAction() != null)
				&& actingPlayer.getPlayerAction().isMoving() && ArrayTool.isProvided(game.getFieldModel().getMoveSquares())
				&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
				&& (game.getTurnMode() != TurnMode.PASS_BLOCK) && (game.getTurnMode() != TurnMode.KICKOFF_RETURN)
				&& (game.getTurnMode() != TurnMode.SWARMING)
				&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventAutoMove)) {
				FieldCoordinate[] shortestPath = PathFinderWithPassBlockSupport.getShortestPath(game, pCoordinate);
				if (ArrayTool.isProvided(shortestPath)) {
					fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
					fieldComponent.refresh();
				}
			}
		}
		return super.mouseOverField(pCoordinate);
	}

	private void setCustomCursor(MoveSquare pMoveSquare) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pMoveSquare.isGoingForIt() && (pMoveSquare.isDodging() && !actingPlayer.isJumping())) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI_DODGE);
		} else if (pMoveSquare.isGoingForIt()) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI);
		} else if (pMoveSquare.isDodging() && !actingPlayer.isJumping()) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_DODGE);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_MOVE);
		}
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		// clicks on fields are ignored
	}
}
