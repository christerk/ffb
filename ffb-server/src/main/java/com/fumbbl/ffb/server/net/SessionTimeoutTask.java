package com.fumbbl.ffb.server.net;

import java.util.Arrays;
import java.util.TimerTask;
import java.util.stream.Stream;

public class SessionTimeoutTask extends TimerTask {

	private final long timeout;

	private final SessionManager sessionManager;
	private final ReplaySessionManager replaySessionManager;

	private final ServerCommunication communication;

	public SessionTimeoutTask(SessionManager sessionManager, ReplaySessionManager replaySessionManager, ServerCommunication communication, long timeout) {
		this.timeout = timeout;
		this.sessionManager = sessionManager;
		this.communication = communication;
		this.replaySessionManager = replaySessionManager;
	}

	@Override
	public void run() {

		Stream.concat(Arrays.stream(sessionManager.getAllSessions()), Arrays.stream(replaySessionManager.getAllSessions()))
				.filter(session -> sessionManager.getLastPing(session) + timeout < System.currentTimeMillis())
				.forEach(communication::close);

	}
}
