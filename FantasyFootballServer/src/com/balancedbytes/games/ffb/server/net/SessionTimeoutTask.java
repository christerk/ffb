package com.balancedbytes.games.ffb.server.net;

import java.util.Arrays;
import java.util.TimerTask;

public class SessionTimeoutTask extends TimerTask {

	private final long timeout;

	private final SessionManager sessionManager;

	private final ServerCommunication communication;

	public SessionTimeoutTask(SessionManager sessionManager, ServerCommunication communication, long timeout) {
		this.timeout = timeout;
		this.sessionManager = sessionManager;
		this.communication = communication;
	}

	@Override
	public void run() {

		Arrays.stream(sessionManager.getAllSessions())
				.filter(session -> sessionManager.getLastPing(session) + timeout < System.currentTimeMillis())
				.forEach(communication::close);

	}
}
