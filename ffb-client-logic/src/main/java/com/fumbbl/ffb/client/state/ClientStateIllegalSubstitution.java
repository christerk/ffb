package com.fumbbl.ffb.client.state;

import java.util.HashSet;
import java.util.Set;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class ClientStateIllegalSubstitution extends ClientStateSetup {

	private Set<Player<?>> fFieldPlayers;

	protected ClientStateIllegalSubstitution(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.ILLEGAL_SUBSTITUTION;
	}

	@Override
	public void initUI() {
		super.initUI();
		Game game = getClient().getGame();
		fFieldPlayers = new HashSet<>();
		for (Player<?> player : game.getTeamHome().getPlayers()) {
			if (!game.getFieldModel().getPlayerCoordinate(player).isBoxCoordinate()) {
				fFieldPlayers.add(player);
			}
		}
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			Game game = getClient().getGame();
			Player<?> draggedPlayer = game.getFieldModel().getPlayer(pCoordinate);
			if (draggedPlayer != null) {
				if (pCoordinate.isBoxCoordinate()) {
					for (FieldCoordinate coordinate : FieldCoordinateBounds.ENDZONE_HOME.fieldCoordinates()) {
						Player<?> player = game.getFieldModel().getPlayer(coordinate);
						if ((player != null) && game.getTeamHome().hasPlayer(player) && !fFieldPlayers.contains(player)) {
							return false;
						}
					}
					return true;
				} else {
					return !fFieldPlayers.contains(draggedPlayer);
				}
			}
		}
		return false;
	}

	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate == null) {
			return false;
		}
		Game game = getClient().getGame();
		ClientData clientData = getClient().getClientData();
		return ((clientData.getDragStartPosition() != null) && (game.getFieldModel().getPlayer(pCoordinate) == null)
				&& (pCoordinate.isBoxCoordinate() || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(pCoordinate)));
	}

	@Override
	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate == null) {
			return false;
		}
		return (pCoordinate.isBoxCoordinate() || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(pCoordinate));
	}

}
