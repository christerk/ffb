package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandUnzapPlayer;

public class ClientCommandHandlerUnzapPlayer extends ClientCommandHandler {

	protected ClientCommandHandlerUnzapPlayer(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_UNZAP_PLAYER;
	}

	@Override
	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		ServerCommandUnzapPlayer command = (ServerCommandUnzapPlayer) pNetCommand;
		Team team = getClient().getGame().getTeamById(command.getTeamId());
		Player<?> player = team.getPlayerById(command.getPlayerId());

		if (player instanceof ZappedPlayer) {
			ZappedPlayer zappedPlayer = (ZappedPlayer) player;
			RosterPlayer rosterPlayer = zappedPlayer.getOriginalPlayer();
			team.addPlayer(rosterPlayer);
			getClient().getGame().getFieldModel().sendPosition(rosterPlayer);
		}
		return true;
	}
}
