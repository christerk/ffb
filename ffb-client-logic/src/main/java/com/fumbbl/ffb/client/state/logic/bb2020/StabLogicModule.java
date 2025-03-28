package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.BlockLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

public class StabLogicModule extends BlockLogicModule {

	private Player<?>[] targets;

	public StabLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.STAB;
	}

	@Override
	public void setUp() {
		super.setUp();
		targets = findTargets();
	}

	private Player<?>[] findTargets() {
		Game game = client.getGame();
		Player<?> player = game.getActingPlayer().getPlayer();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		return UtilPlayer.findAdjacentBlockablePlayers(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player));
	}

	public Player<?>[] getTargets() {
		return targets;
	}

	@Override
	protected InteractionResult block(Player<?> player, ActingPlayer actingPlayer) {
		extension.block(actingPlayer.getPlayerId(), player, true, false, false, false);
		return InteractionResult.handled();
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		if (Arrays.stream(targets).anyMatch(target -> target.getId().equals(player.getId()))) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.reset();
		}
	}
}
