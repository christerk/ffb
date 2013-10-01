package com.balancedbytes.games.ffb.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Timer;

import com.balancedbytes.games.ffb.server.admin.AdminConnector;
import com.balancedbytes.games.ffb.server.db.DbConnectionManager;
import com.balancedbytes.games.ffb.server.db.DbInitializer;
import com.balancedbytes.games.ffb.server.db.DbQueryFactory;
import com.balancedbytes.games.ffb.server.db.DbUpdateFactory;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestProcessor;
import com.balancedbytes.games.ffb.server.handler.ServerCommandHandlerFactory;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.net.HttpServer;
import com.balancedbytes.games.ffb.server.net.NioServer;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.net.ServerPingTask;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.fumbbl.rng.Fortuna;


/**
 * 
 * @author Kalimar
 */
public class FantasyFootballServer {
  
  public static final String SERVER_VERSION = "1.0.6";
  public static final String CLIENT_VERSION = "1.0.6";

  private static final String _USAGE = "java -jar FantasyFootballServer.jar standalone\n"
                                     + "java -jar FantasyFootballServer.jar standalone initDb\n"
                                     + "java -jar FantasyFootballServer.jar fumbbl\n"
                                     + "java -jar FantasyFootballServer.jar fumbbl initDb\n";
  
  private ServerMode fMode;
  private DbConnectionManager fDbConnectionManager;
  private DbQueryFactory fDbQueryFactory;
  private DbUpdateFactory fDbUpdateFactory;
  private DbUpdater fDbUpdater;
  private Thread fPersistenceUpdaterThread;
  private ServerCommunication fCommunication;
  private Thread fCommunicationThread;
  private NioServer fNioServer;
  private Thread fNioServerThread;
  private HttpServer fHttpServer;
  private Thread fHttpServerThread;
  private ServerCommandHandlerFactory fCommandHandlerFactory;
  private GameCache fGameCache;
  private ChannelManager fChannelManager;
  private Properties fProperties;
  private Fortuna fFortuna;
  private Timer fPingTimer;
  private DebugLog fDebugLog;
  private ServerReplayer fReplayer;
  private AdminConnector fAdminConnector;
  private FumbblRequestProcessor fFumbblRequestProcessor;
  private boolean fBlockingNewGames;
  
  public FantasyFootballServer(ServerMode pMode, Properties pProperties) {
    fMode = pMode;
    fProperties = pProperties;
  }
  
  public ServerMode getMode() {
    return fMode;
  }
  
  public DebugLog getDebugLog() {
    return fDebugLog;
  }
  
  public void run() throws IOException, SQLException {

    fPingTimer = new Timer(true);
    fAdminConnector = new AdminConnector(this);
    fFortuna = new Fortuna();

    File logDir = null;
    String logDirProperty = getProperty(IServerProperty.SERVER_DIR_LOG);
    if (StringTool.isProvided(logDirProperty)) {
      logDir = new File(logDirProperty);
    }
    int logLevel = IServerLogLevel.ERROR;
    String logLevelProperty = getProperty(IServerProperty.SERVER_LOG_LEVEL);
    if (StringTool.isProvided(logLevelProperty)) {
      try {
        logLevel = Integer.parseInt(logLevelProperty);
      } catch (NumberFormatException pNumberFormatException) {
        // logLevel remains at ERROR
      }
    }
    fDebugLog = new DebugLog(this, logDir, logLevel);

    try {
      Class.forName(getProperty(IServerProperty.DB_DRIVER));
    } catch (ClassNotFoundException cnfe) {
      throw new SQLException("JDBCDriver Class not found");
    }

    fDbConnectionManager = new DbConnectionManager(this);
    
    if (fMode.isInitDb()) {
      
      System.err.println("FantasyFootballServer " + SERVER_VERSION + " initializing database.");

      DbInitializer dbInitializer = new DbInitializer(this);
      dbInitializer.initDb();

    } else {

      fDbQueryFactory = new DbQueryFactory(this);
      fDbQueryFactory.prepareStatements();

      fDbUpdateFactory = new DbUpdateFactory(this);
      fDbUpdateFactory.prepareStatements();

      fGameCache = new GameCache(this);
      fGameCache.init();
      
      fDbUpdater = new DbUpdater(this);
      fPersistenceUpdaterThread = new Thread(fDbUpdater);
      fPersistenceUpdaterThread.start();
              
      fChannelManager = new ChannelManager();

      fCommandHandlerFactory = new ServerCommandHandlerFactory(this); 

      fCommunication = new ServerCommunication(this);
      fCommunicationThread = new Thread(fCommunication);
      fCommunicationThread.start();
      
      int serverPort = Integer.parseInt(getProperty(IServerProperty.SERVER_PORT));
      fNioServer = new NioServer(null, serverPort, fCommunication);
      fNioServerThread = new Thread(fNioServer);
      fNioServerThread.start();

      String httpPortProperty = getProperty(IServerProperty.HTTP_PORT);
      String httpDirProperty = getProperty(IServerProperty.HTTP_DIR);
      if (StringTool.isProvided(httpPortProperty) && StringTool.isProvided(httpDirProperty)) {
        File httpDir = new File(httpDirProperty);
        int httpPort = Integer.parseInt(httpPortProperty);
        fHttpServer = new HttpServer(this, httpPort, httpDir);
        fHttpServerThread = new Thread(fHttpServer);
        fHttpServerThread.start();
      }
      
      String pingIntervalProperty = getProperty(IServerProperty.SERVER_PING_INTERVAL);
      if (StringTool.isProvided(pingIntervalProperty)) {
        int pingInterval = Integer.parseInt(pingIntervalProperty);
        String pingMaxDelayProperty = getProperty(IServerProperty.SERVER_PING_MAX_DELAY);
        int pingMaxDelay = StringTool.isProvided(pingMaxDelayProperty) ? Integer.parseInt(pingMaxDelayProperty) : 0;
        String dbKeepAliveProperty = getProperty(IServerProperty.DB_KEEP_ALIVE);
        int dbKeepAlive = StringTool.isProvided(dbKeepAliveProperty) ? Integer.parseInt(dbKeepAliveProperty) : 0;
        fPingTimer.schedule(new ServerPingTask(this, pingInterval, pingMaxDelay, dbKeepAlive), 0, pingInterval);
      }
      
      fReplayer = new ServerReplayer(this);
      Thread replayerThread = new Thread(fReplayer);
      replayerThread.setPriority(replayerThread.getPriority() - 1);
      replayerThread.start();
      
      fFumbblRequestProcessor = new FumbblRequestProcessor(this);
      fFumbblRequestProcessor.start();

      System.err.println("FantasyFootballServer " + SERVER_VERSION + " running on port " + serverPort + ".");
      
    }
    
  }
  
  public DbQueryFactory getDbQueryFactory() {
    return fDbQueryFactory;
  }
  
  public NioServer getNioServer() {
    return fNioServer;
  }
  
  public ServerCommunication getCommunication() {
    return fCommunication;
  }
  
  public ServerCommandHandlerFactory getCommandHandlerFactory() {
    return fCommandHandlerFactory;
  }
  
  public GameCache getGameCache() {
    return fGameCache;
  }
  
  public ChannelManager getChannelManager() {
    return fChannelManager;
  }
  
  public Fortuna getFortuna() {
    return fFortuna;
  }
  
  public void stop(int pStatus) {
    fPingTimer = null;
    if (fReplayer != null) {
      fReplayer.stop();
    }
    if (getCommunication() != null) {
      getCommunication().stop();
      try {
        fCommunicationThread.join();
      } catch (InterruptedException ie) {
      }
      getDebugLog().log(IServerLogLevel.ERROR, "Communication Thread stopped.");
    }
    if (getNioServer() != null) {
      getNioServer().stop();
      try {
        fNioServerThread.join();
      } catch (InterruptedException ie) {
      }
      getDebugLog().log(IServerLogLevel.ERROR, "NioServer Thread stopped.");
    }
    if (getDbUpdater() != null) {
      getDbUpdater().stop();
      try {
        fPersistenceUpdaterThread.join();
      } catch (InterruptedException ie) {
      }
      getDebugLog().log(IServerLogLevel.ERROR, "PersistenceUpdater Thread stopped.");
    }
    getDebugLog().close();
    System.exit(pStatus);
  }
  
  public String getProperty(String pProperty) {
    return fProperties.getProperty(pProperty);
  }
  
  public String[] getProperties() {
    return fProperties.keySet().toArray(new String[fProperties.size()]);
  }
  
  public DbUpdater getDbUpdater() {
    return fDbUpdater;
  }
  
  public DbUpdateFactory getDbUpdateFactory() {
    return fDbUpdateFactory;
  }
  
  public DbConnectionManager getDbConnectionManager() {
    return fDbConnectionManager;
  }
  
  public ServerReplayer getReplayer() {
    return fReplayer;
  }
  
  public AdminConnector getAdminConnector() {
    return fAdminConnector;
  }
  
  public FumbblRequestProcessor getFumbblRequestProcessor() {
    return fFumbblRequestProcessor;
  }
  
  public boolean isBlockingNewGames() {
	  return fBlockingNewGames;
  }
  
  public void setBlockingNewGames(boolean pBlockingNewGames) {
	  fBlockingNewGames = pBlockingNewGames;
  }
  
  public static void main(String[] args) throws IOException, SQLException {
    
    if (!ArrayTool.isProvided(args)) {

      System.err.println(_USAGE);
      System.exit(0);
      
    } else {

      ServerMode serverMode = ServerMode.fromArguments(args);

      BufferedInputStream propertyInputStream = new BufferedInputStream(new FileInputStream("server.ini"));
      Properties properties = new Properties();
      properties.load(propertyInputStream);
      propertyInputStream.close();

      FantasyFootballServer server = new FantasyFootballServer(serverMode, properties);
      try {
        server.run();
      } catch (Exception all) {
        server.getDebugLog().log(all);
        server.stop(99);
      }
      
    }
            
  }
  
}
