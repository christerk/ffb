package com.fumbbl.ffb.server.net;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;

public class FileServlet extends DefaultServlet {

	private FantasyFootballServer fServer;

	public FileServlet(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	@Override
	protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse)
			throws ServletException, IOException {
		super.doGet(pRequest, pResponse);
		if (fServer.getDebugLog().isLogging(IServerLogLevel.TRACE)) {
			fServer.getDebugLog().log(IServerLogLevel.TRACE, "get " + pRequest.getRequestURL());
		}
	}

}
