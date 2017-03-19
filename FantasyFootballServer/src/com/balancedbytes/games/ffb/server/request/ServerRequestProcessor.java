package com.balancedbytes.games.ffb.server.request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerRequestProcessor extends Thread {

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
    return fRequestQueue.offer(pServerRequest);
  }

  @Override
  public void run() {
    while (true) {
      ServerRequest serverRequest = null;
      try {
        serverRequest = fRequestQueue.take();
      } catch (InterruptedException pInterruptedException) {
        // continue with fumbblRequest == null
      }
      boolean sent = false;
      do {
        try {
          serverRequest.process(this);
          sent = true;
        } catch (Exception pAnyException) {
          getServer().getDebugLog().log(IServerLogLevel.ERROR, StringTool.print(serverRequest.getRequestUrl()));
          getServer().getDebugLog().log(pAnyException);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException pInterruptedException) {
            // just continue
          }
        }
      } while (!sent);
    }
  }

}
