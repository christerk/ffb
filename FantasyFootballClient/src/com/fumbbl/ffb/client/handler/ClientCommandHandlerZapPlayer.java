package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandZapPlayer;

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
			zappedPlayer.init(rosterPlayer, getClient().getGame().getRules());
			team.addPlayer(zappedPlayer);
			getClient().getGame().getFieldModel().sendPosition(player);
		}
		return true;
	}
}
