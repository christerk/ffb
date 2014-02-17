package com.balancedbytes.games.ffb.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Timer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.balancedbytes.games.ffb.server.admin.AdminServlet;
import com.balancedbytes.games.ffb.server.admin.BackupServlet;
import com.balancedbytes.games.ffb.server.db.DbConnectionManager;
import com.balancedbytes.games.ffb.server.db.DbInitializer;
import com.balancedbytes.games.ffb.server.db.DbQueryFactory;
import com.balancedbytes.games.ffb.server.db.DbUpdateFactory;
import com.balancedbytes.games.ffb.server.db.old.DbConversion;
import com.balancedbytes.games.ffb.server.handler.ServerCommandHandlerFactory;
import com.balancedbytes.games.ffb.server.net.CommandServlet;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.net.ServerPingTask;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.fumbbl.rng.Fortuna;


/**
 * 
 * @author Kalimar
 */
public class FantasyFootballServer {
  
  public static final String SERVER_VERSION = "1.1.1";
  public static final String CLIENT_VERSION = "1.1.1";

  private static final String _USAGE = "java -jar FantasyFootballServer.jar standalone\n"
                                     + "java -jar FantasyFootballServer.jar standalone initDb\n"
                                     + "java -jar FantasyFootballServer.jar fumbbl\n"
                                     + "java -jar FantasyFootballServer.jar fumbbl initDb\n"
                                     + "java -jar FantasyFootballServer.jar fumbbl convertDb <startGameId> <endGameId>\n";
  
  private ServerMode fMode;
  private DbConnectionManager fDbConnectionManager;
  private DbQueryFactory fDbQueryFactory;
  private DbUpdateFactory fDbUpdateFactory;
  private DbUpdater fDbUpdater;
  private Thread fPersistenceUpdaterThread;
  private ServerCommunication fCommunication;
  private Thread fCommunicationThread;
  private ServerCommandHandlerFactory fCommandHandlerFactory;
  private GameCache fGameCache;
  private SessionManager fSessionManager;
  private Properties fProperties;
  private Fortuna fFortuna;
  private Timer fPingTimer;
  private DebugLog fDebugLog;
  private ServerReplayer fReplayer;
  private ServerRequestProcessor fServerRequestProcessor;
  private boolean fBlockingNewGames;
  
  private long fConvertStartGameId;
  private long fConvertEndGameId;
  
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
  
  public void run() throws Exception {

    fPingTimer = new Timer(true);
    fFortuna = new Fortuna();

    File logDir = null;
    String logDirProperty = getProperty(IServerProperty.SERVER_LOG_DIR);
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
      
      if (fMode.isConvertDb()) {

        System.err.println("FantasyFootballServer " + SERVER_VERSION + " converting games from id " + fConvertStartGameId + " to " + fConvertEndGameId + ".");

      	DbConversion dbConversion = new DbConversion(this);
      	dbConversion.convert(fConvertStartGameId, fConvertEndGameId);
      	
      	fDbQueryFactory.closeDbConnection();
      	fDbUpdateFactory.closeDbConnection();
      	
      } else {
      	
        fDbUpdater = new DbUpdater(this);
        fPersistenceUpdaterThread = new Thread(fDbUpdater);
        fPersistenceUpdaterThread.start();
                
        fSessionManager = new SessionManager();
  
        fCommandHandlerFactory = new ServerCommandHandlerFactory(this); 
  
        fCommunication = new ServerCommunication(this);
        fCommunicationThread = new Thread(fCommunication);
        fCommunicationThread.start();
        
        String httpPortProperty = getProperty(IServerProperty.SERVER_PORT);
        String httpDirProperty = getProperty(IServerProperty.SERVER_BASE_DIR);
        if (StringTool.isProvided(httpPortProperty) && StringTool.isProvided(httpDirProperty)) {
          Server server = new Server(Integer.parseInt(httpPortProperty));
          ServletContextHandler context = new ServletContextHandler();
          context.setContextPath("/");
          server.setHandler(context);
          File httpDir = new File(httpDirProperty);
          ServletHolder holder = context.addServlet(DefaultServlet.class, "/icons/*");
          holder.setInitParameter("resourceBase", new File(httpDir, "icons").getAbsolutePath());
          holder.setInitParameter("pathInfoOnly", "true");
          context.addServlet(new ServletHolder(new AdminServlet(this)), "/admin/*");
          context.addServlet(new ServletHolder(new BackupServlet(this)), "/backup/*");
          context.addServlet(new ServletHolder(new CommandServlet(this)), "/command/*");
          server.start();
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
        
        fServerRequestProcessor = new ServerRequestProcessor(this);
        fServerRequestProcessor.start();
  
        System.err.println("FantasyFootballServer " + SERVER_VERSION + " running on port " + httpPortProperty + ".");
        
      }
      
    }
    
  }
  
  public DbQueryFactory getDbQueryFactory() {
    return fDbQueryFactory;
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
  
  public SessionManager getSessionManager() {
    return fSessionManager;
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
    if (getDbUpdater() != null) {
      getDbUpdater().stop();
      try {
        fPersistenceUpdaterThread.join();
      } catch (InterruptedException ie) {
      }
      getDebugLog().log(IServerLogLevel.ERROR, "PersistenceUpdater Thread stopped.");
    }
    
    // TODO: stop jetty?
    
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
  
  public ServerRequestProcessor getRequestProcessor() {
    return fServerRequestProcessor;
  }
  
  public void setConversionRange(long pStartGameId, long pEndGameId) {
  	fConvertStartGameId = pStartGameId;
  	fConvertEndGameId = pEndGameId;
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

      long convertStartGameId = (args.length > 3) ? Long.parseLong(args[2]) : 0;
      long convertEndGameId = (args.length > 3) ? Long.parseLong(args[3]) : 0;

      BufferedInputStream propertyInputStream = new BufferedInputStream(new FileInputStream("server.ini"));
      Properties properties = new Properties();
      properties.load(propertyInputStream);
      propertyInputStream.close();

      FantasyFootballServer server = new FantasyFootballServer(serverMode, properties);
    	server.setConversionRange(convertStartGameId, convertEndGameId);
      
      try {
        server.run();
      } catch (Exception all) {
        server.getDebugLog().log(all);
        server.stop(99);
      }
      
    }
            
  }
  
}
