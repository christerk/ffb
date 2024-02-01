package com.fumbbl.ffb.server.net;

import org.eclipse.jetty.websocket.api.Session;

import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommand;

/**
 * 
 * @author Kalimar
 */
public class ReceivedCommand {

	private NetCommand fCommand;
	private Session fSession;

	public ReceivedCommand(NetCommand pCommand, Session pSession) {
		fCommand = pCommand;
		fSession = pSession;
	}

	public NetCommand getCommand() {
		return fCommand;
	}

	public Session getSession() {
		return fSession;
	}

	public NetCommandId getId() {
		return getCommand().getId();
	}

	public boolean isInternalCommand() {
		return getCommand().isInternal();
	}

	public boolean isClientCommand() {
		return (getCommand() instanceof ClientCommand);
	}

}
