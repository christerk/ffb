package com.fumbbl.ffb.server;

import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.FantasyFootballConstants;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.server.admin.AdminServlet;
import com.fumbbl.ffb.server.admin.BackupServlet;
import com.fumbbl.ffb.server.admin.GameStateServlet;
import com.fumbbl.ffb.server.commandline.InifileParamFilter;
import com.fumbbl.ffb.server.commandline.InifileParamFilterResult;
import com.fumbbl.ffb.server.db.DbConnectionManager;
import com.fumbbl.ffb.server.db.DbInitializer;
import com.fumbbl.ffb.server.db.DbQueryFactory;
import com.fumbbl.ffb.server.db.DbUpdateFactory;
import com.fumbbl.ffb.server.handler.ServerCommandHandlerFactory;
import com.fumbbl.ffb.server.net.CommandServlet;
import com.fumbbl.ffb.server.net.FileServlet;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.ServerDbKeepAliveTask;
import com.fumbbl.ffb.server.net.ServerGameTimeTask;
import com.fumbbl.ffb.server.net.ServerNetworkEntropyTask;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.net.SessionTimeoutTask;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.rng.Fortuna;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.DateTool;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

/**
 * @author Kalimar
 */
@SuppressWarnings({"rawtypes", "FieldCanBeLocal"})
public class FantasyFootballServer implements IFactorySource {

	private static final String _USAGE = "java -jar FantasyFootballServer.jar standalone\n"
		+ "java -jar FantasyFootballServer.jar standalone initDb\n" + "java -jar FantasyFootballServer.jar fumbbl\n"
		+ "java -jar FantasyFootballServer.jar fumbbl initDb\n";

	private final ServerMode fMode;
	private DbQueryFactory fDbQueryFactory;
	private DbUpdateFactory fDbUpdateFactory;
	private DbUpdater fDbUpdater;
	private Thread fPersistenceUpdaterThread;
	private ServerCommunication fCommunication;
	private Thread fCommunicationThread;
	private ServerCommandHandlerFactory fCommandHandlerFactory;
	private GameCache fGameCache;
	private ReplayCache replayCache;
	private SessionManager fSessionManager;
	private ReplaySessionManager replaySessionManager;
	private final Properties fProperties;
	private Fortuna fFortuna;
	private DebugLog fDebugLog;
	private ServerReplayer fReplayer;
	private ServerRequestProcessor fServerRequestProcessor;
	private boolean fBlockingNewGames;

	private Timer fServerGameTimeTimer;
	private Timer fDbKeepAliveTimer;
	private Timer fNetworkEntropyTimer;
	private Timer sessionTimeoutTimer;

	private final FactoryManager factoryManager;

	private final Map<Factory, INamedObjectFactory> factories;

	public FantasyFootballServer(ServerMode pMode, Properties pProperties) {
		fMode = pMode;
		fProperties = pProperties;
		factoryManager = new FactoryManager();

		factories = factoryManager.getFactoriesForContext(getContext());

		for (INamedObjectFactory factory : factories.values()) {
			factory.initialize(null);
		}
		
	}

	public ReplaySessionManager getReplaySessionManager() {
		return replaySessionManager;
	}

	public FactoryManager getFactoryManager() {
		return factoryManager;
	}
	
	public ServerMode getMode() {
		return fMode;
	}

	public DebugLog getDebugLog() {
		return fDebugLog;
	}

	public void run() throws Exception {

		fServerGameTimeTimer = new Timer(true);
		fFortuna = new Fortuna();

		File logFile = null;
		String logFileProperty = getProperty(IServerProperty.SERVER_LOG_FILE);
		if (StringTool.isProvided(logFileProperty)) {
			logFile = new File(logFileProperty);
		}

		File logPath = null;
		String logPathProperty = getProperty(IServerProperty.SERVER_LOG_FOLDER);
		if (StringTool.isProvided(logPathProperty)) {
			logPath = new File(logPathProperty);
			if (!logPath.exists() && !logPath.mkdirs()) {
				throw new IllegalArgumentException("Can't create folder " + logPathProperty);
			}

			if (!logPath.isDirectory() || !logPath.canWrite()) {
				throw new IllegalArgumentException("Can't create new files in " + logPathProperty);
			}
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
		fDebugLog = new DebugLog(this, logFile, logPath, logLevel);

		try {
			Class.forName(getProperty(IServerProperty.DB_DRIVER));
		} catch (ClassNotFoundException cnfe) {
			throw new SQLException("JDBCDriver Class not found");
		}

		DbConnectionManager dbConnectionManager = new DbConnectionManager(this);
		dbConnectionManager.setDbUrl(getProperty(IServerProperty.DB_URL));
		dbConnectionManager.setDbUser(getProperty(IServerProperty.DB_USER));
		dbConnectionManager.setDbPassword(getProperty(IServerProperty.DB_PASSWORD));
		dbConnectionManager.setDbType(getProperty(IServerProperty.DB_TYPE));

		if (fMode.isInitDb()) {

			System.err
					.println("FantasyFootballServer " + FantasyFootballConstants.VERSION + " initializing database.");

			DbInitializer dbInitializer = new DbInitializer(dbConnectionManager);
			dbInitializer.initDb();

		} else {

			fDbQueryFactory = new DbQueryFactory(dbConnectionManager);
			fDbQueryFactory.prepareStatements();

			fDbUpdateFactory = new DbUpdateFactory(dbConnectionManager);
			fDbUpdateFactory.prepareStatements();

			fGameCache = new GameCache(this);
			fGameCache.init();
			replayCache = new ReplayCache(this);

			fDbUpdater = new DbUpdater(this);
			fPersistenceUpdaterThread = new Thread(fDbUpdater);
			fPersistenceUpdaterThread.start();

			fSessionManager = new SessionManager();
			replaySessionManager = new ReplaySessionManager();

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
				context.addServlet(new ServletHolder(new AdminServlet(this)), "/admin/*");
				context.addServlet(new ServletHolder(new GameStateServlet(this)), "/gamestate/*");
				context.addServlet(new ServletHolder(new BackupServlet(this)), "/backup/*");
				context.addServlet(new ServletHolder(new CommandServlet(this)), "/command/*");
				ServletHolder fileServletHolder = new ServletHolder(new FileServlet(this));
				fileServletHolder.setInitParameter("resourceBase", httpDir.getAbsolutePath());
				fileServletHolder.setInitParameter("pathInfoOnly", "true");
				context.addServlet(fileServletHolder, "/*");
				server.start();
			}

			String dbKeepAliveProperty = getProperty(IServerProperty.TIMER_DB_KEEP_ALIVE);
			int dbKeepAlivePeriod = StringTool.isProvided(dbKeepAliveProperty) && (getMode() != ServerMode.STANDALONE)
					? Integer.parseInt(dbKeepAliveProperty)
					: 0;
			if (dbKeepAlivePeriod > 0) {
				fDbKeepAliveTimer = new Timer(true);
				fDbKeepAliveTimer.schedule(new ServerDbKeepAliveTask(this, dbConnectionManager), 0, dbKeepAlivePeriod);
			}

			String networkEntropyProperty = getProperty(IServerProperty.TIMER_NETWORK_ENTROPY);
			int networkEntropyPeriod = StringTool.isProvided(networkEntropyProperty)
					? Integer.parseInt(networkEntropyProperty)
					: 0;
			if (networkEntropyPeriod > 0) {
				fNetworkEntropyTimer = new Timer(true);
				fNetworkEntropyTimer.schedule(new ServerNetworkEntropyTask(this), 0, networkEntropyPeriod);
			}

			fServerGameTimeTimer = new Timer(true);
			fServerGameTimeTimer.scheduleAtFixedRate(new ServerGameTimeTask(this), 0, 1000);

			fReplayer = new ServerReplayer(this);
			Thread replayerThread = new Thread(fReplayer);
			replayerThread.setPriority(replayerThread.getPriority() - 1);
			replayerThread.start();

			fServerRequestProcessor = new ServerRequestProcessor(this);
			fServerRequestProcessor.start();

			String timerEnabledProperty = getProperty(IServerProperty.TIMER_SESSION_TIMEOUT_ENABLED);

			if (Boolean.parseBoolean(timerEnabledProperty)) {
				sessionTimeoutTimer = new Timer(true);
				int timerSchedule = Integer.parseInt(getProperty(IServerProperty.TIMER_SESSION_TIMEOUT_SCHEDULE));
				int sessionTimeout = Integer.parseInt(getProperty(IServerProperty.SESSION_TIMEOUT_VALUE));
				sessionTimeoutTimer.scheduleAtFixedRate(new SessionTimeoutTask(fSessionManager, replaySessionManager,  fCommunication, sessionTimeout),
						0, timerSchedule);
			}

			System.err.print(DateTool.formatTimestamp(new Date()));
			System.err.print(" FantasyFootballServer " + FantasyFootballConstants.VERSION);
			System.err.println(" running on port " + httpPortProperty);

		}

	}

	public ReplayCache getReplayCache() {
		return replayCache;
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

	public static void main(String[] origArgs) throws IOException {

		InifileParamFilterResult filterResult = new InifileParamFilter().filterForInifile(origArgs);

		String[] args = filterResult.getFilteredArgs();

		if (!ArrayTool.isProvided(args)) {

			System.err.println(_USAGE);
			System.exit(0);

		} else {

			ServerMode serverMode = ServerMode.fromArguments(args);
			Properties properties = new Properties();

			try (FileInputStream fileInputStream = new FileInputStream(filterResult.getIniFileName());
					 BufferedInputStream propertyInputStream = new BufferedInputStream(fileInputStream)) {
				properties.load(propertyInputStream);
			}

			if (StringTool.isProvided(filterResult.getOverrideFileName())) {
				try (FileInputStream fileInputStream = new FileInputStream(filterResult.getOverrideFileName());
						 BufferedInputStream propertyInputStream = new BufferedInputStream(fileInputStream)) {
					properties.load(propertyInputStream);
				}
			}

			FantasyFootballServer server = new FantasyFootballServer(serverMode, properties);

			try {
				server.run();
			} catch (Exception all) {
				if (server.getDebugLog() == null) {
					all.printStackTrace();
				} else {
					server.getDebugLog().logWithOutGameId(all);
				}
				server.stop(99);
			}
		}
	}

	public String getProperty(String pProperty) {
		return fProperties.getProperty(pProperty);
	}

	public String[] getPropertyKeys() {
		//noinspection SuspiciousToArrayCall
		return fProperties.keySet().toArray(new String[0]);
	}

	public Properties getProperties() {
		return fProperties;
	}

	public DbUpdater getDbUpdater() {
		return fDbUpdater;
	}

	public DbUpdateFactory getDbUpdateFactory() {
		return fDbUpdateFactory;
	}

	public ServerReplayer getReplayer() {
		return fReplayer;
	}

	public ServerRequestProcessor getRequestProcessor() {
		return fServerRequestProcessor;
	}

	public boolean isBlockingNewGames() {
		return fBlockingNewGames;
	}

	public void setBlockingNewGames(boolean pBlockingNewGames) {
		fBlockingNewGames = pBlockingNewGames;
	}

	public void stop(int pStatus) {
		setBlockingNewGames(true);
		fDbKeepAliveTimer = null;
		fNetworkEntropyTimer = null;
		fServerGameTimeTimer = null;
		if (fReplayer != null) {
			fReplayer.stop();
		}
		if (getGameCache() != null) {
			getGameCache().closeAllGames();
			getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, "All games closed.");
		}
		if (getRequestProcessor() != null) {
			getRequestProcessor().shutdown();
			getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, "RequestProcessor shut down.");
		}
		if (getCommunication() != null) {
			getCommunication().shutdown();
			getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, "Communication shut down.");
		}
		if (getDbUpdater() != null) {
			getDbUpdater().shutdown();
			getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, "DbUpdater shut down.");
		}
		if (getDbQueryFactory() != null) {
			try {
				getDbQueryFactory().closeDbConnection();
			} catch (SQLException sqlE) {
				getDebugLog().log(IServerLogLevel.ERROR, sqlE);
			}
		}
		if (getDbUpdateFactory() != null) {
			try {
				getDbUpdateFactory().closeDbConnection();
			} catch (SQLException sqlE) {
				getDebugLog().log(IServerLogLevel.ERROR, sqlE);
			}
		}
		getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, "FantasyFootballServer shut down.");
		System.exit(pStatus);
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends INamedObjectFactory> T getFactory(Factory factory) {
		return (T) factories.get(factory);
	}

	@Override
	public void logError(long gameId, String message) {
		fDebugLog.log(IServerLogLevel.ERROR, gameId, message);
	}

	@Override
	public void logDebug(long gameId, String message) {
		fDebugLog.log(IServerLogLevel.DEBUG, gameId, message);
	}

	@Override
	public void logWithOutGameId(Throwable throwable) {
		fDebugLog.logWithOutGameId(throwable);
	}

	public IFactorySource getFactorySource() {
		return this;
	}

	@Override
	public IFactorySource forContext(FactoryContext context) {
		if (context == getContext()) {
			return this;
		}
		throw new FantasyFootballException("Trying to get game context from application.");
	}

	public boolean isInTestMode() {
		String testSetting = getProperty(IServerProperty.SERVER_TEST);
		return StringTool.isProvided(testSetting) && Boolean.parseBoolean(testSetting);
	}

	public void closeResources(long id) {
		fDebugLog.closeResources(id);
	}
}
