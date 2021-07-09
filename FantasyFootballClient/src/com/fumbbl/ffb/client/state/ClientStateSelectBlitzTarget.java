package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.IClientPropertyValue;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

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
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_BLOCK);
		}

		showShortestPath(game.getFieldModel().getPlayerCoordinate(pPlayer), game, fieldComponent, actingPlayer);

		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();

		UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_BLOCK);

		showShortestPath(pCoordinate, game, fieldComponent, actingPlayer);

		return true;
	}

	private void showShortestPath(FieldCoordinate pCoordinate, Game game, FieldComponent fieldComponent,
			ActingPlayer actingPlayer) {
		String automoveProperty = getClient().getProperty(IClientProperty.SETTING_AUTOMOVE);
		if (actingPlayer != null
			&& actingPlayer.getPlayerAction() != null
			&& actingPlayer.getPlayerAction().isMoving()
			&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
			&& game.getTurnMode() != TurnMode.PASS_BLOCK
			&& game.getTurnMode() != TurnMode.KICKOFF_RETURN
			&& game.getTurnMode() != TurnMode.SWARMING
			&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventAutoMove)
		) {
			
			FieldCoordinate[] shortestPath = null;
			
			Player playerInTarget = game.getFieldModel().getPlayer(pCoordinate);
			
			if (playerInTarget != null && playerInTarget.getTeam() != actingPlayer.getPlayer().getTeam()) {
				shortestPath = PathFinderWithPassBlockSupport.getShortestPathToPlayer(game, playerInTarget);
			} else {
				shortestPath = PathFinderWithPassBlockSupport.getShortestPath(game, pCoordinate);
			}
			if (ArrayTool.isProvided(shortestPath)) {
				fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
				fieldComponent.refresh();
			}
		}
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		// clicks on fields are ignored
	}
}
