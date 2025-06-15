package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandTransferReplayControl;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ReplayState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.util.StringTool;

public class ServerCommandHandlerTransferControl extends ServerCommandHandler {
	protected ServerCommandHandlerTransferControl(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_TRANSFER_REPLAY_CONTROL;
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandTransferReplayControl command = (ClientCommandTransferReplayControl) receivedCommand.getCommand();
		if (StringTool.isProvided(command.getCoach())) {

			ReplaySessionManager sessionManager = getServer().getReplaySessionManager();
			if (sessionManager.transferControl(receivedCommand.getSession(), command.getCoach())) {
				ReplayState replayState = getServer().getReplayCache().replayState(sessionManager.replayNameForSession(receivedCommand.getSession()));
				getServer().getCommunication().sendReplayControlChange(replayState, command.getCoach());
				if (replayState.isCoachPreventedFromSketching(command.getCoach())) {
					getServer().getCommunication().sendReplayPreventSketching(replayState, command.getCoach(), false);
				}
			}
		}
		return true;
	}
}
