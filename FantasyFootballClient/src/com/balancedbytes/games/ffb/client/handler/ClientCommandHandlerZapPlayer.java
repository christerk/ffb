package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandZapPlayer;

public class ClientCommandHandlerZapPlayer extends ClientCommandHandler {

	protected ClientCommandHandlerZapPlayer(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_ZAP_PLAYER;
	}

	@Override
	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		ServerCommandZapPlayer command = (ServerCommandZapPlayer) pNetCommand;
		Team team = getClient().getGame().getTeamById(command.getTeamId());
		Player<?> player = team.getPlayerById(command.getPlayerId());

		if (player instanceof RosterPlayer) {
			RosterPlayer rosterPlayer = (RosterPlayer) player;
			ZappedPlayer zappedPlayer = new ZappedPlayer();
			zappedPlayer.init(rosterPlayer, getClient().getGame().getApplicationSource());
			team.addPlayer(zappedPlayer);
			getClient().getGame().getFieldModel().sendPosition(player);
		}
		return true;
	}
}
