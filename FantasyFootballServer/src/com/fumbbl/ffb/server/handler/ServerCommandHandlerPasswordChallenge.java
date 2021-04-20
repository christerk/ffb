package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPasswordChallenge;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestPasswordChallenge;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerPasswordChallenge extends ServerCommandHandler {

	protected ServerCommandHandlerPasswordChallenge(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PASSWORD_CHALLENGE;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {

		ClientCommandPasswordChallenge passwordChallengeCommand = (ClientCommandPasswordChallenge) pReceivedCommand
				.getCommand();

		String challenge = null;
		if ((ServerMode.FUMBBL == getServer().getMode()) && StringTool.isProvided(passwordChallengeCommand.getCoach())) {
			getServer().getRequestProcessor()
					.add(new FumbblRequestPasswordChallenge(passwordChallengeCommand.getCoach(), pReceivedCommand.getSession()));
		} else {
			getServer().getCommunication().sendPasswordChallenge(pReceivedCommand.getSession(), challenge);
		}

		return true;

	}

}
