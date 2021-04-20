package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandUnzapPlayer;

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
