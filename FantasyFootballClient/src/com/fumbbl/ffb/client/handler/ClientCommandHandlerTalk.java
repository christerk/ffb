package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.ui.ChatComponent;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandTalk;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 * @author Kalimar
 */
public class ClientCommandHandlerTalk extends ClientCommandHandler {

	protected ClientCommandHandlerTalk(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_TALK;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		ServerCommandTalk talkCommand = (ServerCommandTalk) pNetCommand;

		Game game = getClient().getGame();
		String coach = talkCommand.getCoach();
		String[] allTalk = talkCommand.getTalks();
		if (ArrayTool.isProvided(allTalk)) {
			for (String talk : allTalk) {
				StringBuilder status = new StringBuilder();
				TextStyle style = TextStyle.NONE;
				if (StringTool.isProvided(coach)) {
					status.append("<");
					status.append(talkCommand.getMode().getPrefix());
					status.append(coach);
					status.append("> ");
					if (coach.equals(game.getTeamHome().getCoach())) {
						style = TextStyle.HOME;
					} else if (coach.equals(game.getTeamAway().getCoach())) {
						style = TextStyle.AWAY;
					} else if (talkCommand.getMode() == ServerCommandTalk.Mode.STAFF) {
						style = TextStyle.ADMIN;
					} else if (talkCommand.getMode() == ServerCommandTalk.Mode.DEV) {
						style = TextStyle.DEV;
					} else {
						style = TextStyle.SPECTATOR;
					}
				}
				status.append(talk);
				ChatComponent chat = getClient().getUserInterface().getChat();
				chat.append(null, style, status.toString());
				chat.append(null, null, null);
			}
		}

		return true;

	}

}
