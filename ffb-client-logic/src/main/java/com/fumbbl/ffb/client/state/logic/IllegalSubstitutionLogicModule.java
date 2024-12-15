package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class IllegalSubstitutionLogicModule extends SetupLogicModule {

	private Set<Player<?>> fFieldPlayers;

	public IllegalSubstitutionLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		Game game = client.getGame();
		fFieldPlayers = new HashSet<>();
		for (Player<?> player : game.getTeamHome().getPlayers()) {
			if (!game.getFieldModel().getPlayerCoordinate(player).isBoxCoordinate()) {
				fFieldPlayers.add(player);
			}
		}
	}

	public boolean squareContainsSubstitute(FieldCoordinate coordinate) {
		Optional<Player<?>> player = getPlayer(coordinate);
		return (player.isPresent()) && client.getGame().getTeamHome().hasPlayer(player.get()) && isSubstitute(player.get());
	}

	public boolean isSubstitute(Player<?> draggedPlayer) {
		return !fFieldPlayers.contains(draggedPlayer);
	}

}
