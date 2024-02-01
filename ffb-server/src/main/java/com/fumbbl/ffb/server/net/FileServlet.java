package com.fumbbl.ffb.server.net;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import org.eclipse.jetty.servlet.DefaultServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileServlet extends DefaultServlet {

	private final FantasyFootballServer fServer;

	public FileServlet(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	@Override
	protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse)
			throws ServletException, IOException {
		super.doGet(pRequest, pResponse);
		if (fServer.getDebugLog().isLogging(IServerLogLevel.TRACE)) {
			fServer.getDebugLog().logWithOutGameId(IServerLogLevel.TRACE, "get " + pRequest.getRequestURL());
		}
	}

}
