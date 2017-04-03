package com.balancedbytes.games.ffb.server.request;

import java.util.ArrayList;
import java.util.List;
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
    return fRequestQueue.offer(pServerRequest);
  }

  @Override
  public void run() {
    while (!fStopped) {
      ServerRequest request = null;
      try {
        request = fRequestQueue.take();
      } catch (InterruptedException pInterruptedException) {
        // continue with serverRequest == null
      }
      handleRequestInternal(request, true);
    }
  }
  
  public void shutdown() {
    fStopped = true;
    List<ServerRequest> requests = new ArrayList<ServerRequest>(); 
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
          request.process(this);
        }
        sent = true;
      } catch (Exception pAnyException) {
        getServer().getDebugLog().log(IServerLogLevel.ERROR, StringTool.print(request.getRequestUrl()));
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
