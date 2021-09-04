package com.fumbbl.ffb.server.request;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Kalimar
 */
public class ServerRequestProcessor extends Thread {

	private boolean fStopped;
	private final FantasyFootballServer fServer;
	private final BlockingQueue<ServerRequest> fRequestQueue;

	public ServerRequestProcessor(FantasyFootballServer pServer) {
		fServer = pServer;
		fRequestQueue = new LinkedBlockingQueue<ServerRequest>();
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public boolean add(ServerRequest pServerRequest) {
		if (fStopped) {
			return false;
		}
		getServer().getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG,
			"Adding request to request processor queue: " + pServerRequest.getClass().getName());
		return fRequestQueue.offer(pServerRequest);
	}

	@Override
	public void run() {
		getServer().getDebugLog().logWithOutGameId(IServerLogLevel.INFO, "Request Processor Started.");
		while (!fStopped) {
			ServerRequest request = null;
			try {
				request = fRequestQueue.take();
			} catch (InterruptedException pInterruptedException) {
				// continue with serverRequest == null
			}
			handleRequestInternal(request, true);
		}
		getServer().getDebugLog().logWithOutGameId(IServerLogLevel.INFO, "Request Processor Stopped.");
	}

	public void shutdown() {
		fStopped = true;
		List<ServerRequest> requests = new ArrayList<>();
		fRequestQueue.drainTo(requests);
		for (ServerRequest request : requests) {
			handleRequestInternal(request, false);
		}
	}

	private void handleRequestInternal(ServerRequest request, boolean loopOnError) {
		boolean sent = !loopOnError;
		do {
			try {
				if (request != null) {
					getServer().getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG,
						"Processing request from request processor queue: " + request.getClass().getName());
					request.process(this);
				}
				sent = true;
			} catch (Exception pAnyException) {
				getServer().getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, StringTool.print(request.getRequestUrl()));
				getServer().getDebugLog().logWithOutGameId(pAnyException);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException pInterruptedException) {
					// just continue
				}
			}
		} while (!sent);
	}

}
